/**
* Name: Comodel of Boids and Voronoi
* Author: HUYNH Quang Nghi
* Description: Co-model example : The Boids is applied in Voronoi presentation . https://www.youtube.com/watch?v=I9hBeJQUFYg
* Tags: comodel
 */
model Voroboids
import "Adapters/Boids Adapter.gaml" as Boids
import "Adapters/Voronoi Adapter.gaml" as Voronoi


global
{
	int width_and_height_of_environment<-200;
	// set the bound of the environment
	geometry shape <- envelope(width_and_height_of_environment);
	
	init
	{	
		//create experiment from micro-model Boids
		create Boids."Adapter" with: [
			shape::square(width_and_height_of_environment), 
			width_and_height_of_environment::width_and_height_of_environment, 
			number_of_agents::10
		];
		//create experiment form micro-model Voronoi
		create Voronoi."Adapter 2" with:[
			num_points::Boids."Adapter"[0].simulation.number_of_agents, 
			env_width::width_and_height_of_environment, 
			env_height::width_and_height_of_environment
		];
	}

	reflex simulate_micro_models
	{
		//tell myBoids to step a cycle
		ask (Boids."Adapter" collect each.simulation){ do _step_;}
		//get all boids's location into a list
		list<point> theLocations<-(Boids."Adapter" accumulate each.get_boids()) collect each.location;
		//myVoronoi do a step with the location of their agent from the location list above 
		ask (Voronoi."Adapter 2" collect each.simulation){ ask center{location<-theLocations at (int(self)); }do _step_;}
	}

}
 
experiment main type: gui
{
	output synchronized: true
	{
		display "Comodel Display"  
		{
			agents "cell" value: (Voronoi."Adapter 2" accumulate each.get_cell()) transparency:0.5;
			
			agents "boids_goal" value: (Boids."Adapter" accumulate each.get_boids_goal()) aspect:default;
			
			agents "boids" value: (Boids."Adapter" accumulate each.get_boids())  aspect:default;
			
		}

	}

}
