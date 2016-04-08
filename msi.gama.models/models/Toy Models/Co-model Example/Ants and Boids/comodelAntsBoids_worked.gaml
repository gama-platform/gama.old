/**
* Name: ComodelAntsBoids
* Author: hqnghi
* Description: Co-model example : coupling Ants and Boids. In an experimental use case, Boids chase and eat Ants when Ants are trying to fill-up their nids.
* Tags: comodel
 */
model comodelAnts
// Import the Ant model with an alias name, this alias name will be used as micro-model identifier in co-model.
import "../../../Toy Models/Ants (Foraging and Sorting)/models/Ant Foraging (Complex).gaml" as myAnt
//Import the Boids  model with an alias name, this alias name will be used as micro-model identifier in co-model.
import "../../../Toy Models/Boids/models/Boids.gaml" as myBoids


global
{
	geometry shape <- envelope(500);
	init
	{
	//		 create instants of experiment (experiment agents) of micro-model  
		create myBoids.boids_gui with: [shape::circle(5), width_and_height_of_environment::500, number_of_agents::20];
		create myAnt.Complete with: [gridsize::100, ants_number::100];
	}

	reflex dododo
	{
		// tell the micro-model to do a simulation step
		ask (first(myAnt.Complete).simulation)
		{
			do _step_;
		}

		ask (first(myBoids.boids_gui).simulation)
		{
			do _step_;
		}

	}

}

experiment comodelExp_Ants_Boids type: gui
{
	output
	{
		display "comodel_disp"
		{
			image 'background' file: '../images/soil.jpg';
			species species((first(myBoids.boids_gui).simulation.boids)[0]) aspect: image;
			agents "agents_ant_grid" transparency: 0.5 position: point((first(myBoids.boids_gui).simulation.boids_goal)) value: (first(myAnt.Complete).simulation.ant_grid as list) where
			((each.food > 0) or (each.road > 0) or (each.is_nest));
			agents "agents_ant" aspect: icon value: first(myAnt.Complete).simulation.ant position: point((first(myBoids.boids_gui).simulation.boids_goal));
			agents "agents_bois" aspect: image value: first(myBoids.boids_gui).simulation.boids; //  position:(first(myBoids.Boids2).simulation.boids_goal); 

		}

	}

}
