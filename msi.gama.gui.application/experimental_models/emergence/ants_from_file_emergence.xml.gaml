model ants_from_file_emergence
// gen by Xml2Gaml


import "includes/ants_from_file.xml.gaml"

global {
	abstract components;
	abstract groups;
	var two_ants_distance type: int init: 5 min: 1 parameter: 'Distance of two ants' category: 'Ant flock';
	var mergeFrequency type: int init: 3 min: 1 parameter: 'Evaluation frequency of merge command' category: 'Ant flock';
	var creationFrequency type: int init: 3 min: 1 parameter: 'Evaluation frequency of creation command' category: 'Ant flock';
	var updateFrequency type: int init: 2 min: 1 parameter: 'Evaluation frequency of update command' category: 'Ant flock';
	var disposalFrequency type: int init: 1 min: 1 parameter: 'Evaluation frequency of disposal command' category: 'Ant flock';
	var mergingDistance type: int init: 10 min: 1 parameter: 'Distance to merge two flocks' category: 'Ant flock';
}
entities {
	species ant_flock skills: [situated, visible] {
		var color type: rgb init: rgb [64, 64, 64];
		var cohesionIndex type: float init: two_ants_distance value: two_ants_distance + (length(components) / 4) + (float(area) / 500);
		var shape type: geometry value: polygon (components collect (each as ant).location);
		creation frequency: creationFrequency {
			let potentialAnts var: potentialAnts value: (ant as list) where ((empty (groups)) and ((each.state = 'carryingFood') or (each.state = 'followingRoad')));
			if condition: (length(potentialAnts) > 1) {
				let potentialAntsNeighboursMap var: potentialAntsNeighboursMap type: map value: [] as map;
				abstract oneAnt;
				loop over: potentialAnts var: oneAnt {
					let freeNeighbours var: freeNeighbours type: list of: ant value: ((oneAnt neighbours_at two_ants_distance) of_species ant) where (potentialAnts contains each);
					if condition: (length(freeNeighbours) > 0) {
						add item: oneAnt::freeNeighbours to: potentialAntsNeighboursMap;
					}
				}
				abstract keys;
				let sortedPotentialAnts var: sortedPotentialAnts value: keys(potentialAntsNeighboursMap) sort_by (length(list(potentialAntsNeighboursMap at each)));
				loop over: sortedPotentialAnts var: oneAnt {
					let oneAntNeighbours var: oneAntNeighbours value: potentialAntsNeighboursMap at oneAnt;
					if condition: oneAntNeighbours != nil {
						abstract oneNeighbour;
						loop over: oneAntNeighbours var: oneNeighbour {
							remove item: oneNeighbour from: potentialAntsNeighboursMap;
						}
					}
				}
				let antKeys var: antKeys value: keys(potentialAntsNeighboursMap);
				abstract oneKey;
				loop over: antKeys var: oneKey {
					put item: remove_duplicates ((list (potentialAntsNeighboursMap at oneKey)) + oneKey) at: oneKey in: potentialAntsNeighboursMap;
				}
				loop over: keys(potentialAntsNeighboursMap) var: oneKey {
					let microAgents var: microAgents type: list value: potentialAntsNeighboursMap at oneKey;
					if condition: (length(microAgents) > 1) {
						create with: [components::microAgents, color::[rnd(255), rnd(255), rnd(255)]] species: ant_flock;
					}
				}
			}
		}
		update frequency: updateFrequency {
			let removed_components var: removed_components value: components where (((each as ant) distance_to location) > cohesionIndex);
			let added_components var: added_components value: ((self neighbours_at cohesionIndex) of_species ant) where (empty (each.groups) and ((each.state = 'carryingFood') or (each.state = 'followingRoad')));
			let newComponents var: newComponents value: components union added_components;
			abstract com;
			loop over: removed_components var: com {
				remove item: com from: newComponents;
			}
			if condition: !empty(added_components) or !empty(removed_components) {
				set components value: newComponents;
			}
		}
		disposal when: (length(components) < 2) frequency: 1;
		merge frequency: mergeFrequency {
			let nearbyFlocks var: nearbyFlocks type: list of: ant_flock value: ((self neighbours_at mergingDistance) of_species ant_flock);
			if condition: !empty(nearbyFlocks) {
				add item: self to: nearbyFlocks;
				set nearbyFlocks value: nearbyFlocks sort_by (length ((each as ant_flock).components));
				let largestFlock var: largestFlock type: ant_flock value: nearbyFlocks at (length(nearbyFlocks) - 1);
				remove item: largestFlock from: nearbyFlocks;
				let added_components var: added_components value: [];
				abstract oneFlock;
				loop over: nearbyFlocks var: oneFlock {
					loop over: oneFlock.components var: com {
						add item: com to: added_components;
					}
				}
				if condition: !empty(added_components) {
					set largestFlock.components value: largestFlock.components union added_components;
					loop over: nearbyFlocks var: oneFlock {
						ask target: oneFlock {
							do action: die;
						}
					}
				}
			}
		}
		aspect default {
			draw shape: geometry color: color;
		}
	}
}
output {
	display Ants refresh_every: 1 {
		grid ant_grid transparency: 0.5;
		species ant transparency: 0.5;
	}
	display Ants_Flock refresh_every: 1 {
		species ant_flock transparency: 0.5;
	}
}
