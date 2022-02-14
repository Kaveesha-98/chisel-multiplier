import chisel3._
import chisel3.util._
import chisel3.Driver

class multiplier extends Module {
    val io = IO(new Bundle{
    	//inputs
    	val multiplier = Input(UInt(32.W))
    	val multiplicand = Input(UInt(32.W))
    	//outputs
    	val Sout = Output(UInt(32.W))
    	val Cout = Output(UInt(32.W))
    })
    
    val partial_products = Wire(Vec(32, UInt(32.W)))

	//creating partial products
	for(i <- 0 to 31){
		partial_products(i) := Mux(io.multiplier(i) === 1.U, io.multiplicand, 0.U)
	}
    
    //adders for 1st stage
    var stage1_adders_set1 = new Array[csa_3_input_n_bit](8)
    var stage1_adders_set2 = new Array[csa_3_input_n_bit](8)
    
    //output of stage1_adders_set1
    val stage1_adders_set1_Sout = Wire(Vec(8, UInt(32.W)))
    val stage1_adders_set1_Cout = Wire (Vec(8, UInt(32.W)))
    
    //output of stage1_adders_set2
    val stage1_adders_set2_Sout = Wire(Vec(8, UInt(32.W)))
    val stage1_adders_set2_Cout = Wire (Vec(8, UInt(32.W)))
    
    //output of stage1_adders
    val stage1_adders_Sout = Wire(Vec(8, UInt(35.W)))
    val stage1_adders_Cout = Wire (Vec(8, UInt(32.W)))
    
    for(i <- 0 to 7){
    	stage1_adders_set1(i) = Module(new csa_3_input_n_bit(32))
    	
    	stage1_adders_set1(i).io.A := Cat(0.U(1.W), partial_products(4*i + 0)(31, 1))
    	stage1_adders_set1(i).io.B := partial_products(4*i + 1)
    	stage1_adders_set1(i).io.Cin := Cat(partial_products(4*i + 2)(30, 0), 0.U(1.W))
    	
    	stage1_adders_set1_Sout(i) := stage1_adders_set1(i).io.Sout
    	stage1_adders_set1_Cout(i) := stage1_adders_set1(i).io.Cout
    	
    	stage1_adders_set2(i) = Module(new csa_3_input_n_bit(32))
    	
    	stage1_adders_set2(i).io.A := Cat(partial_products(4*i + 2)(31), stage1_adders_set1_Sout(i)(31, 1))
    	stage1_adders_set2(i).io.B := stage1_adders_set1_Cout(i)
    	stage1_adders_set2(i).io.Cin := Cat(partial_products(4*i + 3)(30, 0), 0.U(1.W))
    	
    	stage1_adders_set2_Sout(i) := stage1_adders_set2(i).io.Sout
    	stage1_adders_set2_Cout(i) := stage1_adders_set2(i).io.Cout
    	
    	stage1_adders_Sout(i) := Cat(Cat(partial_products(4*i + 3)(31), stage1_adders_set2_Sout(i)), Cat(stage1_adders_set1_Sout(i)(0), partial_products(4*i + 0)(0)))
    	stage1_adders_Cout(i) := stage1_adders_set2_Cout(i)
    }
    
    //adders for 2nd stage
    var stage2_adders_set1 = new Array[csa_3_input_n_bit](4)
    var stage2_adders_set2 = new Array[csa_3_input_n_bit](4)
    
    //ouput of stage2_adders_set1
    val stage2_adders_set1_Sout = Wire(Vec(4, UInt(32.W)))
    val stage2_adders_set1_Cout = Wire(Vec(4, UInt(32.W)))
    
    //output of stage2_adders_set2
    val stage2_adders_set2_Sout = Wire(Vec(4, UInt(32.W)))
    val stage2_adders_set2_Cout = Wire (Vec(4, UInt(32.W)))
    
    //output of stage2_adders
    val stage2_adders_Sout = Wire(Vec(4, UInt(39.W)))
    val stage2_adders_Cout = Wire (Vec(4, UInt(36.W)))
    
