model evacuation_environment



global {
	/** GIS data */
	var shape_file_road type: string init: '../gis/roadlines.shp';
	var shape_file_rivers type: string init: '../gis/rivers.shp';
	var shape_file_beach type: string init: '../gis/beach.shp';
	var shape_file_roadwidth type: string init: '../gis/roads.shp';
	var shape_file_building type: string init: '../gis/buildr.shp';
	var shape_file_bounds type: string init: '../gis/bounds.shp';
	var shape_file_destination type: string init: '../gis/Destination.shp';

	const building_colors type: list init: ['orange', 'red', 'blue', 'black', 'gray', 'magenta'];
	var the_graph type: graph;
	
	
	init {
		create species: road from: shape_file_road returns: the_roads;
		create species: beach from: shape_file_beach;
		create species: building from: shape_file_building; 
		create species: roadwidth from: shape_file_roadwidth;
		create species: river from: shape_file_rivers;
		create species: destination from: shape_file_destination with: [fid :: read ('IND')];
		set the_graph value: as_edge_graph (list(road) collect (each.shape));
		create species: people number: 100 { 
			set goal value: one_of (destination as list) ;
			set location value:any_location_in(one_of(list(road)).shape);
		}
	}
}

environment bounds: shape_file_bounds;

entities {
	species road skills: situated {
		aspect default {
			draw shape: geometry color: 'yellow';
		}
	}
	
	species roadwidth skills: situated {
		aspect default {
			draw shape: geometry color: 'yellow';
		}
	}
	
	species building skills: situated {
		aspect default {
			draw shape: geometry color: 'red';
		}
	}
	
	species beach skills: situated {
		aspect default {
			draw shape: geometry color: 'green';
		}
	}
	
	species river skills: situated {
		aspect default {
			draw shape: geometry color: 'blue';
		}
	}
	
	species destination skills: situated {
		var fid type: int;
		var color type: rgb init: rgb (one_of (building_colors));
		
		aspect default {
			draw shape: geometry color: color;
		}
	}
	
	
	species people skills: [moving] {
		var goal type: destination ;
		var my_path type: path;
	
		aspect default {
			draw shape: circle color: 'green' size: 50 ;
		}
		reflex {
			let followedPath type: path value: self.goto [on:: the_graph,target:: goal.location,speed::50];
		}
	}
}

output {
	display name: 'Display' {
		species road aspect: default transparency: 0.1;
		species roadwidth aspect: default transparency: 0.1;
		species building aspect: default transparency: 0.8;
		species beach aspect: default transparency: 0.9;
		species river aspect: default transparency: 0.5;
		species destination aspect: default transparency: 0.1;
		species people aspect: default;
	}
}