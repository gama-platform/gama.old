/**
 *  CreateBuildingTableMySQL
 *  Author: thaitruongminh
 *  Description: 
 */

model CreateBuildingTable_MSSQL


global {
			map<string,string> PARAMS <- ['host'::'127.0.0.1','dbtype'::'sqlserver','database'::'','port'::'1433','user'::'sa','passwd'::'tmt'];
	init {
		create dummy;
		ask dummy {
			if (self testConnection[ params::PARAMS]){
				
 			    do executeUpdate    params:PARAMS updateComm: "CREATE DATABASE spatial_DB"; 
 			    write "spatial_BD database was created ";
 			    remove key: "database" from: PARAMS;
				put "spatial_DB" key:"database" in: PARAMS;
				do executeUpdate params: PARAMS 
				  updateComm : "CREATE TABLE bounds"+
				  "( "  +
                    " geom GEOMETRY " + 
                  ")";
				write "bounds table was created ";
				do executeUpdate params: PARAMS 
				  updateComm : "CREATE TABLE buildings "+
				  "( "  +
                   	" name VARCHAR(255), " + 
                    " type VARCHAR(255), " + 
                    " geom GEOMETRY " + 
                  ")";
                write "buildings table was created ";
 			}else {
 				write "Connection to MySQL can not be established ";
 			}	
		}
	}
}
entities {
	species dummy skills: [ SQLSKILL ] {

	}
}      
experiment default_expr type: gui {

}