/**
 *  macro_graph
 *  Author: Arnaud Grignard
 *  Description: Display the number of node according to the class they belong.
 */


model macro_graph
 
global {
	
	graph my_graph ;
	
	int nbAgent parameter: 'Number of Agents' min: 1 <- 500 category: 'Model';
	int nbTypeOfClass parameter: "Type of class" min:1 <-1 category: 'Model';
	int nbValuePerClass parameter: 'Number of value per class' min: 1 max:100 <- 15 category: 'Model';
	int threshold parameter: 'Threshold' min: 0 <- 0 category: 'Model';
	int m_barabasi parameter: 'Edges density' min: 1 <- 2 category: 'Model';
		
	int nodeSize parameter: 'Node size' min: 1 <- 2 category: 'Aspect';
	int macroNodeSize parameter: 'Macro Node size' min: 1 <- 2 category: 'Aspect';
	
	int zoomFactor <- nbTypeOfClass;
			
    list<matrix> interactionMatrix size:nbTypeOfClass;	
    
    reflex updateInteractionMatrix{
    	ask edge{
			loop i from:0 to: nbTypeOfClass-1{															
				set src <- my_graph source_of(self);
				set dest <- my_graph target_of(self);
				int tmp <- int(interactionMatrix[i]  at {(src.classVector[i]-1),(dest.classVector[i]-1)});
				interactionMatrix[i][src.classVector[i]-1,dest.classVector[i]-1] <- (tmp+1);
			}
		}
	}

	
	init {
		
		do InitInteractionMatrix;

        my_graph <- generate_barabasi_albert(node,edge,nbAgent,m_barabasi);

		ask node as list{
			loop i from:0 to:nbTypeOfClass-1{
				classVector[i] <- rnd(nbValuePerClass-1)+1;
 			}		
		}

		let i<-1;
		create macroNode number: nbValuePerClass{	 
			class <-i;
			location <- {(cos (float((class-1)/nbValuePerClass)*360)*50 +50),(sin (float((class-1)/nbValuePerClass)*360)*50+50),0};
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


entities {

	species node  {
		
		rgb color;
		
		list<int> classVector size:nbTypeOfClass;
		list<point> posVector size:nbTypeOfClass;
		list<rgb> colorList size:nbTypeOfClass;
								
		reflex shuffleClass{
			loop i from:0 to: nbTypeOfClass-1{
				classVector[i] <- rnd(nbValuePerClass-1)+1;
			}	
		}
 		
		aspect real {			 
			draw sphere(nodeSize) color: rgb('white');
		} 
								
		aspect coloredByClass{
			loop i from:0 to: nbTypeOfClass-1{
			    colorList[i]<- color hsb_to_rgb ([classVector[i]/nbValuePerClass,1.0,1.0]);					
			    posVector[i] <- {(location.x+i*110)*(1/zoomFactor),(location.y)*(1/zoomFactor),0};  
			    draw sphere(nodeSize/zoomFactor) color: rgb(colorList[i]) at: point(posVector[i]) ;   
			}
		}
	
	}
	

	species edge { 
		rgb color;
		node src;
		node dest;
			 
		aspect base {
			draw shape color: rgb(125,125,125);
		}	
		
		aspect edgeGenericSpatialized{
			loop i from:0 to: nbTypeOfClass-1{
			  if ((src != nil) and (dest !=nil) ){
				draw line( [ point(src.posVector[i]) , point(dest.posVector[i])] ) color:rgb(125,125,125);
			  }
			}
		}
	}
	
	species macroNode{
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
			draw sphere((nbAggregatedNodes[0]/10)*macroNodeSize) color: color at: point([location.x,location.y]) ;
		}
		
		aspect Generic{
			loop i from:0 to: nbTypeOfClass-1
			{
			posVector[i] <- {(location.x+i*150)*(1/zoomFactor),(location.y)*(1/zoomFactor),0};	
			draw sphere((nbAggregatedNodes[i]/10)*macroNodeSize*(1/zoomFactor)) color: color at: point(posVector[i]) ;
			}
		}
		
		//This action only works when having nbTypeOfClass=1
		action removeMicroNode{
			ask node as list{
				  if	(classVector[0] = myself.class) {
				      do die;
				  }	 
	         }
		}
		
		user_command "Remove micro node" action: removeMicroNode;
	}
	
	
	species macroEdge  { 
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
	
	species macroGraph {
		

  	
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
		              set src <- macroNode(list(macroNode) at (i));
				      set dest <- macroNode(list(macroNode) at (j));	
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
}


experiment MODAVI type: gui {
	output {			
		display MODAVI type:opengl ambient_light: 100  draw_env:false{
			graphics ReferenceModel{
				draw rectangle(100,100) at: {150,50,0} empty:true color:rgb('black');
				draw text:"Reference model" at:{200,50,0} size:5 color: rgb('black') bitmap:false;
			}
			species node aspect: real z:0 position: {100,0.0,0};
			
			graphics View1{
				draw rectangle(100,100) at: {50,150,0} empty:true color:rgb('black');
				draw text:"Advanced view" at:{50,210,0} size:5 color: rgb('black') bitmap:false;
			}
			species node aspect: coloredByClass position: {0,100,0};
			species edge aspect: edgeGenericSpatialized position: {0,100,0};
			
			graphics AbstractView{
				draw rectangle(100,100) at: {250,150,0} empty:true color:rgb('black');
				draw text:"Abstract view and Controller" at:{250,210,0} size:5 color: rgb('black') bitmap:false;
			}
			species macroNode aspect:Generic position: {200,100,0};
			species macroEdge aspect:base position: {200,100,0};	
		}
	}		
}



