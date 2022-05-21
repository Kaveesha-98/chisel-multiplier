import chisel3._
import chisel3.util._
import chisel3.Driver

class riscv_divider_32bit extends Module {
    val io = IO(new Bundle{
        val divider     = Input(UInt(32.W))
        val dividend    = Input(UInt(32.W))
        val valid       = Input(Bool())
        val ready       = Output(Bool())
        val quotient    = Output(UInt(32.W))
        val remainder   = Output(UInt(32.W))
        val opcode      = Input(UInt(1.W))//use funct[0]
    })

    def takeComplimentResult32bits[T <: Data](originalInt: chisel3.UInt): chisel3.UInt = {

        //adder module will reverse the sign
        val ret = Wire(UInt(32.W))
        val adder = Module(new CLA_adder(32))
        adder.io.A := 0.U
        adder.io.B := ~originalInt
        adder.io.Cin := 1.U 
        ret := adder.io.sum//~originalInt + 1 = -originalInt
        ret

    }

    def addUnsignedInts[T <: Data](int_a: T, int_b: T, carry_in: T, width: Int): chisel3.UInt = {
        
        val ret = Wire(UInt((width+1).W)) 
        val adder = Module(new CLA_adder(width))
        adder.io.A := int_a
        adder.io.B := int_b
        adder.io.Cin := carry_in
        ret := Cat(adder.io.overflow.asUInt, adder.io.sum)
        ret

    }

    def divideUnsignedInts[T <: Data](dividend: T, divider: T, requestValid: T, moduleReady: T): chisel3.UInt = {

        val ret = Wire(UInt(64.W))
        val dividerModule = Module(new unsigned_divider_32bit())
        dividerModule.io.divider := divider
        dividerModule.io.dividend := dividend
        dividerModule.io.valid := requestValid
        moduleReady := dividerModule.io.ready
        ret := Cat(dividerModule.io.quotient, dividerModule.io.remainder)
        ret

    }

    val ready :: beginDivide :: divide :: Nil = Enum(3)
    val stateReg = RegInit(ready)

    val dividerBuffer   = Reg(UInt(32.W))
    val dividendBuffer  = Reg(UInt(32.W))
    val finalQuotient   = Reg(UInt(32.W))
    val finalRemainder  = Reg(UInt(32.W))

    val unsigedDivideBegin = WireInit(false.B)
    val unsigedDivideReady = Wire(Bool())

    val unsignedDivideResult    = divideUnsignedInts(dividendBuffer, dividerBuffer, unsigedDivideBegin, unsigedDivideReady)
    val unsignedQuotient        = unsignedDivideResult(63, 32)
    val unsignedRemainder       = unsignedDivideResult(31, 0)

    val resultsNegative = Reg(Bool())//register to remember division type
    val unsignedRequest = io.opcode.asBool//when opcode =1, unsigned result

    val resultQuotient  = unsignedDivideResult(63, 32)
    val resultRemainder = unsignedDivideResult(31,  0)

    val complimentingInt0 = Mux(stateReg === ready, io.divider, unsignedDivideResult(63, 32))
    val complimentingInt1 = Mux(stateReg === ready, io.dividend, unsignedDivideResult(31, 0))

    val complimentedInt0 = takeComplimentResult32bits(complimentingInt0)
    val complimentedInt1 = takeComplimentResult32bits(complimentingInt1)

    val isDividerNeg    = io.divider(31).asBool
    val isDividendNeg   = io.dividend(31).asBool

    switch(stateReg){
        is(ready){
            when(io.valid){
                //for signed requests
                //division only works for unsigned numbers
                dividerBuffer   := Mux(isDividerNeg, complimentedInt0, io.divider)
                dividendBuffer  := Mux(isDividendNeg, complimentedInt1, io.dividend)
                //results are negative iff only operand is negative
                resultsNegative := isDividendNeg^isDividerNeg
                when(unsignedRequest){
                    //for unsiged execution
                    dividerBuffer   := io.divider
                    dividendBuffer  := io.dividend
                    resultsNegative := false.B
                }

                stateReg := beginDivide
            }
        }
        is(beginDivide){
            unsigedDivideBegin := true.B
            stateReg := divide
        }
        is(divide){
            when(unsigedDivideReady){
                stateReg := ready
                finalQuotient := Mux(resultsNegative, complimentedInt0, unsignedQuotient)
                finalRemainder := Mux(resultsNegative, complimentedInt1, unsignedRemainder)
            }
        }
    }

    io.ready := stateReg === ready
    io.quotient := finalQuotient
    io.remainder := finalRemainder

}

object riscv_divider_32bit extends App{
    (new chisel3.stage.ChiselStage).emitVerilog(new riscv_divider_32bit)
}