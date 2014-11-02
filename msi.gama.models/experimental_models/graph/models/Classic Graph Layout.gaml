/**
 *  classicgraphgeneration
 *  Author: Patrick Taillandier & Philippe Caillou
 *  Description: Show how to create scale-free/small-world/complete graphs with layouts
 */

model classicgraphgeneration

global {
	graph the_graph ;
	string graph_type <- "small-world";
	int nb_nodes <- 100;
	float p <- 0.3;
	int k <- 2;
	int m <- 5;
	
	string algo<-"Yifan Hu";
	// -- Force-Directed  (Yifan Hu) Algo's variables/parameters 
	// < Parameters >
	float optimal_distance <- 10.0 min: 1.0; //§
	float regulator <- 0.95 min: 0.0 max: 1.0; //§
	float barnes_hut_theta <- 1.2 min: 0.0; //§
	float step_ratio <- 0.95 min: 0.0 max: 1.0; //§
	float convergence_threshold <- 0.1 min: 0.0; //§
	float initial_step <- 1 min: 0.0; //§
	float strength <- 0.2 min: 0.0 max:1.0; //§
	int max_level <-10 min:1 max:100000;
	point the_bounds_p1 <- {0, 0};
	point the_bounds_p2 <- {100, 100};
	// </ Parameters >
	
	int thread_number<-1;
	bool dissuade_hubs<-false;
	bool linlog_mode<-false;
	bool prevent_overlap<-false;
	float edge_weight_influence<-1;
	float scaling<-2;
	bool stronger_gravity<-false;
	float gravity<-1;
	float tolerance<-0.1;
	bool approximate_repulsion<-false;
	float approximation<-1.2;
	
	
	reflex
	{
		switch algo
		{
			match "Yifan Hu"
			{
				layout_yifanhu graph:the_graph optimal_distance:optimal_distance step_size:initial_step theta:barnes_hut_theta relative_strength:strength quadtree_max_level:max_level; 
			}
			match "Bounded Yifan Hu"
			{
				layout_yifanhu graph:the_graph optimal_distance:optimal_distance step_size:initial_step theta:barnes_hut_theta relative_strength:strength quadtree_max_level:max_level bounded_point1:the_bounds_p1 bounded_point2:the_bounds_p2; 
			}
			match "Yifan Hu default parameter values"
			{
				layout_yifanhu graph:the_graph    ; 
			}
			match "Force Atlas 2"
			{
				layout_forceatlas2 graph: the_graph thread_number:thread_number dissuade_hubs:dissuade_hubs linlog_mode:linlog_mode prevent_overlap:prevent_overlap edge_weight_influence:edge_weight_influence scaling:scaling stronger_gravity:stronger_gravity gravity:gravity tolerance:tolerance approximate_repulsion:approximate_repulsion approximation:approximation   ;
			}
			match "Bounded Force Atlas 2"
			{
				layout_forceatlas2 graph: the_graph thread_number:thread_number dissuade_hubs:dissuade_hubs linlog_mode:linlog_mode prevent_overlap:prevent_overlap edge_weight_influence:edge_weight_influence scaling:scaling stronger_gravity:stronger_gravity gravity:gravity tolerance:tolerance approximate_repulsion:approximate_repulsion approximation:approximation  bounded_point1:the_bounds_p1 bounded_point2:the_bounds_p2  ;
			}
			match "Force Atlas 2 default parameter values"
			{
				layout_forceatlas2 graph: the_graph;
			}

		} 
 }
	
	init {
		switch graph_type {
			match "scale-free" {
				the_graph <- generate_barabasi_albert(node_agent, edge_agent, nb_nodes,m, true);	
			}
			match "small-world" {
				the_graph <- generate_watts_strogatz(node_agent, edge_agent, nb_nodes, p, k, true);	
			}
			match "complete" {
				the_graph <- generate_complete_graph(node_agent, edge_agent, nb_nodes, true);	
			}	
		}
	}
	
}

species edge_agent {
	aspect default {	
		draw shape color: rgb("black");
	}
}

species node_agent {
	aspect default {	
		draw circle(1) color: rgb("red");
	}
}

experiment loadgraph type: gui {
	parameter "Graph type" var: graph_type among: [ "scale-free", "small-world", "complete"];
	parameter "Number of nodes" var: nb_nodes min: 5 max: 10000;
	parameter "Probability to rewire an edge (beta)" var: p min: 0.0 max: 1.0 category: "small-world";
	parameter "Base degree of each node. k must be even" var: k min: 2 max: 10 category: "small-world";
	parameter "Number of edges added per novel node" var: m min: 1 max: 10 category: "scale-free";
	
	parameter "Layout Algo" var: algo among: [ "Yifan Hu", "Bounded Yifan Hu","Yifan Hu default parameter values", "Force Atlas 2", "Bounded Force Atlas 2", "Force Atlas 2 default parameter values"];
	parameter "optimal_distance" var: optimal_distance category:"Yifan Hu";
	parameter "step size" var: initial_step min: 0.0 category: "Yifan Hu";
	parameter "step_ratio" var: step_ratio min: 0.0 category: "Yifan Hu";
//	parameter "regulator" var: regulator min: 0.0 max: 1.0 category: "Yifan Hu";
	parameter "quadtree max level" var: max_level min: 1 max: 10000 category: "Yifan Hu";
	parameter "barnes_hut_theta" var: barnes_hut_theta min: 0 max: 10 category: "Yifan Hu";
//	parameter "convergence_threshold" var: convergence_threshold min: 1 max: 10 category: "Yifan Hu";
	parameter "relative strength" var: strength min: 0.0 max: 1.0 category: "Yifan Hu";

	parameter "bounded_p1" var: the_bounds_p1 category: "Bounded Points";
	parameter "bounded_p2" var: the_bounds_p2 category: "Bounded Points";

	parameter "thread_number" var: thread_number category:"Force Atlas 2";
	parameter "dissuade_hubs" var: dissuade_hubs category:"Force Atlas 2";
	parameter "linlog_mode" var: linlog_mode category:"Force Atlas 2";
	parameter "prevent_overlap" var: prevent_overlap category:"Force Atlas 2";
	parameter "edge_weight_influence" var: edge_weight_influence category:"Force Atlas 2";
	parameter "scaling" var: scaling category:"Force Atlas 2";
	parameter "stronger_gravity" var: stronger_gravity category:"Force Atlas 2";
	parameter "gravity" var: gravity category:"Force Atlas 2";
	parameter "tolerance" var: tolerance category:"Force Atlas 2";
	parameter "approximate_repulsion" var: approximate_repulsion category:"Force Atlas 2";
	parameter "approximation" var: approximation category:"Force Atlas 2";

	output {
		display map type: opengl{
			species edge_agent ;
			species node_agent ;
		}
	}
}
