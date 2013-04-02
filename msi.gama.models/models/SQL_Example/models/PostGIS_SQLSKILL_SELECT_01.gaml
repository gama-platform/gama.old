/**
 *  Author: Truong Minh Thai (thai.truongminh@gmail.com)
 *  Description: 
 *       select data from :postgres/postgis and create agent with selected data
 */

model PostGis_SQLSKILL_SELECT_01
 
/* Insert your model definition here */

  
global {
    var numAgent type:int init:0;
 	var BOUNDS type:map init: ['host'::'localhost','dbtype'::'postgres','database'::'SurveillanceNetDB','port'::'5432','user'::'postgres','passwd'::'tmt'
 		                    ,'srid'::'4326'
  							,"select"::	"select  ST_AsBinary(geom) as geom from vnm_adm1"];
    var PARAMS type:map init: ['host'::'localhost','dbtype'::'Postgres','database'::'SurveillanceNetDB','port'::'5432','user'::'postgres','passwd'::'tmt'];

	var LOCATIONS type:string init: "select ID_1, name_1, ST_AsBinary(geom) as geo from vnm_adm1";
	init {
		create species: toto number: 1  
		{ 
			create species:locations from: list(self select [params:: PARAMS, select:: LOCATIONS]) with:[ id:: "id_1", custom_name:: "name_1", geo::"geo"]{ set shape value: geo; }
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
				write 'agent '+numAgent+ ' shape: ' + geo;
			
		}
	}
}      

experiment default_expr type: gui {
	output {
		
		display GlobalView {
			species locations transparency: 0 ;
		}
		monitor numberAgent_ value: numAgent;
	}
}

