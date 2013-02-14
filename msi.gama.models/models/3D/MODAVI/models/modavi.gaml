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
			set location <- {cos (float((class-1)/nbClass)*360)*tmpradius +50,sin (float((class-1)/nbClass)*360)*tmpradius +50,0};
		} 
						
		/*reflex shuffleClass{	
			set class <- rnd(nbClass-1)+1;
			do setPositionAndColor;
		}*/
						
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
		 
		
		/*reflex update{
			do updatemyNodes;
		}
		action updatemyNodes{			
			set nbAggregatedNodes<-0;
			
			
			ask node as list{
			  if	(class = myself.class) {
				set myself.nbAggregatedNodes <- myself.nbAggregatedNodes+1;
				set myself.color <-color;
			  }	 
		    }	    
		} */
		

		aspect sphere{
			draw geometry (point([location.x,location.y])) color:color   depth:(nbAggregatedNodes/10);//(nbAggregatedNodes/nbAgent)*10;
			//draw geometry: geometry (point([location.x,location.y])) color: color depth:(nbAggregatedNodes/10)*macroNodeSize;
			draw text : 'class' + class +": " + nbAggregatedNodes depth:10 ;
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
		
	/* 	reflex updateMacroEdge {
	 	ask macroEdge as list{
	 		do die;
	 	}
	 	
	 	loop i from: 0 to: nbClass-1{
	      loop j from: 0 to: nbClass-1{
	        let tmp <- interactionMatrix  at {i,j};  
	        if(i!=j){
	        	//write string(i+1) + "<->" + string(j+1) + " average linkage: " + int(tmp);
	          if (int(tmp)>threshold){
	            //write string(i+1) + "<->" + string(j+1) + " average linkage: " + int(tmp);
	            create macroEdge{
	            	set nbAggregatedLinks <- tmp;
	              set src <- macroNodes at (i);
			          set dest <- macroNodes at (j);	
			          set my_macroGraph <- my_macroGraph add_edge (src::dest);
	            }	
	          }
	        }      
	      }
	    }
  	}*/
  	
  	/*reflex initMatrix{
		set interactionMatrix <- 0 as_matrix({nbClass,nbClass});	
	  }*/
		
	}
	
	//species scheduler schedules : shuffle (list(node)) + shuffle (list(edge)) + shuffle (list(macroNode)) + shuffle (list(macroEdge)) + list(macroGraph); 
}

/*experiment generate_graph type: gui {
	output {	
		display test_display type:opengl ambiant_light: 0.5	{
					
			species node aspect: base ; 
			species edge aspect: base ;		
			species macroNode aspect:sphere  position: {120,0};
			species macroEdge aspect:base  position: {120,0};	
			text  text1 value:"Original graph" position: {50,110};
			text  text2 value:"Interaction graph" position: {170,110};
			
		}		
	}		
}*/
