/**
 *  Cellular Automaton Based Urban Growth
 *  Author: Truong Chi Quang, Patrick Taillandier, Benoit Gaudou & Alexis Drogoul
 * Description: model based on the one proposed by (Raimbault et al., 2014): 
 * At each simulation step the nb_plots_to_build empty plots with the highest constructability are built 
 * The constructability is computed from 3 criteria: the density of construction in the neighborhood, the distance to a road, the distance to the city center (using the road network). 
 * */
model raster3


global
{ 
	file asc_grid <- grid_file("../includes/cantho_1999_v6.asc");
	file road_shapefile <- shape_file("../includes/roads15_3.shp");
	file city_center_shapefile <- shape_file("../includes/city center.shp");
	geometry shape <- envelope(asc_grid);
	graph roads_network;
	
	// Dynamic list of the cells to consider at each cycle
	list<plot> empty_plots <- plot where (each.grid_value = 0.0) update: shuffle(plot where (each.grid_value = 0.0));
	
	list<rgb> plot_colors <- [ 
		#lightgray, //empty
		#orange, // 1 built
		#blue // 2 River-lake
	];
	
	int density_radius <- 4;
	float weight_density <- 0.05;
	float weight_road_dist <- 0.5;
	float weight_cc_dist <- 0.3;
	int nb_plots_to_build <- 195;

	init
	{
		create road from: road_shapefile;
		create city_center from: city_center_shapefile;
		roads_network <- as_edge_graph(road);
		ask road {
			do compute_cc_dist;
		}
		ask empty_plots {
			do compute_distances;
		}
		do normalize_distances;
	}
	
	action normalize_distances {
		float max_road_dist <- empty_plots max_of each.dist_route;
		float max_cc_dist <- empty_plots max_of each.dist_cv;
		
		ask empty_plots {
			dist_cv <- 1 - dist_cv / max_cc_dist;
			dist_route <- 1 - dist_route / max_road_dist;
		}
	}
	
	reflex dynamique_globale when: weight_density != 0 or weight_road_dist != 0 or weight_cc_dist != 0 {
		ask empty_plots {
			constructability <- compute_constructability();
		}
		list<plot> ordered_plots <- empty_plots sort_by (each.constructability);
		ordered_plots <- nb_plots_to_build last ordered_plots;
		ask ordered_plots
		{
			do build;
		}
	}	
}

species city_center {
	aspect default {
		draw circle(300) color: #cyan;
	}	
}

species road
{
	float dist_cv;
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

grid plot file: asc_grid use_individual_shapes: false use_regular_agents: false neighbors: 4
{
	rgb color <- grid_value = -1 ? #white : plot_colors[int(grid_value)];
	float dist_route <- 0.0;
	float dist_cv <- 0.0;
	float constructability;
	
	action compute_distances
	{
		road route_pp <- road closest_to self;
		dist_route <- (self distance_to route_pp) using topology(world);
		dist_cv <- dist_route + route_pp.dist_cv;
	}
	
	action build
	{
		grid_value <- 1.0;
		color <- plot_colors[1];
	}

	float compute_constructability
	{
		list<plot> voisins <- (self neighbors_at density_radius);
		float densite <- (voisins count (each.grid_value = 1.0)) / length(voisins);
		return (densite * weight_density + dist_route * weight_road_dist + dist_cv * weight_cc_dist) / (weight_density + weight_road_dist + weight_cc_dist);
	}			
}

experiment raster type: gui {
 	parameter "Weight of the density criteria" var: weight_density;
 	parameter "Weight of the distance to roads criteria" var: weight_road_dist;
 	parameter "Weight of the distance to city center criteria" var: weight_cc_dist;
 	output {
 		display map type: opengl {
			grid plot;
			species road;
			species city_center;
		}
	}
}

