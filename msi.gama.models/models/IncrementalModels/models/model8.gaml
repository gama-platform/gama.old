/**
 *  model8
 *  Author: hqnghi
 *  Description: 
 */
model model8



global {
	file shape_file_buildings <- file('../includes/building.shp');
	file shape_file_roads <- file('../includes/road.shp');
	file shape_file_bounds <- file('../includes/bounds.shp');
	int model_light<-255;
	rgb model_color<-rgb(model_light,model_light,model_light);
//	geometry shape <- envelope(shape_file_bounds);
	int initial_S <- 900;//495;
  int initial_I <- 5;
  int initial_R <- 0; 
	
	float global_beta <- 0.1 ;	
	float global_delta <- 0.01 ;
	
	graph roads_graph;
	init {
		
		create Buildings from: shape_file_buildings with: [type:: string(read('NATURE')), company::string(read('COMPANY'))] {
			if type = 'Industrial' {
				my_color <- rgb('blue');
				height <-20+rnd(100);
			} else if type = 'Residential' {
				my_color <- rgb('red');
				height <-20+rnd(50);
			}
			
		}

		create Roads from: shape_file_roads;
		roads_graph <- as_edge_graph(Roads as list);
		create Workers number: round(initial_S) {
			is_susceptible <- true;
	        is_infected <-  false;
	        is_immune <-  false; 
	        my_color <-  rgb('green');
			do wanna_go_to_work;
			do moving;
		}
		
		create Workers number: round(initial_I) {
			is_susceptible <- false;
	        is_infected <-  true;
	        is_immune <-  false; 
	        my_color <-  rgb('red');
			do wanna_go_to_work;
			do moving;
		}
		
		create Workers number: round(initial_R) {
			is_susceptible <- false;
	        is_infected <-  false;
	        is_immune <-  true; 
	        my_color <-  rgb('yellow');
			do wanna_go_to_work;
			do moving;
		}
		create Stats;
	}
	int flag<-1;
	reflex day_night{
		model_light<-model_light+flag*1;
		if(model_light< 0 or model_light>255){
			flag<--flag;
		}
		model_color<-rgb(model_light,model_light,model_light);
	}
}
environment bounds:shape_file_bounds{
	grid env_base width:1 height:1{
		aspect asp1{
			draw shape color:model_color;
		}
	}
}
entities {
	species Buildings {
		int height;
		string type;
		string company;
		rgb my_color;
		int pop;
		int N <- pop;
    	float t;    
		float I <- 1.0; 
		float S <- pop - I; 
		float R <- 0.0; 
   		float h<-0.1;
   		float beta<-0.1;
   		float delta<-0.01; 
   			
		equation SIR{ 
			diff(S,t) = (- beta * S * I / pop);
			diff(I,t) = (beta * S * I / pop) - (delta * I);
			diff(R,t) = (delta * I);
		}
        reflex ss when:(S>0 and I>0){
        	
    	solve SIR method: "rk4" step: h { }
        }        
    	
    	aspect asp1 {
			draw shape color: my_color depth: height ;
//			if (pop>0){				
//				draw text:" "+pop+" S="+S+" I="+I+" R="+R size:20 color:rgb('black');
//			}
		} 
	}

	species Roads {
		aspect normal {
			draw shape color: rgb('black');
		}

	}

	species Workers skills: [moving] use_individual_shapes:true use_regular_agents:true {
		Buildings my_target;
		bool is_susceptible <- true;
		bool is_infected <- false;
   bool is_immune <- false;
   rgb my_color;
		int transportation <- rnd(10); // moving speed depend on type of transportation
 		int threshold_time <- rnd(100);
		int flag_time <- 0;
		aspect asp1 {
			draw circle(5) color: my_color depth:transportation;
		}

		action wanna_go_to_work {
			my_target <- any(Buildings where (each.type = 'Industrial'));
		}

		action wanna_go_home {
			my_target <- any(Buildings where (each.type = 'Residential'));
		}

		action moving {
			do goto target: my_target on: roads_graph speed: 5 + transportation;
			if (location = my_target.location and flag_time = 0) {
				flag_time <- cycle;
				my_target.pop<-my_target.pop+1;
					if(is_susceptible)				
					{						
						my_target.S<-my_target.S+1;
					}
					if(is_infected)				
					{						
						my_target.I<-my_target.I+1;
					}
					if(is_immune)				
					{						
						my_target.R<-my_target.R+1;
					}
					
			}

		}

		reflex day_life {
			do moving;
			if (flag_time > 0 and cycle - flag_time >= threshold_time) {
				flag_time <- 0;
				if(my_target!=nil){
					my_target.pop<-my_target.pop-1;	
					if(is_susceptible)				
					{						
						my_target.S<-my_target.S-1;
					}
					if(is_infected)				
					{						
						my_target.I<-my_target.I-1;
					}
					if(is_immune)				
					{						
						my_target.R<-my_target.R-1;
					}
					
					
				}
				
				threshold_time <- rnd(100);
				if (my_target.type = 'Industrial') {
					do wanna_go_home;
				} else {
					do wanna_go_to_work;
				}

			}

		}



		reflex become_infected when: (is_susceptible and flag_time=0) {
//        		if (flip(1 - (1 - beta)  ^ (((myPlace.neighbours + myPlace) collect (each.agents)) of_species Host as list) count (each.is_infected))) {
//				write ""+agents_overlapping(self) of_species Workers where (each.is_infected);
	        	if (flip(1 - (1 - global_beta)  ^ ((agents_at_distance (1)) of_species Workers) count (each.is_infected) * 100)) {
	        		is_susceptible <-  false;
	            	is_infected <-  true;
	            	is_immune <-  false;
	            	my_color <-  rgb('red');       	
	        }
        }
        
        reflex infecte_others when: (is_infected and flag_time=0) { // flag_time =0 : peoples are not in building (not in working time and relaxing time) 
//          		loop hst over: ((myPlace.neighbours + myPlace) collect (each.agents)) of_species Host{
          			loop hst over: ( agents_at_distance (1)) of_species Workers{
        			if (Workers(hst).is_susceptible){
        				if(flip(global_beta)){
			 	        	hst.is_susceptible <-  false;
				            hst.is_infected <-  true;
				            hst.is_immune <-  false;
				            hst.my_color <-  rgb('red');     		
        				}    				
        			}
        		}
        }
        
        reflex become_immune when: (is_infected and flip(global_delta) and flag_time=0) {
        	is_susceptible <- false;
        	is_infected <- false;
            is_immune <- true;
            my_color <- rgb('yellow');
        }

	}

	species Stats{
		float meanS;
		float meanI;
		float meanR;
		
		float globalS;
		float globalI;
		float globalR;
		
		reflex update_Statistic{
			meanS<-mean(Buildings collect each.S);
			meanI<-mean(Buildings collect each.I);
			meanR<-mean(Buildings collect each.R);
			
			globalS<-(Workers as list) count (each.is_susceptible) as float;
			globalI<-(Workers as list) count (each.is_infected) as float;
			globalR<-(Workers as list) count (each.is_immune) as float;
		}
	}
}


experiment exp1 type: gui {

	output {
		display city_display refresh_every: 1 type:opengl ambient_light:model_light{
			species env_base aspect:asp1;
			species Buildings  aspect:asp1 ;
			species Roads aspect: normal;
			species Workers aspect: asp1 ;
		}
display 'Statistic' {
			chart "StatisticChart" type: series  position:{0,0} size:{1,0.5}{
				data 'mean S of all buildings' value: first(Stats).meanS color: rgb('green');							
				data 'mean I of all buildings ' value: first(Stats).meanI color: rgb('red');
				data 'mean R of all buildings' value: first(Stats).meanR color: rgb('yellow');
			}
			chart "StatisticChart" type: series position:{0,0.5} size:{1,0.5} {
				data 'S on roads' value: first(Stats).globalS color: rgb('green');							
				data 'I on roads' value: first(Stats).globalI color: rgb('red');
				data 'R on roads' value: first(Stats).globalR color: rgb('yellow');
			}
			
		}
	}
}




