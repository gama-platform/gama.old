/**
* Name: Predefined equestions
* Author: Benoit Gaudou
* Description: Presentation of all the predefined equation systems.
* Comparaison with hand-written systems to test them.
* Tags: equation, math
*/
model all_predefined_equations

global {
	float mu <- 0.02;
	float alpha <- 35.842;
	float gamma <- 100.0;
	float beta0 <- 1884.95;
	float beta1 <- 0.255;
	float hKR4 <- 0.01;
	
	init {
		create preSI  with: [h::0.1,N::500,I::1.0];
		create userSI with: [h::0.1,N::500,I::1.0];

		create preSIS  with: [h::0.1,N::500,I::1.0];
		create userSIS with: [h::0.1,N::500,I::1.0];
				
		create preSIR  with: [h::0.1,N::500,I::1.0];
		create userSIR with: [h::0.1,N::500,I::1.0];
		
		create preSIRS  with: [h::0.1,N::500,I::1.0];
		create userSIRS with: [h::0.1,N::500,I::1.0];	
		
		create preSEIR  with: [h::0.1,N::500,I::1.0];
		create userSEIR with: [h::0.1,N::500,I::1.0];		
		
		create preLV  with: [h::0.1,x::2.0,y::2.0];
		create userLV with: [h::0.1,x::2.0,y::2.0];			
	}
}


species preSI {
	float t;
	int N;
	float I ; 
	float S <- N - I; 
	float h;
	float beta<-0.4;

	// must be followed with exact order S, I, t  and N,beta
	equation eqSI type: SI vars: [S,I,t] params: [N,beta] ;
	/*reflex solving {
		list i_list;
		//solve eqSI method: "dp853" step: 0.01 cycle_length: 100 min_step: 1 max_step: 1 scalAbsoluteTolerance: 1 scalRelativeTolerance: 1 integrated_times: i_list ;
		//solve eqSI method:rk4 step:h;
	}*/
	
	reflex solving {
		list i_list;
		list v_list ;
		solve eqSI method: "rk4" step: h cycle_length: 1/h integrated_times: i_list integrated_values: v_list;
		write "i_list: " + i_list;
		write "v_list:" + v_list;
	}
}

species userSI {
	float t;
	int N;
	float I ; 
	float S <- N - I; 
	float h;
	float beta<-0.4;
	
	equation eqSI {
		diff(S,t) = -beta * S * I / N ;
		diff(I,t) = beta * S * I / N ;
	}		
	reflex solving{solve eqSI method:rk4 step:h;}
	
}


species preSIS {
	float t;
	int N;
	float I ; 
	float S <- N - I; 
	float h;
	float beta<-0.4;
	float gamma<-0.01;    		

	// must be followed with exact order S, I, t  and N,beta
	equation eqSIS type: SIS vars: [S,I,t] params: [N,beta,gamma] ;
	reflex solving {
		solve eqSIS method:rk4 step:h;
	}
}

species userSIS {
	float t;
	int N;
	float I ; 
	float S <- N - I; 
	float h;
	float beta<-0.4;
	float gamma<-0.01;    		
	
	equation eqSIS {
		diff(S,t) = -beta * S * I / N + gamma * I;
		diff(I,t) = beta * S * I / N - gamma * I;
	}		
	reflex solving {	
		solve eqSIS method:rk4 step:h;
	}
}


species preSIR {
	float t;
	int N;
	float I ; 
	float S <- N - I; 
	float R <- 0.0; 
	float h;
	float beta<-0.4;
	float gamma<-0.01; 

	// must be followed with exact order S, I, R, t  and N,beta,delta
	equation eqSIR type:SIR vars:[S,I,R,t] params:[N,beta,gamma] ;
	reflex solving {	
		solve eqSIR method:rk4 step:h;
	}
}

species userSIR{
	float t;
	int N;
	float I ; 
	float S <- N - I; 
	float R <- 0.0; 
	float h;
	float beta<-0.4;
	float gamma<-0.01; 				
	
	equation eqSIR {
		diff(S,t) = (- beta * S * I / N);
		diff(I,t) = (beta * S * I / N) - (gamma * I);
		diff(R,t) = (gamma * I);
	}		
	reflex solving {
		solve eqSIR method:rk4 step:h;
	}
}