    for(i <- 0 to 3){
    	stage2_adders_set1(i) = Module(new csa_3_input_n_bit(32))
    	
    	stage2_adders_set1(i).io.A := stage1_adders_Sout(i)(34, 3)
    	stage2_adders_set1(i).io.B := stage1_adders_Cout(i)
    	stage2_adders_set1(i).io.Cin := Cat(stage1_adders_Sout(i + 1)(30, 0), 0.U(1.W))
    	
    	stage2_adders_set1_Sout(i) := stage2_adders_set1(i).io.Sout
    	stage2_adders_set1_Cout(i) := stage2_adders_set1(i).io.Cout
    	
    	stage2_adders_set2(i) = Module(new csa_3_input_n_bit(32))
    	
    	stage2_adders_set2(i).io.A := Cat(stage1_adders_Sout(i + 1)(34, 31), stage2_adders_set1_Sout(i)(31, 4))
    	stage2_adders_set2(i).io.B := Cat(0.U(3.W), stage2_adders_set1_Cout(i)(31, 3))
    	stage2_adders_set2(i).io.Cin := stage1_adders_Cout(i + 1)
    	
    	stage2_adders_set2_Sout(i) := stage2_adders_set2(i).io.Sout
    	stage2_adders_set2_Cout(i) := stage2_adders_set2(i).io.Cout
    	
    	stage2_adders_Sout(i) := Cat(stage2_adders_set2_Sout(i), Cat(stage2_adders_set1_Sout(i)(3, 0), stage1_adders_Sout(i)(2, 0)))
    	stage2_adders_Cout(i) := Cat(stage2_adders_set2_Cout(i), Cat(0.U(1.W), stage2_adders_set1_Cout(i)(2, 0)))
    }
    
    //adders for 3rd stage
    var stage3_adders_set1 = new Array[csa_3_input_n_bit](2)
    var stage3_adders_set2 = new Array[csa_3_input_n_bit](2)
    
    //ouput of stage3_adders_set1
    val stage3_adders_set1_Sout = Wire(Vec(2, UInt(32.W)))
    val stage3_adders_set1_Cout = Wire(Vec(2, UInt(32.W)))
    
    //output of stage3_adders_set2
    val stage3_adders_set2_Sout = Wire(Vec(2, UInt(35.W)))
    val stage3_adders_set2_Cout = Wire (Vec(2, UInt(35.W)))
    
    //output of stage3_adders
    val stage3_adders_Sout = Wire(Vec(2, UInt(48.W)))
    val stage3_adders_Cout = Wire (Vec(2, UInt(44.W)))
    
    for(i <- 0 to 1){
    	stage3_adders_set1(i) = Module(new csa_3_input_n_bit(32))
    	
    	stage3_adders_set1(i).io.A := Cat(0.U(1.W), stage2_adders_Sout(i)(38, 8))
    	stage3_adders_set1(i).io.B := stage2_adders_Cout(i)(35, 4)
    	stage3_adders_set1(i).io.Cin := stage2_adders_Sout(i + 1)(31, 0)
    	
    	stage3_adders_set1_Sout(i) := stage3_adders_set1(i).io.Sout
    	stage3_adders_set1_Cout(i) := stage3_adders_set1(i).io.Cout
    	
    	stage3_adders_set2(i) = Module(new csa_3_input_n_bit(35))
    	
    	stage3_adders_set2(i).io.A := Cat(stage2_adders_Sout(i + 1)(38, 32), stage3_adders_set1_Sout(i)(31, 4))
    	stage3_adders_set2(i).io.B := Cat(0.U(6.W), stage3_adders_set1_Cout(i)(31, 3))
    	stage3_adders_set2(i).io.Cin := stage2_adders_Cout(i + 1)(34, 0)
    	
    	stage3_adders_set2_Sout(i) := stage3_adders_set2(i).io.Sout
    	stage3_adders_set2_Cout(i) := stage3_adders_set2(i).io.Cout
    	
    	stage3_adders_Sout(i) := Cat(Cat(stage2_adders_Cout(i + 1)(35), stage3_adders_set2_Sout(i)) , Cat(stage3_adders_set1_Sout(i)(3, 0), stage2_adders_Sout(i)(7, 0)))
    	stage3_adders_Cout(i) := Cat(Cat(Cat(stage3_adders_set2_Cout(i), 0.U(1.W)), Cat(stage3_adders_set1_Cout(i)(2, 0), 0.U(1.W))), stage2_adders_Cout(i)(3, 0))
    }
    
    val stage4_adders_set1 = Module(new csa_3_input_n_bit(32))
    
    stage4_adders_set1.io.A := stage3_adders_Sout(0)(47, 16)
    stage4_adders_set1.io.B := stage3_adders_Cout(0)(43, 12)
    stage4_adders_set1.io.Cin := stage3_adders_Sout(1)(31, 0)
    
    
    
	io.Sout := 1.U
    io.Cout := 1.U
}

object multiplier extends App{
    (new chisel3.stage.ChiselStage).emitVerilog(new multiplier())
}
