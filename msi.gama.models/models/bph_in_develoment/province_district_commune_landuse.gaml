model province_district_commune_landuse

global {
	// GIS data
	string environment_bounds <- 'gis/bounds_DT_province.shp';
	file mekong_f <- shapefile('gis/MekongDelta_provinces.shp');
	file dongthap_districts_f <- shapefile('gis/dongthap_districts.shp');
	file dongthap_communes_f <- shapefile('gis/dongthap_communes.shp');
	file dongthap_landuse_f <- shapefile('gis/landuse_DT_12_03_012.shp');
	matrix TPHCM_weather <- matrix(file('data/weather/daily_TPHCM_2008_no_title.txt')) const: true;
	var xBoundDT type: float init: 0;
	var yBoundDT type: float init: 0;	

	const dirTranslation type: int init: 270 ;	
	var directionList type: list init: ['North','NNE','NE','ENE','East','ESE','SE','SSE','South','SSW','SW','WSW','West','WNW','NW','NNW'] ;
	var degreeList type: list of: float init: [0,22.5,45,67.5,90,112.5,135,157.5,180,202.5,225,247.5,270,292.5,315,337.5] ;
	var weatherTPHCM type: weather init: nil ;
	var scaleDegreeList type: list init: [];
	
	int col_num <- 15;
	int row_num <- length(TPHCM_weather)/col_num;

	int cloud_min_member <- 10;

	int no_infection <- 0 depends_on: [cloud_min_member];
	int light_infection <- cloud_min_member depends_on: [cloud_min_member];
	int medium_infection <- 2 * cloud_min_member depends_on: [cloud_min_member];
	int heavy_infection <- 3 * cloud_min_member depends_on: [cloud_min_member];
	int hopper_burn <- 4 * cloud_min_member depends_on: [cloud_min_member];
	float diedBphRate <- 0.035;
	
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
	int no_infection_no <- 0;
	int light_infection_no <- 0;
	int medium_infection_no <- 0;
	int heavy_infection_no <- 0;
	int hopper_burn_no <- 0;

	var shareKnow type: shareKnowledge init: shareKnow ;

	init {
		create species: shareKnowledge;
		set shareKnow value: first (shareKnowledge as list);

		let wTPHCM type: weather value: nil;
		create species: weather {
			set stationName value: 'TPHCM';
			set wTPHCM value: self;			
		}
		 
		ask target: wTPHCM {
			loop irow from: 1 to: row_num var: irow {
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

		let sdList var: sdList type: list value: [] ;
		let tempDegree var: tempDegree type: float value: 0 ;
		loop td over: degreeList {
			set tempDegree value: (td + dirTranslation) ;
			if condition: (tempDegree > 360) {
				set sdList value: sdList+[(td-90)]; }
				else {
					if condition: tempDegree = 360 {
						set sdList value: sdList+[0]; }
						else {
							set sdList value: sdList+[tempDegree];
						}
				}
		}

		set scaleDegreeList value: sdList ;

		create climate;

		loop p over: (mekong_f.contents) {
			let province_code type: string value: geometry(p) get ('P_CODE');
			if (province_code = 'DT') {
				create province with: [ shape :: p ];
			}
		}

		create bph_cloud number: bph_cloud_number with: [ bph_in_cloud :: cloud_min_member ];
	}
}

environment bounds: environment_bounds;

entities {

	species agentCopy skills: [moving] {
		var agentCopyName type: string init: '';
		var color type: rgb value: rgb('yellow') ;
	}	

	species shareKnowledge skills: situated {
		string name1 <- 'province_level';
		var x type: int;
		var newLoc type: point;

		action findMaxAxisCoordinateFromList {
			arg coordinateList type: list;
			arg xya type: string;
		
			if condition: (xya = 'x') {
				return value: coordinateList max_of (point(each).x);
			}
			else 
				{
					return value: coordinateList max_of (point(each).y);
			}
		}
	
		action returnCoordinatesFromGeometry type: list {
			arg fromGeometry type: list;
			
			let coordiList type: list of: point value: (fromGeometry as list);
			return coordiList ;
		}

		action createAgentCopy type: agentCopy {
			arg originAgent type: landunit;
			
			create agentCopy returns: rets {
				set agentCopyName value: (string(originAgent) + 'copy');
				set location value: originAgent.location;
				set shape value: originAgent.shape;
			}
			
			return agentCopy(rets at 0);
		}


		action findIntersectionDisAndBound type: point {
			arg agentLocation type: point ;
			arg movingDir type: float ;
			arg xBound type: float ;
			arg yBound type: float ;

			let xOldLoc type: float value: (agentLocation).x;
			let yOldLoc type: float value: (agentLocation).y;
			let newLoc var: newLoc type: point value: nil;
			let tempMovingDir var: tempMovingDir type: float value: 0 ;
			let crossAngle var: crossAngle type: float value: 0 ;
			let xTemp var: xTemp type: float value: 0 ;
			let yTemp var: yTemp type: float value: 0 ;

			if condition: movingDir=0 {
				set newLoc value: {xBound,yOldLoc} ;
			} //movingDir =0
			
			if condition: ( (movingDir > 0) and (movingDir < 90) ) {
				set crossAngle value: atan(((yBound-yOldLoc)/(xBound-xOldLoc))) ;
				if condition: movingDir < crossAngle {
					set yTemp value: yOldLoc+((xBound-xOldLoc)*(tan(movingDir))) ;
					set newLoc value: {xBound,yTemp} ;
				}
				
				if condition: movingDir = crossAngle {
					set newLoc value: {xBound,yBound} ;
				}
				
				if condition: movingDir > crossAngle {
					set xTemp value: xOldLoc+((yBound-yOldLoc)/(tan(movingDir))) ;
					set newLoc value: {xTemp,yBound} ;
				}
			}
			
			if condition: movingDir=90 {
				set newLoc value: {xOldLoc,yBound} ;
			}
			
			if condition: ( (movingDir > 90) and (movingDir < 180) ) {
				set tempMovingDir value: (movingDir-90) ;
				set crossAngle value: atan(((xOldLoc)/(yBound-yOldLoc))) ;
				if condition: tempMovingDir < crossAngle {
					set xTemp value: (xOldLoc-((yBound-yOldLoc)*(tan(tempMovingDir)))) ;
					set newLoc value: {xTemp,yBound} ;
				}
				if condition: tempMovingDir = crossAngle {
					set newLoc value: {0,yBound} ;
				}
				if condition: tempMovingDir > crossAngle {
					set yTemp value: yOldLoc+((xOldLoc)/(tan(tempMovingDir))) ;
					set newLoc value: {0,yTemp} ;
				}
			}
			
			if condition: movingDir=180 {
				set newLoc value: {0,yOldLoc} ;
			}
			
			if condition: ( (movingDir > 180) and (movingDir < 270) ) {
				set tempMovingDir value: (movingDir-180) ;
				set crossAngle value: atan(yOldLoc/xOldLoc) ;
				if condition: tempMovingDir < crossAngle {
					set yTemp value: (yOldLoc-(xOldLoc*(tan(tempMovingDir)))) ;
					set newLoc value: {0,yTemp} ;
				}
				if condition: tempMovingDir = crossAngle {
					set newLoc value: {0,0} ;
				}
				if condition: tempMovingDir > crossAngle {
					set xTemp value: xOldLoc-((yOldLoc)/(tan(tempMovingDir))) ;
					set newLoc value: {xTemp,0} ;
				}
			}
			
			if condition: movingDir=270 {
				set newLoc value: {xOldLoc,0} ;
			}
			
			if condition: ( (movingDir > 270) and (movingDir < 360) ) {
				set tempMovingDir value: (movingDir-270) ;
				set crossAngle value: atan((xBound-xOldLoc)/yOldLoc) ;
				if condition: tempMovingDir < crossAngle {
					set xTemp value: (xOldLoc+(yOldLoc*(tan(tempMovingDir)))) ;
					set newLoc value: {xTemp,0} ;
				}
				if condition: tempMovingDir = crossAngle {
					set newLoc value: {xBound,0} ;
				}
				if condition: tempMovingDir > crossAngle {
					set yTemp value: yOldLoc-((xBound-xOldLoc)/(tan(tempMovingDir))) ;
					set newLoc value: {xBound,yTemp} ;
				}
			}
			
			if condition: movingDir=360 {
				set newLoc value: {xBound,yOldLoc} ;
			}
			
			return value: newLoc ;
		}

	}
	
	species weather skills: visible {
		var stationName type: string init: nil;
		var dailyDate type: list of: string init: [] ;
		var dailyAvgTemp type: list of: float init: [] ;
		var dailyAvgHumid type: list of: float init: [] ;
		var dailyMostWindDir type: list of: string init: [] ;
		var dailyAvgWindSpeed type: list of: float init: [] ;
		var daily6hTemp type: list of: float init: [] ;
		var daily6hgHumid type: list of: float init: [] ;
		var daily6hWindDir type: list of: string init: [] ;
		var daily6hWindSpeed type: list of: float init: [] ;
		var daily14hTemp type: list of: float init: [] ;
		var daily14hgHumid type: list of: float init: [] ;
		var daily14hWindDir type: list of: string init: [] ;
		var daily14hWindSpeed type: list of: float init: [] ;
	}	
	
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

					int riceStage <- 0;
					
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
		
		var bphEggsNum type: float init: 0 ;
		var bphNymphsNum type: float init: 0 ;
		var bphAdultsNum type: float init: 0 ;
		var bphFSNum type: float init: 0 ;
		var bphMSNum type: float init: 0 ;
		var bphFLNum type: float init: 0 ;
		var bphMLNum type: float init: 0 ;
		var color type: rgb ;

		var bphMigrateList type: list of: movingBph init: [];

		var isPropagating type: bool init: false ;

		reflex {
			let lu type: landunit value: current_landunit;
			ask target: lu {
				if (lu.riceStage < 120) {
					set lu.riceStage value: lu.riceStage + 1; }
					else {
						set lu.riceStage value: 0 ;
					}
				}

			set bphFSNum value: bphFSNum*(1-diedBphRate) ;
			set bphMSNum value: bphMSNum*(1-diedBphRate) ;
			set bphFLNum value: bphFLNum*(1-diedBphRate) ;
			set bphMLNum value: bphMLNum*(1-diedBphRate) ;

			if condition: isPropagating {
				do action: propagate ;
			}
		}
		
		reflex when: (time mod 7)=0 {
			let bphMNNum value: 0 ;
			let bphFNNum value: 0 ;
			set bphAdultsNum value: bphNymphsNum*0.4 ;
			set bphNymphsNum value: bphEggsNum*0.3 ;
			set bphEggsNum value: (bphFSNum*(100+rnd(50)))+(bphFLNum*50);
			
			if (current_landunit.riceStage) < 65 {
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
			
			if bphMigrateList !=[] {
				loop bp over: bphMigrateList var: bp {
					set color value: 'red' ;
					ask target: bp {
						do action: die ;
					}
				}
			}
			
			if ( ( ( (bphAdultsNum+bphNymphsNum) > 10000) or (((current_landunit.riceStage) > 85) and 
						((current_landunit.riceStage) < 120))) and ((bphFLNum*bphMLNum) > 0) ) 
				{set isPropagating value: true;}
				else {
					set isPropagating value: false ;
				}
				
//			do action: coloringBphDensity ;
		}	
		
		action propagate {
			let immigratedLus type: list of: landunit value: [] ;
			let currentLandunit type: landunit value: current_landunit;

			let idx type: int value: (directionList index_of (weatherTPHCM.dailyMostWindDir at time));						
	
			if (idx != (-1)) {
				let windDegree type: float value: (scaleDegreeList at idx);

				do action: write {
					arg message value: windDegree;
				}
				
				let tempSpeed type: float value: (weatherTPHCM.dailyAvgWindSpeed at (time as int));
				let windSpeed type: float value: ((tempSpeed*1000)/3600);
				
				let tempMovingBph1 type: movingBph value: nil;
				let tempMovingBph type: movingBph value: nil;
		

				create species: movingBph {
					set tempMovingBph1 value: self;
				}
				
				set tempMovingBph1.location value: location;

				set immigratedLus value: self findImmgratedlandunits[currlandunit:: currentLandunit, currBph:: self, currMovingBph:: tempMovingBph1,
					windDir:: windDegree, windSpeed:: windSpeed];

				ask target: tempMovingBph1 {
					do action: die ;
				}
				
				let migrateBphFLNum value: bphFLNum/7 ;
				let migrateBphMLNum value: bphMLNum/7 ;
				set bphFLNum value: bphFLNum-migrateBphFLNum ;
				set bphMLNum value: bphMLNum-migrateBphMLNum ;
				
				if (immigratedLus !=[]) {
					let immigrateBphFLNum value: (migrateBphFLNum/length (immigratedLus)) ;
					let immigrateBphMLNum value: (migrateBphMLNum/length (immigratedLus)) ;

					loop lu over: immigratedLus var: lu {
						create species: movingBph with: [movingTime::time, originBph:: self,destinationBph:: lu.my_bph_group, 
							movingBphFLNum:: immigrateBphFLNum, movingBphMLNum::immigrateBphMLNum]
						{
							set location value: originBph.location;
							set tempMovingBph value: self;
						}

						set bphMigrateList value: bphMigrateList+[tempMovingBph] ;

						ask target: lu {
							let immigratedRate value: 0.0;
							if condition: lu.riceStage < 65 {
								set immigratedRate value: 1.0; }
								else {
									if condition: lu.riceStage < 120 {
										set immigratedRate value: 0.5 ;
									}
								}
								
							ask target: my_bph_group {
								let cBphFLNum value: bphFLNum ;
								let cBphMLNum value: bphMLNum ;
								ask target: tempMovingBph {
									set realMovingBphFLNum value: (immigrateBphFLNum*immigratedRate);
									set realMovingBphMLNum value: (immigrateBphMLNum*immigratedRate);
								}
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
			let overlappingLandunits type: list of: landunit value: [] ;

			ask target: currMovingBph {
				do action: move {
					arg heading value: windDir ;
					arg speed value: windSpeed ;
				}
			}
			
			if (currBph.location = currMovingBph.location) {
				ask target: one_of (shareKnowledge as list) {
					let tempLoc type: point value: self findIntersectionDisAndBound[agentLocation:: currMovingBph.location,
						movingDir::windDir, xBound:: xBoundDT, yBound:: yBoundDT];
					set currMovingBph.location value: tempLoc ;
				}
			}

			create linket with: [originFrom:: currBph.location, destinationTo:: currMovingBph.location] returns: tempLink {
				set shape value: polyline([originFrom, destinationTo]);				
			}
			
			set overlappingLandunits value: landunit overlapping (tempLink);
			set overlappingLandunits value: (overlappingLandunits- currlandunit);
			
			let agentCopyMoving type: agentCopy value: nil ;
			ask target: one_of (shareKnowledge as list) {
				set agentCopyMoving value: self createAgentCopy[originAgent::currlandunit];
			}
			
			let ddx type: float value: ( (currMovingBph.location).x - (currBph.location).x );
			let ddy type: float value: ((currMovingBph.location).y - (currBph.location).y) ;
			ask target: agentCopyMoving translated_by {ddx,ddy};
			
			let agentCopyMovingCoordinateList var: agentCopyMovingCoordinateList type: list of: point value: [] ;
			let currRicefieldCoordinateList var: currRicefieldCoordinateList type: list of: point value: [] ;
			
				
			set currRicefieldCoordinateList value: (currlandunit.shape).points ;
			set agentCopyMovingCoordinateList value: (agentCopyMoving.shape).points ;
				
				
			let ii var: ii type: int value: 0 ;
			let fromCoord var: fromCoord type: point value: nil ;
			let toCoord var: toCoord type: point value: nil ;
			let tpl type: linkeet value: nil ;
			let ovl var: ovl type: list value: nil ;
			loop rcl over: currRicefieldCoordinateList var: rcl {
				set fromCoord value: (currRicefieldCoordinateList at ii) ;
				set toCoord value: (agentCopyMovingCoordinateList at ii) ;
				create species: linkeet with: [originFrom::fromCoord, destinationTo::toCoord, shape :: polyline( [fromCoord, toCoord] )] returns: tpl; 

				set ovl value: landunit overlapping tpl;
				if condition: (ovl != []) {
					set ovl value: ovl-currlandunit;
					loop ov over: ovl var: ov {
						if condition: !(ov in overlappingLandunits) {
							set overlappingLandunits value: overlappingLandunits + [ov] ;
						}
					}
				}
				set ii value: ii+1 ;
			}
			
			do action: write {
				arg message value: 'From: '+currlandunit.name+'  To:' ;
			}
			
			loop rf over: overlappingLandunits {
				do action: write {
					arg message value: (rf).name+', ' ;
				}
			}
			do action: write {
				arg message value: 'ooooooooooooooooo' ;
			}
			ask target: agentCopyMoving {
				set color value: rgb('yellow') ;
			}
			ask target: currlandunit {
				set color value: rgb('red') ;
			}			
			return overlappingLandunits;
		}
}
	
	species linket skills: [situated, visible] {
		rgb color init: rgb('blue') ;
		var originFrom type: point init: nil;
		var destinationTo type: point init: nil;
		var shape type: geometry;
	}

	species linkeet skills: [situated, visible] {
		rgb color init: rgb('blue');
		var originFrom type: point init: nil;
		var destinationTo type: point init: nil;
		var shape type: geometry;
	}
			
	species movingBph skills: moving {
		var color type: rgb value: 'black' ;
		var movingTime type: int init: 0 ;
		var originBph type: bph_group init: nil;
		var destinationBph type: bph_group init: nil;
		var movingBphFLNum type: float init: 0.0 ;
		var movingBphMLNum type: float init: 0.0 ;
		var realMovingBphFLNum type: float init: 0.0 ;
		var realMovingBphMLNum type: float init: 0.0 ;
		
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