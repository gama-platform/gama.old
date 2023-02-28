/**
* Name:  Agents from Database in PostGIS
* Author: Benoit Gaudou
* Description:  This model does SQL query commands and create agents using the results
* Tags: database
  */

model DB2agentPOSTGIS 

global {
	map<string,string> BOUNDS <- [	//'srid'::'32648', // optional
	 								'host'::'localhost',
									'dbtype'::'postgis',
									'database'::'spatial_db',
									'port'::'5432',
									'user'::'postgres',
									'passwd'::'',
								  	'select'::'SELECT ST_AsEWKB(geom) as geom FROM bounds;' ];
	map<string,string> PARAMS <- [	//'srid'::'32648', // optional
									'host'::'localhost',
									'dbtype'::'postgis',
									'database'::'spatial_db',
									'port'::'5432',
									'user'::'postgres',
									'passwd'::''];
	
	string QUERY <- "SELECT type, ST_AsEWKB(geom) as geom FROM buildings;";
	geometry shape <- envelope(BOUNDS);		  	
		  	
	init {
		write "This model will work only if the corresponding database is installed and initialized." color:#red;
		write "To this purpose, the following models can run first: ";
		write "     - \"Create Spatial Table in PostGIS.gaml\" to create the database,";		
		write "     - \"Agents to Database in PostGIS.gaml\" to insert data in the database.";
		write "";		
		
		create DB_accessor {
			create buildings from: select(PARAMS, QUERY)
							 with:[ nature::"type", shape::"geom"];
		 }
		 write "Buildings created: "+length(buildings) ;
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
		display fullView type:3d{
			species buildings aspect: default;
		}
	}
}
