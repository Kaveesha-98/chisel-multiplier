#include <stdio.h>
#include <stdlib.h>
#include "Vcsa_3_input_32_bit.h"
#include <iostream>
#include "verilated.h"

using namespace std;

int main(int argc, char **argv){

	unsigned tickcount = 0;

	// Call commandArgs first!
	Verilated::commandArgs(argc, argv);
	
	//Instantiate our design
	Vcsa_3_input_32_bit *tb = new Vcsa_3_input_32_bit;
	
	int end_limit = 1024;
	
	for(int A = 0; A < end_limit; A++){
		for(int B = 0; B < end_limit; B++){
			for(int Cin = 0; Cin < end_limit; Cin++){
				tb->io_A = A;
				tb->io_B = B;
				tb->io_Cin = Cin;
				tb->eval();
				cout << A << ' ' << B << ' ' << Cin << ' ' << tb->io_Sout << ' ' << (tb->io_Cout)*2 << endl;
				
			}
		}
	}
	/*
	for(int i = 0; i < end_limit; i++){
		tb->io_A = 10;
		tb->io_B = 20;
		tb->io_Cin = 30;
		tb->eval();
		cout << tb->io_Sout << ' ' << (tb->io_Cout)*2 << endl;
	}*/
}
