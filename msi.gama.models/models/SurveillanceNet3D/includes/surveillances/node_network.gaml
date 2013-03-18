/*
 * 
 * 
 *  Node species
 *  Author: Truong Xuan Viet
 *  Last Modified Date: 01-11-2012 
 */

 
model NodeNetworkModel
import "../GlobalParam.gaml"
import "../../models/GlobalModel3D.gaml"

global {
	//var SHAPE_NODE type: string init: '../includes/gis/surveillances/provinces/DongThap_Lighttraps.shp' parameter: 'Sensors Network - DONG THAP:' category: 'SURVEILLANCE' ;
}

entities {
	
	species node {
		var id type: string ;
		var name type: string ;
		var id_0 type: string ;
		var id_1 type: string ;
		var id_2 type: string ;  
		var province_name type: string ;
		var id_3 type: string ;
		var district_name type: string ;
		matrix density_matrix size: {1, 365} ;
		
		var number_of_BPHs type: float init: 0.0;
		var number_of_BPHs_by_day type: float init: 0.0;
		
		var number_of_BPHs_total type: float init: 0.0 ;
		
		// WORKING STATUS: TRUE/FALSE
		var working_status type: bool init: true;
		var existing_status type: bool init: true;
		
		// Dominated cell:
		var dominated_cell type: cellula_automata;
		var dominated_cell_correlation type: cellula_correlation;
		
		// OPTIMIZATION SUPPORTS
		var correlation_coefficient type: float init: 0.0;
		var sample_variance type: float init: 0.0;
		
		
		var degree type: int init: 0;
		
		var nodecolor type: rgb value: rgb([0, 0, 255]); //(existing_status)?rgb([0,255,0]):rgb([255, 255,0]);
		
		var z type: float init: 0.0;
				
		init{
			set location value: self.location;
			set nodecolor value: rgb([0,255,0]);
		}
		
		/*
		var color type: rgb value: (number_of_BPHs > 1000000)?rgb([0,0,0]):        
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
					* 
					*/
		aspect default {
			//draw shape: circle color: rgb([255, 0, 255]) size: 1500 ;
			draw circle(500) color: nodecolor;
		}
		
		aspect ThreeDirections {
			//draw shape: circle color: rgb([255, 0, 255]) size: 1500 ;
			draw  circle(500) color: nodecolor;
			set z value: 50;
		}
		
		action setnewcolor {
			set nodecolor value: rgb([255,0,0]);
		}
		
		action setcolor {
			//set nodecolor value: (existing_status)?rgb([0,255,0]):rgb([255, 0, 0]);
			set nodecolor value: (number_of_BPHs > 1000000)?rgb([0,0,0]):        
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
		
		// Chua kiem chung:
		action getCorrelationCoefficient
		{
			let  list_neighbors value: (list (cellula_correlation)) where (each overlaps(self));
			loop var: i from: 0 to: length(list_neighbors) - 1
			{
				let the_cell type: cellula_correlation value: list_neighbors at i;
				if self intersects the_cell
				{
					set correlation_coefficient value: the_cell.correlation_coefficient;
				}
			}
		}
	}
}
