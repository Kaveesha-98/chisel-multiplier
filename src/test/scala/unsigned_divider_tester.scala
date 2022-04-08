import chisel3.iotesters.PeekPokeTester
import org.scalatest._

import chisel3._
import chisel3.Driver

class unsigned_divider_32bit_tester(dut: unsigned_divider_32bit) extends
	PeekPokeTester(dut){
	
	poke(dut.io.dividend, 11.U)
	poke(dut.io.divider, 4.U)
	poke(dut.io.valid, true.B)
	step(1)
	poke(dut.io.valid, false.B)
	step(30)
	/* for(Cin <- 0 to 1){
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
	} */
	println (" Quotient is: " + peek(dut.io.quotient).toString)
	println (" Remainder is: " + peek(dut.io.remainder).toString)
}

/*
object multiplier_tester extends App{

	def addInt( a:Int, b:Int ) : Int = {
      var sum:Int = 0
      sum = a + b
      return sum
   }

	iotesters.Driver.execute(Array("--target-dir", "generated", "--generate-vcd-output", "on"), () => new unsigned_divider_32bit()){
		c => new multiplier_tester(c)
	}
}*/

object unsigned_divider_32bit_tester extends App{
	chisel3.iotesters.Driver.execute(Array("--target-dir", "generated", "--generate-vcd-output", "on"), () => new unsigned_divider_32bit()){
		c => new unsigned_divider_32bit_tester(c)
	}
}