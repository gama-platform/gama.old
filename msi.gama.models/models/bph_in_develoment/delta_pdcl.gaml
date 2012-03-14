model delta_pdcl

global {
	// GIS data
	string environment_bounds <- 'gis/bounds_mekong.shp';
	file mekong_f <- shapefile('gis/MekongDelta_provinces.shp');
	file dongthap_districts_f <- shapefile('gis/dongthap_districts.shp');
	file dongthap_communes_f <- shapefile('gis/dongthap_communes.shp');
	file dongthap_landuse_f <- shapefile('gis/landuse_DT_12_03_012.shp');

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

	rgb province_color <- rgb('green') const: true;
	rgb district_color <- rgb('blue') const: true;
	rgb commune_color <- rgb('cyan') const: true;
	rgb landuse_color <- rgb('red') const: true;
	
	int bph_cloud_number <- 5000;
	rgb bph_cloud_color <- rgb('cyan') const: true;
	int bph_in_field_min_time <- 30 const: true;
	
	list possible_wind_directions <- [0, 30, 60, 90, 120, 150, 180, 210, 240, 270, 300, 330, 360] of: int; 
	int wind_direction <- one_of(possible_wind_directions) depends_on: possible_wind_directions; // 45¡
	float wind_speed <- 0.001 parameter: 'Wind speed' min: 0.0001;
	
	// workaround for visibility
	int no_infection_no <- 0 value: sum(list(province) collect (each.no_infection_lu_d_p));
	int light_infection_no <- 0 value: sum(list(province) collect (each.light_infection_lu_d_p));
	int medium_infection_no <- 0 value: sum(list(province) collect (each.medium_infection_lu_d_p));
	int heavy_infection_no <- 0 value: sum(list(province) collect (each.heavy_infection_lu_d_p));
	int hopper_burn_no <- 0 value: sum(list(province) collect (each.hopper_burn_lu_d_p));
	
	init {
		create climate;

		loop p over: (mekong_f.contents) {
			create province with: [ shape :: p ];
		}

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
		int no_infection_lu_d_p <- 0 value: sum(list(district) collect (each.no_infection_lu_d));
		int light_infection_lu_d_p <- 0 value: sum(list(district) collect (each.light_infection_lu_d));
		int medium_infection_lu_d_p <- 0 value: sum(list(district) collect (each.medium_infection_lu_d));
		int heavy_infection_lu_d_p <- 0 value: sum(list(district) collect (each.heavy_infection_lu_d));
		int hopper_burn_lu_d_p <- 0 value: sum(list(district) collect (each.hopper_burn_lu_d));
		
		init {
			let province_code type: int value: shape get ('P_NUM');
			
			loop d over: (dongthap_districts_f.contents) {
				let province_district_code type: int value: geometry(d) get ('P_NUM');
				if (province_code = province_district_code) {
					create district with: [ shape :: d ];
				}
			}
			
			create PDecisionMaker with: [ managed_province :: self ];
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

		species district {
			int no_infection_lu_d <- 0 value: sum(list(commune) collect (each.no_infection_lu));
			int light_infection_lu_d <- 0 value: sum(list(commune) collect (each.light_infection_lu));
			int medium_infection_lu_d <- 0 value: sum(list(commune) collect (each.medium_infection_lu));
			int heavy_infection_lu_d <- 0 value: sum(list(commune) collect (each.heavy_infection_lu));
			int hopper_burn_lu_d <- 0 value: sum(list(commune) collect (each.hopper_burn_lu));
			
			init {
				let d_num type: int value: shape get ('D_NUM');

				loop c over: (dongthap_communes_f.contents) {
					let district_code type: int value: (geometry(c) get ('ID_3'));
					if (district_code = d_num) {
						create commune with: [ shape :: c ];
					}
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

			species commune {
				int no_infection_lu <- 0 value: length( (list(landunit)) where (each.infection_status = 0) );
				int light_infection_lu <- 0 value: length( (list(landunit)) where (each.infection_status = 1) );
				int medium_infection_lu <- 0 value: length( (list(landunit)) where (each.infection_status = 2) );
				int heavy_infection_lu <- 0 value: length( (list(landunit)) where (each.infection_status = 3) );
				int hopper_burn_lu <- 0 value: length( (list(landunit)) where (each.infection_status = 4) );
				
				init {
					let c_num type: int value: shape get ('ID_4');

					loop lu over: (dongthap_landuse_f.contents) {
						let commune_code type: int value: geometry(lu) get ('ID_4');
						if (commune_code = c_num) {
							create landunit with: [ shape :: lu ];
						}	
					}
				}
				
				action bphs_land_on_c {
					arg a_bph_cloud type: bph_cloud;
					
					let target_lu type: landunit value: one_of(list(landunit));
					if (target_lu != nil) {
						ask target_lu {
							do bphs_land_on_lu {
								arg a_bph_cloud value: a_bph_cloud;
							}
						}
					}
				}
				
				species landunit {
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

					action bphs_land_on_lu {
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
		geometry shape <- circle(0.0005);
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
	
	species PDecisionMaker schedules: (every(30) ? (list(PDecisionMaker)) : [] ) {
		province managed_province;
		
		init {
			do write {
				arg message value: (string(managed_province)) + ' with managed_province: ' + (string(managed_province)) + '; members: ' + (string(managed_province.members));
			}
			
			loop d over: (managed_province.members) {
				create DDecisionMaker with: [ managed_district :: d ];
			}
		}
		
		species DDecisionMaker schedules: ( every(7) ? (list(DDecisionMaker)) : [] ) {
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
					/*
					do write {
						arg message value: name + ' with managed_commune: ' + (string(managed_commune)) + ' and lanunits: ' + (string(managed_commune.members));
					}
					*/
				}
			}
		}
	}
}

experiment default_expr type: gui {
	output {
		display province_display {
			species province aspect: base transparency: 0.5;
		}
		
		display district_display {
			species province aspect: base transparency: 0.5 {
				species district aspect: base transparency: 0.5;
			}
		}
		
		display landunit_display {
			species province aspect: base transparency: 0.5 {
				species district aspect: base transparency: 0.5 {
					species commune aspect: base transparency: 0.5 {
						species landunit aspect: base transparency: 0.5;
					}
				}
			}

			species bph_cloud transparency: 0.5 aspect: base;
		}
		
		display bph_infection_status {
			chart name: 'Infection status' type: histogram background: rgb('lightGray') {
				data no_infection_gram value: no_infection_no;
				data light_infection_gram value: light_infection_no;
				data medium_infection_gram value: medium_infection_no;
				data heavy_infection_gram value: heavy_infection_no;
				data hopper_infection_gram value: hopper_burn_no;
			}
		}
		
		monitor length_bph_cloud value: length(list(bph_cloud));
	}
}