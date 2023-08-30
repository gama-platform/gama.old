/**
* Name: OLZ_proxy_grid
* 
* Every cells of the grid have OLZ (OverLappingZone) that represent a shared spaces with their neighbors,
* When an agent cross an OLZ, a distant copy of that agent is send to the neighbor
* This distant copy will be updated every cycle with the new data of the local agent
* 
* This model should only be launched in headless mode, otherwise it wont work
* 
* Author: Lucas Grosjean
* Tags: Proxy, HPC, multi simulation, distribution
*/

model OLZ_proxy_grid

import "../Models_to_distribute/MovingAgent.gaml"

global skills: [MPI_Network]
{		
	// << add
	// >> remove

	geometry shape <- rectangle(100,100);
	int grid_width <- 1;
	int grid_height <- 2;
	int size_OLZ <- 25;
	int max_step <- 50;
	
    int mpi_rank <- 0;
    int mpi_size <- 0;
	string file_name;
	
	init
	{
		mpi_rank <- MPI_RANK();
		mpi_size <- MPI_SIZE();
		file_name <- "../output.log/log"+mpi_rank+".txt";
		do clearLogFile();
		
		do writeLog("mpi_rank : " + mpi_rank);
		do writeLog("mpi_size : " + MPI_SIZE);
		
		if(mpi_rank = 0)
		{		
			create movingAgent number: 1 with: [location :: any_location_in(cell[mpi_rank]), target :: any_location_in(any(cell))];
		}
	}
	
	action writeLog(string log)
	{
		save log format: text to: file_name rewrite: false;
	}
	
	action clearLogFile
	{
		save "" format: text to: file_name rewrite:true;
	}
}

