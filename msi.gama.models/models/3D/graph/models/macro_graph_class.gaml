/**
 *  generate_graph_barabasi_albert
 *  Author: Samuel Thiriot
 *  Description: Shows how to generate a scale-free graph using a Barabasi-Albert scale-free generator. 
 * Nothing "moves" into this first model.
 */

model generate_graph_barabasi_albert
 
global {
	
	/*
	 * The variable that will store the graph
	 */  
	graph my_graph ;
	
	int nb_Class <-10;
	int colorFactor <-25;
	
	init {
		
		 /*
		  * The actual generation of the network. 
		  * Note that for technical reasons, parameters are provided as a gama map.  
		  */
		 set my_graph <- generate_barabasi_albert( [
				"edges_specy"::edgeSpecy,
				"vertices_specy"::nodeSpecy,
				"size"::500,
				"m"::2
			] );
		
		ask edgeSpecy as list{
			set color <- [rnd(100),rnd(100) ,rnd(100)] as rgb;
		}	
		
		let i<-0;
		create nodeMacroSpecy number: nb_Class{
			set location <- {i*10,0};
			set aggregatedAttribute <-i;
			set i<-i+1;		
		}
		
		let i<-0;
		ask nodeSpecy as list{
			do initClass;
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
			set class <- rnd(nb_Class);
			set color <-rgb([class*colorFactor, class*colorFactor, class*colorFactor]);
		}
		
		//Update randomly the value of each attribute of the node 
		reflex shuffleClass{			
			set class <- rnd(nb_Class);
			set color <-rgb([class*colorFactor, class*colorFactor, class*colorFactor]);
		}
						
		aspect base {
			draw shape: geometry color: color z:0.5 ; 
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
		int aggregatedAttribute;
		int nbAggregatedNodes;
		//geometry shape <- circle (10) ;
		
		reflex updatemyNodes{
			write self.name + " update attribute  " + self.aggregatedAttribute;
			set nbAggregatedNodes<-0;
			
			ask nodeSpecy as list{

			  if	(class = myself.aggregatedAttribute) {
				set myself.nbAggregatedNodes <- myself.nbAggregatedNodes+1;
			  }	
		    }
		    set shape <- circle (nbAggregatedNodes/10) ;
		    set color <- rgb([nbAggregatedNodes*colorFactor, nbAggregatedNodes*colorFactor, nbAggregatedNodes*colorFactor]);
		    
		} 
		write "nbAggregatedNodes" + nbAggregatedNodes;
		
		
		
		aspect base{
			draw shape: geometry color: color z:nbAggregatedNodes/10;
			draw text : 'attribute' + aggregatedAttribute ;
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
