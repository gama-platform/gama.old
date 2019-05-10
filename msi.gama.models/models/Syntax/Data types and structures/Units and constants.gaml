/***
* Name: Units and constants
* Author: Benoit Gaudou
* Description: The model illustrates all the possible constants (including units) existing in  GAML.
* Tags: unit, constant
***/

model UnitsAndConstants

global {
	init {
		// Constants related to errors
		do constant_current_err;
		
		// Mathematical classical constants
		do constant_math;
		
		// Graphical constants
		do constant_graphic;
		
		// Classical unit constants
		do length_surface_units;
	}

	// #current_error contains the last error that have been thrown during the current execution.
	action constant_current_err {
		write "Constant containing the last thrown error";
		write "=========================================";
		try {
			float error <- 1 / 0;
		} catch {
			write "A " + #current_error + " is caught";
		}
		write "";
	}
	
	// Constants include the main mathematical constant (such as pi, e ...)
	action constant_math {
		write "Mathematical constances (#e, #pi, #max_float ...)";
		write "=================================================";		
		// #e constant is characterized by the relation ln(#e) = 1
		write sample(ln(#e));
		
		// Constants includes the min and max numbers for integers and floats
		// #max_int, #min_int are resp. the minimum (resp. maximum) possible values for integer variables.
		// As a consequence, #max_int + 1 is out of the possible values of integers, and thus go back to the maximum negative value.
		write sample(int(#max_int));		
		write sample(int(#max_int) + 1);
		write sample(int(#min_int));
		write sample(int(#min_int) - 1);
		// #max_float is the maximum float value, whereas #min_float is the minimum *positive* value.
		write sample(#max_float);
		write sample(#min_float);
		// #infinity (and #nan) contains the positivie infinity value: so most of the computations involving #infinity return #infinity
		write sample(#infinity * 3);
		write sample(#infinity / - 7);
		// Only few computation involving #infinity do not return #infinity 
		write sample(#infinity / #infinity);
		write sample(0 / #infinity);

		
		// #to_deg and #to_rad can be used to convert angle value between radius to degree: 2 * pi rad = 360 degrees.
		write sample(2 * #pi * #to_deg);
		write sample(180 * #to_rad);
		
	}
	
	action constant_graphic {
		write sample(#bottom_center);
	}
	
	// GAML provides many units. The basic units are meter, gram... 
	// All the other units are converted to the corresponding basic one.
	action length_surface_units {
		write sample(1#m) + "#m";  	// can also be written #meter,#meters
 		write sample(1#km) + "#m";   // can also be written #kilometer,#kilometers
  		write sample(1#dm) + "#m";   // can also be written #decimeter,#decimeters		
  		write sample(1#cm) + "#m";   // can also be written #centimeter,#centimeters	
 		write sample(1#mm) + "#m"; 	// can also be written #milimeter, #milimeters
 		write sample(1#micrometer) + "#m";	// can also be written #micrometers
 		
 		// GAMA also provides non-metrics units such as foot, inch, mile and yard.
 		write sample(1#ft) + "#m";  	// can also be written #foot,#feet
 		write sample(1#inch) + "#m";   // can also be written #inches
  		write sample(1#mile) + "#m";   // can also be written #miles		
  		write sample(1#yard) + "#m";   // can also be written #yards	
 		
		//Length units


//Surface units
//#m2, value= 1.0, Comment: square meter: the basic unit for surfaces
//#sqft (#square_foot,#square_feet), value= 0.09290304, Comment: square foot unit
//#sqin (#square_inch,#square_inches), value= 6.451600000000001E-4, Comment: square inch unit
//#sqmi (#square_mile,#square_miles), value= 2589988.110336, Comment: square mile unit
		
	}

}

//  Create a model about the various constants/units (incl. the ones used in displays)

experiment Strings {
	font my_font <- font("Helvetica", 24, #bold);
	map<string, point>
	anchors <- ["center"::#center, "top_left"::#top_left, "left_center"::#left_center, "bottom_left"::#bottom_left, 
		"bottom_center"::#bottom_center, "bottom_right"::#bottom_right, "right_center"::#right_center, "top_right"::#top_right, 
		"top_center"::#top_center];

	output {
		layout #split;
		display "Strings OpenGL" type: opengl {
			graphics Strings {
				draw world.shape empty: true color: #black;
				int y <- 5;
				loop p over: anchors.pairs {
					draw circle(0.5) at: {50, y} color: #red;
					draw p.key at: {50, y} anchor: p.value color: #black font: my_font;
					y <- y + 5;
				}

				draw circle(0.5) at: {50, y} color: #red;
				draw "custom {0.6, 0.1}" at: {50, y} anchor: {0.6, 0.1} color: #black font: my_font;
				draw circle(0.5) at: {50, y + 5} color: #red;
				draw "custom {0.2, 0.2}" at: {50, y + 5} anchor: {0.2, 0.2} color: #black font: my_font;
				draw circle(0.5) at: {50, y + 10} color: #red;
				draw "custom {0.8, 0.8}" at: {50, y + 10} anchor: {0.8, 0.8} color: #black font: my_font;
			}

		}

		display "Strings Java2D" type: java2D {
			graphics Strings {
				draw world.shape empty: true color: #black;
				int y <- 5;
				loop p over: anchors.pairs {
					draw circle(0.5) at: {50, y} color: #red;
					draw p.key at: {50, y} anchor: p.value color: #black font: my_font;
					y <- y + 5;
				}

				draw circle(0.5) at: {50, y} color: #red;
				draw "custom {0.6, 0.1}" at: {50, y} anchor: {0.6, 0.1} color: #black font: my_font;
				draw circle(0.5) at: {50, y + 5} color: #red;
				draw "custom {0.2, 0.2}" at: {50, y + 5} anchor: {0.2, 0.2} color: #black font: my_font;
				draw circle(0.5) at: {50, y + 10} color: #red;
				draw "custom {0.8, 0.8}" at: {50, y + 10} anchor: {0.8, 0.8} color: #black font: my_font;
			}

		}

	}

}


//Graphics units
//#bold, value= 1, Comment: This constant allows to build a font with a bold face. Can be combined with #italic
//#bottom_center, value= No Default Value, Comment: Represents an anchor situated at the center of the bottom side of the text to draw
//#bottom_left, value= No Default Value, Comment: Represents an anchor situated at the bottom left corner of the text to draw
//#bottom_right, value= No Default Value, Comment: Represents an anchor situated at the bottom right corner of the text to draw
//#camera_location, value= No Default Value, Comment: This unit, only available when running aspects or declaring displays, returns the current position of the camera as a point
//#camera_orientation, value= No Default Value, Comment: This unit, only available when running aspects or declaring displays, returns the current orientation of the camera as a point
//#camera_target, value= No Default Value, Comment: This unit, only available when running aspects or declaring displays, returns the current target of the camera as a point
//#center, value= No Default Value, Comment: Represents an anchor situated at the center of the text to draw
//#display_height, value= 1.0, Comment: This constant is only accessible in a graphical context: display, graphics…
//#display_width, value= 1.0, Comment: This constant is only accessible in a graphical context: display, graphics…
//#flat, value= 2, Comment: This constant represents a flat line buffer end cap style
//#horizontal, value= 3, Comment: This constant represents a layout where all display views are aligned horizontally
//#italic, value= 2, Comment: This constant allows to build a font with an italic face. Can be combined with #bold
//#left_center, value= No Default Value, Comment: Represents an anchor situated at the center of the left side of the text to draw
//#none, value= 0, Comment: This constant represents the absence of a predefined layout
//#pixels (#px), value= 1.0, Comment: This unit, only available when running aspects or declaring displays, returns a dynamic value instead of a fixed one. px (or pixels), returns the value of one pixel on the current view in terms of model units.
//#plain, value= 0, Comment: This constant allows to build a font with a plain face
//#right_center, value= No Default Value, Comment: Represents an anchor situated at the center of the right side of the text to draw
//#round, value= 1, Comment: This constant represents a round line buffer end cap style
//#split, value= 2, Comment: This constant represents a layout where all display views are split in a grid-like structure
//#square, value= 3, Comment: This constant represents a square line buffer end cap style
//#stack, value= 1, Comment: This constant represents a layout where all display views are stacked
//#top_center, value= No Default Value, Comment: Represents an anchor situated at the center of the top side of the text to draw
//#top_left, value= No Default Value, Comment: Represents an anchor situated at the top left corner of the text to draw
//#top_right, value= No Default Value, Comment: Represents an anchor situated at the top right corner of the text to draw
//#user_location, value= No Default Value, Comment: This unit contains in permanence the location of the mouse on the display in which it is situated. The latest location is provided when it is out of a display
//#vertical, value= 4, Comment: This constant represents a layout where all display views are aligned vertically
//#zoom, value= 1.0, Comment: This unit, only available when running aspects or declaring displays, returns the current zoom level of the display as a positive float, where 1.0 represent the neutral zoom (100%)


//Length units
//#Âµm (#micrometer,#micrometers), value= 1.0E-6, Comment: micrometer unit
//#cm (#centimeter,#centimeters), value= 0.01, Comment: centimeter unit
//#dm (#decimeter,#decimeters), value= 0.1, Comment: decimeter unit
//#foot (#feet,#ft), value= 0.3048, Comment: foot unit
//#inch (#inches), value= 0.025400000000000002, Comment: inch unit
//#km (#kilometer,#kilometers), value= 1000.0, Comment: kilometer unit
//#m (#meter,#meters), value= 1.0, Comment: meter: the length basic unit
//#mile (#miles), value= 1609.344, Comment: mile unit
//#mm (#milimeter,#milimeters), value= 0.001, Comment: millimeter unit
//#nm (#nanometer,#nanometers), value= 9.999999999999999E-10, Comment: nanometer unit
//#yard (#yards), value= 0.9144, Comment: yard unit

//Surface units
//#m2, value= 1.0, Comment: square meter: the basic unit for surfaces
//#sqft (#square_foot,#square_feet), value= 0.09290304, Comment: square foot unit
//#sqin (#square_inch,#square_inches), value= 6.451600000000001E-4, Comment: square inch unit
//#sqmi (#square_mile,#square_miles), value= 2589988.110336, Comment: square mile unit

//Time units
//#custom, value= CUSTOM, Comment: custom: a custom date/time pattern that can be defined in the preferences of GAMA and reused in models
//#cycle (#cycles), value= 1, Comment: cycle: the discrete measure of time in the simulation. Used to force a temporal expression to be expressed in terms of cycles rather than seconds
//#day (#days), value= 86400.0, Comment: day time unit: defines an exact duration of 24 hours
//#epoch, value= No Default Value, Comment: The epoch default starting date as defined by the ISO format (1970-01-01T00:00Z)
//#h (#hour,#hours), value= 3600.0, Comment: hour time unit: defines an exact duration of 60 minutes
//#iso_local, value= ISO_LOCAL_DATE_TIME, Comment: iso_local: the standard ISO 8601 output / parsing format for local dates (i.e. with no time-zone information)
//#iso_offset, value= ISO_OFFSET_DATE_TIME, Comment: iso_offset: the standard ISO 8601 output / parsing format for dates with a time offset
//#iso_zoned, value= ISO_ZONED_DATE_TIME, Comment: iso_zoned: the standard ISO 8601 output / parsing format for dates with a time zone
//#minute (#minutes,#mn), value= 60.0, Comment: minute time unit: defined an exact duration of 60 seconds
//#month (#months), value= 2592000.0, Comment: month time unit: defines an exact duration of 30 days. WARNING: this duration is of course not correct in terms of calendar
//#msec (#millisecond,#milliseconds,#ms), value= 0.001, Comment: millisecond time unit: defines an exact duration of 0.001 second
//#now, value= 1.0, Comment: This value represents the current date
//#sec (#second,#seconds,#s), value= 1.0, Comment: second: the time basic unit, with a fixed value of 1. All other durations are expressed with respect to it
//#week (#weeks), value= 604800.0, Comment: week time unit: defines an exact duration of 7 days
//#year (#years,#y), value= 3.1536E7, Comment: year time unit: defines an exact duration of 365 days. WARNING: this duration is of course not correct in terms of calendar
//
//Volume units
//#cl (#centiliter,#centiliters), value= 1.0E-5, Comment: centiliter unit
//#dl (#deciliter,#deciliters), value= 1.0E-4, Comment: deciliter unit
//#hl (#hectoliter,#hectoliters), value= 0.1, Comment: hectoliter unit
//#l (#liter,#liters,#dm3), value= 0.001, Comment: liter unit
//#m3, value= 1.0, Comment: cube meter: the basic unit for volumes
//
//Weight units
//#gram (#grams), value= 0.001, Comment: gram unit
//#kg (#kilo,#kilogram,#kilos), value= 1.0, Comment: second: the basic unit for weights
//#longton (#lton), value= 1016.0469088000001, Comment: short ton unit
//#ounce (#oz,#ounces), value= 0.028349523125, Comment: ounce unit
//#pound (#lb,#pounds,#lbm), value= 0.45359237, Comment: pound unit
//#shortton (#ston), value= 907.18474, Comment: short ton unit
//#stone (#st), value= 6.35029318, Comment: stone unit
//#ton (#tons), value= 1000.0, Comment: ton unit

//Colors
//In addition to the previous units, GAML provides a direct access to the 147 named colors defined in CSS (see http://www.cssportal.com/css3-color-names/). E.g,
//
//rgb my_color <- °teal;
//#aliceblue, value= r=240, g=248, b=255, alpha=1
//#antiquewhite, value= r=250, g=235, b=215, alpha=1
//#aqua, value= r=0, g=255, b=255, alpha=1
//#aquamarine, value= r=127, g=255, b=212, alpha=1
//#azure, value= r=240, g=255, b=255, alpha=1
//#beige, value= r=245, g=245, b=220, alpha=1