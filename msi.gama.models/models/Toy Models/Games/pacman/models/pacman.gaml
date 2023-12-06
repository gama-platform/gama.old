/**
* Name: pacman
* Based on the internal empty template. 
* Author: Loris
* Tags: 
*/


@no_warning
model pacman

global {
	geometry shape <- rectangle(17,19);
	float size_p<- 0.85;
	graph the_graph;
	graph the_graph2;
	file map_init <- image_file("../includes/pacman_grid.png");
	file Blinky0 <- image_file("../includes/Blinky0.png");
	image_file pacman_file <- image_file("../includes/pacman.png");
	list<point> void <- [{0, 5}, {1, 5}, {0, 6}, {1, 6}, {15, 5}, {16, 5}, {15, 6}, {16, 6}, {8, 7}, {7, 8}, {8, 8}, {9, 8}, {0, 10}, {1, 10}, {15, 10}, {16, 10}];
	list<string> Ghost_Names <- ["Blinky", "Pinky", "Inky", "Clyde"];
	list<rgb> Ghost_Colors <- [#red, #pink, #cyan, #orange];
	list<point> Ghost_Starts <- [{8,7}, {8,8}, {7,8}, {9,8}];
	list<point> ghost_scatter_points <- [{15,1}, {1,1}, {15,15}, {1,15}];
	list<point> super_points <- [{1,2}, {15,2}, {1,14}, {15,14}];
	bool end_of_game <- false;
	
	float step <- 20#ms;
	int score;
	int food_unit <- 10;
	int super_unit <- 50;
	int kill_unit <- 200;
	float time_a <- 0.0#s;
	
	bool has_started <- false;
	
	int level;
	
	float progression <- 5.0;
	
	float pacman_speed{
		return 3 + 5 *(1-exp(-level/progression));
	}
//	list<float> ghost_speed <- [2.10, 2.40, 3.60];
	float ghost_speed{
		return pacman_speed() * (0.3 * (1 - exp(-level/progression)) + 0.7);
	}
	//list<rgb> color_levels <- [#blue, #mediumpurple, #teal];
	
	rgb color_level{
		return hsb( (level mod 360)/360.0 * 20, 0.25,0.75);
	}
	
	reflex add_time {
		time_a <- time_a + step;
	}
	
	string get_score(int scr){
		string ret <- "";
		if scr < 100000 {
			ret <- ret + "0";
		}
		if scr < 10000 {
			ret <- ret + "0";
		}
		if scr < 1000 {
			ret <- ret + "0";
		}
		if scr < 100 {
			ret <- ret + "0";
		}
		if scr < 10 {
			ret <- ret + "0";
		}
		//write ret + scr;
		return ret + string(scr);
	}
	
	action game_over {
		do pause;
		end_of_game <- true;
	}
	

	
	action init_a {
		has_started <- false;
		time_a <- 0.0;
		
		
		create pac {
			speed <- world.pacman_speed();
			location <- cell[8,10].location;
			orientation <- 0;
		}
		ask cell {
			is_wall <- map_init at {grid_x, grid_y}= -16777216;
			color <- not is_wall ? #black: world.color_level();
			has_food <- not is_wall;
		}
		loop p over:void {
			ask cell[int(p.x), int(p.y)] {
				has_food <- false;
			}
		}
		loop p over:super_points {
			ask cell[int(p.x), int(p.y)] {
				has_superpower <- true;
				
			}
		}
		loop i over:range(3) {
			create ghost {
				name <- Ghost_Names[i];
				color <- Ghost_Colors[i];
				location <- cell[int(Ghost_Starts[i].x), int(Ghost_Starts[i].y)].location;
				scatter_point <- cell[int(ghost_scatter_points[i].location.x), int(ghost_scatter_points[i].location.y)].location;
				speed <- world.ghost_speed();
				if name in ["Blinky", "Pinky"] {
					can_move <- true;
				}
			}
		}
		the_graph <- grid_cells_to_graph(cell select not each.is_wall);
		the_graph2 <- grid_cells_to_graph([cell[7,8], cell[8,8], cell[9,8]]);
		
		if end_of_game {
			level <- 0;
		}
		end_of_game <- false;
		
	}
	init {
	
		do init_a;
		
	}
	
	
	
	reflex end when: cell select (each.has_food) = []{
//		if level < 2 {
			level <- level + 1;
//		}
		ask agents-world-cell {
			do die;
		}
		do init_a;
		has_started <- false;
		do pause;
		has_started <- false;
		
	}
	
}

species pac {
	float size <- 0.4#m;
	int orientation <- 5;
	int orientation_query;
	cell my_cell;
	float speed;
	
	
	reflex move {
		my_cell <- cell closest_to self;
		
		bool is_doable <- false;
		
		switch orientation_query {
			match 0 {
				cell to_test <- my_cell.custom_neighbors()[0];
				if not (to_test = nil) {
					if not to_test.is_wall {
						
						is_doable <- true;
						if orientation = 1 {
							if location.x < my_cell.location.x - 0.05  {
								is_doable <- false;
							}
						}
						if orientation = 3 {
							if location.x > my_cell.location.x + 0.05 {
								is_doable <- false;
							}
						}
					}
				}
			}
			match 1 {
				cell to_test <- my_cell.custom_neighbors()[1];
				if not (to_test = nil) {
					if not to_test.is_wall {
						is_doable <- true;
						if orientation = 0 {
							if location.y > my_cell.location.y + 0.05 {
								is_doable <- false;
							}
						}
						if orientation = 2 {
							if location.y < my_cell.location.y - 0.05{
								is_doable <- false;
							}
						}
					}
				}
			}
			match 2 {
				cell to_test <- my_cell.custom_neighbors()[2];
				if not (to_test = nil) {
					if not to_test.is_wall {
						is_doable <- true;
						if orientation = 1 {
							if location.x < my_cell.location.x - 0.05 {
								is_doable <- false;
							}
						}
						if orientation = 3 {
							if location.x > my_cell.location.x + 0.05 {
								is_doable <- false;
							}
						}
					}
				}
			}
			match 3 {
				cell to_test <- my_cell.custom_neighbors()[3];
				if not (to_test = nil) {
					if not to_test.is_wall {
						is_doable <- true;
						if orientation = 0 {
							if location.y > my_cell.location.y + 0.05{
								is_doable <- false;
							}
						}
						if orientation = 2 {
							if location.y < my_cell.location.y - 0.05{
								is_doable <- false;
							}
						}
					}
				}
			}
		}
		
		
		/*if circle(0.5) intersects union((cell select (each.is_wall)) collect each.shape) {
			orientation <- 5;
		}*/
		if is_doable {
			orientation <- orientation_query;
		}
		
		switch orientation {
			match 0 { //North
			location <- {my_cell.location.x,location.y};
			
				if not (my_cell.custom_neighbors()[0].is_wall and location.y < my_cell.location.y) {
					
				
				location <- location + {0,-speed*step};
				
				}
			
				
			}
			match 1 { //East
			location <- {location.x,my_cell.location.y};
			if not (my_cell.custom_neighbors()[1].is_wall and location.x > my_cell.location.x) {
				location <- location + {speed*step,0};
			}
			
			}
			match 2 { //South
			location <- {my_cell.location.x,location.y};
			if not (my_cell.custom_neighbors()[2].is_wall and location.y > my_cell.location.y) {
				location <- location + {0, speed*step};
			}
			
			}
			match 3 { //West
			location <- {location.x,my_cell.location.y};
			if not (my_cell.custom_neighbors()[3].is_wall and location.x < my_cell.location.x) {
				location <- location + {-speed*step,0};
			}
			
			}
		}
		if location.x < 0 {
			location <- {location.x + 17, location.y};
		}
		if location.x > 17 {
			location <- {location.x - 17, location.y};
		}
	}
	
	reflex eat {
		if my_cell.has_food {
			ask my_cell {
				has_food <- false;
				world.score <- world.score + food_unit;
			}
		}
	}
	
	reflex trigger_superpower {
		if my_cell.has_superpower {
			world.score <- world.score + super_unit;
			ask my_cell {
				has_superpower <- false;
			}
			ask ghost {
				mode <- "frightened";
				total_frightened_time <- total_frightened_time + 5#s;
				start_fright <- time;
				
			}
		}
	}
	
	aspect default {
		geometry c <- circle(size);
		geometry d <- arc(2*size, 90*(orientation - 1),50*(1+cos(360*time/0.5)),true);
		//write 45*(1+cos(2*#pi*time/0.5));
		draw c-d color:#yellow;
		
	}
	aspect png {
		draw pacman_file size:size_p rotate:90*(orientation-1);
	}
}

grid cell height:19 width:17 neighbors:4{
	bool is_wall;
	bool has_food <- true;
	bool has_superpower <- false;
	
	
	list<cell> custom_neighbors {
		list<cell> res;
		if grid_y = 0 {
			res << nil;
		}
		else {
			res << cell[grid_x, grid_y - 1];
		}
		if grid_x = 16 {
			res << nil;
		}
		else {
			res << cell[grid_x + 1, grid_y];
		}
		if grid_y = 16 {
			res << nil;
		}
		else {
			res << cell[grid_x, grid_y + 1];
		}
		if grid_x = 0 {
			res << nil;
		}
		else {
			res << cell[grid_x - 1, grid_y];
		}
		if self=cell[136] {
			res[3] <- cell[152];
		}
		if self=cell[152] {
			res[1] <- cell[136];
		}
		
		return res;
	}
	aspect default {
		if has_food {
			draw circle(0.1) color:#white;
		}
		if has_superpower {
			draw circle(0.3) color:#white;
		}
		if is_wall {
			loop c over: neighbors {
				if not c.is_wall {
					draw c intersection self color:#yellow;
				}
			}
		}
	}
	
	
}

species ghost skills:[moving]{
	string name;
	string mode <- "scatter";
	rgb color;
	point target;
	point scatter_point;
	bool can_move<-false;
	float total_frightened_time <- 0#s;
	float start_fright <- 0#s;
	bool will_move_start<-true;
	float speed update: mode="frightened" ? world.ghost_speed()*0.55:world.ghost_speed();
	
	reflex will_move when: (not can_move) and will_move_start{
		if name = "Inky" {
			if length(cell select each.has_food) < 112 {
				can_move <- true;
				will_move_start <- false;
				
			}
		}
		if name = "Clyde" {
			if length(cell select each.has_food) < 43 {
				can_move <- true;
				will_move_start <- false;
			}
		}
		if name in ["Blinky", "Pinky"] {
			will_move_start <- false;
		}
	}
	
	aspect default {
		
		draw square(0.8) color:mode="frightened" ? #darkblue : color;
	}
	aspect png {
		image_file select_file;
		//write "../includes/"+name+string(mod(int(heading/90)+1, 4))+".png";
		if mode="frightened" {
			select_file <- image_file("../includes/frightened.png");
			if time>start_fright+3 and time<start_fright+5 {
				if round(time-start_fright) = int(time-start_fright) {
					select_file <- image_file("../includes/frightened_2.png");
				}
				
				
				
			}
		}
		else {
			
		select_file <- image_file("../includes/"+name+string(mod(int(heading/90)+1, 4))+".png");
		
		}
		
		draw select_file size:0.98#m at:location + {0,0.03};
		
		
	}
	reflex scatter when:mode="scatter" and can_move{
		target <- scatter_point;
	}
	
	reflex chase when:mode="chase" and can_move{
		switch name {
			match "Blinky" {
				target <- pac[0].my_cell.location;
			}
			match "Pinky" {
				point pac_cell <- pac[0].my_cell.location;
				
				switch pac[0].orientation {
					match 0 {
						target <- pac_cell + {0, -2};
					}
					match 1 {
						target <- pac_cell + {2, 0};
					}
					match 2 {
						target <- pac_cell + {0, 2};
					}
					match 3 {
						target <- pac_cell + {-2, 0};
					}
				}
				target <- (cell select(not each.is_wall) closest_to target).location;
				
			}
			match "Inky" {
				point over_pac <- pac[0].my_cell.custom_neighbors()[pac[0].orientation].location;
				target <- {2*over_pac.x - ghost[0].location.x, 2*over_pac.y - ghost[0].location.y};
				target <- (cell select(not each.is_wall) closest_to target).location;
			}
			match "Clyde" {
				if self distance_to pac[0].location > 8 {
					target <- pac[0].my_cell.location;
				}
				else {
					target <- scatter_point;
				}
			}
		}
	}
	
	
	reflex wanderr when:not can_move {
		if flip(step) {
		target <- any(cell[7,8], cell[8,8], cell[9,8]).location;
		
		}
		if target = location {
			target <- any([cell[7,8], cell[8,8], cell[9,8]] - cell closest_to self).location;
		}
		do goto target:target on:the_graph2;
	}
	reflex move when:can_move and not (mode="frightened"){
		do goto target:target on:the_graph;
	}
	reflex move_frightened when:can_move and mode="frightened" {
		if flip(step) {
			
		target <- any(cell).location;
		
		}
		do goto target:target on:the_graph;
		if pac[0].my_cell = cell closest_to self {
			world.score <- world.score + kill_unit;
			location <- cell[8,8].location;
			can_move <- false;
		}
	}
	reflex kill when: not (mode="frightened"){
		if pac[0].my_cell = cell closest_to self {
			ask world{
				do game_over;			
			}
			
		}
	}
	
	reflex cure when: time = start_fright + 5#s and time > 5#s{
		mode <- "chase";
		can_move <- true;
	}
	
	reflex change_mode when:time_a>=7 + total_frightened_time#s and time_a<= 27+total_frightened_time#s and mode!="frightened" {
		mode <- "chase";
	}
	reflex change_mode2 when:time_a>=27 + total_frightened_time#s and time_a<= 34+total_frightened_time#s and mode!="frightened" {
		mode <- "scatter";
	}
	reflex change_mode3 when:time_a>=34 + total_frightened_time#s and time_a<= 54+total_frightened_time#s and mode!="frightened" {
		mode <- "chase";
	}
	reflex change_mode4 when:time_a>=54 + total_frightened_time#s and time_a<= 59+total_frightened_time#s and mode!="frightened" {
		mode <- "scatter";
	}
	reflex change_mode5 when:time_a>=59 + total_frightened_time#s and time_a<= 79+total_frightened_time#s and mode!="frightened" {
		mode <- "chase";
	}
	reflex change_mode6 when:time_a>=79 + total_frightened_time#s and time_a<= 84+total_frightened_time#s and mode!="frightened" {
		mode <- "scatter";
	}
	reflex change_mode7 when:time_a>=84 + total_frightened_time#s and mode!="frightened" {
		mode <- "chase";
	}
	
	
}


