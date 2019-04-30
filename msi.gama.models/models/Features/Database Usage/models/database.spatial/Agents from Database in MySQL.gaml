/**
* Name:  Agents from Database in MySQL
* Author: Benoit Gaudou
* Description:  This model does SQl query commands and create agents using the results
* Tags: database
  */

model DB2agentMySQL

global {
	map<string,string> BOUNDS <- [	//'srid'::'32648', // optional
									'host'::'localhost',
									'dbtype'::'MySQL',
									'database'::'spatial_DB_GAMA',
									'port'::'8889',
									'user'::'root',
									'passwd'::'root',
								  	"select"::"SELECT geom FROM bounds;" ];
	map<string,string> PARAMS <- [	//'srid'::'32648', // optional
									'host'::'localhost',
									'dbtype'::'MySQL',
									'database'::'spatial_DB_GAMA',
									'port'::'8889',
									'user'::'root',
									'passwd'::'root'];
	
	string QUERY <- "SELECT name, type, geom FROM buildings ;";
	geometry shape <- envelope(BOUNDS);		  	
	 	
	init {
		write "This model will work only if the corresponding database is installed and contains proper data." color: #red;
		write "To this purpose, the following models can run first: ";
		write "     - \"Create Spatial Table in MySQL.gaml\" to create the database,";
		write "     - \"Agents to Database in MySQL.gaml\" to insert data in the database.";
		write "";
		
		create DB_accessor {
			create buildings from: select(PARAMS,QUERY) 
							 with:[ type::"type", shape:: "geom"];
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
