/**
* Name: 3D Display model and Height of Building using shapefiles
* Author: Arnaud Grignard
* Description: Model presenting a 3D display of people and buildings moving on a road network imported thanks to shapefiles. 
* 
* Two experiments are proposed : one showing people represented by a yellow sphere moving from a living 3D building to a working 3D building and coming back 
* using a road network (road_traffic). The second experiment distinguish the species by using different layers for species (road_traffic_multi_layer).
* Tags: 3d, shapefile, gis
*/
model tutorial_gis_city_traffic


global
{
//Load of the different shapefiles used by the model
	file shape_file_buildings <- shape_file('../includes/building.shp', 0);
	file shape_file_roads <- shape_file('../includes/road.shp', 0);
	file shape_file_bounds <- shape_file('../includes/bounds.shp', 0);

	//Definition of the shape of the world as the bounds of the shapefiles to show everything contained
	// by the area delimited by the bounds
	geometry shape <- envelope(shape_file_bounds);
	int nb_people <- 1000;
	int day_time update: cycle mod 144;
	int min_work_start <- 36;
	int max_work_start <- 60;
	int min_work_end <- 84;
	int max_work_end <- 132;
	float min_speed <- 50.0;
	float max_speed <- 100.0;
	list<building> residential_buildings;
	list<building> industrial_buildings;

	//Declaration of a graph that will represent our road network
	graph the_graph;
	init
	{
		create building from: shape_file_buildings with: [type:: string(read('NATURE'))]
		{
			if type = "Industrial"
			{
				color <- # blue;
			}
			height <- 10 + rnd(90);
		}

		residential_buildings <- building where (each.type = 'Residential');
		industrial_buildings <- building where (each.type = 'Industrial');
		create road from: shape_file_roads;
		the_graph <- as_edge_graph(road);
		create people number: nb_people;
	}

}

species building
{
	string type;
	rgb color <- # gray;
	int height;
	aspect base
	{
		draw shape color: color depth: height;
	}

}

species road
{
	rgb color <- # black;
	aspect base
	{
		draw shape color: color;
	}

}

species people skills: [moving]
{
	float speed <- min_speed + rnd(max_speed - min_speed);
	rgb color <- rnd_color(255);
	building living_place <- one_of(residential_buildings);
	building working_place <- one_of(industrial_buildings);
	point location <- any_location_in(living_place) + { 0, 0, living_place.height };
	int start_work <- min_work_start + rnd(max_work_start - min_work_start);
	int end_work <- min_work_end + rnd(max_work_end - min_work_end);
	string objectif;
	point the_target <- nil;
	reflex time_to_work when: day_time = start_work
	{
		objectif <- 'working';
		the_target <- any_location_in(working_place);
	}

	reflex time_to_go_home when: day_time = end_work
	{
		objectif <- 'go home';
		the_target <- any_location_in(living_place);
	}

	reflex move when: the_target != nil
	{
		do goto( target: the_target ,on: the_graph);
		switch the_target
		{
			match location
			{
				the_target <- nil;
				location <- { location.x, location.y, objectif = 'go home' ? living_place.height : working_place.height };
			}

		}

	}

	aspect base
	{
		draw sphere(3) color: color;
	}

}

experiment road_traffic type: gui
{
	parameter 'Shapefile for the buildings:' var: shape_file_buildings category: 'GIS';
	parameter 'Shapefile for the roads:' var: shape_file_roads category: 'GIS';
	parameter 'Shapefile for the bounds:' var: shape_file_bounds category: 'GIS';
	parameter 'Earliest hour to start work' var: min_work_start category: 'People';
	parameter 'Latest hour to start work' var: max_work_start category: 'People';
	parameter 'Earliest hour to end work' var: min_work_end category: 'People';
	parameter 'Latest hour to end work' var: max_work_end category: 'People';
	parameter 'minimal speed' var: min_speed category: 'People';
	parameter 'maximal speed' var: max_speed category: 'People';
	parameter 'Number of people agents' var: nb_people category: 'People' min: 0 max: 1000 on_change:
	{
		int nb <- length(people);
		ask simulation
		{
			if (nb_people > nb)
			{
				create people number: nb_people - nb;
			} else
			{
				ask (nb - nb_people) among people
				{
					do die;
				}
			}
		}
	};
	output
	{
		display city_display type:opengl 
		{
			species building aspect: base refresh: true;
			species road aspect: base ;
			species people aspect: base;
		}
	}

}

experiment road_traffic_multi_layer type: gui
{
	parameter 'Shapefile for the buildings:' var: shape_file_buildings category: 'GIS';
	parameter 'Shapefile for the roads:' var: shape_file_roads category: 'GIS';
	parameter 'Shapefile for the bounds:' var: shape_file_bounds category: 'GIS';
	parameter 'Number of people agents' var: nb_people category: 'People';
	parameter 'Earliest hour to start work' var: min_work_start category: 'People';
	parameter 'Latest hour to start work' var: max_work_start category: 'People';
	parameter 'Earliest hour to end work' var: min_work_end category: 'People';
	parameter 'Latest hour to end work' var: max_work_end category: 'People';
	parameter 'minimal speed' var: min_speed category: 'People';
	parameter 'maximal speed' var: max_speed category: 'People';
	parameter 'Number of people agents' var: nb_people category: 'People' min: 0 max: 1000 on_change:
	{
		int nb <- length(people);
		ask simulation
		{
			if (nb_people > nb)
			{
				create people number: nb_people - nb;
			} else
			{
				ask (nb - nb_people) among people
				{
					do die;
				}
			}
		}
	};
	output
	{
		display city_display type: opengl
		{
			species road aspect: base;
			species building aspect: base position: { 0, 0, 0.25 };
			species people aspect: base position: { 0, 0, 0.5 };
		}
	}
}
