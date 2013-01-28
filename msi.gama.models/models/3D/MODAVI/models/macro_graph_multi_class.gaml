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


/* Need to distinguish aggregation != hierarchical
 * 
 * 
 */

/*  dendrogramme */

model macro_graph
 
global {
	
	graph my_graph ;
	
	graph my_macroGraph;
	
	int nbAgent parameter: 'Number of Agents' min: 1 <- 100 category: 'Model';
	int nbTypeOfClass parameter: "Type of class" min:0 <-0 category: 'Model';
	int nbClass parameter: 'Number of class' min: 1 max:100 <- 15 category: 'Model';
	int threshold parameter: 'Threshold' min: 0 <- 0 category: 'Model';
	int m_barabasi parameter: 'Edges density' min: 1 <- 1 category: 'Model';
	
	bool microLayout parameter: 'Micro Layout' <- true category: 'Visualization';
	float macroLayer parameter: "Macro Layer Z" min:0.0 max:1.0 <-0.2 category: 'Visualization';
	
	int colorFactor parameter: 'color factor' min:1 <-25 category: 'Aspect';
	int nodeSize parameter: 'Node size' min: 1 <- 2 category: 'Aspect';
	int macroNodeSize parameter: 'Macro Node size' min: 1 <- 2 category: 'Aspect';
	
	
		
    matrix interactionMatrix;	
	list macroNodes of: macroNode;
	
	//FIXME: Does not work
	//draw circle(1);
	
	init {

		 set my_graph <- generate_barabasi_albert( [
				"edges_specy"::edge,
				"vertices_specy"::node,
				"size"::nbAgent,
				"m"::m_barabasi
			] );
		
		
		ask node as list{
			loop i from:0 to:nbTypeOfClass{
				classVector[i] <- rnd(nbClass-1)+1;
 			}		
		}
		
		ask edge as list{
			set color <- [125,125,125] as rgb;
		}	
		
		
		let i<-1;
		create macroNode number: nbClass{	
			set class <-i;
			set location <- {cos (float((class-1)/nbClass)*360)*50 +50,sin (float((class-1)/nbClass)*360)*50+50,0};
			color <- color hsb_to_rgb ([i/nbClass,1.0,1.0]);
			set i<-i+1;	
			add self to: macroNodes;
			do updatemyNodes;
		}
		
		set my_macroGraph <- graph(macroNodes);
		
		/* macroGraph is created in last to be sure that all the agent update their state before that the macroGraph does something
		 * A another possibility can be to define a scheduler like:
		 * species scheduler schedules : shuffle (list(node) + list(edge) +list(macroNode) + list(macroEdge));
		 * without forgetting to disable the scheduling of each species (e.g species node schedules [])
		 */
		create macroGraph;
		
		create scheduler;

		//FIXME: If this is call at the beginning of the init block there is some null value in the matrix.
		set interactionMatrix <- 0 as_matrix({nbClass,nbClass});	
		
		
	 }
}

environment;

