/**
* Name: Comodel of Boids and Voronoi
* Author: HUYNH Quang Nghi
* Description: Co-model example : Voronoi applied on Boids.
* Tags: comodel
 */
model comodelBoidsVoronoi
import "Boids_coupling.gaml" as myBoids
import "Voronoi_coupling.gaml" as myVoronoi


global
{
	// set the bound of the environment
	geometry shape <- envelope(100);
	
	init
	{	
		//create experiment from micro-model myBoids
		create myBoids.Boids_coupling_exp with: [shape::square(0.5), width_and_height_of_environment::100, number_of_agents::100];
		//create experiment form micro-model myVoronoi
		create myVoronoi.Voronoi_coupling_exp with:[num_points::100, env_width::100, env_height::100];
	}

	reflex simulate_micro_models
	{
		//tell myBoids to step a cycle
		ask (myBoids.Boids_coupling_exp collect each.simulation){ do _step_;}
		//get all boids's location into a list
		list<point> theLocations<-(myBoids.Boids_coupling_exp accumulate each.getBoids()) collect each.location;
		//myVoronoi do a step with the location of their agent from the location list above 
		ask (myVoronoi.Voronoi_coupling_exp collect each.simulation){ ask center{location<-theLocations at (int(self)); }do _step_;}
	}

}

experiment comodel_Boids_Voronoi_Exp type: gui
{
	output
	{
		display "comodel_disp" 
		{
			agents "cell" value: (myVoronoi.Voronoi_coupling_exp accumulate each.getCell());
			
			agents "boids_goal" value: (myBoids.Boids_coupling_exp accumulate each.getBoids_goal()) {draw circle(5) color:#red;}
			
			agents "boids" value: (myBoids.Boids_coupling_exp accumulate each.getBoids()) {draw circle(1) color:#blue;}
			
		}

	}

}
