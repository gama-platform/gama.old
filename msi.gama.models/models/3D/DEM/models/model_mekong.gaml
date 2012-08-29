model tutorial_gis_city_traffic

global {
	file shape_file_river <- file(project_path + 'DEM/includes/Mekong_River/majortribdskratie.shp') parameter: 'Shapefile for the rivers:' category: 'GIS' ;
	file shape_file_district <- file(project_path + 'DEM/includes/Mekong_River/district.shp') parameter: 'Shapefile for the rivers:' category: 'GIS' ;
	
	init {
		create river from: shape_file_river  {        
	  	}
	  	create district from: shape_file_district  {        
	  	}   
	}
}
entities {
	species river {
		string type; 
		rgb color <- rgb('blue')  ; 
		aspect base {
			draw shape: geometry color: color ; 
		}
	}
	
	species district {
		string type; 
		rgb color <- rgb('green')  ; 
		aspect base {
			draw shape: geometry color: color ; 
		}
	}

}
environment bounds: shape_file_district ;

experiment display type: gui {
	
	output {
	display city_display type:opengl   refresh_every: 1 {
		//species river aspect:base ;
		species district aspect:base ;
		
	}
}
}






