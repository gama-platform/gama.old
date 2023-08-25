/**
* Name: MoussaidModel
* Description: Pedestrian model proposed by: "Moussaïd, M., Helbing, D., & Theraulaz, G. (2011). 
* How simple rules determine pedestrian behavior and crowd disasters. Proceedings of the National Academy of Sciences, 108(17), 6884-6888."
* Based on the internal skeleton template. 
* Author: Patrick Taillandier
* Tags: Pedestrian
*/

model MoussaidModel

global {
	geometry shape <- rectangle(10.0, 5);
	float step <- 0.1;
	pedestrian focus;
	bool display_field_vision <- false parameter: true;
	geometry east ;
	geometry west; 
	
	float P_v0_mean <- 1.3 #m/#s parameter: "desired speed";
	float P_v0_std <- 0.2 #m/#s parameter: "desired speed";
	float P_teta0 <- 90.0 parameter: "max angle of vision"; //degrees
	float P_disc_factor <- 10.0 parameter: "discretisation factor for the vision angle";
	float P_tau <- 0.5 #s parameter: "reaction time";
	float P_dmax <- 8.0 #m parameter: "distance of vision";
	float P_k <- 1.0 * 10^3 parameter: "repulsion strength";
	int num_data <- 10 ;
	
	int new_arrving_per_s <- 2;
	
	init {
		do create_corridor;
			
		ask local_indicator overlapping (union(east + west) + 1.0) {
			is_entry <- true;
			col_speed<- #lightgray;
			col_compression<- #lightgray;
	
		} 
		ask wall {
			ask local_indicator overlapping self {
				is_wall <- true;
			}
		}
	}
	
	
	
	action create_corridor {
		create wall with: (shape: rectangle(5.0, 1.0) at_location {2.5, -0.5});
		create wall with: (shape: rectangle(5.0, 1.0) at_location {2.5, 4.5});
		
		create wall with: (shape: rectangle(5.0, 3.0) at_location {7.5, -0.5});
		create wall with: (shape: rectangle(5.0, 3.0) at_location {7.5, 4.5});
		
		east <- rectangle(shape.width / 100.0, shape.height)at_location {shape.width/200.0, location.y} - union(wall collect (each.shape + 0.5));
		west <- rectangle(shape.width /100.0, shape.height) at_location {shape.width * 199.0 /200.0, location.y} - union(wall collect (each.shape + 0.5)) ;
	
	}
	
	reflex add_people when: every(1#s) and (cycle < 200){
		geometry east_tmp <- copy(east);
		geometry west_tmp <- copy(west);
		create pedestrian number: new_arrving_per_s {
			location <- any_location_in(east_tmp);
			east_tmp <- east_tmp - shape;
			o <- any_location_in(west);
			color <- #blue;
		}
		create pedestrian number: new_arrving_per_s {
			location <- any_location_in(west_tmp);
			west_tmp <- west_tmp - shape;
			o <- any_location_in(east);
			
			color <- #red;
		}
	}
	reflex end_sim when: (cycle > 300) and empty(pedestrian) {
		do pause;
	}
}



