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
	
	graph macroGraph;
	
	int nbAgent parameter: 'Number of Agents' min: 1 <- 100 category: 'Model';
	int nbClass parameter: 'Number of class' min: 1 max:100 <- 10 category: 'Model';
	int threshold parameter: 'Threshold' min: 1 <- 4 category: 'Model';
	
	int colorFactor parameter: 'color factor' min:1 <-25 category: 'Aspect';
	int nodeSize parameter: 'Node size' min: 1 <- 2 category: 'Aspect';
	int macroNodeSize parameter: 'Macro Node size' min: 1 <- 2 category: 'Aspect';
		
    matrix interactionMatrix;	
	list macroNodes of: macroNode;
	
	init {

		 set my_graph <- generate_barabasi_albert( [
				"edges_specy"::edge,
				"vertices_specy"::node,
				"size"::nbAgent,
				"m"::1
			] );
		
		ask edge as list{
			set color <- [125,125,125] as rgb;
		}	
		
		ask node as list{
			set class <- rnd(nbClass-1)+1;
			set color <-rgb([(class/nbClass)*255, (class/nbClass)*255, (class/nbClass)*255]);	
			
			set color <- color hsb_to_rgb ([class/nbClass,1.0,1.0]);	
		}
		
		let i<-1;
		create macroNode number: nbClass{
			set location <- {rnd(100),rnd(100)};
			set class <-i;
			set i<-i+1;	
			add self to: macroNodes;
		}
		
		set macroGraph <- graph(macroNodes);
		
		//FIXME: If this is call at the beginning of the init block there is some null value in the matrix.
		set interactionMatrix <- 0 as_matrix({nbClass,nbClass});	
		
	 }
	 
	 
	 
	reflex updateMacroEdge{
	 	
	 	ask macroEdge as list{
	 		do die;
	 	}
	 	
	 	loop i from: 0 to: nbClass-1{
	      loop j from: 0 to: nbClass-1{
	        let tmp <- interactionMatrix  at {i,j};
	        if(i!=j){
	          if (int(tmp)>threshold){
	            write string(i+1) + "<->" + string(j+1) + " average linkage: " + int(tmp);
	            create macroEdge{
	              set src <- macroNodes at (i);
			      set dest <- macroNodes at (j);	
			      set macroGraph <- macroGraph add_edge (src::dest);
	            }	
	          }
	        }      
	      }
	    }
  	}
	 
	 
	 reflex initMatrix{
		set interactionMatrix <- 0 as_matrix({nbClass,nbClass});	
	 }
}

environment;

entities {

	species node  {
		
		int class;
		rgb color;
		geometry shape <- geometry (point([location.x,location.y])) ;  
						
		reflex shuffleClass{			
			set class <- rnd(nbClass-1)+1;
			//set color <-rgb([(class/nbClass)*255, (class/nbClass)*255, (class/nbClass)*255]);	
			set color <- color hsb_to_rgb ([class/nbClass,1.0,1.0]);
		}
						
		aspect base {
			draw shape: geometry color: color z:nodeSize ; 
		}  		
	}
	

	species edge  { 
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
	
	
	species macroNode{
		rgb color;
		int class;
		int nbAggregatedNodes;
		
		reflex updatemyNodes{			
			set nbAggregatedNodes<-0;
			
			ask node as list{
			  if	(class = myself.class) {
				set myself.nbAggregatedNodes <- myself.nbAggregatedNodes+1;
				set myself.color <-color;
			  }	 
		    }
		    set shape <- circle ((nbAggregatedNodes/10)*macroNodeSize) ;
		    
		} 
		
        //set location <- {cos((float(class)/float(nbClass))*6.32),sin((float(class)/float(nbClass))*6.32)};
        
		aspect base{
			draw shape: geometry color: color z:(nbAggregatedNodes/10)*macroNodeSize;
			draw text : 'class' + class +": " + nbAggregatedNodes z:10 ;
		}
	}
	
	
	species macroEdge  { 
		rgb color <- rgb("black");
		node src;
		node dest;
		
		aspect base {
			draw geometry: line([src.location,dest.location]) color: color ;
			draw text : 'nblink: ' + interactionMatrix  at {src.class-1,dest.class-1} z:10 at: location;
		}	
	}
}
experiment generate_graph type: gui {
	output {	
		display test_display type:opengl {
			species node aspect: base ; 
			species edge aspect: base ;		
			species macroNode aspect:base  position: {100,0};
			species macroEdge aspect:base  position: {100,0};	
		}		
	}		
}
