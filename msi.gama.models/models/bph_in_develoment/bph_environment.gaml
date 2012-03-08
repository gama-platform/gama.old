model bph_environment

global {
	string env_bounds_shp <- '/gis/bounds_mekong.shp';
	string mekong_provinces_shp <- '/gis/MekongDelta_provinces.shp';
	string dongthap_shp <- '/gis/DT_LU_08_region-clean.shp';
	
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
	rgb landuse_color <- no_infection_color const: true depends_on: [no_infection_color];
	
	int bph_cloud_number <- 1000;
	rgb bph_cloud_color <- rgb('cyan') const: true;
	int bph_in_field_min_time <- 30 const: true;
	
	
	list possible_wind_directions <- [0, 30, 60, 90, 120, 150, 180, 210, 240, 270, 300, 330, 360] of: int; 
	int wind_direction <- one_of(possible_wind_directions) depends_on: possible_wind_directions; // 45¡
	float wind_speed <- 0.01 parameter: 'Wind speed' min: 0.01;
	
	
	
	init {
		create climate;
		create province from: mekong_provinces_shp;
		create landunit from: dongthap_shp;
		
		create bph_cloud number: bph_cloud_number with: [ bph_in_cloud :: cloud_min_member ];
	}

/*
	reflex print_debug {
		do write {
			arg message value: name + ' with: ' + ' width: ' + (string(shape.width)) + '; height: ' + (string(shape.height));
		}
	}
	*/
}

environment bounds: env_bounds_shp;

entities {
	species climate {
		reflex shuffle_wind_direction when: ((time mod 24) = 0 ){
			set wind_direction value: one_of(possible_wind_directions);
		}
	}
	
	species province {
 
		aspect base {
			draw geometry color: province_color;
		}
	}
	 
	species landunit topology: (topology(world.shape)) {
		int active_bph <- 0;
		rgb color <- landuse_color value: (active_bph >= hopper_burn) ? hopper_burn_color : ( (active_bph >= heavy_infection) ? heavy_infection_color : ( (active_bph >= light_infection) ? light_infection_color : no_infection_color ) );
		bool is_hopper_burn function: { active_bph >= hopper_burn };
		list taken_of_bphs of: bph_group value: (list(bph_group)) where ( (each.current_landunit = self) and ( (time - each.landing_time) > bph_in_field_min_time )  );
		
		reflex check_bphs_taken_off when: (length(taken_of_bphs) >= cloud_min_member) {
			create bph_cloud with: [ location :: location, bph_in_cloud :: length(taken_of_bphs), last_landing_land :: self ];
			set active_bph value: active_bph - length(taken_of_bphs);
			ask taken_of_bphs { do die; }
		}
		
		action is_hopper_burn type: bool {
			return (active_bph >= hopper_burn);
		}
		
		aspect base {
			draw shape: geometry color: color;
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
		geometry shape <- circle(0.005);
		int altitude min: 0;
		
		landunit last_landing_land;
		
		int bph_in_cloud;
		
		reflex travel {
			do wander {
				arg amplitude value: amplitude;
			}
		}
		
		reflex landing {
			
			let potential_landunit type: landunit value: one_of ( ( (list(landunit) - last_landing_land) overlapping self) where !(each.is_hopper_burn));
			
			if (potential_landunit != nil) {
//				create bph number: bph_in_cloud with: [location :: any_location_in(potential_landunit.shape), current_landunit :: potential_landunit];
				create bph_group number: bph_in_cloud with: [location :: potential_landunit.location, current_landunit :: potential_landunit];
				set potential_landunit.active_bph value: potential_landunit.active_bph + bph_in_cloud;
				
				do die;
			}
		}
		
		aspect base {
			draw shape: geometry color: bph_cloud_color;
		}
	}
}

experiment default_expr type: gui {
	output {
		display default_display {
			species province transparency: 0.5 aspect: base {
//				species landunit transparency: 0.5 aspect: base;
			}
			species landunit transparency: 0.5 aspect: base;
			
			species bph_cloud transparency: 0.5 aspect: base;
		}
		
		monitor number_of_bph value: (list(bph_group)) collect (each.bph_in_group);
//		monitor number_of_bph_group value: length(list(bph_group));
		monitor number_of_bph_cloud value: length(list(bph_cloud));
	}
}