species pedestrian {
	float m <- rnd(60.0,100.0);
	float shoulder_length <- m/320.0;
	float speed <- 0.0;
	geometry shape <- circle(shoulder_length);
	rgb color <- rnd_color(255);
	point o;
	float v0 <- gauss(P_v0_mean, P_v0_std);
	float teta0 <- P_teta0; //degrees
	float tau <- P_tau;
	float dmax <- P_dmax;
	float disc_factor <- P_disc_factor;
	float k <- P_k;
	float alpha0;
	float v;
	float dh;
	list<geometry> visu_ray;
	list<float> val_angles; 
	float heading;
	point acc<- {0,0};
	point vi <- {0,0};
	float c;
	
	user_command focus {
		focus <- self;
	}
	
	init {
		int num <- int(2 * teta0 / disc_factor);
		heading <- location towards o;
		loop i from: 0 to: num {
			val_angles <<  ((i * disc_factor) - teta0);
		}
	}
	
	reflex move_pedestrian {
		c <- 0.0;
		float dist_o <- location distance_to o;
		alpha0 <- location towards o;
		visu_ray <- [];
		dh <- #max_float;
		float dmin <- #max_float;
		
		float h0 <- copy(heading);
		
		loop a over:  val_angles {
			float alpha <- a + h0;
			list<float> r <- compute_distance(alpha,min(dist_o,dmax));
			
			if self = focus {write name + " " + sample(alpha) + " " + sample(r) +  " " + sample(alpha0) + " " + sample(cos(alpha0 - alpha)) ;}
			float dist <- r[0];
			if (dist < dmin ) {
				dmin <- dist;
				dh <- r[1];
				heading <- alpha;
			}
		}
		do manage_move(dist_o);
		if (self distance_to o) < 1.0 {
			do die;
		}
		
	}
	
	list<float> compute_distance (float alpha, float dist_o){
		float f_alpha <- f(alpha, dist_o);
		
		return [dist_o ^2 + f_alpha ^2 - 2 * dist_o *f_alpha * cos(alpha0 - alpha), f_alpha];
	}
	
	point force_repulsion_wall(wall w) {
		
		if (location intersects w) {
			float strength <- k * shoulder_length ;
			point pt_w <- (w.shape.contour closest_points_with location)[0];
			point vv <- {pt_w.x - location.x ,pt_w.y - location.y };
			float n <- norm(vv);
			return vv * (strength/n); 
		} else {
			float strength <- k * (shoulder_length - (location distance_to w));
			c <- c + strength;
			point pt_w <- (w closest_points_with location)[0];
			point vv <- {location.x - pt_w.x,location.y - pt_w.y };
			float n <- norm(vv);
			return vv * (strength/n); 
			
		}
	}
	point force_repulsion(pedestrian other) {
		float strength <- k * (other.shoulder_length + shoulder_length - (location distance_to other.location));
		c <- c + strength;
		point vv <- {location.x - other.location.x, location.y - other.location.y};
		float n <- norm(vv);
		return vv * (strength/n); 
	}
	
	
	float f(float alpha,float dmax_r) {
		
		geometry line <- line([location, location + ({cos(alpha), sin(alpha)} * dmax_r)]);
		list<pedestrian> ps <- (pedestrian overlapping line)  - self;
		list<wall> ws <- wall overlapping line;
		
		loop w over: ws {
			line <- line - w;
			if line = nil {return 0.0;}
		}
		loop p over: ps {
			line <- line - p;
			if line = nil {return 0.0;}
		}
		line <- line.geometries first_with (location in each.points);
		if line = nil {
			return 0.0;
		}
		line <- line - self;
		if line = nil {
			return 0.0;
		}
		visu_ray << line;
		return line.perimeter;	
		
		
		
	}
	
	point compute_sf_pedestrian {
		point sf <- {0.0,0.0};
		loop p over: pedestrian overlapping self {
			sf <- sf + force_repulsion(p);
		}
		
		return sf/m;
	}
	
	point compute_sf_wall {
		point sf <- {0.0,0.0};
		loop w over: wall overlapping self {
			sf <- sf + force_repulsion_wall(w);
		}
		
		return sf/m;
	}
	
	action manage_move (float dist_o) {
		
		float vdes <-  min(v0, dh/tau);
		point vdes_vector <-  { cos(heading),sin(heading)};
		vdes_vector <- vdes_vector * vdes;
		acc <- (vdes_vector - vi)/ tau +  compute_sf_pedestrian() +  compute_sf_wall();
		vi <- vi + (acc * step);
		location <- location + (vi * step);
		
		
	}
	
	aspect default {	
		if display_field_vision {
			loop l over: visu_ray {
				draw l color: color;
			}
		}
	
		draw  circle(shoulder_length) rotate: heading + 90.0 color: color;
	}
}

grid local_indicator cell_width: 0.5 cell_height: 0.5{
	bool is_wall <- false;
	bool is_entry <- false;
	float R <- 0.7;
	float R2 <- 0.7 ^2;
	list<float> local_speeds;
	list<float> local_compressions;
	rgb col_speed<- #black;
	rgb col_compression<- #black;
	
	reflex indicator_computation {
		do compute_local_indicator;
		if not is_wall and not is_entry{
			float local_speed <- mean(local_speeds);
			float local_compression <- mean(local_compressions);
			col_speed <- rgb(255 * (1 -local_speed), 255 * local_speed, 0.0  );
			col_compression <- rgb(255 * (local_compression), 255 * (1.0 - local_compression), 0.0  );
		}
	
		
	}
	action compute_local_indicator {
		float sum_f;
		float sum_s;
		float sum_c;
		loop p over: pedestrian {
			float d <- location distance_to p.location;
			float f_v <- f(d);	
			sum_f <- sum_f + f_v;
			sum_s <- sum_s + (f_v * norm(p.vi));
			sum_c <- sum_c + (f_v * p.c);
		}
		local_speeds << sum_s = 0 ? 1.0 : (sum_s/sum_f/P_v0_mean) ;
		local_compressions << sum_c = 0 ? 0.0 : (sum_c/sum_f/(P_k/3.0)) ;
		if length(local_speeds) > num_data {
			local_speeds >> first(local_speeds);
			local_compressions >> first(local_compressions);
		}
			
	}
	float f(float d) {
		return 1/(#pi * R2) * exp(-(d^2) / R2);
	}
	
	aspect speed_val {
		draw shape color: col_speed;
	}
	
	aspect compression_val {
		draw shape color: col_compression;
	}
	
	
}

species wall {
	aspect default {	
		draw shape  color: #black;
	
	}
}


experiment corridor_xp type: gui {
	float minimum_cycle_duration <- 0.02;
	output {
		display map type: 3d axes: false{
			graphics "east area" {
				draw east color: #blue;
			}
			graphics "west area" {
				draw west color: #red;
			}
			
			species wall;
			species pedestrian;
		}
		
		display local_speed type:2d antialias:false{
			species local_indicator aspect: speed_val;
		}
		
		display local_compression type:2d antialias:false{
			species local_indicator aspect: compression_val;			
		}
	}
}
