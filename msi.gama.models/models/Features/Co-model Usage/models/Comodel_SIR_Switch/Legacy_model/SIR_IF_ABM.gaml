/**
 *  Comodel
 *  Author: Quang and Nghi
 *  Description: 
 */  
model SIR_IF_ABM

import "SIR_ABM.gaml"

experiment SIR_ABM_interface type:gui parent:SIR_ABM_exp {
	
	int get_num_S{
		return length(Host where (each.state=0));
	}
	
	int get_num_I{
		return length(Host where (each.state=1));
	}
		
	int get_num_R{
		return length(Host where (each.state=2));
	}
	
	
	action set_num_S(int num){
		ask (Host where (each.state=0)){
			do die;
		}
		create Host number:num {state<-0;}
	}
	
	action set_num_I(int num){
		ask (Host where (each.state=1)){
			do die;
		}
		create Host number:num {state<-1;}
	}
	
	action set_num_R(int num){
		ask (Host where (each.state=2)){
			do die;
		}
		create Host number:num {state<-2;}
	}
//		output{}
	

}