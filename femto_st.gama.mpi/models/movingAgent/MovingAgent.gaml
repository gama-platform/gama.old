/**
* Name: MovingAgent
* Author: Lucas Grosjean
* Description: MPI Serialize test
*/

model MovingAgent

global {

}

species generic_species {
	
	int index;

	init 
	{
		self.index <- rnd(100);
		self.location <- one_of(vegetation_cell).location;
	}
	
	reflex basic_move {
		vegetation_cell current <- vegetation_cell({self.location.x, self.location.y});
		self.location <- one_of(current.neighbours).location;
	}
}
	
grid vegetation_cell width: 50 height: 50 neighbors: 4{
	
	int data <- rnd(100);
	list<vegetation_cell> neighbours  <- (self neighbors_at 2); 
}

experiment movingExp type: gui {
	
	action create_species(int nb)
	{
		create generic_species number:nb;
	}
	
	float get_size_model
	{
		return world.shape.width;
	}
	
	list<generic_species> get_generic_species_list_in_area(float start, float end)
	{
		list<generic_species> generic_species_list <- generic_species where(each.location.x >= start and each.location.x <= end and not dead(each)) collect (each);
		return generic_species_list;
	}
	
	int delete_agent_not_in_band(float start, float end)
	{
		list<generic_species> generic_species_list <- generic_species where(each.location.x < start or each.location.x > end) collect (each);
		
		ask generic_species_list
		{
			write("to be deleted : "+self.location+"\n");
			do die;
		}
		
		return length(generic_species_list);
	}
	
	vegetation_cell get_cell_at_position(int x, int y)
	{
		return vegetation_cell({x, y});
	}
	
	list<generic_species> create_generic_species_from_list(unknown specie_attributes_list)
	{
		list<generic_species> list_generic;
		loop tmp over: specie_attributes_list
		{
			map<string,unknown> map_unk <- map(tmp);
			add create_generic_species(map_unk) to: list_generic;
		}
		
		return list_generic;
	}
	
	generic_species create_generic_species(map<string,unknown> specie_attributes)
	{
		create generic_species returns:created
		{
			self.location <- specie_attributes at "location";
			self.index <- specie_attributes at "index";
		}
		
		return created[0];
	}
}
 

