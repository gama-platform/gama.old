model path_and_follow

global{
	graph the_graph;
    init{
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
       map<road_of_graph,float> weight_map <- road_of_graph as_map (each::each.shape.perimeter * 10);
       the_graph <- as_edge_graph(road_of_graph) with_weights weight_map;
      
       create myCircle {
       		location <- {0,0};
       }
      
    }  
}

species myCircle skills:[moving]{
 	path path_to_follow1 <- path([{0,0},{10,10},{0,20},{20,30},{20,40},{0,50}]);	
 	path path_to_follow2 <- path(list(road));	
 	path path_to_follow3 <- list(road_of_graph) as_path the_graph;	
 	
	reflex myfollow when: cycle < 100{ 
	 	do follow path: path_to_follow1;	
	 
	}
	reflex myfollow2 when: cycle > 100 and cycle < 200{ 
	 	do follow path: path_to_follow2;	
	 
	}
	reflex myfollow3 when: cycle > 200{ 
	 	do follow path: path_to_follow3;	
	 
	}
	aspect base {
	  draw circle(1) color:rgb("red") ;	
	  if (cycle < 100) {
	  	loop seg over: path_to_follow1.edges {
	  		draw seg color: °green;
	 	 }
	  } else if ( cycle > 100 and cycle < 200) {
	  	loop seg over: path_to_follow2.edges {
	  		draw seg color: °blue;
	 	 }
	  } else {
	  	loop seg over: path_to_follow3.edges {
	  		draw seg color: °pink;
	 	 }
	  }
	  
	} 
}

species road {
	aspect base {
	  draw shape color:rgb("blue") ;	
	  
	} 
}

species road_of_graph {
	aspect base {
	  draw shape color:rgb("red") ;	
	  
	} 
}

experiment main type: gui {
	output {
		display myView { 
			species myCircle aspect:base; 
		}
	}
}







