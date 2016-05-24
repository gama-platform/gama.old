model circle

global
{
	int number_of_agents min: 1 <- 50;
	int radius_of_circle min: 10 <- 1000;
	int repulsion_strength min: 1 <- 5;
	int width_and_height_of_environment min: 10 <- 3000;
	int range_of_agents min: 1 <- 25;
	float speed_of_agents min: 0.1 <- 2.0;
	int size_of_agents <- 100;
	point center const: true <- { width_and_height_of_environment / 2, width_and_height_of_environment / 2 };
	geometry shape <- square(width_and_height_of_environment);
	init
	{
		create cell number: number_of_agents;
	}

}

species cell skills: [moving]
{
	rgb color const: true <- [100 + rnd(155), 100 + rnd(155), 100 + rnd(155)] as rgb;
	float size const: true <- float(size_of_agents);
	float range const: true <- float(range_of_agents);
	float speed const: true <- speed_of_agents;
	int heading <- rnd(359);
	geometry shape <- circle(size) simplification (1);
	reflex go_to_center
	{
		heading <- (((self distance_to center) > radius_of_circle) ? self towards center : (self towards center) - 180);
		do move speed: speed;
	}

	reflex flee_others
	{
		cell close <- one_of(((self neighbors_at range) of_species cell) sort_by (self distance_to each));
		if (close != nil)
		{
			heading <- (self towards close) - 180;
			float dist <- self distance_to close;
			do move speed: dist / repulsion_strength heading: heading;
		}

	}

	aspect default
	{
		draw shape color: color;
	}

}

experiment circle type: gui
{
	parameter 'Number of Agents' var: number_of_agents;
	parameter 'Radius of Circle' var: radius_of_circle;
	parameter 'Strength of Repulsion' var: repulsion_strength;
	parameter 'Dimensions' var: width_and_height_of_environment;
	parameter 'Range of Agents' var: range_of_agents;
	parameter 'Speed of Agents' var: speed_of_agents;
	output
	{
		display Circle refresh: every(1)
		{
			species cell;
		}

	}

}

