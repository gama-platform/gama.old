/**
* Name: Anisotropic diffusion (Simple)
* Author: Benoit Gaudou
* Description: This model is used to show how to construct an anisotropic diffusion through a grid. The cell at the center of the grid emit a pheromon at each step, which is spread 
*     through the grid thanks to the diffusion mechanism, using a particular matrix of diffusion.
* Tags: diffusion, matrix, math, elevation
*/
model anisotropic_diffusion

global {
	int size <- 128; // better to have a pow of 2 for the size of the grid
	field cells <- field(size, size, 0.0, 0.0);

	// Declare the anisotropic matrix (diffuse to the left-upper direction)
	matrix<float> mat_diff <- matrix([
		[4 / 9, 1.5 / 9, 1 / 9], 
		[1.5 / 9, 0 / 9, 0 / 9], 
		[1 / 9, 0 / 9, 0.1 / 9]
	]);

	reflex diff {
		diffuse "phero" on: cells matrix: mat_diff;
	}

	reflex new_Value {
		cells[size / 2 + rnd(8) - 4, size / 2 + rnd(8) - 4] <- 15;
	}
}

experiment diffusion type: gui {
	output {
		display a type: opengl synchronized: true {
			graphics text {
				draw "Max " + string(float(max(cells)) with_precision 2) + " min " + string(float(min(cells)) with_precision 2) at: location + {-20, 20, 10};
			}

			mesh cells scale: 3 color: one_of(#lightgreen, #lightblue, #lightsalmon) triangulation: true;
		}

	}

}
