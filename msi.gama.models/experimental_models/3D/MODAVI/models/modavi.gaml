/**
 *  macro_graph
 *  Author: Arnaud Grignard
 *  Description: Display the number of node according to the class the belong.
 */
 
 
/* Different way to compute the linkage
	single_linkage
	average_linkage
	complete_linkage
	houssedorf
*/


/*  dendrogramme */

model macro_graph
 
global {
	
	graph my_graph ;
	
	graph my_macroGraph;
	
	//int nbAgent parameter: 'Number of Agents' min: 1 <- 100 category: 'Model';
	//int nbClass parameter: 'Number of class' min: 1 max:100 <- 10 category: 'Model';
	int nbClass;
	int nbAgent;
	int threshold parameter: 'Threshold' min: 1 <- 4 category: 'Model';
	int m_barabasi parameter: 'Edges density' min: 1 <- 1 category: 'Model';
	
	int colorFactor parameter: 'color factor' min:1 <-25 category: 'Aspect';
	int nodeSize parameter: 'Node size' min: 1 <- 2 category: 'Aspect';
	int macroNodeSize parameter: 'Macro Node size' min: 1 <- 2 category: 'Aspect';
		
  matrix interactionMatrix;	
	list macroNodes of: macroNode;
	list macroEdges of: macroEdge;
	
	init {
	
		
	 }
}

environment;

entities {

	species node schedules:[] {
		
		int class;
		rgb color;
		geometry myShape <- geometry (point([location.x,location.y])) ; 
		
		action setPositionAndColor{
			set color <- color hsb_to_rgb ([class/nbClass,1.0,1.0]);
			let tmpradius <- rnd(25)+25;
			set location <- {cos (float((class-1)/nbClass)*360)* tmpradius +50,sin (float((class-1)/nbClass)*360)* tmpradius +50,0};
		} 
						
		aspect base {
			draw myShape color: color depth:nodeSize ; 
		}  		
	}
	

	species edge schedules:[] { 
		rgb color;
		
		reflex updateInteractionMatrix{															
				let src type:node<- my_graph source_of(self);
				let dest type:node <- my_graph target_of(self);	
				let tmp <- interactionMatrix  at {src.class-1,dest.class-1};
				put (int(tmp)+1) at: {src.class-1,dest.class-1} in: interactionMatrix;
		} 
		
		aspect base {
			draw color: color ;
		}	
	}
	
	
	species macroNode schedules:[]{
		rgb color;
		int class;
		int nbAggregatedNodes;
		 
		aspect sphere{
			draw geometry (point([location.x,location.y])) color:color   depth:(nbAggregatedNodes/10);//(nbAggregatedNodes/nbAgent)*10;
			//draw geometry: geometry (point([location.x,location.y])) color: color depth:(nbAggregatedNodes/10)*macroNodeSize;
			draw text: 'class' + class +": " + nbAggregatedNodes depth:10 ;
		}
	}
	
	
	species macroEdge schedules:[] { 
		rgb color <- rgb("black");
		macroNode src;
		macroNode dest;
		int nbAggregatedLinks;
		
		aspect base {

			draw geometry: (line([src.location,dest.location]) buffer ((nbAggregatedLinks+1)/(nbAgent/5))) color: color ;
			//draw text : 'nblink: ' + interactionMatrix  at {src.class-1,dest.class-1} z:10 at: location;
		}	
	}
	
	species macroGraph schedules:[] {
			
	}
}


