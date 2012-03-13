model asian_country

global {
	// GIS data
	string environment_bounds <- '/gis/bounds_Asian.shp';
	string asian_gis <- '/gis/Asia.shp';
	
	rgb country_color <- rgb('blue') const: true;
	
	init {
		create asian_country from: asian_gis;
	}
}

environment bounds: environment_bounds;

entities {
	species asian_country {
		
		aspect base {
			draw shape: geometry color: country_color;
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