/**
 *  SAVE_AGENT2SQL
 *  Author: Truong Minh Thai (thai.truongminh@gmail.com)
 *  Description: 
 *      init: Select data from postgres and create location agents
 * 	    savetosql: Save data of agent into MySQL. 
 * 					transform= true because you need to transform geometry data from Absolute(GAMA) to Gis
 */

model SAVE_AGENT2SQL
 
/* Insert your model definition here */

  
global {
    var numAgent type:int init:0;
 	var BOUNDS type:map init: ['host'::'localhost','dbtype'::'postgres','database'::'SurveillanceNetDB','port'::'5432','user'::'postgres','passwd'::'tmt'
 		                    ,'srid'::'4326'
  							,"select"::	"select  ST_AsBinary(geom) as geom from vnm_adm1 ;"];
    var PARAMS type:map init: ['host'::'localhost','dbtype'::'Postgres','database'::'SurveillanceNetDB','port'::'5432','user'::'postgres','passwd'::'tmt'];
	var MySQL type:map init: ['host'::'localhost','dbtype'::'MySQL','port'::'3306','database'::'bph','user'::'root','passwd'::'root'];

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
		reflex savetosql{  // save data into MySQL
			let myid <-id;
			let myname <- custom_name;
			let mylocation <- shape;
			write "begin"+ myid;
			ask toto{
				do action: insert{ 
					arg params value: MySQL; 
					arg into value: "vnm_adm1";
					arg columns value:["ID_1", "name_1", "geom"];
					arg values value: [myid,myname,mylocation] ;
					arg transform value:true;  //Need to transform geometry data from Absolute(GAMA) to Gis
 				}
 			    write "finish "+ myid;
			}	
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

