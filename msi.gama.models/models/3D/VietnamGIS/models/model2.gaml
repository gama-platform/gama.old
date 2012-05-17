model tutorial_gis_city_traffic

global {
	file shape_file_province <- '../includes/Test/VNM_adm2_plus.shp' parameter: 'Shapefile for the provinces:' category: 'GIS' ;
		
	init {
	
		create province from : shape_file_province with: [year_of_creation :: read("Creation"), year_of_destruction :: read ("Destructio")]{		
		}

		create layer1 number: 1;				
	}
	
}

entities {

	species province{
		rgb color <- rgb('green');
		string name;
		int year_of_creation ;
		int year_of_destruction;
		
		
		aspect default{
			draw shape: geometry color:color;
			}
	}
	
	
	species layer1 {
		
		rgb color <- rgb('green');
		//Draw all geometry
		aspect default {
			loop p over: list(province) {
				draw geometry: p.shape;
			}

		}	
		//Draw only the geometry that exists in the given step
		aspect real_data {
			loop p over: (list(province) where (each.year_of_creation < time and each.year_of_destruction > time) ) {
				draw geometry: p.shape;
			}
		}
			
		}
	}

environment bounds: shape_file_province ;
output {
	display city_display type:opengl   {
		//species province aspect: default;
		species layer1 aspect: real_data;
		//agents Provinces value: (list(province) where (each.year_of_creation > step * 12) );
		//agents Layer1 value: layer1 at 0 aspect: default;
		//agents Layer2 value: layer1 at 0 aspect: real_data transparency:0.8;
	}
}
