/**
 *  comodel
 *  Author: Administrator
 *  Description: 
 */

model SIR_MATHS

global {

	
	

	init{
		create my_SIR_maths number:1;
	}
}

environment  width: 10 height: 10;

entities {
species my_SIR_maths {
	float alpha <- 0.2 min: 0.0 max: 1.0;
	float beta <- 0.8 min: 0.0 max: 1.0;
	int N <- 1500 min: 1 max: 3000;
	float hKR4 <- 0.01;
	int iInit <- 1;
    float t;    
	float I <- float(iInit); 
	float S <- N - I; 
	float R <- 0.0; 
   
	equation SIR{ 
			diff(S,t) = (- beta * S * I / N);
			diff(I,t) = (beta * S * I / N) - (alpha * I);
			diff(R,t) = (alpha * I);
	}
                
    solve SIR method: "rk4" step:0.001{ 
    	
    }
        
}
}

experiment maths type: gui {
	output { 
		display SIR_MATHS refresh_every: 1 {
			chart "SIR_MATHS" type: series background: rgb('white') {
				data 'S' value: first(list(my_SIR_maths)).S color: rgb('green') ;				
				data 'I' value: first(list(my_SIR_maths)).I color: rgb('red') ;
				data 'R' value: first(list(my_SIR_maths)).R color: rgb('blue') ;
			}
		}
	}
}
