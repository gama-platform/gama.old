/**
* Name: Spatialqueries
* Author: Patrick Taillandier
* Description: A model which shows how to use spatial queries - in magenta the source geometry, in red the agents concerned by the spatial query
* Tags: topology, spatial_computation, spatial_queries
*/

model Spatialqueries

global {
	/*
	 * overlapping: the geometries have at least one point in common with the source geometry
	 * inside: the geometries are completely within the source geometry (no touching edges)
	 * touhcing: the geometries only touch edges of the source geometry and do not overlap it in any way
	 * crossing: the geometries do more than touch the source geometry, they actually overlap edges of it
	 * covering: the geometries completely cover the source geometry (no touching edges)
	 * closest_to: the geometry closest to the source geometry
	 * farthest_to: the geometry farthest to the source geometry
	 */
	string type_query <- "overlapping" among: ["overlapping", "partially_overlapping", "intersecting", "inside", "touching", "crossing", "covering", "closest_to", "farthest_to"] ;
	agent_base selected_agent;
	
	init {
		create polygon_agent number:10 {
			shape <- square(20);
		}
		
		create polyline_agent number:10 {
			shape <- line([any_location_in(world),any_location_in(world)]);
		}
		
		create point_agent number: 10 {
			shape <-any_location_in(world);
		}
		selected_agent <- one_of (polygon_agent);
		do apply_query;
	}
	
	action change_agent {
		selected_agent <- (polygon_agent + polyline_agent + point_agent) closest_to #user_location; 	
		do apply_query;
	}
	
	action apply_query {
		list<agent_base> agents_concerned;
		switch type_query {
			match "overlapping" {
				agents_concerned <- (polygon_agent + polyline_agent + point_agent) overlapping selected_agent;
			}
			match "partially_overlapping"  {
				agents_concerned <- (polygon_agent + polyline_agent + point_agent) partially_overlapping selected_agent;				
			}
			match "intersecting" {
				agents_concerned <- (polygon_agent + polyline_agent + point_agent) intersecting selected_agent;								
			}
			match "inside" {
				agents_concerned <- (polygon_agent + polyline_agent + point_agent) inside selected_agent;
			}
			match "touching" {
				agents_concerned <- (polygon_agent + polyline_agent + point_agent) touching selected_agent;
			}
			match "crossing" {
				agents_concerned <- (polygon_agent + polyline_agent + point_agent) crossing selected_agent;
			}
			match "covering" {
				agents_concerned <- (polygon_agent + polyline_agent + point_agent) covering selected_agent;
			}
			match "closest_to" {
				agents_concerned <- [polygon_agent closest_to selected_agent,polyline_agent closest_to selected_agent,point_agent closest_to selected_agent];
			}
			match "farthest_to" {
				agents_concerned <- [polygon_agent farthest_to selected_agent,polyline_agent farthest_to selected_agent,point_agent farthest_to selected_agent];
			}
		}
		ask polygon_agent +  polyline_agent + point_agent{
			is_concerned <- false;
		}
		ask agents_concerned {
			is_concerned <- true;
		}
	}
}
species agent_base {
	bool is_concerned <- false;
	
}
species polygon_agent parent:agent_base {
	rgb color <- #gray;
	aspect default {
		if (self = selected_agent) {
			
			draw shape + 1.0 color: #magenta; 
		}
		draw shape color: is_concerned ? #red : color border: #black; 
	}
}

species polyline_agent parent:agent_base {
	rgb color <- #black;
	aspect default {
		if (self = selected_agent) {
			draw shape + 0.5 color: #magenta; 
		}
		draw shape color: is_concerned ? #red : color; 
	}
}

species point_agent parent:agent_base {
	rgb color <- #green;
	aspect default {
		if (self = selected_agent) {
			draw circle(1.5) color: #magenta; 
		}
		draw circle(1.0) color:is_concerned ? #red : color border: #black; 
	}
}

experiment Spatialqueries type: gui {
	parameter Query var: type_query on_change: {ask simulation{do apply_query;} do update_outputs();};
	output {
		display map {
			species polygon_agent;
			species point_agent;
			species polyline_agent;	
			event #mouse_down  {ask simulation{do change_agent;}}
		}
	}
}
