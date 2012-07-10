model tutorial_gis_city_traffic

global {
	file shape_file_province <- '../includes/SIG_Vietnam_plus/VNM_adm2_historical_plus.shp' parameter: 'Shapefile for the provinces:' category: 'GIS' ;
		
	init {
	
		create province from : shape_file_province with: [provinceName :: read("VARNAME_2"), year_of_creation :: read("YEAR_CREAT"), year_of_destruction :: read ("YEAR_FUSIO")]{
			set color<- rgb ([0, 125,(year_of_creation-1980)*10]);		
		}
		
		create layer1 number: 1;	
		
		//Print the name of each province
		write (string (length(province)) + "provinces"); 
		loop p over: list(province) {
				write (p.provinceName + ": " + p.year_of_creation + " " + p.year_of_destruction );	
			}			
	}
	
}

entities {

	species province{
		rgb color;// <- rgb('green');
		string name;
		string provinceName;
		int year_of_creation ;
		int year_of_destruction;
		
		
		
		aspect default{
			draw shape: geometry color:color;
			draw text : string ( time+1980) color : rgb ( 'black' ) size : 1;
			}
	}
		
	
	species layer1 {
		
		//rgb color <- rgb('green');
		//Draw all geometry
		aspect default {
			loop p over: list(province) {
				draw geometry: p.shape;
			}

		}	
		//Draw only the geometry that exists in the given step
		aspect real_data {
			loop p over: (list(province) where (each.year_of_creation <= time +1981 and each.year_of_destruction > time+1981) ) {
				
				draw geometry: p.shape color: p.color empty:true ;	
							
			}
			//write (string (length(list(province))));
			write ("year:" + string (time+1980 ) + ": " + string (length(list(province) where (each.year_of_creation <= time +1980 and each.year_of_destruction > time+1980)))+ " provinces");
		}
		
		aspect infectious_data{
			
			loop p over: (list(province) where (each.year_of_creation <= time +1981 and each.year_of_destruction > time+1981) ) {
				draw shape: circle at: p.location size:0.1 empty:true color:'red';
				write (string(p.location));
			}
			
		}
			
		}
	}

environment bounds: shape_file_province ;
output {

	
		display city_display type:opengl    {
		//species province aspect: default;
		species layer1 aspect: real_data;
		//species layer1 aspect: infectious_data;
		//agents Provinces value: (list(province) where (each.year_of_creation > step * 12) );
		//agents Layer1 value: layer1 at 0 aspect: default;
		//agents Layer2 value: layer1 at 0 aspect: real_data transparency:0.8;
	}
}
