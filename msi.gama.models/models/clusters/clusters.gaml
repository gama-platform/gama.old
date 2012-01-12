model clusters

global {
	var number_of_agents type: int parameter: 'true' init: 1000 min: 1 max: 5000 ;
	var width_and_height_of_environment type: int parameter: 'true' init: 400 min: 10 max: 2000 ;
	var range_of_agents type: float parameter: 'true' init: 4.0 min: 1.0 max: 10.0 ;
	var speed_of_agents type: float parameter: 'true' init: 4.0 min: 0.1 max: 10.0 ;
	var grow_leader type: bool parameter: 'true' init: true ;
	var torus type: bool parameter: 'true' init: true ;
	var multiple_agents_per_place type: bool parameter: 'true' init: false ;
	init {
		create species: cells number: number_of_agents ;
	}
	reflex when: every(rnd(30)) {
		ask target: (cells as list) where (each.leader = each) {
			set heading value: heading + (rnd(45)) - (rnd(45)) ;
		}
	}
}
environment width: width_and_height_of_environment height: width_and_height_of_environment torus: torus ;
entities {
	species cells skills: [moving] {
		var color type: rgb init: [100 + rnd(155),100 + rnd(155), 100 + rnd(155)] as rgb value: !(leader != self) ? color : leader.color ;
		var size type: float init: 4 min: 1 max: 10 ;
		var strength type: int init: 0 ;
		var range type: float init: range_of_agents min: range_of_agents max: width_and_height_of_environment / 3 value: !(leader != self) ? range : range_of_agents ;
		const speed type: float init: speed_of_agents  ;
		var leader type: cells init: self ;
		var heading type: int init: rnd(359) value: leader.heading ;
		reflex move {
			do action: move ;
		}
		reflex when: (leader != self) and (self distance_to leader > (leader.range - (leader.range / 10.0))) {
			if condition: grow_leader {
				set range of my leader value: (range of my leader) - 0.05 ;
			}
			set leader value: self ;
			set color value: [100 + rnd(155),100 + rnd(155), 100 + rnd(155)] as rgb;
			set range value: range_of_agents ;
			set heading value: rnd(360) ;
		}
		reflex aggregate when: leader = self {
			let candidates value: ((self neighbours_at range) of_species cells) where ((each).leader != self) ;
			if condition: ! (empty(candidates)) {
				ask target:  candidates {
					if condition: grow_leader {
						set range of leader value: (range of leader) - 0.1 ;
						//set name: the range of its leader value: (the range of its leader) - 0.1 ;
						set my range  value: (my range) + 0.1 ;
					}
					set leader value: my leader ;
					//set name: its leader value: my leader ;
				}
			}
		}
		aspect default {
			draw shape: circle size: size color: color ;
			if condition: leader = self {
				draw shape: circle size: range * 2.0 color: color + 30  empty: false ;
			}
		}
	}
}
output {
	display Graphics refresh_every: 1 {
		species cells aspect: default ;
	}
	monitor name: 'number of clusters' value: (cells as list) count (each.leader = each) ;
}
