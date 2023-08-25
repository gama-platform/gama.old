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
		write "";
		
		// Mathematical classical constants
		do constant_math;
		write "";
		
		// Classical unit constants
		do length_surface_time_units;
		write "";
		
		// Time constants
		do time_units;		
		
		// The experiment allows to illustrate all the constant related to graphical aspects.
	}

	// #current_error contains the last error that have been thrown during the current execution.
	action constant_current_err {
		write "Constant related to system state (e.g. the last thrown error)";
		write "=============================================================";
		try {
			float error <- 1 / 0;
		} catch {
			write "A " + #current_error + " is caught";
		}
		write "";
	}
	
	// Constants include the main mathematical constants (such as pi, e ...)
	action constant_math {
		write "Mathematical constants (#e, #pi, #max_float ...)";
		write "=================================================";		
		// #e constant is characterized by the relation ln(#e) = 1
		write sample(ln(#e));
		
		// Constants include the min and max numbers for integers and floats
		// #max_int (resp.  #min_int) are the minimum (resp. maximum) possible values for integer variables.
		// As a consequence, #max_int + 1 is out of the possible values of integers, and thus returns the maximum negative value.
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
		// Only few computations involving #infinity do not return #infinity 
		write sample(#infinity / #infinity);
		write sample(0 / #infinity);
		
		// #to_deg and #to_rad can be used to convert angle value between radius to degree: 2 * pi rad = 360 degrees.
		write sample(2 * #pi * #to_deg);
		write sample(180 * #to_rad);
		
	}
	
	// GAML provides many units. The basic units are meter, kilogram, m2, m3 and second. 
	// All the other units are converted to the corresponding basic one.
	action length_surface_time_units {
		write "Units-related constants (meter, m2, m3, kg and second)";
		write "=================================================";		
		write " *** The basic units are: ***";			
		write sample(1#m)  + "(#m, for length)";  	// can also be written #meter, #meters
 		write sample(1#m2) + "(#m2, for surface)";  	
 		write sample(1#m3) + "(#m3, for volume)";
		write sample(1#kg) + "(#kg, for weight)"; 	// can also be written #kilo, #kilogram, #kilos
		write sample(1#s)  + "(#s, for time)";  	// can also be written #sec, #second, #seconds,
		
		write " *** Metrics length units: ***";			
 		write sample(1#km) + " (#m)";   // can also be written #kilometer, #kilometers
  		write sample(1#dm) + " (#m)";   // can also be written #decimeter, #decimeters		
  		write sample(1#cm) + " (#m)";   // can also be written #centimeter, #centimeters	
 		write sample(1#mm) + " (#m)";	// can also be written #milimeter, #milimeters
 		write sample(1#micrometer) + "#m";	// can also be written #micrometers
 		
 		// GAMA also provides non-metrics units such as foot, inch, mile and yard.
		write " *** Non-Metrics length units: ***";			
 		write sample(1#ft) + " (#m)";  	// can also be written #foot, #feet
 		write sample(1#inch) + " (#m)";   // can also be written #inches
  		write sample(1#mile) + " (#m)";  // can also be written #miles		
  		write sample(1#yard) + " (#m)";  // can also be written #yards	
 		
		//Surface units
		write " *** Surface units: ***";					
 		write sample(1#sqft) + " (#m2)";   // can also be written #square_foot, #square_feet
  		write sample(1#sqin) + " (#m2)";   // can also be written #square_inch, #square_inches	
  		write sample(1#sqmi) + " (#m2)";
  		
  		// Volume units 
		write " *** Volume units: ***";					
 		write sample(1#cl) + " (#m3)";   // can also be written #centiliter, #centiliters
  		write sample(1#dl) + " (#m3)";   // can also be written #deciliter, #deciliters	
  		write sample(1#hl) + " (#m3)";   // can also be written #hectoliter, #hectoliters	
   		write sample(1#l)  + " (#m3)";   // can also be written #liter, #liters, #dm3	 		
  			
		//Weight units
		write " *** Weight units: ***";					
 		write sample(1#gram) + " (#kg)";   // can also be written #gram
  		write sample(1#ton) + " (#kg)";   // can also be written #tons
  		write sample(1#lton) + " (#kg)";   // can also be written #longton	
  		write sample(1#ounce) + " (#kg)";   // can also be written #oz, #ounces	
   		write sample(1#pound)  + " (#kg)";   // can also be written #lb,#pounds,#lbm		
  		write sample(1#shortton) + " (#kg)";   // can also be written #ston	
  		write sample(1#stone) + " (#kg)";   // can also be written #st

		//Time units
		write " *** Time units: ***";							
		write sample(1#ms) + " (#s)";   // can also be written #millisecond, #milliseconds, #msec		
 		write sample(1#mn) + " (#s)";   // can also be written #minute, #minutes
 		write sample(1#h) + " (#s)";   // can also be written #hour, #hours
		write sample(1#day) + " (#s)";   // can also be written #days
		write sample(1#week) + " (#s)";   // can also be written #weeks
		
		// Time not correct units (in the sense that they are ambiguous concept in terms of duration)
		write "1 #month :-" + #month + " (#s - NOTE: this is the duration of 30 days. This is an ambiguous duration in natural language.)";   // can also be written #months
		write "1 #year  :-" + #year + "(#s - NOTE: this is the duration of 365 days. This is an ambiguous duration in natural language.)";   // can also be written #years, #y
		
	}
  	
  	action 	time_units {
 		write "Time-related constants ";		
		write "=================================================";		
 		write " *** Additional constants related to time: ***";			
 		write sample(#now) + " is the current date.";  
  		write sample(#cycle) + " corresponds to 1 cycle";  
		write "This constant is used to force a temporal expression to be expressed in terms of cycles rather than seconds";
    	write sample(#custom) + " : is the custom date/time defined in the preferences of GAMA.";  
  	   	write sample(#epoch) + " : is the default starting date (defined by the ISO format (1970-01-01T00:00Z))."; 
  	   	
  	   	write "The 3 following constants can be used as output/parsingformat for local dates ("+#iso_local+"), dates with a time offset ("+#iso_offset+") ";
  	   	write "and dates with time zone ("+#iso_zoned+").";  	
	}

	

}


	
// Constants include many graphical constants (that can be used only in displays or to define some graphical objects such as font ...).
// In addition to the previous units, GAML provides a direct access to the 147 named colors defined in CSS (see http://www.cssportal.com/css3-color-names/). 
// E.g, #teal, #aliceblue, antiquewhite .... (Model menu in GAMA lists all of them). 
experiment exp {
	
	// #bold, #italic and #plan can be used to defin the font style (#bbold and #italic can be combined).
	font my_font_bold <- font("Helvetica", 12, #bold);
	font my_font_italic <- font("Arial", 12, #italic);
	font my_font_bold_italic <- font("SansSerif", 12, #bold + #italic);
	font my_font_plain <- font("Helvetica", 12, #plain);
	
	// Anchor constants are used to locate text to display in a text area.
	map<string, point> anchors <- ["center"::#center, "top_left"::#top_left, "left_center"::#left_center, "bottom_left"::#bottom_left, 
		"bottom_center"::#bottom_center, "bottom_right"::#bottom_right, "right_center"::#right_center, "top_right"::#top_right, 
		"top_center"::#top_center];

	output {
		// GAML provides several ways to split the various displays (at the launch of the simulation).
		// Each of them can be set using the one of the following constants:
		// #horizontal, #vertical, #stack, #split (displays split in a grid-like structure) or #none (no split).	
		layout #split;
		display "Strings 2D" type: 2d {
			graphics Strings {
				draw world.shape wireframe: true color: #black;
				int y <- 7;
				
				// The loop displays for each anchor an associated text
				loop p over: anchors.pairs {
					draw circle(0.5) at: {50, y} color: #red;
					draw p.key at: {50, y} anchor: p.value color: #black font: my_font_bold_italic;
					y <- y + 7;
				}

				// These anchors can also been defined at hand using a point
				draw circle(0.5) at: {50, y} color: #red;
				draw "custom {0.6, 0.1}" at: {50, y} anchor: {0.6, 0.1} color: #black font: my_font_italic;
			}

		}

		display "Strings 3D" type: 3d {
			// #pixels (or #px) corresponds to the value of one pixel, depending on the display, zoom...
			// So pixel is used to define the dimension of an overlay in order to keep it size constant.
            overlay position: { 0, 0 } size: { 300 #pixels, 200 #px } background: #grey transparency: 0.2 border: #black rounded: true {
            	// Constant contain useful information about the way of vizualising the simulation with: the camera location (#camera_location), 
            	// orientation (#camera_orientation) and target (#camera_target). 
                draw "Camera location:  " + string(#camera_location with_precision 3) at:{10#px,20#px,0#px} color: #black font: my_font_plain;
				draw "Camera orientation:  " + string(#camera_orientation with_precision 3) at:{10#px,40#px,0} color: #black font: my_font_plain;
				draw "Zoom level:  " + string(#zoom) at:{10#px,60#px,0} color: #black font: my_font_plain;	
				// #display_height and #display_width contain the size in pixel of the simulation environment.
				// It obviously depends on the zoom level.		
				draw "Display height:  " + string(#display_height) at:{10#px,80#px,0} color: #black font: my_font_plain;				  		
				draw "Display width:  " + string(#display_width) at:{10#px,100#px,0} color: #black font: my_font_plain;				  		  
			}
					
			// We can access location of the mouse in the display using #mouse_location.
			graphics circle_mouse {	
				// We can also access the #zoom level: we can thus display a visual element depending on the zoom level, to keep visible some 
				// elements even when we zoom out.
				// We display a sphere at the location of the mouse (with a size depending on the zoom).
				draw sphere(1.0/#zoom) color: #green at: #user_location;
				draw "       " +string(#user_location with_precision 3)  at: #user_location color: #black font:font("Helveetica", max(1,14/#zoom), #plain);

				// We can also visualize the target point of the camera.
				draw sphere(1.0/#zoom ) at: #camera_target color: #red ;
				draw "       " +string(#camera_target with_precision 3)   
					at:#camera_target color: #black font:font("Helveetica", max(1,14/#zoom), #plain);//anchor: #bottom_center;
					
				// A buffer extends a line as a geometry. This buffer can be set #square, #round or #flat
				// Note that #flat and #square are both a buffer with a square shape, but the flat stop the rectangle at the limit of the line. 
				draw line([#user_location + {1.0/#zoom ,0,0}, #user_location - {1.0/#zoom ,0,0}]) buffer(0.5/#zoom ,0.5/#zoom ,#flat)  at:#user_location color: #black ;
				draw line([#user_location + {0,1.0/#zoom ,0}, #user_location - {0,1.0/#zoom ,0}]) buffer(0.5/#zoom ,0.5/#zoom ,#square) at:#user_location color: #black ;					
				draw line([#user_location + {1.0/#zoom ,0,0}, #user_location - {1.0/#zoom ,0,0}]) at:#user_location color: #red ;
				draw line([#user_location + {0,1.0/#zoom ,0}, #user_location - {0,1.0/#zoom ,0}]) at:#user_location color: #red ;						
			}
			event #mouse_move {			
				do update_outputs;		
			}	
		}
	}
}