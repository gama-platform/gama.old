model GraphNetworkModel


import "../GlobalParam.gaml"
import "node_network.gaml"
import "edge_network.gaml"
//import "../../models/GlobalModel3D.gaml"

global {
	//var SHAPE_NODE type: string init: '../includes/gis/surveillances/provinces/DongThap_Lighttraps.shp' parameter: 'Sensors Network - DONG THAP:' category: 'SURVEILLANCE' ;
	var lasted_edge_id type: int init: 0;
	var current_edge type: edge init: nil;
	
	var vectorXX type: list value: nil;
	var vectorYY type: list value: nil;
	var correlationAVG type: float init: 0.0;
	var correlationCount type: int init: 0;
	
	
	var x1 type: float value: 0.0;  
	var y1 type: float value: 0.0;						
	var x2 type: float value: 0.0;  
	var y2 type: float value: 0.0;
	var x1_to type: float value: 0.0;
	var y1_to type: float value: 0.0;
	
	// TEST
	var testtest type: int init: 0;
	var testcor type: float init: 0.0;
	
	init{
		//create species: node from: SHAPE_NODE with: [id :: read('ID'), name :: read('LightTrap'), district_name :: read('District'), province_name :: read('Province'), id_0 :: read('ID_0'), id_1 :: read('ID_1'), id_2 :: read('ID_2')];
	}
}

