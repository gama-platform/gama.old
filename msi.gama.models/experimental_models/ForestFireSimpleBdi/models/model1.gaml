/**
 *  model1
 *  Author: van-minh le
 *  Description: 
 */

model model1

global {
	init{
		create helicopter number: 1{
			set persistence_coefficient <- 0.7;
			set fact_base <- [];
		}
		
	}
	
	reflex global_scenario when: (time mod 10 = 9){
		ask first(helicopter){
			set fire_degree <- 20;
		}
	}
	/** Insert the global definitions, variables and actions here */
}

environment {
	/** Insert the grids and the properties of the environment */
}

entities {
	species helicopter control: simple_bdi{
		float fire_degree <- 1;
		
		/* */
		perceive {
			write "preception phase";
			do add_predicate predicate_name: "Fire" predicate_parameters: [1, 2];
			write "fact base" + fact_base;
		}
		/* */
		
		plan gotoFireScene priority: fire_degree{
			write "goto fire scene with the persistenc of " + persistence_coefficient;
		}
		plan gotoBase priority: 10{
			//set persistence_coefficient <- 0.8;
			write "goto base with the persistenc of " + persistence_coefficient;
		}
	}
}

experiment model1 type: gui {
	/** Insert here the definition of the input and output of the model */
}
