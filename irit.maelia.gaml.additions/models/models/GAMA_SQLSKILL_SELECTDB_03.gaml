/**
 *  GAMA_SQLSKILL_SELECTDB_02
 *  Author: Truong Minh Thai
 *  Description: 
 */

model GAMA_SQLSKILL_SELECTDB_02

/* Insert your model definition here */

  
global { 
    var numAgent type:int;
	var BOUNDS type:map init: ['host'::'tmthai','dbtype'::'SQLSERVER','port'::'1433','database'::'BPH','user'::'sa','passwd'::'tmt',
		"select"::	"select  geom.STAsBinary() as geo from VNM_ADM4 where id_2=38253 or id_2=38254"];
	

	init {
		create species: toto number: 1  
		{
			set listRes value: list(self selectDB[  
 				dbtype:: "SQLSERVER",
 				host :: "localhost",  
 				port:: "1433", 
 				database:: "BPH",  
 				user:: "sa",
 				passwd:: "tmt",
				select::		"select  id_4,Name_4,geom.STAsBinary() as geo from VNM_ADM4 where id_2=38253 or id_2=38254"
				//ID_1:
				// 	3289: Bac Trung bo
 				// 	3290: Dong bac 
 				// 	3291: DBSCL
 				// 	3292: Dong Bang Song Hong
 				// 	3293: Dong Nam Bo
 				//	3294: Nam Trung Bo 
 				// 	3295: Tay Bac
 				//	3296: Tay Nguyen
 				// ID_2: 
 				//    38254: Can tho
 				//    38253: Dong Thap
 				
 			]);
 		
			set numAgent value: length(listRes at 2);
			do write message: "number of Agent" + numAgent;
			create species:locations from: listRes with:[ id:: "id_4", custom_name:: "name_4", geo::"geo"]{ set shape value: geo; }
			
		set listRes value: list(self selectDB[ 
 				dbtype:: "SQLSERVER",
 				host :: "localhost",  
 				port:: "1433", 
 				database:: "BPH",  
 				user:: "sa",
 				passwd:: "tmt",
 				select::"select  id,ID_LT,ID_2,geom.STAsBinary() as geo from LightTrap_adm4 where id_2=38253 or id_2=38254"
 				]);
			set numAgent value: length(listRes at 2);
			do write message: "number of Agent" + numAgent;
 			create species:lightTraps from: listRes with:[ id::"id", id_lt:: "id_lt", id_2::"id_2", geo:: "geo"]{ set shape value: geo; }
 
 				
  			set listRes value: list(self selectDB[ 
 				dbtype:: "SQLSERVER",
 				host :: "localhost",  
 				port:: "1433", 
 				database:: "BPH",  
 				user:: "sa",
 				passwd:: "tmt",
 				select::"select  id,ID_2,geom.STAsBinary() as geo from vnm_grid2 where id_2=38253 or id_2=382534"
 			]);
			set numAgent value: length(listRes at 2);
			do write message: "number of Agent" + numAgent;
  			create species:myGrids from: listRes with:[ id:: "id", id_2:: "id_2", geo:: "geo"]{ set shape value: geo; }
		}	 
	}

}  

environment bounds: BOUNDS ;
entities {   
	species toto skills: [SQLSKILL] {  
		var listRes type: list of: list init:[]; 
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
		monitor numberAgent_ value: numAgent;
		display GlobalView {
			species locations transparency: 0 ;
			species lightTraps transparency: 0 ;
			species myGrids transparency: 0.9 ;
		}
	}
}

