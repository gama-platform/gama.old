model clusters

global torus: torus{
	int number_of_agents min: 1 max: 2000 <- 100;
	int width_and_height_of_environment min: 10 max: 2000 <- 400 ;
	geometry shape <- square(width_and_height_of_environment);
	float range_of_agents min: 1.0 max: 10.0 <- 10.0;
	float speed_of_agents min: 0.1 max: 10.0 <- 4.0;
	bool grow_leader <- true ;
	bool torus <- true ;
	init {  
		create cells number: number_of_agents ;
	}
	reflex change_heading when: every(rnd(30)) { 
		ask cells where (each.leader = each) {  
			heading <- heading + (rnd(45)) - (rnd(45)) ;  
		} 
	}
	
}
entities {
	species cells skills: [moving] {
		const speed type: float <- speed_of_agents  ;
		rgb color <- rgb(100 + rnd(155),100 + rnd(155), 100 + rnd(155)) update: !(leader != self) ? color : leader.color ;
		float size min: 1.0 max: 10.0 <- 4.0;
		int strength <- 0 ;
		float range  min: range_of_agents max: width_and_height_of_environment / 3 <- range_of_agents update: (leader = self) ? range : range_of_agents ;
		cells leader <- self ;
		int heading <- rnd(359) update: leader.heading;
		reflex move {    
			do move ;
		}  
		reflex change_leader when: (leader != self) and (self distance_to leader > (leader.range - (leader.range / 10.0))) {
			if grow_leader {
				leader.range  <- (range of my leader) - 0.05 ;
			} 
			leader <- self ;
			range <- range_of_agents ;  
			heading <- rnd(360) ;
		}
		reflex aggregate when: leader = self {
			list<cells> candidates <- (cells at_distance range) where ((each).leader != self) ;
			if ! (empty(candidates)) {
				ask candidates {
					if grow_leader {
						range of leader <- (range of leader) - 0.1 ;
						range  <- (my range) + 0.1 ;
					}
					leader <- my leader ;
				}
			}
		}
		aspect default {
			draw circle(size) color: color ;
			if leader = self {
				draw circle(range * 2.0) color: color  empty: false ;
			}
		}
	}
}


experiment EZE type: gui {
	parameter 'Number of agents :' var: number_of_agents;
	parameter 'Width and height of the environment' var:  width_and_height_of_environment;
	parameter 'Range of agents'var: range_of_agents;
	parameter 'Speed of agents'var: speed_of_agents;
	parameter 'Grow leader?' var: grow_leader;
	parameter 'Torus?'var: torus;
	output {
		display Graphics refresh_every: 1 {
			species cells aspect: default ;
		}
		monitor name: 'number of clusters' value: cells count (each.leader = each) ;
	}
}
