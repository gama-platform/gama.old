model tutorial_gis_city_traffic

/* This model is not working with the svn version of Gama as I don't have the permission to commit the shape file */

global {
	file shape_file_river <- file(project_path + 'DEM/includes/Rhone/affluent_bdcarthage.shp') parameter: 'Shapefile for the rivers:' category: 'GIS' ;

    file shape_file_chenal <- file(project_path + 'DEM/includes/Rhone/chenal.shp') parameter: 'Shapefile for the rivers:' category: 'GIS' ;
    
    file shape_file_building <- file(project_path + 'DEM/includes/BAT/BAT.shp') parameter: 'Shapefile for the rivers:' category: 'GIS' ;
    
    
	graph the_graph;
	
	init {
		create river from: shape_file_river  {        
	  	}
	  	create chenal from: shape_file_chenal  {        
	  	}   	
	  	create building from: shape_file_building  {        
	  	}  
	}
	

}
entities {
	species river {
		string type; 
		rgb color <- rgb('gray')  ; 
		aspect base {
			draw shape: geometry color: color ; 
		}
	}
	
	species chenal {
		string type; 
		rgb color <- rgb('blue')  ; 
		aspect base {
			draw shape: geometry color: color ; 
		}
	}
	
	species building {
		string type; 
		rgb color <- rgb('green')  ; 
		aspect base {
			draw shape: geometry color: color ; 
		}
	}


}
environment bounds: shape_file_river ;

experiment display type: gui {
	
	output {
	display city_display type:opengl   refresh_every: 1 {
		species river aspect:base ;
		species chenal aspect:base ;
		species building aspect:base ;
	}
}
}






