/***
* Name: Aspects
* Author: ben
* Description: 
* Tags: Tag1, Tag2, TagN
***/

//  Create a model with different options for displays/aspects/, oriented towards drawing something specific 
// ("drawing a title",
//  "using transparency", 
// "focusing on agents" )


model Aspects

global {	
	init {
		// All the agents are displayed using a single circle and text comment
		create people with: [location::{25,25},aspect_type::"Asp. default"];
		// The agent people1 is displayed using several geometries, that can be empty or not	
		create people with: [location::{25,75},aspect_type::"Asp. simple"];
		// The agent people2 is displayed using a cube with transparency (not that the transparency can only be defined in the display, not in the aspect)		
		create people with: [location::{75,25},aspect_type::"Asp. cube"];
		// The agent people3 is displayed to show its relation with another agent, using a line between them and arrows at the beginning and the end of the line.		
		create people with: [location::{75,75},aspect_type::"Asp. arrows"];
		// The agent people4 is displayed using advanced features on text: a font, perspective ...				
		create people with: [location::{25,50},aspect_type::"Asp. text"];
		// The agent people5 is displayed in a location that is not its own location 
		// (to illustrate that the way the agent is displayed can be totally different from its actual state.						
		create people with: [location::{50,25},aspect_type::"Asp. centered"];		
	}
}

species people {
	people other <- (int(self) > 0) ? people[int(self) - 1] : nil;
	string aspect_type;
	
	// aspect blocks defines how each agent is displayed
	aspect default {
		// The draw statement takes any drawable objects (geometry, text, image)...
		// The aspect color: defines the inner color of the geometry or the color of the text.
		// The aspect border: defines the geometry border color.
		draw circle(0.5) color: #red border: #yellow;
		draw aspect_type color: #black at: location+{0,3,0} anchor: #bottom_center;
	}

	// In an aspect, it is possible to draw several layers one after the other.	
	aspect simple {
		draw circle(0.5) color: #grey border: #darkgrey;
		// The facet empty: sets whether the drawn geometry is plain or empty.  
		draw circle(1.0) border: #darkgreen empty: true width: 30;
		draw square(3.0) border: #darkgreen empty: true width: 30;
	}

	// In an aspect, it is possible to draw several layers one after the other.	
	aspect big_square_for_transparency {
		draw square(3.0) color: #darkblue border: #black  ;
	}
	
	// The object to draw can be text.
	// The draw can be configured using the color: facet and the font: one.
	// font: facet is expecting a font obbject composed by the name of the font, its size and #bold/#italic/#plain/#bold+#italic.	
	aspect simple_text {
		// perspective: facet specifies whether to render the text in perspective or facing the user.		
		draw "Agent: " + name + "(without perspective)" color: #green font: font("Arial",10,#bold) perspective: false;
		draw "Agent: " + name at: location + {0,5,0} color: #blue font: font("SansSerif",15,#italic) perspective: true;		
	}

	// When a line is drawn, it could be interesting to also draw an arrow at one of the extremities of the line
	aspect arrows {
		// begin_arrow: and end_arrow: facets are used to define the size of the drawn line.
		// width: facet can be used to increase the width of a line 
		// (the use of width with other geometry will provide unexpected result, as it increase each line of the geometry)
		draw line([self.location, other.location]) color: #orange end_arrow: 1 begin_arrow: 2.2 width: 3.0 ;			
	}

	// at (point): location where the shape/text/icon is drawn
	// rotate (any type in [float, int, pair]): orientation of the shape/text/icon; can be either an int/float (angle) or a pair float::point (angle::rotation axis). The rotation axis, when expressed as an angle, is by defaut {0,0,1}
	// The elements drawn are by default located on the agent location, but they can be moved anywhere in the environment.
	// They can also be rotated.		
	aspect locate_geometry {
		draw square(1.0) color: #silver border: #black at: world.location rotate: 45;
		draw "Agent always displayed in the center" color: #green font: font("Arial",24,#bold) perspective: false at: world.location ;
	}

// draw shape
//	draw "text"
// 	draw gif
}

experiment Aspects type: gui {
	output {
		display d type: opengl {
			// when no aspect: facet is specified, GAMA tries to use the aspect named default. 
			// If no such an aspect exists, it draws the shape of the agent with a color defined in the preferences.
			species people;
			agents "layer simple" value: [people(1)] aspect: simple;
			agents "simple with transparency" value: [people(2)] aspect: big_square_for_transparency transparency: 0.5;		
			
			agents "Arrows" value: [people(3)] aspect: arrows;
			agents "Text" value: [people(4)] aspect: simple_text;
			agents "Text" value: [people(5)] aspect: locate_geometry;			
		}
		display d type: java2D {
			// when no aspect: facet is specified, GAMA tries to use the aspect named default. 
			// If no such an aspect exists, it draws the shape of the agent with a color defined in the preferences.
			species people;
			agents "layer simple" value: [people(1)] aspect: simple;
			agents "simple with transparency" value: [people(2)] aspect: big_square_for_transparency transparency: 0.5;		
			agents "Arrows" value: [people(3)] aspect: arrows;
			agents "Text" value: [people(4)] aspect: simple_text;
			agents "Text" value: [people(5)] aspect: locate_geometry;			
		}		
	}
}



/*

depth (float): (only if the display type is opengl) Add an artificial depth to the geometry previously defined (a line becomes a plan, a circle becomes a cylinder, a square becomes a cube, a polygon becomes a polyhedron with height equal to the depth value). Note: This only works if the geometry is not a point
lighted (boolean): Whether the object should be lighted or not (only applicable in the context of opengl displays)
material (25): Set a particular material to the object (only if you use it in an "opengl2" display).
rounded (boolean): specify whether the geometry have to be rounded (e.g. for squares)
size (any type in [float, point]): Size of the shape/icon/image to draw, expressed as a bounding box (width, height, depth; if expressed as a float, represents the box as a cube). Does not apply to texts: use a font with the required size instead
texture (any type in [string, list, file]): the texture(s) that should be applied to the geometry. Either a path to a file or a list of paths
*/