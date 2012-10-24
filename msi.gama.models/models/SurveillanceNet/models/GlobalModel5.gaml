/*
 * 
 * 
 *  Insect Surveillance Network Model
 *  Author: Truong Xuan Viet
 *  Last Modified Date: 10-10-2012 
 */
 

model InsectSNM

import "../includes/GlobalParam.gaml"
import "../includes/administratives/administrative_region.gaml"
import "../includes/ecologies/rice_region.gaml"
import "../includes/surveillances/node_network.gaml"
import "../includes/surveillances/edge_network.gaml"
import "../includes/surveillances/graph_network.gaml"
import "../includes/naturals/natural_environment.gaml"
import "../includes/naturals/weather_region.gaml"


global {
	
	//matrix diskgraph_network size: {no_of_sensor, no_of_sensor} ;
	//var lighttrap_data type: matrix value: matrix (file('../datasources/lighttrap_data_mekong_scenario2.csv')) ;
	
	var SHAPE_NODE type: string init: '../includes/gis/surveillances/Three_Provinces_Lighttraps_WGS.shp' parameter: 'Sensors Network - DONG THAP:' category: 'SURVEILLANCE' ;
	
	var num_of_edges type: int init: 0;
	
	var current_udg type: UnitDiskGraph init: nil;
	var current_edge type: edge init: nil;
	var current_natural_environment type: NaturalEnvironment init: nil; 
	
	// Light-trap data:
	var lighttrap_data type: matrix value: matrix (file ('../includes/datasources/lighttrap_data_2010b.csv'));
	
	// Weather data:
	var genaral_weather_data type: matrix value: matrix (file ('../includes/datasources/general_weather_data.csv'));
	
	// Wind data:
	var station_weather_data type: matrix value: matrix (file ('../includes/datasources/station_weather_data03.csv'));
	
	var no_of_nodes type: int init: 50;
	var count_loop_of_inference type: float init: 0.0 ;
	var orthogonal_vector_index type: int init: 0 ;
	var sum_distance type: float init: 0.0 ;
	var sum_beta type: float init: 0.0 ;
	var estimated_value type: float init: 0.0 ;
			
	
	
	// Simulation step:
	var simStep type: int init: 0;
	var vectorX type: list value: [0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0];
	var vectorY type: list value: [0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0];
	
	
	var twoDaysCorrelation type: float init: 0.0;
	var twoDaysCorrelationAVG type: float init: 0.0;
	
	
	var edgesCorrelation type: float init: 0.0;
	var edgesCorrelationAVG type: float init: 0.0;
	
	var source_density type: float init: 0.0;
	var destination_density type: float init: 0.0;
	
	// SUM VALUES:
	var sum_attractive_index type: float init: 0.0;	
	var sum_hinder_index type: float init: 0.0;
	
	// ORTHOGONAL VECTOR:
	//var orthogonal_vector type: matrix <- 0 as_matrix({3, no_of_nodes});
	
	var orthogonal_vector type: matrix size: {3, no_of_nodes};
	//var orthogonal_list type: list init: list ([no_of_nodes]);
	
	var orthogonal_vector_index_low type: int init: 0 ;
	var sum_distance_low type: float init: 0.0 ;
	var sum_beta_low type: float init: 0.0 ;
	var estimated_value_low type: float init: 0.0 ;
	
	// Date variables:
	var sim_Month type: int init: 1;
	var sim_Year type: int init: 2010;
	
	// Temp variables:
	var temp type: list init: nil;
	var _month type: int init: 0;
	var _id_Data type: string init: 0;
	var _id type: string init: 0;
	var _count type: int init: 0;
	
	init{
		create species: district_region from: SHAPE_ADMINISTRATIVE_DISTRICT with: [id_1 :: read('ID_1'), region_name :: read('NAME_1'), id_2 :: read('ID_2'), province_name :: read('NAME_2'), district_name :: read('NAME_3')];
		create species: province_region from: SHAPE_ADMINISTRATIVE_PROVINCE with: [id_1 :: read('ID_1'), region_name :: read('NAME_1'), id_2 :: read('ID_2'), province_name :: read('NAME_2')];
		create species: sea_region from: SHAPE_SEA_REGION with: [description :: read('Description')];
		create species: rice_region from: SHAPE_LAND_USE with: [id :: read('ID'), description :: read('SDD')];
		create species: node from: SHAPE_NODE with: [id :: read('ID'), name :: read('LightTrap'), district_name :: read('District'), province_name :: read('Province'), id_0 :: read('ID_0'), id_1 :: read('ID_1'), id_2 :: read('ID_2')];
		create species: weather_region from: SHAPE_WEATHER with: [id :: read('ID'), name :: read('NAME')];
		
		// UDG Species
		create species: UnitDiskGraph number: 1;
		set current_udg value: UnitDiskGraph at 0;
		
		// Natural Environment species:
		create species: NaturalEnvironment number: 1;
		set current_natural_environment value: NaturalEnvironment at 0;
	}

	
	reflex main_reflex
	{
		
		if condition: (current_udg.setup = 0)
		{
			do action: loadLighttrapData;
			do action: loadGeneralWeatherData;
			do action: loadStationWeatherData;
			ask target: current_udg
			{
				do action: resetEdgesList;
			}
		}
		
		// Calculating the correlation:
		if (simStep < 31)
		{
			do action: getsCorrelationByEdges;
			do action: getCorrelationTwoDays;
		}
		else if (simStep = 31)
		{
			do action: estimate_Inverse_Distance_Weighting;
		}
		else
		{
			do action: dispatchByWind2;
		}
		  
		set simStep value: simStep + 1;
	}
	
	// Loading the lighttrap data from the CSV file:
	action loadLighttrapData
	{
		let no_of_rows value: lighttrap_data.rows - 1;
		loop from: 0 to: length (node) - 1 var: cnt {
			let the_node type: node value: node at cnt;
			ask target: the_node
			{
				loop from: 1 to: (no_of_rows - 1) var: i
				{
					if condition: (id = lighttrap_data at {2, i})
					{
						loop from: 0 to: (HISTORICAL_DURATION - 1) var: j
						{
							put item: lighttrap_data at {3 + j, i} at: {0, j} in: density_matrix;
						}
						
						// Choosing the first day for estimation & prediction 
						set number_of_BPHs value: lighttrap_data at {31, i};
						set number_of_BPHs_total value: lighttrap_data at {368, i};
					}
				}
				do action: setcolor;
				
			}
		}
	}
	
	// Loading the general weather data from the CSV file:
	action loadGeneralWeatherData
	{
		let no_of_months value: 12;
		ask target: current_natural_environment{ 
			loop from: 0 to: (no_of_months - 1) var: i
			{
				put item: genaral_weather_data at {i + 1, 0} at: i in: Mean_Wind_Speed;
				put item: genaral_weather_data at {i + 1, 1} at: i in: Min_Wind_Speed;
				put item: genaral_weather_data at {i + 1, 2} at: i in: Max_Wind_Speed;
				put item: genaral_weather_data at {i + 1, 3} at: i in: Wind_Direction_From;
				put item: genaral_weather_data at {i + 1, 4} at: i in: Wind_Direction_To;
			}
		}
	}
	
	// Loading the station weather data from th CSV file (Voronoi polygons):
	action loadStationWeatherData
	{
		let no_of_months value: 12;
		let no_of_stations value: length (weather_region);
		
		loop from: 0 to: no_of_stations - 1 var: j {
			let the_weather_region type: weather_region value: weather_region at j;
			ask target: the_weather_region{
				loop from: 1 to: (no_of_months * no_of_stations) var: i {
					if condition: the_weather_region.id = (station_weather_data at {0, i})
					{
						set _month  value: int (station_weather_data at {3, i});
						// Temperature:
						put item: station_weather_data at {5, i} at: _month - 1 in: Temp_Mean;
						put item: station_weather_data at {6, i} at: _month - 1 in: Temp_Max;
						put item: station_weather_data at {7, i} at: _month - 1 in: Temp_Min;
						
						// Rainning
						put item: station_weather_data at {8, i} at: _month - 1 in: Rain_Amount;
						put item: station_weather_data at {9, i} at: _month - 1 in: Rain_Max;
						put item: station_weather_data at {10, i} at: _month - 1 in: Rain_No_Days_Max;
						put item: station_weather_data at {11, i} at: _month - 1 in: Rain_No_Days;
						put item: station_weather_data at {12, i} at: _month - 1 in: Rain_Mean;
								
						// Humidity
						put item: station_weather_data at {13, i} at: _month - 1 in: Hum_Mean;
						put item: station_weather_data at {14, i} at: _month - 1 in: Hum_Min;
						put item: station_weather_data at {15, i} at: _month - 1 in: Hum_No_Days;
								
						// Sunning
						put item: station_weather_data at {16, i} at: _month - 1 in: Sunning_Hours;
						put item: station_weather_data at {17, i} at: _month - 1 in: Sunning_Hours_Mean;
					}
				}
			}
		}
		
		ask target: weather_region at 0
		{
				set temp value: Sunning_Hours_Mean;
				set _id  value: id;
				set _id_Data  value: station_weather_data at {0, 1};
		}
		
		
	}
	
		
	// INVERSE DISTANCE WEIGHTING ESTIMATION:
	action estimate_Inverse_Distance_Weighting 
	{
		
		let is_sampled_point type: float value: 0.0 ;
		let distance type: float value: 0.0 ;
		set no_of_nodes value: length (node);
		
		let cnt type: int value: 0;
		loop var: cnt from: 0 to: no_of_nodes - 1 {
			
			put item: 0.0 at: {0, cnt} in: orthogonal_vector;
			put item: 0.0 at: {1, cnt} in: orthogonal_vector;
			put item: 0.0 at: {2, cnt} in: orthogonal_vector;
		}
		

		set cnt value: 0; 
		loop from: 0 to: length (cellula_automata) - 1 var: cnt {
			let the_cell type: cellula_automata value: cellula_automata at cnt;
			set is_sampled_point value: 0.0 ;
			set distance value: 0.0 ;
			set orthogonal_vector_index value: 0 ;
			set sum_distance value: 0.0 ;
			set estimated_value value: 0.0 ;
			set sum_beta value: 0.0 ;
			
			let the_node type: node value: nil;
			loop var: i from: 0 to: no_of_nodes - 1 
			 {
			 	set the_node value: node at i;
			 	
				set distance value: the_cell distance_to the_node ;
				if condition: distance < 1.0 {
					set is_sampled_point value: 1.0 ;
					set estimated_value value: the_node.number_of_BPHs ;
				}
				
				put item: distance at: {0, orthogonal_vector_index} in: orthogonal_vector;
				put item: the_node.number_of_BPHs at: {1, orthogonal_vector_index} in: orthogonal_vector;
				
				set sum_distance value: sum_distance + float (orthogonal_vector at {0, orthogonal_vector_index}) ;
				set orthogonal_vector_index value: orthogonal_vector_index + 1 ;
			}
			
			let i type: int value: 0;
			loop var: i from: 0 to: no_of_nodes - 1 {
				put item: (sum_distance/ float ((orthogonal_vector) at {0, i})) at: {2, i} in: orthogonal_vector ;
				set sum_beta value: sum_beta + float (orthogonal_vector at {2, i}) ;
			}
				
			set i value: 0;
			loop var: i from: 0 to: no_of_nodes - 1 {
				set estimated_value value: estimated_value + (( float (orthogonal_vector at {2, i})/sum_beta) * float (orthogonal_vector at {1, i})) ;
					
				put item: 0.0 at: {0, i} in: orthogonal_vector;
				put item: 0.0 at: {1, i} in: orthogonal_vector;
				put item: 0.0 at: {2, i} in: orthogonal_vector;
				
				ask target: the_cell {
					if(hinder_index != 1.0)
					{
						set number_of_BPHs value: estimated_value;
					}
					else
					{
						set number_of_BPHs value: estimated_value * attractive_index;	
					}
					
					// Mouvable amount in the next simulation step:
					set number_of_movable_BPHs value: (1.0 - attractive_index) * number_of_BPHs;
					
					do action: setcolor ;
				}
			}
				
		}
		
	}
	
	// ORDERRED PROPAGATION METHOD:
	action dispatchByWindDraft
	{
		let propagation_distance type: float value: DISK_RADIUS;
		// BPHs density rate between lighttrap (n0_BPHs/lighttrap)  and field (n0_BPHs/m^2)
		let lighttrap_field_rate type: float value: 1.0;
		
		let cnt type: int value: 0;
		loop from: 0 to: length (cellula_automata) - 1 var: cnt
		{
			let the_source_cellula type: cellula_automata value: cellula_automata at cnt;
			let xO type: float value: (float(((the_source_cellula) . location) . x));
			let yO type: float value: (float(((the_source_cellula) . location) . y));
			
			// Modified: (Depend on the local condition)
			let dispatchThreshold type: float value: rnd (1000);
			
			
			if condition: ((the_source_cellula.number_of_BPHs/lighttrap_field_rate) > dispatchThreshold)
			{
				let difference_amount type: float value: the_source_cellula.number_of_BPHs - dispatchThreshold;
				let FO type: float value: rnd (difference_amount);
				
				let cnt1 type: int value: 0;
				loop from: 0 to: length (cellula_automata) - 1 var: cnt1
				{
					let the_destination_cellula type: cellula_automata value: cellula_automata at cnt1;
					if condition: (FO > 0.0) and (abs (the_source_cellula distance_to the_destination_cellula) < propagation_distance)
					{
						let xN type: float value: (float(((the_destination_cellula) . location) . x));
						let yN type: float value: (float(((the_destination_cellula) . location) . y));
						let dNO type: float value: sqrt(((yN - yO) ^ 2) + ((xN - xO) ^ 2));
						let a3 type: float value: (yN - yO) / (xN - xO);
						let b3 type: float value: ((xN * yO) - (xO * yN)) / (xN - xO);
						let a1 type: float value: 1;
						let b1 type: float value: yO - (a1 * xO);
						let xC type: float value: -1;
						let yC type: float value: -1;
						let a2 type: float value: -(1/a1);
						let b2 type: float value: yO - (a2 * xO);
						let s1 type: float value: -1;
						if condition: yN > ((a2 * xN) + b2)
						{
							set s1 value: 1;
						}
						
						let s2 type: float value: -1;
						if condition: yC > ((a2 * xC) + b2)
						{
							set s2 value:1;
	
						}
								 
						if condition: (s1 * s2) < 0
						{
							let out_amount type: float value: rnd (FO);
							set the_source_cellula.out_number_of_BPHs value: the_source_cellula.out_number_of_BPHs + out_amount;
							set the_destination_cellula.in_number_of_BPHs value: the_destination_cellula.in_number_of_BPHs + out_amount;
							set FO value: FO - out_amount;
						}
					}
				}
				
				set the_source_cellula.in_number_of_BPHs value: the_source_cellula.in_number_of_BPHs + FO;
			}
			
		}	
					
		// Reset BPHs density value of propagation
		set cnt value: 0;
		loop from: 0 to: length (cellula_automata) - 1 var: cnt 
		{
			let the_cellula type: cellula_automata value: cellula_automata at cnt;
			ask target: the_cellula
			{
				set number_of_BPHs value: (number_of_BPHs + in_number_of_BPHs) - out_number_of_BPHs;
				set in_number_of_BPHs value: 0;
				set out_number_of_BPHs value:0;
				do action: setcolor;
			}
		}
	}
	
	
	action dispatchByWind
	{
		let alpha type: float value: current_udg.alpha;
		let propagation_distance type: float value: DISK_RADIUS;
		let no_of_cell type: int value: 2500; //length (cellula_automata);
		
		
			let i type: int value: 0;
			loop var: i from: 0 to: no_of_cell - 1 
			{
				let the_source_cell type: cellula_automata value: cellula_automata at i;  
				
				
				
				///////////////////////////////////////////////////////////////
				// Modified: (Depend on the local condition)
				let dispatchThreshold type: float value: rnd (1000);
					
				let difference_amount type: float value: the_source_cell.number_of_BPHs - dispatchThreshold;
				let FO type: float value: rnd (difference_amount);
				/////////////////////////////////////////////////////////////
				
				
				let j type: int value: 0;
				loop var: j from: 0 to: no_of_cell - 1  
				{
					
					let the_destination_cell type: cellula_automata value: cellula_automata at j;
					let distance type: float value: the_source_cell distance_to the_destination_cell;
					
					if condition: distance < DISK_RADIUS
					{
						
						set x1  value: (float(((the_source_cell) . location) . x));  
						set y1  value: (float(((the_source_cell) . location) . y));						
						set x2  value: (float(((the_destination_cell) . location) . x));  
						set y2  value: (float(((the_destination_cell) . location) . y));
						
						// Calculating the maximum location:
						set x1_to  value: (float (x1 + float(DISK_RADIUS * float(float (cos(alpha))))));
						set y1_to value: (float (y1 + float(DISK_RADIUS * float(float(sin(alpha))))));
						
							
						let vv12 type: float value:0.0; 
						set vv12 value: (((x1_to - x1) * (x2 - x1)) + ((y1_to - y1) * (y2 - y1))); 
						let length_v1 type: float value: 0.0;
						set length_v1 value: (sqrt(((x1_to - x1) * (x1_to - x1)) + ((y1_to - y1) * (y1_to - y1))));
						let length_v2 type: float value: 0.0;
						set length_v2 value: (sqrt(((x2 - x1) ^ 2) + ((y2 - y1) ^ 2))) + 1.0;
								
							
						let beta type: float value: acos( ((vv12) / ( length_v1 * length_v2 )));
						
						// Calculating the amount of BPHs:
						
						let out_amount type: float value: rnd (FO);
						set the_source_cell.out_number_of_BPHs value: the_source_cell.out_number_of_BPHs + out_amount;
						set the_destination_cell.in_number_of_BPHs value: the_destination_cell.in_number_of_BPHs + out_amount;
						set FO value: FO - out_amount;
						
						if(beta < 90)
						{
							ask target: the_destination_cell
							{
								set number_of_BPHs value: (number_of_BPHs + in_number_of_BPHs) - out_number_of_BPHs;
								set in_number_of_BPHs value: 0;
								set out_number_of_BPHs value:0;
								do action: setcolor;
							}
						}
						 
					}
				}
			}
	}
	
	
	action dispatchByWind2
	{
		loop var: i from: 0 to: (COLUMNS_NO * ROWS_NO) - 1
		{
			do action: propagateInSemiRound
			{
				arg source_cell value: cellula_automata at i;
			}
		}
		
		loop var: i from: 0 to: (COLUMNS_NO * ROWS_NO) - 1
		{
			
			ask target: cellula_automata at i
			{
				set number_of_BPHs value: number_of_BPHs + in_number_of_BPHs - out_number_of_BPHs;
				set in_number_of_BPHs value: 0.0;
				set out_number_of_BPHs value: 0.0;
				set number_of_movable_BPHs value: number_of_BPHs * attractive_index;
				do action: setcolor;
			}
		}
		
	}
	
	//
	// Propagation model: Applied for one cell
	// Built date: June 05, 2012
	//
	action propagateInSemiRound
	{
		// ARGUMENTS:
		arg source_cell type: cellula_automata;
		
		
		// INTERNAL VARIABLES:
		let alpha type: float value: current_udg.alpha;
		
		//let cell_list type: list value: (list species cellula_automata)  where ((cellula_automata (each) distance_to source_cell) <= DISK_RADIUS) ;
		let cell_list type: list value: (list (cellula_automata))  where ((cellula_automata (each) distance_to source_cell) <= DISK_RADIUS) ;
		
		
		let cell_list_under_wind type: list value: cell_list; 
		
		// OPERATIONS:
		set sum_attractive_index value: 0.0;
		set sum_hinder_index value: 0.0;
		set _count value: _count + 1;
		
		loop var: i from: 0 to: length(cell_list) - 1
		{
			let the_neighbour_cell type: cellula_automata value: cellula_automata (cell_list at i);
				
			set x1  value: (float(((source_cell) . location) . x));  
			set y1  value: (float(((source_cell) . location) . y));						
			set x2  value: (float(((the_neighbour_cell) . location) . x));  
			set y2  value: (float(((the_neighbour_cell) . location) . y));
						
			// Calculating the maximum location:
			set x1_to  value: (float (x1 + float(DISK_RADIUS * float(float (cos(alpha))))));
			set y1_to value: (float (y1 + float(DISK_RADIUS * float(float(sin(alpha))))));
										
			let vv12 type: float value:0.0; 
			set vv12 value: (((x1_to - x1) * (x2 - x1)) + ((y1_to - y1) * (y2 - y1))); 
			let length_v1 type: float value: 0.0;
			set length_v1 value: (sqrt(((x1_to - x1) * (x1_to - x1)) + ((y1_to - y1) * (y1_to - y1))));
			let length_v2 type: float value: 0.0;
			set length_v2 value: (sqrt(((x2 - x1) ^ 2) + ((y2 - y1) ^ 2))) + 1.0; // + 1.0: Avoide the "Divide by zero" error!
								
			let beta type: float value: acos( ((vv12) / ( length_v1 * length_v2 )));
			if(beta <= 90 )
			{
				// Updating the sum value of attractive/hinder indices:
				set sum_attractive_index value: sum_attractive_index + the_neighbour_cell.attractive_index;
				set sum_hinder_index value: sum_hinder_index + the_neighbour_cell.hinder_index;
			}
			else
			{
				set cell_list_under_wind value: cell_list_under_wind - the_neighbour_cell;
			}
		}
		
		// Reset the densities:
		loop var: i from: 0 to: length(cell_list_under_wind) - 1
		{
			let the_under_cell type: cellula_automata value: cellula_automata (cell_list_under_wind at i);
			ask target: the_under_cell
			{
				set in_number_of_BPHs value: in_number_of_BPHs + source_cell.number_of_movable_BPHs * (attractive_index / sum_attractive_index);
			}				
		}

		ask target: source_cell
		{
			set out_number_of_BPHs value: number_of_movable_BPHs;
		}			
					
	}
	
	action getCorrelationTwoDays
	{

		let vectorX type: list  value: [0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0];
		let vectorY type: list  value: [0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0];
		
		
		loop var: i from: 0 to: length (node) - 1
		{ 
			ask node at i
			{
				put item: float(density_matrix at {0, simStep}) at: i in: vectorX;
				put item: float(density_matrix at {0, simStep+1}) at: i in: vectorY;
			}
		}
			
		set twoDaysCorrelation value: float(corR(vectorX, vectorY));
		set twoDaysCorrelationAVG value: (twoDaysCorrelationAVG * simStep + twoDaysCorrelation)  / (simStep + 1);   
		//set simStep value: simStep + 1;
	}
	
	action getsCorrelationByEdges
	{
		let vectorX type: list value: [0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0];
		let vectorY type: list value: [0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0];
		
		
		loop var: i from: 0 to: length (edge) - 1
		{ 
			ask edge at i
			{
				let source_node type: node value: source;
				let destination_node type: node value: destination;
				ask source_node
				{
					put item: float(density_matrix at {0, simStep}) at: i in: vectorX;
					//set source_density value: density_matrix at {0, simStep}; 
				}
				ask destination_node
				{
					put item: float(density_matrix at {0, simStep}) at: i in: vectorY;
					//set destination_density value: density_matrix at {0, simStep + 1};
				}
				
			}
		}
		
		set edgesCorrelation value: float(corR(vectorX, vectorY));
		set edgesCorrelationAVG value: (edgesCorrelationAVG * simStep + edgesCorrelation)  / (simStep + 1);   
		//set simStep value: simStep + 1;
	}
	
	
}

