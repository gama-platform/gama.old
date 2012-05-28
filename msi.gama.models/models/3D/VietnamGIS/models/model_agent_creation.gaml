model tutorial_gis_city_traffic

global {
	file shape_file_province <- '../includes/SIG_Vietnam_plus/VNM_adm2_historical_plus.shp' parameter: 'Shapefile for the provinces:' category: 'GIS' ;
		
	init {
		create province from : shape_file_province with: [provinceName :: read("VARNAME_2")]{	
		}		
	}
	
}

entities {

	species province{
		rgb color <- rgb('green');
		string name;
	
		aspect default{
			draw shape: geometry color:color;
			}
	}
	

	}

environment bounds: shape_file_province ;
output {
	display city_display type:opengl   {
		species province aspect: default;
	}
}
