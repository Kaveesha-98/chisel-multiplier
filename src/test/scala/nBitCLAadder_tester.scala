import chisel3.iotesters.PeekPokeTester
import org.scalatest._

import chisel3._
import chisel3.Driver

class nBitCLAadder_tester(dut: CLA_adder) extends
	PeekPokeTester(dut){
	
	poke(dut.io.A, 2.U)
	poke(dut.io.B, 2.U)
	poke(dut.io.Cin, 0.U)
	step(1)
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
	chisel3.iotesters.Driver(() => new CLA_adder(32)){
		c => new nBitCLAadder_tester(c)
	}
}
