/**
* Name:  Movement of an agent on different paths
* Author: 
* Description: Model showing the movement of an agent following three different paths : one defined by its vertices, an other defined thanks to all the roads species, and finally 
*       a path defined by a graph with weights (graph created thanks to an other species)
* Tags: graph, agent_movement, skill
*/
model path_and_follow

global{
	graph the_graph;
    init{
    		//It is possible to define a road by defining its shape, being a line. The line need to be created by passing the location of the vertices
       create road {
       		shape <- line([{0,50},{40,60}]);
       }
       create road {
       		shape <- line([{40,60},{50,50},{55,60}]);
       }
       create road {
       		shape <- line([{55,60},{65,40}]);
       }
       
       create road_of_graph {
       		shape <- line([{65,40},{75,35}]);
       }
       create road_of_graph {
       		shape <- line([{75,35},{85,40},{80,60}]);
       }
       //It is possible to define a weights map by linking the road and their weights (the road will be the key of the weight)
       map<road_of_graph,float> weight_map <- road_of_graph as_map (each::each.shape.perimeter * 10);
       
       //A graph can be defined by using a list or all the agents of a species and it is possible to use a map of weights with each key 
       // of the map being a road to link the road and its weight
       //The as_edge_graph operator is an operator creating a graph using the list of agents passed as edges of the graph
       the_graph <- as_edge_graph(road_of_graph) with_weights weight_map;
      
       create myCircle {
       		location <- {0,0};
       }
      
    }  
}

species myCircle skills:[moving]{
	//The different ways to declare a path : declaring all the lines of the path using their vertices
	//							  using a list of agents representing the lines of the path
	//							  using the graph as a path
	
	
 	path path_to_follow1 <- path([{0,0},{10,10},{0,20},{20,30},{20,40},{0,50}]);	
 	path path_to_follow2 <- path(list(road));	
 	path path_to_follow3 <- list(road_of_graph) as_path the_graph;	
 	
 	//These two variables will change when the cycle will be higher than 100 and 200
 	path path_following<- path_to_follow1;
 	rgb color <- #green;
 	
	reflex myfollow{ 
		//The operator follow make the agent move from the starting vertice of the starting edge of a path to the last vertice of the last edge of the path
		// but following the edges of the concerned path
	 	do follow path: path_following;	
	 	if(cycle>200)
	 	{
	 		path_following<- path_to_follow3;	
	 		color<-#pink;
	 	}
	 	else
	 	{
	 		if(cycle>100)
		 	{
		 		path_following<- path_to_follow2;	
		 		color<-#blue;
		 	}
	 	}
	}
	
	aspect base {
	  draw circle(1) color:#red ;	
	  //We loop on all the edges of the path the agent follow to display them
	  	loop seg over: path_following.edges {
	  		draw seg color: color;
	 	 }
	  
	} 
}

species road {
	aspect base {
	  draw shape color:#blue ;	
	  
	} 
}

species road_of_graph {
	aspect base {
	  draw shape color:#red ;	
	  
	} 
}

experiment main type: gui {
	float minimum_cycle_duration <- 0.10;
	output {
		display myView { 
			species myCircle aspect:base; 
		}
	}
}