entities {

	species node schedules:[] {
		
		list classVector <- [0];
		//list<int> classVector size: 3 fill_with: {0,0,0};
		list<point> posVector <- [{0,0,0},{0,0,0},{0,0,0},{0,0,0},{0,0,0},{0,0,0}];
		list colorList <- [[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0]];
		rgb color;
								
		reflex shuffleClass{
			loop i from:0 to: nbTypeOfClass{
				classVector[i] <- rnd(nbClass-1)+1;
			}	
		}
						
		aspect base {			
			draw shape color: rgb('blue') z:nodeSize ; 
		}  
		
		aspect class0 {
			let tmpradius <- rnd(25)+25;
			colorList[0]<- color hsb_to_rgb ([classVector[0]/nbClass,1.0,1.0]);					
			posVector[0] <- {(cos (float((classVector[0]-1)/nbClass)*360)*tmpradius +50),(sin (float((classVector[0]-1)/nbClass)*360)*tmpradius +50),0}; 
			draw geometry (point(posVector[0])) color: rgb(colorList[0])  z:nodeSize ;
			
		}
		
		aspect class1 {
			let tmpradius <- rnd(25)+25;
			colorList[1] <- color hsb_to_rgb ([classVector[1]/nbClass,1.0,1.0]);
			posVector[1] <- {(cos (float((classVector[1]-1)/nbClass)*360)*tmpradius +50),(sin (float((classVector[1]-1)/nbClass)*360)*tmpradius +50),10}; 
			draw geometry (point(posVector[1])) color: rgb(colorList[1])  z:nodeSize ;
		}	
		
		
		
		aspect classGenericColored{
			loop i from:0 to: nbTypeOfClass{
				let tmpradius <- rnd(25)+25;
			    colorList[i]<- color hsb_to_rgb ([classVector[i]/nbClass,1.0,1.0]);					
			    posVector[i] <- {location.x+i*110,location.y,0}; 
			    draw geometry (point(posVector[i])) color: rgb(colorList[i])  z:nodeSize ; 
			}
		}
		
		aspect classGenericSpatialized{
			loop i from:0 to: nbTypeOfClass{
				let tmpradius <- rnd(25)+25;
			    colorList[i]<- color hsb_to_rgb ([classVector[i]/nbClass,1.0,1.0]);					
			    posVector[i] <- {(cos (float((classVector[i]-1)/nbClass)*360)*tmpradius +50)+i*110,(sin (float((classVector[i]-1)/nbClass)*360)*tmpradius +50),0}; 
			    draw geometry (point(posVector[i])) color: rgb(colorList[i])  z:nodeSize ; 
			}
		}	
	}
	

	species edge schedules:[] { 
		rgb color;
		node src;
		node dest;
		
		
		
		reflex updateInteractionMatrix{															
				set src <- my_graph source_of(self);
				set dest <- my_graph target_of(self);	
				let tmp <- interactionMatrix  at {src.classVector[0]-1,dest.classVector[0]-1};
				put (int(tmp)+1) at: {src.classVector[0]-1,dest.classVector[0]-1} in: interactionMatrix;
		} 
		
		aspect base {
			draw shape color: color ;
		}	
		aspect edge0{
		//Line from src to dest			
			if ((src != nil) and (dest !=nil) ){
				draw geometry: line( [ point(src.posVector[0]) , point(dest.posVector[0])] ) color:color;
			}
		}
		
		aspect edge1{
		//Line from src to dest			
			if ((src != nil) and (dest !=nil) ){
				draw geometry: line( [ point(src.posVector[1]) , point(dest.posVector[1])] ) color:color;
			}
		}
		
		aspect edgeGenericSpatialized{
			loop i from:0 to: nbTypeOfClass{
				if ((src != nil) and (dest !=nil) ){
				draw geometry: line( [ point(src.posVector[i]) , point(dest.posVector[i])] ) color:color;
			}
			}
		}
	}
	
	
	species macroNode schedules:[]{
		rgb color;
		int class;
		int nbAggregatedNodes;
		 
		reflex update{
			do updatemyNodes;
		}
		action updatemyNodes{			
			set nbAggregatedNodes<-0;
			
			
			ask node as list{
			  if	(classVector[0] = myself.class) {
				set myself.nbAggregatedNodes <- myself.nbAggregatedNodes+1;
			  }	 
		    }	    
		} 
		
		aspect sphere{
			draw geometry (point([location.x,location.y])) color: color z:(nbAggregatedNodes/10)*macroNodeSize;
		}
	}
	
	
	species macroEdge schedules:[] { 
		rgb color <- rgb("black");
		node src;
		node dest;
		int nbAggregatedLinks;
		
		aspect base {
			if(nbAggregatedLinks>threshold){
			draw geometry: (line([src.location,dest.location]) buffer ((nbAggregatedLinks^2.5)/(nbAgent))) color: [125,125,125] as rgb border:[125,125,125] as rgb; 	
			}
			
		}	
	}
	
	species macroGraph schedules:[] {
		

  	
   reflex updateAllMacroEdge {
			
	 	ask macroEdge as list{
	 		do die;
	 	}
	 	
	 	loop i from: 0 to: nbClass-1{
	      loop j from: 0 to: nbClass-1{
	        let tmp <- interactionMatrix  at {i,j};  
	        if(i!=j){
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
  	
  	reflex initMatrix{
		set interactionMatrix <- 0 as_matrix({nbClass,nbClass});	
	  }
		
	}
	
	species scheduler schedules : shuffle (list(node)) + shuffle (list(edge)) + shuffle (list(macroNode)) + shuffle (list(macroEdge)) + list(macroGraph); 
}
experiment generate_graph type: gui {
	output {	
			
		display OriginalGraph type:opengl ambiant_light: 0.4{
			species node aspect: base ; 
			species edge aspect: base ;
		}	
		
		display Augmented_Graph type:opengl{
			species node aspect: classGenericColored ; 
			species edge aspect: edgeGenericSpatialized ;
			text name:"class1" value:"class1" position:{40,110,0};
			text name:"class2" value:"class2" position:{150,110,0};
			text name:"class3" value:"class3" position:{250,110,0};
		}
				
		display Augmented_Graph_Spatialized type:opengl ambiant_light: 0.4{
					
			species node aspect: classGenericSpatialized ; 
			species edge aspect: edgeGenericSpatialized ;
			
			//species node aspect: class1;
		    //species edge aspect: edge1;
			//species macroNode aspect:sphere  position: {0,0} z:0.2;
			//species macroEdge aspect:base  position: {0,0} z:0.2;
			
			//species node aspect: class1  z:0.4; 
			//species edge aspect: base z:0.4;
			
			
				
			//text  text1 value:"Original graph" position: {50,110};
			//text  text2 value:"Interaction graph" position: {170,110};
			
		}
		display Augmented_Graph_Macro_Spatialized type:opengl ambiant_light: 0.4{
					
			//species node aspect: classGenericSpatialized ; 
			//species edge aspect: edgeGenericSpatialized ;
			
			species macroNode aspect:sphere  position: {0,0} z:0.2;
			species macroEdge aspect:base  position: {0,0} z:0.2;
			
		}		
		
		
		
			
	}		
}
