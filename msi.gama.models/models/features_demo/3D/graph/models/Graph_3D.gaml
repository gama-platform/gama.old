model graph3D

/**
 *  graph3D
 * 
 *  Author: Arnaud Grignard
 * 
 *  Description: Create and update a 3D Graph. Each node is represented by a sphere 
 *  with a size and a color that evolves according to its degree.
 */   

global {
	
	int number_of_agents parameter: 'Number of Agents' min: 1 <- 250 category: 'Initialization';
	int width_and_height_of_environment parameter: 'Dimensions' min: 10 <- 500 category: 'Initialization';  
	float distance parameter: 'distance ' min: 1.0 <-100.0 ;
	int degreeMax <-1;
	geometry shape <- square(width_and_height_of_environment);

	init { 
		create node number: number_of_agents { 
			set degree <-0;
			set location <- {rnd(width_and_height_of_environment), rnd(width_and_height_of_environment),rnd(width_and_height_of_environment)};
		} 
	} 
	
	reflex updateDegreeMax{
		degreeMax <-1;
		ask node{
			if((my_graph) degree_of(self)>degreeMax){
				degreeMax <-(my_graph) degree_of(self);
			}
		}
	}
} 
  
entities { 
	species node parent: graph_node edge_species:edge skills: [moving]{  
		int degree;		
		bool related_to(node other){
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
	
	species edge parent:base_edge{
		rgb color;
	
		aspect base {
			draw shape color: rgb('white');
		}
	}
}
experiment Display  type: gui {
	output {
		display WanderingSphere type:opengl ambient_light:100{
			species node aspect: dynamic;
			species edge aspect:base;
		}
	}
}
