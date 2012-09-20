/**
 *  MidiPyrenees
 *  Author: Truong Minh Thai
 *  Description: 
 */

model MidiPyrenees

/* Insert your model definition here */

  
global {
	var BOUNDS type:map init: ['host'::'tmthai','dbtype'::'SQLSERVER','port'::'1433','database'::'BPH','user'::'sa','passwd'::'tmt',
								"select"::	"select geom.STAsBinary() as geo from FRA_ADM2 where id_1=1004"];
	var PARAMS type:map init: ['host'::'tmthai','dbtype'::'SQLSERVER','port'::'1433','database'::'BPH','user'::'sa','passwd'::'tmt'];
	var LOCATIONS type:string init: "select ID_2, Name_2, geom.STAsBinary() as geo from FRA_ADM2 where id_1=1004";
	init {
		create species: toto number: 1  
		{ 
			create species:locations from: list(self select [params:: PARAMS, select:: LOCATIONS]) with:[ id:: "id_2", name_2:: "name_2", geo::"geo"]{ set shape value: geo; }
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

