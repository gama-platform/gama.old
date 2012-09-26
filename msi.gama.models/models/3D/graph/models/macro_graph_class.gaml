/**
 *  macro_graph
 *  Author: Arnaud Grignard
 *  Description: Shows how to generate a scale-free graph using a Barabasi-Albert scale-free generator. 
 */

model macro_graph
 
global {
	
	/*
	 * The variable that will store the graph
	 */  
	graph my_graph ;
	

	int colorFactor <-25;
	int nbAgent parameter: 'Number of Agents' min: 1 <- 100 ;
	int nbClass parameter: 'Number of class' min: 1 max:10 <- 10 ;
	int nodeSize parameter: 'Noide size' min: 1 <- 1 ;
	
	init {
		
		 /*
		  * The actual generation of the network. 
		  * Note that for technical reasons, parameters are provided as a gama map.  
		  */
		 set my_graph <- generate_barabasi_albert( [
				"edges_specy"::edgeSpecy,
				"vertices_specy"::nodeSpecy,
				"size"::nbAgent,
				"m"::1
			] );
		
		ask edgeSpecy as list{
			set color <- [rnd(100),rnd(100) ,rnd(100)] as rgb;
		}	
		
		ask nodeSpecy as list{
			do initClass;		
		}
		
		let i<-0;
		create nodeMacroSpecy number: nbClass{
			set location <- {i*10,0};
			set class <-i;
			set i<-i+1;	
		}
		
	 }
	  
}

environment;

entities {

	/*
	 * The specy which will describe nodes. 
	 * Note that these agents will be implicitely
	 * initialized with default x,y random locations.
	 */
	species nodeSpecy  {
		
		int class;
		rgb color;
		geometry shape <- geometry (point([location.x,location.y])) ;  
				
		action initClass{
			set class <- rnd(nbClass);
			set color <-rgb([class*colorFactor, class*colorFactor, class*colorFactor]);
		}
		
		//Update randomly the value of each attribute of the node 
		reflex shuffleClass{			
			set class <- rnd(nbClass);
			set color <-rgb([class*colorFactor, class*colorFactor, class*colorFactor]);
		}
						
		aspect base {
			draw shape: geometry color: color z:nodeSize ; 
		}  		
	}
	
	/*
	 * The specy which will describe edges. 
	 */
	species edgeSpecy  { 
		rgb color;// <- rgb('blue') ; 
		
		aspect base {
			draw color: color ;
			
		}
		
	}
	
	
	species nodeMacroSpecy{
		rgb color;
		int class;
		int nbAggregatedNodes;
		//geometry shape <- circle (10) ;
		
		reflex updatemyNodes{
			
			set nbAggregatedNodes<-0;
			
			ask nodeSpecy as list{

			  if	(class = myself.class) {
				set myself.nbAggregatedNodes <- myself.nbAggregatedNodes+1;
			  }	
		    }
		    set shape <- circle (nbAggregatedNodes/10) ;
		    set color <- rgb([class*colorFactor, class*colorFactor, class*colorFactor]);
		    
		} 
		write  " Class  " + self.class + ": "+ nbAggregatedNodes ;
		
		
		
		aspect base{
			draw shape: geometry color: color z:nbAggregatedNodes/10;
			draw text : 'attribute' + class ;
		}
	}
}
experiment generate_graph type: gui {
	output {	
		/*
		 * This first display is the classical GAMA display: 
		 * agents are represented according to their aspects (shapes)
		 * and location. This provides a spatialized view of the network.
		 * 
		 */
		display test_display type:opengl {
			species nodeSpecy aspect: base ; 
			species edgeSpecy aspect: base ;
			
			species nodeMacroSpecy aspect:base z:0.2;	
		}		
	}		
}
