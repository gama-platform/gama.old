/**
 *  macro_graph
 *  Author: Arnaud Grignard
 *  Description: Display the number of node according to the class they belong.
 */
 
 
/* Different way to compute the linkage
	single_linkage
	average_linkage
	complete_linkage
	houssedorf
*/


/* Need to distinguish aggregation != hierarchical
 * 
 * 
 */

/*  dendrogramme */

model macro_graph
 
global {
	
	graph my_graph ;
	
	int nbAgent parameter: 'Number of Agents' min: 1 <- 500 category: 'Model';
	int nbTypeOfClass parameter: "Type of class" min:0 <-1 category: 'Model';
	int nbValuePerClass parameter: 'Number of value per class' min: 1 max:100 <- 15 category: 'Model';
	int threshold parameter: 'Threshold' min: 0 <- 0 category: 'Model';
	int m_barabasi parameter: 'Edges density' min: 1 <- 2 category: 'Model';
		
	int nodeSize parameter: 'Node size' min: 1 <- 2 category: 'Aspect';
	int macroNodeSize parameter: 'Macro Node size' min: 1 <- 2 category: 'Aspect';
	
	int zoomFactor <- nbTypeOfClass;
			
    list<matrix> interactionMatrix size:nbTypeOfClass;	
	list<macroNode> macroNodes;
	
	init {


        my_graph <- generate_barabasi_albert(node,edge,nbAgent,m_barabasi);

		ask node as list{
			loop i from:0 to:nbTypeOfClass-1{
				classVector[i] <- rnd(nbValuePerClass-1)+1;
 			}		
		}
		
		ask edge as list{
			set self.color <- [125,125,125] as rgb;
		}	
		
		
		
		let i<-1;
		create macroNode number: nbValuePerClass{	 
			set self.class <-i;
			set location <- {(cos (float((class-1)/nbValuePerClass)*360)*50 +50),(sin (float((class-1)/nbValuePerClass)*360)*50+50),0};
			set color <- hsb (i/nbValuePerClass,1.0,1.0);
			set i<-i+1;	
			add self to: macroNodes;
			do updatemyNodes;
		}
		
		//set my_macroGraph <- graph(macroNodes);
		
		/* macroGraph is created in last to be sure that all the agent update their state before that the macroGraph does something
		 * A another possibility can be to define a scheduler like:
		 * species scheduler schedules : shuffle (list(node) + list(edge) +list(macroNode) + list(macroEdge));
		 * without forgetting to disable the scheduling of each species (e.g species node schedules [])
		 */
		create macroGraph;
		
		create scheduler;

		//FIXME: If this is call at the beginning of the init block there is some null value in the matrix.
		loop i from:0 to:nbTypeOfClass-1{
				interactionMatrix[i] <- 0 as_matrix({nbValuePerClass,nbValuePerClass});
 		}
 		
 		//do ModaviInit;
 		
 		
	 }
}

environment;

