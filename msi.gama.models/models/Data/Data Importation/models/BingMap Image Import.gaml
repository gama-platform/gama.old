/**
* Name: BingMapImageImport
* Author: Alexis Drogoul
* Description: Demonstrates how to load a image from BingMap https://docs.microsoft.com/en-us/bingmaps/rest-services/imagery/get-a-static-map and how to refresh it
* Tags: data_loading, displays, user_input, on_change
*/
model BingMapImageImport

 
global
{
	image_file static_map_request;
	map
	answers <- user_input("Address can be a pair lat,lon (e.g; '48.8566140,2.3522219')", [enter("Address","")]);
	string center_text <- answers["Address"]; 
	int zoom_text <- 15;
	geometry shape<-square(500);
	action load_map
	{ 
		string zoom <- "zoom=" + zoom_text;
		string center <- "center=" + center_text;
		string rest_link<- "https://dev.virtualearth.net/REST/v1/Imagery/Map/AerialWithLabels/"+center_text+"/"+zoom_text+"?mapSize="+int(world.shape.width)+","+int(world.shape.height)+"&key=AvZ5t7w-HChgI2LOFoy_UF4cf77ypi2ctGYxCgWOLGFwMGIGrsiDpCDCjliUliln";
		write rest_link;
		static_map_request <- image_file(rest_link);
		
		
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
	 
	output
	{
		display "Bing Map" type: opengl
		{
			image static_map_request;
		}

	}

}

