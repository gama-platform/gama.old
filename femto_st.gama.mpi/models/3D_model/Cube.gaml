/**
* Name: Moving cells with neighbors
* Author: Arnaud Grignard
* Description: Third part of the tutorial : Tuto3D
* Tags: 3d, light, grid, neighbors
*/

model Cube

global 
{
	int environment_size <- 100;
	int distance_compute <- 10;
	geometry shape <- cube(environment_size);

	init 
	{
	}
}

species cell skills: [moving3D] 
{

	reflex move 
	{
		do wander speed: 10;
	}
	
	reflex compute
	{
		write("neighbors : "+(cell select ((each distance_to self) < distance_compute)));
	}
}

experiment movingExp type: gui 
{
	action create_cells(int nb)
	{
		create cell number:nb
		{
			location <- {rnd(environment_size), rnd(environment_size), rnd(environment_size)};
		}
	}
	
	float get_size_model
	{
		return world.shape.width;
	}
	
	list<cell> get_cell_list_in_area(float start, float end)
	{
		list<cell> generic_species_list <- cell where(each.location.x >= start and each.location.x <= end) collect (each);
		
		write("get cells = "+generic_species_list+"?  >= "+start +" <="+end+"\n");
		return generic_species_list;
	}
	
	int delete_agent_not_in_band(float start, float end)
	{
		list<cell> generic_species_list <- cell where(each.location.x < start or each.location.x > end) collect (each);
		
		ask generic_species_list
		{
			write("to be deleted : "+self.location.x+"\n");
			do die;
		}
		
		return length(generic_species_list);
	}
	
	list<cell> create_cell_from_list(unknown specie_attributes_list)
	{
		list<cell> list_generic;
		loop tmp over: specie_attributes_list
		{
			map<string,unknown> map_unk <- map(tmp);
			add create_cell(map_unk) to: list_generic;
		}
		
		return list_generic;
	}
	
	cell create_cell(map<string,unknown> specie_attributes)
	{
		write("creating = "+specie_attributes+"\n");
		create cell returns:created
		{
			self.location <- specie_attributes at "location";
			//self.neighbors <- specie_attributes at "neighbors";
		}
		
		return created[0];
	}
}
