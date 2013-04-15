model circle   

global {
	int number_of_agents parameter: 'Number of Agents' min: 1 <- 200 ;
	int radius_of_circle parameter: 'Radius of Circle' min: 10 <- 690 ;
	int repulsion_strength parameter: 'Strength of Repulsion' min: 1 <- 5 ;
	int width_and_height_of_environment parameter: 'Dimensions' min: 10 <- 2000 ; 
	int range_of_agents parameter: 'Range of Agents' min: 1 <- 25 ;
	float speed_of_agents parameter: 'Speed of Agents' min: 1.0  <- 10.0 ; 
	int size_of_agents <- 10;
	const center type: point <- {width_and_height_of_environment/2,width_and_height_of_environment/2};
    list blueCombination <- [([0,113,188]),([68,199,244]),([157,220,249]),([212,239,252])];
	init { 
		create cells number: number_of_agents { 
			set location <- {rnd(width_and_height_of_environment), rnd(width_and_height_of_environment)};
			set color <- rgb((blueCombination)[rnd(3)]);
		}

	}  
} 

environment width: width_and_height_of_environment height: width_and_height_of_environment;  
 
  
entities { 
	species cells skills: [moving] {  
		rgb color;
		const size type: float <- float(size_of_agents);
		const range type: float <- float(range_of_agents); 
		const speed type: float <- speed_of_agents;   
		int heading <- rnd(359);
		int z <- rnd(100);
		
		reflex go_to_center {
			set heading <- (((self distance_to center) > radius_of_circle) ? self towards center : (self towards center) - 180);
			do move speed: speed; 
		}
		
		reflex flee_others {
			let close type: cells <- one_of ( ( (self neighbours_at range) of_species cells) sort_by (self distance_to each) );
			if close != nil {
				set heading <- (self towards close) - 180;
				let dist <- self distance_to close;
				do move speed: dist / repulsion_strength heading: heading;
			}
		}
		
		aspect cylinder {
			draw cylinder(size,z) color:color border:color;
		}
		aspect sphere{
			draw sphere(size) color:color;
		}
	}
}
experiment cylinder type: gui {
	output {
		display Cylinder refresh_every: 1 type: opengl ambiant_light:0.4 {
			species cells aspect:cylinder;
		}
	}
}

experiment sphere type: gui {
	output {
		display Sphere refresh_every: 1 type: opengl ambiant_light:0.4 {
			species cells aspect: sphere;
		}
	}
}
