/**
* Name:  Agents from Database in MSSQL
* Author: Benoit Gaudou, Quang Truong
* Description:  This model does SQl query commands and create agents using the results
* Tags: database
  */
model DB2agentMSSQL

global {
	map<string,string> BOUNDS <- [	"srid"::"32648", // optional
									"host"::"localhost",  // server name
									"dbtype"::"sqlserver",
									"database"::"GAMAMSSQL",
									"port"::"1433",
									"user"::"gama_usr",
									"passwd"::"123456",
								  	"select"::"SELECT geom.STAsBinary() as geom FROM bounds;" ];
	map<string,string> PARAMS <- [	"srid"::"32648", // optional
									"host"::"localhost",
									"dbtype"::"sqlserver",
									"database"::"GAMAMSSQL",
									"port"::"1433",
									"user"::"gama_usr",
									"passwd"::"123456"];
	
	string QUERY <- "SELECT nature, geom.STAsBinary() as geom FROM buildings;";
	geometry shape <- envelope(BOUNDS);		  	
	init {
		//write "This model will work only if the corresponding database is installed" color: #red;
		create DB_accessor {
			create buildings from: select(PARAMS,QUERY)
							 with:[ type::"nature", shape:: "geom"];
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

experiment DB2agentMSSQL type: gui {
	output {
		display fullView {
			species buildings aspect: default;
		}
	}
}
