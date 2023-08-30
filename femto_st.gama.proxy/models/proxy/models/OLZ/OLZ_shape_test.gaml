/**
* Name: OLZ_shape_test
* 
* Model to test out the shape of the OLZ depending of the grid width and height.
* 
* 
* Author: Lucas Grosjean
* Tags: Proxy, HPC, multi simulation, distribution
*/

model OLZ_proxy_grid

import "../Models_to_distribute/MovingAgent.gaml"

global 
{		
	geometry shape <- rectangle(rnd(100) + 50, rnd(100) + 50);
	int gridWidth <- rnd(10) + 1;
	int gridHeight <- rnd(10) + 1;
	int size_OLZ <- 5;
	
	int simulation_id <- 0;
}

grid cell width: gridWidth height: gridHeight neighbors: 4
{ 
	
	int rank;
	
	list<geometry> OLZ_list;
	map<geometry, pair<int,int>> neighborhood_shape;
	
	/* key : rank of the neighbour cell, value : list of agent */
	map<int, list<agent>> new_agents_in_OLZ; 				// agents entering OLZ
	map<int, list<agent>> agents_in_OLZ; 					// agents currently in OLZ
	map<int, list<agent>> agents_in_OLZ_previous_step; 		// agent that was in the OLZ last step
	map<int, list<agent>> agents_leaving_OLZ; 				// agent leaving the OLZ
	
	map<int, list<agent>> agent_leaving_OLZ_to_neighbor; 	// agent leaving the OLZ to the neighbor managed area
	map<int, list<agent>> agent_leaving_OLZ_to_me; 			// agent leaving the OLZ to my managed area
	
	/* INNER OLZ */
	geometry OLZ_top_inner <- shape - rectangle(world.shape.width / gridWidth, world.shape.height / gridHeight) translated_by {0,(size_OLZ / 2),0};
	geometry OLZ_bottom_inner <- shape - rectangle(world.shape.width / gridWidth, world.shape.height / gridHeight) translated_by {0,-(size_OLZ / 2),0};
	geometry OLZ_left_inner <- shape - rectangle(world.shape.width / gridWidth, world.shape.height / gridHeight) translated_by {size_OLZ / 2,0,0};
	geometry OLZ_right_inner <- shape - rectangle(world.shape.width / gridWidth, world.shape.height / gridHeight) translated_by {-(size_OLZ / 2),0,0};
	
	/* CORNER */
	geometry OLZ_bottom_left_inner <- OLZ_left_inner inter OLZ_bottom_inner;
	geometry OLZ_bottom_right_inner <- OLZ_right_inner inter OLZ_bottom_inner;
	geometry OLZ_top_left_inner <- OLZ_left_inner inter OLZ_top_inner;
	geometry OLZ_top_right_inner <- OLZ_right_inner inter OLZ_top_inner;
	
	/* OUTER OLZ */
	geometry OLZ_top_outer <- (shape - rectangle(world.shape.width / gridWidth, world.shape.height / gridHeight) translated_by {0,(size_OLZ / 2),0}) translated_by {0,-(size_OLZ / 2),0};
	geometry OLZ_bottom_outer <- (shape - rectangle(world.shape.width / gridWidth, world.shape.height / gridHeight) translated_by {0,-(size_OLZ / 2),0}) translated_by {0,(size_OLZ / 2),0};
	geometry OLZ_left_outer <- (shape - rectangle(world.shape.width / gridWidth, world.shape.height / gridHeight) translated_by {size_OLZ / 2,0,0}) translated_by {-(size_OLZ / 2),0,0};
	geometry OLZ_right_outer <- (shape - rectangle(world.shape.width / gridWidth, world.shape.height / gridHeight) translated_by {-(size_OLZ / 2),0,0}) translated_by {(size_OLZ / 2),0,0};
	
	/* ALL INNER OLZ */
	geometry inner_OLZ <- OLZ_top_inner + OLZ_bottom_inner + OLZ_left_inner + OLZ_right_inner;
	
	/* ALL OUTER OLZ */
	geometry outer_OLZ <- OLZ_top_outer + OLZ_bottom_outer + OLZ_left_outer + OLZ_right_outer;
	
	init
	{
		rank <- grid_x + (grid_y * gridWidth);
		
		/* INNER OLZ */
		if(grid_y - 1 >= 0)
		{		
			write(""+grid_x + "," + (grid_y-1));
			add OLZ_top_inner :: (grid_x :: grid_y - 1) to: neighborhood_shape;
			add OLZ_top_inner to: OLZ_list;
		}
		if(grid_y + 1 < gridHeight)
		{		
			add OLZ_bottom_inner :: (grid_x :: grid_y + 1) to: neighborhood_shape;
			add OLZ_bottom_inner to: OLZ_list;
		}
		if(grid_x - 1 >=0)
		{		
			add OLZ_left_inner :: (grid_x  - 1 :: grid_y) to: neighborhood_shape;
			add OLZ_left_inner to: OLZ_list;
		}	
		if(grid_x + 1 < gridWidth)
		{		
			add OLZ_right_inner :: (grid_x + 1 :: grid_y) to: neighborhood_shape;
			add OLZ_right_inner to: OLZ_list;
		}
		
		/* CORNER */
		if(grid_x + 1 < gridWidth and grid_y - 1 >= 0)
		{		
			add OLZ_top_right_inner :: (grid_x + 1 :: grid_y - 1) to: neighborhood_shape;
			add OLZ_top_right_inner to: OLZ_list;
		} 
		if(grid_x - 1 >= 0 and grid_y + 1 < gridHeight)
		{		
			add OLZ_bottom_left_inner :: (grid_x - 1 :: grid_y + 1) to: neighborhood_shape;
			add OLZ_bottom_left_inner to: OLZ_list;
		}
		if(grid_x + 1 < gridWidth and grid_y + 1 < gridHeight)
		{		
			add OLZ_bottom_right_inner :: (grid_x + 1 :: grid_y + 1) to: neighborhood_shape;
			add OLZ_bottom_right_inner to: OLZ_list;
		}
		if(grid_x - 1 >= 0 and grid_y - 1 >= 0)
		{		
			add OLZ_top_left_inner :: (grid_x -  1 :: grid_y - 1) to: neighborhood_shape;
			add OLZ_top_left_inner to: OLZ_list;
		}
	}
	
	aspect default
	{
		draw self.shape color: rgb(#white,125) border:#black;	
		draw "[" + self.grid_x + "," + self.grid_y +"] : " + rank color: #red;
		
		if(cell[simulation_id] = self)
		{	
			
			loop shape_to_display over: neighborhood_shape.keys
			{
				draw shape_to_display color: rgb(#green, 125) border: #black;
			}
			draw outer_OLZ color: rgb(#red, 125) border: #black;
		}
	}
}

experiment OLZ_proxy_grid type: proxy 
{
	output{
		display OLZ_proxy_grid type: 2d
		{
			species cell;
			species movingAgent aspect: classic;
		}
	}
}
