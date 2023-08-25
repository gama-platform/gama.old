/**
* Name: Cellular Automaton Based Urban Growth
* Author: Truong Chi Quang, Patrick Taillandier, Benoit Gaudou & Alexis Drogoul
* Description: model based on the one proposed by (Raimbault et al., 2014): 
* At each simulation step the nb_plots_to_build empty plots with the highest constructability are built 
* The constructability is computed from 3 criteria: the density of construction in the neighborhood, the distance to a road, the distance to the city center (using the road network). 
* Tags: gis, shapefile, grid, graph
*/

model raster3


global
{ 
	//File for the ascii grid
	file asc_grid <- grid_file("../includes/cantho_1999_v6.asc");
	//Shapefile for the road
	file road_shapefile <- shape_file("../includes/roads15_3.shp");
	//Shapefile for the city
	file city_center_shapefile <- shape_file("../includes/city center.shp");
	//Shape of the environment
	geometry shape <- envelope(asc_grid);
	//Graph of the roads
	graph roads_network;
	
	// Dynamic list of the cells to consider at each cycle
	list<plot> empty_plots <- plot where (each.grid_value = 0.0) update: shuffle(plot where (each.grid_value = 0.0));
	
	list<rgb> plot_colors <- [ 
		#lightgray, //empty
		#orange, // 1 built
		#blue // 2 River-lake
	];
	//Radius of density
	int density_radius <- 4;
	//Weight of density
	float weight_density <- 0.05;
	//Weight of the road distance
	float weight_road_dist <- 0.5;
	//Weight of the city center distance
	float weight_cc_dist <- 0.3;
	//Number of plot allowing to build a building
	int nb_plots_to_build <- 195;

	init
	{
		//Creation of the roads using the shapefile of the road
		create roads from: road_shapefile;
		//Creation of the city center using the city center shapefile
		create city_center from: city_center_shapefile;
		//Creation of the graph of the road network
		roads_network <- as_edge_graph(roads);
		
		//Each road has to compute its distance from the city center
		ask roads {
			do compute_cc_dist;
		}
		//Compute the city distance for each plot
		ask empty_plots {
			do compute_distances;
		}
		//Normalization of the distance
		do normalize_distances;
	}
	//Action to normalize the distance
	action normalize_distances {
		//Maximum distance from the road of all the plots
		float max_road_dist <- empty_plots max_of each.dist_route;
		//Maximum distance from the city center for all the plots
		float max_cc_dist <- empty_plots max_of each.dist_cv;
		//Normalization of  each empty plot according to the maximal value of each attribute
		ask empty_plots {
			dist_cv <- 1 - dist_cv / max_cc_dist;
			dist_route <- 1 - dist_route / max_road_dist;
		}
	}
	
	
	//Reflex representing the global dynamic of the model
	reflex dynamique_globale when: weight_density != 0 or weight_road_dist != 0 or weight_cc_dist != 0 {
		//Ask to each empty plot to compute its constructability
		ask empty_plots {
			constructability <- compute_constructability();
		}
		list<plot> ordered_plots <- empty_plots sort_by (each.constructability);
		ordered_plots <- nb_plots_to_build last ordered_plots;
		//Build on each empty plot having the highest constructability
		ask ordered_plots
		{
			do build;
		}
	}	
}
//Species representing the city center
species city_center {
	aspect default {
		draw circle(300) color: #cyan;
	}	
}
//Species representing the roads
species roads
{
	float dist_cv;
	//Action to compute the city center distance for the road
	action compute_cc_dist {
		using topology(roads_network)
		{
			dist_cv <- self distance_to first(city_center);
		}
	}
	aspect default {
		draw shape color: #black;	
	}
}
//Grid species to represent the different building plots
grid plot file: asc_grid use_individual_shapes: false use_regular_agents: false neighbors: 4
{
	rgb color <- grid_value = -1 ? #white : plot_colors[int(grid_value)];
	//Distance from the road
	float dist_route <- 0.0;
	//Distance from the city center
	float dist_cv <- 0.0;
	//Constructability of the plot
	float constructability;
	
	//Action to compute all the distances for the cell
	action compute_distances
	{
		roads route_pp <- roads closest_to self;
		dist_route <- (self distance_to route_pp) using topology(world);
		dist_cv <- dist_route + route_pp.dist_cv;
	}
	//Action to build on the cell
	action build
	{
		grid_value <- 1.0;
		color <- plot_colors[1];
	}
	//Action to compute the constructability of the plot cell
	float compute_constructability
	{
		//Get all the neighbours plots
		list<plot> voisins <- (self neighbors_at density_radius);
		//Compute the density of all the neighbours plots
		float densite <- (voisins count (each.grid_value = 1.0)) / length(voisins);
		return (densite * weight_density + dist_route * weight_road_dist + dist_cv * weight_cc_dist) / (weight_density + weight_road_dist + weight_cc_dist);
	}			
}

experiment raster type: gui {
 	parameter "Weight of the density criteria" var: weight_density;
 	parameter "Weight of the distance to roads criteria" var: weight_road_dist;
 	parameter "Weight of the distance to city center criteria" var: weight_cc_dist;
 	output {
 		display map type: 2d axes:false antialias:false {
			grid plot;
			species roads;
			species city_center;
		}
	}
}

