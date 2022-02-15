import chisel3.iotesters.PeekPokeTester
import org.scalatest._

import chisel3._
import chisel3.Driver

class full_adder_tester(dut: cla_8bit) extends
	PeekPokeTester(dut){
	
	def nextInputs( iteral:Int ) : String = {
      var binaryString:String = iteral.toBinaryString
      binaryString = binaryString.reverse.padTo(32,'0').reverse
      var result:String = ""
      for(i <- 0 to 31){
      	result = result + binaryString(i) + "0"	
      }
      return result
   }
   
   	val shifter:BigInt = BigInt(4294967296L)
	
	poke(dut.io.A, 2.U)
	poke(dut.io.B, 2.U)
	poke(dut.io.Cin, 0.U)
	step(1)
	println (" Result is: " + peek(dut.io.Sout).toString)
	
	var correct_carry:Int = 0
	var correct_sum:Int = 0
	var total:Int = 0
		
	for(Cin <- 0 to 1){
		for(A <- 0 to 255){
			for(B <- 0 to 255){
				total = A + B + Cin
				correct_carry = total/256
				correct_sum = total%256
				
				poke(dut.io.A, A.U)
				poke(dut.io.B, B.U)
				poke(dut.io.Cin, Cin.U)
				
				step(1)
				
				expect(dut.io.Sout , correct_sum)
				expect(dut.io.Cout , correct_carry)
			}
		} 
	}
}
/*
object multiplier_tester extends App{

	def addInt( a:Int, b:Int ) : Int = {
      var sum:Int = 0
      sum = a + b
      return sum
   }

	iotesters.Driver.execute(Array("--target-dir", "generated", "--generate-vcd-output", "on"), () => new multiplier()){
		c => new multiplier_tester(c)
	}
}*/

object full_adder_tester extends App{
	chisel3.iotesters.Driver(() => new cla_8bit()){
		c => new full_adder_tester(c)
	}
}
