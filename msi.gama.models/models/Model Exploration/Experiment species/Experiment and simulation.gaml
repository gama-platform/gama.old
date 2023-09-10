/***
* Name: Experiment species
* Author: Benoit Gaudou
* Description: The model shows the different dynamics of the experiment and its simulation(s).
* 
* Tags: experiment, simulation, gui, batch
***/

// Create a model showing the different dynamics of the experiment and its simulation(s), 
// in regular and batch experiments & memorize
// (i.e. when are the behaviors of the experiment executed, how does it access its simulations, etc.)


model Experimentspecies

global {
	int nb_people <- 5;
	
	init {
		write "====== Init of " + self;
		write sample(seed) + " - of " + self.name;				
		create people number: nb_people;
	}
	
	reflex t {
		write sample(cycle) + " - reflex t of " + self.name;
	}	
	
	abort {
		write "====== Death of " + self;
	}
}

species people {}

// The basic GUI experiment behavior is:
// - create a single simulation (initialization of the model)
// - (at each step) call the behavior of the model (e.g. the reflexes in the global).
experiment basic_exp type: gui { }

// The GUI experiment can be configured to add a behavior at the initialization of the experiment and before each step.
// Note that a simulation is always created by default, and the execution of the simulation is managed by its own behaviors.
experiment exp_with_init_reflex type: gui {

	init {
		write "====== Init of " + self color: #green;
		
		// simulations field of experiment contain all the simulations
		// simulation filed contains the last created simulation.
		write "The experiment contains " + length(simulations) + " simulations." color: #green;
		write "The last created simulation is: " + simulation color: #green;	
	}
	
	reflex t {
		write sample(cycle) + " - reflex t of " + self color: #red;
	}
}

// The init of the experiment is the place to create the additional simulations to run.
// The experiment can control the way these simulations are executed using:
// - schedules: facet (list of simulation agents): that specifies which simulations are executed and in which order.
// - parallel: facet (true/false): whether they are executed parallelly or sequenticially.
//
// The experiment can access to all the simulations.
//
// Note: the experiment and all the simulations have the same seed by default. To avoid that, the seed should be set at hand.
experiment exp_4_simulations type: gui schedules: shuffle(simulations) parallel: true  {

	init {
		write "====== Init of " + self color: #green;
		write sample(seed) + " - of " + self.name color: #green;								
		
		create simulation with:[name::"Simu 1",nb_people::rnd(10),seed::rnd(1.0)];
		create simulation with:[name::"Simu 2",nb_people::rnd(10),seed::rnd(1.0)];
		create simulation with:[name::"Simu 3",nb_people::rnd(10),seed::rnd(1.0)];
				
		write "The experiment contains " + length(simulations) + " simulations." color: #green;
		write "The last created simulation is: " + simulation color: #green;
	}
	
	reflex t {
		write sample(cycle) + " - reflex t of " + self color: #red;
		// The experiment can access to all the simulations, but also all the agents of all the simulations
		write sample(simulations with_max_of(each.nb_people)) color: #red;
		write sample(sum(simulations accumulate(length(each.agents)))) color: #red;
		
		loop s over: simulations {
			write sample(length(s.people)) + " ("+s.name+")" color: #red;
		}
	}
}

// The default behavior of the experiment (with init and reflexs) can also be modified by redefining 2 actions:
// - _init_ : its default behavior is to create a simulation and run the init statement of the experiment
// - _step_ : its default behavior is to execute the behavior of the experiment (its reflexes) and then to call the behavior of the simulations.
// When they are redefined, the default behaviors are disabled :
// - _init_ : no simulation created and init statement is not called, 
// - _step_ : the behavior of exepriment and simulations is not called.
experiment exp_no_simulation type: gui {

	action _init_ {
		write "_init_ of " + self color: #green;
		create simulation with:[name::"Simu 1",nb_people::rnd(10)];		
	}

	init {
		error "This will not be executed";
		write "init of " + self color: #green;	
	}
	
	action _step_ {
		write sample(cycle) + "  _step_ of " + self  color: #red;
		write "NOTHING more is executed in the step" color: #red;
	}
	
	reflex t {
		error "This will not be executed" ;		
		write "reflex t of " + self color: #red;
	}
}

// Note that the basic simulation defined as:
//       experiment basic_exp type: gui { }
// could be written, using _init_ and _step_ as follows
experiment basic_exp_init_step {
	action _init_ {
		create simulation;
	}
	
	action _step_ {
		ask simulations {
			do _step_;
		}
	}
} 

////////////////////////////////////////////////////////////
// When the experiment is a batch, the two init and reflex statements are executed as follows:
// - init is executed as the initialization of the experiment design and thus before the creation of any simulation 
// - reflex is executed after each replications (i.e. after the execution of the N simulations corresponding to a replication,
// N is specified in the repeat: statement.
experiment batch_4_simu type: batch autorun: true repeat: 4 until: (cycle > 2) parallel: true keep_simulations: true {

	parameter "nb_people" var: nb_people among: [0,5,10];

	init {
		write "====== Init of " + self color: #green;
		write sample(seed) + " - of " + self.name color: #green;								
		write "The experiment contains " + length(simulations) + " simulations." color: #green;
		write "The last created simulation is: " + simulation color: #green;		
	}
	
	// 
	reflex t {
		write "============================================" color: #red;
		write "End of replicates" color: #red;
		write sample(cycle) + " - reflex t of " + self.name color: #red;
		
		// The experiment can access to all the simulations, but also all the agents of all the simulations
		// It can be classically used in batch to save results in a file.
		write sample(simulations with_max_of(each.nb_people))  color: #red;
		write sample(sum(simulations accumulate(length(each.agents)))) color: #red;
		
		loop s over: simulations {
			write sample(length(s.people)) + " ("+s.name+")" color: #red;
		}
		// We get rid of the simulations manually as we memorize them with 'keep_simulations' (necessary for accessing their attributes)
		ask simulations {
			do die;
		}
	}
}

////////////////////////////////////////////////////////////
// When the experiment is recorded, the behavior is very similar to the gui experiment one's.
// Note that when 1 step back is made, no behavior is executed
experiment mem_simu record: true {

	init {
		write "====== Init of " + self color: #green;
		write "The experiment contains " + length(simulations) + " simulations." color: #green;
		write "The last created simulation is: " + simulation color: #green;		
	}
	
	// 
	reflex t {
		write "============================================" color: #red;
		write sample(cycle) + " - reflex t of " + self.name color: #red;
	}
}

////////////////////////////////////////////////////////////
// When the experiment is recorded, if several simulations are created, they are all stepped back.
experiment mem_2_simu record: true {

	init {
		write "====== Init of " + self color: #green;
		create simulation;
		write "The experiment contains " + length(simulations) + " simulations." color: #green;
		write "The last created simulation is: " + simulation color: #green;		
	}
	
	// 
	reflex t {
		write "============================================" color: #red;
		write sample(cycle) + " - reflex t of " + self.name color: #red;
	}
}
