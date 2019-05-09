/**
* Name: equationTest
* Author: Tri
* Description: This model compares the results get with GAMA and Maple
* Tags: math, equation
*/

model equationTest


global{
	int final_cycle <- 20;
	file data_rk4_file <- csv_file("../../includes/data_rk4.csv","\t");
	file data_euler_file <- csv_file("../../includes/data_euler.csv","\t");
	file data_dp_file <- csv_file("../../includes/data_dp.csv","\t");
	
	int number_S <- 495 ; // The number of susceptible
	int number_I <- 5 ; // The number of infected
	int number_R <- 0 ; // The number of removed 
	int N <- number_S + number_I + number_R ;
	float beta <-  1.0 ; 
	float delta <- 0.01 ; 
	float hRK4 <- 0.005 ;
	
	float R0 ;
	
	init {
		create S {
			Ssize <- float(number_S) ;
		}
		create I {
			Isize <- float(number_I) ;
		}
		create R {
			Rsize <- float(number_R);
		}
		        
		   
		create my_SIR_maths_rk4 {
			self . Sm <- float(number_S) ;
			self . Im <- float(number_I) ;
			self . Rm <- float(number_R) ;
			self.numericalIntegrationStep <- hRK4;
		}
		
		create my_SIR_maths_euler {
			self . Sm <- float(number_S) ;
			self . Im <- float(number_I) ;
			self . Rm <- float(number_R) ;
			self.numericalIntegrationStep <- hRK4;
		}
		
		create my_SIR_maths_dp {
			self . Sm <- float(number_S) ;
			self . Im <- float(number_I) ;
			self . Rm <- float(number_R) ;
		}
		
		create testAgent { 
			data_rk4 <- matrix<float>(data_rk4_file);  
			data_euler <- matrix<float>(data_euler_file);  
			data_dp <- matrix<float>(data_dp_file);  
		}
		
		create my_SIR_maths_rk4 {
			self . Sm <- float(number_S) ;
			self . Im <- float(number_I) ;
			self . Rm <- float(number_R) ;
			self.numericalIntegrationStep <- 0.1;
		}
		
		create my_SIR_maths_rk4 {
			self . Sm <- float(number_S) ;
			self . Im <- float(number_I) ;
			self . Rm <- float(number_R) ;
			self.numericalIntegrationStep <- 1.0;
		}
	}	
		
	reflex endSim{
		if (cycle = final_cycle)  {
			ask first(testAgent) {do summarize;}
			do pause;
		}
	}	
	
}


species S {
	float Ssize ;
	float t ;
	
	equation evol simultaneously: [I, R] {
		diff (self . Ssize , t) = 
		 - beta * first(I).Isize * first (S).Ssize / N;
	}
	reflex solveEquation{
		solve evol method: rk4 step_size: hRK4;
	}
}

species I {
	float Isize ; 
	float t ;
	
	equation evol simultaneously: [S , R] {
		diff (self. Isize , t) = 
		       ( beta * first (S) . Ssize * self. Isize / N ) - delta * self . Isize ;
	}
}
species R {
	float Rsize ;
	float t ;
	equation evol simultaneously: [S , I ] {
		diff (self.Rsize , t) = delta * first(I).Isize ;
	}
}
	

	
species my_SIR_maths_rk4 {
	float t ;
	float Im ;
	float Sm ;
	float Rm ;
	list<list<float>> data_cont <- [[],[],[]];
	
	float numericalIntegrationStep;
	
	equation SIR {
		diff ( Sm , t ) = - beta * Sm * Im / N; 
		diff ( Im , t ) =   beta * Sm * Im / N  - delta * Im; 
		diff ( Rm , t ) =   delta * Im;
	}
	reflex solveEquation{
		solve SIR method: rk4 step_size: numericalIntegrationStep ;
	}
	
	reflex computeContinuousValues {
		write "S= "+Sm;
		loop i from:0 to: 2{
			data_cont[i] <- t[] accumulate (first(testAgent).return_value(each, i));
		}
	}
}

species my_SIR_maths_euler {
	float t ;
	float Im ;
	float Sm ;
	float Rm ;
	
	float numericalIntegrationStep;
	
	equation SIR {
		diff ( Sm , t ) = - beta * Sm * Im / N; 
		diff ( Im , t ) =   beta * Sm * Im / N  - delta * Im; 
		diff ( Rm , t ) =   delta * Im;
	}
	reflex solveEquation{
		solve SIR method: Euler step_size: numericalIntegrationStep ;
	}
}

species my_SIR_maths_dp {
	float t ;
	float Im ;
	float Sm ;
	float Rm ;

	equation SIR {
		diff ( Sm , t ) = - beta * Sm * Im / N; 
		diff ( Im , t ) =   beta * Sm * Im / N  - delta * Im; 
		diff ( Rm , t ) =   delta * Im;
	}
	reflex solveEquation {
		solve SIR method: DormandPrince54  min_step: 7.105427e-16 max_step: 0.0003 step_size: 0.0001 scalAbsoluteTolerance:10^-7 scalRelativeTolerance: 0.000001;
	}
}