environment bounds: SHAPE_ADMINISTRATIVE_THREE_PROVINCES //SHAPE_ADMINISTRATIVE_PROVINCE  
{
	grid cellula_automata width: 60 height: 60 neighbours: 8
	{
		const id type: string ;
		const name type: string ;
		var square_area type: float ;
					
		var attractive_index type: float init: 0.001 ;
		var hinder_index type: float init: 0.001 ;
		var transplantation_index type: float init: 0.0 ;
		
		var number_of_BPHs type: float init: 0.0 ;
		var regression_count type: int init: 0 ;
		
		// PROPAGATION VARIABLES
		var number_of_movable_BPHs type: float init: 0.0 ; // Depending on the local conditions (Determined by the attractive and hinder indices)
		var out_number_of_BPHs type: float init: 0.0 ;
		var in_number_of_BPHs type: float init: 0.0 ;
		
		var color type: rgb init: rgb('white') ;
						
		init
		{
			do action: discretizeHinderAndAttractiveIndices;
		}

		action discretizeHinderAndAttractiveIndices{
			
			// Overlapping with transplantation area (From transplantation map):
			let rice_area type: float value: 0.0;
			
			loop var: i from: 0 to: length(rice_region) - 1
			{
				let the_rice_region type: rice_region value: rice_region at i;
				if self intersects the_rice_region
				{
					set rice_area value: 1.0;
				}
			}
			
			// Temperature:
			let Temp_Mean type: float value: 0.0; 
			let Temp_Max type: float value: 0.0; 
			let Temp_Min type: float value: 0.0; 
			
			// Rainning
			let Rain_Amount type: float value: 0.0; 
			let Rain_Max type: float value: 0.0; 
			let Rain_No_Days_Max type: float value: 0.0; 
			let Rain_No_Days type: float value: 0.0; 
			let Rain_Mean type: float value: 0.0; 
			
			// Humidity
			let Hum_Mean type: float value: 0.0; 
			let Hum_Min type: float value: 0.0; 
			let Hum_No_Days type: float value: 0.0; 
			
			// Sunning
			let Sunning_Hours type: float value: 0.0; 
			let Sunning_Hours_Mean type: float value: 0.0; 
			
			// Wind velocity:
			let Mean_Wind_Velocity type: float value: 0.0; 
			let Min_Wind_Velocity type: float value: 0.0; 
			let Max_Wind_Velocity type: float value: 0.0; 
			
			// Wind direction:
			let Wind_Direction_From type: float value: 0.0; 
			let Wind_Direction_To type: float value: 0.0; 
			
			loop var: i from: 0 to: length(weather_region) - 1
			{
				let the_weather_region type: weather_region value: weather_region at i;
				if self intersects the_weather_region
				{
					// Temperature:
					set Temp_Mean  value: the_weather_region.Temp_Mean at (sim_Month - 1); 
					set Temp_Max  value: the_weather_region.Temp_Max at (sim_Month - 1); 
					set Temp_Min  value: the_weather_region.Temp_Min at (sim_Month - 1); 
							
					// Rainning
					set Rain_Amount  value: the_weather_region.Rain_Amount at (sim_Month - 1); 
					set Rain_Max  value: the_weather_region.Rain_Max at (sim_Month - 1); 
					set Rain_No_Days_Max  value: the_weather_region.Rain_No_Days_Max at (sim_Month - 1); 
					set Rain_No_Days  value: the_weather_region.Rain_No_Days at (sim_Month - 1); 
					set Rain_Mean  value: the_weather_region.Rain_Mean  at (sim_Month - 1); 
							
					// Humidity
					set Hum_Mean  value: the_weather_region.Hum_Mean  at (sim_Month - 1); 
					set Hum_Min  value: the_weather_region.Hum_Min  at (sim_Month - 1); 
					set Hum_No_Days  value: the_weather_region.Hum_No_Days  at (sim_Month - 1); 
							
					// Sunning
					set Sunning_Hours  value: the_weather_region.Sunning_Hours  at (sim_Month - 1); 
					set Sunning_Hours_Mean  value: the_weather_region.Sunning_Hours_Mean  at (sim_Month - 1); 
				}
			}
			
			
			// Set up the general weather parameters (homogeneous):
			let Mean_Wind_Speed type: float value: current_natural_environment.Mean_Wind_Speed;
			let Min_Wind_Speed type: float value: current_natural_environment.Min_Wind_Speed;
			let Max_Wind_Speed type: float value: current_natural_environment.Max_Wind_Speed;
			let Wind_Direction_From type: float value: current_natural_environment.Wind_Direction_From;
			let Wind_Direction_To type: float value: current_natural_environment.Wind_Direction_To;
			
			if condition: (rice_area = 0.0) //(!in_land)  
			{
				set hinder_index value: 1.0;
				set attractive_index value: 0.05;
			}
			else
			{
				set hinder_index value: 0.001;
				set attractive_index value: 0.7;
			}
		}
		
		
		action setcolor {
			set color value: (number_of_BPHs > 1000000)?rgb([0,0,0]):        
					((number_of_BPHs > 750000)?rgb([20,0,0]):        
					((number_of_BPHs > 500000)?rgb([38,0,0]):        
					((number_of_BPHs > 250000)?rgb([58,0,0]):        
					((number_of_BPHs > 100000)?rgb([76,0,0]):        
					((number_of_BPHs > 75000)?rgb([96,0,0]):        
					((number_of_BPHs > 50000)?rgb([102,0,0]):        
					((number_of_BPHs > 25000)?rgb([130,0,0]):        
					((number_of_BPHs > 10000)?rgb([160,0,0]):        
					((number_of_BPHs > 7500)?rgb([208,0,0]):        
					((number_of_BPHs > 5000)?rgb([178,0,0]):        
					((number_of_BPHs > 2500)?rgb([255,7,7]):        
					((number_of_BPHs > 1000)?rgb([255,37,37]):        
					((number_of_BPHs > 750)?rgb([255,65,65]):        
					((number_of_BPHs > 500)?rgb([255,93,93]):        
					((number_of_BPHs > 250)?rgb([255,123,123]):        
					((number_of_BPHs > 100)?rgb([255,151,151]):        
					((number_of_BPHs > 75)?rgb([255,181,181]):        
					((number_of_BPHs > 50)?rgb([255,209,209]):        
					((number_of_BPHs > 25)?rgb([255,237,237]):
					rgb([255,255,255])))))))))))))))))))) ;
		}
		
		
	}
}


