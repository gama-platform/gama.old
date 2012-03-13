model mekong_province

global {
	// GIS data
	string environment_bounds <- '/gis/bounds_mekong.shp';	
	string mekong_gis <- '/gis/MekongDelta_provinces.shp';
	
	rgb province_color <- rgb('green') const: true;
	
	init {
		create province from: mekong_gis;
	}
}

environment bounds: environment_bounds;

entities {
	species province {
		aspect base {
			draw shape: geometry color: province_color;
		}
	}
}

experiment default_expr type: gui {
	output {
		display default_expr {
			species province aspect: base transparency: 0.5;
		}
	}
}