species preSIRS {
	float t;
	int N;
	float I ; 
	float S <- N - I; 
	float R <- 0.0; 
	float h;
	float beta<-0.4;
	float gamma<-0.01; 
	float omega <- 0.05;
	float mu <- 0.01;

	// must be followed with exact order S, I, R, t  and N,beta,delta
	equation eqSIRS type: SIRS vars: [S,I,R,t] params: [N,beta,gamma,omega,mu] ;
	reflex solving {
		solve eqSIRS method:rk4 step:h;
	}
}

species userSIRS {
	float t;
	int N;
	float I ; 
	float S <- N - I; 
	float R <- 0.0; 
	float h;
	float beta<-0.4;
	float gamma<-0.01; 	
	float omega <- 0.05;
	float mu <- 0.01;   					
	
	equation eqSIRS {
		 diff(S,t) = mu * N + omega * R + - beta * S * I / N - mu * S ;
		 diff(I,t) = beta * S * I / N - gamma * I - mu * I ;
		 diff(R,t) = gamma * I - omega * R - mu * R ;
	}		
	reflex solving {
		solve eqSIRS method:rk4 step:h;
	}
}


species preSEIR {
	float t;
	int N;
	float S <- N - I;     	
	float E <- 0.0;
	float I ; 
	float R <- 0.0; 
	float h;
	float beta<-0.4;
	float gamma<-0.01; 
	float sigma <- 0.05;
	float mu <- 0.01;

	// must be followed with exact order S, E, I, R, t  and N,beta,gamma,sigma,mu
	equation eqSEIR type: SEIR vars: [S,E,I,R,t] params: [N,beta,gamma,sigma,mu] ;
	reflex solving {
		solve eqSEIR method:rk4 step:h;
	}
}

species userSEIR {
	float t;
	int N;
	float S <- N - I;     	
	float E <- 0.0;
	float I ; 
	float R <- 0.0; 
	float h;
	float beta<-0.4;
	float gamma<-0.01; 	
	float sigma <- 0.05;
	float mu <- 0.01;   					
	
	equation eqSEIR {
		diff(S,t) = mu * N - beta * S * I / N - mu * S ;
		diff(E,t) = beta * S * I / N - mu * E - sigma * E ;
		diff(I,t) = sigma * E - mu * I - gamma * I;
		diff(R,t) = gamma * I - mu * R ;
	}		
	reflex solving {
	solve eqSEIR method:rk4 step:h;
	}
}


species preLV {
	float t;
	float x ; 
	float y ; 
	float h;
	float alpha <- 0.8 ;
	float beta  <- 0.3 ;
	float gamma <- 0.2 ;
	float delta <- 0.85;

	// must be followed with exact order x, y, t  and  alpha,beta,delta,gamma
	equation eqLV type: LV vars: [x,y,t] params: [alpha,beta,delta,gamma] ;
	reflex solving {
		solve eqLV method:rk4 step:h;
	}
}

species userLV {
	float t;
	float x ; 
	float y ; 
	float h;
	float alpha <- 0.8 ;
	float beta  <- 0.3 ;
	float gamma <- 0.2 ;
	float delta <- 0.85;
	
	equation eqLV { 
		diff(x,t) =   x * (alpha - beta * y);
		diff(y,t) = - y * (delta - gamma * x);
    }		
	reflex solving {
		solve eqLV method:rk4 step:h;
	}
}



