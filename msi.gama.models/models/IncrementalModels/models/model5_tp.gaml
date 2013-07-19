/**
 *  model5
 *  This model illustrates interaction graph and multi-layer
 */ 
model model5 
 
global {
	file roads_shapefile <- file("../includes/road.shp");
	file buildings_shapefile <- file("../includes/building.shp");
	geometry shape <- envelope(roads_shapefile);
	graph road_network;
	graph friendship_graph;
	init {
		friendship_graph <- generate_barabasi_albert(people,friendship_link,50,1);
		create roads from: roads_shapefile;
		road_network <- as_edge_graph(roads);
		create buildings from: buildings_shapefile with: [type:: string(read("NATURE"))] {
			color <- type="Industrial" ? rgb("blue") : rgb("gray");
		}
		ask people  {
			target <- any_location_in(one_of(buildings where (each.type="Industrial" )).shape);
			list<buildings> residential_bg <- buildings where (each.type="Residential");
			buildings habitation <- residential_bg with_min_of (length(people inside each)/ each.shape.area);
			location <- any_location_in(habitation.shape) add_z habitation.height;
			
		}
	}
}

species people skills:[moving]{		
	int size <- 5;
	float speed <- 5.0 + rnd(5);
	point target;
	
	reflex move{
		do goto target:target on: road_network ;
	}
	aspect circle{
		draw circle(size) color:rgb("green");
	}
	aspect sphere{
		draw sphere(size) color:rgb("green");
	}
	aspect node{
		draw sphere(1 + (friendship_graph degree_of (self))) color:rgb("red");
	}
}

species roads {
	aspect geom {
		draw shape color: rgb("black");
	}
}

species buildings {
	string type;
	rgb color;
	float height <- 5.0 + rnd(10.0);
	aspect geom {
		draw shape color: color depth: height;
	}
}

species friendship_link {	
	aspect base{
   	  draw shape + 0.5 color:rgb("red") border: rgb("red") ;
   	}
}

experiment main_experiment type:gui{
	output {
		display map type: opengl ambient_light: 100 {
			species roads aspect:geom;
			species buildings aspect:geom;
			species people aspect:sphere;	
			
	        species people aspect: node z: 0.2;
			species friendship_link aspect: base z: 0.2;
		}
		graphdisplay friendship graph: friendship_graph ;
	}
}