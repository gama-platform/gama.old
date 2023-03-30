/**
* Name: ODE Solver Consitency Test
* Author: Tri Nguyen-Huu, Huynh Quang Nghi
* 
* Description: Tests the consistency of the different equation solvers implemented in Gama. A solution is computed 
* for each numerical integrator embedded in Gama. 
* 
* The experiment 'with_vizualization' plots the curve obtained for each methods. Consistency is admitted when the 
* curves seem to overlap.
* 
* The tests 'consistency_test' calculates the maximum difference between the solution given by any two methods for 
* 20 cycles. Since the Euler method is far less efficient than the others, two indicators are caculated: one with 
* all the methods, and one with the Euler method. The test is passed if the differences are smaller than some 
* threshold (10^-3 without Euler, 0.7 with Euler).
* 
* Tags: math, equation
*/

model consistencyTest


global{
	int final_cycle <- 20;
	float step <- 1.0 ;
	bool with_viz <- false;

	list methods<-["Euler", "ThreeEighthes","Midpoint", "Gill", "Luther", "rk4", "dp853", "AdamsBashforth", "AdamsMoulton","DormandPrince54", "GraggBulirschStoer", "HighamHall54"];
	list<rgb> color_list<-list<rgb>(brewer_colors("Paired"));

// model parameters
	float beta <-  1.5 ; 
	float delta <- 0.3 ; 

// numerical integration parameters	
	float stepsize <- 0.005;
	float min_step <- 7.105427e-16;
	float abs_tol <- 10^-7;
	float rel_tol <- 0.000001;

// compute the maximum difference between the solutions of the different models, including Euler (diff1) and excluding Euler (diff2). They should be as low as possible.
	float diff1 <- 0.0;
	float diff2 <- 0.0;
	
	
	init {
		
		loop i from: 0 to: length(methods)-1{
			create EDO_model{
				my_method <- methods[i];
				color <- color_list[i];
			}
		}
		
		if !with_viz{
			loop test_t from: 0 to: 20{
				ask EDO_model {do solveEquation;}
				do compute_diff;
			}
		}
	}	
	
	reflex compute_diff when: with_viz{
		do compute_diff;
	}
	
	action compute_diff{
		diff1 <- max([diff1,max(EDO_model collect each.Im)-min(EDO_model collect each.Im)]);
		diff2 <- max([diff2,max((EDO_model where (each.my_method !="Euler")) collect each.Im)-min((EDO_model where (each.my_method !="Euler")) collect each.Im)]);
	}
	
	reflex endSim when:  (cycle >= final_cycle) {
		write "Maximum difference between solutions (including Euler) after 20 cycles: " + diff1;
		write "Maximum difference between solutions (excluding Euler) after 20 cycles: " + diff2;
		do pause;
	}	
	
}

	species EDO_model {
		string my_method;
		float t ;
		float Sm <- 495.0;
		float Im <- 5.0;
		float Rm <- 0.0;
		float N <- Sm + Im + Rm;
		rgb color;
		
		equation SIR {
			diff ( Sm , t ) = - beta * Sm * Im / N; 
			diff ( Im , t ) =   beta * Sm * Im / N  - delta * Im; 
			diff ( Rm , t ) =   delta * Im;
		}
		
		reflex solveEquation{
			do solveEquation;
		}
				
		action solveEquation{
			switch my_method{
				match "rk4" {solve SIR method: #rk4 step_size: stepsize ;}
				match "Euler" {solve SIR method: #Euler step_size: 0.1*stepsize ;}
				match "ThreeEighthes" {solve SIR method: #ThreeEighthes step_size: stepsize;}
				match "Midpoint" {solve SIR method: #Midpoint step_size: stepsize;}
				match "Gill" {solve SIR method: #Gill step_size: stepsize;}
				match "Luther" {solve SIR method: #Luther step_size: stepsize;}
				match "dp853" {solve SIR method:  #dp853 min_step: min_step max_step: 0.0003 scalAbsoluteTolerance:abs_tol scalRelativeTolerance: rel_tol;}
				match "AdamsBashforth" {solve SIR method: #AdamsBashforth min_step: min_step max_step: 0.0003 scalAbsoluteTolerance:abs_tol scalRelativeTolerance: rel_tol;}
				match "AdamsMoulton" {solve SIR method: #AdamsMoulton min_step: min_step max_step: 0.0003 scalAbsoluteTolerance:abs_tol scalRelativeTolerance: rel_tol;}
				match "DormandPrince54" {solve SIR method: #DormandPrince54 min_step: min_step max_step: 0.0003 scalAbsoluteTolerance:abs_tol scalRelativeTolerance: rel_tol;}
				match "GraggBulirschStoer" {solve SIR method: #GraggBulirschStoer min_step: min_step max_step: 0.0003 scalAbsoluteTolerance:abs_tol scalRelativeTolerance: rel_tol;}
				match "HighamHall54" {solve SIR method: #HighamHall54 min_step: min_step max_step: 0.0003 scalAbsoluteTolerance:abs_tol scalRelativeTolerance: rel_tol;}
			}
		}
	}

experiment consistency_test type: test {
	test "consistency" {
		with_viz <- false;
		assert diff1 < 0.6;
		assert diff2 < 10^-3;
	}
}

experiment with_visualization type: gui {
	action _init_ {
		create consistencyTest_model with: [with_viz::true];
	}
	output {
		display "Numerical solutions" refresh: every(1 #cycle)  type: 2d {
			chart 'Comparision between the numerical solutions provided by EDO solvers' type: series background:  #white  { 
				loop m over: EDO_model{
					data m.my_method value: m.Im color: m.color marker: false;
				}		
			}	
		}
	}
}