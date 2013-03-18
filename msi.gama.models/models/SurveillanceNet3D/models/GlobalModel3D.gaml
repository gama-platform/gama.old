/*
 * 
 * 
 *  Insect Surveillance Network Model
 *  Author: Truong Xuan Viet
 *  Last Modified Date: 04-12-2012 
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
	var lighttrap_data type: matrix value: matrix (file ('../includes/datasources/lighttrap_data_2010_Correlation_Var.csv'));
	
	// Map of standard deviation: 
	var standard_deviation_data type: matrix value: matrix (file ('../includes/datasources/stdDeviation.csv'));
	
	
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
	var growthToken type: int value: 0;
	
	// Simulation step:
	//var simStep type: int init: 0;
	var vectorX type: list value: nil;
	var vectorY type: list value: nil;
	
	
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
	var _id_Data type: string init: "0";
	var _id type: string init: "0";
	var _count type: int init: 0;
	
	init{
		create species: district_region from: SHAPE_ADMINISTRATIVE_DISTRICT with: [id_1 :: read('ID_1'), region_name :: read('NAME_1'), id_2 :: read('ID_2'), province_name :: read('NAME_2'), district_name :: read('NAME_3')];
	    
	    /*let SQLDISTRICT type:string <- ' select id_1,id_2,id_3,name_1,name_2,name_3,geom.STAsBinary() as geo from VNM_district';	
		create species: db{
			create species:district_region 
				from: list(self select [params:: PARAMS, select:: LOCATIONS]) 
				with:
				[id_1 :: 'ID_1', region_name ::'NAME_1', id_2 :: 'ID_2', province_name :: 'NAME_2', district_name ::'NAME_3',shape::'geo'];	
		}*/
		
		
		
		create species: province_region from: SHAPE_ADMINISTRATIVE_PROVINCE with: [id_1 :: read('ID_1'), region_name :: read('NAME_1'), id_2 :: read('ID_2'), province_name :: read('NAME_2')];
		create species: sea_region from: SHAPE_SEA_REGION with: [description :: read('Description')];
		create species: WS_rice_region from: WS_SHAPE_LAND_USE with: [id :: read('ID'), description :: read('SDD')];
		create species: SA_rice_region from: SA_SHAPE_LAND_USE with: [id :: read('ID'), description :: read('SDD')];
		create species: node from: SHAPE_NODE with: [id :: read('ID'), name :: read('LightTrap'), district_name :: read('District'), province_name :: read('Province'), id_0 :: read('ID_0'), id_1 :: read('ID_1'), id_2 :: read('ID_2')];
		create species: weather_region from: SHAPE_WEATHER with: [id :: read('ID'), name :: read('NAME')];
		
		// UDG Species
		create species: UnitDiskGraph number: 1
		{
			set no_of_nodes value: length(node);
		}
		
		set current_udg value: UnitDiskGraph at 0;
		
		
		// Natural Environment species:
		create species: NaturalEnvironment number: 1;
		set current_natural_environment value: NaturalEnvironment at 0;
	}

	reflex main_reflex {
		
		if condition: (current_udg.setup = 0)
		{
			do action: loadLighttrapData;
			do action: loadStandardDeviation;
			//do action: loadStandardDeviationR;
			do action: loadGeneralWeatherData;
			do action: loadStationWeatherData;
			ask target: current_udg
			{
				do action: resetEdgesList;
			}
			do action: estimate_IDW_Correlation;
			
			// Surface of standard deviation 
			ask cellula_std_deviation as list
			{		
				let cells_possibles type: list of: cellula_std_deviation <- (self neighbours_at 2) + self;
			
				loop i from: 0 to: length(shape.points) - 1
				{ 
					let geom type: geometry <- square(1.0);
					set geom <- geom translated_to (shape.points at i);
					let myCells type: list of: cellula_std_deviation <- cells_possibles where (each.shape intersects geom);
					write "z value: " + z;
					let z1 type: float <- mean (myCells collect (each.z));
					set shape <- shape add_z_pt {i, (z1^2)};
				}
			}
		}
		// Calculating the correlation:
		if (SIMULATION_STEP < BPH_LIFE_DURATION)
		{
			do action: getsCorrelationByEdges;
			
			do action: getCorrelationTwoDays;
			do action: estimate_IDW_Density;
			
			do action: estimate_IDW_by_Day
			{
				arg estimated_day value: SIMULATION_STEP; 
			}
			
			// Surface of environmental hinder:
			ask cellula_automata as list
			{		
				let cells_possibles type: list of: cellula_automata <- (self neighbours_at 2) + self;
			
				loop i from: 0 to: length(shape.points) - 1
				{ 
					let geom type: geometry <- square(1.0);
					set geom <- geom translated_to (shape.points at i);
					let myCells type: list of: cellula_automata <- cells_possibles where (each.shape intersects geom);
					write "z value: " + z;
					let z1 type: float <- mean (myCells collect (each.z));
					set shape <- shape add_z_pt {i, (z1^2)};
				}
			}
			
			
		}
		//else if (SIMULATION_STEP = 32)
		//{
			//do action: estimate_IDW_Density;
			//do action: estimate_IDW_Correlation;
		//}
		else
		{
			/*
			ask target: current_udg
			{
				loop from: 0 to: length(node) - 1 var: i
				{
					let the_node type: node value: node at i; 
					do allocateNewNodeByStdDeviation
					{
						arg center_node value: the_node;
					}
				}
			}
			* 
			*/
			do action: optimize_network_by_addition
			{
				arg added_number value: NUMBER_OF_ADDED_NODES; 				
			}
			
			//do action: dispatchByWind;
			
			do action: halt;
		}
		
		set SIMULATION_STEP value: SIMULATION_STEP + 1;
	}

	// Action: optimize_network_by_addition
	// Optimizing the surveillance network by addition
	
	action optimize_network_by_addition
	{
		arg added_number type: int;
		
		let min_degree type: int value: (list (node)) min_of (each.degree);
		let max_degree type: int value: (list (node)) max_of (each.degree);
		
		
		// Controlling the placement:  
		let number type: int value: added_number;
		let all_allocated type: bool value: false;
		
		let the_degree type: int value: min_degree;
		loop while: !all_allocated 
		{
			let node_list type: list value: (list (node)) where (each.degree = the_degree);
			
			write "Degree: " + string(the_degree);
			write "No of nodes: " + string(length(node_list));
			
			if(number >= length(node_list))
			{
				set number value: number - length(node_list);
				ask target: current_udg
				{
					loop from: 0 to: length(node_list) - 1 var: i
					{
						let the_node value: node at i;
						do allocateNewNodeByStdDeviation
						{
							arg center_node value: the_node;
						}
					}
				}
				
				// The next loop for placing the node:
				if(the_degree = max_degree)
				{
					set min_degree value: (list (node)) min_of (each.degree);
					set max_degree value: (list (node)) max_of (each.degree);
					set the_degree value: min_degree;
				}
				
			}
			else
			{
				if(number != 0)
				{
					
					write "Degree: " + string(the_degree);
					write "No of nodes: " + string(number);
					
					ask target: current_udg
					{
						loop from: 0 to: number - 1 var: i
						{
							let the_node value: node at i; 
							do allocateNewNodeByStdDeviation
							{
								arg center_node value: the_node;
							}
						}
					}
				}
				
				// Exit
				set number value: 0;
				set all_allocated value: true;
			}
			
			set the_degree value: the_degree + 1;
		}
	}
	 	
		
	// Loading the lighttrap data from the CSV file:
	action loadLighttrapData
	{
		let no_of_rows value: lighttrap_data.rows - 1;
		loop cnt from: 0 to: length (node) - 1  {
			let the_node type: node value: node at cnt;
			 
			ask target: the_node
			{
				loop i from: 1 to: no_of_rows
				{
					if condition: (id = lighttrap_data at {2, i})
					{
						loop j from: 0 to: (HISTORICAL_DURATION - 1) 
						{
							put lighttrap_data at {3 + j, i} at: {0, j} in: density_matrix;
						}
						
						// Choosing the first day for estimation & prediction 
						set number_of_BPHs value: lighttrap_data at {31, i};
						set number_of_BPHs_total value: lighttrap_data at {368, i};
						set correlation_coefficient value: lighttrap_data at {369, i};
						set sample_variance value: lighttrap_data at {374, i};
					}
				}
			}
		}
	}
	
	// Loading the map of standard deviation from the CSV file:
	action loadStandardDeviation
	{
		loop from: 1 to: 60 var: i
		{
			loop from: 1 to: 60 var: j
			{
			
				ask target: cellula_std_deviation at ((j - 1) * 60 + (i - 1))
				{
					set estimation_std_deviation <-  standard_deviation_data at {1, ((60 - j) * 60 + (i - 1)) + 1};
					do aspect_by_std_deviation;
				}
			}
		}
	}
	
	// Loading the map of standard deviation from the CSV file:
	action loadStandardDeviationR
	{
		map result <- nil;
		list rs <- nil;
		result <- R_compute("D:/PSN-Simulation/RCaller/RGama/KrigingCutted.R");
		
		rs <- result['result'];
		loop from: 1 to: 60 var: i
		{
			loop from: 1 to: 60 var: j
			{
			
				ask target: cellula_std_deviation at ((j - 1) * 60 + (i - 1))
				{
					set estimation_std_deviation <- rs at (((60 - j) * 60 + (i - 1)));
					do aspect_by_std_deviation;
				}
			}
		}
	}
	
	
	// Loading the general weather data from the CSV file:
	action loadGeneralWeatherData
	{
		let no_of_months value: 12;
		ask target: current_natural_environment{ 
			loop i from: 0 to: (no_of_months - 1)
			{
				put genaral_weather_data at {i + 1, 0} at: i in: Mean_Wind_Speed;
				put genaral_weather_data at {i + 1, 1} at: i in: Min_Wind_Speed;
				put genaral_weather_data at {i + 1, 2} at: i in: Max_Wind_Speed;
				put genaral_weather_data at {i + 1, 3} at: i in: Wind_Direction_From;
				put genaral_weather_data at {i + 1, 4} at: i in: Wind_Direction_To;
			}
		}
	}
	
	// Loading the station weather data from th CSV file (Voronoi polygons):
	action loadStationWeatherData
	{
		let no_of_months value: 12;
		let no_of_stations value: length (weather_region);
		
		loop j from: 0 to: no_of_stations - 1 {
			let the_weather_region type: weather_region value: weather_region at j;
			ask target: the_weather_region{
				loop i from: 1 to: (no_of_months * no_of_stations) {
					if condition: the_weather_region.id = (station_weather_data at {0, i})
					{
						set _month  value: int (station_weather_data at {3, i});
						// Temperature:
						put station_weather_data at {5, i} at: _month - 1 in: Temp_Mean;
						put station_weather_data at {6, i} at: _month - 1 in: Temp_Max;
						put station_weather_data at {7, i} at: _month - 1 in: Temp_Min;
						
						// Rainning
						put station_weather_data at {8, i} at: _month - 1 in: Rain_Amount;
						put station_weather_data at {9, i} at: _month - 1 in: Rain_Max;
						put station_weather_data at {10, i} at: _month - 1 in: Rain_No_Days_Max;
						put station_weather_data at {11, i} at: _month - 1 in: Rain_No_Days;
						put station_weather_data at {12, i} at: _month - 1 in: Rain_Mean;
								
						// Humidity
						put station_weather_data at {13, i} at: _month - 1 in: Hum_Mean;
						put station_weather_data at {14, i} at: _month - 1 in: Hum_Min;
						put station_weather_data at {15, i} at: _month - 1 in: Hum_No_Days;
								
						// Sunning
						put station_weather_data at {16, i} at: _month - 1 in: Sunning_Hours;
						put station_weather_data at {17, i} at: _month - 1 in: Sunning_Hours_Mean;
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
	action estimate_IDW_Density 
	{
		let is_sampled_point type: float value: 0.0 ;
		let distance type: float value: 0.0 ;
		set no_of_nodes value: length (node);
		
		let cnt type: int value: 0;
		loop cnt  from: 0 to: no_of_nodes - 1 {
			
			put  0.0 at: {0, cnt} in: orthogonal_vector;
			put  0.0 at: {1, cnt} in: orthogonal_vector;
			put  0.0 at: {2, cnt} in: orthogonal_vector;
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
					//set the_node.dominated_cell value: the_cell;
				}
				
				if(the_node intersects the_cell)
				{
					ask target: the_node
					{
						set dominated_cell value: the_cell;
						ask target: dominated_cell
						{
							set is_monitored value: true;
						}
					} 
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
					/*if(hinder_index != 1.0)
					{
						set number_of_BPHs value: estimated_value;
					}
					else
					{
						set number_of_BPHs value: estimated_value * attractive_index;	
					}*/
					set number_of_BPHs value: estimated_value * attractive_index;
					
					// Mouvable amount in the next simulation step:
					set number_of_movable_BPHs value: (1.0 - attractive_index) * number_of_BPHs;
					
					do action: setcolor ;
				}
			}
				
		}
		
	}
	
	
	//
	// Action: estimate_IDW_by_Day
	// Purpose: Estimating the BPH density from the node's density matrix to the grid's density matrix 
	// Parameters:
	// (1) estimated_day: Estimated day
	// Built date: November 30, 2012
	//

	action estimate_IDW_by_Day
	{
		arg estimated_day type: int; // The day is standardized as the index!
		
		let is_sampled_point type: float value: 0.0 ;
		let distance type: float value: 0.0 ;
		set no_of_nodes value: length (node);
		
		let cnt type: int value: 0;
		loop var: cnt from: 0 to: no_of_nodes - 1 {
			put item: 0.0 at: {0, cnt} in: orthogonal_vector;
			put item: 0.0 at: {1, cnt} in: orthogonal_vector;
			put item: 0.0 at: {2, cnt} in: orthogonal_vector;
		} 
		
		set no_of_nodes value: length (node);
		let mynode type: node value: nil;
		
		loop var: i from: 0 to: no_of_nodes - 1 {
			ask target: node at i
			{
				set number_of_BPHs_by_day value: float(density_matrix at {0, estimated_day});
				set number_of_BPHs value: float(density_matrix at {0, estimated_day});
				do action: setcolor; 
			}
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
				
			 	set distance value: the_cell distance_to the_node;
				if (distance < 1.0) {
					set is_sampled_point value: 1.0 ;

					//set estimated_value value: the_node.number_of_BPHs ;
					set estimated_value value: the_node.number_of_BPHs_by_day;
				}
				
				if(the_node intersects the_cell)
				{
					ask target: the_node
					{
						set dominated_cell value: the_cell;
					} 
				}
				
				put item: distance at: {0, orthogonal_vector_index} in: orthogonal_vector;
				
				//put item: the_node.number_of_BPHs at: {1, orthogonal_vector_index} in: orthogonal_vector;
				put item: the_node.number_of_BPHs_by_day at: {1, orthogonal_vector_index} in: orthogonal_vector;
				
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
					set number_of_BPHs value: estimated_value * attractive_index;
					
					put item: number_of_BPHs at: {0, estimated_day} in: grid_density_matrix;  
					
					// Mouvable amount in the next simulation step:
					set number_of_movable_BPHs value: (1.0 - attractive_index) * number_of_BPHs;
					
					do action: setcolor ;
				}
			}
				
		}
		
	}
	
	//
	// Action: get_number_of_BPH_by_Day 
	// Purpose: Getting the BPH density from matrix 
	// Parameters:
	// (1) estimated_day: Monitored day
	// Built date: November 30, 2012
	//

	action get_number_of_BPH_by_Day 
	{
		arg monitored_day type: int; // The day is standardized as the index! 
		
		let the_node type: node value: nil;
		loop var: cnt from: 0 to: no_of_nodes - 1 {
			set the_node value: node at cnt;
			let number_of_BPHs_by_day value: 0.0;
			ask target: the_node
			{
				set number_of_BPHs_by_day value: float(density_matrix at {0, monitored_day - 1}); 
			}
		}
	}
	
	// LOAD ATTRACTIVE/HINDER INDICES:
	action loadIndices
	{
		loop from: 0 to: length (cellula_automata) - 1 var: cnt {
			let the_cell type: cellula_automata value: cellula_automata (cellula_automata at cnt);
			let the_correlation_cell type: cellula_correlation value: cellula_correlation (cellula_correlation at cnt);
			set the_correlation_cell.attractive_index value: the_cell.attractive_index;
			set the_correlation_cell.hinder_index value: the_cell.hinder_index;
		}
	}
	
	
	// INVERSE DISTANCE WEIGHTING ESTIMATION (CORRELATION):
	action estimate_IDW_Correlation 
	{
		do loadIndices;
		
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
		loop from: 0 to: length (cellula_correlation) - 1 var: cnt {
			let the_cell type: cellula_correlation value: cellula_correlation at cnt;
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
					set estimated_value value: the_node.correlation_coefficient;
				}
				if(the_node intersects the_cell)
				{
					ask target: the_node
					{
						set dominated_cell_correlation value: the_cell;
					} 
				}
				
				put item: distance at: {0, orthogonal_vector_index} in: orthogonal_vector;
				put item: the_node.correlation_coefficient at: {1, orthogonal_vector_index} in: orthogonal_vector;
			
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
					
					//set number_of_movable_BPHs value: (1.0 - attractive_index) * number_of_BPHs;
					
					
					set correlation_coefficient value: estimated_value;
					do action: setcolor ;
				}
			}
				
		}
	}
	
	
	action estimate_IDW_Variance 
	{
		do loadIndices;
		
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
		loop from: 0 to: length (cellula_correlation) - 1 var: cnt {
			let the_cell type: cellula_correlation value: cellula_correlation at cnt;
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
					set estimated_value value: the_node.sample_variance;
				}
				if(the_node intersects the_cell)
				{
					ask target: the_node
					{
						set dominated_cell_correlation value: the_cell;
					}
				}
				
				put item: distance at: {0, orthogonal_vector_index} in: orthogonal_vector;
				put item: the_node.sample_variance at: {1, orthogonal_vector_index} in: orthogonal_vector;
				
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
					
					//set number_of_movable_BPHs value: (1.0 - attractive_index) * number_of_BPHs;
					set sample_variance value: estimated_value;
					do action: setcolor_as_variance;
				}
			}
				
		}
	}
	
	//
	// ACTION: dispatchByWind
	// Built date: June 05, 2012
	//
	action dispatchByWind
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
		let vectorX type: list  value: nil;
		let vectorY type: list  value: nil;
		
		
		loop var: i from: 0 to: length (node) - 1
		{ 
			ask node at i
			{
				set vectorX value: vectorX + [float(density_matrix at {0, SIMULATION_STEP})];
				set vectorY value: vectorY + [float(density_matrix at {0, SIMULATION_STEP+1})];
				//put item: float(density_matrix at {0, simStep}) at: i in: vectorX;
				//put item: float(density_matrix at {0, simStep+1}) at: i in: vectorY;
			}
		}
			
		set twoDaysCorrelation value: float(corR(vectorX, vectorY));
		set twoDaysCorrelationAVG value: (twoDaysCorrelationAVG * SIMULATION_STEP + twoDaysCorrelation)  / (SIMULATION_STEP + 1);   
		//set simStep value: simStep + 1;
	}
	
	action getsCorrelationByEdges
	{
		let vectorX type: list value: nil;
		let vectorY type: list value: nil;
		
		
		loop var: i from: 0 to: length (edge) - 1
		{ 
			ask edge at i
			{
				let source_node type: node value: source;
				let destination_node type: node value: destination;
				ask source_node
				{
					set vectorX value: vectorX + [float(density_matrix at {0, SIMULATION_STEP})];
					
					//put item: float(density_matrix at {0, simStep}) at: i in: vectorX;
				}
				ask destination_node
				{
					set vectorY value: vectorY + [float(density_matrix at {0, SIMULATION_STEP})];
					//put item: float(density_matrix at {0, simStep}) at: i in: vectorY;
				}
				
			}
		}
		
		set edgesCorrelation value: float(corR(vectorX, vectorY));
		set edgesCorrelationAVG value: (edgesCorrelationAVG * SIMULATION_STEP + edgesCorrelation)  / (SIMULATION_STEP + 1);   
		//set simStep value: simStep + 1;
	}
}

