/**
* Name: Event Feature
* Author: Arnaud Grignard & Patrick Taillandier & Jean-Daniel Zucker
* Description: Model which shows how to use the event layer to trigger an action according to an event occuring in the display. The experiment 
* has two displays : one for the changing color event, one for the changing shape event.
* Tags: gui
 */
model event_layer_model


global
{

//number of agents to create
	int nbAgent <- 200;
	int radius <- 10;
	dummy pointClicked;

	init {
	//creation of the agents
		create cell number: nbAgent
		{
			colour <- #darkgreen;
		}
       create dummy number:1 returns: temp with: [dummyRadius :: radius];
       pointClicked <- first(temp);
   }

	//Action to change the color of the agents, according to the point to know which agents we're in intersection with the point
	action change_color 
	{

	//change the color of the agents
		
		list<cell> selected_agents <- cell overlapping (circle(10) at_location #user_location);
		ask selected_agents
		{
			colour <- colour = #lightgreen ? #darkgreen : #lightgreen;
		}

	}

	action draw_clicked_area_in_view_color
	{
		pointClicked.location <- #user_location;
		pointClicked.visibleViewColor <- true;
	}
	action draw_clicked_area_in_view_shape
	{
		pointClicked.location <- #user_location;
		pointClicked.visibleViewShape <- true;
	}

	action hide_clicked_area
	{
		pointClicked.visibleViewColor <- false;
		pointClicked.visibleViewShape <- false;
	}


	//Action to change the shape of the agents, according to the point to know which agents we're in intersection with the point
	action change_shape 
	{
		list<cell> selected_agents <- cell overlapping (circle(radius) at_location #user_location);
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
	rgb colour;
	bool is_square <- false;
	reflex mm
	{
		do wander amplitude: 30.0;
	}

	aspect default
	{
		draw is_square ? square(2) : circle(1) color: colour;
	}

}

species dummy  {
	int dummyRadius <- 10;
	bool visibleViewColor <- false;
	bool visibleViewShape <- false;
	
	aspect aspect4ViewChangeColor {
		if visibleViewColor {draw circle(radius) color: #grey;}
	}
	
	aspect aspect4ViewChangeShape {
		if visibleViewShape {draw circle(radius) color: #grey;}
	}
	
}



experiment Displays type: gui
{
	parameter "Radius of selection" var: radius ;	// The radius of the disk around the click 
	output synchronized:true
	{    
		layout horizontal([0::5000,1::5000]) tabs:true editors: false;
		display View_change_color
		{
			species cell;
			species dummy transparency:0.9 aspect: aspect4ViewChangeColor;
			// event, launches the action change_color if the event mouse_down (ie. the user clicks on the layer event) is triggered
			// the action can be either in the experiment or in the global section. If it is defined in both, the one in the experiment will be chosen in priority
			event #mouse_down {ask simulation {do change_color;}}  
			event #mouse_move {ask simulation {do draw_clicked_area_in_view_color;}} 
			event #mouse_exit {ask simulation {do hide_clicked_area;}}
			// Shows how to manipulate the cells using keyboard events
			event #arrow_left {ask (cell) {location <- location - {1,0};}}
			event #arrow_right {ask (cell) {location <- location + {1,0};}}
			event #arrow_up {ask (cell) {location <- location - {0,1};}}
			event #arrow_down {ask (cell) {location <- location + {0,1};}}
			event #escape {ask (cell) {location <- rnd(point(100,100));}}
			
		}

		display View_change_shape type: 3d
		{
			light #ambient active: false;
			species cell;
			species dummy transparency:0.9 aspect: aspect4ViewChangeShape ;
			//event, launches the action change_shape if the event mouse_down (ie. the user clicks on the layer event) is triggered
			// The block is executed in the context of the experiment, so we have to ask the simulation to do it. 
			event #mouse_down {ask simulation {do change_shape;}}  
			event #mouse_move {ask simulation {do draw_clicked_area_in_view_shape;}} 
			event #mouse_exit {ask simulation {do hide_clicked_area;}} 
			// Shows how to manipulate the cells using keyboard events, disactivating the "regular" arrow / esc keys behavior
			event #arrow_left {ask (cell) {location <- location - {1,0};}}
			event #arrow_right {ask (cell) {location <- location + {1,0};}}
			event #arrow_up {ask (cell) {location <- location - {0,1};}}
			event #arrow_down {ask (cell) {location <- location + {0,1};}}
			event #escape {ask (cell) {location <- rnd(point(100,100));}}
		}

	}

}

