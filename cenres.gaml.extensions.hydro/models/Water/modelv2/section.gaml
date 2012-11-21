/**
 *  section
 *  Author: bgaudou
 *  Description: 
 */

model section

global {
	float n <- 0.02;
	
	list sections of: section function: {list(section)};
}

species section {
	string section_name;
	int section_id;
	
	list ptsOfSection of:point;
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
		let d type: float <- (self.location distance_to next_section.location);
		// write "d " + d + " s " + next_section.s + " H " + next_section.H;
		// let heigth type: float <- d * sin(next_section.s * 180 / (PI * 3600));
		let heig type: float <- d * next_section.s;
		// write "height " + heig;
		set H <- heig + next_section.H; 
	}
	
	action compute_A {
		set A <- polyline(ptsOfSection) water_area_for H;
	}

	action compute_R {
		// Hypothesis !
		let section_width type: float <- (ptsOfSection max_of each.x) - (ptsOfSection min_of each.x);
		write "section width " + section_width;
		set R <- A / section_width;
	}
	
	action compute_s {
		// From n = 1/Q * A* R^(2/3) * S ^(1/2)
		write " Q et A " + Q + "  --  " + A;
		set s <- (n * Q / (A * R ^(2/3)))^2;
	}
		
	aspect sectio {
		draw geometry: shape;
	}
}
	
