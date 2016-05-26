/**
* Name: Comodel of Boids and Voronoi
* Author: HUYNH Quang Nghi
* Description: Co-model example : The Boids is applied in Voronoi presentation . https://www.youtube.com/watch?v=I9hBeJQUFYg
* Tags: comodel
 */
model Voroboids
import "The Couplings/Boids Coupling.gaml" as myBoids
import "The Couplings/Voronoi Coupling.gaml" as myVoronoi


global
{
	// set the bound of the environment
	geometry shape <- envelope(100);
	
	init
	{	
		//create experiment from micro-model myBoids
		create myBoids.boids_gui with: [shape::square(0.5), width_and_height_of_environment::100, number_of_agents::100];
		//create experiment form micro-model myVoronoi
		create myVoronoi.voronoi with:[num_points::100, env_width::100, env_height::100];
	}

	reflex simulate_micro_models
	{
		//tell myBoids to step a cycle
		ask (myBoids.boids_gui collect each.simulation){ do _step_;}
		//get all boids's location into a list
		list<point> theLocations<-(myBoids.boids_gui accumulate each.get_boids()) collect each.location;
		//myVoronoi do a step with the location of their agent from the location list above 
		ask (myVoronoi.voronoi collect each.simulation){ ask center{location<-theLocations at (int(self)); }do _step_;}
	}

}

experiment main type: gui
{
	output
	{
		display "comodel_disp" 
		{
			agents "cell" value: (myVoronoi.voronoi accumulate each.get_cell());
			
			agents "boids_goal" value: (myBoids.boids_gui accumulate each.get_boids_goal()) {draw circle(5) color:#red;}
			
			agents "boids" value: (myBoids.boids_gui accumulate each.get_boids()) {draw circle(1) color:#blue;}
			
		}

	}

}