experiment main {
	float minimum_cycle_duration<-20#ms;
	
	
	
	output {
		
		layout 0 tabs:false editors: false;	
		
		display main fullscreen:false type:2d antialias:false toolbar:false background:hsb( (level mod 360)/360.0 * 20, 0.2,0.7) axes:true{

			grid cell ;
			species cell;
			species pac aspect:default;
			species ghost aspect:png;
			graphics scoreboard position:{0,17} size:{17,2} background:#black border:#yellow{
				draw shape color:#black;
				draw polyline({0,0}, {17,0}) color:#yellow;
				
				draw "Score" at:{15, 8.5} font:font("Arial", 20);
				draw get_score(score) at: {14.8,14} font:font("Arial", 20, #bold);
		
				draw "Level " at: {2,8.5} font:font("Arial", 20);
				draw string(level+1) at: {2.5,14} font:font("Arial", 20, #bold);
				
				if not has_started {
					draw "Press an arrow to start" at:{5.0,11} font:font("Helvetica Neue", 25, #bold + #italic);
				}
				else if end_of_game {
					draw "Press R to restart" at:{6.1,11} font:font("Helvetica Neue", 25, #bold + #italic);					
				}
			}
			graphics "borders" {
				draw world.shape -0.001 wireframe:true color:#black width:2;
			}
			
			event #arrow_up {
				ask simulation {
					if not has_started {
						has_started <- true;
						do resume;
					}
					ask pac {
						orientation_query <- 0;
					}
					
				}
			}
			event #arrow_right {
				ask simulation {
					if not has_started {
						has_started <- true;
						do resume;
					}
					ask pac {
						orientation_query <- 1;
					}
				}
			}
			event #arrow_down {
				ask simulation {
					if not has_started {
						has_started <- true;
						do resume;
					}
					ask pac {
						orientation_query <- 2;
					}					
				}
			}
			event #arrow_left {
				ask simulation {
					if not has_started {
						has_started <- true;
						do resume;
					}
					ask pac {
						orientation_query <- 3;
					}
				}
			}
			event " " {
				ask simulation {
					do resume;
				}
			}
			event "r" {
				ask agents-world-cell {
					do die;
				}
				score <- 0;
				
				ask simulation {
					do init_a;
				}
			}
 		}
	}
}