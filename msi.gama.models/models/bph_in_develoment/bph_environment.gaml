model bph_environment

global {
	point environment_bounds <- {200, 200};
	geometry delta_mekong <- ( square(180) at_location {100, 100} );
	geometry dongthap <- square(160) at_location {100, 100};
	
	geometry district1 <- rectangle ({60, 140}) at_location {60, 100};
	geometry commune1 <- rectangle ({40, 50}) at_location {60, 60};
	geometry commune2 <- rectangle ({40, 50}) at_location {60, 130};
	geometry landunit1 <- rectangle ({30, 15}) at_location {60, 50};
	geometry landunit2 <- rectangle ({30, 15}) at_location {60, 70};
	geometry landunit3 <- rectangle ({30, 15}) at_location {60, 120};
	geometry landunit4 <- rectangle ({30, 15}) at_location {60, 140};
	map district1_config <- ['shape' :: district1, 'communes' :: [ ( commune1 :: ( [ landunit1, landunit2 ] ) ), ( commune2 :: [ landunit3, landunit4 ] ) ] ];
	
	geometry district2 <- rectangle ({60, 140}) at_location {140, 100};
	geometry commune3 <- rectangle ({40, 50}) at_location {140, 60};
	geometry commune4 <- rectangle ({40, 50}) at_location {140, 130};
	geometry landunit5 <- rectangle ({30, 15}) at_location {140, 50};
	geometry landunit6 <- rectangle ({30, 15}) at_location {140, 70};
	geometry landunit7 <- rectangle ({30, 15}) at_location {140, 120};
	geometry landunit8 <- rectangle ({30, 15}) at_location {140, 140};
	map district2_config <- ['shape' :: district2, 'communes' :: [ ( commune3 :: ( [ landunit5, landunit6 ] ) ), ( commune4 :: [ landunit7, landunit8 ] ) ] ];
	
	int cloud_min_member <- 10;

	int no_infection <- 0 depends_on: [cloud_min_member];
	int light_infection <- cloud_min_member depends_on: [cloud_min_member];
	int medium_infection <- 2 * cloud_min_member depends_on: [cloud_min_member];
	int heavy_infection <- 3 * cloud_min_member depends_on: [cloud_min_member];
	int hopper_burn <- 4 * cloud_min_member depends_on: [cloud_min_member];
	
	rgb no_infection_color <- rgb('blue');
	rgb light_infection_color <- rgb('yellow');
	rgb medium_infection_color <- rgb('pink');
	rgb heavy_infection_color <- rgb('red');
	rgb hopper_burn_color <- rgb('black');

	rgb province_color <- rgb('green') const: true;
	rgb district_color <- rgb('blue') const: true;
	rgb commune_color <- rgb('cyan') const: true;
	rgb landuse_color <- no_infection_color const: true depends_on: [no_infection_color];
	
	int bph_cloud_number <- 1000;
	rgb bph_cloud_color <- rgb('cyan') const: true;
	int bph_in_field_min_time <- 30 const: true;
	
	
	list possible_wind_directions <- [0, 30, 60, 90, 120, 150, 180, 210, 240, 270, 300, 330, 360] of: int; 
	int wind_direction <- one_of(possible_wind_directions) depends_on: possible_wind_directions; // 45¡
	float wind_speed <- 0.01 parameter: 'Wind speed' min: 0.01;
	
	init {
		create climate;
		create province with: [shape :: dongthap] returns: ps;
		create PDecisionMaker with: [ managed_province :: (ps at 0)];
		
		create bph_cloud number: bph_cloud_number with: [ bph_in_cloud :: cloud_min_member ];
	}
}

environment bounds: environment_bounds;

