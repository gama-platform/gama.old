/**
* Name:  Agents from Database in MySQL
* Author: Benoit Gaudou
* Description:  This model does SQl query commands and create agents using the results
* Tags: database
  */

model DB2agentMySQL

global {
	map<string,string> BOUNDS <- [	//'srid'::'32648', // optinal
									'host'::'localhost',
									'dbtype'::'MySQL',
									'database'::'spatial_DB',
									'port'::'3306',
									'user'::'gama_usr1',
									'passwd'::'123456',
								  	"select"::"SELECT geom FROM bounds;" ];
	map<string,string> PARAMS <- [	//'srid'::'32648', // optinal
									'host'::'localhost',
									'dbtype'::'MySQL',
									'database'::'spatial_DB',
									'port'::'3306',
									'user'::'gama_usr1',
									'passwd'::'123456'];
	
	string QUERY <- "SELECT name, type, geom FROM buildings ;";
	geometry shape <- envelope(BOUNDS);		  	
	 	
	init {
		write "This model will work only if the corresponding database is installed";
		create DB_accessor {
			create buildings from: list(self select [params:: PARAMS, select:: QUERY]) 
							 with:[ 'type'::"type", 'shape':: "geom"];
		 }
	}
}


species DB_accessor skills: [SQLSKILL];

species buildings {
	string type;
	aspect default {
		draw shape color: #gray ;
	}	
}	

experiment DB2agentMySQL type: gui {
	output {
		display fullView {
			species buildings aspect: default;
		}
	}
}
