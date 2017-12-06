/**
* Name:  Agents to Database in PostGIS
* Author: Truong Minh Thai, Quang Truong, Thu  Tran
* Description: 
* savetosql: Save data of agent into MySQL.
* Geometry  column in PostGIS is in Multipolygon3D
* * transform= true because you need to transform geometry data from Absolute(GAMA) to Gis
* 
*  NOTE: Geodatabase and tables are created before. Then use the this model to insert data from shapefile
* Geometry  column in PostGIS is in Multipolygon3D 
* Tags: database
*/

model agent2DB_POSTGIS 
  
global { 
	file districtShp <- file('../../includes/district.shp') ;
	geometry shape <- envelope(districtShp);
	 
	map<string,string> PARAMS <-  ['srid'::'32846', // 32648 represents for the WGS 84 - Zone 48 Northern.
								   'host'::'localhost','dbtype'::'postgres','database'::'GAMA_POSTGIS',
								   'port'::'5432','user'::'postgres','passwd'::'123456'];

	init {
		write "This model will work only if the corresponding database is installed";
		create district from: districtShp with: [ward_name::string(read ('Ward_name'))];
		write "Click on <<Step>> button to save data of agents to DB";
		
		create DB_Accessor
		{ 			
			do executeUpdate params: PARAMS updateComm: "DELETE FROM district";	
		}
	}
}   
  
species DB_Accessor skills: [SQLSKILL] ;   

species district {
	string ward_name;
	
	reflex printdata{
		write " name : " + (name) ;
	}
	reflex savetosql{  // save data into Postgres
		write "begin"+ name;
	    ask (DB_Accessor) {
	    	// Using  ST_Force3D() for converting the geometry data to 3D in the case that the geom in table is in 3D      
			do executeUpdate params: PARAMS updateComm: "INSERT INTO district(ward_name,geom) VALUES('"+myself.ward_name+"',ST_Force3D(ST_Multi(ST_GeomFromText('" + myself.shape +"',32648))))";
		}	
	}	
	aspect default {
		draw shape color: #gray ;
	}
}   

experiment default_expr type: gui {
	output {
		
		display GlobalView {
			species district aspect: default;
		}
	}
}