entities {

	species node schedules:[] {
		
		list<int> classVector size:nbTypeOfClass;
		list<point> posVector size:nbTypeOfClass;
		list<rgb> colorList size:nbTypeOfClass;
		rgb color;
								
		reflex shuffleClass{
			loop i from:0 to: nbTypeOfClass-1{
				classVector[i] <- rnd(nbValuePerClass-1)+1;
			}	
		}
 		
		aspect real {			
			draw shape color: rgb('white') depth:nodeSize ; 
			//draw sphere(nodeSize) color: rgb('white');
		} 
						
		aspect proxy {			
			draw shape color: rgb('blue') depth:nodeSize ;
			//draw sphere(nodeSize) color: rgb('blue'); 
		}  
				
		aspect classGenericColored{
			loop i from:0 to: nbTypeOfClass-1{
				let tmpradius <- rnd(25)+25;
			    colorList[i]<- color hsb_to_rgb ([classVector[i]/nbValuePerClass,1.0,1.0]);					
			    posVector[i] <- {(location.x+i*110)*(1/zoomFactor),(location.y)*(1/zoomFactor),0}; 
			    draw geometry (point(posVector[i])) color: rgb(colorList[i])  depth:nodeSize/zoomFactor ; 
			    //draw sphere(nodeSize/zoomFactor) color: rgb(colorList[i]) at: point(posVector[i]) ;
			    
			}
		}
		
		aspect classGenericSpatialized {
			loop i from:0 to: nbTypeOfClass-1{
				let tmpradius <- rnd(25)+25;
			    colorList[i]<- color hsb_to_rgb ([classVector[i]/nbValuePerClass,1.0,1.0]);					
			    posVector[i] <- {((cos (float((classVector[i]-1)/nbValuePerClass)*360)*tmpradius +50)+i*110)*(1/zoomFactor),(sin (float((classVector[i]-1)/nbValuePerClass)*360)*tmpradius +50)*(1/zoomFactor),0}; 
			    draw geometry(point(posVector[i])) color: rgb(colorList[i])  depth:nodeSize/zoomFactor ;
			    //draw sphere(nodeSize/zoomFactor) color: rgb(colorList[i]) at: point(posVector[i]) ; 
			}
		}	
	}
	

	species edge schedules:[] { 
		rgb color;
		node src;
		node dest;
		
		reflex updateInteractionMatrix{
			loop i from:0 to: nbTypeOfClass-1{															
				set src <- my_graph source_of(self);
				set dest <- my_graph target_of(self);
				int tmp <- int(interactionMatrix[i]  at {(src.classVector[i]-1),(dest.classVector[i]-1)});
				interactionMatrix[i][src.classVector[i]-1,dest.classVector[i]-1] <- (tmp+1);
			}
		} 
		 
		aspect base {
			draw shape color: color ;
		}	
		
		aspect edgeGenericSpatialized{
			loop i from:0 to: nbTypeOfClass-1{
			  if ((src != nil) and (dest !=nil) ){
				draw line( [ point(src.posVector[i]) , point(dest.posVector[i])] ) color:color;
			  }
			}
		}
	}
	
	species macroNode schedules:[]{
		rgb color;
		int class;
		list<int> nbAggregatedNodes size:nbTypeOfClass;
		list<point> posVector  size:nbTypeOfClass;
		 
		reflex update{
			do updatemyNodes;
		}
		action updatemyNodes{
			loop i from:0 to: nbTypeOfClass-1{			
				nbAggregatedNodes[i]<-0;
				ask node as list{
				  if	(classVector[i] = myself.class) {
					myself.nbAggregatedNodes[i] <- myself.nbAggregatedNodes[i]+1;
				  }	 
			    }
		    }	    
		} 
		
		aspect sphere{
			draw geometry (point([location.x,location.y]))  color: color depth:(nbAggregatedNodes[0]/10)*macroNodeSize;
			//draw sphere((nbAggregatedNodes[0]/10)*macroNodeSize) color: color at: point([location.x,location.y]) ;
		}
		
		aspect Generic{
			loop i from:0 to: nbTypeOfClass-1
			{
			posVector[i] <- {(location.x+i*150)*(1/zoomFactor),(location.y)*(1/zoomFactor),0};	
			draw geometry (point(posVector[i])) color: color depth:(nbAggregatedNodes[i]/10)*macroNodeSize*(1/zoomFactor);
			//draw sphere((nbAggregatedNodes[i]/10)*macroNodeSize*(1/zoomFactor)) color: color at: point(posVector[i]) ;
			}
		}
	}
	
	
	species macroEdge schedules:[] { 
		rgb color <- rgb("black");
		macroNode src;
		macroNode dest;
		list<int> nbAggregatedLinkList size:nbTypeOfClass;
		
		aspect base {
			loop i from:0 to: nbTypeOfClass-1{
				if(nbAggregatedLinkList[i]>threshold){
				draw geometry: (line([src.posVector[i],dest.posVector[i]]) buffer ((nbAggregatedLinkList[i]^2.5)/(nbAgent*zoomFactor))) color: [125,125,125] as rgb border:[125,125,125] as rgb; 	
				}
			}
		}	
	}
	
	species macroGraph schedules:[] {
		

  	
   reflex updateAllMacroEdge {	
	 	ask macroEdge as list{
	 		do die;
	 	}
	 	
	 	loop h from:0 to: nbTypeOfClass-1{
		 	loop i from: 0 to: nbValuePerClass-1{
		      loop j from: 0 to: nbValuePerClass-1{
		        int tmp <- interactionMatrix[h]  at {i,j}; 

		        if(i!=j){
		            create macroEdge{
		              nbAggregatedLinkList[h] <- tmp;
		              set src <- macroNode(macroNodes at (i));
				      set dest <- macroNode(macroNodes at (j));	
		            }	  
		        }      
		      }
		    }
	    }
  	}
  	
  	reflex initMatrix{
  		loop i from:0 to:nbTypeOfClass-1{
  		  interactionMatrix[i] <- 0 as_matrix({nbValuePerClass,nbValuePerClass});	
  		}
			
	  }
		
	}
	
	species scheduler schedules : shuffle (list(node)) + shuffle (list(edge)) + shuffle (list(macroNode)) + shuffle (list(macroEdge)) + list(macroGraph); 
}


experiment MODAVI type: gui {
	output {			
		display MODAVI type:opengl ambient_light: 100{
			species node aspect: classGenericColored; 
			species edge aspect: edgeGenericSpatialized;
			species macroNode aspect:Generic position: {0,0,25};
			species macroEdge aspect:base position: {0,0,25};	
		}
	}		
}

experiment MODAVI_Multiple_View type: gui {
	output {	
		
		display RealModel  type:opengl ambient_light: 100{
		  species node aspect: real ; 	
		}
		
		display MODAVI type:opengl ambient_light: 100{
			species node aspect: classGenericColored; 
			species edge aspect: edgeGenericSpatialized;
			species macroNode aspect:Generic position: {125,0.0,0};
			species macroEdge aspect:base position: {125,0.0,0};	
		}
		
		display MODAVI_3D type:opengl ambient_light: 100{
			species node aspect: real z:0;
			species node aspect: proxy z:0.3; 
			species edge aspect: base z:0.3;
			species node aspect: classGenericSpatialized z:0.6; 
			species edge aspect: edgeGenericSpatialized z:0.6;
			species macroNode aspect:Generic z:0.9;
			species macroEdge aspect:base z:0.9;	
		}
		
	}		
}

experiment MODAVI_3D_Animated type: gui {
	output {			
		display  AnimatedView type:opengl ambient_light: 100{
			species node aspect: real z:0;
			species node aspect: classGenericColored position:{0,0,sin(time)*10}; 
			species edge aspect: edgeGenericSpatialized position:{0,0,sin(time)*10};
			species macroNode aspect:Generic position:{0,0,sin(time)*10*2};
			species macroEdge aspect:base position:{0,0,sin(time)*10*2};	
		}
	}		
}
