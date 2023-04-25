/**
* Name: MovingAgents
* Author: drogoul
* Description: Shows how to move agents using two event layers : 
* 
* Click to grab an group of agents, click again to drop them. Press the keys "r" to kill the agents in the selection, and "c" to duplicate them.
* Tags: gui
*/
model MovingAgents

global
{
	list<being> moved_agents ;
	geometry shape <- square(1000);
	geometry zone <- circle(100);
	bool can_drop;
	init
	{
		create being number: 100;
	}

	action kill 
	{
		ask moved_agents
		{
			do die;
		}

		moved_agents <- [];
	}

	action duplicate 
	{
		geometry available_space <- (zone at_location #user_location) - (union(moved_agents) + 10);
		create being number: length(moved_agents) with: (location: any_location_in(available_space));
	}

	action click 
	{
		if (empty(moved_agents))
		{
			moved_agents <- being inside (zone at_location #user_location);
			ask moved_agents
			{
				difference <- #user_location - location;
				color <- # olive;
			}

		} else if (can_drop)
		{
			ask moved_agents
			{
				color <- # burlywood;
			}

			moved_agents <- [];
		}

	}

	action move 
	{
		can_drop <- true;
		list<being> other_agents <- (being inside (zone at_location #user_location)) - moved_agents;
		geometry occupied <- geometry(other_agents);
		ask moved_agents
		{
			location <- #user_location - difference;
			if (occupied intersects self)
			{
				color <- # red;
				can_drop <- false;
			} else
			{
				color <- # olive;
			}

		}

	}

}

species being skills: [moving]
{
	geometry shape <- square(10);
	point difference <- { 0, 0 };
	rgb color <- # burlywood;
	reflex r
	{
		if (!(moved_agents contains self))
		{
			do wander amplitude: 30.0;
		}

	}

	aspect default
	{
		draw shape color: color at: location + {1,0,1};
	}

}

experiment "Click and Move" type: gui
{
	font regular <- font("Helvetica", 14, # bold);
	rgb c1 <- rgb(#darkseagreen, 120);
	rgb c2 <- rgb(#firebrick, 120);
	output
	{
		layout #split;
		display "Click and Move [JAVA3D]" type: 3d  axes: false parent: "Click and Move [JAVA2D]"
		{
			light #ambient intensity: 100;
		}
		
		
		display "Click and Move [JAVA2D]" type: 2d 
		{

			graphics "Full target" 
			{
				int size <- length(moved_agents);
				if (size > 0)
				{
					draw zone at: #user_location wireframe: false border: false color: (can_drop ? c1 : c2);
					draw string(size) at: #user_location + { -30, -30 } font: regular color: # white;
					draw "'r': remove" at: #user_location + { -30, 0 } font: regular color: # white;
					draw "'c': copy" at: #user_location + { -30, 30 } font: regular color: # white;
				} else {
					draw zone at: #user_location wireframe: false border: false color: #wheat;
				}

			}

			species being;
			event #mouse_move {ask simulation {do move;}} 
			event #mouse_up {ask simulation {do click;}} 
			event 'r' {ask simulation {do kill;}} 
			event 'c'{ask simulation {do duplicate;}} 


		}

		
		
	}

}