environment bounds: SHAPE_ADMINISTRATIVE_THREE_PROVINCES //SHAPE_ADMINISTRATIVE_PROVINCE  
{
	grid cellula_std_deviation width: 60 height: 60 neighbours: 8
	{
		var estimation_std_deviation type: float init: 0.001;
		var color type: rgb init: rgb('white') ;
		var maximum_std_deviation type: float init: 2200.0;
		var z type: float init: 0.0;
		
		aspect elevation{
			draw shape color: color;
		}
		
		
		action aspect_by_std_deviation {
					set color value: (estimation_std_deviation > 0.80 * maximum_std_deviation)?rgb([255,0,0]):        
					((estimation_std_deviation > 0.75 * maximum_std_deviation)?rgb([255,30,17]):        
					((estimation_std_deviation > 0.60 * maximum_std_deviation)?rgb([255,55,29]):        
					((estimation_std_deviation > 0.55 * maximum_std_deviation)?rgb([255,80,41]):        
					((estimation_std_deviation > 0.50 * maximum_std_deviation)?rgb([255,105,53]):        
					((estimation_std_deviation > 0.45 * maximum_std_deviation)?rgb([255,130,65]):        
					((estimation_std_deviation > 0.40 * maximum_std_deviation)?rgb([255,155,77]):        
					((estimation_std_deviation > 0.38 * maximum_std_deviation)?rgb([255,180,89]):        
					((estimation_std_deviation > 0.36 * maximum_std_deviation)?rgb([255,205,101]):        
					((estimation_std_deviation > 0.34 * maximum_std_deviation)?rgb([255,230,113]):        
					((estimation_std_deviation > 0.32 * maximum_std_deviation)?rgb([255,255,125]):        
					((estimation_std_deviation > 0.30 * maximum_std_deviation)?rgb([230,230,137]):        
					((estimation_std_deviation > 0.28 * maximum_std_deviation)?rgb([205,205,149]):        
					((estimation_std_deviation > 0.26 * maximum_std_deviation)?rgb([180,180,161]):        
					((estimation_std_deviation > 0.24 * maximum_std_deviation)?rgb([155,155,173]):        
					((estimation_std_deviation > 0.22 * maximum_std_deviation)?rgb([130,130,185]):        
					((estimation_std_deviation > 0.20 * maximum_std_deviation)?rgb([105,105,197]):        
					((estimation_std_deviation > 0.15 * maximum_std_deviation)?rgb([80,80,209]):        
					((estimation_std_deviation > 0.10 * maximum_std_deviation)?rgb([55,55,221]):        
					((estimation_std_deviation > 0.05 * maximum_std_deviation)?rgb([30,30,233]):
					rgb([0, 0,255])))))))))))))))))))) ;
					
					// 3D model
					set z value: (estimation_std_deviation/maximum_std_deviation) * 300;
		}
	}
	
	grid cellula_correlation width: 60 height: 60 neighbours: 8
	{
		const id type: string ;
		const name type: string ;
							
		var correlation_coefficient type: float init: 0.001;
		var sample_variance type: float init: 0.001;
		
		var color type: rgb init: rgb('white') ;
		
		
		// TWO PRINCIPAL INDICES 			
		var attractive_index type: float init: 0.001 ;
		var hinder_index type: float init: 1.0 ;
		
		
		action setcolor {
					set color value: (correlation_coefficient > 0.80)?rgb([255,0,0]):        
					((correlation_coefficient > 0.75)?rgb([255,30,17]):        
					((correlation_coefficient > 0.60)?rgb([255,55,29]):        
					((correlation_coefficient > 0.55)?rgb([255,80,41]):        
					((correlation_coefficient > 0.50)?rgb([255,105,53]):        
					((correlation_coefficient > 0.45)?rgb([255,130,65]):        
					((correlation_coefficient > 0.40)?rgb([255,155,77]):        
					((correlation_coefficient > 0.38)?rgb([255,180,89]):        
					((correlation_coefficient > 0.36)?rgb([255,205,101]):        
					((correlation_coefficient > 0.34)?rgb([255,230,113]):        
					((correlation_coefficient > 0.32)?rgb([255,255,125]):        
					((correlation_coefficient > 0.30)?rgb([230,230,137]):        
					((correlation_coefficient > 0.28)?rgb([205,205,149]):        
					((correlation_coefficient > 0.26)?rgb([180,180,161]):        
					((correlation_coefficient > 0.24)?rgb([155,155,173]):        
					((correlation_coefficient > 0.22)?rgb([130,130,185]):        
					((correlation_coefficient > 0.20)?rgb([105,105,197]):        
					((correlation_coefficient > 0.15)?rgb([80,80,209]):        
					((correlation_coefficient > 0.10)?rgb([55,55,221]):        
					((correlation_coefficient > 0.05)?rgb([30,30,233]):
					rgb([0, 0,255])))))))))))))))))))) ;
		}
		
		action setcolor_as_variance {
					set color value: (sample_variance > (0.80 * 24))?rgb([255,0,0]):        
					((sample_variance > (0.75 * 24))?rgb([255,30,17]):        
					((sample_variance > (0.60 * 24))?rgb([255,55,29]):        
					((sample_variance > (0.55 * 24))?rgb([255,80,41]):        
					((sample_variance > (0.50 * 24))?rgb([255,105,53]):        
					((sample_variance > (0.45 * 24))?rgb([255,130,65]):        
					((sample_variance > (0.40 * 24))?rgb([255,155,77]):        
					((sample_variance > (0.38 * 24))?rgb([255,180,89]):        
					((sample_variance > (0.36 * 24))?rgb([255,205,101]):        
					((sample_variance > (0.34 * 24))?rgb([255,230,113]):        
					((sample_variance > (0.32 * 24))?rgb([255,255,125]):        
					((sample_variance > (0.30 * 24))?rgb([230,230,137]):        
					((sample_variance > (0.28 * 24))?rgb([205,205,149]):        
					((sample_variance > (0.26 * 24))?rgb([180,180,161]):        
					((sample_variance > (0.24 * 24))?rgb([155,155,173]):        
					((sample_variance > (0.22 * 24))?rgb([130,130,185]):        
					((sample_variance > (0.20 * 24))?rgb([105,105,197]):        
					((sample_variance > (0.15 * 24))?rgb([80,80,209]):        
					((sample_variance > (0.10 * 24))?rgb([55,55,221]):        
					((sample_variance > (0.05 * 24))?rgb([30,30,233]):
					rgb([0, 0,255])))))))))))))))))))) ;
					
					set shape <- shape add_z sample_variance;
		}
	}
	
	grid cellula_automata width: 60 height: 60 neighbours: 8
	{
		const id type: string ;
		const name type: string ;
		var square_area type: float ;
		
		// TWO PRINCIPAL INDICES 			
		var attractive_index type: float init: 0.001 ;
		var hinder_index type: float init: 1.0 ;
		
		// Transplantation indices:
		var based_transplantation_index type: float init: 0.0 ; // By default: grass, other plants ...
		var WS_transplantation_index type: float init: 0.0;
		var SA_transplantation_index type: float init: 0.0;
		
		// Sea & river region:
		var is_sea_region type: bool init: false;
		var is_monitored type: bool init: false;
		
			
		// Brown Plant Hopper:
		var number_of_BPHs type: float init: 0.0 ;
		var regression_count type: int init: 0 ;
		matrix grid_density_matrix size: {1, 32}; // Containing density of BPHs for all stages of life cycle
		
		// PROPAGATION VARIABLES
		var number_of_movable_BPHs type: float init: 0.0 ; // Depending on the local conditions (Determined by the attractive and hinder indices)
		var out_number_of_BPHs type: float init: 0.0 ;
		var in_number_of_BPHs type: float init: 0.0 ;
		
		var color type: rgb init: rgb('white') ;
		var _discretized type: int init: 1;
		
		var z type: float init: 0.0;
		
		aspect ThreeDirections
		{
			//let shape_to_display type: geometry <- (shape + 1.0) add_z hinder_index * 100;
			//draw geometry: shape_to_display color: rgb('red');
			draw shape color: color;
		} 
						
		reflex Step
		{
			if(_discretized = 1)
			{
				do action: discretizeHinderAndAttractiveIndices;
				set _discretized value: 2;
			}
			
			if(SIMULATION_STEP > BPH_LIFE_DURATION)
			{
				do action: growthCycle;
				do action: setcolor;
			}
			else if(SIMULATION_STEP = BPH_LIFE_DURATION)
			{
				do action: updateDensityCycle;
			}
		}

		action discretizeHinderAndAttractiveIndices{
			
			// Overlapping with transplantation area (From transplantation map):
			let is_in_land_planted type: bool value: false;
			
			loop var: i from: 0 to: length(province_region) - 1
			{
				let the_province_region type: province_region value: province_region (province_region at i);
				if self intersects the_province_region
				{
					set is_in_land_planted value: true;
				}
			}
			
			
			loop var: i from: 0 to: length(WS_rice_region) - 1
			{
				let the_rice_region type: WS_rice_region value: WS_rice_region (WS_rice_region at i);
				
				if self intersects the_rice_region
				{
					//set rice_area value: rice_area + 0.4;
					set WS_transplantation_index value: WINTER_SPRING_SEASON_COEF;
				}
			}
			
			loop var: i from: 0 to: length(SA_rice_region) - 1
			{
				let the_rice_region type: SA_rice_region value: SA_rice_region (SA_rice_region at i);
				
				if self intersects the_rice_region
				{
					//set rice_area value: rice_area + 0.4;
					set SA_transplantation_index value: SUMMER_AUTUMN_SEASON_COEF;
				}
			}
			
			// SEA REGION
			loop var: i from: 0 to: length(sea_region) - 1
			{
				let the_sea_region type: sea_region value: sea_region (sea_region at i);
				
				if self intersects the_sea_region																																															
				{
					//set rice_area value: rice_area + 0.4;
					set is_sea_region value: true;
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
			let Mean_Wind_Speed  value: current_natural_environment.Mean_Wind_Speed;
			let Min_Wind_Speed  value: current_natural_environment.Min_Wind_Speed;
			let Max_Wind_Speed  value: current_natural_environment.Max_Wind_Speed;
			let Wind_Direction_From  value: current_natural_environment.Wind_Direction_From;
			let Wind_Direction_To  value: current_natural_environment.Wind_Direction_To;
			
			/*if condition: (rice_area = 0.0) //(!in_land)  
			{
				set hinder_index value: 1.0;
				set attractive_index value: 0.05;
			}
			else
			{
				set hinder_index value: 0.001;
				set attractive_index value: 0.7;
			}*/
			
			if(is_in_land_planted)
			{
				set based_transplantation_index value: BASED_SEASON_COEF;
				set attractive_index value: based_transplantation_index + WS_transplantation_index + SA_transplantation_index;
				set hinder_index value: 1.0 - attractive_index;
			}
			
			if condition: (is_sea_region) //(!in_land)
			{
				set hinder_index value: 1.0;
			}
		}
		
		//
		// Action: updateDensityCycle
		// Built date: December 04, 2012
		//
		
 		action updateDensityCycle
		{
			loop var: i from: 0 to: BPH_LIFE_DURATION - 1
			{
				let _duration type: int value: BPH_LIFE_DURATION - (i + 1);
				let _density type: float value: grid_density_matrix at {0, i};
				let egg_duration type: int value: 0; 
				let nymph_duration type: int value: 0;  
				let adult_duration type: int value: 0;
				
				let egg_number type: float value: _density * ADULT_EGG_RATE;
				
				// Update the duration of each stage: 
				let age type: int value: _duration;
				if(age > (EGG_DURATION + NYMPH_DURATION))
				{
					set adult_duration value: age - (EGG_DURATION + NYMPH_DURATION);
					set age value: (EGG_DURATION + NYMPH_DURATION);
				}
					
				if (age > EGG_DURATION)
				{
					set nymph_duration value: age - EGG_DURATION;
					set age value: EGG_DURATION;
				}
					
				set egg_duration value: age;
					
				// Update the real density:
				set _density value: egg_number * (EGG_NYMPH_RATE ^ egg_duration);
				set _density value: _density * (NYMPH_ADULT_RATE ^ nymph_duration);
				
				// Kiem tra lai:
				
				set _density value: _density - _density * adult_duration * NATURAL_MORTALITY_RATE;
				
				put item: _density at: {0, i} in: grid_density_matrix;
			}
		}
		
		//
		// Growth model: Applied for one cell
		// Built date: December 04, 2012
		//
 		action growthCycle
		{
			let _density_sum type: float value: 0.0;
			
			loop var: i from: 0 to: BPH_LIFE_DURATION - 1
			{
				//let _density type: float value: float(grid_density_matrix at {0, i});
				let _density_previous type: float value: float(grid_density_matrix at {0, (i + 1) mod BPH_LIFE_DURATION});
				let _density type: float value: _density_previous - (_density_previous * NATURAL_MORTALITY_RATE);
				put item: _density at: {0, i} in: grid_density_matrix;
				
				// Sum all densities of adult satges (11 days):
				
				if((i >= 0) and (i <= 11))
				{
					set _density_sum value: _density_sum + _density; 
				}
			}
			
			set number_of_BPHs value: _density_sum;
		}
		
		action setcolor {
			set color value: (number_of_BPHs > 10000)?rgb([255,0,0]):        
					((number_of_BPHs > 7500)?rgb([255,30,17]):        
					((number_of_BPHs > 5000)?rgb([255,55,29]):        
					((number_of_BPHs > 2500)?rgb([255,80,41]):        
					((number_of_BPHs > 1000)?rgb([255,105,53]):        
					((number_of_BPHs > 750)?rgb([255,130,65]):        
					((number_of_BPHs > 500)?rgb([255,155,77]):        
					((number_of_BPHs > 250)?rgb([255,180,89]):        
					((number_of_BPHs > 200)?rgb([255,205,101]):        
					((number_of_BPHs > 150)?rgb([255,230,113]):        
					((number_of_BPHs > 100)?rgb([255,255,125]):        
					((number_of_BPHs > 90)?rgb([230,230,137]):        
					((number_of_BPHs > 80)?rgb([205,205,149]):        
					((number_of_BPHs > 70)?rgb([180,180,161]):        
					((number_of_BPHs > 60)?rgb([155,155,173]):        
					((number_of_BPHs > 50)?rgb([130,130,185]):        
					((number_of_BPHs > 40)?rgb([105,105,197]):        
					((number_of_BPHs > 30)?rgb([80,80,209]):        
					((number_of_BPHs > 20)?rgb([55,55,221]):        
					((number_of_BPHs > 10)?rgb([30,30,233]):
					rgb([0, 0,255])))))))))))))))))))) ;

					// 3D model
					set z value: (hinder_index) * 100;
		}

						
		
	}
}


output {
	display GlobalView {
		grid cellula_automata transparency: 0;
		species WS_rice_region transparency: 0.7;
		species district_region transparency: 0.8 ;
		species province_region transparency: 0.8 ;
		//species UnitDiskGraph transparency: 0.5 ;
		species sea_region transparency: 0 ;
		species node  aspect: default transparency: 0;
		species edge aspect: default  transparency: 0 ;
		
		//species weather_region aspect: default  transparency: 0.5 ;
		
	}
	
	display GlobalView3D type:opengl{
		species cellula_automata aspect: ThreeDirections;
		species node  aspect: ThreeDirections transparency: 0;
		species edge aspect: default  transparency: 0 ;
	}
	
	display GlobalCorrelation {
		grid cellula_correlation transparency: 0;
		species node  aspect: default transparency: 0;
		species edge aspect: default  transparency: 0 ;
		species sea_region transparency: 0.7;
	}
	
	display StandardDeviation type:opengl {
		species cellula_std_deviation aspect: elevation;
		species node  aspect: ThreeDirections transparency: 0;
		species edge aspect: default  transparency: 0 ;
	}
	
	
	display MovingCorrelationChart_Graph
	{
		chart name: "BPHs density via days (from dd/mm/yyyy)" type: series
		{
			data name: "Correlation between all two consecutive days" value: twoDaysCorrelation color: rgb('blue');
			data name: "Accumulative average of correlation" value: twoDaysCorrelationAVG color: rgb('red');
		}
	}
	
	display EdgesCorrelationChart_Graph
	{
		chart name: "BPHs density via days (from dd/mm/yyyy)" type: series
		{
			data name: "Correlation between all edges" value: edgesCorrelation color: rgb('blue');
			data name: "Accumulative average of correlation" value: edgesCorrelationAVG color: rgb('red');
		}
	}
	
	monitor NUMBER_OF_EDGES value: length(edge as list) refresh_every: 1 ;
	monitor NUMBER_OF_NODES value: no_of_nodes;
	
	monitor Simulation_Step value: SIMULATION_STEP refresh_every: 1;
	monitor SOURCE_DENSITY value: source_density refresh_every: 1;
	monitor DESTINATION_DENSITY value: destination_density refresh_every: 1;
	monitor RICE_REGION_LENGTH value: length(WS_rice_region);
	monitor VORONOI value: temp;
	monitor ID_DATA value: _id_Data;
	monitor ID value: _id;
	monitor COUNT value: _count;
	//monitor COORRE value: corR([2, 3,4], [2, 3, 5]);
	//monitor TEMP_MIN value: current_natural_environment.Min_Wind_Speed;
	//monitor TEMP_MAX value: current_natural_environment.Min_Wind_Speed;
	
	
}

	


