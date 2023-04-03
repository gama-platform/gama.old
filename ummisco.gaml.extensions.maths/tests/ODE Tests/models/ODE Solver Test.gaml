/**
* Name: ODE Solver Test
* Author: Tri Nguyen-Huu, Huynh Nghi
* 
* Description: This model tests that the behaviour of equations regarding to the time step of the simulation, the 
* bounds of integration and the unit of parameters is correct. It compares the numerical solutions computed by ODE 
* solvers from GAMA and Maple.
* 
* The experiment 'simulation' compares the curves obtained from ODE solvers (rk4) with data in the upper left chart.
* The upper right charts compares the curve obtained with a ODE system split between several agents. The lower left
* chart illustrates the curves obtained in continuous display. The lower right charts compares the curves obtained
* for different integration steps.
* 
* Tests 1, 2 and 3 verify that solutions obtained with rk4, euler and DP correspond to their equivalent in MAPLE.
* Test 4 is passed if the solution of a system between several agents is the same than the one from the unsplitted system.
* Test 5 is passed if the numerical solution is correct when changing the step_size 
* Test 6 is passed if the numerical solution is correct when changing the time scale "step"
* Test 7 is passed if the numerical solution is correct when using facets t0 and tf 
* Test 8 is passed if the numerical solution is correct when changing the scale of the model parameters
* 
* Tags: math, equation
*/



model ODETest


