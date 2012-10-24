model NodeNetworkModel
import "../GlobalParam.gaml"

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
		var number_of_BPHs_total type: float init: 0.0 ;
		
		// WORKING STATUS: TRUE/FALSE
		var working_status type: bool value: true;
		
		// OPTIMIZATION SUPPORTS
		var correlation_coefficient type: float init: 0;
		
		init{
			set location value: self.location;
		}
		
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
		aspect default {
			draw shape: circle color: rgb ("black") size: 1000;
			draw shape: circle color: color size: 2000 ;
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
output;

