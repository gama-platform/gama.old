/**
 *  model7
 *  Author: Arnaud Grignard
 *  Description: 
 */ model model7 /* Insert your model definition here */ 
 
 global {
 	file shape_file_state <- file('../includes/building.shp');
	file shape_file_roads <- file('../includes/road.shp');
	geometry shape <- envelope(shape_file_state);
	graph roads_graph;
	
	init {
		create State from: shape_file_state{

			create city_in_state number:10{	
			 set location <- any_location_in(myself.shape);	
			 set N <- rnd(100);
			}
			create inter_city_link number: 10{
				set src <- one_of(city_in_state);
				set dest <- one_of(city_in_state);	
			}
			//connexionGraph <- generate_barabasi_albert(city_in_state,inter_city_link,10,1);
		}
		
		create inter_state_link number: 10{
				set src <- one_of(State);
				set dest <- one_of(State);	
		}	
	}

}


species City {
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
		if (pop>0){				
		draw text:" "+pop+" S="+S+" I="+I+" R="+R size:20 color:rgb('black');
		}
	} 
}

species State{
	graph connexionGraph;
	species city_in_state parent: City topology: topology((world).shape)  {
		aspect default{		
		  //write "S" + 	self.S + "I" + self.I + "R" + self.R;		
		  draw circle(N/50) color: rgb(self.S,0,0);
	    }	
	}
	
	species inter_city_link{
		city_in_state src;
		city_in_state dest;
		aspect default{
			set shape <- geometry (line ([src.location,dest.location]));
			draw shape color:rgb("yellow");
		}
	}
	
	aspect default{
		draw shape color: rgb('blue');
	}
}

species inter_state_link{
	State src;
	State dest;
		
	aspect default{
	  set shape <- geometry (line ([src.location,dest.location]));
	  draw shape color:rgb("red");
	}
	
}


experiment exp1 type: gui {
	output {
		display disp1 type:opengl {
			species State{
				species city_in_state;
				species inter_city_link;
			}
			
			species inter_state_link;
		}		
	}

}