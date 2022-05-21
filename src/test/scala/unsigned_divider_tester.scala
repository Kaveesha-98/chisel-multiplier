import chisel3.iotesters.PeekPokeTester
import org.scalatest._

import chisel3._
import chisel3.Driver

class unsigned_divider_32bit_tester(dut: unsigned_divider_32bit, divisorCoeffient: BigInt, dividendCoeffcient: BigInt, threadName: String) extends
	PeekPokeTester(dut){
	
	poke(dut.io.dividend, "b11111111111111111111111111111111".U)
	poke(dut.io.divider, "b00000000000000000000000000000001".U)
	poke(dut.io.valid, true.B)
	step(1)
	poke(dut.io.valid, false.B)
	step(18)
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

	

	/* for(divisor <- 1 to 255){
		println("Thread number: " + threadName + ", currently working divisor: " + divisor.toString)
		for(dividend <- 0 to 255){
			poke(dut.io.dividend, (dividend*dividendCoeffcient).U)
			poke(dut.io.divider, (divisor*divisorCoeffient).U)
			poke(dut.io.valid, true.B)
			step(1)
			poke(dut.io.valid, false.B)
			step(18)
			expect(dut.io.quotient, (dividend*dividendCoeffcient)/(divisor*divisorCoeffient))
			expect(dut.io.remainder, (dividend*dividendCoeffcient)%(divisor*divisorCoeffient))
		}
	} */
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

class Method1(divisorCoeffient: Int, dividendCoeffcient: Int) extends Thread {
  override def run(): Unit ={
    chisel3.iotesters.Driver.execute(Array("--target-dir", "generated", "--generate-vcd-output", "on"), () => new unsigned_divider_32bit()){
		c => new unsigned_divider_32bit_tester(c, divisorCoeffient, dividendCoeffcient, this.getName)
	}
  }
}

/* object unsigned_divider_32bit_tester extends App {
    val th0 = new Method1(1, 1)
    th0.setName(0.toString)
    val th1 = new Method1(1, 256)
    th1.setName(1.toString)
    val th2 = new Method1(1, 256*256)
    th2.setName(2.toString)
    val th3 = new Method1(1, 256*256*256)
    th3.setName(3.toString)
    val th4 = new Method1(256, 1)
    th4.setName(4.toString)
    val th5 = new Method1(256, 256)
    th5.setName(5.toString)
    /* val th6 = new Method1(256, 256*256)
    th6.setName(6.toString)
    val th7 = new Method1(256, 256*256*256)
    th7.setName(7.toString)
    val th8 = new Method1(256*256, 1)
    th8.setName(8.toString)
    val th9 = new Method1(256*256, 256)
    th9.setName(9.toString)
    val th10 = new Method1(256*256, 256*256)
    th10.setName(10.toString)
    val th11 = new Method1(256*256, 256*256*256)
    th11.setName(11.toString) */
    th0.start()
    th1.start()
    th2.start()
    th3.start()
    th4.start()
    th5.start()
    /* th6.start()
    th7.start()
    th8.start()
    th9.start()
    th10.start()
    th11.start() */
} */

object unsigned_divider_32bit_tester extends App{
	chisel3.iotesters.Driver.execute(Array("--target-dir", "generated", "--generate-vcd-output", "on"), () => new unsigned_divider_32bit()){
		c => new unsigned_divider_32bit_tester(c, 1, 256*256*256, "0")
	}
}