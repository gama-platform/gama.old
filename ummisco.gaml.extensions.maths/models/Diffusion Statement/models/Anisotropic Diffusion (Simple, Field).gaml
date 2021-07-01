/**
* Name: Anisotropic diffusion (Simple)
* Author: Benoit Gaudou
* Description: This model is used to show how to construct an anisotropic diffusion through a grid. The cell at the center of the grid emit a pheromon at each step, which is spread 
*     through the grid thanks to the diffusion mechanism, using a particular matrix of diffusion.
* Tags: diffusion, matrix, math, elevation
*/
model anisotropic_diffusion

global {
	geometry shape <- rectangle(100,100);
	int size <- 128; // better to have a pow of 2 for the size of the grid
	field cells <- field(size, size, 0.0);
			int rnd_component -> rnd(8) - 4;

	// Declare the anisotropic matrix (diffuse to the left-upper direction)
	matrix<float> mat_diff <- matrix([
		[4 / 9, 2.5 / 9, 0 / 9], 
		[2.5 / 9, 0 / 9, 0 / 9], 
		[0 / 9, 0 / 9, 0.1 / 9]
	]);

	reflex diff {
		diffuse "phero" on: cells matrix: mat_diff;
	}

	reflex new_Value {
int i <- 0;
		//loop i from: -10 to: 10 step: 5 {
			cells[size / 2 - i + rnd_component, size / 2 + i + rnd_component] <- 15;
		//}
		
	}
}

experiment diffusion type: gui {
	output {
		layout #split;
		display "Brewer" type: opengl  background: #black antialias:true  {
			mesh cells scale: 3 grayscale: true color:(brewer_colors("Set3")) triangulation: true;
		}

		display "HSB" type: opengl background: #black {
			mesh cells scale: 3 color: cells collect hsb(float(each)/5,1,1) triangulation: true;
		}
		display "One Color" type: opengl background: #black {
			mesh cells scale: 3 color: #white triangulation: true border: #yellow;
		}
		
		
		display "Scale" type: opengl background: #black {
			mesh cells scale:3 color: scale([#red::1, #yellow::2, #green::3, #blue::6]) triangulation: true ;
		}
		
		display "Simple gradient" type: opengl background: #black antialias:true { 
			mesh cells scale:3 color: palette([#lightblue, #blue, #blue, #darkblue]) triangulation: true ;
			
		}
	}

}