grid cell width: grid_width height: grid_height neighbors: 8 skills: [ProxySkill]
{ 
	
	int rank;
	
	list<geometry> OLZ_list;
	map<geometry, int> neighborhood_shape;
	
	
	// INNER OLZ 
	geometry OLZ_top_inner <- shape - rectangle(world.shape.width / grid_width, world.shape.height / grid_height) translated_by {0,(size_OLZ / 2),0};
	geometry OLZ_bottom_inner <- shape - rectangle(world.shape.width / grid_width, world.shape.height / grid_height) translated_by {0,-(size_OLZ / 2),0};
	geometry OLZ_left_inner <- shape - rectangle(world.shape.width / grid_width, world.shape.height / grid_height) translated_by {size_OLZ / 2,0,0};
	geometry OLZ_right_inner <- shape - rectangle(world.shape.width / grid_width, world.shape.height / grid_height) translated_by {-(size_OLZ / 2),0,0};
	
	// CORNER
	geometry OLZ_bottom_left_inner <- OLZ_left_inner inter OLZ_bottom_inner;
	geometry OLZ_bottom_right_inner <- OLZ_right_inner inter OLZ_bottom_inner;
	geometry OLZ_top_left_inner <- OLZ_left_inner inter OLZ_top_inner;
	geometry OLZ_top_right_inner <- OLZ_right_inner inter OLZ_top_inner;
	
	// OUTER OLZ
	geometry OLZ_top_outer <- (shape - rectangle(world.shape.width / grid_width, world.shape.height / grid_height) translated_by {0,(size_OLZ / 2),0}) translated_by {0,-(size_OLZ / 2),0};
	geometry OLZ_bottom_outer <- (shape - rectangle(world.shape.width / grid_width, world.shape.height / grid_height ) translated_by {0,-(size_OLZ / 2),0}) translated_by {0,(size_OLZ / 2),0};
	geometry OLZ_left_outer <- (shape - rectangle(world.shape.width / grid_width, world.shape.height / grid_height) translated_by {size_OLZ / 2,0,0}) translated_by {-(size_OLZ / 2),0,0};
	geometry OLZ_right_outer <- (shape - rectangle(world.shape.width / grid_width, world.shape.height / grid_height) translated_by {-(size_OLZ / 2),0,0}) translated_by {(size_OLZ / 2),0,0};
	
	// ALL INNER OLZ
	geometry inner_OLZ <- OLZ_top_inner + OLZ_bottom_inner + OLZ_left_inner + OLZ_right_inner;
	
	// ALL OUTER OLZ
	geometry outer_OLZ <- OLZ_top_outer + OLZ_bottom_outer + OLZ_left_outer + OLZ_right_outer;
	
	init
	{
		rank <- grid_x + (grid_y * grid_width);
		//write("my rank : " + rank +" :: "+grid_x + "," + grid_y);
		
		// INNER OLZ
		if(grid_y - 1 >= 0)
		{		
			write(""+grid_x + "," + (grid_y-1));
			neighborhood_shape << OLZ_top_inner :: (grid_x + ((grid_y - 1) * grid_width));
			add OLZ_top_inner to: OLZ_list;
		}
		if(grid_y + 1 < grid_height)
		{		
			neighborhood_shape << OLZ_bottom_inner :: (grid_x + ((grid_y + 1) * grid_width));
			add OLZ_bottom_inner to: OLZ_list;
		}
		if(grid_x - 1 >=0)
		{		
			neighborhood_shape << OLZ_left_inner :: ((grid_x - 1)  + (grid_y * grid_width));
			add OLZ_left_inner to: OLZ_list;
		}	
		if(grid_x + 1 < grid_width)
		{		
			neighborhood_shape << OLZ_right_inner :: ((grid_x + 1)  + (grid_y * grid_width));
			add OLZ_right_inner to: OLZ_list;
		}
		
		// CORNER
		if(grid_x + 1 < grid_width and grid_y - 1 >= 0)
		{		
			neighborhood_shape << OLZ_top_right_inner :: ((grid_x + 1)  + ((grid_y - 1)  * grid_width));
			add OLZ_top_right_inner to: OLZ_list;
		} 
		if(grid_x - 1 >= 0 and grid_y + 1 < grid_height)
		{		
			neighborhood_shape << OLZ_bottom_left_inner :: ((grid_x - 1)  + ((grid_y + 1)  * grid_width));
			add OLZ_bottom_left_inner to: OLZ_list;
		}
		if(grid_x + 1 < grid_width and grid_y + 1 < grid_height)
		{		
			neighborhood_shape << OLZ_bottom_right_inner :: ((grid_x + 1)  + ((grid_y + 1)  * grid_width));
			add OLZ_bottom_right_inner to: OLZ_list;
		}
		if(grid_x - 1 >= 0 and grid_y - 1 >= 0)
		{		
			neighborhood_shape << OLZ_top_left_inner :: ((grid_x - 1)  + ((grid_y - 1)  * grid_width));
			add OLZ_top_left_inner to: OLZ_list;
		}
		//write("map size : " + length(neighborhood_shape));
	}
	
	// key : rank of the neighbour cell, value : list of agent
	map<int, list<agent>> new_agents_in_OLZ; 				// agents entering OLZ
	map<int, list<agent>> agents_in_OLZ; 					// agents currently in OLZ
	map<int, list<agent>> agents_in_OLZ_previous_step; 		// agent that was in the OLZ last step
	
	map<int, list<agent>> agent_leaving_OLZ_to_neighbor; 	// agent leaving the OLZ to the neighbor managed area
	map<int, list<agent>> agent_leaving_OLZ_to_me; 			// agent leaving the OLZ to my managed area
	
	map<int, list<agent>> agent_to_update; 					// agent to be updated in neighbor
	map<int, list<agent>> agent_to_migrate; 				// agent to be migrated to neighbor
	
	reflex agent_crossing_olz when: cell[mpi_rank] = self
	{	
		
	}
	
	aspect default
	{
		draw self.shape color: rgb(#white,125) border:#black;	
		draw "[" + self.grid_x + "," + self.grid_y +"] : " + rank color: rgb(#red,125);
		
		if(cell[mpi_rank] = self)
		{
			loop shape_to_display over: neighborhood_shape.keys
			{
				draw shape_to_display color: rgb(200,125,100,125) border: #black;
			}
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
			species followingAgent aspect: classic;
		}
	}
}