entities {
	
	species UnitDiskGraph  {
		var UDgraph type: graph init: nil;
		var setup type: int init: 0;
		
		var alpha type: float init: 135.0;
		
				
		// Tempo variable used in check_simple_connection action:
		var connected_temp type: bool init: false;
		
		var working_status type: bool value: true;
		
		action allocateNewNodeByCorrelation
		{
			arg center_node type: node;
			
			let location_found type: bool value: false;
			let center_correlation type: float value: 0.0;
			let cell_correlation type: float value: 0.0;
			let potential_correlation type: float value: 0.0;
			let potential_inverse_distance type: float value: 0.0;
			let potential_condition type: float value: 0.0;			
			let the_potential_cell type: cellula_automata value: nil;
			let cell_list type: list value: (list (cellula_correlation))  where (((cellula_correlation (each) distance_to center_node) <= DISK_RADIUS) and ((cellula_correlation (each) distance_to center_node) > LOWEST_RADIUS)) ;
			
			ask target: center_node
			{
				set center_correlation value: dominated_cell_correlation.correlation_coefficient;
				
			}
						
			loop from: 0 to: length(cell_list) - 1 var: cnt
			{
				let the_cell type: cellula_automata value: cell_list at cnt; // SAI
				ask target: cell_list at cnt
				{
					set cell_correlation value: correlation_coefficient;
				}
				let deviation_correlation value: abs(center_correlation - cell_correlation);
				let distance value: the_cell distance_to center_node;
				
				set potential_inverse_distance value: distance/DISK_RADIUS;
				set potential_correlation value: deviation_correlation;
				
				if ((deviation_correlation >= CORRELATION_THRESHOLD) and (((potential_inverse_distance + potential_correlation) / 2) > potential_condition))
				{
					set potential_condition value: (potential_inverse_distance +  potential_correlation) / 2;	
					set the_potential_cell value: the_cell; // SAI
					set location_found value: true;
				}
				
				set testcor value: potential_condition;
			}
			
			if(location_found)
			{
				create species: node number: 1
				{
					set existing_status value: false;
					set location value: the_potential_cell.location;
					do setcolor;
				}
				
				// CREATE NEW EDGE:
						
				create species: edge number: 1;
				set current_edge value: edge at (length(edge) - 1);
				set current_edge.source value: center_node;
				
				let the_destination type: node value: node at (length(node) - 1); 
				set current_edge.destination value: the_destination;
				set current_edge.shape value: polygon([{((center_node) . location) . x, ((center_node) . location) . y }, { ((the_destination) . location) . x, ((the_destination) . location) . y}]);
			}
			
		}
		
		
		action allocateNewNodeByStdDeviation
		{
			arg center_node type: node;
			
			let location_found type: bool value: false;
			let maximum_priority type: float value: 0.0;
			let current_priority type: float value: 0.0;
			
			let current_std_deviation type: float value: 0.0;
			let current_attractive_index type: float value: 0.0;
			let current_hinder_index type: float value: 0.0;
			let current_being_monitored type: bool value: false;
			
			let selected_correlation type: float value: 0.0;
			let current_correlation type: float value: 0.0;
			let center_correlation type: float value: 0.0;
			let the_potential_cell type: cellula_automata value: nil;
			
			ask target: center_node
			{
				set center_correlation value: correlation_coefficient; //dominated_cell_correlation.correlation_coefficient;
			}
			
			// Three lists of cells
			let cell_list type: list value: (list (cellula_automata))  where (((cellula_automata (each) distance_to center_node) <= DISK_RADIUS) and ((cellula_automata (each) distance_to center_node) > LOWEST_RADIUS)) ;
			let std_deviation_cell_list type: list value: (list (cellula_std_deviation))  where (((cellula_std_deviation (each) distance_to center_node) <= DISK_RADIUS) and ((cellula_std_deviation (each) distance_to center_node) > LOWEST_RADIUS)) ;
			let correlation_cell_list type: list value: (list (cellula_correlation))  where (((cellula_correlation (each) distance_to center_node) <= DISK_RADIUS) and ((cellula_correlation (each) distance_to center_node) > LOWEST_RADIUS)) ;
			let the_sigma_cell type: cellula_std_deviation value: nil;
			let the_rho_cell type: cellula_correlation value: nil;
			let the_cell type: cellula_automata value: nil;
			
			let the_node type: node value: nil;
			loop from: 0 to: length(cell_list) - 1 var: cnt
			{
				set the_cell value: cell_list at cnt;
				set the_sigma_cell value: std_deviation_cell_list at cnt;
				set the_rho_cell value: correlation_cell_list at cnt;
				
				ask target: the_sigma_cell
				{
					set current_std_deviation value: estimation_std_deviation;
				} 
				
				ask target: the_rho_cell
				{
					set current_correlation value: correlation_coefficient;
				}
				
				ask target: the_cell
				{
					set current_attractive_index value: attractive_index;
					set current_hinder_index value: hinder_index;
					set current_being_monitored value: is_monitored;
				}
				
				// Calculating the priority (SIGMA(distance) * deviation):
				let node_list type: list value: (list (node))  where (((node (each) distance_to the_cell) <= DISK_RADIUS)) ;
				let max_distance type: float value: 0.0;
				let mean_distance type: float value: 0.0;
				loop from: 0 to: length(node_list) - 1 var: count
				{
					let distance type: float value: (the_cell distance_to (node_list at count));
					if (distance > max_distance)
					{
						set max_distance value: distance;
					}
				}
				
				loop from: 0 to: length(node_list) - 1 var: count
				{
					let distance type: float value: (the_cell distance_to (node_list at count));
					set mean_distance value: mean_distance + (distance/max_distance);
				}
				set mean_distance value: mean_distance/length(node_list);
				
				// DECISION MAKERS: Considered by the attractive/hinder indices
				if(current_hinder_index >= 0.8 or current_being_monitored)
				{
					set current_priority value: -1.0;
				}
				else
				{
					//set current_priority value: (sum_distance/length(node_list)) * current_std_deviation;
					set current_priority value: mean_distance * current_std_deviation;
				}
				
				/*
				// Not considering the local constraints
				if(current_being_monitored)
				{
					set current_priority value: -1.0;
				}
				else
				{
					//set current_priority value: (sum_distance/length(node_list)) * current_std_deviation;
					set current_priority value: mean_distance * current_std_deviation;
				}
				*/
				// Updating the maximum priority: 
				if (current_priority > maximum_priority)
				{
					set the_potential_cell value: the_cell;
					set maximum_priority value: current_priority;
					set selected_correlation value: current_correlation;
					set location_found value: true;
				}
			}
			if(location_found)
			{
				create species: node number: 1
				{
					set existing_status value: false;
					set location value: the_potential_cell.location;
					//set dominated_cell value: nil;
					//set dominated_cell_correlation value: correlation_automata at 0; //the_rho_cell;
					
					write "Localtion: " + string(location.x) + "," + string(location.y);
					
					set correlation_coefficient value: current_correlation; 
					//do setcolor;
					do setnewcolor;
					
					set dominated_cell value: the_potential_cell;
					ask target: the_potential_cell
					{
						set is_monitored value: true;
					}
					set dominated_cell_correlation value: the_rho_cell;
				}
				
				// CREATE NEW EDGE:
				if(abs(center_correlation - selected_correlation) <= CORRELATION_THRESHOLD)
				{
					create species: edge number: 1;
					set current_edge value: edge at (length(edge) - 1);
					set current_edge.source value: center_node;
					ask center_node{
						set degree value: degree + 1;
					}
					
					let the_destination type: node value: node at (length(node) - 1);
					ask the_destination{
						set degree value: degree + 1;
					}
					
					 
					set current_edge.destination value: the_destination;
					set current_edge.shape value: polygon([{((center_node) . location) . x, ((center_node) . location) . y }, { ((the_destination) . location) . x, ((the_destination) . location) . y}]);
				}
			}
			
		}
		
		action resetEdgesList{
			set setup value: 1;
			
			
	
			let count type: int value: length (node) - 1;
			let i type: int value: 0;
			loop from: 0 to: length (node) - 2 var: i
			{
				
				let the_outside_node value: node at i;  
				
				let j type: int value: 0;
				loop from: i + 1 to: length (node) - 1 var: j 
				{
					
					let the_inside_node value: node at j;
					
					
					if condition: (the_outside_node distance_to the_inside_node) < DISK_RADIUS
					{
						set x1  value: (float(((the_outside_node) . location) . x));  
						set y1  value: (float(((the_outside_node) . location) . y));						
						set x2  value: (float(((the_inside_node) . location) . x));  
						set y2  value: (float(((the_inside_node) . location) . y));
						
						// Calculating the maximum location:
						set x1_to  value: (float (x1 + float(DISK_RADIUS * float(float (cos(alpha))))));
						set y1_to value: (float (y1 + float(DISK_RADIUS * float(float(sin(45))))));
						
						let vv12 type: float value:0.0; 
						set vv12 value: (((x1_to - x1) * (x2 - x1)) + ((y1_to - y1) * (y2 - y1))); 
						let length_v1 type: float value: 0.0;
						set length_v1 value: (sqrt(((x1_to - x1) * (x1_to - x1)) + ((y1_to - y1) * (y1_to - y1))));
						let length_v2 type: float value: 0.0;
						set length_v2 value: (sqrt(((x2 - x1) ^ 2) + ((y2 - y1) ^ 2))) + 1.0;
								
							
						let beta type: float value: acos( ((vv12) / ( length_v1 * length_v2 )));
						
				
						if(beta < 90)
						{
							// Correlation weight:
							set vectorXX value: nil;
							set vectorYY value: nil;
							ask target: the_outside_node
							{
								loop var: i from: 0 to: (HISTORICAL_DURATION - 1)
								{
									set vectorXX value: vectorXX + [float(density_matrix at {0, i})];
									//put item: float(density_matrix at {0, i}) at: i in: vectorXX;
								}
							}
							
							ask target: the_inside_node
							{
								loop var: i from: 0 to: (HISTORICAL_DURATION - 1)
								{
									set vectorYY value: vectorYY + [float(density_matrix at {0, i})];
									//put item: float(density_matrix at {0, i}) at: i in: vectorYY;
								}
							}
							
							
							let correlationW type: float value: float(corR(vectorXX, vectorYY));
							set current_edge.correlationWeight value: correlationW;
								
							set correlationAVG value: correlationW + correlationAVG;
							if correlationW >= CORRELATION_THRESHOLD
							{
								
								create species: edge number: 1;
								set current_edge value: edge at lasted_edge_id;
								set current_edge.source value: the_outside_node;
								
								ask the_outside_node{
									set degree value: degree + 1;
								}
								
								set current_edge.destination value: the_inside_node;
								ask the_inside_node
								{
									set degree value: degree + 1;
								}
								
								set current_edge.shape value: polygon([{((the_outside_node) . location) . x, ((the_outside_node) . location) . y }, { ((the_inside_node) . location) . x, ((the_inside_node) . location) . y}]);
								set lasted_edge_id value: lasted_edge_id + 1;
								
								set correlationCount value: correlationCount + 1;
							}
							
						
						}
						else
						{
							// Correlation weight:
							set vectorXX value: nil;
							set vectorYY value: nil;
							ask target: the_inside_node
							{
								loop var: i from: 0 to: (HISTORICAL_DURATION - 1)
								{
									set vectorXX value: vectorXX + [float(density_matrix at {0, i})];
									//put item: float(density_matrix at {0, i}) at: i in: vectorXX;
								}
							}
							
							
							ask target: the_outside_node
							{
								loop var: i from: 0 to: (HISTORICAL_DURATION - 1)
								{
									set vectorYY value: vectorYY + [float(density_matrix at {0, i})];
									//put item: float(density_matrix at {0, i}) at: i in: vectorYY;
								}
								
							}
							let correlationW type: float value: float(corR(vectorXX, vectorYY));
							set current_edge.correlationWeight value: correlationW;
							
							
							set correlationAVG value: correlationW + correlationAVG;
							if correlationW >= CORRELATION_THRESHOLD
							{
								create species: edge number: 1;
								set current_edge value: edge at lasted_edge_id;
								set current_edge.source value: the_inside_node;
								set current_edge.destination value: the_outside_node;
								set current_edge.shape value: polygon([{((the_inside_node) . location) . x, ((the_inside_node) . location) . y }, { ((the_outside_node) . location) . x, ((the_outside_node) . location) . y}]);
								set lasted_edge_id value: lasted_edge_id + 1;
							
								
								set correlationCount value: correlationCount + 1;
							}
							
						}
						
					}
					
					
				}
			
			}
			
			
		}
	} 
}
output{
}

