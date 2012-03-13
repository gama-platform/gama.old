model bph_environment_real_data

global {
	// GIS data
	string environment_bounds <- 'gis/bounds_Asian.shp';
	string asian_gis <- 'gis/Asia.shp';
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
	
	rgb no_infection_color <- rgb('blue');
	rgb light_infection_color <- rgb('yellow');
	rgb medium_infection_color <- rgb('pink');
	rgb heavy_infection_color <- rgb('red');
	rgb hopper_burn_color <- rgb('black');

	rgb country_color <- rgb('magenta') const: true;
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
		create country from: asian_gis;
		
		loop p_gis_geom over: (mekong_f.contents) {
			create province with: [shape :: p_gis_geom];
		}
	}	
}

environment bounds: environment_bounds;

entities {
	species climate {
		reflex shuffle_wind_direction when: ((time mod 24) = 0 ){
			set wind_direction value: one_of(possible_wind_directions);
		}
	}
	
	species country {
		
		aspect base {
			draw shape: geometry color: country_color;
		}
	}
	
	species province {
		int p_num;
		string p_name;
		string p_code;
		
		init {
			set p_num value: shape get ('P_NUM');
			set p_name value: shape get ('P_NAME');
			set p_code value: shape get ('P_CODE');
			do write {
				arg message value: p_name + ' with code: ' + (string(p_code));
			}
			
			// create districts for "Dong Thap" province
			if (p_code = 'DT') {
				loop d over: (dongthap_districts_f.contents) {
					create district with: [ shape :: d ];
				}
			}
		}
		
		species district {
			string d_name;
			int d_num;
			
			init {
				set d_name value: shape get ('D_NAME');
				set d_num value: shape get('D_NUM');
				
				do write {
					arg message value: d_name + ' with code: ' + (string(d_num));
				}
				
				loop c over: (dongthap_communes_f.contents) {
					let district_code type: int value: (geometry(c) get ('ID_3'));
					if (district_code = d_num) {
						create commune with: [ shape :: c ];
					}
				}
			}
			
			species commune {
				string c_name;
				int c_num;
				
				init {
					set c_name value: shape get ('NAME_4');
					set c_num value: shape get ('ID_4');

					loop lu over: (dongthap_landuse_f.contents) {
						let commune_code type: int value: geometry(lu) get ('ID_4');
						if (commune_code = c_num) {
							create landunit with: [ shape :: lu ];
						}	
					}
				}
				
				species landunit {
					int lu_code; // TODO find out the signification of this code?
					
					init {
						set lu_code value: shape get ('LU_CODE');
					}
					
					aspect base {
						draw shape: geometry color: landuse_color;
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
}

experiment default_expr type: gui {
	output {
		display default_display {
			species country aspect: base transparency: 0.5;
			
			species province aspect: base transparency: 0.5 {
				species district aspect: base transparency: 0.5 {
					species commune aspect: base transparency: 0.5 {
						species landunit aspect: base transparency: 0.5;
					}
				}
			}
		}
	}
}