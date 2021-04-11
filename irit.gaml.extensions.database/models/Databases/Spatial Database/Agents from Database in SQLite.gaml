/**
* Name:  Agents from Database in SQLite 
* Author: Benoit Gaudou
* Description:  This model creates buildings agents from the sqlite database using the result of a query
* Tags: database
  */
model DB2agentSQLite

global {
//	file buildingsShp <- file('../includes/building.shp');
//	file boundsShp <- file('../includes/bounds.shp');
//	geometry shape <- envelope(boundsShp);

	
	map<string,string> BOUNDS <- ["dbtype"::"sqlite",
								  'database'::'../includes/spatialite.db',
								 //'srid'::'32648',
								  "select"::"SELECT AsBinary(geom) as geom FROM bounds;"				
				  				 ];
	map<string,string> PARAMS <- ["dbtype"::"sqlite",
								  //'srid'::'32648',
								  'database'::'../includes/spatialite.db'
								  ];
	
	string QUERY <- "SELECT name, type, AsBinary(geom) as geom FROM buildings ;";
	geometry shape <- envelope(BOUNDS);		  	
	  	
	init {
		write "This model will work only if the corresponding database is installed";
		create DB_accessor {
			create buildings from: select(PARAMS, QUERY)
							 with:[ name::"name", type::"type", shape:: "geom"];
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

experiment DB2agentSQLite type: gui {
	output {
		display fullView {
			species buildings aspect: default;
		}
	}
}
