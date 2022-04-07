import chisel3.iotesters.PeekPokeTester
import org.scalatest._

import chisel3._
import chisel3.Driver

class riscv_multiplier_tester(dut: riscv_multiplier) extends
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
	
	poke(dut.io.rs2, "b11111111111111111111111111111111".U)
	poke(dut.io.rs1, "b11111111111111111111111111111111".U)
	poke(dut.io.opcode, 3.U)
	for (i <- 1 to 7){
		step(1)
		println ("after " + i.toString + " step(s) answer_low: " + peek(dut.io.answer_low).toString + " answer_high:" +  peek(dut.io.answer_high).toString)
	}
	
	var result = nextInputs(4294967295L)
	val in1 = (("b" + result.substring(0, 31)).U).toInt
	println(in1.toString)
	println(result)
	
	var inputs:String = ""
	var input1:Int = 4294967295L
	var input2:Int = 4294967295L
	
	var i:Int = 0
	/*
	while (i <= 4294L){
	
		inputs = nextInputs(i)
		input1 = (("b" + inputs.substring(0, 31)).U).toInt
		input2 = (("b" + inputs.substring(32, 63)).U).toInt
		correctOutput = BigInt(input1)*BigInt(input2)
		poke(dut.io.multiplier, input1.U)
		poke(dut.io.multiplicand, input2.U)
		step(1)
		expect(dut.io.answer_high , correctOutput/shifter)
		expect(dut.io.answer_low , correctOutput%shifter)
		
		input1 = (("b0" + inputs.substring(0, 30)).U).toInt
		input2 = (("b" + inputs.substring(32, 63)).U).toInt
		correctOutput = BigInt(input1)*BigInt(input2)
		poke(dut.io.multiplier, input1.U)
		poke(dut.io.multiplicand, input2.U)
		step(1)
		expect(dut.io.answer_high , correctOutput/shifter)
		expect(dut.io.answer_low , correctOutput%shifter)
		
		input1 = (("b" + inputs.substring(0, 31)).U).toInt
		input2 = (("b" + inputs.substring(31, 62)).U).toInt
		correctOutput = BigInt(input1)*BigInt(input2)
		poke(dut.io.multiplier, input1.U)
		poke(dut.io.multiplicand, input2.U)
		step(1)
		expect(dut.io.answer_high , correctOutput/shifter)
		expect(dut.io.answer_low , correctOutput%shifter)
		
		input1 = (("b0" + inputs.substring(0, 30)).U).toInt
		input2 = (("b" + inputs.substring(31, 62)).U).toInt
		correctOutput = BigInt(input1)*BigInt(input2)
		poke(dut.io.multiplier, input1.U)
		poke(dut.io.multiplicand, input2.U)
		step(1)
		expect(dut.io.answer_high , correctOutput/shifter)
		expect(dut.io.answer_low , correctOutput%shifter)
		
		i = i + 1
	}
	*/
}

object riscv_multiplier_tester_vcd extends App{

	def addInt( a:Int, b:Int ) : Int = {
      var sum:Int = 0
      sum = a + b
      return sum
   }

	iotesters.Driver.execute(Array("--target-dir", "generated", "--generate-vcd-output", "on"), () => new riscv_multiplier()){
		c => new riscv_multiplier_tester(c)
	}
}

object riscv_multiplier_tester extends App{
	chisel3.iotesters.Driver(() => new riscv_multiplier()){
		c => new riscv_multiplier_tester(c)
	}
}
