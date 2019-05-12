/**
 *  Author: Tri Nguyen-Huu
 *  Description: growing tree using L-systems.
 */

model l_tree_and_seasons


global { 
	float rigidity;
	int max_level <- 8;
	float min_energy <- 300.0;
	float main_split_angle_alpha <- 30.0;
	float secondary_split_angle_alpha <- 90.0;
	float main_split_angle_beta <- 20.0;
	float secondary_split_angle_beta <- 90.0;
	float length_max <- 100.0;
	float width_ini <- 1.0;
	float level_step <- 0.8;
	float energy_var <- 0.0;
	seasons season;
	
	
	float width <- shape.width;
	float height <- shape.height;

	
	float env_size <- 0.5*length_max/(1-level_step);
	point seed_pos <- {width/2,height/2};

	
	init {
		
		create trunk number:1{
			base <- seed_pos;	
			alpha <- rnd(100)*360/100;
			beta <- 90.0;
			level <- 1.0;
			parent <- nil;
		}
		
		create seasons number:1 {
			season <- self;
			location <- seed_pos;
		}
 	}
 }
 

	species seasons {
		int season_duration <- 600;
		int shift_cycle -> {season_duration * 4 + int(cycle - floor(season_duration/2))};
		list<string> season_list <- ["winter","spring","summer","autumn"];
		string current_season -> {season_list[(cycle div season_duration) mod 4]};
		int current_day -> {cycle mod season_duration};
		int shift_current_day -> {shift_cycle mod season_duration};
		int se -> {(shift_cycle div season_duration) mod 4};	
		int next_se -> {(se +1) mod 4};
		int ns_se -> {(cycle div season_duration) mod 4};	
		int ns_next_se -> {(ns_se +1) mod 4};
		
		
	 	list<rgb> sky_color_list <- [rgb(238,238,238),rgb(129,207,224),rgb(25,181,254),rgb(254,224,144)];
		list<rgb> leaf_color_list <- [rgb(150,40,27),rgb(134,174,83),rgb(30,130,76),rgb(192,57,43)];
		list<rgb> ground_color_list <- [rgb(236,240,241),rgb(46,204,113),rgb(38,166,91),rgb(95,104,40)];
		list<rgb> branch_color_list <- [#sienna,#sienna,#sienna, #sienna];
		list<rgb> fruit_color_list <- [rgb(102,62,81),rgb(200,247,197),rgb(135,211,124),rgb(211,84,0)];
		

		rgb sky_color <- sky_color_list[0];
		rgb leaf_color <- leaf_color_list[0];
		rgb ground_color <- ground_color_list[0];
		rgb branch_color <- branch_color_list[0];
		rgb fruit_color <- fruit_color_list[0];
	

		map<string,float> energy_map <- ["winter"::0.0,"spring"::0.3,"summer"::0.08,"autumn"::0];
	
		
		init{
			do change_color;
		}
		

		action change_color{	
			leaf_color <- blend(leaf_color_list[se],leaf_color_list[next_se], 1-shift_current_day/season_duration);
			sky_color <- blend(sky_color_list[se],sky_color_list[next_se], 1-shift_current_day/season_duration);
			ground_color <- blend(ground_color_list[se],ground_color_list[next_se], 1-shift_current_day/season_duration);
			branch_color <- blend(branch_color_list[se],branch_color_list[next_se], 1-shift_current_day/season_duration);
			float tmp <- 500.0;
			fruit_color <- blend(fruit_color_list[ns_se],fruit_color_list[ns_next_se], exp(-current_day/tmp));
		}
		
		reflex update{
			do change_color;
		}
		
		
		reflex change_season when: (current_day = 0){
			write current_season;
			energy_var <- energy_map[current_season];
		}
		
		aspect default { 
			draw polygon([location+{env_size/2,0,-1},location+{0.5*env_size/2,0.86*env_size/2,-1},location+{-0.5*env_size/2,0.86*env_size/2,-1},location+{-env_size/2,0,-1},location+{-0.5*env_size/2,-0.86*env_size/2,-1},location+{0.5*env_size/2,-0.86*env_size/2,-1}]) color: ground_color border: ground_color;	
			draw cone3D(6,6) at: location color: rgb(108,122,137);
			draw polygon([location+{0,0,3},location+{env_size/6,0,0},location+{0.5*env_size/6,0.86*env_size/6,0}]) color: rgb(135,121,78) border: rgb(135,121,78);
			draw polygon([location+{0,0,3},location+{0.5*env_size/6,0.86*env_size/6,0},location+{-0.5*env_size/6,0.86*env_size/6,0}]) color: rgb(115,101,58) border: rgb(115,101,58);
			draw polygon([location+{0,0,3},location+{-0.5*env_size/6,0.86*env_size/6,0},location+{-env_size/6,0,0}]) color: rgb(115,101,58) border: rgb(115,101,58);
			draw polygon([location+{0,0,3},location+{-env_size/6,0,0},location+{-0.5*env_size/6,-0.86*env_size/6,0}]) color: rgb(135,121,78) border: rgb(135,121,78);
			draw polygon([location+{0,0,3},location+{-0.5*env_size/6,-0.86*env_size/6,0},location+{0.5*env_size/6,-0.86*env_size/6,0}]) color: rgb(135,121,78) border: rgb(135,121,78);
			draw polygon([location+{0,0,3},location+{0.5*env_size/6,-0.86*env_size/6,0},location+{env_size/6,0,0}]) color: rgb(135,121,78) border: rgb(135,121,78);		
		}

	}
	
	species tree_part{
		tree_part parent <- nil;
		point base <- {0,0,0};
		point end <- {0,0,0};
		float alpha <- 0.0;
		float beta <- 0.0;	
		float level <- 1.0;
		list childs <- nil;
		float energy <- 0.0;
	}
	
	species burgeon parent: tree_part{

		
		reflex growth{
			energy <- energy +energy_var;
		}
			
		reflex bloom when: flip(energy/1){
				branch tmp <- nil;
				create branch number: 1{
					tmp <- self;
					self.level <- myself.level;
					self.base <- myself.base;
					self.alpha <- myself.alpha;
					self.beta <- myself.beta;
					self.parent <- myself.parent;
					if myself.parent != nil{
						myself.parent.childs <- myself.parent.childs + tmp;		
					}
				}
				create leaf{
					self.level <- myself.level;
					self.parent <- tmp;			
					self.alpha <- myself.alpha;
					self.beta <- myself.beta;
					self.base <- tmp.end;
					tmp.childs <- tmp.childs + self;
				}
				
				
			do die;
		}
		
		aspect default{
//			draw sphere(3) color: rgb(231,76,60) at: base;
		}
	}
	
	species trunk parent: tree_part{
		float length <- 0.0;
		float width <- 0.0;
		bool can_split <- true;


		
		aspect default { 
			draw line([base,end], width) color: rgb(90,139,140);
			draw line([base,end], width) color: season.branch_color;
		}
		
		reflex growth {
			if (parent != nil){
				base <- parent.end;
			}
//			loop tmp over: childs{
//				tmp.base <- end;
//			}
			energy <- energy + energy_var;
			float level_correction <- 1.8*0.3^level;
			length <- level_correction * (length_max * (1 - min([1,exp(- energy/1000)])));
			width <- length/10/level_correction/1.3;
			end <- base+{length*cos(beta)*cos(alpha),length*cos(beta)*sin(alpha),length*sin(beta)};
		}
		
		reflex split when: can_split and (level < max_level) and (min_energy < energy){

			float branch1_alpha <-  rnd(100)/100 * 360;
			float branch1_beta <- 30 + rnd(100)/100 * 40;
			float branch2_alpha <-  rnd(100)/100 * 360;
			float branch2_beta <- 30 + rnd(100)/100 * 40;
			can_split <- false;

			create burgeon number: 1{
					self.level <- myself.level + 1.9;
					self.base <- myself.end;
					self.alpha <- branch1_alpha;
					self.beta <- branch1_beta;
					self.parent <- myself;
			}
			
			if flip(0.7){
			create burgeon number: 1{
						self.level <- myself.level + 2.1;
						self.base <- myself.end;
						self.alpha <- branch2_alpha;
						self.beta <- branch2_beta;
						self.parent <- myself;
				}			
			}
			
			
			create trunk number: 1{
					self.level <- myself.level + 0.3;
					self.base <- myself.end;
					self.alpha <- myself.alpha -10 + rnd(200)/10;
					self.beta <- myself.beta-10 + rnd(200)/10;
					self.parent <- myself;
			}
			

			

	
		}
		
		
		

	}
	
	
	
	species branch parent: tree_part{	
		float length <- 0.0;			
		float width <- 0.0;
		bool can_split <- true;


		reflex growth {
			if (parent != nil){
				base <- parent.end;
			}
			energy <- energy + energy_var;
			length <- level_step^level * (length_max * (1 - min([1,exp(- energy/1000)])));
			width <- length/10*(4+max_level - level)/(4+max_level);	
			end <- base+{length*cos(beta)*cos(alpha),length*cos(beta)*sin(alpha),length*sin(beta)};
		}
		
		aspect default { 
			draw line([base,end], width) color: season.branch_color;
			if (season.current_season = "winter") and (abs(beta) < 50) {
				draw line([base+{0,0,1.2*width},end+{0,0,1.2*width}], width*sin(180*season.current_day/season.season_duration)) color: °white;
			}
		}
	}
	
	
	
	species leaf{
		float level <- 1.0;
		branch parent;
		point base;
		point end;
		float alpha <-0.0;
		float beta <- 0.0;
		float fall <- 0.0;
		int fall_shift <- int(rnd(season.season_duration/2.5));
		float size <- 3.0;
		
		aspect default { 
			draw line([base,end],min([parent.width,1]))  color: season.leaf_color;
			draw circle(size) at: (end - {0,0,fall*end.z}) color: season.leaf_color border: season.leaf_color;
		}
		
		reflex update{
			base <- parent.end;
			end <- base+{5*cos(beta)*cos(alpha),5*cos(beta)*sin(alpha),5*sin(beta)};
			if (season.current_season = "autumn"){
				fall <- 1 - exp(-max([0,5*(season.current_day - fall_shift)/season.season_duration*3]));
			}
			if (season.current_season = "winter"){
				size <- 3*(season.season_duration - season.current_day)/season.season_duration;
			}
			if (season.current_season = "spring"){
				fall <- 0.0;
				size <- 3* season.current_day / season.season_duration;
			}
		}
		
		reflex split when: (level < max_level) and flip(1-exp(level*(min_energy-parent.energy)/50)){
			int side1 <- -1+2*rnd(1);
			int side2 <- -1+2*rnd(1);
			int side3 <- -1+2*rnd(1);
			int side4 <- -1+2*rnd(1);
			float branch1_alpha <- parent.alpha + side1 * rnd(100)/100 * main_split_angle_alpha;
			float branch2_alpha <- parent.alpha -side1 * rnd(100)/100 * secondary_split_angle_alpha;
			float branch3_alpha <- parent.alpha +side3 * rnd(100)/100 * secondary_split_angle_alpha;
			float branch4_alpha <- parent.alpha -side3 * rnd(100)/100 * secondary_split_angle_alpha;

			
			int sideb <- -1+2*rnd(1);
			float branch1_beta <- parent.beta + sideb * rnd(100)/100 * main_split_angle_beta;
			float branch2_beta <- -20 + rnd(100)/100 * secondary_split_angle_beta;
			float branch3_beta <- -20 + rnd(100)/100 * secondary_split_angle_beta;
			float branch4_beta <- -20 + rnd(100)/100 * secondary_split_angle_beta;

			create burgeon number: 1{
				self.level <- myself.parent.level + 1;
				self.base <- myself.base;
				self.alpha <- branch1_alpha;
				self.beta <- branch1_beta;
				self.parent <- myself.parent;
			}

			
			create burgeon number: 1{
				self.level <- myself.parent.level + 1.2;
				self.base <- myself.base;
				self.alpha <- branch2_alpha;
				self.beta <- branch2_beta;
				self.parent <- myself.parent;
			}
			
			if flip(0.6){
				create burgeon number: 1{
					self.level <- myself.parent.level + 1.7;
					self.base <- myself.base;
					self.alpha <- branch3_alpha;
					self.beta <- branch3_beta;
					self.parent <- myself.parent;
				}
			}
			
			if flip(0.3){
				create burgeon number: 1{
					self.level <- myself.parent.level + 2;
					self.base <- myself.base;
					self.alpha <- branch4_alpha;
					self.beta <- branch4_beta;
					self.parent <- myself.parent;
				}
			}
			
			if flip(0.8){
				create burgeon number: 1{
					self.level <- myself.parent.level + 3.5;
					self.base <- myself.base;
					self.alpha <- branch4_alpha;
					self.beta <- branch4_beta;
					self.parent <- myself.parent;
				}
			}
			
			if flip(0.9){
				create fruit number: (1+rnd(2)){
					self.base <- myself.base;
					self.parent <- myself.parent;
					self.alpha <- myself.alpha+(-1+2*rnd(1))*30;
					self.beta <- -40.0 + rnd(80);			
				}
			}
			
			
		self.parent.childs <- self.parent.childs - self;
		do die;
		}
		
		
	}
	
	
	
 	
	species fruit{
		branch parent;
		point base;
		point end;
		float alpha;
		float beta;
		float fall <- 0.0;
		int fall_shift <- int(rnd(season.season_duration/2.5));

		
		aspect default { 
			if (season.current_season = "spring"){
				draw line([base,end],0.1) color: season.leaf_color;
				draw circle(1*sin(180*season.current_day/season.season_duration)) at: end color: °pink border: °pink;
			}
			if (season.current_season = "summer"){
				draw line([base,end],0.1) color: season.leaf_color;
				draw sphere(1*sin(90*season.current_day/season.season_duration)) at: end color: season.fruit_color border: season.fruit_color;
			}				

		}
		
		reflex update{
			base <- parent.end;
			if (season.current_season = "spring"){
				end <- base+{3*cos(beta)*cos(alpha),3*cos(beta)*sin(alpha),3*sin(beta)};
			}
			if (season.current_season = "summer"){
				float beta2 <- -90 + (beta+90)*exp(-season.current_day/100);
				end <- base+{3*cos(beta2)*cos(alpha),3*cos(beta2)*sin(alpha),3*sin(beta2)};
			}
			if (season.current_season = "autumn"){
				fall <- 1 - exp(-max([0,5*(season.current_day - fall_shift)/season.season_duration*3]));
			}
		}
		
		
		
	}
	



experiment random_tree type: gui { 
 	output { 
		display 'Arbre' type:opengl background: season.sky_color draw_env: false toolbar: false
		camera_pos: {-66.4803,275.5971,235.9631} camera_look_pos: {68.2571,22.1928,56.6337} camera_up_vector: {0.2488,0.4679,0.8481}{
	        species branch aspect: default;
	        species leaf aspect: default;
	        species burgeon aspect: default;
	        species trunk aspect: default;
	        species seasons aspect: default;
	        species fruit aspect: default;
	    }
	    
		
	}
}	

	


experiment L_Tri type: gui { 
	float seed <- 0.5084902259417993;
 	output { 
		display 'Arbre' type:opengl background: season.sky_color draw_env: false toolbar: false
		camera_pos: {-66.4803,275.5971,235.9631} camera_look_pos: {68.2571,22.1928,56.6337} camera_up_vector: {0.2488,0.4679,0.8481}{
	        species branch aspect: default;
	        species leaf aspect: default;
	        species burgeon aspect: default;
	        species trunk aspect: default;
	        species seasons aspect: default;
	        species fruit aspect: default;
	    }
	    
		
	}
}
