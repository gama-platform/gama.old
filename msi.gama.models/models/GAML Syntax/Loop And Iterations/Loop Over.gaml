/***
* Name: Loops
* Author: Benoit Gaudou
* Description: This model illustrates the behavior of loop over many kinds of container.
* Tags: loop, container, list, map, matrix, point, population
***/

model LoopsOverContainers

global {
	// GAML contains several container types: they are data structures containing several elements. Containers can be:
	// list (of a given type of elements):
	list<string> list_of_string <- ["A","B","C"];
	// map (a set of pairs key::value):
	map<string,int> map_of_string_int <- ["A"::1,"B"::2,"C"::3];	
	// matrix (a 2-dimensions vector of elements of a given type)
	matrix<string> matrix_of_string <- matrix([["R1C1","R2C1"],["R1C2","R2C2"],["R1C3","R2C3"]]);
	// pair (a pair of 2 elements, that can be of different types)
	pair<string,int> pair_of_string_int <- "B"::2;
	// graph (a set of nodes and edges between these nodes)
	graph<point,geometry> graph_of_points <- as_edge_graph([edge({10,5}, {20,3}), edge({10,5}, {30,30}),edge({30,30}, {80,35}),edge({80,35}, {40,60}),edge({80,35}, {10,5}), node ({50,50})]);		
	// file
	file file_of_text <- text_file("created_file",["First line","Second line","last sentence"]);
	// population (the list of all the agents of a given species).	
	// point (2 or 3 float coordinates)
	point point_3D <- {12,45,2};
	
	
	init {
		create dummy_species number: 10;
		
		// A very convenient way to loop over all the elements of a container is to use the over: facet of the loop.
		do loop_over;
		// To be more precise on the loop over a structure we can need to loop using an index value.
		do loop_using_indices;
		// Some specificities of the loop over maps.
		do loop_map;
		// Some specificities of the loop over graphs.		
		do loop_graph;
	}
	
	action loop_over {
		write "==================================";
		write "= Loop over containers and point =";
		write "==================================";
		
		// In the loop over a container, a local variable is used and updated with the current element of the container at each step of the loop.
		// The type of this variable can be different depending on the container.
	
		// When we loop over a list, the local variable elt_of_list has the type of the elments of the list.	
		write "----------------------------------";
		write "Loop over the list: " + list_of_string;
		write "----------------------------------";
		loop elt_of_list over: list_of_string {
			write sample(elt_of_list);
			// As elt_of_list is taken from a list of string, it is of type string and can be used in any string-related operator.
			write "  " + sample(lower_case(elt_of_list));
		}
		
		// When we loop over a map, the loop is done over the values. 
		// As a consequence the local variable elt_of_map has the type of the values, i.e. int in the following case.
		write "----------------------------------";
		write "Loop over the map: " + map_of_string_int;
		write "----------------------------------";
		loop elt_of_map over: map_of_string_int {
			write sample(elt_of_map);
			// As the loop is made over the values of map_of_string_int, elt_of_map it is of type int.
			write "  " + sample(type_of(elt_of_map));
		}		
		
		// When we loop over a matrix, the loop is done over all the elements (row by row). 
		write "----------------------------------";
		write "Loop over the matrix: " + matrix_of_string;
		write "----------------------------------";
		loop elt_of_matrix over: matrix_of_string {
			write sample(elt_of_matrix);
			// As the loop is made over a matrix of string, elt_of_matrix is of type string.
			write "  " + sample(lower_case(elt_of_matrix));
		}		
		
		// When we loop over a pair, the loop is only over the 2 elements of the pair. 
		write "----------------------------------";
		write "Loop over the pair: " + pair_of_string_int;
		write "----------------------------------";
		loop elt_of_pair over: pair_of_string_int {
			write sample(elt_of_pair);
			write "  " + sample(type_of(elt_of_pair));
			// In a pair, the 2 elements can be of the same type or of different types.
			// In the first case, the elt_of_pair will be of the type common to the 2 elements whereas in the second case, it will be of type unknown.
			write sample("  " + type_of(elt_of_pair));
		}	
		
		// When we loop over a graph, the loop is done over all the edges of the graph
		write "----------------------------------";
		write "Loop over the graph: " + graph_of_points;
		write "----------------------------------";
		loop elt_of_graph over: graph_of_points {
			write sample(elt_of_graph);
			// As the loop is made over a graph of point and geometries, elt_of_graph is of type geometry (type of edges).
			// We can thus ask them their source and target nodes.			
			write "  " + sample(graph_of_points source_of(elt_of_graph));
			write "  " + sample(graph_of_points target_of(elt_of_graph));
		}		

		// When we loop over a file, the loop is done over its content (which depends on the file type).
		// When the file is a text_file, the content is a list of string, each element being a line of the text_file
		write "----------------------------------";
		write "Loop over the file: \n" + file_of_text;
		write "----------------------------------";
		loop elt_of_file over: file_of_text {
			write sample(elt_of_file);
			// As the loop is made over a text_file, elt_of_file is of type string.
			write "  " + sample(upper_case(elt_of_file));
		}	
		
		// When we loop over a species/population, the loop is done over all the agents of this population.
		write "----------------------------------";
		write "Loop over the population: " + dummy_species;
		write "----------------------------------";
		loop elt_of_species over: dummy_species {
			write sample(elt_of_species);
			// As the loop is made over a species/population, elt_of_species is of type agent.
			// We can thus ask them some specific agents attributes.			
			write "  " + sample(elt_of_species.name);
			write "  " + sample(elt_of_species.location);			
		}
				
		// When we loop over a point, the loop is done over the three coordinates of the point.
		write "----------------------------------";
		write "Loop over the point: " + point_3D;
		write "----------------------------------";
		loop elt_of_point over: point_3D {
			write sample(elt_of_point);
			// As the loop is made over a point, elt_of_point is of type float.			
			write sample(type_of(elt_of_point));
		}			
	}
	
	
	action loop_using_indices {
		write "=======================";
		write "= Loop using an index =";
		write "=======================";
		
		// The previous example loop over a container using the over: facet and thus it iterates over all the elements of the container, 
		// but without any information on it location in the container.
		// When we need to know this information, we can loop over a container (in particular list or matrix) through an index value.
		// Reminder: in GAML, lists and matrices index starts at 0 and ends at (number of elements in the container) - 1.
		write sample(matrix_of_string[2,1])	;
		
		write "----------------------------------";
		write "Loop over the list: " + list_of_string;
		write "----------------------------------";			
		// The loop over a list using an index will be written as follows (it is equivalent to the loop elt_of_list over: list_of_string).
		// The local variable i is an integer.
		loop i from: 0 to: length(list_of_string) - 1 {
			write "The "+i+"th element of the list is: " + list_of_string[i];
		}
		
		write "----------------------------------";
		write "Loop over the matrix: " + matrix_of_string;
		write "----------------------------------";
		// As a matrix is a 2-dimension set, we need a double loop to loop over all the elements
		loop index_row from: 0 to: matrix_of_string.rows - 1 {
			loop index_column from: 0 to: matrix_of_string.columns - 1 {
				write "The element at row: " + (index_row+1) + " and column: " + (index_column+1) + " of the matrix is: " + matrix_of_string[index_column,index_row];				
			}
		}	
	}
	
				
	// When we loop over a map, the default behavior is to loop over the values (and we thus lose the key in the loop).
	// To keep it we can loop over the map either through the keys list, the values list or even the pairs key::value list.
	action loop_map {
		write "=======================";
		write "= Loop over maps      =";
		write "=======================";

		// The field keys of map is the list of all the keys of the map.
		loop key over: map_of_string_int.keys {
			write sample(key);
			// Given the key, we can also access to the associated value
			write "  Value associated to the key " + key + ": " + map_of_string_int[key];
		}

		// The field values of map is the list of all the values of the map.	
		// The loop over map_of_string_int.values is thus equivalent to the loop over map_of_string_int.
		loop value over: map_of_string_int.values {
			write sample(value);	
		}

		// The field pairs of map is the list of all the keys of the map.	
		// The local variable a_pair thus contains both key and value.	
		loop a_pair over: map_of_string_int.pairs {
			write sample(a_pair);
			// As the map is a map of string and int, each a_pair is of type pair<string,int>.
			write sample(type_of(a_pair));
			// We can thus access directly to both key and value.
			write "  The key is: " + a_pair.key;
			write "  The value is: " + a_pair.value;
		}
	}

	// When we loop over a graph, the default behavior is to loop over the edges.
	// But we can also want to loop over the nodes.
	action loop_graph {
		write "=======================";
		write "= Loop over graphs    =";
		write "=======================";
				
		// The field edges of graph is the list of all the edges of the graph. 
		// The loop over the edges of the graph is thus equivalent to the loop over the graph itself.
		loop edge over: graph_of_points.edges {
			write sample(edge);
			// Given the key, we can also access to the associated value
			write "  " + sample(graph_of_points source_of(edge));
			write "  " + sample(graph_of_points target_of(edge));			
		}
		
		// The field vertices of graph is the list of all the nodes of the graph.
		loop node over: graph_of_points.vertices {	
			write sample(node);
			// Given the node, we can access to some other information as its neighbors, degree ...
			write("Degree of the node: " + graph_of_points degree_of(node));
			write("Successors of the node: " + graph_of_points successors_of(node));			
		}	
	}
}


species dummy_species {}

experiment exp type: gui {}