experiment examples type: gui {
	output {		
		display SI  {
			chart 'examplePreSI' type: series background: #lightgray position: {0,0} size:{1,0.5} {
				data "S" value: first(preSI).S color: #green;
				data "I" value: first(preSI).I color: #red;
			}
			chart 'examplesUserSI' type: series background: #lightgray position: {0,0.5} size:{1,0.5} {
				data "S" value: first(userSI).S color: #green;
				data "I" value: first(userSI).I color: #red;
			}
		}

		display SISs  {
			chart 'examplePreSIS' type: series background: #lightgray position: {0,0} size:{1,0.5} {
				data "S" value: first(preSIS).S color: #green;
				data "I" value: first(preSIS).I color: #red;
			}
			chart 'examplesUserSIS' type: series background: #lightgray position: {0,0.5} size:{1,0.5} {
				data "S" value: first(userSIS).S color: #green;
				data "I" value: first(userSIS).I color: #red;
			}			
		}
		
		display SIR  {
			chart 'examplePreSIR' type: series background: #lightgray position: {0,0} size:{1,0.5} {
				data "S" value: first(preSIR).S color: #green;
				data "I" value: first(preSIR).I color: #red;
				data "R" value: first(preSIR).R color: #blue;
			}
			chart 'examplesUserSIR' type: series background: #lightgray position: {0,0.5} size:{1,0.5} {
				data "S" value: first(userSIR).S color: #green;
				data "I" value: first(userSIR).I color: #red;
				data "R" value: first(userSIR).R color: #blue;
			}			
		}

		display SIRS  {
			chart 'examplePreSIRS' type: series background: #lightgray position: {0,0} size:{1,0.5} {
				data "S" value: first(preSIRS).S color: #green;
				data "I" value: first(preSIRS).I color: #red;
				data "R" value: first(preSIRS).R color: #blue;
			}
			chart 'examplesUserSIRS' type: series background: #lightgray position: {0,0.5} size:{1,0.5} {
				data "S" value: first(userSIRS).S color: #green;
				data "I" value: first(userSIRS).I color: #red;
				data "R" value: first(userSIRS).R color: #blue;
			}			
		}

		display SEIR  {
			chart 'examplePreSEIR' type: series background: #lightgray position: {0,0} size:{1,0.5} {
				data "S" value: first(preSEIR).S color: #green;
				data "E" value: first(preSEIR).E color: #yellow;
				data "I" value: first(preSEIR).I color: #red;
				data "R" value: first(preSEIR).R color: #blue;
			}
			chart 'examplesUserSEIR' type: series background: #lightgray position: {0,0.5} size:{1,0.5} {
				data "S" value: first(userSEIR).S color: #green;
				data "E" value: first(userSEIR).E color: #yellow;				
				data "I" value: first(userSEIR).I color: #red;
				data "R" value: first(userSEIR).R color: #blue;
			}
		}

		display LV  {
			chart 'examplePreLV' type: series background: #lightgray position: {0,0} size:{1,0.5} {
				data "x" value: first(preLV).x color: #yellow;
				data "y" value: first(preLV).y color: #blue;
			}
			chart 'examplesUserLV' type: series background: #lightgray position: {0,0.5} size:{1,0.5} {
				data "x" value: first(userLV).x color: #yellow;
				data "y" value: first(userLV).y color: #blue;
			}			
		}						
	}
}

experiment diff_predefined_defined_by_user type: gui {
	output {
		display diff  {
			chart 'diffSI' type: series background: #lightgray  position: {0,0} size:{0.5, 0.33} {
				data "dS" value: (first(userSI).S - first(preSI).S) color: #yellow;
				data "dI" value: (first(userSI).I - first(preSI).I) color: #blue;
			}
			chart 'diffSIS' type: series background: #lightgray position: {0.5,0} size:{0.5, 0.33} {
				data "dS" value: (first(userSIS).S - first(preSIS).S) color: #yellow;
				data "dI" value: (first(userSIS).I - first(preSIS).I) color: #blue;
			}
			chart 'diffSIR' type: series background: #lightgray position: {0,0.33} size:{0.5, 0.33} {
				data "dS" value: (first(userSIR).S - first(preSIR).S) color: #yellow;
				data "dI" value: (first(userSIR).I - first(preSIR).I) color: #blue;
				data "dR" value: (first(userSIR).R - first(preSIR).R) color: #red;
			}		
			chart 'diffSIRS' type: series background: #lightgray position: {0.5,0.33} size:{0.5, 0.33} {
				data "dS" value: (first(userSIRS).S - first(preSIRS).S) color: #yellow;
				data "dI" value: (first(userSIRS).I - first(preSIRS).I) color: #blue;
				data "dR" value: (first(userSIRS).R - first(preSIRS).R) color: #red;
			}	
			chart 'diffSEIR' type: series background: #lightgray position: {0,0.66} size:{0.5, 0.33} {
				data "dS" value: (first(userSEIR).S - first(preSEIR).S) color: #yellow;
				data "dE" value: (first(userSEIR).E - first(preSEIR).E) color: #yellow;				
				data "dI" value: (first(userSEIR).I - first(preSEIR).I) color: #blue;
				data "dR" value: (first(userSEIR).R - first(preSEIR).R) color: #red;
			}	
			chart 'diffLV' type: series background: #lightgray position: {0.5,0.66} size:{0.5, 0.33} {
				data "dx" value: (first(userLV).x - first(preLV).x) color: #yellow;
				data "dy" value: (first(userLV).y - first(preLV).y) color: #red;				
			}					
		}	
	}
}

