/**
 *  modavi
 * 
 *  Author: Arnaud Grignard
 * 
 *  Description: Multi-level Online Data Analysis, Visualization and Interaction
 * 
 *  From a reference model with node of a given class, a spatial graph is created 
 *  (or a barabasi graph if spatialGraph is set to false) in the advanced view to 
 *  represent the interaction in the reference model.
 *  An abstract view/controller is created to summarize the interaction in the advanced view
 *  in a macro graph and control the reference model by defining an action (user_command) 
 *  for each macroNode and macroEdge.
 */


model modavi
 
global {
	
	graph<node_agent,edge_agent> my_graph ;
	
	int nbAgent parameter: 'Number of Agents' min: 1 <- 500 category: 'Model';
	int nbValuePerClass parameter: 'Number of value per class' min: 1 max:100 <- 15 category: 'Model';
	bool spatialGraph parameter: 'Spatial Graph' <- true category: 'Model';
	float distance parameter: 'Distance' min: 1.0<- 10.0 category: 'Model';
	int threshold parameter: 'Threshold' min: 0 <- 0 category: 'Model';

		
	int nodeSize parameter: 'Node size' min: 1 <- 1 category: 'Aspect';
	int macroNodeSize parameter: 'Macro Node size' min: 1 <- 2 category: 'Aspect';
	
	int nbTypeOfClass <-1;
	
	int zoomFactor <- nbTypeOfClass;

	list<matrix<int>> interactionMatrix <-list_with(nbTypeOfClass,matrix([0]));
    int nbEdgeMax;
    
    reflex updateInteractionMatrix{
    	ask edge_agent{
			loop i from:0 to: nbTypeOfClass-1{															
				set src <- my_graph source_of(self);
				set dest <- my_graph target_of(self);
				int tmp <- (interactionMatrix[i]  at {(src.classVector[i]-1),(dest.classVector[i]-1)});
				interactionMatrix[i][src.classVector[i]-1,dest.classVector[i]-1] <- (tmp+1);
			}
		}
	}
	
	reflex computeNbEdgeMax{
		nbEdgeMax <-1;
		ask macroEdge{
			if(nbAggregatedLinkList[0] > nbEdgeMax){
				nbEdgeMax <-nbAggregatedLinkList[0];
			}	
		}
	}

	
	init {
		
		do InitInteractionMatrix;
		
		if(spatialGraph){
			create node_agent number:nbAgent;
			my_graph <- graph<node_agent, edge_agent>(as_distance_graph(node_agent, (["distance"::distance, "species"::edge_agent])));
			
		}
        else{
          my_graph <- graph<node_agent, edge_agent>(generate_barabasi_albert(node_agent,edge_agent,nbAgent,2,true));	
        }
        

		ask node_agent as list{
			loop i from:0 to:nbTypeOfClass-1{
				classVector[i] <- rnd(nbValuePerClass-1)+1;
 			}		
		}

		int i<-1;
		create macroNode number: nbValuePerClass{	 
			class <-i;
			location <- {(cos (((class-1)/nbValuePerClass)*360)*50 +50),(sin (((class-1)/nbValuePerClass)*360)*50+50),0};
			color <- hsb (i/nbValuePerClass,1.0,1.0);
			do updatemyNodes;
			set i<-i+1;	
		}
			
		create macroGraph;
	 }
	 
	 action InitInteractionMatrix{
		 loop i from:0 to:nbTypeOfClass-1{
				interactionMatrix[i] <- 0 as_matrix({nbValuePerClass,nbValuePerClass});
 		  }	
	}
}



	species node_agent  {
		
		rgb color;
		
		list<int> classVector <- list_with (nbTypeOfClass,0);
		list<point> posVector <- list_with (nbTypeOfClass,{0,0});
		list<rgb> colorList <- list_with (nbTypeOfClass, rgb(0,0,0));
								
		reflex shuffleClass{
			loop i from:0 to: nbTypeOfClass-1{
				classVector[i] <- rnd(nbValuePerClass-1)+1;
			}	
		}
 		
		aspect real {			 
			draw sphere(nodeSize) color: colorList[0];
		} 
								
		aspect coloredByClass{
			loop i from:0 to: nbTypeOfClass-1{
			    colorList[i]<- hsb (classVector[i]/nbValuePerClass,1.0,1.0);					
			    posVector[i] <- {(location.x+i*110)*(1/zoomFactor),(location.y)*(1/zoomFactor),0};  
			    draw sphere(nodeSize/zoomFactor) color: colorList[i] at: posVector[i] ;   
			}
		}
	
	}
	

	species edge_agent { 
		rgb color;
		node_agent src;
		node_agent dest;
			 
		aspect base {
			draw shape color: rgb(125,125,125);
		}	
		
		aspect edgeGenericSpatialized{
			loop i from:0 to: nbTypeOfClass-1{
			  if ((src != nil) and (dest !=nil) ){
				draw line( [ (src.posVector[i]) , (dest.posVector[i])] ) color:rgb(125,125,125);
			  }
			}
		}
	}
	
	species macroNode{
		rgb color;
		int class;
		list<int> nbAggregatedNodes <- list_with(nbTypeOfClass,0);
		list<point> posVector <-list_with(nbTypeOfClass,{0,0});
		 
		reflex update{
			do updatemyNodes;
		}
		action updatemyNodes{
			loop i from:0 to: nbTypeOfClass-1{			
				nbAggregatedNodes[i]<-0;
				ask node_agent as list{
				  if	(classVector[i] = myself.class) {
					myself.nbAggregatedNodes[i] <- myself.nbAggregatedNodes[i]+1;
				  }	 
			    }
		    }	    
		} 
		
		aspect sphere{
			draw sphere((nbAggregatedNodes[0]/10)*macroNodeSize) color: color at: point([location.x,location.y]) ;
		}
		
		aspect Generic{
			loop i from:0 to: nbTypeOfClass-1
			{
			posVector[i] <- {(location.x+i*150)*(1/zoomFactor),(location.y)*(1/zoomFactor),0};	
			draw sphere((nbAggregatedNodes[i]/10)*macroNodeSize*(1/zoomFactor)) color: color at: posVector[i] ;
			}
		}
		
		//This action only works when having nbTypeOfClass=1
		action removeMicroNode{
			ask node_agent as list{
				  if	(classVector[0] = myself.class) {
				      do die;
				  }	 
	         }
		}
		
		user_command "Remove all micro node" action: removeMicroNode;
	}
	
	
	species macroEdge  { 
		rgb color <- rgb("black");
		macroNode src;
		macroNode dest;
		list<int> nbAggregatedLinkList <- list_with(nbTypeOfClass,0);
		
		aspect base {
			loop i from:0 to: nbTypeOfClass-1{
				if(nbAggregatedLinkList[i]>threshold){
				draw geometry: (line([src.posVector[i],dest.posVector[i]]) buffer ((nbAggregatedLinkList[i])/((length(edge_agent)))*nbEdgeMax)) color: rgb(125,125,125) border:rgb(125,125,125); 	
				}
			}
		}
		
		action removeMicroEdge{
			ask edge_agent as list{
				  if	((self.src.classVector[0] =  myself.src.class) and (self.dest.classVector[0] =  myself.dest.class)) {
				      do die;
				  }	 
	         }
		}
		
		user_command "Remove all micro edge" action: removeMicroEdge;	
	}
	
	species macroGraph {
		

  	
   reflex updateAllMacroEdge {	
	 	ask macroEdge as list{
	 		do die;
	 	}
	 	
	 	loop h from:0 to: nbTypeOfClass-1{
		 	loop i from: 0 to: nbValuePerClass-1{
		      loop j from: 0 to: nbValuePerClass-1{
		        int tmp <- interactionMatrix[h] at {i,j}; 

		        if(i!=j){
		            create macroEdge{
		              nbAggregatedLinkList[h] <- tmp;
		              set src <- macroNode[i];
				      set dest <- macroNode[j];
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



experiment MODAVI type: gui {
	output {			
		display MODAVI type:opengl ambient_light: 10 diffuse_light:100 draw_env:false {
			graphics 'ReferenceModel'{
				draw text:"Reference model" at:{200,50,0} size:5 color: rgb('black') bitmap:false;
			}
			species node_agent aspect: real position:{100,0,0.01} ;
			
			graphics 'View1'{
				draw text:"Advanced view" at:{50,210,0} size:5 color: rgb('black') bitmap:false;
			}
			species node_agent aspect: coloredByClass position: {0,100,0.02};
			species edge_agent aspect: edgeGenericSpatialized position: {0,100,0.02};
			
			graphics 'AbstractView'{
				draw text:"Abstract view/controller" at:{250,210,0} size:5 color: rgb('black') bitmap:false;
			}
			species macroNode aspect:Generic position: {200,100,0.01};
			species macroEdge aspect:base position: {200,100,0.01};	
		}
	}		
}



