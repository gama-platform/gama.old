/**
* Name: PNGIcons
* Author: drogoul
* Description: Shows how to load icons in PNG format and display them, playing with their size and rotation
* Tags: Image, Display
*/
model PNGIcons


global
{
// We fist load the folder of icons
	file icons <- folder("../includes/icons/");
	// And filter the file names that contain "png"
	list<image_file> file_list <- icons select (each contains ("png")) collect (png_file: image_file(icons.path + "/" + png_file));
	// We give an arbitrary size to the world 
	geometry shape <- envelope(200);
	init
	{
	// We create a number of people equivalent to the number of icons
		create people number: length(file_list) ;
	}

}

species people skills: [moving]
{
	
// Each people is provided with the path of its icon (can be changed dynamically, of course)
	image_file icon <- file_list[int(self)];
	// 'increment' is used to change the size dynamically
	int increment <- 1;
	// The size with which the icon will be displayed (w.r.t. to the size of the world). It is incremented or decremented each step
	int size <- rnd(int(world.shape.width / 4)) update: size + increment on_change:
	{
		if (increment = 1)
		{
		// When the size is greater than the half the size of the world, the 'increment' becomes -1
			if (size > world.shape.width / 2)
			{
				increment <- -1;
			}

		} else
		{
		// Otherwise if the size is too small, 'increment" becomes 1
			if (size < 1)
			{
				increment <- 1;
			}

		}

	};

	// The default behavior of people is to move around
	reflex default
	{
	//	heading <- heading + 30;
		do wander amplitude: 100.0;
	}

	// The default aspect will be used when no other aspect is invoked in displays
	aspect 3d
	{ 
		// We draw the image corresponding to the path, with a size given by 'size' and we use the heading of the people to rotate it
		draw 	sphere(size/4) 
				lighted: false 
				rotate:heading 
				wireframe: false 
				texture: icon 
				at: location + {0,0,increment} 
				color: rgb (251, 217, 113,255);
 	}
 	
 	aspect java2d {
 		// We draw the image corresponding to the path, with a size given by 'size' and we use the heading of the people to rotate it
		draw icon size: size/2 rotate: heading;
 	}

}

experiment Icons
{
	// We slow down the simulation in order to see something !

	output synchronized: true
	{
		layout #split;
		
		display "Colorful in 3D" type: 3d axes:false
		{
			// We simply display people, which will use their aspect named 3d
			species people aspect: 3d;
		}
		
		display "Colorful in 2D" type:  2d {
			
			// Idem for 2d
			species people aspect: java2d;
		}

	}

}

