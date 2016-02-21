/**
* Name: netcdf
* Author: Lô
* Description: 
* Tag : netcdf, Tag2, TagN
*/

model netcdf

global {
//	file nc<-netcdf_file("../includes/pres_temp_4D.nc");
	file nc<-netcdf_file("../includes/simple.nc");
	init{
		map m<- map<string, list>(nc.contents);
		write length(m["lon"]); 
//		write getNetCDFvar("pressure",[0,0,0,2]);
	}
}

experiment my_experiment type:gui {

output {
}
}