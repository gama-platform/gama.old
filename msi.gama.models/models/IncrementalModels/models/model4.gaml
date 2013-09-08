/**
 *  multigraph
 *  Description: 
 */

model multigraph

global {
	file shape_file_in <- file('../includes/road.shp') ;
	file shape_file_bounds <- file('../includes/bounds.shp') ;
	geometry shape <- envelope(shape_file_bounds);
	graph road_graph; 
  
  graph friendship_graph;
	
	init {
		//Road graph creation
		create road from: shape_file_in;
		road_graph <- as_edge_graph(road);
		
		//Friendship graph creation 
		friendship_graph <- generate_barabasi_albert(people,friendship_link,100,1);		
	}
}


species people skills: [moving]{
	rgb my_color<-rgb(rnd(2)*120,rnd(1)*250,rnd(2)*120);
	point location <- any_location_in(one_of(road));
	people target<- one_of(people);
	float size ;
	float z;
	action updateSize {
		size <- 10 * friendship_graph degree_of (self) as float;
	}
	reflex movement {
		if (location distance_to target < 5.0) {
			target <- one_of(people);
			do updateSize;
		}
		do goto on:road_graph target:target speed:1 + rnd(2);
	}
	aspect base {
		draw circle((friendship_graph degree_of (self))) color:my_color;
	}
	
	//FIXME: Not clean
	aspect friendship{
		int degree <- (friendship_graph degree_of (self));
		z<- rnd(1000) as float;		
		location <- {rnd(1000),rnd(1000),z};
		shape <- sphere(degree);
		draw shape color:my_color;
	}
}

species friendship_link parent:base_edge{
	rgb my_color;
	aspect base {
		draw shape color: rgb('black') ;			
	}
	//FIXME: Not clean
	aspect friendship{
		
    set shape <- geometry (line ([{(people(friendship_graph source_of self)).location.x,(people(friendship_graph source_of self)).location.y,(people(friendship_graph source_of self)).z},
    	{(people(friendship_graph target_of self)).location.x,(people(friendship_graph target_of self)).location.y,(people(friendship_graph target_of self)).z}
    ]));	
    
    //set shape <- geometry (line ([self.source.location,self.target.location]));

    draw shape color:rgb('gray');
}
}

species road  {
	aspect base {
		draw geometry: shape color: rgb('black') ;
	}
} 


experiment multigraph type: gui {
	output {
		display city {
			species road aspect: base;
			species friendship_link aspect: base;
			species people aspect: base;
		}
		
		//FIXME: Not working correctly yet
		/*display friendshipGraph type:opengl{
            species people aspect: friendship;
			species friendship_link aspect: friendship;
			
		}*/
	}
}
