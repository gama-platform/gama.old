model SIR_EBM

global {
	init{
		create agent_with_SIR_dynamic;
	} 
}


species agent_with_SIR_dynamic {
	int N <- 495 ;
	int iInit <- 5;		

    float t;  
	float S <- N - float(iInit); 	      
	float I <- float(iInit); 
	float R <- 0.0; 
	
	float alpha <- 0.2;
	float beta <- 0.8; 

	float h <- 0.01;
   
	equation SIR{ 
		diff(S,t) = (- beta *   S * I / N);
		diff(I,t) = (  beta*  S * I / N) - (alpha * I)  ;
		diff(R,t) = (  alpha  *  I) ;
	}
                
    reflex solving {
//    	write S;
    	solve SIR method: "rk4" step_size: h;
    }    
}


experiment SIR_EBM_exp type: gui {
	output { 
		display display_charts  type: 2d  {
			chart "SIR_agent" type: series background: #white {
				data 'S' value: first(agent_with_SIR_dynamic).S color: #green ;				
				data 'I' value: first(agent_with_SIR_dynamic).I color: #red ;
				data 'R' value: first(agent_with_SIR_dynamic).R color: #blue ;
			}
		}
	}
}
