/**
 *  m1
 *  Author: hqnghi
 *  Description:  
 */
//		aa<-gamlfile("SIR_switch.gaml" ); 
  
model comodel   
    
global {
//	list<file> SIRtest<-[];
//	init{  
//		loop i from:0 to:1{			
//			SIRtest<+gaml_file("SIR_switch.gaml","SIRsimulation","SIR"+i);
//		}
//		float tmp<-0.0;
//		loop SIR over:SIRtest{			
//			simulate comodel:SIR with_input:["enviSize"::50,"initial_S"::1495,"initial_I"::1] with_output:["current_model_S"::"tmp"];		
//		}
//	}
	

}
 
experiment comodel2exp type: gui {
	list<file> SIRtest<-[];
	init{  
		loop i from:0 to:1{			
			SIRtest<+gaml_file("SIR_switch.gaml","SIRsimulation","SIR"+i);
		}
		float tmp<-0.0;
		loop SIR over:SIRtest{			
			simulate comodel:SIR with_input:["enviSize"::50,"initial_S"::1495,"initial_I"::1] with_output:["current_model_S"::"tmp"];		
		}
	}
}
