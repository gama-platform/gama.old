/**
* Name: DXF to Agents Model
* Author:  Patrick Taillandier
* Description: Model which shows how to create agents by importing data of a DXF file
* Tags:  dxf, load_file
*/
model DXFAgents


global
{
	file house_file <- dxf_file("../includes/house.dxf",#m);

	//compute the environment size from the dxf file envelope
	geometry shape <- envelope(house_file);
	init
	{
	//create house_element agents from the dxf file and initialized the layer attribute of the agents from the the file
		create house_element from: house_file with: [layer::string(get("layer"))];
		
		//define a random color for each layer
		map layers <- list(house_element) group_by each.layer;
		loop la over: layers.keys
		{
			rgb col <- rnd_color(255);
			ask layers[la]
			{
				color <- col;
			}
		}
	}
}

species house_element
{
	string layer;
	rgb color;
	aspect default
		{
		draw shape color: color;
	}
	init {
		shape <- polygon(shape.points);
	}
}

experiment DXFAgents type: gui
{   
	output
	{	layout #split;
		display map type: 3d
		{
			species house_element;
		}

		display "As_Image" type: 3d
		{
			graphics "House"
			{
				draw house_file at: {0,0} color: # brown;
			}

		}

	}

}
