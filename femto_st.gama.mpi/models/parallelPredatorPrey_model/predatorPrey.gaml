/**
* Name: Predator Prey MPI
* Author: Lucas Grosjean
* Tags: inheritance
*/

model prey_predator

global {
	
	int nb_preys_init <- 50;
	int nb_predators_init <- 50;
	float prey_max_energy <- 1.0;
	float prey_max_transfert <- 0.1 ;
	float prey_energy_consum <- 0.05;
	float predator_max_energy <- 1.0;
	float predator_energy_transfert <- 0.5;
	float predator_energy_consum <- 0.02;
	int nb_preys -> {length (prey)};
	int nb_predators -> {length (predator)};
	
	int width_grid <- 20;
	int height_grid <- 20;
	
	//geometry shape <- square(200#m);

	
	init {
		create prey number: nb_preys_init ; 
		create predator number: nb_predators_init ;
		
		write "Init";
		write "nb_preys_init: " + nb_preys_init;
		write "nb_predators_init: " + nb_predators_init;
		write "seed : " + seed;
		write "shape.width : " + shape.width;
 	}
}

species generic_species {
	float max_energy;
	float max_transfert;
	float energy_consum;
	float energy <- (rnd(1000) / 1000) * max_energy  update: energy - energy_consum max: max_energy ;
	
	init {
		location <- one_of (vegetation_cell).location;
	}
		
	reflex basic_move {
		write("................................................................. start move "+self.name+"\n");
		vegetation_cell current <- vegetation_cell({self.location.x, self.location.y});
		self.location <- one_of(current.neighbours).location;
		write("................................................................. end move "+self.name+"\n");

	}
		
	reflex die when: energy <= 0 {
		write("dead "+self.name+"\n");
		do die ;
	}
	
}

species prey parent: generic_species {
	float max_energy <- prey_max_energy ;
	float max_transfert <- prey_max_transfert ;
	float energy_consum <- prey_energy_consum ;
		
	reflex eat when: (vegetation_cell({self.location.x, self.location.y}).food > 0) {
		
		write("................................................................. start eat prey"+self.name+"\n");
		vegetation_cell current <- vegetation_cell({self.location.x, self.location.y});
		float energy_transfert <- min([max_transfert, current.food]) ;
		current.food <- current.food - energy_transfert ;
		energy <- energy + energy_transfert ;
		write("................................................................. end eat prey"+self.name+"\n");
	}
}
	
species predator parent: generic_species {
	float max_energy <- predator_max_energy ;
	float energy_transfert <- predator_energy_transfert ;
	float energy_consum <- predator_energy_consum ;
	list<prey> reachable_preys update: prey inside (vegetation_cell({self.location.x, self.location.y}));
		
	reflex eat when: ! empty(reachable_preys) {
		
		write("................................................................. end eat predator"+self.name+"\n");
		ask one_of (reachable_preys) {
			write("//////////////////////////////////// eated ////////////////////////////////////////\n");
			do die ;
		}
		energy <- energy + energy_transfert ;
		write("................................................................. end eat predator "+self.name+"\n");
	}
}
	
grid vegetation_cell width: width_grid height: height_grid neighbors: 4 {
	float maxFood <- 1.0 ;
	float foodProd <- (rnd(1000) / 1000) * 0.01 ;
	float food <- (rnd(1000) / 1000) max: maxFood update: food + foodProd ;
	list<vegetation_cell> neighbours  <- (self neighbors_at 2); 
}

experiment prey_predatorExp type: gui {
	
	int get_width_grid
	{
		return width_grid;
	}
	int get_height_grid
	{
		return height_grid;
	}
	float get_size_model
	{
		return world.shape.width;
	}
	
	int get_nb_preys
	{
		list<prey> prey_list <- prey where(not dead(each));
		return length(prey_list);
	}
	int get_nb_predators
	{
		list<prey> predator_list <- predator where(not dead(each));
		return length(predator_list);
	}
	
	list<prey> get_prey_in_area(float start, float end)
	{
		list<prey> prey_list <- prey where(each.location.x >= start and each.location.x <= end and not dead(each)) collect (each);
		
		write("prey_list in model = "+length(prey_list));
		write("get_nb_preys in model = "+get_nb_preys());
		return prey_list;
	}
	
	list<predator> get_predator_in_area(float start, float end)
	{
		list<predator> predator_list <- predator where(each.location.x >= start and each.location.x <= end and not dead(each)) collect (each);
		return predator_list;
	}
	
	int delete_agent_not_in_band(float start, float end)
	{
		list<prey> prey_list <- prey where(each.location.x < start or each.location.x > end and not dead(each)) collect (each);
		list<predator> predator_list <- predator where(each.location.x < start or each.location.x > end and not dead(each)) collect (each);
		
		ask prey_list
		{
			do die;
		}
		ask predator_list
		{
			do die;
		}
		
		return length(prey_list) + length(predator_list);
	}
	
	
	list<prey> create_prey_from_list(unknown specie_attributes_list)
	{
		write("create_prey_from_list start "+specie_attributes_list+"\n");
		list<prey> list_prey;
		
		int nbloop <- 0;
		loop tmp over: specie_attributes_list
		{
			map<string,unknown> map_unk <- map(tmp);
			write("map_unk prey start "+map_unk+"\n");
			add create_prey(map_unk) to: list_prey;
			write("tmp = "+tmp+"\n");
			nbloop <- nbloop + 1;
		}
		
		write("create_prey_from_list end"+list_prey+"\n");
		write("nb loop = "+nbloop+"\n");
		return list_prey;
	}
	
	prey create_prey(map<string,unknown> specie_attributes)
	{
		
		write("create_prey start+"+specie_attributes+"\n");
		create prey returns:prey_tmp
		{
			self.location <- specie_attributes at "location";
			self.max_energy <- specie_attributes at "max_energy";
			self.max_transfert <- specie_attributes at "max_transfert";
			self.energy_consum <- specie_attributes at "energy_consum";
			self.energy <- specie_attributes at "energy";
		}
		
		write("prey_tmp reach = "+prey_tmp[0]+"\n");
		
		return prey_tmp[0];
	}
	
	list<predator> create_predator_from_list(unknown specie_attributes_list)
	{
		
		write("create_predator_from_list start "+specie_attributes_list+"\n");
		list<predator> list_predator;
		loop tmp over: specie_attributes_list
		{
			map<string,unknown> map_unk <- map(tmp);
			write("map_unk start "+map_unk+"\n");
			add create_predator(map_unk) to: list_predator;
		}
		
		write("create_predator_from_list end : "+list_predator+"\n");
		
		return list_predator;
	}
	
	predator create_predator(map<string,unknown> specie_attributes)
	{
		write("create_predator start+"+specie_attributes+"\n");
		create predator returns:predator_tmp
		{
			
		 	self.location <- specie_attributes at "location";
			self.max_energy <- specie_attributes at "max_energy";
			self.max_transfert <- specie_attributes at "max_transfert";
			self.energy_consum <- specie_attributes at "energy_consum";
			self.energy_transfert <- specie_attributes at "energy_transfert";
			self.energy <- specie_attributes at "energy";
			self.reachable_preys <- prey inside(vegetation_cell({self.location.x, self.location.y}));
			
		}
		
		write("predator_tmp reach = "+predator_tmp[0]+"\n");
		
		return predator_tmp[0];
	}
}
 

