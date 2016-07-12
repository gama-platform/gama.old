/**
* Name: Event Feature
* Author: Arnaud Grignard & Patrick Taillandier
* Description: Model which shows how to use the event layer to trigger an action according to an event occuring in the display. The experiment 
* has two displays : one for the changing color event, one for the changing shape event.
* Tags: gui
 */
model event_layer_model


global
{

//number of agents to create
	int nbAgent <- 500;
	init
	{

	//creation of the agents
		create cell number: nbAgent
		{
			color <- °green;
		}

	}

	//Action to change the color of the agents, according to the point to know which agents we're in intersection with the point
	action change_color 
	{

	//change the color of the agents
		list<cell> selected_agents <- cell overlapping (circle(10) at_location #user_location);
		ask selected_agents
		{
			color <- color = °green ? °pink : °green;
		}

	}

	//Action to change the shape of the agents, according to the point to know which agents we're in intersection with the point
	action change_shape 
	{
		list<cell> selected_agents <- cell overlapping (circle(10) at_location #user_location);
		ask selected_agents
		{

		//change the bool attribute is_square to change the shape in the display
			is_square <- not (is_square);
		}

	}

}

//Species cells moving randomly
species cell skills: [moving]
{
	rgb color;
	bool is_square <- false;
	reflex mm
	{
		do wander amplitude: 30;
	}

	aspect default
	{
		draw is_square ? square(2) : circle(1) color: color;
	}

}

experiment Displays type: gui
{
	output
	{
		display View_change_color
		{
			species cell aspect: default;

			//event, launches the action change_color if the event mouse_down (ie. the user clicks on the layer event) is triggered
			event [mouse_down] action: change_color;
		}

		display View_change_shape type: opengl
		{
			species cell;

			//event, launches the action change_shape if the event mouse_down (ie. the user clicks on the layer event) is triggered
			event [mouse_down] action: {do change_shape;};
		}

	}

}

