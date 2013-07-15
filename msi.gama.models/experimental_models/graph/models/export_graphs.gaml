/**
 *  savingmodels
 *  Author: Samuel Thiriot
 *  Description: demonstrates how to write (export) networks
 */

model savingmodels


global {
	
	bool openfiles <- true parameter: 'Open generated files' category: 'graphs' ;
	
	graph my_graph ;
	
	init {
	
		// starting with a network (here it is genereated, but it could be loaded, created from GIS, etc.)
		my_graph  <- generate_watts_strogatz(
				nodeSpecy,
				edgeSpecy,
				200,
				0.05,
				2
		);
		
		// a simple "write" just writes the content of the graph in the console
		write "my_graph : " + my_graph;
		
		// this exports the save view in a text file
		save my_graph type: "text" to: "test.txt" rewrite: true;
		
		// now, most of standard formats are accepted by GAMA
		
		// we could export in TULIP
		save my_graph type: "tlp" to: "../doc/test.tlp" rewrite: true;
		// ... note that aliases are sometimes available for convinience;
		// for instance we could use as well:
		save my_graph type: "tulip" to: "../doc/test2.tlp" rewrite: true;
		// ... or even, in order to define explicitely the underlying library 
		// which exports with more or less talent the network:
		save my_graph type: "gephi.tulip" to: "../doc/test2.tlp" rewrite: true;
		
		// other formats currently include:
		// DGS: "dgs", "graphstream.dgs"
		// GML: "gml", "graphstream.gml", "graphstream.graphml", "gephi.graphml"
		// tikz: "tikz"
		// gexf
		// pajek
		// (soon) UCINET (as lists): dl_list, ucinet_list
		// (soon) UCINET (as matrix): dl_matrix, ucinet_matrix
		// csv
		// GUESS: gdf
		// 
		
		// here are some examples:
		save my_graph type: "dgs" to: "../doc/test.dgs" rewrite: true;
		save my_graph type: "gml" to: "../doc/test.gml" rewrite: true;
		save my_graph type: "tikz" to: "../doc/test.tikz" rewrite: true;
		save my_graph type: "gephi.graphml" to: "../doc/test.bis.gml" rewrite: true;
		save my_graph type: "gephi.gexf" to: "../doc/test.gexf" rewrite: true;
		save my_graph type: "gdf" to: "../doc/test.gdf" rewrite: true;
		save my_graph type: "pajek" to: "../doc/test.net" rewrite: true;
		// (soon) save my_graph type: "dl_list" to: "../doc/test.dl" rewrite: true;
		
		// we could even open these files from GAMA :-)
		// this will open the program configured by default for this filetype
		// this could be nodepad, gephi, etc...
		if (openfiles) {
			
			open file:"../doc/test.net"; 
			
		}
		
		// to check the result, just run this model (init is enough, as network 
		// are saved during the init; then look into the navigator on the left,
		// in the doc/ directory, to view examples of outputs (first do a 
		// right-click / refresh to update the content of the folder).
		
		// you'll observe that exporters have different capabilities, 
		// depending on both the format and the implementation of the exporter:
		// some will export the color and node position and/or the attributes of nodes
		// and/or the attributes of edges. Other will only export the list of nodes and edges.
	 }
	
	  
}

environment ;

entities {

	/*
	 * The specy which will describe nodes. 
	 * Note that these agents will be implicitely
	 * initialized with default x,y random locations.
	 */
	species nodeSpecy  {
		rgb color <- rgb([rnd(255),rnd(255),rnd(255)]) ;
		int age <- rnd(100);
		  
		aspect base { 
			draw circle(1) color: color ;
		} 
		 		
	}
	
	/*
	 * The specy which will describe edges. 
	 */
	species edgeSpecy  { 
		rgb color <- rgb('blue') ; 
		int strengh <- rnd(10);
		
		aspect base {
			draw shape color:color;
			
		}
		
	}
}

experiment load_graph type: gui {
	
	output {
		
		display spatial_view refresh_every: 1 type:opengl {
			species nodeSpecy aspect: base ; 
			species edgeSpecy aspect: base ;
		}
		
	}
	
}
