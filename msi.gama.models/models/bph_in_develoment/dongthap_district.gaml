model dongthap_district

global {
	// GIS data
	string environment_bounds <- 'gis/bounds_DT_province.shp';
	string dongthap_district_gis <- 'gis/dongthap_districts.shp';
	
	file district_f <- shapefile(dongthap_district_gis);
	
	rgb landunit_color <- rgb('cyan') const: true;
	
	init {
		do write {
			arg message value: string(length(district_f.contents));
		}
		
		create district from: dongthap_district_gis;   
	}
}

environment bounds: environment_bounds;

entities {
	species district {
		aspect base {
			draw shape: geometry color: landunit_color;
		}
	}
}

experiment default_expr type: gui {
	output {
		display default_display {
			species district aspect: base transparency: 0.5;
		}
	}
}