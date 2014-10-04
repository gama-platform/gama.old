/**
 *  m1
 *  Author: hqnghi 
 *  Description:   
 */     
model comodelSIR  
import "m2.gaml" as M1
//import "SIR_switch.gaml" as SIRswitch
global {  
	
	geometry shape<-envelope(50); 
	int macroM<-5;
	list toto;
	init{   		
//		create A; 
// 		create M1.expm2  with:[n::1+rnd(5)] returns:MM number:5; 
// 		toto<-MM;
		create M1.expm2 with:[n::1];// number:4;		
//		create SIRswitch.SIRsimulation with:[initial_S::1495] number:1;
//		create expm2 number:1;
//		create SIRswitch.SIRsimulation with:[initial_S::1495] number:2;

//		ask  M1.expm2 {
//			do _init_;			
//		}

//		ask  M1.expm2 {
//			create M21_rn;
//			ask M21_rn{
//				do _init_;
//				write "\n\tM21 IQ= "+IQ;
//				ask M211{
//					do _init_;
//					write "\t\tM211 IQ= "+IQ;
//				}
//			}			
//		}
		do verb_ing;
	}    	
	action verb_ing{
//		create M1.expm2 with:[n::200];// number:4;
		
//		ask expm2{
//			write "species "+self+" in mainModel ";
//		}
		ask  M1.expm2 {
			write "experiment "+self+" in microModel ";
			write "\nbefore exchange data";
			write "microM \t\t\t"+microM ;
			write "myself.macroM \t"+myself.macroM;
			int tmp<-microM;
			microM<-myself.macroM;
			myself.macroM<-tmp;
			write "\nafter exchange data";
			write "microM \t\t\t"+microM;
			write "myself.macroM \t"+myself.macroM;
			ask M21_rn{
				write "\n\tM21 IQ= "+IQ;
				ask M211{
					write "\t\tM211 IQ= "+IQ;
				}
			}
			
			do _step_;
		}
	}
	reflex ssss {
		ask  M1.expm2 {
			do _step_;
		}
//		do verb_ing;
//		ask  M1.expm2 { 
//			loop times:5{
////				write ""+self+" step";			
//				do _step_;
//			}
//		}
	}
}  
   
//species expm2{}
//species expm2 skills:[moving] {
//	int n<-100; 
//	reflex dosomething{     
//		do wander;
//	}
//	aspect base{ 
//		draw circle(1) color:rgb("red");
//	}  
//} 
experiment comodelExp type: gui {
	
	output {
//		display "a_disp" background:rgb("black") {
//			species expm2 aspect:base;			
//		}  

	}
}
