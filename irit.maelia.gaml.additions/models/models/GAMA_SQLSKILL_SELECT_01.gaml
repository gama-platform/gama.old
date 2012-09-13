/**
 *  GAMA_SQLSKILL_SELECT_01
 *  Author: Truong Minh Thai
 *  Description: 
 */

model GAMA_SQLSKILL_SELECT_01

/* Insert your model definition here */

  
global {
    var numAgent type:int;
	var SQL type:map init: ['url'::'tmthai','dbtype'::'MSSQL','port'::'1433','database'::'BPH','user'::'sa','passwd'::'tmt',
		"select"::	"Select ID_4,Name_4,geom.STAsBinary() as geo from VNM_ADM4 where id_2=38253 or id_2=38254"];
	init {
		create species: toto number: 1  
		{ 
			let  t value: self select[params::SQL]; //select data with parameter is a map type

			set listRes value: t; 
			set numAgent value: length(listRes at 2);  
			do write message: "number of Agent" + numAgent;

		}	
		ask (toto at 0) {
			create species:vnm_adm1 from: listRes with:[ id:: 0, custom_name:: 1, geo::2]{ set shape value: geo; }
		}
		 
	}
  
}   
environment bounds: SQL ;
entities {   
	species toto skills: [SQLSKILL]
	 {  
		var listRes type: list of: list init:[]; 
	 } 
	
	species vnm_adm1 {
		var id type: int;
		var custom_name type: string;
		var geo type:geometry;
		var color type: rgb init: rgb([rnd(255),rnd(255),0]);


		reflex test{
				do write message: ' id : ' + (id) + '; custom_name: ' + (custom_name);
		}
	}
}      

experiment default_expr type: gui {
	output {
		monitor numberAgent_ value: numAgent;
		display GlobalView {
			species vnm_adm1 transparency: 0 ;
		}
	}
}

