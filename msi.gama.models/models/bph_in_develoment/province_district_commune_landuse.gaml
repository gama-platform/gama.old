model province_district_commune_landuse
//multi-scale model_Vinh

global {

	string environment_bounds <- 'gis/bounds_DT_province.shp';
	
	file mekong_f <- shapefile('gis/MekongDelta_provinces.shp');
	file dongthap_districts_f <- shapefile('gis/dongthap_districts.shp');
	file dongthap_communes_f <- shapefile('gis/dongthap_communes.shp');
	file dongthap_landuse_f <- shapefile('gis/landuse_DT_12_03_012.shp');
	matrix TPHCM_weather <- matrix(file('data/weather/daily_TPHCM_2008_no_title.txt')) const: true;
	float xBoundDT init: 0;
	float yBoundDT init: 0;	
	rgb clNormal init: rgb('white') parameter: 'Color with normal BPH density:' category: 'INPUT WINDOW'; 
	rgb clLight init: rgb('green') parameter: 'Color with light BPH density:' category: 'INPUT WINDOW' ; 
	rgb clMedium init: rgb('yellow') parameter: 'Color with medium BPH density:' category: 'INPUT WINDOW';
	rgb clHeavy init: rgb([251,153,234]) parameter: 'Color with heavy BPH density:' category: 'INPUT WINDOW'; 
	rgb clStrongHeavy init: rgb('red') parameter: 'Color with strong heavy BPH density:' category: 'INPUT WINDOW';
	
	const dirTranslation type: int init: 270 ;	
	list directionList init: ['North','NNE','NE','ENE','East','ESE','SE','SSE','South','SSW','SW','WSW','West','WNW','NW','NNW'] ;
	list degreeList of: float init: [0,22.5,45,67.5,90,112.5,135,157.5,180,202.5,225,247.5,270,292.5,315,337.5] ;
	weather weatherTPHCM init: nil ;
	list scaleDegreeList init: [];
	
	int col_num <- 15;
	int row_num <- length(TPHCM_weather)/col_num;

	int cloud_min_member <- 10;

	int no_infection <- 0 depends_on: [cloud_min_member];
	int light_infection <- cloud_min_member depends_on: [cloud_min_member];
	int medium_infection <- 2 * cloud_min_member depends_on: [cloud_min_member];
	int heavy_infection <- 3 * cloud_min_member depends_on: [cloud_min_member];
	int hopper_burn <- 4 * cloud_min_member depends_on: [cloud_min_member];
	float diedBphRate <- 0.035;
	list setOfLandunit of: landunit init: [];
	
	rgb no_infection_color <- rgb('blue') const: true;
	rgb light_infection_color <- rgb('yellow') const: true;
	rgb medium_infection_color <- rgb('pink') const: true;
	rgb heavy_infection_color <- rgb('red') const: true;
	rgb hopper_burn_color <- rgb('black') const: true;

//	rgb province_color <- rgb('green') const: true;
//	rgb district_color <- rgb('blue') const: true;
//	rgb commune_color <- rgb('cyan') const: true;
//	rgb landuse_color <- rgb('red') const: true;
	rgb landuse_color <- rgb('green') const: true;
	rgb province_color <- rgb('white') const: true;
	rgb district_color <- rgb('white') const: true;
	rgb commune_color <- rgb('white') const: true;
	
	int bph_cloud_number <- 5000;
	rgb bph_cloud_color <- rgb('cyan') const: true;
	int bph_in_field_min_time <- 30 const: true;
	
	list possible_wind_directions <- [0, 30, 60, 90, 120, 150, 180, 210, 240, 270, 300, 330, 360] of: int; 
	int wind_direction <- one_of(possible_wind_directions) depends_on: possible_wind_directions; // 45¡
	float wind_speed <- 0.001 parameter: 'Wind speed' min: 0.0001;
	int stepNo init:0;

	// workaround for visibility
	int no_infection_no <- 0;
	int light_infection_no <- 0;
	int medium_infection_no <- 0;
	int heavy_infection_no <- 0;
	int hopper_burn_no <- 0;

	shareKnowledge shareKnow init: shareKnow;
	list coordinateList of: point init:[];
	
	topology world_topo <- topology(shape) depends_on: shape;
	

	init {
		create species: shareKnowledge;
		set shareKnow value: first (shareKnowledge as list);

		let wTPHCM type: weather value: nil;
		create species: weather {
			set stationName value: 'TPHCM';
			set wTPHCM value: self;			
		}
		 
		ask target: wTPHCM {
			loop irow from: 1 to: row_num {
				set dailyDate value: dailyDate + [TPHCM_weather at {0,irow}] ;
				set dailyAvgTemp value: dailyAvgTemp + [TPHCM_weather at {1,irow}] ;
				set dailyAvgHumid value: dailyAvgHumid + [TPHCM_weather at {2,irow}] ;
				set dailyMostWindDir value: dailyMostWindDir + [TPHCM_weather at {3,irow}] ;
				set dailyAvgWindSpeed value: dailyAvgWindSpeed + [TPHCM_weather at {4,irow}] ;
				set daily6hTemp value: daily6hTemp + [TPHCM_weather at {5,irow}] ;
				set daily6hgHumid value: daily6hgHumid + [TPHCM_weather at {6,irow}] ;
				set daily6hWindDir value: daily6hWindDir + [TPHCM_weather at {7,irow}] ;
				set daily6hWindSpeed value: daily6hWindSpeed + [TPHCM_weather at {8,irow}] ;
				set daily14hTemp value: daily14hTemp + [TPHCM_weather at {9,irow}] ;
				set daily14hgHumid value: daily14hgHumid + [TPHCM_weather at {10,irow}] ;
				set daily14hWindDir value: daily14hWindDir + [TPHCM_weather at {11,irow}] ;
				set daily14hWindSpeed value: daily14hWindSpeed + [TPHCM_weather at {12,irow}] ;
			}
		}
		
		set weatherTPHCM value: wTPHCM;

		let sdList type: list value: [] ;
		let tempDegree type: float value: 0 ;
		loop td over: degreeList {
			set tempDegree value: (td + dirTranslation) ;
			if  (tempDegree > 360) {
				set sdList value: sdList+[(td-90)]; }
				else {
					if  tempDegree = 360 {
						set sdList value: sdList+[0]; }
						else {
							set sdList value: sdList+[tempDegree];
						}
				}
		}

		set scaleDegreeList value: sdList ;

		set coordinateList value: (world.shape).points;
		
		ask target: shareKnow {
			set xBoundDT value: self findMaxAxisCoordinateFromList[coordinateList:: coordinateList, xya:: 'x'];			
			set yBoundDT value: self findMaxAxisCoordinateFromList[coordinateList:: coordinateList, xya:: 'y'];
			set xbou value: xBoundDT;
			set ybou value: yBoundDT;
			set coordinateList1 value: coordinateList;
		}

		create climate;

		loop p over: (mekong_f.contents) {
			let province_code type: string value: geometry(p) get ('P_CODE');
			if (province_code = 'DT') {
				create province with: [ shape :: p ]; 
			}
		}
		create bph_cloud number: bph_cloud_number with: [ bph_in_cloud :: cloud_min_member ];
		
	}
	
	reflex {
		set stepNo value: stepNo+1;
	}	
	set setOfLandunit value: (( (one_of(list(province))).agents) where ( (string(each) ) contains ('landunit') ));
}

