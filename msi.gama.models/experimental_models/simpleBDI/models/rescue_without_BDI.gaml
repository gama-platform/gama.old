/**
 *  new
 *  Author: Arno
 *  Description: 
 */

model FireRescue

/* Insert your model definition here */

global {
	file grid_file <- file("../includes/DEM-Vulcano/vulcano_50.asc");
	file dem parameter: 'DEM' <- file('../includes/DEM-Vulcano/DEM.png');
	file texture parameter: 'Texture' <- file('../includes/DEM-Vulcano/Texture.png');
	geometry shape <- envelope(grid_file);
	
	init {
		create helicopter number: 10 {
			waterValue <-0.0;
			isTakingWater <-true;
			float z <- (cell(location)).grid_value;
			location <- { location.x, location.y, z };
		}
		
		create fireArea number:3{
			ask one_of(cell){
			  myself.location <- { self.location.x, self.location.y, float((cell(self.location)).grid_value) };
			  myself.firedCell <- (self neighbours_at (2)); 
		    }
		    
		}
		create waterArea number:2{
			ask one_of(cell){
			  myself.location <- { self.location.x, self.location.y, float((cell(self.location)).grid_value) };
			  myself.waterCell <- (self neighbours_at (2)); 
			}

		}
	}
}

grid cell file: grid_file {
	init {
		grid_value <- grid_value * 0.5;
	}
}

species helicopter skills: [moving] {
	rgb color;
	float waterValue;
	waterArea currentWaterArea;
	fireArea currentFireArea;
	bool isTakingWater;

	
	reflex goToWaterArea when: (isTakingWater){

		do goto target:waterArea closest_to(self);
		float z <- (cell(location)).grid_value;
		location <- { location.x, location.y, z };
		
		if(location = (waterArea closest_to(self)).location){
			waterValue <- waterValue+1;
		}
		if(waterValue =255){
			isTakingWater <-false;
		}
	}
	
	reflex goToSaveTheWorld when: (!isTakingWater){
		write "save the world";
		do goto target:fireArea closest_to(self);
		float z <- (cell(location)).grid_value;
		location <- { location.x, location.y, z };
		
		if(location = (fireArea closest_to(self)).location){
			write "lache ta flotte" + waterValue;
			waterValue <- waterValue-1;
		}
		if(waterValue = 0){
			isTakingWater <-true;
		}
	}

	aspect base {
		draw teapot(0.5) at:{location.x,location.y,location.z+0.25}color: rgb(0,0,waterValue);	
	}
}

species fireArea{
	list<cell> firedCell;
	init{
		
	}
	aspect base {
		ask firedCell{
			draw square(1) at:{self.location.x,self.location.y,grid_value+1}color: #red;
		}	
	}
}

species waterArea{
	list<cell> waterCell;
	
	reflex burn{
		
	}

	aspect base {
		ask waterCell{
			draw square(1) at:{self.location.x,self.location.y,grid_value+1}color: #blue;
		}	
	}
}

experiment DEM type: gui {

	output {		//Display the textured grid in 3D with the cell altitude corresponding to its grid_value.				
		display gridTextured type: opengl ambient_light: 25 diffuse_light:100  { 
			grid cell texture: dem text: false triangulation: true elevation: true;
			species helicopter aspect: base;
			species fireArea aspect:base;
			species waterArea aspect:base;
		}
	}

}