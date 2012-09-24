/**
 *  GAMA_SQLSKILL_SELECT_01
 *  Author: Truong Minh Thai
 *  Description: 
 */

model MySQL_SQLSKILL_SELECT_02
 
/* Insert your model definition here */

  
global {
    var numAgent type:int;
	var BOUNDS type:map init: ['host'::'localhost','dbtype'::'MySQL','port'::'3306','database'::'bph','user'::'root','passwd'::'root',
								"select"::	"select shape from VNM_ADM4"];

	var PARAMS type:map init: ['host'::'localhost','dbtype'::'MySQL','port'::'3306','database'::'bph','user'::'root','passwd'::'root'];
	var LOCATIONS type:string init: "select ID_4, name_4, shape from VNM_ADM4";
	init {
		create species: toto number: 1  
		{ 
			create species:locations from: list(self select [params:: PARAMS, select:: LOCATIONS]) with:[ id:: "id_4", custom_name:: "name_4", geo::"shape"]{ set shape value: geo; }
		}
		 
	}
  
}   
environment bounds: BOUNDS ;
entities {   
	species toto skills: [SQLSKILL]
	 {  
		//Nothing
	 } 
	
	species locations {
		var id type: int;
		var custom_name type: string;
		var geo type:geometry;
		var color type: rgb init: rgb([rnd(255),rnd(255),0]);


		reflex test{
				set numAgent value: numAgent+1;
				do write message: 'agent '+numAgent+ ' id : ' + (id) + '; custom_name: ' + (custom_name);
			
		}
	}
}      

experiment default_expr type: gui {
	output {
		monitor numberAgent_ value: numAgent;
		display GlobalView {
			species locations transparency: 0 ;
		}
	}
}

