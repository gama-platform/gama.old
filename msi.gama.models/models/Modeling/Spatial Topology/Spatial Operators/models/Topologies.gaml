/**
* Name: Spatial Operators
* Author: Patrick Taillandier
* Description: A model which shows how to use spatial operator, allowing the user to change the parameter operator in the 
* 	experiment to test the different operators and see the results
* Tags: topology, grid
*/

model gridfilter

global {
	map<string,map<dummy,rgb>> theDummies;
	map<string,map<cell,rgb>> theCells;
 	string parameter_operator <-"closest_to" among:["closest_to","at_distance","neighbors_at","distance_to","path_to","cluster_distance","cluster_hierarchical"];
	init {
		
		//Create the agents
		do create_dummy_agents;
 

		//Different actions to test the operators
		do test_agents_at_distance;
		do test_distance_to;
		do test_neighbors_at;
		do test_path_to;
		do test_simple_clustering_by_distance;
		do test_hierarchical_clustering;
		do test_agent_closest_to; 	
	}  
	
	action test_agent_closest_to {
		//It is possible to use the topology of the world (default), the graph or the grid
		add [dummy(8)::#yellow] at:"closest_to" to:theDummies;
		add #red at:dummy closest_to (dummy(8)) to:theDummies["closest_to"];
		
		
		add [cell(40)::#yellow] at:"closest_to" to:theCells;
		add #red at:cell closest_to (cell(40)) to:theCells["closest_to"];
	}
	action test_agents_at_distance {
		
		//It is possible to use the topology of the world (default), the graph or the grid
		ask dummy(8)
		{
			add [self::#yellow] at:"at_distance" to:theDummies;
			loop a_dummy over: agents_at_distance(30) of_species dummy
			{	
				add #red at:a_dummy to:theDummies["at_distance"];
			}
		}
		ask cell(40)
		{
			add [self::#yellow] at:"at_distance" to:theCells;
			loop a_cell over: agents_at_distance(10) of_species cell
			{	
				add #red at:a_cell to:theCells["at_distance"];
			}
		}
	}
	
	action test_neighbors_at {
		
		//It is possible to use the topology of the world (default), the graph or the grid
		//The operator neighbors_at gives the same results that neighbors_of( an_agent, a_distance )
		
		add [dummy(8)::#yellow] at:"neighbors_at" to:theDummies;
		loop a_dummy over: dummy(8) neighbors_at 30
		{	
			add #red at:a_dummy to:theDummies["neighbors_at"];
		}
		add [cell(40)::#yellow] at:"neighbors_at" to:theCells;
		loop a_cell over: cell(40) neighbors_at 20
		{	
			add #red at:a_cell to:theCells["neighbors_at"];
		}
	}
	
	action test_distance_to {
		
		//It is possible to use the topology of the world (default), the graph or the grid
		//The operator distance_to gives the same results that the operator topology distance_between[an_agent_A,an_agent_B]
		add [dummy(8)::#yellow] at:"distance_to" to:theDummies;
		add #red at:dummy(5) to:theDummies["distance_to"];
		
		add [cell(40)::#yellow] at:"distance_to" to:theCells;
		add #red at:cell(27) to:theCells["distance_to"];
	}
	
	action test_path_to {
		
		//It is possible to use the topology of the world (default), the graph or the grid
		//The operator distance_to gives the same results that the operator topology path_between[an_agent_A,an_agent_B]
		add [dummy(8)::#yellow] at:"path_to" to:theDummies;
		add #red at:dummy(9) to:theDummies["path_to"];
		
		add [cell(40)::#yellow] at:"path_to" to:theCells;
		add #red at:cell(28) to:theCells["path_to"];
	}
	action test_simple_clustering_by_distance 
	{
		//Can be used for other topologies by adding using(topology(cell)) for example
		
		list<list<dummy>> clusteredDummies <- list<list<dummy>>(dummy simple_clustering_by_distance 30);
		loop a_list over: clusteredDummies
		{
			rgb colorList <- rgb(rnd(255),rnd(255),rnd(255));
			loop a_dummy over: a_list
			{
				if(!(theDummies contains_key "cluster_distance") or length(theDummies["cluster_distance"])=0)
				{
					add map<dummy,rgb>([a_dummy::colorList]) at:"cluster_distance" to:theDummies;
				}
				else
				{
					add colorList at:a_dummy to:theDummies["cluster_distance"];
				}
			}
		}
	}
	action test_hierarchical_clustering {
		//Can be used for other topologies by adding using(topology(cell)) for example
		list<list<dummy>> clusteredDummies <- list<list<dummy>>(dummy hierarchical_clustering 10);
		
		loop a_list over: clusteredDummies
		{
			rgb colorList <- rgb(rnd(255),rnd(255),rnd(255));
			loop a_dummy over: a_list
			{
				if(!(theDummies contains_key "cluster_hierarchical") or length(theDummies["cluster_hierarchical"])=0)
				{
					add map<dummy,rgb>([a_dummy::colorList]) at:"cluster_hierarchical" to:theDummies;
				}
				else
				{
					add colorList at:a_dummy to:theDummies["cluster_hierarchical"];
				}
			}
		}
	}
	action create_dummy_agents {
		create dummy with: [location :: {5,5}];
		create dummy with: [location :: {8,9}];
		create dummy with: [location :: {14,6}];
		create dummy with: [location :: {35,55}];
		create dummy with: [location :: {25,75}];
		create dummy with: [location :: {56,80}];
		create dummy with: [location :: {10,70}];
		create dummy with: [location :: {80,8}];
		create dummy with: [location :: {34,78}];
		create dummy with: [location :: {67,32}];
		loop i from: 0 to: length(dummy) - 1 {
			ask dummy[i] {id <- string(i);}
		}
	}
	
}

grid cell width: 10 height: 10 neighbors: 4{
	rgb color <- #green;
}

species dummy {
	string id;
	aspect default {
		draw circle(2) color: #yellow;
		draw id size: 6 color: #black;
	}
}
experiment topology_test type: gui {
	/** Insert here the definition of the input and output of the model */
	parameter "Operator" var: parameter_operator;
	output {
		
		display Continuous_Environment  
		{
			graphics cell
			{
				loop a_key over: theCells[parameter_operator].pairs {
					draw a_key.key at:a_key.key.location color: theCells[parameter_operator][a_key.key] ;
				}
				loop a_cell over: cell-theCells[parameter_operator].keys
				{
					draw a_cell at:a_cell.location color: #green ;
				}
				
				//Display a line between the two chosen cell for the distance_to operator
				if(parameter_operator="distance_to")
				{
					geometry aLine <- line([theCells[parameter_operator].keys[0].location,theCells[parameter_operator].keys[1].location]) +1.2;
					draw (aLine) color:#silver;
					draw string(distance_to (theCells[parameter_operator].keys[0].location,theCells[parameter_operator].keys[1].location)) at:aLine.location size: 6 color: #silver;	
				}
				else
				{
					//Display a path between the two chosen cell for the path_to operator
					if(parameter_operator="path_to")
					{
							path aPath <- theCells[parameter_operator].keys[0] path_to theCells[parameter_operator].keys[1].location;
							loop eg over: aPath.edges
							{
								draw (geometry(eg)+1.2) color:#silver;
							}
					}
				}
			}
			graphics dummy
			{
				loop a_key over: theDummies[parameter_operator].pairs {
					draw circle(2) at:a_key.key.location color: theDummies[parameter_operator][a_key.key] ;
					draw a_key.key.id at:a_key.key.location size: 6 color: #black;	
				}
				loop a_dummy over: dummy-theDummies[parameter_operator].keys
				{
					draw circle(2) at:a_dummy.location color: #grey ;
					draw a_dummy.id at:a_dummy.location size: 6 color: #black;	
				}
				//Display a line between the two chosen dummies for the distance_to operator
				if(parameter_operator="distance_to")
				{
					geometry aLine <- line([theDummies[parameter_operator].keys[0].location,theDummies[parameter_operator].keys[1].location]) +1.2;
					draw (aLine) color:#pink;
					draw string(distance_to (theDummies[parameter_operator].keys[0].location,theDummies[parameter_operator].keys[1].location)) at:aLine.location size: 6 color: #pink;	
				}
				else
				{
					//Display a path between the two chosen dummies for the path_to operator
					if(parameter_operator="path_to")
					{
							path aPath <- theDummies[parameter_operator].keys[0] path_to theDummies[parameter_operator].keys[1].location;
							loop eg over: aPath.edges
							{
								draw (geometry(eg)+1.2) color:#pink;
							}
					}
				}
			}
		}
	}
}
