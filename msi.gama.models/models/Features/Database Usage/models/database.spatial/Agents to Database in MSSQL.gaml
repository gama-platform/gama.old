/**
 *  SAVE_AGENT2SQL
 *  Author: Truong Minh Thai (thai.truongminh@gmail.com)
 */

model agent2DB_MSSQL 
  
global {  
	file buildingsShp <- file('../../includes/building.shp');
	file boundsShp <- file('../../includes/bounds.shp');
	geometry shape <- envelope(boundsShp);
	map<string,string> PARAMS <- [//'srid'::'4326', // optinal
								  'host'::'127.0.0.1','dbtype'::'sqlserver','database'::'spatial_DB',
								  'port'::'1433','user'::'sa','passwd'::'tmt'];

	init {
		create buildings from: buildingsShp with: [type::string(read ('NATURE'))];
		create bounds from: boundsShp;
		
		create DB_Accessor 
		{ 			
			do executeUpdate params: PARAMS updateComm: "DELETE FROM buildings";	
			do executeUpdate params: PARAMS updateComm: "DELETE FROM bounds";
		}
		write "Click on <<Step>> button to save data of agents to DB";		 
	}
}   

entities {   
	species DB_Accessor skills: [SQLSKILL] ;   
	species bounds {
		reflex printdata{
			 write ' name : ' + (name) ;
		}
		
		reflex savetosql{  // save data into MSSQL
			write "begin"+ name;
			ask DB_Accessor {
				do insert params: PARAMS into: "bounds"
						  columns: ["geom"]
						  values: [myself.shape];
			}
		    write "finish "+ name;
		}		
	}
	species buildings {
		string type;
		
		reflex printdata{
			 write ' name : ' + (name) + '; type: ' + (type) + "shape:" + shape;
		}
		
		reflex savetosql{  // save data into MSSQL
			write "begin"+ name;
			ask DB_Accessor {
				do insert params: PARAMS into: "buildings"
						  columns: ["name", "type","geom"]
						  values: [myself.name,myself.type,myself.shape];
			}
		    write "finish "+ name;
		}	
		
		aspect default {
			draw shape color: rgb('gray') ;
		}
	}
}      

experiment default_expr type: gui {
	output {
		
		display GlobalView {
			species buildings aspect: default;
		}
	}
}

