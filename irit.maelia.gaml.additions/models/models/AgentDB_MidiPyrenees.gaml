/**
 *  AgentDB_MidiPyrenees
 *  Author: Truong Minh Thai
 *  Description: 
 */

model AgentDB_MidiPyrenees

/* Insert your model definition here */

  
global {
	var BOUNDS type:map init: ['host'::'tmthai','dbtype'::'SQLSERVER','port'::'1433','database'::'BPH','user'::'sa','passwd'::'tmt',
								"select"::	"select geom.STAsBinary() as geo from FRA_ADM2 WHERE ID_1=1004"];
	//							"select"::	"select geom.STAsBinary() as geo from FRA_ADM2"];

	var PARAMS type:map init: ['host'::'tmthai','dbtype'::'SQLSERVER','port'::'1433','database'::'BPH','user'::'sa','passwd'::'tmt'];

    // Where example
	//var LOCATIONS type:string init: "select ID_2, Name_2, geom.STAsBinary() as geo from FRA_ADM2  where A.id_1=1004" ;

	// Join 2 table example
	var LOCATIONS type:string init: "SELECT B.id_2 as ID_2, B.name_2, A.geom.STAsBinary() as geo1,B.geom.STAsBinary() as geo 
									FROM fra_adm1 as A JOIN fra_adm2 as B ON A.id_1=B.id_1 
									WHERE A.ID_1=1004" ;
	init {
		create species: toto number: 1  
		{ 
			do action: connect with: [params::PARAMS];			
			create species:locations from: list(self select [select:: LOCATIONS]) with:[ id:: "id_2", name_2:: "name_2", geo::"geo"]{ set shape value: geo; }
		}
		 
	}
  
}   
environment bounds: BOUNDS ;
entities {   
	species toto parent: AgentDB {
		var listRes type: list init:[];		
	}
	
	species locations {
		var id type: int;
		var name_2 type: string;
		var geo type:geometry;
		var color type: rgb init: rgb([rnd(255),rnd(255),0]);


		reflex test{
				do write message: ' id : ' + (id) + '; name: ' + (name_2);
		}
	}
}      

experiment default_expr type: gui {
	output {
		display GlobalView {
			species locations transparency: 0 ;
		}
	}
}

