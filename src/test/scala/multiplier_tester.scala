import chisel3.iotesters.PeekPokeTester
import org.scalatest._

import chisel3._
import chisel3.Driver

class multiplier_tester(dut: multiplier) extends
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
	
	poke(dut.io.multiplier, "b11111111111111111111111111111111".U)
	poke(dut.io.multiplicand, "b11111111111111111111111111111111".U)
	poke(dut.io.in1, 3.U)
	poke(dut.io.in2, 14.U)
	step(1)
	println("Result of out " + peek(dut.io.out).toInt.toBinaryString)
	println (" Result is: " + peek(dut.io.answer_low).toString + " " +  peek(dut.io.answer_high).toString)
	var correctOutput:BigInt = BigInt(4294967295L)*BigInt(4294967295L)
	var b = correctOutput/shifter
	println(b.toString)
	expect(dut.io.answer_high , b)
	expect(dut.io.answer_low , correctOutput%shifter)
	
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

object multiplier_tester extends App{
	chisel3.iotesters.Driver(() => new multiplier()){
		c => new multiplier_tester(c)
	}
}
