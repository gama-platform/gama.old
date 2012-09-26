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
	
	int nb_Attributes <-10;
	
	init {
		
		 /*
		  * The actual generation of the network. 
		  * Note that for technical reasons, parameters are provided as a gama map.  
		  */
		 set my_graph <- generate_barabasi_albert( [
				"edges_specy"::edgeSpecy,
				"vertices_specy"::nodeSpecy,
				"size"::100,
				"m"::2
			] );
		
		ask edgeSpecy as list{
			set color <- [rnd(100),rnd(100) ,rnd(100)] as rgb;
		}	
		
		let i<-0;
		create nodeMacroSpecy number: nb_Attributes{
			set location <- {i*10,0};
			set aggregatedAttribute <-i;
			set i<-i+1;		
		}
		
		let i<-0;
		ask nodeSpecy as list{
			do initAttribute;
			set attributeId <-i;
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
		
		int attributeId;
		rgb color <- [rnd(255),rnd(255) ,rnd(255)] as rgb;
		list myAttributes of:int;
		geometry shape <- geometry (point([location.x,location.y])) ;  
		
		//Initialize each attribute of the node
		action initAttribute{
			loop i from:1 to: nb_Attributes{
				add rnd(100) to: myAttributes;
				set color <-rgb([attributeId*10, attributeId*10, attributeId*10]);
				}
		}
		
		//Update randomly the value of each attribute of the node 
		reflex shuffleAttribute{			
			set myAttributes <- shuffle(myAttributes);
		}
				
		aspect base {
			let colorValue <- (location.x + location.y);
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
			
			//Check the value of the aggregatedAttribute of each node to update the number of node aggregated.
			ask nodeSpecy as list{
			write self.name + string( myAttributes at myself.aggregatedAttribute);
			if	(myAttributes at myself.aggregatedAttribute >50) {
				set myself.nbAggregatedNodes <- myself.nbAggregatedNodes+1;
			}	
		    }
		    set shape <- circle (nbAggregatedNodes/10) ;
		    set color <- rgb([aggregatedAttribute*50, aggregatedAttribute*50, aggregatedAttribute*50]);//colorValue <- ((self.location.x + self.location.y))*255;
		    
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
