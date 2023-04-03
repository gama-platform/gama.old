/***
* Name: Mondrian City
* Author: Arnaud Grignard, Tri Nguyen-Huu and Patrick Taillandier 
* Description: An abstract mobilty Model represented in a Mondrian World. 
* Tags: art, interaction, mobility
***/

model Mondrian_City


global{
	
	float weight_mobility1;
	float weight_mobility2;
	float weight_mobility3;
	int population_level;

	
	float spacing <- 0.75;
	float line_width <- 0.65;
	bool dynamical_width <- true;
	
	float building_scale <- 0.65; 
	bool show_cells <- false;
	bool show_building <- true;
	bool show_road <- true;
	bool show_agent <- true;
		 
	int grid_height <- 6;
	int grid_width <- 6;
	float environment_height <- 5000.0;
	float environment_width <- 5000.0;
	int global_people_size <-50;
			
	float computed_line_width;
	float road_width;
	float block_size;
	
	bool on_modification_cells <- false update: show_cells != show_cells_prev;
	bool show_cells_prev <- show_cells update: show_cells ;
	bool on_modification_bds <- false update: false;	
		
	map<string,int> max_traffic_per_mode <- ["mobility1"::50, "mobility2"::50, "walk"::50];
	map<string,int> mode_order <- ["mobility1"::0, "mobility2"::1, "walk"::2]; 
	map<string,rgb> color_per_mode <- ["mobility1"::rgb(52,152,219), "mobility2"::rgb(192,57,43), "walk"::rgb(161,196,90), "mobility3"::#magenta];
	map<string,geometry> shape_per_mode <- ["mobility1"::circle(global_people_size*0.225), "mobility2"::circle(global_people_size*0.21), "walk"::circle(global_people_size*0.2), "mobility3"::circle(global_people_size*0.21)];
	
	map<string,point> offsets <- ["mobility1"::{0,0}, "mobility2"::{0,0}, "walk"::{0,0}];
	map<string,list<rgb>> colormap_per_mode <- ["mobility1"::[rgb(107,213,225),rgb(255,217,142),rgb(255,182,119),rgb(255,131,100),rgb(192,57,43)], "mobility2"::[rgb(107,213,225),rgb(255,217,142),rgb(255,182,119),rgb(255,131,100),rgb(192,57,43)], "walk"::[rgb(107,213,225),rgb(255,217,142),rgb(255,182,119),rgb(255,131,100),rgb(192,57,43)]];
	map<string,rgb> color_per_id <- ["residentialS"::#blue,"residentialM"::#white,"residentialL"::#cyan,"officeS"::#yellow,"officeM"::#red,"officeL"::#green];
	map<string,float> nb_people_per_size <- ["S"::10.0, "M"::50.0, "L"::100.0];
	map<string,float> proba_choose_per_size <- ["S"::0.1, "M"::0.5, "L"::1.0];
	map<int, list<string>> id_to_building_type <- [1::["residential","S"],2::["residential","M"],3::["residential","L"],4::["office","S"],5::["office","M"],6::["office","L"]];
		
	float weight_mobility1_prev <- weight_mobility1;
	float weight_mobility2_prev <- weight_mobility2;
	float weight_mobility3_prev <- weight_mobility3;
	
	list<building> residentials;
	map<building, float> offices;

	map<string,graph> graph_per_mode;
	float road_capacity <- 10.0;
	geometry shape<-rectangle(environment_width, environment_height);
	float step <- sqrt(shape.area) /2000.0 ;
	
	map<string,list<float>> speed_per_mobility <- ["mobility1"::[20.0,40.0], "mobility2"::[5.0,15.0], "walk"::[3.0,7.0], "mobility3"::[15.0,30.0]];
	
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
		block_size <- min([first(cell).shape.width,first(cell).shape.height]);
	}
	
	action update_graphs {
		loop mode over: ["walk", "mobility1", "mobility2"] {
			graph_per_mode[mode] <- directed(as_edge_graph(road where (mode in each.allowed_mobility)));
		}
	}
	
	reflex update_mobility  {
		if(weight_mobility1_prev != weight_mobility1) or (weight_mobility2_prev != weight_mobility2) or (weight_mobility3_prev != weight_mobility3) {
			ask people {
				know_mobility3 <- flip(weight_mobility3);
				has_mobility1 <- flip(weight_mobility1);
				has_mobility2 <- flip(weight_mobility2);
				
				do choose_mobility;
				do mobility;
			}
		}
		weight_mobility1_prev <- weight_mobility1;
		weight_mobility2_prev <- weight_mobility2;
		weight_mobility3_prev <-weight_mobility3;
		
	}
		
	reflex randomGridUpdate when:every(1000#cycle){
		do randomGrid;
	} 
		
	reflex compute_traffic_density{
		ask road {traffic_density <- ["mobility1"::[0::0,1::0], "mobility2"::[0::0,1::0], "walk"::[0::0,1::0], "mobility3"::[0::0,1::0]];}

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
		computed_line_width <- line_width * road_width/10;
		loop t over: mode_order.keys{
			offsets[t] <- {0.5*road_width*spacing*(mode_order[t]+0.5)/(length(mode_order)-0.5),0.5*road_width*spacing*(mode_order[t]+0.5)/(length(mode_order)-0.5)};
		}		
	}
		
	action manage_road{
		road selected_road <- first(road overlapping (circle(sqrt(shape.area)/100.0) at_location #user_location));
		if (selected_road != nil) {
			bool with_mobility1 <- "mobility1" in selected_road.allowed_mobility;
			bool with_mobility2 <- "mobility2" in selected_road.allowed_mobility;
			bool with_pedestrian <- "walk" in selected_road.allowed_mobility;
			map input_values <- user_input_dialog([enter("mobility1 allowed",with_mobility1),enter("mobility2 allowed",with_mobility2),enter("pedestrian allowed",with_pedestrian)]);
			if (with_mobility1 != input_values["mobility1 allowed"]) {
				if (with_mobility1) {selected_road.allowed_mobility >> "mobility1";}
				else {selected_road.allowed_mobility << "mobility1";}
				
			}
			if (with_mobility2 != input_values["mobility2 allowed"]) {
				if (with_mobility2) {selected_road.allowed_mobility >> "mobility2";}
				else {selected_road.allowed_mobility << "mobility2";}
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
		color <- color_per_id[type+size];
	}
	aspect default {
		if show_building {draw shape scaled_by building_scale*1.1 wireframe:true color: color;}
	}
}

species road {
	int nb_people;
	map<string,map<int,int>> traffic_density <- ["mobility1"::[0::0,1::0], "mobility2"::[0::0,1::0], "walk"::[0::0,1::0], "mobility3"::[0::0,1::0]];
	rgb color <- rnd_color(255);
	list<string> allowed_mobility <- ["walk","mobility2","mobility1"];

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
		if(show_road){
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

species people skills: [moving]{

	int heading_index <- 0;
	string mobility_mode <- "walk"; 
	float display_size <-sqrt(world.shape.area)* 0.01;
	building origin;
	building dest;
	bool to_destination <- true;
	point target;
	bool know_mobility3 <- false;
	bool has_mobility1 <- flip(weight_mobility1);
	bool has_mobility2 <- flip(weight_mobility2);
	float max_dist_walk <- 1000.0;
	float max_dist_mobility2 <- 3000.0;
	float max_dist_mobility3 <- 5000.0;
	action choose_mobility {
		if (origin != nil and dest != nil) {
			float dist <- manhattan_distance(origin.location, dest.location);
			if (dist <= max_dist_walk ) {
					mobility_mode <- "walk";
			} else if (has_mobility2 and dist <= max_dist_mobility2 ) {
					mobility_mode <- "mobility2";
			} else if (know_mobility3 and (dist <= max_dist_mobility3 )) {
					mobility_mode <- "mobility3";
			} else if has_mobility1 {
					mobility_mode <- "mobility1";
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
		do goto target: target on: graph_per_mode[(mobility_mode = "mobility3") ? "mobility2" : mobility_mode] recompute_path: false ;
		do register;
	}
	action update_target {
		if (to_destination) {target <- any_location_in(dest);}//centroid(dest);}
		else {target <- any_location_in(origin);}//centroid(origin);}
		do choose_mobility;
		do mobility;
	}
	
	action register {
		if ((mobility_mode = "mobility1") and current_edge != nil) {
			road(current_edge).nb_people <- road(current_edge).nb_people + 1;
		}
	}
	action unregister {
		if ((mobility_mode = "mobility1") and current_edge != nil) {
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
		if(show_agent){
		point offset <- {0,0};
		if self.current_edge != nil {
		  offset <- offsets[mobility_mode]*(heading_index > 0 ? (-1): 1);	
		}
		if (target != nil or dest = nil) {
			if(mobility_mode ="mobility1"){
			  draw copy(shape_per_mode[mobility_mode])  color: color_per_mode[mobility_mode] border:color_per_mode[mobility_mode] rotate:heading +90 at: location+offset;
			}else{
			  draw copy(shape_per_mode[mobility_mode])  color: color_per_mode[mobility_mode] rotate:heading +90 at: location+offset;	
			}
		}	
	  }
	}
}

grid cell width: grid_width height: grid_height { 
	building my_building;
	bool is_active <- true;
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
		if show_cells {draw shape scaled_by (0.5) color: rgb(100,100,100) ;}
	}
}

experiment MondrianCity type: gui autorun: true{
	float minimum_cycle_duration <- 0.05;
	parameter "mobility1 level" var: weight_mobility1 min: 0.1 max: 1.0 step: 0.1 colors: [#gamablue] <-0.5;
	parameter "mobility2 level" var: weight_mobility2 min: 0.1 max: 1.0 step: 0.1 colors: [#gamablue] <-0.5;
	parameter "mobility3 level" var: weight_mobility3 min: 0.1 max: 1.0 step: 0.1 colors: [#gamablue] <-0.5;
	parameter "Population level" var: population_level min: 0 max: 100 step: 1 colors: [#gamablue] <-50;

	output synchronized:true{
		display map background:#black toolbar:false type:3d  axes:false fullscreen:false{
			species cell aspect:default;
			species road ;
			species people;
			species building;
			event "a" {show_agent<-!show_agent;}
			event "r" {show_road<-!show_road;}
			event "b"  {show_building<-!show_building;}		  	
		}		
	}
}