entities {
	species climate {
		reflex shuffle_wind_direction when: ((time mod 24) = 0 ){
			set wind_direction value: one_of(possible_wind_directions);
		}
	}
	
	species province {
		
		list district_geoms of: geometry;
		
		init {
			create district with: [district_config :: district1_config];
			create district with: [district_config :: district2_config];
		}
		
		action bph_cloud_landing {
			arg a_bph_cloud type: bph_cloud;
			
			let target_district type: district value: one_of(list(district));
			ask target_district {
				do bphs_land_on_d {
					arg a_bph_cloud value: a_bph_cloud;
				}
			}			 
		}

		species district topology: (topology(world.shape)) {
			
			map district_config;
			
			init {
				set shape value: district_config at ('shape');
				
				loop c_config over: list(district_config at ('communes')) {
					create commune with: [ shape :: (pair(c_config)).key, landunit_geoms :: (pair(c_config)).value ];
				}
			}
			
			action bphs_land_on_d {
				arg a_bph_cloud type: bph_cloud;
				
				let target_commune type: commune value: one_of(list(commune));
				ask target_commune {
					do bphs_land_on_c {
						arg a_bph_cloud value: a_bph_cloud;
					}
				}
			}
			
			species commune topology: (topology(world.shape)) {
				
				list landunit_geoms of: geometry;
				
				init {
					loop l_geom over: landunit_geoms {
						create species: landunit with: [ shape :: l_geom ];
					}
				}
				
				action bphs_land_on_c {
					arg a_bph_cloud type: bph_cloud;
					
					let target_lu type: landunit value: one_of(list(landunit));
					ask target_lu {
						do bphs_land_on_lu {
							arg a_bph_cloud value: a_bph_cloud;
						}
					}
				}
				
				species landunit topology: (topology(world.shape)) {
					int active_bph <- 0 value: my_bph_group.bph_in_group;
					rgb color <- landuse_color value: (active_bph >= hopper_burn) ? hopper_burn_color : ( (active_bph >= heavy_infection) ? heavy_infection_color : ( (active_bph >= light_infection) ? light_infection_color : no_infection_color ) );
					bool is_hopper_burn function: { active_bph >= hopper_burn };
					
					/*
					list taken_of_bphs of: bph_group value: (list(bph_group)) where ( (each.current_landunit = self) and ( (time - each.landing_time) > bph_in_field_min_time )  );
					
					reflex check_bphs_taken_off when: (length(taken_of_bphs) >= cloud_min_member) {
						create bph_cloud with: [ location :: location, bph_in_cloud :: length(taken_of_bphs), last_landing_land :: self ];
						set active_bph value: active_bph - length(taken_of_bphs);
						ask taken_of_bphs { do die; }
					}
					 */
					 
					bph_group my_bph_group;
					
					init {
						create bph_group returns: bph_gs;
						set my_bph_group value: (bph_gs at 0);
					}
					
					action bphs_land_on_lu {
						arg a_bph_cloud type: bph_cloud;
						
						if ( (a_bph_cloud.shape intersects shape) and ( rnd(10) > 7 ) ) {
							set my_bph_group.bph_in_group value: my_bph_group.bph_in_group + a_bph_cloud.bph_in_cloud;
							ask a_bph_cloud { do die; }
						}
					}
					
					action is_hopper_burn type: bool {
						return (active_bph >= hopper_burn);
					}
					
					reflex {
						do write {
							arg message value: name + ' with active_bph: ' + (string(active_bph));
						}
					}
					
					aspect base {
						draw shape: geometry color: color;
					}
				}
				
				aspect base {
					draw shape: geometry color: commune_color;
				}
			}

			aspect base {
				draw shape: geometry color: district_color;
			}
		}
		
		aspect base {
			draw shape: geometry color: province_color;
		}
	}
	 

	species bph_group skills: moving {
		landunit last_landunit;
		landunit current_landunit;
		int landing_time <- time;
		
		int bph_in_group;
		
		
		// behavior???
	}
	
	species bph_cloud skills: moving {
		int heading <- wind_direction value: wind_direction;
		float speed <- wind_speed value: wind_speed;
		int amplitude <- rnd (10);
		geometry shape <- circle(0.05);
		int altitude min: 0;
		
		int bph_in_cloud;
		
		reflex travel {
			do wander {
				arg amplitude value: amplitude;
			}
		}
		
		reflex landing {
			let potential_province type: province value: one_of (province overlapping self);
			if (potential_province != nil) {
				ask potential_province {
					do bph_cloud_landing {
						arg a_bph_cloud value: myself;
					}
				}
			}
		}
		
		aspect base {
			draw shape: geometry color: bph_cloud_color;
		}
	}
	
	species PDecisionMaker {
		province managed_province;
		
		init {
			do write {
				arg message value: (string(managed_province)) + ' with managed_province: ' + (string(managed_province)) + '; members: ' + (string(managed_province.members));
			}
			
			loop d over: (managed_province.members) {
				create DDecisionMaker with: [ managed_district :: d ];
			}
		}
		
		species DDecisionMaker {
			district managed_district;
			
			init {
				do write {
					arg message value: name + ' with managed_district: ' + (string(managed_district));
				}
				
				loop c over: (managed_district.members) {
					create CDecisionMaker with: [ managed_commune :: c ];
				}
			}
			
			
			species CDecisionMaker {
				commune managed_commune;
				
				init {
					do write {
						arg message value: name + ' with managed_commune: ' + (string(managed_commune)) + ' and lanunits: ' + (string(managed_commune.members));
					}
				}
			}
		}
	}
}

experiment default_expr type: gui {
	output {
		display default_display {
			species province transparency: 0.5 aspect: base {
				species district transparency: 0.5 aspect: base {
					species commune transparency: 0.5 aspect: base {
						species landunit transparency: 0.5 aspect: base;
					}
				}
			}
			
			species bph_cloud transparency: 0.5 aspect: base;
		}
		
		monitor number_of_bph value: (list(bph_group)) collect (each.bph_in_group);
		monitor number_of_bph_cloud value: length(list(bph_cloud));
	}
}