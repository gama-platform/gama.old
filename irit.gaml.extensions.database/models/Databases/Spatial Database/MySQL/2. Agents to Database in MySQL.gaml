/**
* Name:  Agents to Database in MySQL
* Author: Truong Minh Thai
* Description:   init: Select data from table vnm_adm2 (Created via QGis software)  and create  agents
* 
* savetosql: Save data of agent into MySQL. 
* 
* transform= true because you need to transform geometry data from Absolute(GAMA) to Gis
* 
* NOTE: You should have created a database 'spatial_DB_GAMA' into the MySQL server 
*  in order that the model can run properly.
* Tags: database
*/

model agent2DB_MySQL 
  
global { 
	file buildingsShp <- file('../../includes/building.shp');
	file boundsShp <- file('../../includes/bounds.shp');
	geometry shape <- envelope(boundsShp);
	
	map<string,string> PARAMS <- ['srid'::'4326',
				                  'host'::'localhost','dbtype'::'mysql','database'::'spatial_DB_GAMA',
				                  'port'::'8889','user'::'root','passwd'::'root'];

	init {
		write "This model will work only if the corresponding database is installed" color: #red;
		write "The model \"Create Spatial Table in MySQL.gaml\" can be run previously to create the table.";
		
		create buildings from: buildingsShp with: [type::string(read ('NATURE'))];
		create bounds from: boundsShp;
		
		create DB_Accessor number: 1  
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
	
	reflex savetosql{  // save data into MySQL
		write "begin save of: "+ name;
		ask DB_Accessor {
			do insert params: PARAMS into: "bounds"
					  columns: ["geom"]
					  values: [myself.shape];
		}
	    write "finished save of: "+ name;
	}		
}

species buildings {
	string type;
	
	reflex printdata{
		 write ' name : ' + (name) + '; type: ' + (type) + "shape:" + shape;
	}
	
	reflex savetosql{  // save data into MySQL
		write "begin save of: "+ name;
		ask DB_Accessor {
			do insert params: PARAMS into: "buildings"
					  columns: ["name", "type","geom"]
					  values: [myself.name,myself.type,myself.shape];
		}
	    write "finished save of: "+ name;
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

