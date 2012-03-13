model dongthap_landuse

global {
	file district_f <- file('/gis/DT_district.shp');	

	// GIS data
	string environment_bounds <- '/gis/bounds_DT_province.shp';
	string dongthap_landuse_gis <- '/gis/landuse_DT_12_03_012.shp';
	
	rgb landunit_color <- rgb('cyan') const: true;
	
	init {
		create landunit from: dongthap_landuse_gis;
	}
}

environment bounds: environment_bounds;

entities {
	species landunit {
		aspect base {
			draw shape: geometry color: landunit_color;
		}
	}
}

experiment default_expr type: gui {
	output {
		display default_display {
			species landunit aspect: base transparency: 0.5;
		}
	}
}