/**
* Name: Modavi
* Author: Arnaud Grignard
* Description: From a reference model with node of a given class, a spatial graph is created 
*  (or a barabasi graph if spatialGraph is set to false) in the advanced view to 
*  represent the interaction in the reference model.
*  An abstract view/controller is created to summarize the interaction in the advanced view
*  in a macro graph and control the reference model by defining an action (user_command) 
*  for each macroNode and macroEdge.
* Tags: 3d, graph, gui
*/

model modavi
 
global {
	//Graph of the agents
	graph<node_agent,edge_agent> my_graph ;
	
	//Number of agents to create
	int nbAgent parameter: 'Number of Agents' min: 1 <- 100 category: 'Model';
	//Number of value per class
	int nbValuePerClass parameter: 'Number of value per class' min: 1 max:100 <- 15 category: 'Model';
	//Boolean to know if we display a spatial graph or not
	bool spatialGraph parameter: 'Spatial Graph' <- true category: 'Model';
	//Distance to link two node agents
	float distance parameter: 'Distance' min: 1.0<- 10.0 category: 'Model';
	//Threshold
	int threshold parameter: 'Threshold' min: 0 <- 0 category: 'Model';

	//Size of a node agent
	int nodeSize parameter: 'Node size' min: 1 <- 1 category: 'Aspect';
	//Size of a macro node agent
	int macroNodeSize parameter: 'Macro Node size' min: 1 <- 2 category: 'Aspect';
	
	//Number of type of class
	int nbTypeOfClass <-1;
	
	//Zoom factor
	int zoomFactor <- nbTypeOfClass;

	//List of the different interaction matrices
	list<matrix<int>> interactionMatrix <-list_with(nbTypeOfClass,matrix([0]));
	//Number maximum of edges
    int nbEdgeMax;
    
    //Reflex to update the interaction matrix list
    reflex updateInteractionMatrix{
    	//Ask for each edge agent to update it sources and destination to create the matrix
    	ask edge_agent{
			loop i from:0 to: nbTypeOfClass-1{															
				src <- my_graph source_of(self);
				dest <- my_graph target_of(self);
				int tmp <- (interactionMatrix[i]  at {(src.classVector[i]-1),(dest.classVector[i]-1)});
				interactionMatrix[i][src.classVector[i]-1,dest.classVector[i]-1] <- (tmp+1);
			}
		}
	}
	
	//Reflex to compute te maximum number of edges
	reflex computeNbEdgeMax{
		//Number maximum of edges
		nbEdgeMax <-1;
		//Ask for each macro edge its aggregated link list number
		ask macroEdge{
			if(nbAggregatedLinkList[0] > nbEdgeMax){
				nbEdgeMax <-nbAggregatedLinkList[0];
			}	
		}
	}

	//Initialization of the model
	init {
		//Initialization of the matrix
		do InitInteractionMatrix;
		//If we want a spatial graph in that case we create a graph according to their distance, else we create a barabasi albert graph
		if(spatialGraph){
			create node_agent number:nbAgent;
			my_graph <- graph<node_agent, edge_agent>(as_distance_graph(node_agent, distance, edge_agent)); 
			
		}
        else{
          my_graph <- graph<node_agent, edge_agent>(generate_barabasi_albert(nbAgent * 0.5,5,nbAgent,false,node_agent,edge_agent));	
        }
        
		//For each node agent, we compute its class value
		ask node_agent as list{
			loop i from:0 to:nbTypeOfClass-1{
				classVector[i] <- rnd(nbValuePerClass-1)+1;
 			}		
		}

		int i<-1;
		//Creation of the macronode according to the number of value per class
		create macroNode number: nbValuePerClass{	 
			class <-i;
			location <- {(cos (((class-1)/nbValuePerClass)*360)*50 +50),(sin (((class-1)/nbValuePerClass)*360)*50+50),0};
			color <- hsb (i/nbValuePerClass,1.0,1.0);
			do updatemyNodes;
			i<-i+1;	
		}
		//We finally create the macroGraph
		create macroGraph;
	 }
	 //Action to initialize the interaction Matrix according to the number of type of classes
	 action InitInteractionMatrix{
		 loop i from:0 to:nbTypeOfClass-1{
				interactionMatrix[i] <- 0 as_matrix({nbValuePerClass,nbValuePerClass});
 		  }	
	}
}


	//Species to represent the node_agent
	species node_agent  {
		//Color of the node agent
		rgb color;
		//List of the class
		list<int> classVector <- list_with (nbTypeOfClass,0);
		//List of the position
		list<point> posVector <- list_with (nbTypeOfClass,{0,0});
		//List of the color
		list<rgb> colorList <- list_with (nbTypeOfClass, rgb(0,0,0));
								
		//Shuffle the classes of the node_agent
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
	
	//Species edge_agent to represent the edge of the graph
	species edge_agent { 
		rgb color;
		//Source of the edge
		node_agent src;
		//Target of the edge
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
	//Species representing the macro node agents
	species macroNode{
		rgb color;
		int class;
		//List of all the aggregated nodes
		list<int> nbAggregatedNodes <- list_with(nbTypeOfClass,0);
		//List of all the position
		list<point> posVector <-list_with(nbTypeOfClass,{0,0});
		 
		//Update the nodes of the agents
		reflex update{
			do updatemyNodes;
		}
		//For each classes, find all the nodes with the same classes
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
	
	//Species macroEdge representing the macro edges agents
	species macroEdge  { 
		rgb color <- #black;
		//Source of the macroedge
		macroNode src;
		//Destination of the macroedge
		macroNode dest;
		//List of all the aggregated links
		list<int> nbAggregatedLinkList <- list_with(nbTypeOfClass,0);
		
		aspect base {
			loop i from:0 to: nbTypeOfClass-1{
				if(nbAggregatedLinkList[i]>threshold){
				draw (line([src.posVector[i],dest.posVector[i]]) buffer ((nbAggregatedLinkList[i])/((length(edge_agent)))*nbEdgeMax)) color: rgb(125,125,125) border:rgb(125,125,125); 	
				}
			}
		}
		
		//Action to remove a micro edge
		action removeMicroEdge{
			ask edge_agent as list{
				  if	((self.src.classVector[0] =  myself.src.class) and (self.dest.classVector[0] =  myself.dest.class)) {
				      do die;
				  }	 
	         }
		}
		
		user_command "Remove all micro edge" action: removeMicroEdge;	
	}
	
	//Species macroGraph representing the macro graph composed of macroNode and macroEdge
	species macroGraph {
		

  	//Reflex to update the graph by killing all the previous edges first 
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
		              src <- macroNode[i];
				      dest <- macroNode[j];
		            }	  
		        }      
		      }
		    }
	    }
  	}
  	//Reflex to initialize the matrix
  	reflex initMatrix{
  		loop i from:0 to:nbTypeOfClass-1{
  		  interactionMatrix[i] <- 0 as_matrix({nbValuePerClass,nbValuePerClass});	
  		}	
	  }	
	}



experiment MODAVI type: gui {
	output synchronized: true {			
		display MODAVI type:3d axes:false {
			camera #default location:{world.shape.width*1.5, world.shape.height,world.shape.width*4} target:{world.shape.width*1.5, world.shape.height,0};
			graphics 'ReferenceModel'{
				draw "Reference model" at:{200,50,0} size:5 color: #black perspective:false;
			}
			species node_agent aspect: real position:{100,0,0.01} ;
			
			graphics 'View1'{
				draw "Advanced view" at:{50,210,0} size:5 color: #black perspective:false;
			}
			species node_agent aspect: coloredByClass position: {0,100,0.02};
			species edge_agent aspect: edgeGenericSpatialized position: {0,100,0.02};
			
			graphics 'AbstractView'{
				draw "Abstract view/controller" at:{250,210,0} size:5 color: #black perspective:false;
			}
			species macroNode aspect:Generic position: {200,100,0.01};
			species macroEdge aspect:base position: {200,100,0.01};	
		}
	}		
}



