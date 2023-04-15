/**
* Name: Gama 1.9
* Author:  Arnaud Grignard - Tri Nguyen-Huu
* Description: A toy model animating the GAMA logo
* Tags:  
*/

model GAMA  

global {

	file Gama_shape_file <- shape_file("../includes/GamaVectorized.shp");
	string mode <- "Light to dark" among: ["Light to dark", "Dark to light", "Light", "Dark"];
	bool inner_rings <- false;
	point origin <- {490,490,0};
	float color_speed <- 2.0;

	//definition of the geometry of the world agent (environment) as the envelope of the shapefile
	geometry shape <- envelope(Gama_shape_file);
	
	// auxiliary sigmoid function for smooth transitions
	float sigmoid(int t, int mid_course, float speed){
		return 1/(1+exp(-speed*(t-mid_course)));
	}
	
	// time-depedent color change
	rgb changeColor(int t){
		int mid_course <- 400;
		float lambda <- 0.04;
		float sig;
		switch mode{
			match "Light to dark"{
				sig <- 1 - sigmoid(cycle,mid_course,lambda);
			}			
			match "Dark to light"{
				sig <- sigmoid(cycle,mid_course,lambda);
			}
			match "Light"{
				sig <- 1.0;
			}
			match "Dark"{
				sig <- 0.0;
			}
		}
		return rgb(255*sig,255*sig,255*sig);
	}

	init { 

		Gama_shape_file <- shape_file("../includes/GamaVectorized.shp");
		
		create object from:Gama_shape_file with:[type::string(get("type")), name::string(get("name")),level::int(get("level"))]{
		    origin <- myself.origin;
		    color<-#white;
		    if (name = "gamablue"){
		    	color<-#gamablue;
		    	depth <- 0.0001;
		    }
		    if (name = "gamared"){
		    	color<-#gamared;
		    	depth <- 0.0001;
		    }
		    if (name = "gamaorange"){
		    	color<-#gamaorange;
		    	depth <- 0.0001;
		    }
		    if (name = "donut1"){
		    	color<-rgb(#gamablue,25);
		    }
		    if (name = "donut3"){
		    	color<-rgb(#gamared,25);
		    }
		    if (name = "donut5"){
		    	color<-rgb(#gamaorange,25);
		    }
		    if (name = "circle"){
		    	color<-rgb(#gamared,70);
		    	color<-#gamared;
		    	rotation_speed <- -2 * rotation_speed;
		    	axe <- {1,-1,0};
		    }
		     if (name = "1.9"){
			 	rotation_speed <- 0.0; 
		    	depth <- 1.0;
		    }
		    location<-location  - {0,0,depth/2};
		}

		ask object{
			 if (name = "1.9"){
		    	origin <- first(object where (each.name="circle")).location;
		    }
			switch level {
				match 5 {
					axe <- {1,0,0};
				}
				match 4 {
					axe <- {0,1,0};
				}
				match 3 {
					axe <- {1,0,0};
				}
				match 2 {
					axe <- {0,1,0};
					rotation_speed <- 3 * rotation_speed;
				}
				match 1 {
					axe <- {1,0,0};
					rotation_speed <- -2* rotation_speed;
				}
			}
			shift <- location - origin;
			if level =0 {
				do die;
			}
		}
		
		
		loop i over: remove_duplicates(object collect each.level){
			ask first(object where (each.level = i)){
				linked_objects <- object where (each.level = i-1);
			}
		}
	}  
	
	reflex end_animation when: cycle  = 650{
		do pause;
	}
} 

// definition of the parts of the animated 3d model
species object skills:[moving]{
	rgb color;
	string type;
	string name;
	point axe <- {0,1,0};
	float rotation_speed <- 1.0;
	int level;
	list<object> linked_objects <- [];
	float depth <- 0.0;
	point origin;
	point shift;		

// apply rotation to linked inner objects
	action propagate_rotation(float angle,point ax, point centre){
		origin <- centre + (( origin - centre) rotated_by (angle::ax));
		shape <- shape rotated_by (angle,ax);
	    shift <-  shift rotated_by (angle::ax);
	    axe <-  axe rotated_by (angle::ax);
	   	ask linked_objects{
	    	do propagate_rotation(angle, ax, centre);
	    }
	}

// rotate the object and linked inner objects
	reflex rotate{
		if cycle > 293 {
			rotation_speed <- rotation_speed / 1.015;
		}
		if cycle = 600{
			rotation_speed <- 0.0;
		}
		shape <- shape rotated_by (rotation_speed,axe);
	    shift <-  shift rotated_by (rotation_speed::axe);
	    ask linked_objects{
	    	do propagate_rotation(myself.rotation_speed, myself.axe, myself.origin);
	    }
	}
	
	aspect obj {
		if name = "donut2" or name = "donut4"{
			if inner_rings{
				if mode = "Dark"{
					color <- rgb(20,20,20);
				}else{
					color <- world.changeColor(cycle);				
				}
			}else{
				color <- rgb(#white,0);
			}
		}
		// change color for specific objects
		switch name{
			match "1.9" {
		 		if cycle > 500{
		    		color <- blend(#white,#gamared,(cycle-500)/150);
		    	} else {
		    		color <- rgb(#white,0);
		    	}
		    } 
		   match "circle"{
		    	color <- rgb(#gamared,255*world.sigmoid(cycle, 380, 0.04));
		    }
		}
		draw shape depth: depth color:color at: origin +shift;	    	
	}
			
}	

experiment "Run me !"   type: gui autorun:true{
	float minimum_cycle_duration<-0.025#sec;
	parameter 'Mode' var: mode   category: "Preferences";
	parameter 'Inner rings' var: inner_rings   category: "Preferences";
	output {
		display "1.9"  background: world.changeColor(cycle) type: 3d axes:false autosave:false fullscreen:false toolbar:false{
		  species object aspect:obj;			
		}
	}
}
