/**
* Name:  Agents to Database in MSSQL
* Author: Truong Minh Thai, Quang Truong
* Description:  This model shows how to Insert and Delete agents from a MSSQL DB
* Tags: database
  */

model agent2DB_MSSQL 
  
global {  
	file buildingsShp <- file('../includes/building.shp');
	file boundsShp <- file('../includes/bounds.shp');
	geometry shape <- envelope(boundsShp);
	map<string,string> PARAMS <- [//'srid'::'4326', // optinal
								  'host'::'localhost','dbtype'::'sqlserver','database'::'GAMAMSSQL',
								  'port'::'1433','user'::'gama_usr','passwd'::'123456'];

	init {
		write "This model will work only if the corresponding database is installed";
		create buildings from: buildingsShp with: [type::string(read('NATURE'))];
		create bounds from: boundsShp;
		
		create DB_Accessor 
		{ 			
			do executeUpdate params: PARAMS updateComm: "DELETE FROM buildings";	
			do executeUpdate params: PARAMS updateComm: "DELETE FROM bounds";
		}
		write "Click on <<Step>> button to save data of agents to DB";		 
	}
}   
  
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
					  columns: [ "nature", "geom"]
					  values: [myself.type,myself.shape];
		}
	    write "finish "+ name;
	}	
	
	aspect default {
		draw shape color: #gray ;
	}
}     

experiment default_expr type: gui {
	output {
		
		display GlobalView {
			species buildings aspect: default;
		}
	}
}

