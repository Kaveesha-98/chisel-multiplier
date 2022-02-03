import chisel3._
import chisel3.util._
import chisel3.Driver

class multiplier(width: Int) extends Module {
    val io = IO(new Bundle{
    	//inputs
    	val multiplicand = Input(UInt(width.W))
    	val multiplier = Input(UInt(width.W))
    	//outputs
    	val result = Output(UInt((width*2).W))
    })
    
    val P = Wire(Vec(width, UInt(width.W)))
    
    //creating partial products
    for (i <- 0 to (width-1)) {
		P(i) := Mux(io.multiplier(i) === 1.U, io.multiplicand, 0.U)
	}
    
    var stage1_adders_1 = new Array[csa_3_input_n_bit](width/4)
    var stage1_adders_2 = new Array[csa_3_input_n_bit](width/4)
    
    var stage1_adder_1_Sout = Wire(Vec(width, width/4))
    var stage1_adder_1_Cout = Wire(Vec(width, width/4))
    
    //declearing stage 1 adders
    for (i <- 0 to (width/4 -1)) {
		stage1_adders_1(i) = Module(new csa_3_input_n_bit(width))
		
		stage1_adders_1(i).io.A := Cat(0.U(1.W), P(4*i + 0)(width-1, 1))
    	stage1_adders_1(i).io.B := P(4*i + 1)
    	stage1_adders_1(i).io.Cin := Cat(P(4*i + 2)(width-2, 0), 0.U(1.W))
    	
    	stage1_adder_1_Sout(i) = stage1_adders_1(i).io.Sout
    	stage1_adder_1_Cout(i) = stage1_adders_1(i).io.Cout
    	
    	stage1_adders_2(i) = Module(new csa_3_input_n_bit(width))
    	
    	stage1_adders_2(i).io.A := Cat(P(4*i + 2)(width-1), stage1_adder_1_Sout(i)(width-1, 1))
    	stage1_adders_2(i).io.B := stage1_adder_1_Cout(i)
    	stage1_adders_2(i).io.Cin := Cat(P(4*i + 3)(width-2, 0), 0.U(1.W))
	}
    
	io.result := Cat(P(1), P(0))
}

object multiplier extends App{
    (new chisel3.stage.ChiselStage).emitVerilog(new multiplier(32))
}
