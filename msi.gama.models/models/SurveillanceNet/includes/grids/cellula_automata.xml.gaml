model CellulaAutomata
// gen by Xml2Gaml
import "platform:/plugin/msi.gama.gui.application/generated/std.gaml"

global {
	var shape_file_cellula_mekong type: string init: '../gis/grids/MekongRect_PointZeroFive.shp' parameter: 'Grids of 0.1 degree for Lon/Lat:' category: 'GRIDS' ;
	var shape_file_province_DONGTHAP type: string init: '../gis/grids/provinces/DongThapRect_PointZeroOne_RiceArea.shp' parameter: 'Grids of 0.01 degree for Lon/Lat (DONG THAP):' category: 'GRIDS' ;
	var SHAPE_BOUNDING_BOX type: string value: 'shape_file_province_DONGTHAP' ;
	init ;
}
environment ;
entities {
	species cellula_automata skills: [situated, visible] {
		var id type: string ;
		var name type: string ;
		var E type: string ;
		var NE type: string ;
		var N type: string ;
		var NW type: string ;
		var W type: string ;
		var SW type: string ;
		var S type: string ;
		var SE type: string ;
		var x_coordinate type: string ;
		var y_coordinate type: string ;
		var square_area type: float ;
		var rice_area type: float ;
		var attractive_index type: float init: 0 ;
		var hinder_index type: float init: 0 ;
		var transplantation_index type: float init: 0 ;
		var number_of_BPHs type: float init: 0 ;
		var regression_count type: int init: 0 ;
		var out_number_of_BPHs type: float init: 0 ;
		var in_number_of_BPHs type: float init: 0 ;
		var color type: rgb init: rgb('white') ;
		var color type: rgb value: (number_of_BPHs > 1000000)?rgb [0,0,0]:
			((number_of_BPHs > 750000)?rgb [20,0,0]:
			((number_of_BPHs > 500000)?rgb [38,0,0]:
			((number_of_BPHs > 250000)?rgb [58,0,0]:
			((number_of_BPHs > 100000)?rgb [76,0,0]:
			((number_of_BPHs > 75000)?rgb [96,0,0]:
			((number_of_BPHs > 50000)?rgb [102,0,0]:
			((number_of_BPHs > 25000)?rgb [130,0,0]:
			((number_of_BPHs > 10000)?rgb [160,0,0]:
			((number_of_BPHs > 7500)?rgb [208,0,0]:
			((number_of_BPHs > 5000)?rgb [178,0,0]:
			((number_of_BPHs > 2500)?rgb [255,7,7]:
			((number_of_BPHs > 1000)?rgb [255,37,37]:
			((number_of_BPHs > 750)?rgb [255,65,65]:
			((number_of_BPHs > 500)?rgb [255,93,93]:
			((number_of_BPHs > 250)?rgb [255,123,123]:
			((number_of_BPHs > 100)?rgb [255,151,151]:
			((number_of_BPHs > 75)?rgb [255,181,181]:
			((number_of_BPHs > 50)?rgb [255,209,209]:
			((number_of_BPHs > 25)?rgb [255,237,237]:
			rgb [255,255,255]))))))))))))))))))) ;
		action resetcolor {
			let ratio type: float value: (rice_area / square_area) * 100 ;
			set color value: (ratio > 90)? rgb [0,0,0]:          
								((ratio > 80)?rgb [38,0,0]:	
								((ratio > 70)?rgb [76,0,0]:         
								((ratio > 60)?rgb [102,0,0]:         
								((ratio > 50)?rgb [160,0,0]:         
								((ratio > 40)?rgb [178,0,0]:         
								((ratio > 30)?rgb [255,37,37]:         
								((ratio > 20)?rgb [255,93,93]:         
								((ratio > 10)?rgb [255,151,151]:rgb [255,209,209])))))))) ;
		}
		action setcolor {
			set color value: (number_of_BPHs > 10000000)?rgb [0,0,0]:        ((number_of_BPHs > 7500000)?rgb [20,0,0]:        ((number_of_BPHs > 5000000)?rgb [38,0,0]:        ((number_of_BPHs > 2500000)?rgb [58,0,0]:        ((number_of_BPHs > 1000000)?rgb [76,0,0]:        ((number_of_BPHs > 750000)?rgb [96,0,0]:        ((number_of_BPHs > 500000)?rgb [102,0,0]:        ((number_of_BPHs > 250000)?rgb [130,0,0]:        ((number_of_BPHs > 100000)?rgb [160,0,0]:        ((number_of_BPHs > 75000)?rgb [208,0,0]:        ((number_of_BPHs > 50000)?rgb [178,0,0]:        ((number_of_BPHs > 25000)?rgb [255,7,7]:        ((number_of_BPHs > 10000)?rgb [255,37,37]:        ((number_of_BPHs > 7500)?rgb [255,65,65]:        ((number_of_BPHs > 5000)?rgb [255,93,93]:        ((number_of_BPHs > 2500)?rgb [255,123,123]:        ((number_of_BPHs > 1000)?rgb [255,151,151]:        ((number_of_BPHs > 750)?rgb [255,181,181]:        ((number_of_BPHs > 500)?rgb [255,209,209]:        ((number_of_BPHs > 250)?rgb [255,237,237]:rgb [255,255,255]))))))))))))))))))) ;
		}
		action setcolor_smootly {
			let reverse_color type: int value: (number_of_BPHs > 1000000)?100:              
				((number_of_BPHs > 750000)?((number_of_BPHs-750000) /50000)+100:              
				((number_of_BPHs > 500000)?((number_of_BPHs-500000) /50000)+105:              
				((number_of_BPHs > 250000)?((number_of_BPHs-250000) /30000)+110:              
				((number_of_BPHs > 100000)?((number_of_BPHs-100000) /5000)+115:              
				((number_of_BPHs > 75000)?((number_of_BPHs-75000) /5000)+120:              
				((number_of_BPHs > 50000)?((number_of_BPHs-50000) /5000)+125:              
				((number_of_BPHs > 25000)?((number_of_BPHs-25000) /3000)+130:              
				((number_of_BPHs > 10000)?((number_of_BPHs-10000) /500)+135:              
				((number_of_BPHs > 7500)?((number_of_BPHs-7500) /500)+140:              
				((number_of_BPHs > 5000)?((number_of_BPHs-5000) /500)+145:              
				((number_of_BPHs > 2500)?((number_of_BPHs-2500) /300)+150:              
				((number_of_BPHs > 1000)?((number_of_BPHs-1000) /50)+155:              
				((number_of_BPHs > 750)?((number_of_BPHs-750) /50)+160:              
				((number_of_BPHs > 500)?((number_of_BPHs-500) /50)+165:              
				((number_of_BPHs > 250)?((number_of_BPHs-250) /30)+170:              
				((number_of_BPHs > 100)?((number_of_BPHs-100) /5)+175:              
				((number_of_BPHs > 75)?((number_of_BPHs-75) /5)+180:              
				((number_of_BPHs > 50)?((number_of_BPHs-50) /5)+185:              
				((number_of_BPHs > 25)?((number_of_BPHs-25) /5)+190:              
				((number_of_BPHs-0) /5)+195))))))))))))))))))) ;
			set color value: rgb [255 - reverse_color, 255 - reverse_color, 0];
		}
		
		action reset_no_of_insect {
			arg no_insect type: float ;
			if condition: regression_count > 0 {
				set regression_count value: regression_count + 1 ;
				set number_of_BPHs value: ((number_of_BPHs * (regression_count - 1)) / regression_count) + (no_insect / regression_count) ;
				else {
					set regression_count value: regression_count + 1 ;
					set number_of_BPHs value: no_insect ;
				}
			}
		}
	}
}
output ;
