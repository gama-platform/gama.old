model tutorial_gis_robot_exploration
// gen by Xml2Gaml

global {
	var shape_file_background type: string init: '../gis/Background.shp' parameter: 'Shapefile for the buildings:' category: 'GIS' ;
	var shape_file_obstacles type: string init: '../gis/Obstacles.shp' parameter: 'Shapefile for the roads:' category: 'GIS' ;
	var speed_robot type: float init: 2 min: 1 max: 10 parameter: 'Speed of the robot' category: 'Robot' ;
	var robot_perception_range type: float init: 8 min: 1 max: 1000 parameter: 'Perception distance of the robot' category: 'Robot' ;
	var the_bg type: background ;
	var the_robot type: robot ;
	init {
		create species: background from: shape_file_background;
		set the_bg value: first(background as list) ;
		create species: obstacle from: shape_file_obstacles;
		ask target: obstacle as list { 
			set the_bg.shape value: (the_bg.shape - (shape + 0.1)) ;
		}
		
		create species: robot number: 1  {
			set location value: any_location_in (the_bg.shape);
		}
		set the_robot value: first (robot as list) ;
	}
}
environment bounds: shape_file_background ;
entities {
	species background skills: situated {
		aspect base {
			draw shape: geometry color: rgb('pink') ;
		}
	}
	species obstacle skills: situated {
		aspect base {
			draw shape: geometry color: rgb('black') ;
		}
	}
	species robot skills: moving {
		var the_known type: known_area ;
		var range type: float ;
		var perception type:geometry;
		init {
			set speed value: speed_robot ;
			set range value: robot_perception_range ;
			set perception value: ((circle (range)) masked_by obstacle) /*intersection (the_bg.shape)*/ ;
			create species: known_area {
				set shape value:(myself.perception intersection (the_bg.shape));
			}
			set the_known value: first (known_area as list);
		}
		reflex move {
			do action: wander {
				arg agent value: the_bg ;
			}
		}
		reflex update_the_known {
			set perception value:  ((circle (range)) masked_by obstacle) /**/ ;
			ask target: the_known {
				set shape value: ((clean (shape +  myself.perception)) intersection (the_bg.shape)) simplification 0.1;
			}
		}
		aspect base {
			draw shape: geometry size: 5 color: rgb('red') ;
		}
	}
	species known_area skills: situated {
		var my_color type:rgb init:rgb('green');
		aspect base {
			draw shape: geometry color:my_color ;
		}
	}
}
output {
	display display refresh_every: 1 {
		species background aspect: base ;
		species obstacle aspect: base ;
		species known_area transparency: 0.5 aspect: base ;
		species robot aspect: base ;
	}
}
