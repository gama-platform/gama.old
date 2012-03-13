model province_district_commune_landuse

global {
	// GIS data
	string environment_bounds <- 'gis/bounds_DT_province.shp';
	file mekong_f <- shapefile('gis/MekongDelta_provinces.shp');
	file dongthap_districts_f <- shapefile('gis/dongthap_districts.shp');
	file dongthap_communes_f <- shapefile('gis/dongthap_communes.shp');
	file dongthap_landuse_f <- shapefile('gis/landuse_DT_12_03_012.shp');

	rgb province_color <- rgb('green') const: true;
	rgb district_color <- rgb('blue') const: true;
	rgb commune_color <- rgb('cyan') const: true;
	rgb landuse_color <- rgb('red') const: true;
	
	init {
		loop p over: (mekong_f.contents) {
			let province_code type: string value: geometry(p) get ('P_CODE');
			if (province_code = 'DT') {
				create province with: [ shape :: p ];
			}
		}
	}
}

environment bounds: environment_bounds;

entities {
	species province {
		
		init {
			loop d over: (dongthap_districts_f.contents) {
				create district with: [ shape :: d ];
			}
		}
		
		species district {
			
			init {
				let d_num type: int value: shape get ('D_NUM');

				loop c over: (dongthap_communes_f.contents) {
					let district_code type: int value: (geometry(c) get ('ID_3'));
					if (district_code = d_num) {
						
						create commune with: [ shape :: c ];
					}
				}
			}
			
			species commune {
				init {
					let c_num type: int value: shape get ('ID_4');

					loop lu over: (dongthap_landuse_f.contents) {
						let commune_code type: int value: geometry(lu) get ('ID_4');
						if (commune_code = c_num) {
							create landunit with: [ shape :: lu ];
						}	
					}
				}
				
				species landunit {
					
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
		}
	}
}