import chisel3._
import chisel3.util._
import chisel3.Driver

class unsigned_divider_32bit extends Module {
    val io = IO(new Bundle{
        val divider     = Input(UInt(32.W))
        val dividend    = Input(UInt(32.W))
        val valid       = Input(Bool())
        val quotient    = Output(UInt(32.W))
        val remainder   = Output(UInt(32.W))
    })

    def addUnsignedInts[T <: Data](int_a: T, int_b: T, carry_in: T, width: Int): chisel3.UInt = {
        
        val ret = Wire(UInt((width+1).W)) 
        val adder = Module(new CLA_adder(width))
        adder.io.A := int_a
        adder.io.B := int_b
        adder.io.Cin := carry_in
        ret := Cat(adder.io.overflow.asUInt, adder.io.sum)
        ret

    }

    val decrement = -1.S.asUInt

    val inWireDivisorAddAdd = Wire(UInt(35.W))
    val inWireDivisorSubSub = Wire(UInt(35.W))
    val inWireDivisorComp   = Wire(UInt(35.W))
    val inWireDivisor       = Wire(UInt(35.W))
    
    inWireDivisorAddAdd := addUnsignedInts(Cat(0.U(1.W), io.divider, 0.U(1.W)), Cat(0.U(2.W), io.divider), 0.U, 34)
    inWireDivisorSubSub := Cat(addUnsignedInts( Cat( 1.U(1.W), ~(io.divider) ), Cat( 3.U(2.W), ~(io.divider(31, 1)) ), 1.U, 33), ~(io.divider(0)) )
    inWireDivisorComp   := addUnsignedInts(0.U, Cat( 3.U(2.W), ~(io.divider) ), 1.U, 34)
    inWireDivisor       := Cat(0.U(3.W), io.divider)

    val ready :: running :: remainder_fix :: Nil = Enum(3)
    val stateReg = RegInit(ready)
    val cntReg = Reg(UInt(6.W))

    val quotient    = Reg(UInt(34.W))
    val remainder   = Reg(UInt(34.W))
    val divisor     = Reg(UInt(34.W))
    val divisorComp = Reg(UInt(34.W))

    val divisorAddAdd = Reg(UInt(35.W))
    val divisorSubSub = Reg(UInt(35.W))

    val remainderRound1 = Cat(remainder(32, 0), quotient(33))
    val remainderRound2 = Cat(remainder(32, 0), quotient(33, 32))

    val round1DivisorOp = Mux(remainder(33).asBool, divisor, divisorComp)
    val round1Result    = addUnsignedInts(remainderRound1, round1DivisorOp, 0.U, 34)

    val round2DivisorOpAdd  = Wire(UInt(35.W))
    val round2DivisorOpSub  = Wire(UInt(35.W))
    val round2CarryIn       = Wire(UInt(1.W))

    when(remainder(33).asBool){
        round2DivisorOpAdd := divisorAddAdd
        round2DivisorOpSub := Cat(0.U(1.W), divisor)
        round2CarryIn := 0.U
    }.otherwise{
        round2DivisorOpAdd := Cat(1.U(1.W), divisorComp)
        round2DivisorOpSub := divisorSubSub
        round2CarryIn := 1.U
    }

    val round2ResultAdd = addUnsignedInts(remainderRound2, round2DivisorOpAdd, 0.U, 35)
    val round2ResultSub = addUnsignedInts(remainderRound2, round2DivisorOpSub, round2CarryIn, 35)

    val round2Result = Mux(round1Result(33).asBool, round2ResultAdd, round2ResultSub)

    val newQuotientBits = Cat(~round1Result(33), ~round2Result(34))

    val nextQuotient    = Cat(quotient(31, 0), newQuotientBits)
    val nextRemainder   = round2Result(33, 0)
    val nextCount       = addUnsignedInts(cntReg, "b111111".U, 0.U, 6)

    val fixed_reaminder = addUnsignedInts(divisor, remainder, 0.U, 34)

    switch(stateReg){
        is(ready){
            when(io.valid){
                stateReg        := running
                cntReg          := 16.U
                remainder       := 0.U
                quotient        := io.dividend
                divisor         := inWireDivisor(33, 0)
                divisorComp     := inWireDivisorComp(33, 0)
                divisorAddAdd   := inWireDivisorAddAdd
                divisorSubSub   := inWireDivisorSubSub
            }
        }
        is(running){
            cntReg      := nextCount(5, 0)
            quotient    := nextQuotient
            remainder   := nextRemainder
            when(cntReg === 0.U){
                stateReg := remainder_fix
            }
        }
        is(remainder_fix){
            remainder   := Mux(remainder(33).asBool, fixed_reaminder, remainder)
            stateReg    := ready
        }
    }

    io.quotient     := quotient(31, 0)
    io.remainder    := remainder(31, 0)
}

object unsigned_divider extends App{
    (new chisel3.stage.ChiselStage).emitVerilog(new unsigned_divider_32bit)
}