global{
	bool with_viz <- true;
	bool with_t0_tf <- false;


	int final_cycle <- 19;
	file data_rk4_file <- csv_file("../includes/data_rk4.csv","\t", float, false);
	file data_euler_file <- csv_file("../includes/data_euler.csv","\t", float, false);
	file data_dp_file <- csv_file("../includes/data_dp.csv","\t", float, false);
	matrix<float> data_rk4 <- matrix<float>(data_rk4_file);
	matrix<float> data_euler <- matrix<float>(data_euler_file);  
	matrix<float> data_dp <- matrix<float>(data_dp_file);  
	map<string,matrix<float>> data <- ["rk4"::data_rk4,"euler"::data_euler,"dp"::data_dp];
	
	float hRK4 <- 0.005 #s;
	
	float rk4_err <- 0.0;
	float rk4_stepsize_err <- 0.0;
	float euler_err <- 0.0;
	float dp_err <- 0.0;
	float split_err <- 0.0;
	
	float beta <- 1.0/#s; 
	float delta <- 0.01/#s;

	init {
		create SIR_model;
		
		if (step < 1#mn){// user defined integration step. To be tested only for small values of step, otherwise the computation takes too long
		
			create SIR_model {self.numericalIntegrationStep <- 0.1;}
		
			create SIR_model {self.integrator <- "euler";}
		
			create SIR_model{self.integrator <- "dp";}
		}
		
		create S;
		create I;
		create R;
		
		if !with_viz{
			loop test_t from: 0 to: 20{
				do compute_err(test_t*step);
				ask SIR_model {do solveEquation;}
				ask S {do solveEquation;}
			}
		}
	}
	
	reflex compute_err when: with_viz{
		do compute_err(time);
	}
	
	action compute_err(float t2) {
		rk4_err <- rk4_err + abs(first(SIR_model).Sm - return_value("rk4",t2,0));
		if step < 1#mn{
			rk4_stepsize_err <- rk4_stepsize_err + abs(first(SIR_model  where (each.integrator="rk4" and each.numericalIntegrationStep >= 0)).Sm - return_value("rk4",t2,0));
			euler_err <- euler_err + abs(first(SIR_model where (each.integrator="euler")).Sm - return_value("euler",t2,0));
			dp_err <- dp_err + abs(first(SIR_model where (each.integrator="dp")).Sm - return_value("dp",t2,0));
		}
		split_err <- split_err + abs(first(S).Ssize - first(SIR_model).Sm);
	}
	
	float return_value(string integrator, float t, int i)
	{
		float t_scaled <- step = 1#h ? t/step:t;
		int t0 <- floor(t_scaled) as int;
		return data[integrator][i,t0]+ (t_scaled-t0)*(data[integrator][i,t0+1] - data[integrator][i,t0]);
	}
	
	reflex endSim when:  (cycle >= final_cycle) {
		write "Error from Euler solver: " + euler_err;
		write "Error from RK4 solver: " + rk4_err;
		write "Error from RK4 solver with different step size: " + rk4_stepsize_err;
		write "Error from DP solver: " + dp_err;
		write "Error from split system solver: " + split_err;
		do pause;
	}		
}

	species S {
		float Ssize <- 495.0;
		float t ;
		
		equation evol simultaneously: [I, R] {
			diff (self . Ssize , t) = - beta * first (S).Ssize * first(I).Isize / 500;
		}
		
		reflex solveEquation{
			do solveEquation;
		}
		
		action solveEquation{
			solve evol method: "rk4";
		}
	}
	
	species I {
		float Isize <- 5.0; 
		float t ;
		
		equation evol simultaneously: [S , R] {
			diff (self. Isize , t) =  beta * first (S) . Ssize * self. Isize / 500  - delta * self . Isize ;
		}

	}
	species R {
		float Rsize <- 0.0;
		float t ;
		equation evol simultaneously: [S , I ] {
			diff (self.Rsize , t) = delta * first(I).Isize ;
		}
	}
	
	species SIR_model{
		string integrator <- "rk4";	
		list<list<float>> data_cont <- [[],[],[]];	
		float numericalIntegrationStep <- -1.0;		
		
		float t ;
		float Sm <- 495.0;
		float Im <- 5.0;
		float Rm <- 0.0;

		equation SIR {
			diff ( Sm , t ) = - beta * Sm * Im / 500; 
			diff ( Im , t ) =   beta * Sm * Im / 500  - delta * Im; 
			diff ( Rm , t ) =   delta * Im;
		}
		
		reflex solveEquation{
			do solveEquation;
		}
		
		action solveEquation{
		if !with_t0_tf{
			solve SIR method: "rk4";
		}else{
			solve SIR method: "rk4" t0: 0.0 tf: step;
		}
	}
		
		action solveEquation{
			switch integrator{
				match "rk4" {
					if !with_t0_tf{
						if numericalIntegrationStep<0{
							solve SIR method: "rk4"; 
						}else{
							solve SIR method: "rk4" step_size: numericalIntegrationStep ;
						}	
					}else{
						solve SIR method: "rk4" t0: 0.0 tf: step;
					}
				}
				match "euler" {solve SIR method: "Euler" ;}
				match "dp" {solve SIR method: #DormandPrince54  min_step: 7.105427e-16 max_step: 0.03 scalAbsoluteTolerance:10^-7 scalRelativeTolerance: 0.000001;}
			}
			
		}
		
		reflex computeContinuousValues when: (with_viz and int(self)=0){//only for the first simulation
			loop i from:0 to: 2{
				data_cont[i] <- t[] accumulate world.return_value("rk4",each, i);
			}
		}
	}
	
	
experiment test type: test {
	test "Equations" {
		create ODETest_model with: [with_viz::false,step::1#s];//, beta::1.0 /#s, delta::0.01 /#s];
		// tests for consistency of the ODE solver with the same ODE solvers from Maple and Matlab
		write "Testing solutions for equations using Euler, RK4 and DP solver in Gama:";
		write "Error from Euler solver: " + euler_err;
		write "Error from RK4 solver: " + rk4_err;
		write "Error from DP solver: " + dp_err;
		assert rk4_err < 10^-10;
		assert euler_err < 10^-10;
		assert dp_err < 10^-3;
		// tests that the spit system gives the same numerical solution than the non-split system (rk4)
		write "\nError between split and unsplit systems (rk4):s "+split_err;
		assert split_err = 0;
		write "\nDifference between solutions (rk4) with defaut step_size\nand a user defined step_size (0.1): "+rk4_stepsize_err;
		assert rk4_stepsize_err < 10^-3;
		create ODETest_model with: [with_viz::false, step::2#s];//beta::1.0 /#s, delta::0.01 /#s];
		write "\nDifference between solutions obtained from Gama (rk4) and \nMaple, when changing the step (step = 2s): "+rk4_err;
		assert rk4_err < 10^-7;
		create ODETest_model with: [with_viz::false, with_t0_tf::true,step::2#s];//beta::1.0 /#s, delta::0.01 /#s];
		write "\nTest the behaviour of facets t0 and tf: "+rk4_err;
		assert rk4_err < 10^-7;	
		create ODETest_model with: [with_viz::false,step::1#h, beta::1.0 /#hour, delta::0.01 /#h];
		write "\nTest the behaviour with a changed timescale (step=1h) \nand rescaled parameters beta and delta.: "+rk4_err;
		assert rk4_err < 10^-10;	
	}	
}

experiment simulation type: gui {
	action _init_ {
		create ODETest_model with: [with_viz::true, step::1#s, name::"Simulation step=1s"];
		create ODETest_model with: [with_viz::true, step::2#s, name::"Simulation step=2s"];
	}
	
	output {
		display "Equation Tests" refresh: every(1 #cycle)  type: 2d {
			chart 'Comparision rk4 from Gama (lines) with rk4 from Maple (markers)' type: series background: #white size:{0.5,0.5} position:{0.0,0.0} {
				data "S" value: (first(SIR_model).Sm) color: #green marker: false; 
				data "I" value: (first(SIR_model).Im) color: #red marker: false;
				data "R" value: (first(SIR_model).Rm) color: #blue marker: false;
				data "S (data)" value: world.return_value("rk4",time+step,0) color: rgb('green') marker_shape: marker_diamond line_visible: false;	
				data "I (data)" value: world.return_value("rk4",time+step,1) color: rgb('red') marker_shape: marker_diamond line_visible: false;			
				data "R (data)" value: world.return_value("rk4",time+step,2) color: rgb('blue') marker_shape: marker_diamond line_visible: false;			
			}
			chart 'Continuous graph test' type: series background: #white size:{0.5,0.5} position:{0.0,0.5} x_serie: first(SIR_model).t[]  {
				data "S" value: (first(SIR_model).Sm[]) color: #green marker: false; 
				data "I" value: (first(SIR_model).Im[]) color: #red marker: false;
				data "R" value: (first(SIR_model).Rm[]) color: #blue marker: false;
				data "S (data)" value: (first(SIR_model).data_cont[0]) color: rgb(150,255,150);// marker: false;	
				data "I (data)" value: (first(SIR_model).data_cont[1]) color: rgb(255,150,150); 	
				data "R (data)" value: (first(SIR_model).data_cont[2]) color: rgb(150,150,255);	
			}
			chart 'Split system test' type: series background: #white size:{0.5,0.5} position:{0.5,0.0}  {
				data "S" value: (first(S).Ssize) color: #green marker: false; 
				data "I" value: (first(I).Isize) color: #red marker: false;
				data "R" value: (first(R).Rsize) color: #blue marker: false;
				data "S (data)" value: world.return_value("rk4",time+step,0) color: rgb('green') marker_shape: marker_diamond line_visible: false;	
				data "I (data)" value: world.return_value("rk4",time+step,1) color: rgb('red') marker_shape: marker_diamond line_visible: false;			
				data "R (data)" value: world.return_value("rk4",time+step,2) color: rgb('blue') marker_shape: marker_diamond line_visible: false;
			}
			chart 'Integration step test' type: series background: #white size:{0.5,0.5} position:{0.5,0.5}  {
				data "S" value: (first(SIR_model).Sm) color: #green marker: false; 
				data "I" value: (first(SIR_model).Im) color: #red marker: false;
				data "R" value: (first(SIR_model).Rm) color: #blue marker: false;
				data "S2" value: (SIR_model[1].Sm) color: #green marker: false; 
				data "I2" value: (SIR_model[1].Im) color: #red marker: false;
				data "R2" value: (SIR_model[1].Rm) color: #blue marker: false;
				data "S3" value: (SIR_model[2].Sm) color: #green marker: false; 
				data "I3" value: (SIR_model[2].Im) color: #red marker: false;
				data "R3" value: (SIR_model[2].Rm) color: #blue marker: false;
				data "S (data)" value: world.return_value("rk4",time+step,0) color: rgb('green') marker_shape: marker_diamond line_visible: false;	
				data "I (data)" value: world.return_value("rk4",time+step,1) color: rgb('red') marker_shape: marker_diamond line_visible: false;			
				data "R (data)" value: world.return_value("rk4",time+step,2) color: rgb('blue') marker_shape: marker_diamond line_visible: false;		
			}	
		}
	}
}