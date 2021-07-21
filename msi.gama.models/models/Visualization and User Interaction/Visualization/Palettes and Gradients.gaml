/**
* Name: PalettesandGradients
* Based on the internal empty template. 
* Author: drogoul
* Tags: 
*/


model PalettesandGradients

global {
	
	grid_file volcano <- grid_file("includes/vulcano_50.asc");
	field cells <- field(volcano);
	geometry shape <- square(200);
	init {
		write max(cells);
		write min(cells);
	}
}


experiment Palettes type: gui {
	output {
		layout #split;
		display "Brewer" type: opengl synchronized: true {
			mesh cells  color:(brewer_colors("Set3")) triangulation: true smooth: true;
		}


		display "One Color" type: opengl  {
			mesh cells  color: #green triangulation: true border: #yellow smooth: true;
		}
		
		
		display "Scale" type: opengl  {
			mesh cells  color: scale([#red::1, #yellow::2, #green::3, #blue::6]) triangulation: true smooth: true;
		}
		

		display "Texture " type: opengl { 
			mesh cells texture: file("includes/Texture.jpg") triangulation: true border: #black smooth: true;
			
		}
		display "Simple gradient" type: opengl { 
			mesh cells color: palette([#darkgreen, #darkgreen, #green, #green, #sienna, #sienna, #white]) triangulation: true border: #black ;
			
		}
	}

}