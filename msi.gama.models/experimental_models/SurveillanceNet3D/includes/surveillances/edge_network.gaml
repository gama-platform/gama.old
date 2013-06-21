model GraphNetworkModel

import "../GlobalParam.gaml"
import "node_network.gaml"

global {
	var SHAPE_EDGE type: string init: '../includes/gis/surveillances/provinces/DongThap_Lighttraps.shp' parameter: 'Sensors Network - DONG THAP:' category: 'SURVEILLANCE' ;
}



entities {
	species edge {
		var id type: string ;
		var name type: string ;
		var source type: node init: nil;
		var destination type: node init: nil;
			
		
		var shape type: geometry init: polygon([{0,0}, {1,1}, {0,0}]);		
		var coefficience type: int init: 0 ;
		var correlationWeight type: int init: 0 ;
		var color type: rgb init: rgb([0, 0, 0]);
		
		/*
		var color type: rgb value: (coefficience > 1000000)?rgb [0,0,0]:        
						((coefficience > 750000)?rgb [20,0,0]:        
						((coefficience > 500000)?rgb [38,0,0]:        
						((coefficience > 250000)?rgb [58,0,0]:        
						((coefficience > 100000)?rgb [76,0,0]:        
						((coefficience > 75000)?rgb [96,0,0]:        
						((coefficience > 50000)?rgb [102,0,0]:        
						((coefficience > 25000)?rgb [130,0,0]:        
						((coefficience > 10000)?rgb [160,0,0]:        
						((coefficience > 7500)?rgb [208,0,0]:        
						((coefficience > 5000)?rgb [178,0,0]:        
						((coefficience > 2500)?rgb [255,7,7]:        
						((coefficience > 1000)?rgb [255,37,37]:        
						((coefficience > 750)?rgb [255,65,65]:        
						((coefficience > 500)?rgb [255,93,93]:        
						((coefficience > 250)?rgb [255,123,123]:        
						((coefficience > 100)?rgb [255,151,151]:        
						((coefficience > 75)?rgb [255,181,181]:        
						((coefficience > 50)?rgb [255,209,209]:        
						((coefficience > 25)?rgb [255,237,237]:
						rgb [255,255,255]))))))))))))))))))) ;
						
						*/
		
		aspect default {
			draw shape color: rgb ([128, 128, 0]) size:  2;
			
			
			//draw text: "ABCABCABCABCABCABCABCABCABCABCABCABC" color: rgb('black') size: 10 at: {10000, 100000};
			//draw shape: line at: source.location to: destination.location color: rgb ("blue") size: 1 ;
			
		}
		
		
		action setcolor {
			set color value: (correlationWeight >= 0.1)?rgb ([255, 0, 0]):rgb ([0, 0, 255]);
			// TO DO	
			/*
			set color value: (coefficience > 1000000)?rgb [0,0,0]:        
					((coefficience > 750000)?rgb [20,0,0]:        
					((coefficience > 500000)?rgb [38,0,0]:        
					((coefficience > 250000)?rgb [58,0,0]:        
					((coefficience > 100000)?rgb [76,0,0]:        
					((coefficience > 75000)?rgb [96,0,0]:        
					((coefficience > 50000)?rgb [102,0,0]:        
					((coefficience > 25000)?rgb [130,0,0]:        
					((coefficience > 10000)?rgb [160,0,0]:        
					((coefficience > 7500)?rgb [208,0,0]:        
					((coefficience > 5000)?rgb [178,0,0]:        
					((coefficience > 2500)?rgb [255,7,7]:        
					((coefficience > 1000)?rgb [255,37,37]:        
					((coefficience > 750)?rgb [255,65,65]:        
					((coefficience > 500)?rgb [255,93,93]:        
					((coefficience > 250)?rgb [255,123,123]:        
					((coefficience > 100)?rgb [255,151,151]:        
					((coefficience > 75)?rgb [255,181,181]:        
					((coefficience > 50)?rgb [255,209,209]:        
					((coefficience > 25)?rgb [255,237,237]:
					rgb [255,255,255]))))))))))))))))))) ;
			*/
		}
		
		

	}
}
output ;
