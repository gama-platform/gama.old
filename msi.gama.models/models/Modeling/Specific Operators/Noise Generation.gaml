/**
* Name: simplexnoiseexample
* Author: damienphilippon
* Description: Shows how to generate a random landscape
* Tags: spatial, random
*/

model simplexnoiseexample



global
{
	float AMPLITUDE<-30.0 parameter:"Amplitude ";
	int GRID_SIZE <- 50 parameter:"Size of the grid ";
	float HEIGHT_WATER<-13.00 parameter:"Height for water ";
	float MINIMUM_HEIGHT <- 12.0 parameter:"Minimum height ";
	bool simplexGenerator <- false;
	rgb BLUE_WATER<-rgb(0.0,120.0,255.0,0.6);
	int BIAIS <- rnd(512);
	
	init
	{
		HEIGHT_WATER <- max([HEIGHT_WATER,mean(cells collect(each.grid_value-2))]);
		create water number:1
		{
			shape <- box(world.shape.width,world.shape.height,HEIGHT_WATER);
		}
	}
}
species water schedules:[]
{
	geometry shape;
	
	aspect default
	{
		draw shape color: BLUE_WATER at: world.location;
	}
}
grid cells width:GRID_SIZE height:GRID_SIZE
{
	float height_simplex<-simplex_generator(self.grid_x/GRID_SIZE,self.grid_y/GRID_SIZE,BIAIS)*AMPLITUDE + MINIMUM_HEIGHT;
	float height_open <- open_simplex_generator(self.grid_x/GRID_SIZE,self.grid_y/GRID_SIZE,BIAIS)*AMPLITUDE + MINIMUM_HEIGHT;
	rgb color <- rgb(0,255,0,0.5);
}
experiment "Generate" type:gui
{
	output
	{
		display open_simplex type:opengl
		{
			grid cells elevation:height_open triangulation:true;
			species water aspect: default;
		}
		display simplex type:opengl
		{
			grid cells elevation:height_simplex triangulation:true;
			species water aspect: default;
		}
	}
}