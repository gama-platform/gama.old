/**
 *  macro_graph
 *  Author: Arnaud Grignard
 *  Description: Display the number of node according to the class the belong.
 */

model macro_graph
 
global {
	
	/*
	 * The variable that will store the graph
	 */  
	graph my_graph ;
	
	graph macroGraph;
	

	int colorFactor <-25;
	int nbAgent parameter: 'Number of Agents' min: 1 <- 100 ;
	int nbClass parameter: 'Number of class' min: 1 max:10 <- 10 ;
	int nodeSize parameter: 'Noide size' min: 1 <- 1 ;
	
	int threshold parameter: 'Threshold' min: 1 <- 4 ;
	
	
	matrix interactionMatrix <-matrix([[0,0,0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0,0,0,0],
       [0,0,0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0,0,0,0],
       [0,0,0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0,0,0,0]]);
	

	
	list macroNodes of: macroNode;
	
	
	init {
		
		 
		 
		 /*
		  * The actual generation of the network. 
		  * Note that for technical reasons, parameters are provided as a gama map.  
		  */
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
			set color <-rgb([class*colorFactor, class*colorFactor, class*colorFactor]);	
			//set color <-rgb([rnd(class*colorFactor), rnd(class*colorFactor), rnd(class*colorFactor)]);		
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
	 	
	 	//remove all the existing edges.
	 	ask macroEdge as list{
	 		do die;
	 	}
	 	
	 	loop i from: 0 to: nbClass-1{
              loop j from: 0 to: nbClass-1{
                let tmp <- interactionMatrix  at {i,j};

                if(i!=j){
                	if (int(tmp)>threshold){
	                	write string(i+1) + " and " + string(j+1) + "have linked than" + threshold + "times";
	                	create macroEdge{
	                	  set src <- macroNodes at (i);
					      set dest <- macroNodes at (j);
					     /*  loop while: (macroGraph contains_edge (src::dest)) {
					      	set src <- macroNodes at i;
					        set dest <- macroNodes at j;
					      }*/	
					      set macroGraph <- macroGraph add_edge (src::dest);
	                	}	
                   }
                }      
              }
           }
	 }
	 
	 //Reinitialize the matrix with 0,0 at each iteration
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
			set color <-rgb([class*colorFactor, class*colorFactor, class*colorFactor]);	
			//set color <-rgb([rnd(class*colorFactor), rnd(class*colorFactor), rnd(class*colorFactor)]);
		}
						
		aspect base {
			draw shape: geometry color: color z:nodeSize ; 
		}  		
	}
	
	/*
	 * The specy which will describe edges. 
	 */
	species edge  { 
		rgb color;
		
		reflex updateInteractionMatrix{													
				let src type:node<- my_graph source_of(self);
				let dest type:node <- my_graph target_of(self);	
				//write string(src.class) + "<->" + string(dest.class);
				
				let tmp <- interactionMatrix  at {src.class-1,dest.class-1};
				//write "+1 in matrix " + string(src.class) + string(dest.class) + "=" + tmp;
				let tmp2 <-int(tmp)+1;
				put tmp2 at: {src.class-1,dest.class-1} in: interactionMatrix;
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
				//set myself.color <-color;
			  }	 
		    }
		    set shape <- circle (nbAggregatedNodes/10) ;
		    set color <-rgb([class*colorFactor, class*colorFactor, class*colorFactor]);	
		} 
		

		aspect base{
			draw shape: geometry color: color z:nbAggregatedNodes/10;
			draw text : 'class' + class +": " + nbAggregatedNodes z:10 ;
		}
	}
	
	
	species macroEdge  { 
		rgb color <- rgb("blue");
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
			species macroNode aspect:base  position: {110,0};
			species macroEdge aspect:base  position: {110,0};	
		}		
	}		
}
