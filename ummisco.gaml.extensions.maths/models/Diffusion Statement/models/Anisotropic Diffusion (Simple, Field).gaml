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
	int size <- 100; // better to have a pow of 2 for the size of the grid
	field cells <- field(size, size, 0.0);
	int rnd_component -> rnd(8) - 4;
	
	
	init {
		loop i from: 0 to: size - 1 {
			loop j from: 0 to: size - 1 {
				cells[i,j] <- j*size+i;
			}
		} 
	}

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
		loop i from: -10 to: 10 step: 5 {
			cells[size / 2 - i + rnd_component, size / 2 + i + rnd_component] <- 15;
		}
		
	}
}

experiment diffusion type: gui autorun: true {
	output synchronized: true {
		layout #split;
		display "Brewer" type: 3d  background: #black antialias:true  {
			camera 'default' location: {-36.7763,-33.4928,81.1831} target: {30.905,35.6694,0.0};
			light #default intensity: 60;
			mesh cells scale: 5 grayscale: true color:(brewer_colors("Set3")) triangulation: true;
		}

		display "HSB Smoothed 2" type: 3d background: #black {
			camera 'default' location: {50.7757,142.7832,27.2522} target: {50.3509,7.5626,0.0};
			mesh cells scale: 5 color: cells collect hsb(float(each)/5,1,1) triangulation: true smooth: true;
		}
		display "One Color Smoothed 4 with Lines" type: 3d background: #black {
			camera 'default' location: {50.7757,142.7832,27.2522} target: {50.3509,7.5626,0.0};
			mesh cells scale: 5 color:  palette([#lightblue, #blue, #blue, #darkblue]) triangulation: true border: #white smooth: 4;
		}
		

		display "Scale" type: 3d background: #black {
			camera 'default' location: {48.6197,99.6662,110.4741} target: {50.6666,17.0904,0.0};
			mesh cells scale:5 color: scale([#red::1, #yellow::2, #green::3, #blue::6])  ;
		}
		
		display "Simple gradient" type: 3d background: #white antialias:true { 
			camera 'default' location: {50.7757,142.7832,27.2522} target: {50.3509,7.5626,0.0};
			mesh cells scale:0 color: palette([#lightblue, #blue, #blue, #darkblue]) ;
			
		}
	}

}