species testAgent{
	matrix<float> data_rk4;
	matrix<float> data_euler;
	matrix<float> data_dp;
	
	float rk4Error <- 0.0;
	float eulerError <- 0.0;
	float dpError <- 0.0;
	
	reflex compute {
		rk4Error <- rk4Error + abs(first(my_SIR_maths_rk4).Sm - data_rk4[0,cycle+1]);
		eulerError <- eulerError + abs(first(my_SIR_maths_euler).Sm - data_euler[0,cycle+1]);
		dpError <- dpError + abs(first(my_SIR_maths_dp).Sm - data_dp[0,cycle+1]);
	}

	float return_value(float t, int i) {
		int t0 <- floor(t) as int;
		return data_rk4[i,t0]+ (t-t0)*(data_rk4[i,t0+1] - data_rk4[i,t0]);
	}
	
	action summarize {
		write "Error from Euler solver: " + eulerError;
		write "Error from RK4 solver: " + rk4Error;
		write "Error from DP solver: " + dpError;
	}
	
}


experiment simulation type: gui {
	output {
		display "RK4 Test" refresh: every(1 #cycle) {
			chart 'Comparision rk4 from Gama (lines) with rk4 from Maple (markers)' type: series background: #lightgray size:{0.5,0.5} position:{0.0,0.0} {
				data "S" value: (first(my_SIR_maths_rk4).Sm) color: #green marker: false; 
				data "I" value: (first(my_SIR_maths_rk4).Im) color: #red marker: false;
				data "R" value: (first(my_SIR_maths_rk4).Rm) color: #blue marker: false;
				data "S (data)" value: first(testAgent).return_value(time+1,0) color: #green marker_shape: marker_diamond line_visible: false;	
				data "I (data)" value: first(testAgent).return_value(time+1,1) color: #red   marker_shape: marker_diamond line_visible: false;			
				data "R (data)" value: first(testAgent).return_value(time+1,2) color: #blue  marker_shape: marker_diamond line_visible: false;			
			}
			
			chart 'Continuous graph test' type: series background: #lightgray size:{0.5,0.5} position:{0.0,0.5} x_serie: first(my_SIR_maths_rk4).t[]  {
				data "S" value: (first(my_SIR_maths_rk4).Sm[]) color: #green marker: false; 
				data "I" value: (first(my_SIR_maths_rk4).Im[]) color: #red   marker: false;
				data "R" value: (first(my_SIR_maths_rk4).Rm[]) color: #blue  marker: false;
				data "S (data)" value: (first(my_SIR_maths_rk4).data_cont[0]) color: rgb(150,255,150);
				data "I (data)" value: (first(my_SIR_maths_rk4).data_cont[1]) color: rgb(255,150,150); 	
				data "R (data)" value: (first(my_SIR_maths_rk4).data_cont[2]) color: rgb(150,150,255);	
			}
			
			chart 'Split system test' type: series background: #lightgray size:{0.5,0.5} position:{0.5,0.0}  {
				data "S" value: (first(S).Ssize) color: #green marker: false; 
				data "I" value: (first(I).Isize) color: #red   marker: false;
				data "R" value: (first(R).Rsize) color: #blue  marker: false;
				data "S (data)" value: first(testAgent).data_rk4[0,cycle+1] color: #green marker_shape: marker_diamond line_visible: false;	
				data "I (data)" value: first(testAgent).data_rk4[1,cycle+1] color: #red   marker_shape: marker_diamond line_visible: false;			
				data "R (data)" value: first(testAgent).data_rk4[2,cycle+1] color: #blue  marker_shape: marker_diamond line_visible: false;
			}
			
			chart 'Integration step test' type: series background: #lightgray size:{0.5,0.5} position:{0.5,0.5}  {
				data "S" value: (first(my_SIR_maths_rk4).Sm) color: #green marker: false; 
				data "I" value: (first(my_SIR_maths_rk4).Im) color: #red   marker: false;
				data "R" value: (first(my_SIR_maths_rk4).Rm) color: #blue  marker: false;
				data "S2" value: (my_SIR_maths_rk4[1].Sm) color: #green marker: false; 
				data "I2" value: (my_SIR_maths_rk4[1].Im) color: #red   marker: false;
				data "R2" value: (my_SIR_maths_rk4[1].Rm) color: #blue  marker: false;
				data "S3" value: (my_SIR_maths_rk4[2].Sm) color: #green marker: false; 
				data "I3" value: (my_SIR_maths_rk4[2].Im) color: #red   marker: false;
				data "R3" value: (my_SIR_maths_rk4[2].Rm) color: #blue  marker: false;
				data "S (data)" value: first(testAgent).data_rk4[0,cycle+1] color: #green marker_shape: marker_diamond line_visible: false;	
				data "I (data)" value: first(testAgent).data_rk4[1,cycle+1] color: #red   marker_shape: marker_diamond line_visible: false;			
				data "R (data)" value: first(testAgent).data_rk4[2,cycle+1] color: #blue  marker_shape: marker_diamond line_visible: false;
			}	
		}
	}
}