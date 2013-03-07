
model hydro_steadymodelv3

import "river.gaml"
import "section.gaml"
import "section3D.gaml"
import "agentCreator.gaml" 

global {	
	// Shapefiles 
	file riverFile <- file('../includes/mythanh_river.shp');
	file sectionFile <- file('../includes/section_mt.shp');

	// Storage results
	string fileName <- 'results.csv';
	string fileNameVertical <- 'resVertical.csv';
	bool saveAll;

	// Water-related parameters
	float Q0 <- 20.0;
	float H0;
	int end_step;	
	list<float> water_levels;	 
	
	init {
		create river from: riverFile;

		create section3D from: sectionFile with: [section_name::read('NODE_LABEL'), section_type::read('NODE_TYPE')];
		ask section3D where (each.section_type != 'RIVER_SECTION'){
				do die;
			}
		create db number: 1  
		{ 
			do connect with: [params::SQLITE];	
			do load_global;
			end_step <- self load_step();
			water_levels <- self load_water_levels();
			do init_section3D;
			do close;
		}
		loop i from: 0 to: length(section3D)-2 step: 1 {
			ask (section3D at i) {
				next_section <- section3D at (i+1);
			}
		}
		
		ask section3D {
			do create_river_channel;
			do updatepoints;
		}
		ask section3D {
			do updategeom;
		}
	}
	
	reflex computeRiver when: cycle<end_step {
		write '*******************************************************';
		write "At cycle="+ cycle;
		H0 <- water_levels at cycle;
		
		ask last(section3D){
			if (H0<(ptsOfSection with_min_of each.y).y)
			{
				write("back to min "+H0+" -> "+(ptsOfSection with_min_of each.y).y);
				set H0<-(ptsOfSection with_min_of each.y).y;
			}
			if (H0>(ptsOfSection with_max_of each.y).y)
			{
				write("back to max "+H0+" -> "+(ptsOfSection with_max_of each.y).y);
				set H0<-(ptsOfSection with_max_of each.y).y;
			}
		}
		write "H0  -------- " + H0;
		let tmp_n type:int <- length(section3D)-1;

		ask reverse(section3D) {
			if (self = last(section3D)){ // Computation for the downstream
				write "========= section3D last " + self;
				H <- H0;
				Q <- Q0;
				do compute_A;
				do compute_R;
				do compute_s;
				write section_name + " -  A: "+ A +" - H: "+H + " - R:  " + R + " -s: " + s;
			}else{
				write "========== section3D " + self;
				Q <- Q0;
				do compute_H;
				do compute_A;
				do compute_R;
				do compute_s;					
				write section_name + " -  A: "+ A +" - H: "+H + " - R:  " + R + " -s: " + s;					
			}
		}		

		ask section3D {
			do updatepoints;
		}
		ask section3D {
			do updategeom;
		}
	}
		
	reflex saveInFile when: saveAll {
		if(cycle = 0){
			save ([""]+(section3D collect (each.name))) type: "csv" to: fileName rewrite: true; 
		}
		list rowLine <- [cycle]+ reverse(section3D collect each.H);
		save rowLine type: csv to: fileName;
	}	
	
	reflex saveResVertical when: saveAll {
		if(cycle = 0){
			save ["","H"] type: "csv" to: fileNameVertical rewrite: true; 
		}		
		ask(reverse(section3D)){
			list rowLine <- [cycle]+ self.H;
			save rowLine type: csv to: fileNameVertical;		
		}
	}
		
	reflex stop when: (cycle>=end_step){
			do halt;
	}
}

environment bounds: riverFile; 

entities {}
	
experiment hydro_steadymodelv3 type: gui {
	parameter 'Save results in a file ' var: saveAll <- false;
	output {
		display morpho refresh_every: 1 type:opengl{
			species section3D aspect: aspect2D;
			species river;
		}
		display morpho3D refresh_every: 1 type:opengl{
			species section3D aspect: default3D;
			species river;
		}		
	}
}
