/**
* Name:  Agents to Database in PostGIS
* Author: Truong Minh Thai
* Description: 
* savetosql: Save data of agent into MySQL.
* 
* transform= true because you need to transform geometry data from Absolute(GAMA) to Gis
* 
*  NOTE: Create database and tables using Create Spatial Table in PostGIS.gaml. Then use the this model to insert data from shapefile
* Geometry  column in PostGIS is in Multipolygon 
* Tags: database
*/

model agent2DB_POSTGIS 
  
global { 
	file buildingsShp <- file('../../includes/building.shp') ;
	geometry shape <- envelope(buildingsShp);
	 
	map<string,string> PARAMS <-  ['srid'::'4326', // optinal postgis
								   'host'::'localhost','dbtype'::'postgres','database'::'spatial_db2d',
								   'port'::'5432','user'::'postgres','passwd'::''];

	init {
		write "This model will work only if the corresponding database is installed" color: #red;
		write "The model \"Create Spatial Table in PostGIS.gaml\" can be run previously to create the database and tables. The model should be modified to create the database spatial_db2d.";
		
		create buildings from: buildingsShp with: [type::string(read ('NATURE'))];
		write "Click on <<Step>> button to save data of agents to DB";
		
		create DB_Accessor
		{ 			
			do executeUpdate params: PARAMS updateComm: "DELETE FROM buildings";	
		} 
	}
}   
  
species DB_Accessor skills: [SQLSKILL] ;   

species buildings {
	string type;
	
	reflex printdata{
		write " name : " + (name) + "; type: " + (type) + "shape:" + shape;
	}
//	
	reflex savetosql{  // save data into Postgres
		write "begin"+ name;
	    ask (DB_Accessor) {
			do executeUpdate params: PARAMS updateComm: "INSERT INTO buildings(type,geom) VALUES('" + myself.type + "',ST_Multi(ST_GeomFromText('" + myself.shape +"',4326)))";
		}	
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

