import chisel3._
import chisel3.util._
import chisel3.Driver

class unsigned_divider_32bit extends Module {
    val io = IO(new Bundle{
        val divider = Input(UInt(32.W))
        val dividend = Input(UInt(32.W))
        val valid = Input(Bool())
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

    val ready :: running :: Nil = Enum(2)
    val stateReg = RegInit(ready)

    val quotient    = Reg(UInt(32.W))
    val remainder   = Reg(UInt(32.W))
    val divisor     = Reg(UInt(32.W))

    val add = addUnsignedInts(io.dividend, io.divider, 1.U, 32)

    switch(stateReg){
        is(ready){
            remainder   := 0.U
            quotient    := io.dividend
            divisor     := io.divider
            when(io.valid){
                stateReg := running
            }
        }
    }
}

object unsigned_divider extends App{
    (new chisel3.stage.ChiselStage).emitVerilog(new unsigned_divider_32bit)
}