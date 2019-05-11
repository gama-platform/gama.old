/***
* Name: Urbam
* Author: Arno, Pat et Tri
* Description: 
* Tags: Tag1, Tag2, TagN
***/

model Urbam


global{
	//PARAMETERS
	
	float weight_car parameter: 'weight car' category: "Mobility" step: 0.1 min:0.1 max:1.0 <- 0.75 ;
	float weight_bike parameter: 'weight bike' category: "Mobility" step: 0.1 min:0.1 max:1.0 <- 0.5 ;
	float weight_pev  step: 0.1 min: 0.0 max: 1.0 parameter: "weight pev" category: "Mobility" <- 0.1;
	
	int population_level <- 40 parameter: 'Population level' min: 0 max: 300 category: "General";
	
	string road_aspect parameter: 'Roads aspect:' category: 'Road Aspect' <-"split (5)" among:["default", "default (car)", "hide","road type","edge color","split (3)", "split (5)"];
	float spacing parameter: 'Spacing ' category: 'Road Aspect' <- 0.75 min:0.0 max: 1.5;
	float line_width parameter: 'Line width' category: 'Road Aspect' <- 0.65 min:0.0 max: 3.0;
	bool dynamical_width parameter: 'Dynamical width' category: 'Road Aspect' <- true;
	
	float building_scale parameter: 'Building scale:' category: 'Building Aspect' <- 0.65 min: 0.2 max: 1.0; 
	bool show_cells parameter: 'Show cells:' category: 'Aspect' <- false;
	bool show_building parameter: 'Show Building:' category: 'Aspect' <- true;
	
	
	//SPATIAL PARAMETERS  
	int grid_height <- 6;
	int grid_width <- 6;
	float environment_height <- 5000.0;
	float environment_width <- 5000.0;
	int global_people_size <-50;

	bool blackMirror parameter: 'Dark Room' category: 'Aspect' <- true;
	
	
	string people_aspect parameter: 'People aspect:' category: 'People Aspect' <-"mode" among:["mode", "profile","dynamic_abstract","dynamic_abstract (car)","hide"];
	
	
	
	bool load_grid_file_from_cityIO <-false; //parameter: 'Online Grid:' category: 'Simulation' <- false;
	bool load_grid_file <- false;// parameter: 'Offline Grid:' category: 'Simulation'; 
	bool udpScannerReader <- false; 
	bool udpSliderReader <- true; 
	bool editionMode <-false;
		
	float computed_line_width;
	float road_width;
	float block_size;

	
	bool on_modification_cells <- false update: show_cells != show_cells_prev;
	
	bool show_cells_prev <- show_cells update: show_cells ;
	bool on_modification_bds <- false update: false;
	
	
	//ORIGINAL map<string,int> max_traffic_per_mode <- ["car"::90, "bike"::10, "walk"::50];
	
	map<string,int> max_traffic_per_mode <- ["car"::50, "bike"::50, "walk"::50];
	map<string,int> mode_order <- ["car"::0, "bike"::1, "walk"::2]; // order from 0 to n write only the modes that have to be drawn
	map<string,rgb> color_per_mode <- ["car"::rgb(52,152,219), "bike"::rgb(192,57,43), "walk"::rgb(161,196,90), "pev"::#magenta];
	//map<string,rgb> color_per_mode <- ["car"::rgb(255,0,0), "bike"::rgb(0,255,0), "walk"::rgb(0,0,255), "pev"::#magenta];
	map<string,geometry> shape_per_mode <- ["car"::circle(global_people_size*0.225), "bike"::circle(global_people_size*0.21), "walk"::circle(global_people_size*0.2), "pev"::circle(global_people_size*0.21)];
	
	map<string,point> offsets <- ["car"::{0,0}, "bike"::{0,0}, "walk"::{0,0}];
	map<string,rgb> color_per_profile <- ["young poor"::#deepskyblue, "young rich"::#darkturquoise, "adult poor"::#orangered , "adult rich"::#coral,"old poor"::#darkslategrey,"old rich"::#lightseagreen];
	map<string,list<rgb>> colormap_per_mode <- ["car"::[rgb(107,213,225),rgb(255,217,142),rgb(255,182,119),rgb(255,131,100),rgb(192,57,43)], "bike"::[rgb(107,213,225),rgb(255,217,142),rgb(255,182,119),rgb(255,131,100),rgb(192,57,43)], "walk"::[rgb(107,213,225),rgb(255,217,142),rgb(255,182,119),rgb(255,131,100),rgb(192,57,43)]];
	map<string,rgb> color_per_type <- ["residential"::#gray, "office"::#orange];
	map<string,rgb> color_per_id <- ["residentialS"::#blue,"residentialM"::#white,"residentialL"::#cyan,"officeS"::#yellow,"officeM"::#red,"officeL"::#green];
	map<string,float> nb_people_per_size <- ["S"::10.0, "M"::50.0, "L"::100.0];
	map<string,float> proba_choose_per_size <- ["S"::0.1, "M"::0.5, "L"::1.0];
	map<int, list<string>> id_to_building_type <- [1::["residential","S"],2::["residential","M"],3::["residential","L"],4::["office","S"],5::["office","M"],6::["office","L"]];
	list fivefoods<-["Residential","Retail","Hotel","Office","Industrial","Park"];
		


	float weight_car_prev <- weight_car;
	float weight_bike_prev <- weight_bike;
	float weight_pev_prev <- weight_pev;
	
	list<building> residentials;
	map<building, float> offices;
	string imageFolder <- "../images/flat/";
	string profile_file <- "../includes/profiles.csv"; 
	map<string,map<profile,float>> proportions_per_bd_type;
	int action_type;
	

	int file_cpt <- 1;

	map<string,graph> graph_per_mode;
	
	float road_capacity <- 10.0;
	bool traffic_jam <- true parameter: true;
	
	//geometry shape <- envelope(nyc_bounds0_shape_file);
	geometry shape<-rectangle(environment_width, environment_height); // one edge is 5000(m)
	//geometry shape<-rectangle(8000, 5000);
	float step <- sqrt(shape.area) /2000.0 ;
	
	map<string,list<float>> speed_per_mobility <- ["car"::[20.0,40.0], "bike"::[5.0,15.0], "walk"::[3.0,7.0], "pev"::[15.0,30.0]];
		
	// Network
	int scaningUDPPort <- 9877;
	int interfaceUDPPort <- 9878;
	string url <- "localhost";
	
	init {
		list<geometry> lines;
		float cell_w <- first(cell).shape.width;
		float cell_h <- first(cell).shape.height;
		loop i from: 0 to: grid_width {
			lines << line([{i*cell_w,0}, {i*cell_w,environment_height}]);
		}
		loop i from: 0 to: grid_height {
			lines << line([{0, i*cell_h}, {environment_width,i*cell_h}]);
		}
		create road from: split_lines(lines) {
			create road with: [shape:: line(reverse(shape.points))];
		}
		do update_graphs;
		do init_buttons; 
		do load_profiles;
		block_size <- min([first(cell).shape.width,first(cell).shape.height]);
		if(udpScannerReader){
			create NetworkingAgent number: 1 {
			 type <-"scanner";	
		     do connect to: url protocol: "udp_server" port: scaningUDPPort ;
		    }
		}
		if(udpSliderReader){
			create NetworkingAgent number: 1 {
			 type <-"interface";	
		     do connect to: url protocol: "udp_server" port: interfaceUDPPort ;
		    }	   
		}
	}
		
	action load_profiles {
		create profile from: csv_file(profile_file,";", true) with: [proportionS::float(get("proportionS")),proportionM::float(get("proportionM")),proportionL::float(get("proportionL")),
			name::string(get("typo")), max_dist_walk::float(get("max_dist_walk")),max_dist_bike::float(get("max_dist_bike")),max_dist_pev::float(get("max_dist_pev"))
		];
		ask profile {
			map<profile, float> prof_pro1 <- proportions_per_bd_type["S"];
			prof_pro1[self] <- proportionS; proportions_per_bd_type["S"] <- prof_pro1;
			map<profile, float> prof_pro2 <- proportions_per_bd_type["M"];
			prof_pro2[self] <- proportionM; proportions_per_bd_type["M"] <- prof_pro2;
			map<profile, float> prof_pro3 <- proportions_per_bd_type["L"];
			prof_pro3[self] <- proportionL; proportions_per_bd_type["L"] <- prof_pro3;
		}
	}
	action update_graphs {
		loop mode over: ["walk", "car", "bike"] {
			graph_per_mode[mode] <- directed(as_edge_graph(road where (mode in each.allowed_mobility)));
		}
	}
	
	action init_buttons	{
		int inc<-0;
		ask button {
			action_nb<-inc;
			inc<-inc+1;
		}
	}
	
	
	action activate_act {
		button selected_but <- first(button overlapping (circle(1) at_location #user_location));
		ask selected_but {
			ask button {bord_col<-#black;}
			action_type<-action_nb;
			bord_col<-#red;
		}
	}

	
	reflex update_mobility  {
		if(weight_car_prev != weight_car) or (weight_bike_prev != weight_bike) or (weight_pev_prev != weight_pev) {
			ask people {
				know_pev <- flip(weight_pev);
				has_car <- flip(weight_car);
				has_bike <- flip(weight_bike);
				
				do choose_mobility;
				do mobility;
			}
		}
		weight_car_prev <- weight_car;
		weight_bike_prev <- weight_bike;
		weight_pev_prev <-weight_pev;
		
	}
		
	reflex test_load_file when: load_grid_file and every(100#cycle){
		do load_matrix("../includes/6x6_" +file_cpt+".csv");
		file_cpt <- (file_cpt+ 1) mod 5;
	}
	
	reflex randomGridUpdate when:!udpScannerReader and !editionMode and every(1000#cycle){
		do randomGrid;
	} 
		
	reflex update_graph when: every(3 #cycle) {
		map<road,float> weights <- traffic_jam ? road as_map (each::(each.shape.perimeter)) : road as_map (each::(each.shape.perimeter * (min([10,1/exp(-each.nb_people/road_capacity)]))));
		graph_per_mode["car"] <- graph_per_mode["car"] with_weights weights;
	}
	

	reflex compute_traffic_density{
		ask road {traffic_density <- ["car"::[0::0,1::0], "bike"::[0::0,1::0], "walk"::[0::0,1::0], "pev"::[0::0,1::0]];}

		ask people{
			if current_path != nil and current_path.edges != nil{
				ask list<road>(current_path.edges){
					traffic_density[myself.mobility_mode][myself.heading_index]  <- traffic_density[myself.mobility_mode][myself.heading_index] + 1;
				}
			}
		}
	}
	
	reflex precalculate_display_variables{
		road_width <- block_size * 2/3 * (1-building_scale);
		switch road_aspect {
			match  "split (3)" {
				computed_line_width <- line_width * road_width/6;
			}
			match  "split (5)" {
				computed_line_width <- line_width * road_width/10;
			}
			default{
				computed_line_width <- 0.5*line_width*road_width;
			}
		}
		
		loop t over: mode_order.keys{
			if road_aspect = "split (3)" {offsets[t] <- {0.5*road_width*spacing*(mode_order[t]-1),0.5*road_width*spacing*(mode_order[t]-1)};}
			if road_aspect = "split (5)" {offsets[t] <- {0.5*road_width*spacing*(mode_order[t]+0.5)/(length(mode_order)-0.5),0.5*road_width*spacing*(mode_order[t]+0.5)/(length(mode_order)-0.5)};}
		}		
	}
		
	action infrastructure_management {
		if (action_type = 8) {
			do manage_road;
		} else {
			do build_buildings;
		}
		
	}
	
	
	action manage_road{
		road selected_road <- first(road overlapping (circle(sqrt(shape.area)/100.0) at_location #user_location));
		if (selected_road != nil) {
			bool with_car <- "car" in selected_road.allowed_mobility;
			bool with_bike <- "bike" in selected_road.allowed_mobility;
			bool with_pedestrian <- "walk" in selected_road.allowed_mobility;
			map input_values <- user_input(["car allowed"::with_car,"bike allowed"::with_bike,"pedestrian allowed"::with_pedestrian]);
			if (with_car != input_values["car allowed"]) {
				if (with_car) {selected_road.allowed_mobility >> "car";}
				else {selected_road.allowed_mobility << "car";}
				
			}
			if (with_bike != input_values["bike allowed"]) {
				if (with_bike) {selected_road.allowed_mobility >> "bike";}
				else {selected_road.allowed_mobility << "bike";}
			}
			if (with_pedestrian != input_values["pedestrian allowed"]) {
				if (with_pedestrian) {selected_road.allowed_mobility >> "walk";}
				else {selected_road.allowed_mobility << "walk";}
			}
			point pt1 <- first(selected_road.shape.points);
			point pt2 <- last(selected_road.shape.points);
			road reverse_road <- road first_with ((first(each.shape.points) = pt2) and (last(each.shape.points) = pt1));
			if (reverse_road != nil) {
				reverse_road.allowed_mobility <-  selected_road.allowed_mobility;
			}
			do update_graphs;
		}
		
		
	}
	
	action build_buildings {
		cell selected_cell <- first(cell overlapping (circle(sqrt(shape.area)/100.0) at_location #user_location));
		if (selected_cell != nil) and (selected_cell.is_active) {
			if (action_type = 3) {ask selected_cell {do new_residential("S");}} 
			if (action_type = 4) {ask selected_cell {do new_office("S");}} 
			if (action_type = 5) {ask selected_cell {do erase_building;}} 
			if (action_type = 6) {ask selected_cell {do new_residential("M");}} 
			if (action_type = 7) {ask selected_cell {do new_office("M");}} 
			if (action_type = 9) {ask selected_cell {do new_residential("L");}} 
			if (action_type = 10) {ask selected_cell {do new_office("L");}} 
		}
		on_modification_bds <- true;
	}
	
	action createCell(int id, int x, int y){
		list<string> types <- id_to_building_type[id];
		string type <- types[0];
		string size <- types[1];
		cell current_cell <- cell[x,y];
		bool new_building <- true;
		if (current_cell.my_building != nil) {
			building build <- current_cell.my_building;
			new_building <- (build.type != type) or (build.size != size);
		}
		if (new_building) {
			if (type = "residential") {
				ask current_cell {do new_residential(size);}
			} else if (type = "office") {
				ask current_cell {do new_office(size);}
			}
		}
	} 
	action load_matrix(string path_to_file) {
		file my_csv_file <- csv_file(path_to_file,",");
		matrix data <- matrix(my_csv_file);
		loop i from: 0 to: data.rows - 1 {
			loop j from: 0 to: data.columns - 1 {
					int id <- int(data[j, i]);
					if (id > 0) {
                     do createCell(id, j, i);
					}
					cell current_cell <- cell[j,i];
					current_cell.is_active <- id<0?false:true;
					if (id<=0){					
						ask current_cell{ do erase_building;}
					}
			}
		}
	}
	
	
	
   action randomGrid{
   	int id;
   	loop i from: 0 to: 5 {
			loop j from: 0 to: 5 {
				    if (flip(0.5)){
				        id <- 1+rnd(5);	
				    }else{
				    	id<--1;
				    }
					
					if (id > 0) {
                     do createCell(id, j, i);
					}
					cell current_cell <- cell[j,i];
					current_cell.is_active <- id<0?false:true;
					if (id<=0){					
						ask current_cell{ do erase_building;}
					}
			}
		}
   }
	
	action load_cityIO_matrix_v1(string cityIOUrl_) {
		map<string, unknown> cityMatrixData;
	    list<map<string, int>> cityMatrixCell;	
		try {
			cityMatrixData <- json_file(cityIOUrl_).contents;
		} catch {
			cityMatrixData <- json_file("../includes/cityIO_Kendall.json").contents;
			//write #current_error + "Connection to Internet lost or cityIO is offline - CityMatrix is a local version from cityIO_Kendall.json";
		}
		cityMatrixCell <- cityMatrixData["grid"];	
		loop l over: cityMatrixCell { 
      	  int id <-int(l["type"]);
      	  if(id!=-2 and id !=-1 and id!=6 ){
      	  	do createCell(id+1, l["x"], l["y"]);	
      	  } 
      	  if (id=-1){
		    cell current_cell <- cell[l["x"],l["y"]];
			ask current_cell{ do erase_building;}
		  }     
        }	
	}
	
	
	action load_cityIO_matrix_v2(string cityIOUrl_) {
		map<string, unknown> cityMatrixData;
	    list<map<string, int>> cityMatrixCell;	
		try {
			cityMatrixData <- json_file(cityIOUrl_).contents;
		} catch {
			cityMatrixData <- json_file("../includes/cityIO_gama.json").contents;
			write #current_error + "Connection to Internet lost or cityIO is offline - CityMatrix is a local version from cityIO_gama.json";
		}
		int nbCols <- int(cityMatrixData["header"]["spatial"]["ncols"]);
		int nbRows <- int(cityMatrixData["header"]["spatial"]["nrows"]);
		//cityMatrixCell <- cityMatrixData["grid"];
		//write 	cityMatrixCell;
		loop i from: 0 to: nbCols-1 {
			loop j from: 0 to: nbRows -1{
				int id <-int(cityMatrixData["grid"][j*nbCols+i][0]);
				//write id;
      	  		if(id!=-2 and id !=-1 and id!=6 ){
      	  			do createCell(id+1, i, j);	
      	  		} 
      	  		if (id=-1){
		    		cell current_cell <- cell[i,j];
					ask current_cell{ do erase_building;}
		  		}   
			}
        } 	
	}
		
}


species building {
	string size <- "S" among: ["S", "M", "L"];
	string type <- "residential" among: ["residential", "office"];
	list<people> inhabitants;
	rgb color;
	geometry bounds;

	action initialize(cell the_cell, string the_type, string the_size) {
		the_cell.my_building <- self;
		type <- the_type;
		size <- the_size;
		do define_color;
		shape <- the_cell.shape;
		if (type = "residential") {residentials << self;}
		else if (type = "office") {
			offices[self] <- proba_choose_per_size[size];
		}
		bounds <- the_cell.shape + 0.5 - shape;
			
	}
	
	reflex populate when: (type = "residential"){
		int pop <- int(population_level/100 * nb_people_per_size[size]);
		if length(inhabitants) < pop{
			create people number: 1 with: [location::any_location_in(bounds)] {
				origin <- myself;
				origin.inhabitants << self;
				
				do reinit_destination;
				map<profile, float> prof_pro <- proportions_per_bd_type[origin.size];
				my_profile <- prof_pro.keys[rnd_choice(prof_pro.values)];
			}
		}
		if length(inhabitants) > pop{
			people tmp <- one_of(inhabitants);
			inhabitants >- tmp;
			ask tmp {do die;}
		}
	}
	
	action remove {
		if (type = "office") {
			offices[] >- self;
			ask people {
				do reinit_destination;
			}
		} else {
			ask inhabitants {
				do die;
			}
		}
		cell(location).my_building <- nil;
		do die;
		
	}
	action define_color {
		//color <- rgb(color_per_type[type], size = "S" ? 50 : (size = "M" ? 100: 255)  );
		color <- color_per_id[type+size];
	}
	aspect default {
		//if show_building {draw shape scaled_by building_scale color: color;}
		//if show_building {draw shape scaled_by 0.5 color: rgb(100,100,100);}
		if show_building {draw shape scaled_by building_scale*1.1 empty:true color: color;}
	}
}

species road {
	int nb_people;
	map<string,map<int,int>> traffic_density <- ["car"::[0::0,1::0], "bike"::[0::0,1::0], "walk"::[0::0,1::0], "pev"::[0::0,1::0]];
	rgb color <- rnd_color(255);
	list<string> allowed_mobility <- ["walk","bike","car"];

	init {
	}
	
	int total_traffic{
		return sum(traffic_density.keys collect(sum(traffic_density[each])));
	}
	
	
	int total_traffic_per_mode(string m){
		return sum(traffic_density[m]);
	}
	
	
	rgb color_map(rgb c, float scale){
		return rgb(255+scale * (c.red - 255),255+scale * (c.green - 255),255+scale * (c.blue - 255));
	}

	aspect default {
		switch road_aspect {
			match "default" {
				if total_traffic() > 0 {
					float scale <- min([1,total_traffic() / max_traffic_per_mode["car"]]);
					if dynamical_width{
						draw shape + computed_line_width * scale color: color_per_mode["car"];	
					}else{
						draw shape + computed_line_width color: color_map(color_per_mode["car"],scale);	
					}
				}	
			}
			match "default (car)" {
				if total_traffic_per_mode('car') > 0 {
					float scale <- min([1,total_traffic_per_mode('car') / max_traffic_per_mode["car"]]);
					if dynamical_width{
						draw shape + computed_line_width * scale color: color_per_mode["car"];	
					}else{
						draw shape + computed_line_width color: color_map(color_per_mode["car"],scale);	
					}
				}	
			}
			match "road type" {
				if ("car" in allowed_mobility) {
					draw shape + computed_line_width color:color_per_mode["car"];
				}
				if ("bike" in allowed_mobility) {
					draw shape + 0.5*computed_line_width color:color_per_mode["bike"];
				}
				if ("walk" in allowed_mobility) {
					draw shape + 0.2*computed_line_width color:color_per_mode["walk"];
				}
			}
			match "edge color"{		
				int traffic <-total_traffic() ; 
				if traffic > 0 {
					float scale <- min([1,traffic / 100])^2;
					draw shape + computed_line_width color: colormap_per_mode["car"][int(4*scale)];
				}	
			}
			match "split (3)"{
				loop t over: mode_order.keys{
					float scale <- min([1,total_traffic_per_mode(t) / max_traffic_per_mode[t]]);		
					if scale > 0 {
						if dynamical_width{
							draw shape + computed_line_width * scale color: color_per_mode[t] at: self.location+offsets[t];	
						}else{
							draw shape + computed_line_width color: color_map(color_per_mode[t],scale) at: self.location+offsets[t];	
						}
					}
				}
			}	
			match "split (5)"{
				loop t over: mode_order.keys{
					float scale <- min([1,traffic_density[t][0] / max_traffic_per_mode[t]]);	
					if dynamical_width{
						if scale > 0 {draw shape + computed_line_width * scale color: color_per_mode[t] at: self.location+offsets[t];}
						scale <- min([1,traffic_density[t][1] / max_traffic_per_mode[t]]);	
						if scale > 0 {draw shape + computed_line_width * scale color: color_per_mode[t] at: self.location-offsets[t];}
					}else{
						if scale > 0 {draw shape + computed_line_width color: color_map(color_per_mode[t],scale) at: self.location+offsets[t];}
						scale <- min([1,traffic_density[t][1] / max_traffic_per_mode[t]]);	
						if scale > 0 {draw shape + computed_line_width color: color_map(color_per_mode[t],scale) at: self.location-offsets[t];}
					}
				}
			}		
		}	
	}
}


species profile {
	float proportionS;
	float proportionM;
	float proportionL;
	float max_dist_walk;
	float max_dist_bike;
	float max_dist_pev;
}
species people skills: [moving]{

	int heading_index <- 0;
	string mobility_mode <- "walk"; 
	float display_size <-sqrt(world.shape.area)* 0.01;
	building origin;
	building dest;
	bool to_destination <- true;
	point target;
	profile my_profile;
	bool know_pev <- false;
	bool has_car <- flip(weight_car);
	bool has_bike <- flip(weight_bike);
	action choose_mobility {
		if (origin != nil and dest != nil and my_profile != nil) {
			float dist <- manhattan_distance(origin.location, dest.location);
			if (dist <= my_profile.max_dist_walk ) {
					mobility_mode <- "walk";
			} else if (has_bike and dist <= my_profile.max_dist_bike ) {
					mobility_mode <- "bike";
			} else if (know_pev and (dist <= my_profile.max_dist_pev )) {
					mobility_mode <- "pev";
			} else if has_car {
					mobility_mode <- "car";
			} else {
					mobility_mode <- "walk";
			}
		speed <- rnd(speed_per_mobility[mobility_mode][0],speed_per_mobility[mobility_mode][1]) #km/#h;
		}
	}
	
	float manhattan_distance (point p1, point p2) {
		return abs(p1.x - p2.x) + abs(p1.y - p2.y);
	}
	reflex update_heading_index{
		if (mod(heading+90,360) < 135) or (mod(heading+90,360) > 315){
						heading_index <- 0;
					} else{
						heading_index <- 1;
					}
	}
	action reinit_destination {
		dest <- empty(offices) ? nil : offices.keys[rnd_choice(offices.values)];
		target <- nil;
	}
	
	action mobility {
		do unregister;
		do goto target: target on: graph_per_mode[(mobility_mode = "pev") ? "bike" : mobility_mode] recompute_path: false ;
		do register;
	}
	action update_target {
		if (to_destination) {target <- any_location_in(dest);}//centroid(dest);}
		else {target <- any_location_in(origin);}//centroid(origin);}
		do choose_mobility;
		do mobility;
	}
	
	action register {
		if ((mobility_mode = "car") and current_edge != nil) {
			road(current_edge).nb_people <- road(current_edge).nb_people + 1;
		}
	}
	action unregister {
		if ((mobility_mode = "car") and current_edge != nil) {
			road(current_edge).nb_people <- road(current_edge).nb_people - 1;
		}
	}

	reflex move when: dest != nil{
		if (target = nil) {
			do update_target;
		}
		do mobility;
		if (target = location) {
			target <- nil;
			to_destination <- not to_destination;
			do update_target;
		}
	}
	
	
	reflex wander when: dest = nil and origin != nil {
		do wander bounds: origin.bounds;
	}

	
	aspect default{
		point offset <- {0,0};
		if self.current_edge != nil {
			if road_aspect = "split (3)"{
				offset <- offsets[mobility_mode];
			}
			if road_aspect = "split (5)"{
				offset <- offsets[mobility_mode]*(heading_index > 0 ? (-1): 1);
			}			
				
		}
		switch people_aspect {
		   match "mode" {	
				if (target != nil or dest = nil) {
					if(mobility_mode ="car"){
					  draw copy(shape_per_mode[mobility_mode])  color: color_per_mode[mobility_mode] border:color_per_mode[mobility_mode] rotate:heading +90 at: location+offset;
					}else{
					  draw copy(shape_per_mode[mobility_mode])  color: color_per_mode[mobility_mode] rotate:heading +90 at: location+offset;	
					}
				}	
			}	
			match "profile" {
				if (target != nil or dest = nil) {
					if(mobility_mode ="car"){
					  draw copy(shape_per_mode[mobility_mode])  empty:true border:color_per_profile[my_profile.name] rotate:heading +90 at: location+offset;
					}else{
					  draw copy(shape_per_mode[mobility_mode])  color: color_per_profile[my_profile.name] rotate:heading +90 at: location+offset;	
					}
				}
			}
			match "dynamic_abstract"{		
				float scale <- min([1,road(current_edge).total_traffic() / 100])^2;
				if (target != nil or dest = nil) {draw square(display_size) color: colormap_per_mode["car"][int(4*scale)] at: location+offset;}
			}		
			match "dynamic_abstract (car)"{		
				float scale <- min([1,road(current_edge).total_traffic_per_mode('car') / 100])^2;
				if (target != nil or dest = nil) {draw square(display_size) color: colormap_per_mode["car"][int(4*scale)] at: location+offset;}
			}		
		}
		
	}
}

grid cell width: grid_width height: grid_height { // height: 16{
	building my_building;
	bool is_active <- true;
	//rgb color <- #white;
	action new_residential(string the_size) {
		if (my_building != nil and (my_building.type = "residential") and (my_building.size = the_size)) {
			return;
		} else {
			if (my_building != nil ) {ask my_building {do remove;}}
			create building returns: bds{
				do initialize(myself, "residential", the_size);
			}
		}
		
	}
	action new_office (string the_size) {
		if (my_building != nil and (my_building.type = "office") and (my_building.size = the_size)) {
			return;
		} else {
			if (my_building != nil) {ask my_building {do remove;}}
			create building returns: bds{
				do initialize(myself, "office",the_size);
			}
			ask people {
				do reinit_destination;
			}
		}
	}
	action erase_building {
		if (my_building != nil) {ask my_building {do remove;}}
	}
	
	aspect default{
		//if show_cells {draw shape scaled_by (building_scale+(1-building_scale)/3) color: rgb(100,100,100) ;}
		if show_cells {draw shape scaled_by (0.5) color: rgb(100,100,100) ;}
	}

}

grid button width:3 height:4 
{
	int action_nb;
	rgb bord_col<-#black;
	aspect normal {
		if (action_nb > 2 and not (action_nb in [11])) {draw rectangle(shape.width * 0.8,shape.height * 0.8).contour + (shape.height * 0.01) color: bord_col;}
		if (action_nb = 0) {draw "Residential"  color:#black font:font("SansSerif", 16, #bold) at: location - {15,-10.0,0};}
		else if (action_nb = 1) {draw "Office"  color:#black font:font("SansSerif", 16, #bold) at: location - {12,-10.0,0};}
		else if (action_nb = 2) {draw "Tools"  color:#black font:font("SansSerif", 16, #bold) at: location - {12,-10.0,0};}
		else {
			draw square(shape.width * 0.5) ;
		}
	}
}

species NetworkingAgent skills:[network] {
	string type;
	string previousMess <-"";
	reflex fetch {	
		if (length(mailbox) > 0) {
			message s <- last(mailbox);
			if(s.contents !=previousMess){	
			  previousMess<-s.contents;
			  if(type="scanner"){
			  	list gridlist <- string(s.contents) split_with(";");
			  	int nrows <- 12;
			  	int ncols <- 12;
			  	int x;
			  	int y;
			  	int id;
			  	loop i from:0 to: (length(gridlist)-2){ 
			    	if((i mod nrows) mod 2 = 0 and int(i/ncols) mod 2 = 0){   
			      		x<- int((i mod nrows)/2);
			      		y<-int((int(i/ncols))/2);
			      		id<-int(gridlist[i]);
			      	if(id!=-2 and id !=-1 and id!=6 ){
	      	  	    	ask world{do createCell(id+1, x, y);}	
	      	      	} 
	      	      	if (id=-1){
			        	cell current_cell <- cell[x,y];
				    	ask current_cell{ do erase_building;}
			      	}	   
			    	} 		
	          	}
			  }
			  if(type="interface"){
			  	weight_car<-float(previousMess)/5.0;
			  	write weight_car;
			  }
			}	
	    }
	}
}


experiment cityScienceScreenEditionMode type: gui autorun: true{
	init{
		editionMode<-true;
	}
	float minimum_cycle_duration <- 1.0;
	output {
		layout #split;
		display map synchronized:true background:blackMirror ? #black :#white toolbar:false type:opengl  draw_env:false 
		camera_pos: {2160.3206,1631.7982,12043.0275} camera_look_pos: {2160.3206,1631.588,0.0151} camera_up_vector: {0.0,1.0,0.0}{
		//camera_pos: {2428.2049,2969.8873,11644.0583} camera_look_pos: {2428.2049,2969.684,-0.0081} camera_up_vector: {0.0,1.0,0.0}{
			species cell aspect:default;// refresh: on_modification_cells;
			species road ;
			species people;
			species building;// refresh: on_modification_bds;
			event mouse_down action:infrastructure_management;
			
			event["1"] action: {action_type<-9;};
			event["2"] action: {action_type<-3;};
			event["3"] action: {action_type<-6;};
			event["4"] action: {action_type<-4;};
			event["5"] action: {action_type<-7;};
			event["6"] action: {action_type<-10;};
			event["7"] action: {action_type<-5;};
			event["8"] action: {action_type<-8;}; 
			
			event["h"] action: {road_aspect<-"hide";};
			event["r"] action: {road_aspect<-"default";};
			event["s"] action: {road_aspect<-"split (5)";};
			event["c"] action: {road_aspect<-"edge color";}; 
			
			event["n"] action: {people_aspect<-"hide";};
			event["m"] action: {people_aspect<-"mode";};
			//event["p"] action: {people_aspect<-"profile";};
			event["a"] action: {people_aspect<-"dynamic_abstract";};
		
			event["w"] action: {blackMirror<-!blackMirror;};
			event["b"] action: {show_building<-!show_building;};
			 
			event["g"] action: {show_cells<-!show_cells;}; 
			
			event["o"] action: {weight_car<-weight_car-0.1;}; 
			event["p"] action: {weight_car<-weight_car+0.1;};
			
			event["u"] action: {weight_bike<-weight_bike-0.1;}; 
			event["i"] action: {weight_bike<-weight_bike+0.1;};
			
			event["t"] action: {weight_pev<-weight_pev-0.1;}; 
			event["y"] action: {weight_pev<-weight_pev+0.1;};
			
			
			graphics "mobilityMode" {
				    draw circle(world.shape.width * 0.01) color: color_per_mode["walk"] at: {world.shape.width * 0.2, world.shape.height};
					draw "walk" color: color_per_mode["walk"]  at: {world.shape.width * 0.2+world.shape.width * 0.02, world.shape.height * 1.005} font:font("Helvetica", 6 , #bold);
					
					draw circle(world.shape.width * 0.01) color: color_per_mode["bike"] at: {world.shape.width * 0.4, world.shape.height};
					draw "bike" color: color_per_mode["bike"]  at: {world.shape.width * 0.4 + world.shape.width * 0.02, world.shape.height * 1.005} font:font("Helvetica", 6 , #bold);
					
					draw circle(world.shape.width * 0.01) color: color_per_mode["car"] at: {world.shape.width * 0.6, world.shape.height};
					draw "car" color: color_per_mode["car"]  at: {world.shape.width * 0.6 + world.shape.width * 0.02, world.shape.height * 1.005} font:font("Helvetica", 6 , #bold);
					
					draw circle(world.shape.width * 0.01) color: color_per_mode["pev"] at: {world.shape.width * 0.8, world.shape.height};
					draw "pev" color: color_per_mode["pev"]  at: {world.shape.width * 0.8 + world.shape.width * 0.02, world.shape.height * 1.005} font:font("Helvetica", 6 , #bold);
			}
			
			graphics "landuse" {
					point hpos <- {world.shape.width * 1.1, world.shape.height * 1.1};
					float barH <- world.shape.width * 0.01;
					float factor <-  world.shape.width * 0.1;
					loop i from:0 to:length(color_per_id)-1{
						draw square(world.shape.width*0.02) empty:true color: color_per_id.values[i] at: {i*world.shape.width*0.175, -50};
						draw fivefoods[i] color: color_per_id.values[i] at: {i*world.shape.width*0.175+world.shape.width*0.025, -40} perspective: true font:font("Helvetica", 8 , #bold);
					}
			}
		}
				
	    //Bouton d'action
		display action_buton background:#black name:"Actions possibles" ambient_light:100 	{
			species button aspect:normal ;
			event mouse_down action:activate_act;    
		}	
	}
}

experiment cityScienceDemo type: gui autorun: true{
	float minimum_cycle_duration <- 0.05;
	output {
		display map synchronized:true background:blackMirror ? #black :#white toolbar:false type:opengl  draw_env:false fullscreen:0{
			species cell aspect:default;// refresh: on_modification_cells;
			species road ;
			species people;
			species building;// refresh: on_modification_bds;
		}		
	}
}