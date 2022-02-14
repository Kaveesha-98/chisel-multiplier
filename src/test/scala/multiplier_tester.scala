import chisel3.iotesters.PeekPokeTester
import org.scalatest._

import chisel3._
import chisel3.Driver

class multiplier_tester(dut: multiplier) extends
	PeekPokeTester(dut){
	poke(dut.io.multiplier, 32.U)
	poke(dut.io.multiplicand, 32.U)
	step(1)
	println (" Result is: " + peek(dut.io.answer_low).toString )
}

object multiplier_tester extends App{
	iotesters.Driver.execute(Array("--target-dir", "generated", "--generate-vcd-output", "on"), () => new multiplier()){
		c => new multiplier_tester(c)
	}
}

//object multiplier_tester extends App{
//	chisel3.iotesters.Driver(() => new multiplier()){
//		c => new multiplier_tester(c)
//	}
//}
