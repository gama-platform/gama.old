 model clusters


global {
	int number_of_agents parameter: 'true' min: 1 max: 5000 <- 1000;
	int width_and_height_of_environment parameter: 'true' min: 10 max: 2000 <- 400 ;
	float range_of_agents  parameter: 'true' min: 1.0 max: 10.0 <- 4.0;
	float speed_of_agents  parameter: 'true' min: 0.1 max: 10.0 <- 4.0;
	bool grow_leader  parameter: 'true' <- true ;
	bool torus  parameter: 'true' <- true ;
	bool multiple_agents_per_place  parameter: 'true' <- false ;
	init { 
		create cells number: number_of_agents ;
	}
	reflex change_heading when: every(rnd(30)) {
		ask (cells as list) where (each.leader = each) {  
			set heading <- heading + (rnd(45)) - (rnd(45)) ;
		} 
	}
}
environment width: width_and_height_of_environment height: width_and_height_of_environment torus: torus ;
entities {
	species cells skills: [moving] {
		const speed type: float <- speed_of_agents  ;
		rgb color <- [100 + rnd(155),100 + rnd(155), 100 + rnd(155)] as rgb update: !(leader != self) ? color : leader.color ;
		float size min: 1 max: 10 <- 4;
		int strength <- 0 ;
		float range  min: range_of_agents max: width_and_height_of_environment / 3 <- range_of_agents update: !(leader != self) ? range : range_of_agents ;
		cells leader <- self ;
		int heading <- rnd(359) update: leader.heading;
		reflex move { 
			do move ;
		} 
		
		reflex change_leader when: (leader != self) and (self distance_to leader > (leader.range - (leader.range / 10.0))) {
			if grow_leader {
				set range of my leader <- (range of my leader) - 0.05 ;
			} 
			set leader <- self ;
			set color <- [100 + rnd(155),100 + rnd(155), 100 + rnd(155)] as rgb;
			set range <- range_of_agents ;  
			set heading <- rnd(360) ;
		}
		reflex aggregate when: leader = self {
			let candidates type: list of: cells <- ((self neighbours_at range) of_species cells) where ((each).leader != self) ;
			if ! (empty(candidates)) {
				ask candidates {
					if grow_leader {
						set range of leader <- (range of leader) - 0.1 ;
						set my range  <- (my range) + 0.1 ;
					}
					set leader <- my leader ;
				}
			}
		}
		aspect default {
			draw shape: circle size: size color: color ;
			if leader = self {
				draw shape: circle size: range * 2.0 color: color + 30  empty: false ;
			}
		}
	}
}
output {

	


	display GraphicsGL type:opengl refresh_every : 1 {
		species cells aspect: default ;
	}
	

	monitor name: 'number of clusters' value: (cells as list) count (each.leader = each) ;
}
