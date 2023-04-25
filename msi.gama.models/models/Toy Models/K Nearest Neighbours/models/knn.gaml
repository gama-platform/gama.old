/**
* Name: K Nearest Neighbors
* Description: This model represents how the K Nearest Neighbors Algorithm work. Whenever the user click on any location, a point will
* be generated and its color will be based on the colors of its k-nearest points. The user can also let the point generated dynamically and 
* the colors of these points will also based on the colors of their k-nearest points
* Author: Minh Nguyen Dich Nhat
* Tags: 
*/

/**
 * There will be four parameters
 * The first one is "Number of group", it will specify the number of group in our model. Different groups have different colors.
 * The second one is "Radius", this parameter is the radius of the circles which contain the groups. 
 * The third one is "Initial number of point for each group". 
 * And the final one is "Number of neighbors", this will specify the number of neighbors that we want to use for our "k_nearest_neighbors" operator,
 * the "k_nearest_neighbors" have one parameter called k, this parameter specify the number of agents we want to compare with our agent. The 
 * "Number of neighbours" parameter is the parameter k in the "k_nearest_neighbors" operator.
 */


model KNearestNeighbors


global {

	map<unknown, unknown> store;
	list<rgb> colour <- [#red, #blue, #green, #yellow, #purple, #orange, #pink, #magenta, #cyan];
    int init_amount <- 10;
    int nb_group <- 2;
    float radius <- 10.0;
    int k <- 10;
	init {
		loop i from: 0 to: nb_group - 1{
			special_point center;
			create special_point{
				color <- colour[i];
				center <- self;
			}
			geometry around <- circle(radius, center.location) intersection world.shape;
			create special_point number: init_amount - 1{
				location <- any_location_in(around);
				color <- center.color;
			}
		}
		store <- world.special_point as_map (each::each.color);
	}

	action createAgent{
	    create special_point{
	    	location <- #user_location;
	    	color <- self k_nearest_neighbors (store, k);
		    store <+ (self::color);
	    }
	}

	reflex auto{
		create special_point{
	   		color <- self k_nearest_neighbors (store, k);
	   		store <+ self::color;
	   	}
	}
}

species special_point {
	float size <- 0.5;
	rgb color;
	aspect base{
		draw circle(size) color:color;
	}
	
}

experiment demo {
	parameter "Number of groups: " var:nb_group min: 2 max: 9;
	parameter "Radius: " var: radius min:10.0 max:30.0;
	parameter "Initial numbers of agent for each group: " var:init_amount min:1 max:50;
	parameter "Number of neighbours considered (K)" var:k min:1;
	output {
		display main {
			species special_point aspect: base;
			event #mouse_down {ask simulation {do createAgent;}} 
		}
	}
}