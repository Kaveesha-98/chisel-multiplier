#include <stdio.h>
#include <stdlib.h>
#include "Vcsa_recursive_adder_4_input.h"
#include <iostream>
#include "verilated.h"

using namespace std;

int main(int argc, char **argv){

	unsigned tickcount = 0;

	// Call commandArgs first!
	Verilated::commandArgs(argc, argv);
	
	//Instantiate our design
	Vcsa_recursive_adder_4_input *tb = new Vcsa_recursive_adder_4_input;
	
	int end_limit = 4294967295;
	int multiplier;
	
	tb->io_P_0 = 0;
	tb->io_P_1 = 155275577;
	tb->io_P_2 = 155275577;
	tb->io_P_3 = 155275577;
	
	tb->eval();
	
	cout << tb->io_Sout << ' ' << tb->io_Cout  << endl;	
	int i = 0;
	
	/*
	//cout << "before_loop";
	for (int n = 0; n < 1024*1024*1024; n++){
		//cout << n << '\r';
		
		for(int i_3 = 0; i_3 < 2; i_3++){
			for(int i_2 = 0; i_2 < 2; i_2++){
				for(int i_1 = 0; i_1 < 2; i_1++){
					for(int i_0 = 0; i_0 < 2; i_0++){
						tb->io_P_0 = n*i_0;
						tb->io_P_1 = n*i_1;
						tb->io_P_2 = n*i_2;
						tb->io_P_3 = n*i_3;
	
						tb->eval();
						
						multiplier = i_3*8 + i_2*4 + i_1*2 + i_0*1;
						
						if(tb->io_Sout + tb->io_Cout != multiplier*n){
							cout << n << ' ' << multiplier << endl;
						} 
					}
				}
			}
		}
	}*/
		
	/*
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
	}*/
	/*
	for(int i = 0; i < end_limit; i++){
		tb->io_A = 10;
		tb->io_B = 20;
		tb->io_Cin = 30;
		tb->eval();
		cout << tb->io_Sout << ' ' << (tb->io_Cout)*2 << endl;
	}*/
}
