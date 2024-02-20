/**
* Name: MapQuestImageImport
* Author: Alexis Drogoul
* Description: Demonstrates how to load a (possibly dynamic) image from MapQuest https://developer.mapquest.com/documentation/samples/static-map/v5/map/ and how to refresh it
* Tags: data_loading, displays, user_input, on_change
*/
model MapQuestImageImport

 
global {
	// If you want to use MapQuest data, you need to grab a key from the official MapQuest website:
	// https://developer.mapquest.com/documentation/
	string appkey<-"KEY";
	image_file static_map_request;
	string map_center <- "48.8566140,2.3522219"; 
	int map_zoom <- 8 max: 20 min: 0;
	point map_size <-{600,600};

	action load_map
	{ 
		string zoom_request <- "zoom=" + map_zoom;
		string center_request <- "locations=" + map_center;
		string size_request <- "size=" + int(map_size.x) + "," + int(map_size.y) + "@2x";
		string request <- "https://www.mapquestapi.com/staticmap/v5/map?key="+appkey+"&"+size_request+"&imagetype=jpg&"+zoom_request+"&"+center_request+"&type=hyb";
		write "Request : " + request;
		static_map_request <- image_file(request);
	}
 
	init
	{
		if(appkey = "KEY") {
			map useless <- user_input_dialog("Please enter your MapQuest key in the model code.", []);			
		} else {
			map answers <- user_input_dialog("Center of the map can be a pair lat,lon (e.g; '48.8566140,2.3522219')", [enter("Center",map_center),enter("Zoom x",map_zoom),enter("Size", map_size)]);
		    map_center <- answers["Center"]; 
			map_zoom <- int(answers["Zoom x"]);
			map_size <- point(answers["Size"]);
			
			do load_map;			
		}
	}

}

experiment Display
{
	parameter "Zoom" var: map_zoom  {
		ask simulation  {do load_map;}
		do update_outputs(true);
	}

	output
	{
		
		display "MapQuest" type: 3d axes:false
		{
			graphics toto {
				draw static_map_request;
			}
		}

	}

}

