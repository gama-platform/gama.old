/**
 *  GAMA_SQLSKILL_SELECT_01
 *  Author: Truong Minh Thai
 *  Description: 
 */

model GAMA_SQLSKILL_SELECT_02

/* Insert your model definition here */

  
global {
    var numAgent type:int;
	var BOUNDS type:map init: ['url'::'tmthai','dbtype'::'MSSQL','port'::'1433','database'::'BPH','user'::'sa','passwd'::'tmt',
								"select"::	"select geom.STAsBinary() as geo from VNM_ADM4 where id_2=38253 or id_2=38254"];
	var PARAMS type:map init: ['url'::'tmthai','dbtype'::'MSSQL','port'::'1433','database'::'BPH','user'::'sa','passwd'::'tmt'];
	var LOCATIONS type:string init: "select ID_4, Name_4, geom.STAsBinary() as geo from VNM_ADM4 where id_2=38253 or id_2=38254";
	init {
		create species: toto number: 1  
		{ 
			let t value: list(self select [params:: PARAMS, select:: LOCATIONS]); //select data with parameter is a map type
			set numAgent value: length(list(t at 2));  
			do write message: "number of Agent" + numAgent;
			create species:vnm_adm1 from: t with:[ id:: "id_4", custom_name:: "name_4", geo::"geo"]{ set shape value: geo; }
		}
		 
	}
  
}   
environment bounds: BOUNDS ;
entities {   
	species toto skills: [SQLSKILL]
	 {  
		//Nothing
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

