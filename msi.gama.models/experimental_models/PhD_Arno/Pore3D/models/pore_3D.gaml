model graph3D

/**
 *  graph3D
 *  Author: Arnaud Grignard
 *  Description: Create and update a 3D Graph

 */   

global {
	
	int number_of_agents parameter: 'Number of Agents' min: 1 <- 250 ;
	int width_and_height_of_environment parameter: 'Dimensions' min: 10 <- 500 ;  
	float distance <-100.0;
	int degreeMax <-1;

	init { 
		create myNode number: number_of_agents { 
			set degree <-0;
			set location <- {rnd(width_and_height_of_environment), rnd(width_and_height_of_environment),rnd(width_and_height_of_environment)};
		} 
	} 
	
	reflex updateDegreeMax{
		degreeMax <-1;
		ask myNode{
			if((my_graph) degree_of(self)>degreeMax){
				degreeMax <-(my_graph) degree_of(self);
			}
		}
	}
} 
  
environment width: width_and_height_of_environment height: width_and_height_of_environment;  
 
entities { 
	species myNode parent: graph_node edge_species:myEdge skills: [moving]{  
		int degree;		
		bool related_to(myNode other){
			using topology(self){
				return (self.location distance_to other.location) < distance;
			}
		}		
		reflex move{
			do wander_3D;
		}
	    float radius <-1.0;		
		aspect default {
		  draw sphere(radius) color: rgb("blue");	
	    }
	    
	    aspect dynamic{
		  degree <-(my_graph) degree_of(self);
		  draw sphere(((((degree+1)^1.4)/(degreeMax))*radius)*5) color: hsb(degree/(degreeMax+1) , 0.5, 1.0); 
		}
	}
	
	species myEdge parent:base_edge{
		rgb color;
	
		aspect base {
			draw shape color: rgb('white');
		}
	}
}
experiment display1  type: gui {
	output {
		display WanderingSphere type:opengl ambient_light:100{
			species myNode aspect: dynamic;
			species myEdge aspect:base;
		}
	}
}
