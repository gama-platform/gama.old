/**
* Name: ComodelAntsBoids
* Author: hqnghi
* Description: Co-model example : Voronoi applied on Boids.
* Tags: comodel
 */
model comodelBoidsVoronoi
import "Boids_coupling.gaml" as myBoids
//import "Evacuation_coupling.gaml" as myEvacuation
import "Voronoi_coupling.gaml" as myVoronoi


global
{
	geometry shape <- envelope(100);
	init
	{	
		create myBoids.Boids_coupling_exp with: [shape::square(0.5), width_and_height_of_environment::100, number_of_agents::100];
		create myVoronoi.Voronoi_coupling_exp with:[num_points::100, env_width::100, env_height::100];
	}

	reflex dododo
	{
		ask (myBoids.Boids_coupling_exp collect each.simulation){ do _step_;}
		list<point> p<-(myBoids.Boids_coupling_exp accumulate each.getBoids()) collect each.location;
		ask (myVoronoi.Voronoi_coupling_exp collect each.simulation){ ask center{location<-p at (int(self)); }do _step_;}
	}

}

experiment comodelExp_Boids_Voronoi type: gui
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
