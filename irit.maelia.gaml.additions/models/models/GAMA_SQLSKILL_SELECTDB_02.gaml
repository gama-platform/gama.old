/**
 *  GAMA_SQLSKILL_SELECTDB_02
 *  Author: Truong Minh Thai
 *  Description: 
 */

model GAMA_SQLSKILL_SELECTDB_02

/* Insert your model definition here */

  
global {
	//var SHAPE_ADMINISTRATIF_DISTRICT type: string init: '../includes/vnm_adm4.shp' parameter: 'Administrative boudaries - Vietnam:' category: 'ADMINISTRATIF REGIONS' ;
    //var SHAPE_ADMINISTRATIF_DISTRICT type: string init: '../includes/haugiang_adm4.shp' parameter: 'Administrative boudaries - Vietnam:' category: 'ADMINISTRATIF REGIONS' ;
    //var SHAPE_ADMINISTRATIF_DISTRICT type: string init: '../includes/DONGTHAP_district.shp' parameter: 'Administrative boudaries - France:' category: 'ADMINISTRATIF REGIONS' ;
    var numAgent type:int;
	var rec type: list;
	var SQL type:map init: ['url'::'tmthai','dbtype'::'MSSQL','port'::'1433','database'::'BPH','user'::'sa','passwd'::'tmt',
		"select"::	"select  geom.STAsBinary() as geo from VNM_ADM4 where id_2=38254"];

	init {
		create species: toto number: 1  
		{
			let  t value: self selectDB[ 
 				dbtype:: "MSSQL",
 				url :: "localhost",  
 				port:: "1433", 
 				database:: "BPH",  
 				user:: "sa",
 				passwd:: "tmt",
				select::		"select  id_4,Name_4,geom.STAsBinary() as geo from VNM_ADM4 where id_2=38254"
				//selectComm::		"select  id_4,Name_4,geom.STAsBinary() as geo from VNM_ADM4 where id_2=38253"
				// 3289: Bac Trung bo
 				// 3290: Dong bac 
 				// 3291: DBSCL
 				// 3292: Dong Bang Song Hong
 				// 3293: Dong Nam Bo
 				// 3294: Nam Trung Bo 
 				// 3295: Tay Bac
 				// 3296: Tay Nguyen
 				// ID_2: 
 				//    38254: Can tho
 				//    38253: Dong Thap
 				
 			];
 		
 			//write "t : " + t;
 		
			set listRes value: t;
			set numAgent value: length(listRes at 2);
			//set numAgent value: length(listRes);
			do write message: "number of Agent" + numAgent;
		}	
		
		create species: toto number: 1  
		{
			let  t value: self selectDB[ 
 				dbtype:: "MSSQL",
 				url :: "localhost",  
 				port:: "1433", 
 				database:: "BPH",  
 				user:: "sa",
 				passwd:: "tmt",
 				select::"select  id,ID_LT,ID_2,geom.STAsBinary() as geo from LightTrap_adm4 where id_2=38254"
 				//selectComm::"select  id,ID_LT,ID_2,geom.STAsBinary() as geo from LightTrap_adm4 where id_2=38253"
  				// ID_2: 
 				//    38254: Can tho
 				//    38253: Dong Thap
 			];
 		
 			//write "t : " + t;
 		
			set listRes value: t;
			set numAgent value: length(listRes at 2);
			//set numAgent value: length(listRes);
			do write message: "number of Agent" + numAgent;
			/* 
			let n value: length(listRes);
			loop i from: 0 to: n-1 {
				do write message: listRes at i;
			} 
			*/
		}	
 
		ask (toto at 0) {
			create species:vnm_adm1 from: listRes with:[ id:: 0, custom_name:: 1, geo::2]{ set shape value: geo; }
			//create species:vnm_adm1 from: listRes with:[ id:: 0, custom_name:: 1, geo::2];
		}
		ask (toto at 1) {
			create species:lightTraps from: listRes with:[ id:: 0, id_lt:: 1, id_2:: 2, geo:: 3]{ set shape value: geo; }
			
		}
		 
	}

}  
  
//environment bounds: SHAPE_ADMINISTRATIF_DISTRICT ;
environment bounds: SQL ;
entities {   
	species toto skills: [SQLSKILL] {  
	//species toto skills: [MAELIADBMS] {  	
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
}

experiment default_expr type: gui {
	output {
		monitor numberAgent_ value: numAgent;
		display GlobalView {
		//species grid_region transparency: 0 ;
			species vnm_adm1 transparency: 0 ;
			species lightTraps transparency: 0 ;
		}
	}
}

