/**
 *  model7
 *  Author: hqnghi
 *  Description: 
 */ model model7 /* Insert your model definition here */ global {
	file shape_file_bounds <- file('../includes/bounds.shp');
	file shape_file_buildings <- file('../includes/building.shp');
	file shape_file_roads <- file('../includes/road.shp');
	geometry shape <- envelope(shape_file_bounds);
	graph roads_graph;
	init {
		create Buildings from: shape_file_buildings with: [type:: string(read('NATURE')), company::string(read('COMPANY'))] {
			if type = 'Industrial' {
				my_color <- rgb('blue');
			} else if type = 'Residential' {
				my_color <- rgb('red');
			}

		}

		create Roads from: shape_file_roads;
		roads_graph <- as_edge_graph(Roads as list);
		create Workers number: 100 {
			do wanna_go_to_work;
			do moving;
		}

		create Stats;
	}

}

entities {
	species Buildings {
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
                
    	solve SIR method: "rk4" step: h { }
    	
    	aspect asp1 {
			draw shape color: my_color;
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
		int transportation <- rnd(10); // moving speed depend on type of transportation
 		int threshold_time <- rnd(2000);
		int flag_time <- 0;
		aspect asp1 {
			draw circle(5) color: my_target.my_color;
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
				my_target.pop<-my_target.pop+50;
				my_target.S<-my_target.pop-my_target.I;
			}

		}

		reflex day_life {
			do moving;
			if (flag_time > 0 and cycle - flag_time >= threshold_time) {
				flag_time <- 0;
				if(my_target!=nil){
					my_target.pop<-my_target.pop-50;					
					my_target.S<-my_target.pop-my_target.I;
				}
				
				threshold_time <- rnd(2000);
				if (my_target.type = 'Industrial') {
					do wanna_go_home;
				} else {
					do wanna_go_to_work;
				}

			}

		}

	}

	species Stats{
		float meanS;
		float meanI;
		float meanR;
		
		reflex update_Statistic{
			meanS<-mean(Buildings collect each.S);
			meanI<-mean(Buildings collect each.I);
			meanR<-mean(Buildings collect each.R);
		}
	}
}

experiment exp1 type: gui {
	output {
//		display disp1 refresh_every: 1 {
//			species Buildings aspect: asp1;
//			species Roads aspect: normal;
//			species Workers aspect: asp1;
//		}
		
		display 'Statistic' {
			chart "StatisticChart" type: series  {
				data 'S' value: first(Stats).meanS color: rgb('green');							
				data 'I' value: first(Stats).meanI color: rgb('red');
				data 'R' value: first(Stats).meanR color: rgb('yellow');
			}
			
		}
		
	}

}