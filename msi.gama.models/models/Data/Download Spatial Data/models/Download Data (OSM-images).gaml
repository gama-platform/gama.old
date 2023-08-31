/**
* Name: generate_environment
* Author: Patrick Taillandier
* Description: Demonstrates how to import data from OSM, Bing and google map to generate geographical data. More precisely, the model allows from a shapefile giving the area of the study area to download all the OSM data on this area, to vectorize the buildings and the points of interest from google map data and to download a Bing satellite image of the area.
* Tags: data_loading, OSM, Google Map, Bing, shapefile
*/
model download_spatial_data

global {

/* ------------------------------------------------------------------ 
	 * 
	 *             MANDATORY PARAMETERS
	 * 
	 * ------------------------------------------------------------------
	 */

	//define the bounds of the studied area
	file data_file <-shape_file("../includes/boundary.shp");
	
	//path where to export the created shapefiles
	string exporting_path <- "results/";
	
	//if true, GAMA is going to use OSM data to create the building file
	bool use_OSM_data <- true;
	
	//if true, GAMA is going to use google map image to create the building file
	bool use_google_map_data <- true;
	
	//if true, GAMA is going to download the background satellite image (Bing image).
	bool do_load_satellite_image <- true;
	
	//image to display as background if there is no satellite image
	string default_background_image <- "../includes/white.png";
	
	/* ------------------------------------------------------------------ 
	 * 
	 *             OPTIONAL PARAMETERS
	 * 
	 * ------------------------------------------------------------------
	 */
	// --------------- OSM data parameters ------------------------------
	//path to an existing Open Street Map file - if not specified, GAMA is going to directly download to correct data
	string osm_file_path <- "../includes/map.osm";
	
	//type of feature considered
	map osm_data_to_generate <- ["building"::[], "shop"::[], "historic"::[], "amenity"::[], "sport"::[], "military"::[], "leisure"::[], "office"::[],  "highway"::[], "water"::[], "natural"::[], "landuse"::[]];
	

	// --------------- google image parameters ------------------------------
	//path to an existing google map image - if not speciefied, GAMA can try to download the correct image - WARNING: can be blocked by google
	string googlemap_path <-  "../includes/googlemap.png";
	
	//possibles colors for buildings
	list<rgb> color_bds <- [rgb(241,243,244), rgb(255,250,241)];
	
	//type of markers considered with their associated color
	map<string,list<rgb>> google_map_type <- ["restaurant"::[rgb(255,159,104)], "shop"::[rgb(73,149,244),rgb(238,103,92)], "culture"::[rgb(18,181,203)], "nature"::[rgb(52,168,83)]];
	
	//number of pixels per tile
	int TILE_SIZE <- 256;
	
	//when downloading google images, the level of zoom
	int zoom <- 18 min: 17 max: 20;

	//min area to consider a building (in m^2)
	float min_area_buildings <- 20.0 min: 0.0;
		
	//simplification distance for building (using Douglas Peucker algorithm)
	float simplication_dist <- 1.0 min: 0.0;
	
	//tolerance (distance in meters) for the union of "building" pixels 
	float tolerance_dist <- 0.2 min: 0.0;
	
	//tolerance for the color of building (for a pixel to be considered as a building pixel)
	int tolerance_color_bd <- 1 min: 0 max: 10;
	
	//tolerance for the color of markers (for a pixel to be considered as a marker pixel)
	int tolerance_color_type <- 7 min: 0 max: 20;
	
	//coefficient (area of the building/area of the convex hull of the building) to keep the convex hull of the building rather than its shape (if convex_hull_coeff = 0.0, the convex hull is never used)
	float convex_hull_coeff <- 0.05 min: 0.0 max: 1.0;
	
	//coeffient used to apply a buffer to the building (distance = buffer_coeff * width of a pixel).
	float buffer_coeff <- 0.5 min: 0.0;
	
	//parallel computation
	bool parallel <- true;
	
	/* ------------------------------------------------------------------ 
	 * 
	 *              DYNAMIC VARIABLES
	 * 
	 * ------------------------------------------------------------------
	 */

	//geometry of the bounds
	geometry bounds_tile;
	
	//index used to read google map tiles
	int ind <- 0;
	
	//list of google map tile with their associated metadata
	map<string, map<string,int>> data_google; 
	
	//
	list<geometry> building_google; 
	
	//when using a google map image, the nomber of pixel of this image
	int nb_pixels_x <- (use_google_map_data and file_exists(googlemap_path)) ? matrix(image_file(googlemap_path)).columns :1;
	int nb_pixels_y <- (use_google_map_data and file_exists(googlemap_path)) ? matrix(image_file(googlemap_path)).rows :1;
	
	//geometry of the world
	geometry shape <- envelope(data_file);
	
	
	init {
		write "Start the pre-processing process";
		create Boundary from: data_file;
		
		if use_OSM_data {
			osm_file osmfile;
			if (file_exists(osm_file_path)) {
				osmfile  <- osm_file(osm_file_path, osm_data_to_generate);
			} else {
				point top_left <- CRS_transform({0,0}, "EPSG:4326").location;
				point bottom_right <- CRS_transform({shape.width, shape.height}, "EPSG:4326").location;
				string adress <-"http://overpass.openstreetmap.ru/cgi/xapi_meta?*[bbox="+top_left.x+"," + bottom_right.y + ","+ bottom_right.x + "," + top_left.y+"]";
				write "adress: " + adress;
				osmfile <- osm_file<geometry> (adress, osm_data_to_generate);
			}
			
			write "OSM data retrieved";
			create OSM_agent from: osmfile  where (each != nil);
			loop type over: osm_data_to_generate.keys {
		 		rgb col <- rnd_color(255);
		 		list<OSM_agent> ags <-  OSM_agent where (each.shape.attributes[type] != nil);
		 		ask ags {color <- col;}
		 		list<OSM_agent> pts <- ags where (each.shape.perimeter = 0);
		 		do save_data(pts,type,"point");
		 		
		 		list<OSM_agent> lines <- ags where ((each.shape.perimeter > 0) and (each.shape.area = 0)) ;
		 		do save_data(lines,type,"line");
		 		
		 		list<OSM_agent> polys <- ags where (each.shape.area > 0);
		 		do save_data(polys,type,"polygon");
		 	}
		}	 	
	 	if (do_load_satellite_image) {
	 		do load_satellite_image;
	 	}
	 	//load google map image (if necessary)
	 	if (use_google_map_data) {
	 		//if the image already exists, just load this image and vectorize it
			if (file_exists(googlemap_path)) {
				do load_google_image;
			} else {
				//otherwise propose to download the image from google (WARNING: direct access to google map image without using the google api (and key) is recommended).
				map input_values <- user_input_dialog("Do you want to download google maps to fill in the data? (warning: risk of being blocked by google!)",[enter("Download data",false), enter("Delay (in s) between two requests",5.0)]);
				experiment.minimum_cycle_duration <- max(0.5, float(input_values["Delay (in s) between two requests"]));
	
				//if the user choose to download the data anyway, build and store the url to the needed tiles.
				if bool(input_values["Download data"]) {
					point bottom_right <- CRS_transform({shape.width, shape.height}, "EPSG:4326").location;
					point top_left <- bottom_right - (bottom_right - CRS_transform(location, "EPSG:4326").location) * 2;
					list<int> indtl <- index_tile(top_left);
					list<int> indbr <- index_tile(bottom_right);
					
					int resolution_x <- abs(indbr[2] - indtl[2])  ;
					int resolution_y <- abs(indbr[3] - indtl[3]);	
					int id_x <- 0;
					int id_y <- 0;
					int offset_x <- min(indbr[0],indtl[0]);
					int offset_y <- min(indbr[1],indtl[1]);
					loop ind_tile_x from: 0 to: abs(indbr[0] - indtl[0])  {
						loop ind_tile_y from: 0 to:abs(indtl[1] - indbr[1]) {
							string img <- "http://mt2.google.com/vt/lyrs=m&x=" +(ind_tile_x + offset_x)+"&y="+ (ind_tile_y  + offset_y)+"&z="+zoom;
							data_google[img] <- ["ind_tile_x":: (ind_tile_x + offset_x) ,  "ind_tile_y"::(ind_tile_y + offset_y)];
						}
					}
				}
			}
		}
	}
	
	
	
	bool acceptable_color(rgb current_col, rgb ref_col) {
		return ((abs(current_col.red - ref_col.red)+abs(current_col.green - ref_col.green) + abs(current_col.blue - ref_col.blue)) < tolerance_color_bd);
	}
	
	list<geometry> keep_cell(list<geometry> rectangles, list<rgb> colors, list<rgb> color_ref) {
		list<geometry> cells_to_keep;
		loop i from: 0 to: length(rectangles) - 1 {
			geometry r <- rectangles[i];
			rgb col_r <- colors[i];
			loop col over: color_ref {
				if acceptable_color(col_r,col) { 
					cells_to_keep << r;
					break;
				}
			}
		}
		return cells_to_keep;
	}
	
	list<geometry> generate_geoms(list<geometry> cells) {
		//if this list is not empty, recompute the geometry of each element
		geometry geom <- union(cells collect (each + tolerance_dist));
			
		list<geometry> output_el <- geom.geometries collect clean(each);
			
		//keep only elements inside the boundary
		output_el <- output_el where (not empty(Boundary overlapping each));
						
		//apply a buffer to the element to take into account the imperfection of the vectorization
		if (buffer_coeff > 0) {
			float buffer_dist <- first(cells).width * buffer_coeff;
			output_el <- output_el collect (each + buffer_dist);
		}
		return output_el;
	}
	
	list<geometry> define_building_from_image(list<geometry> rectangles, list<rgb> colors) {
		list<geometry> building ;
		
		//select the building pixel
		list<geometry> cells_building <- keep_cell(rectangles, colors, color_bds);
		
		if (not empty(cells_building)) {
			
			building <- generate_geoms(cells_building);
			
			//simplify the geometry of the building to remove some vectorization acrtifact
			if simplication_dist > 0 {
				building <- building collect (each simplification simplication_dist);
			}
			
			//use the convex hull for building that are nearly convex
			if (convex_hull_coeff > 0.0) {
				list<geometry> gs2;
				loop g over: building {
					geometry ch <- convex_hull(g);
					if (g.area/ch.area > (1 - convex_hull_coeff)) {
						gs2 << ch;
					} else {
						gs2 << g;
					}
				}
				building <- gs2;
			}
			//remove building that are too small
			building <- building where (each.area >= min_area_buildings);
		}		
		
		
		//for each type of marker, create the marker agents from the google image and use to it to give a type to the closest building (of the bottom of the marker)
		loop type over: google_map_type.keys {
			list<rgb> col <- google_map_type[type];
			
			//select the pixel of the given color 
			list<geometry> cells_type <- keep_cell(rectangles, colors, col);
			
							
			if not empty(cells_type) {
				//and build geometries from them
				list<geometry> geom_markers <- generate_geoms(cells_type);
							
				//create the marker agents
				create marker from: geom_markers with: [type::type];
				float min_area <- marker mean_of each.shape.area;
								
				ask marker {	
					//keep only the marker that are not too small (to take into account only "complete" markers)
					if (shape.area < (min_area * 0.5)) {do die;}
					else {
						color <- (type in google_map_type.keys) ? first(google_map_type[type]) : rnd_color(255);
					}
				}
			}
		}
		return building;
	}
	

	
	//reflex used to download a google map tile and to vectorize it
	reflex vectorization {
		if (ind < length(data_google)) {
			bool continue <- true;
			//continue until downloading an image located in the boundary
			loop while: continue and (ind < length(data_google)) { 
				//verify that the tile is really inside the boundary
				list<rgb> colors;
				map<string, int> infos <- data_google[data_google.keys[ind]];
				int tx <- infos["ind_tile_x"];
				int ty <- infos["ind_tile_y"];
				point sw <- toMeter(tx*TILE_SIZE, ty*TILE_SIZE);
				point ne <- toMeter((tx+1)*TILE_SIZE, (ty+1)*TILE_SIZE);
				sw <- to_GAMA_CRS(sw, "EPSG:3857").location;
				ne <- to_GAMA_CRS(ne, "EPSG:3857").location;
				
				//build the bounds of the tile
				bounds_tile <- polygon({sw.x,sw.y}, {sw.x,ne.y}, {ne.x,ne.y}, {ne.x,sw.y});
				
				//if the tile really overlaps the boundary
				if not empty(Boundary overlapping bounds_tile) {
					continue <- false;
					// download the google map tile
					string path_ <-  data_google.keys[ind];
					image_file img<- image_file(path_,"png");
					//transform each pixel into a rectangle geometry
					list<geometry> rectangles <- bounds_tile to_rectangles(TILE_SIZE,TILE_SIZE);
					loop i from: 0 to: length(rectangles) - 1 {
						colors << rgb(img.contents at {int(i/TILE_SIZE),i mod TILE_SIZE});
					}
					building_google <- building_google + define_building_from_image(rectangles, colors);
				}
				ind <- ind + 1; 
			}
		} else {
			//at the end, save the building
			if (not empty(building_google)) {
				save building_google crs:"EPSG:3857" format: "shp" to:exporting_path +"google_map_building.shp";
			}
			if (not empty(marker)) {
				save marker format: "shp"   crs:"EPSG:3857" to: exporting_path + "google_map_markers.shp" attributes:["type"];
			}
			do pause;
		}
		
		 
	}
	
	//a function used to compute the coordinate when retrieving google map images
	point toMeter(int px, int py) {
		float res <- (2 * #pi * 6378137 / TILE_SIZE) / (2^zoom);
		float originShift <- 2 * #pi * 6378137 / 2.0;
		return { px * res - originShift,  - py * res + originShift};
	} 
	
	//a function to compute the google map tile from coordinate
	list<int> index_tile(point coord) {
		point worldCoordinate <- project_to_wp({coord.x,coord.y});
		float scale <- 2^zoom;
		
		int pix <- int(worldCoordinate.x * scale);
		int piy <- int(worldCoordinate.y * scale);
		int ind_x <- int(worldCoordinate.x * scale / TILE_SIZE);
		int ind_y <- int(worldCoordinate.y * scale / TILE_SIZE);
		return [ind_x,ind_y,pix,piy];
	}
	
	
	//a function to compute the google map coordinate from WGS84 coordinate
	point project_to_wp(point latLng) {
		float siny <- sin_rad(latLng.y * #pi / 180);
		siny <- min(max(siny, -0.9999), 0.9999);
        return {TILE_SIZE * (0.5 + latLng.x / 360),TILE_SIZE * (0.5 - ln((1 + siny) / (1 - siny)) / (4 * #pi))};
    }
	
	
	action save_data(list<OSM_agent> ags, string type, string geom_type) {
		if (not empty(ags)) {
	 		list<string> atts <-  remove_duplicates(ags accumulate each.shape.attributes.keys);
	 		save (ags collect each.shape) format: "shp" to: exporting_path + type + "_" + geom_type+".shp" attributes: atts;
	 	}
	}
	
	action save_image (string rest_link) {
		matrix mat <- (image_file(rest_link).contents);
		write "Satellite image retrieved";
		save mat to: exporting_path +"satellite.png"; 
	}
	
	action save_meta_data (string rest_link) {
		list<string> v <- string(json_file(rest_link).contents) split_with ",";
		write "Satellite image retrieved";
		int id <- 0;
		loop i from: 0 to: length(v) - 1 {
			if ("bbox" in v[i]) { 
				id <- i;
				break;
			}
		} 
		float long_min <- float(v[id] replace ("'bbox'::[",""));
		float long_max <- float(v[id+2] replace (" ",""));
		float lat_min <- float(v[id + 1] replace (" ",""));
		float lat_max <- float(v[id +3] replace ("]",""));
		point pt1 <- CRS_transform({lat_min,long_max},"EPSG:4326", "EPSG:3857").location ;
		point pt2 <- CRS_transform({lat_max,long_min},"EPSG:4326","EPSG:3857").location;
		float width <- abs(pt1.x - pt2.x)/1500;
		float height <- (pt2.y - pt1.y)/1500;
			
		string info <- ""  + width +"\n0.0\n0.0\n"+height+"\n"+min(pt1.x,pt2.x)+"\n"+(height < 0 ? max(pt1.y,pt2.y) : min(pt1.y,pt2.y));
	
		save info to: exporting_path +"satellite.pgw" format:"text";
	}
	
	action load_satellite_image
	{ 
		point top_left <- CRS_transform({0,0}, "EPSG:4326").location;
		point bottom_right <- CRS_transform({shape.width, shape.height}, "EPSG:4326").location;
		int size_x <- 1500;
		int size_y <- 1500;
		
		string rest_link<- "https://dev.virtualearth.net/REST/v1/Imagery/Map/Aerial/?mapArea="+bottom_right.y+"," + top_left.x + ","+ top_left.y + "," + bottom_right.x + "&mapSize="+int(size_x)+","+int(size_y)+ "&key=AvZ5t7w-HChgI2LOFoy_UF4cf77ypi2ctGYxCgWOLGFwMGIGrsiDpCDCjliUliln" ;
		do save_image(rest_link);
		float ct <- machine_time + 2000;
		loop while: machine_time < ct {
			
		}
		string rest_link2<- "https://dev.virtualearth.net/REST/v1/Imagery/Map/Aerial/?mapArea="+bottom_right.y+"," + top_left.x + ","+ top_left.y + "," + bottom_right.x + "&mmd=1&mapSize="+int(size_x)+","+int(size_y)+ "&key=AvZ5t7w-HChgI2LOFoy_UF4cf77ypi2ctGYxCgWOLGFwMGIGrsiDpCDCjliUliln" ;
		do save_meta_data(rest_link2);
		
		write "Satellite image saved with the right meta-data";
		 
		 
	}


//action for vectorizing an existing google image
	action load_google_image {
		image_file im <- image_file(googlemap_path);
		ask cell_google {		
			color <-rgb( (im) at {grid_x ,grid_y }) ;
		}
			
		building_google <- building_google + define_building_from_image(cell_google collect each.shape, cell_google collect each.color);
					
		if (not empty(building_google)) {
			save building_google crs:"EPSG:3857" format: "shp" to:exporting_path +"google_map_building.shp";
		}
		if (not empty(marker)) {
			save marker format: "shp"   crs:"EPSG:3857" to: exporting_path + "google_map_markers.shp" attributes:["type"];
		}
			
		write "google image vectorized";
	}
		
}


species marker {
	string type;
	point loc_define;
	aspect default{
		draw shape color: google_map_type[type] depth: 1;
	}
}

grid cell_google width: nb_pixels_x height: nb_pixels_y use_individual_shapes: false use_regular_agents: false neighbors:8;

species OSM_agent {
	rgb color;
	aspect default {
		if (shape.area > 0) {
			draw shape color: color border: #black;
		} else if shape.perimeter > 0 {
			draw shape color: color;
		} else {
			draw circle(5) color: color;
		}
		
	}	
}

species Boundary {
	aspect default {
		draw shape color: #gray border: #black;
	}
}

experiment downloadGISdata type: gui autorun: true{
	action _init_ {
		bool pref_gis <- gama.pref_gis_auto_crs ;
		int crs <- gama.pref_gis_default_crs;
	
		gama.pref_gis_auto_crs <- false;
		gama.pref_gis_default_crs <- 3857;
		create simulation;
		gama.pref_gis_auto_crs <- pref_gis;
		gama.pref_gis_default_crs <- crs;
	}
	output {
		display map type: 3d axes: false{
			image file_exists(exporting_path + "satellite.png")? (exporting_path + "satellite.png") : default_background_image  transparency: 0.2 refresh: true;
			species OSM_agent;
			graphics "google map building" {
				loop bd over: building_google {
					draw bd color: #gray border: #black;
				}
			}
			species marker;
		}
	}
}
