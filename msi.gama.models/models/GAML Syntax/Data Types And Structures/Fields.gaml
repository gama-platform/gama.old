/**
* Name: Fields
* Based on the internal empty template. 
* Author: kevinchapuis
* Tags: 
*/


model Fields

global  {
	field field_display <-  field(grid_file("includes/Lesponne.tif"));
	field var_field <- field(field_display - mean(field_display));
}

species declaring_field {
	
	/*
	 * Declaration of a field
	 */
	field field_from_grid <- field(matrix(cell));
	// Initialize a field from a asc simple raster file
	field field_from_asc <- field(grid_file("includes/grid.asc"));
	// initialize using a tiff raster file
	field field_from_tiff  <-  field(grid_file("includes/Lesponne.tif"));
	
	// Init from a user defined matrix
	field field_from_matrix  <- field(matrix([[1,2,3],[4,5,6],[7,8,9]]));
	//  init an empty field of a given size
	field empty_field_from_size <- field(10,10);
	// init a field for of a given value
	field full_field_from_size<- field(10,10,1.0);  
	// init a field of given size, with a given value and no data
	field full_field_from_size_with_nodata <- field (1,1,1.0,0.0);
	
	init {
		write "";
		write "== DECLARING FIELD ==";
		write "";
		write sample(field_from_grid);
		write sample(field_from_asc);
		write sample(field_from_tiff);
		write sample(field_from_matrix);
		write sample(empty_field_from_size);
		write sample(full_field_from_size);
		write sample(full_field_from_size_with_nodata);
		write "";
		
	}
	
}


species manipulating_field {
	init {
		// max-minimum value of the field
		float max_in_field <- max(field_display);
		float min_in_field <- min(field_display);
		float mean_in_field <- mean(field_display);
		// accessing bands of the field 
		field b1 <- field_display.bands[1];
		field b2 <- field_display.bands[2];
		field b3 <- field_display.bands[3];
	}
}

//Grid that will be saved in the ASC File
grid cell width: 100 height: 100 {
	float grid_value <- rnd(1.0,self distance_to world.location);
	rgb color <- rgb(255 * (1 - grid_value / 100), 0,0);
}

experiment Fields type: gui {
	user_command "Declaring field" {create declaring_field;}	
	user_command "Manipulating field" {create manipulating_field;}	
}

experiment Field_view type:gui{
	output {
		layout #split;
		display "field through mesh" type:opengl {
			mesh field_display grayscale:true scale: 0.05 refresh: false triangulation: true smooth: true;
		}
		display "rgb field through mesh" type:opengl {
			mesh field_display color:field_display.bands scale: 0.0 refresh: false;
		}
		display "rnd field with palette mesh"  type:opengl {
			mesh field_display.bands[2] color:scale([#red::100, #yellow::115, #green::101, #darkgreen::105]) scale:0.1 refresh:false;
		}
		display "var field" type:opengl  {
			mesh var_field color:(brewer_colors("RdBu")) scale:0.0;
		}
	}
}