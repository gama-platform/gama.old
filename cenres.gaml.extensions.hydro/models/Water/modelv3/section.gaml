
model hydro_steadymodelv3

global {
	float n <- 0.02;
	
	// list sections of: section function: {list(section)};
}

species section {
	string section_name;
	string section_type;
	
	list<point> ptsOfSection;
	float H;
	float Q;
	float A;
	float R;
	float s;	

	section next_section;
	
	reflex testS when: (s > 0.0001){
		write 'Section ' + name + ' has trespassed s = 10^-4';
	}

	action compute_H {
		float d <- (self.location distance_to next_section.location);
		float heig <- d * next_section.s;
		H <- heig + next_section.H; 
	}
	
	action compute_A {
		A <- polyline(ptsOfSection) water_area_for H;
	}

	action compute_R {
		// Hypothesis !
		float section_width <- (ptsOfSection max_of each.x) - (ptsOfSection min_of each.x);
		R <- A / section_width;
	}
	
	action compute_s {
		// From n = 1/Q * A* R^(2/3) * S ^(1/2)
		s <- (n * Q / (A * R ^(2/3)))^2;
	}
		
	aspect sectio {
		draw shape;
	}
}
	
