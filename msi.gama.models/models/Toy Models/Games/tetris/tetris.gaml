/**
* Name: tetris
* Based on the internal empty template. 
* Author: Loris
* Tags: 
*/


@no_warning
model tetris


global {
	
	
	
	bool left <- false;
	bool right <- false;
	bool down <- false;
	bool up <- false;
	bool q <- false;
	bool d <- false;
	
	
	bool has_started<- false;
	
	list<int> stats <- [0,0,0,0,0,0,0];
	
	list<rgb> tet_colors <- [#cyan, #yellow, #purple, #green, #red, #blue, #orange];
	float step <- 20#ms;
	
	int score;
	int level <- 1;
	int count_lines_level;
	geometry shape <- rectangle(20,30);
	tetrimino initial;
	int next_te <- rnd(6);
	
	action reload {
		ask agents - cell - world{
					do die;
				}
				do do_init;
				create fixed;
				stats <- [0,0,0,0,0,0,0];
	}
	
	
	
	action do_init {
		do create_tetri;
		initial <- tetrimino(0);
		
		
		next_te <- rnd(6);
	}
	
	float freq(int l) {
		return 1/sqrt(l);
	}
	
	init {
		do do_init;
		create fixed;
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
	string get_stat(int scr){
		string ret <- "";
		
		if scr < 100 {
			ret <- ret + "0";
		}
		if scr < 10 {
			ret <- ret + "0";
		}
		//write ret + scr;
		return ret + string(scr);
	}
	
	
	geometry rotate_s(geometry g, point p, point l, int m) {
		//write (p + {(p-l).y, -(p-l).x});
		//write -int(2*(m-0.5));
		//write int(2*(m-0.5));
		//write (p + {int(2*(m-0.5))*(p-l).y, -int(2*(m-0.5))*(p-l).x});
		return ((g rotated_by int(2*(m-0.5)*90.0)) at_location (p + {int(2*(m-0.5))*(p-l).y, -int(2*(m-0.5))*(p-l).x}));
		//return ((g rotated_by int(2*(m-0.5)*90.0)) at_location (p + {(p-l).y, -(p-l).x}));
		
	}
	
	geometry select_shape(point l, int i) {
		
		switch i {
			match 0 {
				return rectangle({l.x, l.y - 2}, {l.x + 1, l.y + 2});
			}
			match 1 {
				return rectangle({l.x - 1, l.y - 1}, {l.x + 1, l.y + 1});
			}
			match 2 {
				return rectangle({l.x - 1.5, l.y - 1.5}, {l.x + 1.5, l.y + 0.5}) - rectangle({l.x - 1.5, l.y - 1.5}, {l.x - 0.5, l.y - 0.5}) - rectangle({l.x+0.5, l.y - 1.5}, {l.x + 1.5, l.y - 0.5});
			}
			match 3 {
				return rectangle({l.x - 1.5, l.y - 1.5}, {l.x + 1.5, l.y + 0.5}) - rectangle({l.x - 1.5, l.y - 1.5}, {l.x - 0.5, l.y - 0.5}) - rectangle({l.x+0.5, l.y - 0.5}, {l.x + 1.5, l.y + 0.5});

				
			}
			match 4 {
				return rectangle({l.x - 1.5, l.y - 1.5}, {l.x + 1.5, l.y + 0.5}) - rectangle({l.x - 1.5, l.y - 0.5}, {l.x - 0.5, l.y + 0.5}) - rectangle({l.x+0.5, l.y - 1.5}, {l.x + 1.5, l.y - 0.5});
				
				
			}
			match 5 {
				return rectangle({l.x - 1.5, l.y - 1.5}, {l.x + 1.5, l.y + 0.5}) - rectangle({l.x - 0.5, l.y - 1.5}, {l.x + 1.5, l.y - 0.5});
				
			}
			match 6 {
				return rectangle({l.x - 1.5, l.y - 1.5}, {l.x + 1.5, l.y + 0.5}) - rectangle({l.x - 1.5, l.y - 1.5}, {l.x + 0.5, l.y - 0.5});

			}
			
			
		}
	}
	
	point select_rotate(point p, int i) {
		switch i {
			match 0 {
				return location + {0,2};
			}
			match 1 {
				return location + {1,1};
			}
			match 2 {
				return location + {1.5, 1.5};
			}
			match 3 {
				return location + {1.5, 1.5};
			}
			match 4 {
				return location + {1.5, 1.5};
			}
			match 5 {
				return location + {1.5, 1.5};
			}
			match 6 {
				return location + {1.5, 1.5};
			}
			
		}
	}
	action pause_action {
		ask tetrimino {
			is_active <- false;
			do die;
		}
		do pause;
	}
	
	action create_tetri{
		int j <- rnd(6);
		stats[next_te] <- stats[next_te] + 1;
		create tetrimino {
			i <- next_te;
			
			
			color <-  tet_colors[i];
			
			location <- {15,10};
			if i < 2 {
				location <- {15,10};
				center_rotate<-{15,10};
			}
			else {
				location <- {15.5,10.5};
				center_rotate <- {15.5, 10.5};
			}
			shape <- world.select_shape(location, i);
			
			frequency <- world.freq(level);
			is_active <- true;
			ask tetrimino - self {
				do die;
			}
		}
		next_te <- j;
	}
}

grid cell height:30 width:20 {
	init {
		
	}
	aspect default {
		draw shape color:hsb( (level mod 360)/360.0 * 20, 0.2,0.7) border:#white;
	}
}

species tetrimino {
	
	int i;
	int rotation;
	bool is_active;
	float frequency;
	rgb color;
	
	point center_rotate;
	
	
	reflex down when:every(frequency) or down {
		if down {
			down <- false;
		}
		if (last(fixed).fixed_cells.keys collect (each.location + {0,-0.51})) all_match not(each intersects shape) {
						center_rotate <- center_rotate + {0,1};
						location <- location + {0,1};
						
					}
	}
	reflex right when:right {
		right <- false;
		ask tetrimino select each.is_active {
					if not (shape intersects polyline({20,0}, {20,30})) and ((last(fixed).fixed_cells.keys collect (each.location + {0.51,0}) + last(fixed).fixed_cells.keys collect (each.location + {-0.51,0})) all_match not(each intersects shape)) {
						center_rotate <- center_rotate + {1,0};
						location <- location + {1,0};
					}
				}
	}
	reflex left when:left {
		left <- false;
		ask tetrimino select each.is_active {
					if not (shape intersects polyline({10,0}, {10,30})) and ((last(fixed).fixed_cells.keys collect (each.location + {0.51,0}) + last(fixed).fixed_cells.keys collect (each.location + {-0.51,0})) all_match not(each intersects shape)){
						center_rotate <- center_rotate + {-1,0};
						location <- location + {-1,0};
					}
					
				}
		
	}
	reflex up when:up {
		up <- false;
		
					int lines_count;
					loop dist over:range(1,30) {
						lines_count <- dist;
						
						
						if not(dead(self)) {
							
						
						center_rotate <- center_rotate + {0,1};
						location <- location + {0,1};
						do kill_reflex;
						
						
					}
					else {
						score <- score + lines_count;
						break;
					}
				}
	}
	
	reflex q when:q {
		q <- false;
		
					geometry new_shape <- world.rotate_s(shape, center_rotate, location, 0);
					if not (new_shape intersects polyline({20.1,0}, {20.1,30}) or new_shape intersects polyline({9.9,0}, {9.9,30})) and ((last(fixed).fixed_cells.keys collect (each.location + {0.51,0}) + last(fixed).fixed_cells.keys collect (each.location + {-0.51,0})) all_match not(each intersects new_shape)){
						shape <- new_shape;
					}
				
	}
	reflex d when:d {
		d <- false;
		
					geometry new_shape <- world.rotate_s(shape, center_rotate, location, 1);
					if not (new_shape intersects polyline({20.1,0}, {20.1,30}) or new_shape intersects polyline({9.9,0}, {9.9,30})) and ((last(fixed).fixed_cells.keys collect (each.location + {0.51,0}) + last(fixed).fixed_cells.keys collect (each.location + {-0.51,0})) all_match not(each intersects new_shape)){
						shape <- new_shape;
					}
				
	}
	
	aspect default {
		draw shape color:color border:#black at: location;
		//draw circle(0.3) at:center_rotate color:#red;
	}
	
	
	
	action stop {
		ask fixed[0] {
			
			
			
			//self.fixed_cells <- self.fixed_cells + cell select (each.shape intersection myself.shape = nil ? false:(each.shape intersection myself.shape).area>0);
			
			//self.fixed_cells <- self.fixed_cells + ((cell overlapping first(tetrimino select (not(dead(each)) and each.is_active)).shape) select ((each.shape intersection first(tetrimino select (not(dead(each)) and each.is_active)).shape).area>0)) as_map (each::first(tetrimino select (not(dead(each)) and each.is_active)).color);
			
			tetrimino the_one <- first(tetrimino select each.is_active);
			list<cell> cell_touch <- cell select (each.shape touches the_one); 
			self.fixed_cells <- self.fixed_cells + ((cell-cell_touch) overlapping the_one) as_map (each::the_one.color);
			
			
			list<int> lines;
			loop j over:range(10,28) {
				if (cell select (each.grid_y = j and each.grid_x >= 10)) all_match(each in fixed_cells.keys) {
					
					lines << j;
					map<cell, rgb> rem <- (fixed_cells.pairs select (each.key.grid_y = j)) as_map (each.key::each.value);
					
					loop pa over:rem.pairs {
						fixed_cells <-fixed_cells - pa;
					}
					//fixed_cells <- fixed_cells - ((fixed_cells.pairs select (each.key.grid_y = j)) as_map (each.key::each.value));
					
					loop x over:range(10,19) {
						loop y over:range(j-1, 10) {
							if cell[x,y] in fixed_cells.keys {
								rgb cc <- fixed_cells[cell[x,y]];
								fixed_cells <- fixed_cells - (cell[x,y]::cc);
								fixed_cells <- fixed_cells + (cell[x,y+1]::cc);
							}
						}
					}
					
				}
			}
			count_lines_level <- count_lines_level + length(lines);
			switch length(lines) {
				match 1 {
					score <- score + 40;
				}
				match 2 {
					score <- score + 100;
				}
				match 3 {
					score <- score + 300;
				}
				match 4 {
					score <- score + 1200;
				}
				
			}
			if count_lines_level >=4 {
				level <- level+1;
				count_lines_level <- 0;
				ask agents - cell - world - fixed{
					do die;
				}
				ask world {do do_init;}
			}
			
			
			
			
		}
		is_active <- false;
		if location.y >11 { 
		ask world {
			do create_tetri;
		}
		do die;
		
		}
		else {
			
			ask world{do pause_action;}
		}
		
	}
	
	reflex kill_reflex {
		do kill_reflex;
	}
	
	action kill_reflex {
		
			bool intersec <- false;
			loop p over: last(fixed).fixed_cells.keys collect each.location {
				
				if shape intersects (p + {0,-0.5}){
					
					intersec <- true;
					break;
				}
			}
			 if intersec {
				do stop;
				
			}
		}
	
}

species fixed {
	map<cell,rgb> fixed_cells;
	init {
		loop i over:range(19) {
			fixed_cells << (cell[i,29]::#red);
		}
	}
	
	
	
	
	
	
	aspect default {
		loop c over: fixed_cells.pairs {
			
			draw c.key.shape color:c.value at:c.key.shape.centroid border:color;
		}
		/*loop p over: fixed.fixed_cells collect (each.location) {
			draw circle(0.3) at:p + {0,-0.5} color:#white;
		}*/
	}
}

experiment main {
	float minimum_cycle_duration <- 20#ms;
	output {
		layout 0 tabs:false editors: false;
		display main fullscreen:false type:2d antialias:false toolbar:false background:hsb( (level mod 360)/360.0 * 20, 0.2,0.7) axes:true{
		
			//grid cell;
			species cell;
			species tetrimino;
			species fixed;
			
			graphics scoreboard position:{0,0} size:{20,10} background:#black border:#cyan{
				draw shape color:#black border:#cyan;
				draw "Score" at:{14.5,7} font:font("Arial", 30, #bold+#italic) color:#white;
				draw get_score(score) at: {14,12} font:font("Arial", 30, #bold+#italic) color:#white;
				draw "Next" at: {10,25} font:font("Arial", 30, #bold+#italic) color:#white;
				draw rectangle({14,18}, {19,29}) wireframe:true color: #white width:0.5 ;
				draw world.select_shape({16.5, 23.9},next_te)*{0.9,2.7} color:tet_colors[next_te];
				draw "Level :  "+string(level) at: {2,25} font:font("Arial", 30, #bold+#italic)color:#white;
				draw "TETRIS" at:{1.5,14} font:font("Arial", 70, #bold)color:#white;
				
			}
			graphics scoreboard position:{0,10} size:{10,20} background:#black border:#cyan{
				draw shape color:#black border:#cyan;
				draw "Arrows to move" at: {3,2} font:font("Arial", 20, #bold+#italic) color:#white;
				draw "Q/D to rotate" at: {3,3.2} font:font("Arial", 20, #bold+#italic)color:#white;
				draw "R to restart" at: {3,4.4} font:font("Arial", 20, #bold+#italic)color:#white;
				draw "Statistics" at:{3, 7.3} font:font("Arial", 30, #bold+#italic)color:#white;
				loop i over:range(6) {
					point tr <- {1,3/4};
					if i=6 {
						tr <- {1,3/5};
					}
					point att <- {5, 10 + 3*i};
					if i<2 {
						att <- att+{0,-0.3};
					}
					draw world.select_shape(att,i)*{1,3/4} color:tet_colors[i];
					draw get_stat(stats[i]) at:{10, 10 + 3*i} font:font("Arial", 20, #bold+#italic)color:#white;
				}
				
				//draw "Statistics"
			}
			graphics scoreboard position:{0,29} size:{20,1} background:#black border:#cyan{
				draw shape color:#black border:#cyan;
				
			}
			
			
			event #arrow_left {
				left <- true;
				
			}
			event #arrow_right {
				right <- true;
				
			}
			event #arrow_down {
				if not has_started {
					has_started <- true;
					ask world{do resume;}
				}
				down <- true;
				
			}
			
			event #arrow_up {
				up <- true;
			}
			
			event "q" {
				q <- true;
				
			
			}
			event "d" {
				d <- true;
				
			
			}
			event "r" {
				ask simulation {do reload;}
				score <- 0;
				level<-1;
				
			}
			
		}
	}
}

/* Insert your model definition here */

