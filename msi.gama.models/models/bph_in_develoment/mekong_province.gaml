model mekong_province

global {
	// GIS data
	string environment_bounds <- '/gis/bounds_mekong.shp';	
	string mekong_gis <- '/gis/MekongDelta_provinces.shp';
	
	int cloud_min_member <- 10;

	int no_infection <- 0 depends_on: [cloud_min_member];
	int light_infection <- cloud_min_member depends_on: [cloud_min_member];
	int medium_infection <- 2 * cloud_min_member depends_on: [cloud_min_member];
	int heavy_infection <- 3 * cloud_min_member depends_on: [cloud_min_member];
	int hopper_burn <- 4 * cloud_min_member depends_on: [cloud_min_member];
	
	rgb no_infection_color <- rgb('blue') const: true;
	rgb light_infection_color <- rgb('yellow') const: true;
	rgb medium_infection_color <- rgb('pink') const: true;
	rgb heavy_infection_color <- rgb('red') const: true;
	rgb hopper_burn_color <- rgb('black') const: true;

	rgb landuse_color <- no_infection_color const: true depends_on: [no_infection_color];
	
	int bph_cloud_number <- 50;
	rgb bph_cloud_color <- rgb('cyan') const: true;
	int bph_in_field_min_time <- 30 const: true;
	
	list possible_wind_directions <- [0, 30, 60, 90, 120, 150, 180, 210, 240, 270, 300, 330, 360] of: int; 
	int wind_direction <- one_of(possible_wind_directions) depends_on: possible_wind_directions; // 45¡
	float wind_speed <- 0.001 parameter: 'Wind speed' min: 0.0001;

	init {
		create climate;

		create province from: mekong_gis;

		create bph_cloud number: bph_cloud_number with: [ bph_in_cloud :: cloud_min_member ];
	}
}

environment bounds: environment_bounds;

entities {
	species climate {
		reflex shuffle_wind_direction when: ((time mod 10) = 0 ) {
			set wind_direction value: one_of(possible_wind_directions);
		}
	}

	species province {
		int active_bph <- 0 value: my_bph_group.bph_in_group;
		rgb color <- landuse_color value: (active_bph >= hopper_burn) ? hopper_burn_color : ( (active_bph >= heavy_infection) ? heavy_infection_color : ( (active_bph >= light_infection) ? light_infection_color : no_infection_color ) );
		bool is_hopper_burn function: { active_bph >= hopper_burn };
		int infection_status <- 0 value: ( self my_infection_status [] );
		
		bph_group my_bph_group;
		
		init {
			create bph_group returns: bph_gs;
			set my_bph_group value: (bph_gs at 0);
		}
		
		/*
		 * Returns the infection status of the landunit
		 * 0: no infection
		 * 1: light infection
		 * 2: medium infection
		 * 3: heavy infection
		 * 4: hopper burn
		 */
		action my_infection_status type: int {
			switch active_bph {
				match_one [no_infection] {
					return 0;
				}
				match_between [no_infection, light_infection] {
					return 1;
				}
				match_between [light_infection, medium_infection] {
					return 2;
				}
				match_between [medium_infection, heavy_infection] {
					return 3;
				}
				default {
					return 4;
				}
			}
		}

		action bph_cloud_landing {
			arg a_bph_cloud type: bph_cloud;
			
			set my_bph_group.bph_in_group value: (my_bph_group.bph_in_group) + (a_bph_cloud.bph_in_cloud);
			ask a_bph_cloud { do die; }
		}

		reflex bphs_take_off when: (is_hopper_burn) {
			if (rnd(10) > 5) {
				create bph_cloud with: [ location :: self.location, bph_in_cloud :: my_bph_group.bph_in_group ];
				set my_bph_group.bph_in_group value: 0;
			}
		}

		aspect base {
			draw shape: geometry color: color;
		}
	}

	species bph_cloud skills: moving {
		int heading <- wind_direction value: wind_direction;
		float speed <- wind_speed value: wind_speed;
		int amplitude <- rnd (10);
		geometry shape <- circle(0.01);
		int altitude min: 0;
		
		int bph_in_cloud;
		
		reflex travel {
			do wander {
				arg amplitude value: amplitude;
			}
		}
		
		reflex landing {
			if ( (rnd(10)) > 8.5 ) {
				let potential_province type: province value: one_of (province overlapping self);
				if (potential_province != nil) {
					ask potential_province {
						do bph_cloud_landing {
							arg a_bph_cloud value: myself;
						}
					}
				}
			}
		}
		
		aspect base {
			draw shape: geometry color: bph_cloud_color;
		}
	}

	species bph_group skills: moving {
		landunit last_landunit;
		landunit current_landunit;
		int landing_time <- time;
		
		int bph_in_group;
		
		
		// behavior???
	}
}

experiment default_expr type: gui {
	output {
		display default_expr {
			species province aspect: base transparency: 0.5;
			species bph_cloud aspect: base transparency: 0.5;
		}

		display bph_infection_status {
			chart name: 'Infection status' type: histogram background: rgb('lightGray') {
				data no_infection_gram value: length(list(province) where (each.infection_status = 0));
				data light_infection_gram value: length(list(province) where (each.infection_status = 1));
				data medium_infection_gram value: length(list(province) where (each.infection_status = 2));
				data heavy_infection_gram value: length(list(province) where (each.infection_status = 3));
				data hopper_infection_gram value: length(list(province) where (each.infection_status = 4));
			}
		}
		
		monitor length_province value: length(list(province));
	}
}