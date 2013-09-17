/**
 *  CreateBuildingTableMySQL
 *  Author: thaitruongminh
 *  Description: 
 */

model CreateBuildingTableMySQL


global {
	map<string,string> PARAMS <-  ['host'::'localhost','dbtype'::'Postgres','database'::'','port'::'5433','user'::'postgres','passwd'::'tmt'];

	init {
		create dummy ;
		ask dummy {
			if (self testConnection[ params::PARAMS]){
				
 			    do executeUpdate    params:PARAMS updateComm: "CREATE DATABASE spatial_db with TEMPLATE = template_postgis;"; 
 			    write "spatial_BD database was created ";
 			    remove key: "database" from: PARAMS;
				put "spatial_db" key:"database" in: PARAMS;
				do executeUpdate params: PARAMS 
				  updateComm : "CREATE TABLE bounds"+
				  "( "  +
                    " geom GEOMETRY " + 
                  ")";
				write "bounds table was created ";
				do executeUpdate params: PARAMS 
				  updateComm : "CREATE TABLE buildings "+
				  "( "  +
                   	" name character varying(255), " + 
                    " type character varying(255), " + 
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