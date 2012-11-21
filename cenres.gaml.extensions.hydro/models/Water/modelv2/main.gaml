/**
 *  new
 *  Author: Truong Minh Thai
 *  Description: 
 */

model hydro_steadymodel04

import "river.gaml"
import "section.gaml"
import "section3D.gaml"
import "agentCreator.gaml"

global {	
	// Shapefiles 
	file riverFile <- file('../includes/mythanh_river.shp');
	file sectionFile <- file('../includes/section_mt.shp');

	// Environment 
	int width <-3000;
	int height <- 1000; 	
	
	float cycleH0variation<-0.5;

	// Water-related parameters
	float H0 <- -4.31;
	float Q0 <-20.0;
	
	float maxPossibleArea<-0.0;
	
	init {
		create river from: riverFile;
		create section3D from: sectionFile with: [section_name::read('NODE_LABEL')];
		
		create species: db number: 1  
		{ 
			do action: connect with: [params::SQLITE];	
			do load_global;
			do create_section;
			do close;
		}
				
		loop i from: 0 to: length(section3Ds)-2 step: 1 {
			ask (section3Ds at i) {
				set next_section <- list(section3Ds) at (i+1);
			}
		}
		ask(section3Ds){
			do create_river_channel;
			do updatepoints;
		}

		ask section3Ds {
			do updategeom;
		}
	}
	
	reflex computeRiver when: cycle<nstep {
		write "At cycle="+ cycle;
		set H0 <- float(first(list(water_levels at cycle)));

		ask (section3Ds at (length(names)-1)){
			if (H0<(self.ptsOfSection with_min_of each.y).y)
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
		let tmp_n type:int <- length(names)-1;

		loop i from:0  to:tmp_n {
			ask (list(section3Ds) at (tmp_n - i)){
				if (i = 0){ // Computation for the downstream
				write "i = 0";
					set H <-H0;
					set Q <- Q0;
					do compute_A;
					write "A";
					do compute_R;
					write "R";
					do compute_s;
					write section_name + " -  A: "+ A +" - H: "+H + " - R:  " + R + " -s: " + s;
				}else{
					write "i " + i;
					set Q <- Q0;
					do compute_H;
					do compute_A;
					do compute_R;
					do compute_s;					
					write section_name + " -  A: "+ A +" - H: "+H + " - R:  " + R + " -s: " + s;					
				}
			}		
		}
//		let sect type:section3D<-one_of(list(section));
//		let lp type:list <-sect.plist0;
//		let maxh type:float <- (lp with_max_of each.y).y;
//		set maxPossibleArea <-polyline(sect.plist0) water_area_for maxh;
//		
//		loop sec over : ( list(section3D) sort_by each . location . y ) {
//			set maxh  <- (sec.plist0 with_max_of each.y).y;
//			set sec.areamax<-polyline(sec.plist0) water_area_for maxh;
//			set maxPossibleArea <-min([maxPossibleArea,polyline(sec.plist0) water_area_for maxh]);
//		}	

		ask(section3Ds){
			do updatepoints;
		}
		ask(section3Ds){
			do updategeom;
		}
	}
		
	reflex stop {
		if  (cycle>=nstep){
			do halt;
		}
	}
}

environment bounds: riverFile; // width: width height: height;

entities {

}

experiment hydro_steadymodel03 type: gui {
	output {
		display morpho refresh_every: 1 type:opengl{
			species section3D aspect: aspect2D;
			species river;
		}
		display morpho3D refresh_every: 1 type:opengl{
			species section3D aspect: default3D;
			species river;
		}		
//		display displayMaree {
//			chart chartMaree type: histogram {
//				ask list(section){
//						data d value: self.H;			
//				}
//			}
//		}
	}
}
