/**
* Name:  Agents from Database in PostGIS
* Author: Benoit Gaudou
* Description:  This model does SQL query commands and create agents using the results
* Tags: database
  */

model DB2agentPOSTGIS 

global {
	map<string,string> BOUNDS <- [	//'srid'::'32648', // optinal
	 								'host'::'localhost',
									'dbtype'::'postgis',
									'database'::'GAMA_POSTGIS',
									'port'::'5432',
									'user'::'postgres',
									'passwd'::'123456',
								  	'select'::'SELECT ST_AsEWKB(geom) as geom FROM buildings;' ];
	map<string,string> PARAMS <- [	//'srid'::'32648', // optinal
									'host'::'localhost',
									'dbtype'::'postgis',
									'database'::'GAMA_POSTGIS',
									'port'::'5432',
									'user'::'postgres',
									'passwd'::'123456'];
	
	string QUERY <- "SELECT nature, ST_AsEWKB(geom) as geom FROM buildings;";
	geometry shape <- envelope(BOUNDS);		  	
		  	
	init {
//		write "This model will work only if the corresponding database is installed";
		create DB_accessor {
			create buildings from: (self select [params:: PARAMS, select:: QUERY]) 
							 with:[ nature::"nature", shape::"geom"];
		 }
		 write "Building: "+length(buildings) ;
	}
}

species DB_accessor skills: [SQLSKILL];

species buildings {
	string nature;
	aspect default {
		draw shape color: #gray ;
	}	
}	

experiment DB2agentPOSTGIS type: gui {
	output {
		display fullView type:opengl{
			species buildings aspect: default;
		}
	}
}
