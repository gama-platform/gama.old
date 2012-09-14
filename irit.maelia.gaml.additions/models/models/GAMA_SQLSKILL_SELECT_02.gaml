/**
 *  GAMA_SQLSKILL_SELECT_02
 *  Author: Truong Minh Thai
 *  Description: 
 */

model GAMA_SQLSKILL_SELECT_02

/* Insert your model definition here */

global {

	var PARAMS type:map init: ['url'::'tmthai',
	                           'dbtype'::'MSSQL',
	                           'port'::'1433',
	                           'database'::'BPH',
	                           'user'::'sa',
	                           'passwd'::'tmt'];
	                           
	var BOUNDS type:map init: ['url'::'tmthai',
	                           'dbtype'::'MSSQL',
	                           'port'::'1433',
	                           'database'::'BPH',
	                           'user'::'sa',
	                           'passwd'::'tmt',
	                           "select"::	"select  geom.STAsBinary() as geo from VNM_ADM4 where id_2=38253 or id_2=38254"];
	var LOCATIONS  type:string init: "select  id_4 as id,Name_4 as name,geom.STAsBinary() as geo from VNM_ADM4 where id_2=38253 or id_2=38254";
	var LIGHTTRAPS type:string init: "select  id,ID_LT,ID_2,geom.STAsBinary() as geo from LightTrap_adm4 where id_2=38253 or id_2=38254";
	var GRIDS      type:string init: "select  id,ID_2,geom.STAsBinary() as geo from vnm_grid2 where id_2=38253 or id_2=38254";

	init {
	   // create toto agent with SQLSKILL
	   create species: toto number: 1 ; 
       
       // use toto to create other agents
       ask (toto at 0)	
		{
 			//Create location agents
 			//let t value: list(self select [params::PARAMS, select::LOCATIONS]); 
 			//create species:locations from: t with:[ id:: "id", custom_name:: "name", geo::"geo"]{ set shape value: geo; }
 			create species:locations from: list(self select [params::PARAMS, select::LOCATIONS]) with:[ id:: "id", custom_name:: "name", geo::"geo"]{ set shape value: geo; }
			
			//Create light trap agents
			//set t value: list(self select [params::PARAMS, select::LIGHTTRAPS]); 
			//create species:lightTraps from: t with:[ id:: "id", id_lt:: "id_lt", id_2:: "id_2", geo:: "geo"]{ set shape value: geo; }
			create species:lightTraps from: list(self select [params::PARAMS, select::LIGHTTRAPS]) with:[ id:: "id", id_lt:: "id_lt", id_2:: "id_2", geo:: "geo"]{ set shape value: geo; }

			//Create grid agents
			//set t value: list(self select [params::PARAMS, select::GRIDS]); 
			//create species:myGrids from: t with:[ id:: "id", id_2:: "id_2", geo:: "geo"]{ set shape value: geo; }
			create species:myGrids from: list(self select [params::PARAMS, select::GRIDS]) with:[ id:: "id", id_2:: "id_2", geo:: "geo"]{ set shape value: geo; }
		}	
	}

}  
  
environment bounds: BOUNDS ;

entities {   
	species toto skills: [SQLSKILL] {  
		//var listRes type: list of: list init:[]; 
	} 
	
	species locations {
		var id type: int;
		var custom_name type: string;
		var geo type:geometry;
		var color type: rgb init: rgb([rnd(255),rnd(255),0]);


		reflex test{
				do write message: ' id : ' + (id) + '; custom_name: ' + (custom_name);
		}
	}
	species lightTraps {
		var id type: int;
		var id_lt type: int;
		var id_2 type: int;
		var geo type:geometry;
		var color type: rgb init: rgb([255,0,0]);
		reflex test{
				do write message: 'LightTrap id : ' + (id) + '; ID_LT: ' + (id_lt);
		}
	}
	species myGrids {
		var id type: int;
		var id_2 type: int;
		var geo type:geometry;
		var color type: rgb init: rgb([0,255,0]);
	}
}

experiment default_expr type: gui {
	output {
		display GlobalView {
			species locations transparency: 0 ;
			species lightTraps transparency: 0 ;
			species myGrids transparency: 0.9 ;
		}
	}
}

