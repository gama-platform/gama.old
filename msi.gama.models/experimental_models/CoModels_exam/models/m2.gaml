/**
 *  m1
 *  Author: Asus
 *  Description: 
 */ 

model M21    
//import "m3.gaml"
 
global {
	/** Insert the global definitions, variables and actions here */
	int n<-4;
	int microM<-1;
//	geometry shape<-envelope(10);
//	list out_agent function:{length(M21_rn as list)};
	init{
//		write location; 
		create M21_rn number:n{
			create M211 number:n;
		}
		
//		write ""+self+" init with n = "+length(M21_rn);
//		create M22_rn number:n;
//		run of:"SS" with_experiment:"s";
	}
	reflex sss{
//		write self;
	}
} 

species M21_rn skills:[moving] {
	species M211{
		int IQ<-rnd(100);
	}
	int IQ<-rnd(100);
	action living{
//		write self;
		do wander;
	}
	reflex dolive{
//		write ""+"i'm alive !!";
//write pitch; 
		do living;
//		if(flip(0.5)){
//			create M1_rnd;
//		}
	}
	aspect base{
		draw square(1);
	}
}	
//species M22_rn skills:[moving] {
//	int IQ<-rnd(100);
//	reflex dolive{
////		write "alive";
//		do move;
////		if(flip(0.5)){
////			create M1_rnd;
////		}
//	}
//	aspect base{
//		draw circle(1);
//	}
//}	
experiment expm2 type: gui {
	
//	int microM<-1;
//	parameter "nn" var:n init:4;
	/** Insert here the definition of the input and output of the model */
	
	output {
		display "m21_disp"{
			species M21_rn aspect:base; 			
		}
//		display m22_disp{
//			species M22_rn aspect:base; 			
//		}
	}
}


