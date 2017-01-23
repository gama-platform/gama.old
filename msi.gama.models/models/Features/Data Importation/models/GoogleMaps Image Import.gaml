/**
* Name: GoogleMapsImageImport
* Author: Alexis Drogoul
* Description: Demonstrates how to load a (possibly dynamic) image from Google Maps and how to refresh it
* Tags: data_loading, displays, user_input, on_change
*/
model GoogleMapsImageImport

 
global
{
	image_file google_request;
	map
	answers <- user_input("Address can be a complete address (e.g. 'Paris,France') or a pair lat,lon (e.g; '48.8566140,2.3522219')", ["Address"::""]);
	string center_text <- answers["Address"];
	bool visib_flag <- false;
	int zoom_text <- 16;
	action load_map
	{
		string visibility <- "visibility:" + (visib_flag ? "on" : "off");
		string zoom <- "zoom=" + zoom_text;
		string center <- "center=" + center_text;
		google_request <-
		image_file("http://maps.google.com/maps/api/staticmap?" + center + "&" + zoom + "&size=400x400&maptype=roadmap&style=feature:all%7Celement:labels%7C" + visibility);
	}
 
	init
	{
		do load_map;
	}

}

experiment Display
{
	
	parameter "Zoom" var: zoom_text on_change: {
		ask simulation  {do load_map;}
		do update_outputs(true);
	};
	
	parameter "Labels" var: visib_flag on_change: {
			ask simulation  {do load_map;}
			do update_outputs(true);
	};
	output
	{
		display "Google Map" type: opengl
		{
			image google_request;
		}

	}

}

