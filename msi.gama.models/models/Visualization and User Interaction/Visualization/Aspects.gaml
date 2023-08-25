/***
* Name: Aspects
* Author: Benoit Gaudou
* Description: This model details the various possibility of agent's drawing in an aspect.
* In particular, it attempts to answer questions such as:
* 1. how to display agents using geometries ? text ? images ?
* 2. how to display them in 3D ? with texture ? taken into account the light ?
* 3. how to display the link between 2 agents ?
* 4. how to deal with transparency in agents ?
* 5. how to focus on some agents ?
* 
* Tags: aspect, 3D, draw, transparency, focus
***/
model Aspects

global {
	image_file wood0_image_file <- image_file("3D Visualization/includes/wood.jpg");

	init {
	//
	// 1. How to display agents using geometries ? text ? images ?
	// 
	// All the agents are displayed using a single circle and text comment (their aspect)
		create people with: [location::{25, 25}, aspect_type::"Asp. default"];
		// The agent people(1) will be displayed using several geometries, that can be empty or not	
		create people with: [location::{25, 75}, aspect_type::"Asp. simple"];
		// The agent people(2) will be displayed using advanced features on text: a font, perspective ...				
		create people with: [location::{25, 50}, aspect_type::"Asp. text"];
		// The agent people(3) will be displayed in a location that is not its own location 
		//    (to illustrate that the way the agent is displayed can be totally different from its actual state).						
		create people with: [location::{50, 25}, aspect_type::"Asp. centered"];
		// The agent people(4) will be displayed in using a picture that is resize	
		create people with: [location::{50, 12}, aspect_type::"Asp. img"];

		//
		// 2. how to display them in 3D ? with texture ? taken into account the light ?
		//
		// The agent people(5) will be displayed with 3d  or isometric 2D geometries		
		create people with: [location::{85, 50}, aspect_type::"Asp. 3D"];
		// The agent people(6) will be displayed using texture and material
		create people with: [location::{50, 75}, aspect_type::"Asp. textured"];

		//
		// 3. how to display the link between 2 agents ?
		//
		// The agent people(7) is displayed to show its relation with another agent,
		//    drawing a line between them and arrows at the beginning and the end of the line.		
		create people with: [location::{75, 75}, aspect_type::"Asp. arrows"];

		//
		// 4. how to deal with transparency in agents ?
		//
		// The agent people(8) is displayed using a square with transparency 
		create people with: [location::{75, 25}, aspect_type::"Asp. cube"];
		// Note that the transparency facet can only be defined in the display, not in the aspect.
		// To display agents of a species with a transparency that is different for each agent,
		//    GAMA provides the possibility to define a color with a transparency that can be different 
		//    depending in the agent. 
		//
		// In the following, all the people agent have the attribute color_transparency, a color with a transparency depending on the agent
		// and the aspect big_circle_with_transparency that draws all of them with a big circle with a transparency depending on the agent.
	}

	// In addition, every 100 cycles, the camera will focus on one specific random agent.
	reflex focus when: every(100 #cycles) and (cycle > 0) {
		write "Change the focus";
		focus_on one_of(agents);
	}

}

