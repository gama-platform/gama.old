/**
 *  sis
 *  Author: 
 *  Description: A compartmental SI model 
 */

model si

global { 
	float beta <- 0.8 parameter: 'Beta (S->I)'; 	// The parameter Beta
	float delta <- 0.01 parameter: 'Delta (I->R)'; // The parameter Delta
	float s1<-1;
	float s2<-10;
	float s3<-100;
	init {
		create my_SIR_maths with: [h::1,mycycle::s1];
		create my_SIR_maths with: [h::0.1,mycycle::s2];
		create my_SIR_maths with: [h::0.01,mycycle::s3];	
		
		create my_SIR_maths_old_school with: [h::1];
		create my_SIR_maths_old_school with: [h::0.1];
		create my_SIR_maths_old_school with: [h::0.01];		
  	}  
  	reflex sss{
  		write "\n";
  	}
}


entities {

    species my_SIR_maths {
		int N <- 500;
    	float t;    
    	float mycycle<-1.0;
		float I <- 1.0; 
		float S <- N - I; 
		float R <- 0.0; 
   		float h;
   		list dI <- [];
		list dR <- [];
		list dS <- [];
		list dT <- [];
		equation SIR{ 
			diff(S,t) = (- beta * S * I / N);
			diff(I,t) = (beta * S * I / N) - (delta * I);
			diff(R,t) = (delta * I);
		} 
  
//		equation_SIR var:[]; 
				solve SIR method: "rk4" step: h cycle_length:mycycle{}
                
//    	solve SIR method: "rk4" step: h cycle_length:mycycle discretizing_step: 10 integrated_times: dT integrated_values: [dS, dI, dR] { }       
	}
	
	species my_SIR_maths_old_school skills: [EDP] {
		int N <- 500;
		float I <- 1.0;
		float S <- N - I;
		float R <- 0.0;
   		float h;
   		
   		map equations <- [
			"S"::"(- beta * S * I / N)",
			"I"::"(beta * S * I / N) - (delta * I)",
			"R"::"(delta * I)"
		];
		map param <- [
			"beta"::beta,
			"delta"::delta,
			"N"::N
		];
	
		reflex go {
			let varValues type: list of: float <- [];
			add S to: varValues;
			add I to: varValues;
			add R to: varValues;
					
			let temp type: list of: float <- list(self RK4 [
			  				equations::equations,  
			  				value::varValues, 
			  				param::param, 
			  				h::h
			  			]); 
			set S value: (temp at 0);
			set I value: (temp at 1);
			set R value: (temp at 2);
		}	
   		
//		reflex go {
//			let temp type: list of: float <- list(self RK4SIR [S::S, I::I, R::R, alpha::beta, beta::delta, N::N, h::h]);
//			set S value: (temp at 0);
//			set I value: (temp at 1);
//			set R value: (temp at 2);
//		}

	}	
}

experiment mysimulation1 type: gui { 
 	output { 
		display SIRt refresh_every: 1 {
			chart "SI - h=1" type: series background: rgb('white') position:{0,0} size:{1,0.33} {
				data 'S' value: first(my_SIR_maths where (each.mycycle = s1)).S color: rgb('green');				
				data 'I' value: first(my_SIR_maths where (each.mycycle = s1)).I color: rgb('red') ;
				data 'R' value: first(my_SIR_maths where (each.mycycle = s1)).R color: rgb('yellow') ;				
			}
			chart "SI - h=0.1" type: series background: rgb('white') position:{0,0.33} size:{1,0.33} {
				data 'S' value: first(my_SIR_maths where (each.mycycle = s2)).S color: rgb('green');				
				data 'I' value: first(my_SIR_maths where (each.mycycle = s2)).I color: rgb('red') ;
				data 'R' value: first(my_SIR_maths where (each.mycycle = s2)).R color: rgb('yellow') ;				
			}
			chart "SI - h=0.01" type: series background: rgb('white') position:{0,0.66} size:{1,0.33} {
				data 'S' value: first(my_SIR_maths where (each.mycycle = s3)).S color: rgb('green');				
				data 'I' value: first(my_SIR_maths where (each.mycycle = s3)).I color: rgb('red') ;
				data 'R' value: first(my_SIR_maths where (each.mycycle = s3)).R color: rgb('yellow') ;				
			}
		}
		display SIRoldschool refresh_every: 1 {
			chart "SI - h=1" type: series background: rgb('white') position:{0,0} size:{1,0.33} {
				data 'S' value: first(my_SIR_maths_old_school where (each.h = 1)).S color: rgb('green');				
				data 'I' value: first(my_SIR_maths_old_school where (each.h = 1)).I color: rgb('red') ;
				data 'R' value: first(my_SIR_maths_old_school where (each.h = 1)).R color: rgb('yellow') ;				
			}
			chart "SI - h=0.1" type: series background: rgb('white') position:{0,0.33} size:{1,0.33} {
				data 'S' value: first(my_SIR_maths_old_school where (each.h = 0.1)).S color: rgb('green');				
				data 'I' value: first(my_SIR_maths_old_school where (each.h = 0.1)).I color: rgb('red') ;
				data 'R' value: first(my_SIR_maths_old_school where (each.h = 0.1)).R color: rgb('yellow') ;				
			}
			chart "SI - h=0.01" type: series background: rgb('white') position:{0,0.66} size:{1,0.33} {
				data 'S' value: first(my_SIR_maths_old_school where (each.h = 0.01)).S color: rgb('green');				
				data 'I' value: first(my_SIR_maths_old_school where (each.h = 0.01)).I color: rgb('red') ;
				data 'R' value: first(my_SIR_maths_old_school where (each.h = 0.01)).R color: rgb('yellow') ;				
			}
		}		
	}
}