environment bounds: environment_bounds;

entities {

	species shareKnowledge skills: situated {
		string name1 <- 'province_level';
		int x; point newLoc; float xbou;
		float ybou; list coordinateList1 of: point;

		action findMaxAxisCoordinateFromList type:float {
			arg coordinateList type: list;
			arg xya type: string;
		
			if  (xya = 'x') {
				return (coordinateList max_of (point(each).x));
			}
			else 
				{
					return (coordinateList max_of (point(each).y));
			}
		}
	}
	
	species weather skills: visible {
		string stationName init: nil;
		list dailyDate of: string init: [] ;
		list dailyAvgTemp of: float init: [] ;
		list dailyAvgHumid of: float init: [] ;
		list dailyMostWindDir of: string init: [] ;
		list dailyAvgWindSpeed of: float init: [] ;
		list daily6hTemp of: float init: [] ;
		list daily6hgHumid of: float init: [] ;
		list daily6hWindDir of: string init: [] ;
		list daily6hWindSpeed of: float init: [] ;
		list daily14hTemp of: float init: [] ;
		list daily14hgHumid of: float init: [] ;
		list daily14hWindDir of: string init: [] ;
		list daily14hWindSpeed of: float init: [] ;
	}	
	
	species climate {

	}

	species province {
		int no_infection_lu_d_p <- 0 value: sum(list(district) collect (each.no_infection_lu_d));
		int light_infection_lu_d_p <- 0 value: sum(list(district) collect (each.light_infection_lu_d));
		int medium_infection_lu_d_p <- 0 value: sum(list(district) collect (each.medium_infection_lu_d));
		int heavy_infection_lu_d_p <- 0 value: sum(list(district) collect (each.heavy_infection_lu_d));
		int hopper_burn_lu_d_p <- 0 value: sum(list(district) collect (each.hopper_burn_lu_d));
		
		init {
			loop d over: (dongthap_districts_f.contents) {
				create district with: [ shape :: d ];
			}
			
			create PDecisionMaker with: [ managed_province :: self ];
		}
		
		reflex update_infection_status {
 			set no_infection_no value: no_infection_lu_d_p;
 			set light_infection_no value: light_infection_lu_d_p;
 			set medium_infection_no value: medium_infection_lu_d_p;
 			set heavy_infection_lu_d_p value: heavy_infection_no;
 			set hopper_burn_no value: hopper_burn_lu_d_p;
		}
		
		action bph_cloud_landing {
			arg a_bph_cloud type: bph_cloud;
			
			let target_district type: district value: one_of(list(district));
			ask target: target_district {
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
				ask target: target_commune {
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
						ask target: target_lu {
							do bphs_land_on_lu {
								arg a_bph_cloud value: a_bph_cloud;
							}
						}
					}
				}
				
				species landunit {
					int active_bph <- 0 value: my_bph_group.bph_in_group;
					rgb color <- landuse_color;
					bool is_hopper_burn function: { active_bph >= hopper_burn };
					int infection_status <- 0 value: ( self my_infection_status [] );
					int riceStage <- 0;					
					bph_group my_bph_group;
					
					init {
							create bph_group returns: bph_gs {
								if condition: ((rnd(10)) > 9.0) {
									set bphEggsNum value: 100 ;
									set bphNymphsNum value: 10000 ;
									set bphAdultsNum value: 500 ;
								}
								else {
									set bphEggsNum value: rnd(100) ;
									set bphNymphsNum value: rnd(200) ;
									set bphAdultsNum value: rnd(200) ;
								}							
											
							}
							set my_bph_group value: (bph_gs at 0);
							set my_bph_group.my_landunit value: self;
							set my_bph_group.location value: location;
					}
					
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
						ask target: a_bph_cloud { do die; }
					}

//					reflex bphs_take_off when: (is_hopper_burn) {
//						if (rnd(10) > 5) {
//							create bph_cloud with: [ location :: self.location, bph_in_cloud :: my_bph_group.bph_in_group ];
//							set my_bph_group.bph_in_group value: 0;
//						}
//					}

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
		landunit my_landunit;
		int bph_in_group;
		
		float bphEggsNum init: 0;
		float bphNymphsNum init: 0;
		float bphAdultsNum init: 0;
		float bphFSNum init: 0 ;
		float bphMSNum init: 0 ;
		float bphFLNum init: 0 ;
		float bphMLNum init: 0 ;
		
		bool isPropagating init: false ;

		reflex {

			ask target: my_landunit {
				if (riceStage < 120) {
					set riceStage value: (riceStage + 1); }
					else {
						set riceStage value: 0 ;
					}
				}

			set bphFSNum value: bphFSNum*(1-diedBphRate);
			set bphMSNum value: bphMSNum*(1-diedBphRate);
			set bphFLNum value: bphFLNum*(1-diedBphRate);
			set bphMLNum value: bphMLNum*(1-diedBphRate);
			
			if !empty(linket as list) {
				loop ll over: (linket as list) {
					ask target: ll {
						do action: die ;
					}
				}
			}
				
			if  (isPropagating) {
				do action: propagate;
			}
		}
		
		reflex growth_invasion when: ((time mod 7)=0) {
			set bphAdultsNum value: bphNymphsNum*0.4 ;
			set bphNymphsNum value: bphEggsNum*0.3 ;
			set bphEggsNum value: (bphFSNum*(100+rnd(50)))+(bphFLNum*50);

			let bphMNNum value: 0 ;
			let bphFNNum value: 0 ;
			
			if (my_landunit.riceStage) < 65 {
				set bphFNNum value: bphAdultsNum*0.8 ;
				set bphMNNum value: bphAdultsNum*0.2 ;
				set bphFSNum value: bphFNNum*0.8 ;
				set bphMSNum value: bphMNNum*0.8 ; }
				else {
					set bphFNNum value: bphAdultsNum*0.5 ;
					set bphMNNum value: bphAdultsNum*0.5 ;
					set bphFSNum value: bphFNNum*0.2 ;
					set bphMSNum value: bphMNNum*0.2 ;
					set bphEggsNum value: bphEggsNum*0.5 ;
				}

			set bphFLNum value: bphFNNum-bphFSNum ;
			set bphMLNum value: bphMNNum-bphMSNum ;		
				
			if  ( ((bphAdultsNum+bphNymphsNum) > 10000) or
				((((my_landunit.riceStage) > 85) and ((my_landunit.riceStage) < 120)) and ((bphFLNum*bphMLNum) > 0)) ) 
				{ set isPropagating value: true; }
				else {
					set isPropagating value: false ;
				}

			do action: luColorByBphDensity;
		}	

		
		action propagate {
			let immigratedLus type: list of: landunit value: [] ;
			
			let idx type: int value: (directionList index_of (weatherTPHCM.dailyMostWindDir at stepNo));
			
			if (idx != (-1)) {
				let windDegree type: float value: (degreeList at idx);
			
				let tempSpeed type: float value: (weatherTPHCM.dailyAvgWindSpeed at (stepNo));
				let windSpeed type: float value: ((tempSpeed*1000)/3600);
				
				let tempMovingBph1 type: movingBph value: nil;		

				create species: movingBph returns: rets;
				set tempMovingBph1 value: (rets at 0);
				
				set tempMovingBph1.location value: {location.x, location.y};
			
				set immigratedLus value: (self findImmgratedlandunits[currlandunit:: my_landunit, currBph:: self, currMovingBph:: tempMovingBph1, windDir:: windDegree, windSpeed:: windSpeed]);
							
				ask target: tempMovingBph1 {
					do action: die;
				}
				
				let migrateBphFLNum value: (bphFLNum/7);
				let migrateBphMLNum value: (bphMLNum/7);
				
				set bphFLNum value: bphFLNum-migrateBphFLNum;
				set bphMLNum value: bphMLNum-migrateBphMLNum;
			
				if !empty(immigratedLus) {

					let immigrateBphFLNum value: (migrateBphFLNum/length(immigratedLus));
					let immigrateBphMLNum value: (migrateBphMLNum/length(immigratedLus));

					loop lu over: list(immigratedLus) {
						ask target: lu {
							let immigratedRate value: 0.0;
							if  riceStage < 65 {
								set immigratedRate value: 1.0; }
								else {
									if  riceStage < 120 {
										set immigratedRate value: 0.5 ;
									}
								}
								
							ask target: my_bph_group {
								let cBphFLNum value: bphFLNum ;
								let cBphMLNum value: bphMLNum ;

								set bphFLNum value: bphFLNum+(immigrateBphFLNum*immigratedRate);
								set bphMLNum value: bphMLNum+(immigrateBphMLNum*immigratedRate);
							}
							
						}							

					}
				}
			}				
		}
		
		action  findImmgratedlandunits type: list {
			arg currlandunit type: landunit;
			arg currBph type: bph_group;
			arg currMovingBph type: movingBph;
			arg windDir type: float;
			arg windSpeed type: float;

			let tempLink type: linket value: nil ;
			let overlappingLandunits type: list of: landunit value: [];

			ask target: currMovingBph {
				do action: move {
					arg speed value: windSpeed;
					arg heading value: windDir;
				}
			}
					
		if !(currBph.location = currMovingBph.location) {			
			
			create species: linket {
				set originFrom value: currBph.location;
				set destinationTo value:currMovingBph.location;
				set shape value: line([currBph.location,currMovingBph.location]);				
				set tempLink value: self;				
				}

			set overlappingLandunits value: (setOfLandunit overlapping (tempLink));
			set overlappingLandunits value: (overlappingLandunits- currlandunit);

			let ddx type: float value: ( (currMovingBph.location).x - (currBph.location).x );
			let ddy type: float value: ((currMovingBph.location).y - (currBph.location).y) ;

			let geomCurrlandunit type: geometry value: (currlandunit.shape);
						
			let translatedGeom type: geometry value: (geomCurrlandunit translated_by {ddx,ddy});

			let transPointList type: list of: point value: (translatedGeom).points;
			let currLuCorList type: list of: point value: (currlandunit.shape).points;

 			let polygeo type: geometry value: geometry(transPointList+currLuCorList);
			set overlappingLandunits value: ( (setOfLandunit as list) overlapping (polygeo));	
			set overlappingLandunits value: remove_duplicates(overlappingLandunits);	 
 
 			set overlappingLandunits value: (overlappingLandunits-currlandunit);
 
		}
			return overlappingLandunits;
	}
		
		action luColorByBphDensity {
	
			let bphDensity value: (bphAdultsNum+bphNymphsNum);
			let temp_color value: clNormal;
			if bphDensity < 750 {
				set temp_color value: clNormal;}
				else {
					if bphDensity < 1500 {
						set temp_color value: clLight ;}
						else {
							if bphDensity < 3000 {
								set temp_color value: clMedium;}
								else {
									if bphDensity < 10000 {
										set temp_color value: clHeavy; }
										else {
											set temp_color value: clStrongHeavy;
										}
									}
								}
							}				
			let lunit type: landunit value: my_landunit;
			
			ask target: lunit {
				set lunit.color value: temp_color;
			}
		}
}
	
	species linket skills: [situated, visible] {
		point originFrom init: nil;
		point destinationTo init: nil;
		geometry shape;
	}
			
	species movingBph skills: moving {
		rgb color value: 'black' ;
		int movingTime init: 0 ;
		bph_group originBph init: nil;
		bph_group destinationBph init: nil;
		float movingBphFLNum init: 0.0 ;
		float movingBphMLNum init: 0.0 ;
		float realMovingBphFLNum init: 0.0 ;
		float realMovingBphMLNum init: 0.0 ;
		
		aspect base {
			draw shape: geometry at: location color: 'black' size: 600 ;
		}
	}	
	
	
	
	species bph_cloud skills: moving {
		int heading <- wind_direction value: wind_direction;
		float speed <- wind_speed value: wind_speed;
		int amplitude <- rnd (10);
		geometry shape <- circle(0.0005);
		int altitude min: 0;
		
		int bph_in_cloud;
		
		init {
			// put the cloud more probably upon DongThap province
			let dongthap type: province value: list(province) at 0;
			if (rnd(10) > 2) {
				set location value: any_location_in(dongthap.shape);
			}
		}
		
		reflex travel {
			do wander {
				arg amplitude value: amplitude;
			}
		}
		
		reflex landing {
			let potential_province type: province value: one_of (province overlapping self);
			if (potential_province != nil) {
				ask target: potential_province {
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
				
//				init {
//					do write {
//						arg message value: name + ' with managed_commune: ' + (string(managed_commune)) + ' and lanunits: ' + (string(managed_commune.members));
//					}
//				}
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