species people {
	string aspect_type;
	rgb color_transparency <- rgb(#darkred, 0.4 / (1 + int(self)));

	// aspect blocks defines how each agent is displayed
	aspect default {
	// The draw statement takes any drawable objects (geometry, text, image)...
	// The aspect color: defines the inner color of the geometry or the color of the text.
	// The aspect border: defines the geometry border color.
		draw circle(0.5) color: #red border: #yellow;
		draw aspect_type color: #black at: location + {0, 4, 1} anchor: #bottom_center depth: 3;
	}

	// In an aspect, it is possible to draw several layers one after the other.	
	aspect simple {
		draw circle(0.5) color: #grey border: #darkgrey;
		// The facet wireframe: sets whether the drawn geometry is plain or empty.  
		draw circle(1.0) border: #darkgreen wireframe: true;
		draw square(3.0) border: #darkgreen wireframe: true;
	}

	// In an aspect, it is possible to draw several layers one after the other.	
	aspect big_square_for_transparency {
		draw square(3.0) color: #darkblue border: #black;
	}

	// In an aspect, it is possible to draw several layers one after the other.	
	aspect big_circle_with_transparency {
		draw circle(5.0) color: color_transparency border: #black;
	}

	// The object to draw can be text.
	// The draw can be configured using the color: facet and the font: one.
	// font: facet is expecting a font obbject composed by the name of the font, its size and #bold/#italic/#plain/#bold+#italic.	
	aspect simple_text {
	// perspective: facet specifies whether to render the text in perspective or facing the user.		
		draw "Agent: " + name + "(rotate!)" at: location + {0, 8, 0} color: #green font: font("Arial", 10, #bold) perspective: false;
		draw "Agent: " + name at: location + {0, 6, 0} color: #blue font: font("SansSerif", 15, #italic) perspective: true;
	}

	// When a line is drawn, it could be interesting to also draw an arrow at one of the extremities of the line
	aspect arrows {
	// begin_arrow: and end_arrow: facets are used to define the size of the drawn line.
	// width: facet can be used to increase the width of a line 
	// (the use of width with other geometry will provide unexpected result, as it increase each line of the geometry)
		draw line([self.location, people(8).location]) color: #orange end_arrow: 1 begin_arrow: 2.2 width: 3.0;
	}

	// at (point): location where the shape/text/icon is drawn
	// rotate (any type in [float, int, pair]): orientation of the shape/text/icon; can be either an int/float (angle) or a pair float::point (angle::rotation axis). The rotation axis, when expressed as an angle, is by defaut {0,0,1}
	// The elements drawn are by default located on the agent location, but they can be moved anywhere in the environment.
	// They can also be rotated.		
	aspect locate_geometry {
		draw square(1.0) color: #silver border: #black at: world.location rotate: 45;
		draw "Agent in center" color: #green font: font("Arial", 15, #bold) at: world.location + {0.0, 2.0, 0.0} anchor: #bottom_center;
	}

	// In an OpenGL display, the various geometries can be displayed in 3d, or more specifically with a depth (facet depth:).
	// for example, a circle with depth, will be displayed as a cylinder.
	// These 3D objects be displayed taken into account the light or not with the lighted facet:
	aspect col3D {
		draw circle(1.0) color: #tomato depth: 5.0 lighted: true;
		draw sphere(1.0) at: (location - {2.0, 0.0, 0.0}) color: #tomato;
		draw cube(1.0) at: (location - {-2.0, 0.0, 0.0}) color: #tomato;
		draw square(1.0) at: (location - {-2.0, 2.0, 0.0}) color: #tomato depth: 5.0 lighted: true;
	}

	// In a 3D display (openGL), agents can add texture (i.e. a picture on the geometry) and can be lighted or not.
	aspect textured {
		draw sphere(2.0) color: #tomato;
		draw sphere(2.0) at: (location - {4.0, 0.0, 0.0}) color: #tomato lighted: false;
		draw sphere(2.0) at: (location - {-4.0, 0.0, 0.0}) color: #tomato texture: wood0_image_file;
	}

	// When an agent is displayed drawing a picture, that picture can be resize (using the size: facet).
	aspect image {
		draw wood0_image_file size: {50.0, 10.0, 0.0};
	}

}

//experiment expe type: gui {
//	output {
//		display my_display {
//			graphics "layer1" position: {0, 0} size: {0.5, 0.8} {
//				draw shape color: #darkorange;
//			}
//
//			graphics "layer2" position: {0.3, 0.1} size: {0.6, 0.2} {
//				draw shape color: #cornflowerblue;
//			}
//
//			graphics "layer3" position: {0.4, 0.2} size: {0.3, 0.8} {
//				draw shape color: #gold;
//			}
//		}
//	}
//}

experiment Aspects type: gui {
	float minimum_cycle_duration <- 0.01;
	output {
		layout #split;
		display displ_openGL type: 3d {
			species people aspect: big_circle_with_transparency;
			//		
			agents "layer simple" value: [people(1)] aspect: simple;
			agents "Text" value: [people(2)] aspect: simple_text;
			agents "locate geom" value: [people(3)] aspect: locate_geometry;
			agents "image" value: [people(4)] aspect: image;
			//
			agents "3D" value: [people(5)] aspect: col3D;
			agents "Textured" value: [people(6)] aspect: textured;
			//
			agents "Arrows" value: [people(7)] aspect: arrows;
			//
			agents "simple with transparency" value: [people(8)] aspect: big_square_for_transparency transparency: 0.5;
			//
			// when no aspect: facet is specified, GAMA tries to use the aspect named default. 
			// If no such an aspect exists, it draws the shape of the agent with a color defined in the preferences.				
			species people;
		}

		//		display displ_2D type: 2d {
		//			species people aspect: big_circle_with_transparency;
		//			//		
		//			agents "layer simple" value: [people(1)] aspect: simple;
		//			agents "Text" value: [people(2)] aspect: simple_text;
		//			agents "locate geom" value: [people(3)] aspect: locate_geometry;			
		//			agents "image" value: [people(4)] aspect: image;
		//			//
		//			agents "3D" value: [people(5)] aspect: col3D;	
		//			agents "Textured" value: [people(6)] aspect: textured;			
		//			//
		//			agents "Arrows" value: [people(7)] aspect: arrows;
		//			//
		//			agents "simple with transparency" value: [people(8)] aspect: big_square_for_transparency transparency: 0.5;					
		//			//
		//			// when no aspect: facet is specified, GAMA tries to use the aspect named default. 
		//			// If no such an aspect exists, it draws the shape of the agent with a color defined in the preferences.				
		//			species people;					
		//		}		
	}

}
