model asian_country

global {
	// GIS data
	string environment_bounds <- '/gis/bounds_Asian.shp';
	string asian_gis <- '/gis/Asia.shp';
	
	rgb country_color <- rgb('blue') const: true;
	string VIETNAM_color <- rgb('red') const: true;	
	string VIETNAM <- 'Viet Nam' const: true;
	
	init {
		create asian_country from: asian_gis with: [ country_name :: read ('NAME')];
	}
}

environment bounds: environment_bounds;

entities {
	species asian_country {
		rgb color <- country_color;
		string country_name;
		
		init {
			do write {
				arg message value: 'country_name: ' + (string(country_name));
			}
			
			if (country_name = VIETNAM) {
				set color value: VIETNAM_color;
			}
		}
		
		aspect base {
			draw shape: geometry color: color;
			if (country_name = VIETNAM) {
				draw text: country_name at: {location.x - 8, location.y} size: 4 color: rgb('green');
			}
		}
	}
}

experiment default_expr type: gui {
	output {
		display default_display {
			species asian_country aspect: base transparency: 0.5;	
		}
	}
}