output {
	display GlobalView {
		grid cellula_automata transparency: 0;
		species rice_region transparency: 0.7;
		species district_region transparency: 0.8 ;
		species province_region transparency: 0.8 ;
		//species UnitDiskGraph transparency: 0.5 ;
		species node  aspect: default transparency: 0;
		species edge aspect: default  transparency: 0 ;
		species sea_region aspect: default  transparency: 0 ;
		//species weather_region aspect: default  transparency: 0.5 ;
		
	}
	
	display MovingCorrelationChart_Graph
	{
		chart name: "BPHs density via days (from dd/mm/yyyy)" type: "series"
		{
			data name: "Correlation between all two consecutive days" value: twoDaysCorrelation color: rgb('blue');
			data name: "Accumulative average of correlation" value: twoDaysCorrelationAVG color: rgb('red');
		}
	}
	
	display EdgesCorrelationChart_Graph
	{
		chart name: "BPHs density via days (from dd/mm/yyyy)" type: "series"
		{
			data name: "Correlation between all edges" value: edgesCorrelation color: rgb('blue');
			data name: "Accumulative average of correlation" value: edgesCorrelationAVG color: rgb('red');
		}
	}
	
	
	monitor Number_of_edges value: length(edge as list) refresh_every: 1 ;
	monitor Distance_node_node value: (node at 0) distance_to (node at 1) refresh_every: 1 ;
	monitor Number_Of_Node value: no_of_nodes;
	monitor Number_Of_Edge value: length (edge);
	
	monitor SimStep value: simStep refresh_every: 1;
	monitor SOURCE_DENSITY value: source_density refresh_every: 1;
	monitor DESTINATION_DENSITY value: destination_density refresh_every: 1;
	monitor RICE_REGION_LENGTH value: length(rice_region);
	monitor VORONOI value: temp;
	monitor ID_DATA value: _id_Data;
	monitor ID value: _id;
	monitor COUNT value: _count;
	monitor COORRE value: corR([2, 3,4], [2, 3, 5]);
	//monitor TEMP_MIN value: current_natural_environment.Min_Wind_Speed;
	//monitor TEMP_MAX value: current_natural_environment.Min_Wind_Speed;
	
	
}

	


