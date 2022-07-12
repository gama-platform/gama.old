/**
* Name: Shapefile MPI
* Author:  Lucas Grosjean
* Description: Shapefile model using MPI.
*/
model SHP

global 
{
	file shape_file_buildings <- shape_file("../includes/buildings_simple.shp");
	geometry shape <- envelope(shape_file_buildings);
	
	int size_outer <- 10;
	int size_inner <- 5;
	
	string OUTSIDE <- "outside";
	string INSIDE <- "inside";
	string OUTER <- "outer";
	string INNER <- "inner";
	
	init 
	{
		create building from: shape_file_buildings;
		//create agent_common number: 10;
	}
}

species agent_common skills:[moving]
{
	int index;
	building workplace <- one_of(building);
	building home <- one_of(building);
    point the_target;
    string area <- INSIDE;
	
	init 
	{
		self.index <- rnd(100);
		self.location <- any_point_in(home);
		self.area <- INSIDE;
	}
	
	reflex go_home when: ( (the_target = nil) and (self overlaps workplace) )
	{
		the_target <- any_point_in(home);
	}
	
	reflex go_work when: ( (the_target = nil) and (self overlaps home) )
	{
		the_target <- any_point_in(workplace);
	}
	
	reflex move when: the_target != nil {
	    do goto target: the_target;
	    if the_target = location {
	        the_target <- nil ;
	    }
    }
    
    reflex define_position
    {
		if(container(building) one_matches (self overlaps each))
		{
			self.area <- INSIDE;
		}else if(container(building) one_matches (self overlaps each.OLZ_inner))
		{
			self.area <- INNER;
		}else if(container(building) one_matches (self overlaps each.OLZ_outer))
		{
			self.area <- OUTER;
		}else
		{
			self.area <- OUTSIDE;
		}	
		
		write(self.name+" : "+self.area);
    }
    
    aspect default {
		switch (self.area)
		{
			match OUTER { draw circle(4.0) color:#black; draw self.name color:#black; }
			match INNER { draw circle(4.0) color:#pink; draw self.name color:#black; }
			match INSIDE { draw square(4.0) color:#red; draw self.name color:#black; }
			match OUTSIDE { draw triangle(4.0) color:#cyan; draw self.name color:#black; }
			default {draw triangle(4.0) color:#black;}
		}
	}
}

species building 
{
	geometry OLZ_inner <- size_inner around self.shape;
	geometry OLZ_outer <- size_inner around OLZ_inner - self;
	
	aspect default {
		draw self color: #yellow;
		draw OLZ_inner color: #blue;
		draw OLZ_outer color: #purple;
	}
}

experiment movingExp type: gui 
{
	output {
		display city_display type: opengl {
			species building;
			species agent_common;
		}
	}
	
	action create_agents(int nb)
	{
		create agent_common number:nb;
	}
	
	// rank 0 : inside + inner 
	// rank 1 : outside + outer
	list<agent_common> get_agent_list_in_area(int rank)   
	{
		list<agent_common> agent_list_inside <- agent_common where (each.area = (INSIDE) or each.area = (INNER));
		list<agent_common> agent_list_outside <- agent_common where (each.area = (OUTSIDE) or each.area = (OUTER));
		
		if(even(rank))
		{
			write("agent_list_inside = "+agent_list_inside+"\n");
			return agent_list_inside;
		
		}else
		{
			write("agent_list_outside = "+agent_list_outside+"\n");
			return agent_list_outside;
		}
	}
	
	list<agent_common> get_agent_list_in_OLZ_inner
	{
		list<agent_common> agent_list_inside_OLZ <- agent_common where (each.area = (INNER));
		write("agent_list_inside OLZ INNER = "+agent_list_inside_OLZ+"\n");
		return agent_list_inside_OLZ;
	}
	
	list<agent_common> get_agent_list_in_OLZ_outer
	{
		
		list<agent_common> agent_list_inside_OLZ <- agent_common where (each.area = (OUTER));
		write("agent_list_inside OLZ OUTER = "+agent_list_inside_OLZ+"\n");
		return agent_list_inside_OLZ;
	}
	
	int delete_agent_not_in_band(int rank)
	{
		
		list<agent_common> agent_list_inside <- agent_common where (each.area = (INSIDE) or each.area = (INNER));
		list<agent_common> agent_list_outside <- agent_common where (each.area = (OUTSIDE) or each.area = (OUTER));
		
		int dead_agent <- 0;
		if(even(rank))
		{
			ask agent_list_outside
			{
				dead_agent <- dead_agent + 1;
				do die;
			}
		
		}else
		{
			ask agent_list_inside
			{
				dead_agent <- dead_agent + 1;
				do die;
			}
		}
		
		return dead_agent;
	}
	
	list<agent_common> create_agents_from_list(unknown specie_attributes_list)
	{
		list<agent_common> list_generic;
		loop tmp over: specie_attributes_list
		{
			map<string,unknown> map_unk <- map(tmp);
			add create_agent(map_unk) to: list_generic;
		}
		
		return list_generic;
	}
	
	agent_common create_agent(map<string,unknown> specie_attributes)
	{
		create agent_common returns:created
		{
			self.location <- specie_attributes at "location";
			self.index <- specie_attributes at "index";
		}
		
		return created[0];
	}
}

