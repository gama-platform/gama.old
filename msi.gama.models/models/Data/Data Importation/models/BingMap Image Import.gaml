/**
* Name: BingMapImageImport
* Author: Alexis Drogoul
* Description: Demonstrates how to load a image from BingMap https://docs.microsoft.com/en-us/bingmaps/rest-services/imagery/get-a-static-map and how to refresh it
* Tags: data_loading, displays, user_input, on_change
*/
@no_warning
model BingMapImageImport



global
{
	
	user_command "Change destination" action: load_map;
	
	image_file static_map_request;
	geometry shape<-square(500);
	
	
	string zoom_title <- "Zoom (between 1 and 20)";
	string lat_lon_title <- "Latitude and longitude";
	
	int current_zoom <- 15;
	string current_lat_lon <- "48.8566140,2.3522219";
	
	action load_map
	{ 
		map	answers <- user_input_dialog("Address can be a pair lat,lon (e.g; '48.8566140,2.3522219')", [enter(lat_lon_title,current_lat_lon),enter(zoom_title,current_zoom)]);
		current_zoom <- max(min(20,int(answers[zoom_title])),1);
		current_lat_lon <- answers[lat_lon_title]; 
		string rest_link<- "https://dev.virtualearth.net/REST/v1/Imagery/Map/AerialWithLabels/"+current_lat_lon+"/"+current_zoom+"?mapSize="+int(world.shape.width)+","+int(world.shape.height)+"&key=AvZ5t7w-HChgI2LOFoy_UF4cf77ypi2ctGYxCgWOLGFwMGIGrsiDpCDCjliUliln";
		write rest_link;
		static_map_request <- image_file(rest_link);
		ask experiment {do update_outputs(true);}
	}
 
	init
	{
		do load_map;
	}

}

experiment Display
{
	user_command "Change destination" category: "Bing service" {ask simulation {do load_map;}}

	 
	output
	{
		display "Bing Map" type: 3d axes:false
		{
			image static_map_request refresh:true;
		}

	}

}

