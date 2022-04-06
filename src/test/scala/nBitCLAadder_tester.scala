import chisel3.iotesters.PeekPokeTester
import org.scalatest._

import chisel3._
import chisel3.Driver

class nBitCLAadder_tester(dut: CLA_adder) extends
	PeekPokeTester(dut){
	
	poke(dut.io.A, 2.U)
	poke(dut.io.B, 2.U)
	poke(dut.io.Cin, 1.U)
	step(1)
	for(Cin <- 0 to 1){
		for(A <- 0 to 255){
			println(A.toString)
			for(B <- 0 to 255){
				poke(dut.io.A, A.U)
				poke(dut.io.B, B.U)
				poke(dut.io.Cin, Cin.U)
				step(1)
				if(peek(dut.io.overflow) == 1){
					expect(dut.io.sum, A+B+Cin-256)
				}else{
					expect(dut.io.sum, A+B+Cin)
				}
			}
		}
	}
	println (" Result is: " + peek(dut.io.sum).toString)
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

object nBitCLAadder_tester extends App{
	chisel3.iotesters.Driver(() => new CLA_adder(8)){
		c => new nBitCLAadder_tester(c)
	}
}
