/**
* Name: DEMGenerator
* Based on Damien Philippon's initial work, a model that generates maps with several features (which can be interpreted at will: sea, shore, mountain...). 
* Author: Alexis Drogoul
* Tags: 
*/
model RandomMaps

global {
	// the generation is mostly governed by these values (see the documentation of 'generate_terrain')
	float details <- 0.5;
	float smoothness <- 0.4;
	float scattering <- 0.7;
	int width <- 250;
	int height <- 250;
	field terrain;
	string palette_name <- "Seaside";
	int generator_seed <- 1;

	init {
		do generate();
	}

	action generate {
		// the generate_terrain operator returns a field where the elevations are between 0 and 1. It can be scaled afterwards.
		int scale_factor <- palette_name = "Seaside" ? 20 : 10;
		terrain <- generate_terrain(generator_seed, width, height, details, smoothness, scattering) * scale_factor;
	}

}

experiment Terrain type: gui {
	
	parameter "Seed for the generator" var: generator_seed <- 1 {
		do update();
	}
	
	parameter "Width of the environment" var: width min: 50 max: 2000 {
		do update();
	}
	parameter "Height of the environment" var: height min: 50 max: 2000 {
		do update();
	}
	
	parameter "Level of details" var: details min: 0.0 max: 1.0 {
		do update();
	}

	parameter "Level of smoothness" var: smoothness min: 0.0 max: 1.0 {
		do update();
	}

	parameter "Level of scattering" var: scattering min: 0.0 max: 1.0 {
		do update();
	}
	
	parameter "Palette" var: palette_name  among: ["Countryside","Seaside"] {
		do update();
	}
	
	user_command "Save" {
		do save_tif();
	}

	action update {
		ask simulation {
			do generate();
		}
		do update_outputs();
	}
	
	action save_tif {
		string file_name <- "seed"+generator_seed+"w"+width+"h"+height+"d"+(details with_precision 2)+"smooth"+(smoothness with_precision 2)+"scatter"+(scattering with_precision 2)+".tif";
		save grid_file(file_name, terrain);
	}

	action _init_ {
		
	// A trick to make sure the parameters are expanded and visible when the simulation is launched.
		bool previous <- gama.pref_experiment_expand_params;
		gama.pref_experiment_expand_params <- true;
		create simulation;
		gama.pref_experiment_expand_params <- previous;
	}

	list<rgb> land_and_sea <- palette(reverse([#darkgreen, #darkgreen, #green, rgb(32, 176, 0), rgb(224, 224, 0), rgb(128, 128, 255), #blue, #blue]));
	list<rgb> field_and_forest <- palette(reverse([#sienna, #olive, #darkgreen, #green, #forestgreen,  #lightgreen]));
	output {
		layout #split consoles: false toolbars: false;
		display "Terrain" type: 3d axes: false camera: #from_up_front {
			mesh terrain color: palette_name = "Seaside" ? land_and_sea : field_and_forest triangulation: true;
		}

	}

}