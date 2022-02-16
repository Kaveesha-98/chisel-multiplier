import chisel3._
import chisel3.util._
import chisel3.Driver

class riscv_multiplier() extends Module {
    val io = IO(new Bundle{
    	//inputs- register inputs
    	val rs1 = Input(UInt(32.W))
    	val rs2 = Input(UInt(32.W))
    	//input - specify unsignedxunsigned-00, signedxunsigned-10, signedxsigned-11
    	val opcode = Input(UInt(2.W))
    	//outputs
    	val answer_low = Output(UInt(32.W))
    	val answer_high = Output(UInt(32.W))
    })
    
    val multiplier = Module(new multicycle_multiplier())
    
    multiplier.io.multiplicand := Mux(io.opcode(0).asBool, Cat(0.U(1.W), io.rs1(30, 0)), io.rs1)
    multiplier.io.multiplier := Mux(io.opcode(1).asBool, Cat(0.U(1.W), io.rs2(30, 0)), io.rs2)
    
    val csa_adder = Module(new csa_3_input_n_bit(33))
    
    csa_adder.io.A := Cat(3.U(2.W), ~io.rs1(30, 0))
    csa_adder.io.B := Cat(3.U(2.W), ~io.rs2(30, 0))
    csa_adder.io.Cin := Cat(1.U(2.W), 2.U(31.W))
    
    multiplier.io.signed_correct_Sout := 0.U
    multiplier.io.signed_correct_Cout := 0.U
    
    switch(io.opcode){
    	is("b10".U){
    		multiplier.io.signed_correct_Sout := Mux(io.rs1(31).asBool, Cat(1.U(1.W), ~io.rs2), 0.U)
    		multiplier.io.signed_correct_Cout := Mux(io.rs1(31).asBool, 1.U(33.W), 0.U)
    	}
    	is("b11".U){
    		switch(Cat(io.rs1(31), io.rs2(31))){
    			is("b10".U){
    				multiplier.io.signed_correct_Sout := Cat(1.U(1.W), ~io.rs2)
    				multiplier.io.signed_correct_Cout := 1.U(33.W)
    			}
    			is("b01".U){
    				multiplier.io.signed_correct_Sout := Cat(1.U(1.W), ~io.rs1)
    				multiplier.io.signed_correct_Cout := 1.U(33.W)
    			}
    			is("b11".U){
    				multiplier.io.signed_correct_Sout := csa_adder.io.Sout
    				multiplier.io.signed_correct_Cout := Cat(csa_adder.io.Cout(31, 0), 0.U(1.W))
    			}
    		}
    	}
    }
    
    io.answer_low := multiplier.io.answer_low
    io.answer_high := multiplier.io.answer_high
}

object riscv_multiplier extends App{
    (new chisel3.stage.ChiselStage).emitVerilog(new riscv_multiplier())
}
