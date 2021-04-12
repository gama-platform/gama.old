/**
* Name:  Agents to Database in PostGIS
* Author: Truong Minh Thai
* Description: 
* init: Select data from table vnm_adm2 (Created via QGis software)  and create  agents
* 
* savetosql: Save data of agent into MySQL.
* 
* transform= true because you need to transform geometry data from Absolute(GAMA) to Gis
* 
* Tags: database
*/

model agent2DB_MySQL 
  
global { 
	file buildingsShp <- file('../includes/building.shp');
	file boundsShp <- file('../includes/bounds.shp');
	geometry shape <- envelope(boundsShp);
	 
	map<string,string> PARAMS <-  [//'srid'::'4326', // optional
								   'host'::'localhost','dbtype'::'Postgres','database'::'spatial_db',
								   'port'::'5432','user'::'postgres','passwd'::''];

	init {
		write "This model will work only if the corresponding database is installed" color:#red; 
		write "The model \"Create Spatial Table in PostGIS.gaml\" can be run previously to create the database and tables.";
		
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
  
species DB_Accessor skills: [SQLSKILL] ;   

species bounds {
	reflex printdata{
		 write " name : " + (name) ;
	}
	
	reflex savetosql{  // save data into Postgres
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
		write " name : " + (name) + "; type: " + (type) + "shape:" + shape;
	}
	
	reflex savetosql{  // save data into Postgres
		write "begin"+ name;
		ask DB_Accessor {
			do insert params: PARAMS into: "buildings"
					  columns: ["name", "type","geom"]
					  values: [myself.name,myself.type,myself.shape];
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

