/**
* Name: Soccer Game
* Author: Julien
* Description: This model shows how can we make an easy simulation of collective games, such as soccer. 
* 
* Each player have an offensive and a defensive position, and a behavior associated. Each player of the defensive team 
* can either search to catch the ball or mark an other player (offensive player) according to his defensive position.
* 
* Each player of the offensive team can either run to its offensive place (when he does not have the ball), run with the balloon / 
* pass the ball / try to score a goal (when he has the ball). By changing the different parameters, you can see the concequences 
* in real time. 
* Tags: sport
*/

model soccer

global {
	geometry shape <- rectangle(120#m,90#m);
	float red_players_speed <- 1.0;
	float blue_players_speed <- 1.0;
	float red_size_play_area <- 30.0 min:1.0 max:100.0;
	float blue_size_play_area <- 30.0 min: 1.0 max:100.0;
	float previous_red_size_play_area <- red_size_play_area; // just to check if the parameter value has been changed
	float previous_blue_size_play_area <- blue_size_play_area; // just to check if the parameter value has been changed
	float red_collective_play <- 0.5 min:0.0 max:1.0;
	float blue_collective_play <- 0.5 min:0.0 max:1.0;
	float red_recuperation_ability <- 0.5 min:0.0 max:1.0;
	float blue_recuperation_ability <- 0.5 min:0.0 max:1.0;
	
	int red_score <- 0;
	int red_possession <- 0;
	int nb_red_pass <- 0;
	int nb_red_pass_succeed <- 0;
	int nb_red_interception <- 0;
	int blue_score <- 0;
	int blue_possession <- 0;
	int nb_blue_pass <- 0;
	int nb_blue_pass_succeed <- 0;
	int nb_blue_interception <- 0;
	
	bool show_status <- false;
	bool show_area <- false;
	bool show_marked_player <- false;
	bool show_ball_indicator <- false;
	
	bool play_with_offside <- true;
	
	list<point> bluePlayerPosition <- [{8,10}, {8,80}, {10,30}, {10,60},
		{30,25}, {28,45}, {30,65},
		{50,25}, {50,65}, {55,45}
	];
	
	list<point> redPlayerPosition <- [{120-8,10}, {120-8,80}, {120-10,30}, {120-10,60},
		{120-30,25}, {120-28,45}, {120-30,65},
		{120-50,25}, {120-50,65}, {120-55,45}
	];
	
	ball ball_agent;
	player closest_red_player_from_the_ball;
	player closest_blue_player_from_the_ball;
	player called_player;
	string team_possession <- "";
	float blue_offside_pos <- 120.0;
	float red_offside_pos <- 0.0;
	
	init {
		loop pos over:redPlayerPosition {
			create player with:[team::"red", location::pos];
		}
		loop pos over:bluePlayerPosition {
			create player with:[team::"blue", location::pos];
		}
		create ball with:[location::location] returns:ball_agt;
		ball_agent<-ball_agt at 0;
		create goal with:[location::{0,location.y}, team::"blue"];
		create goal with:[location::{120,location.y}, team::"red"];
	}
	
	reflex update {
		float shortest_distance <- 100.0;
		float offside_pos <- 0.0;
		ask player where (each.team = "red") {
			if (distance_to_ball < shortest_distance and inactivity_time=0) {
				closest_red_player_from_the_ball <- self;
				shortest_distance <- distance_to_ball;
			}
			if (location.x > offside_pos) {
				offside_pos <- location.x;
				red_offside_pos <- offside_pos;
			}
		}
		if (not play_with_offside) {
			red_offside_pos <- 120.0;
		}
		
		shortest_distance <- 100.0;
		offside_pos <- 100.0;
		ask player where (each.team = "blue") {
			if (distance_to_ball < shortest_distance and inactivity_time=0) {
				closest_blue_player_from_the_ball <- self;
				shortest_distance <- distance_to_ball;
			}
			if (location.x < offside_pos) {
				offside_pos <- location.x;
				blue_offside_pos <- offside_pos;
			}
		}
		if (not play_with_offside) {
			blue_offside_pos <- 0.0;
		}
		
		if (previous_red_size_play_area != red_size_play_area) {
			ask area where (each.team = "red") {
				do update_size;
			}
			previous_red_size_play_area <- red_size_play_area;
		}
		if (previous_blue_size_play_area != blue_size_play_area) {
			ask area where (each.team = "blue") {
				do update_size;
			}
			previous_blue_size_play_area <- blue_size_play_area;
		}
		if (team_possession = "blue") {
			blue_possession <- blue_possession + 1;
		}
		if (team_possession = "red") {
			red_possession <- red_possession + 1;
		}
	}
	
	action reinit_phase {
		ask player {
			location <- init_pos;
			previous_pos <- init_pos;
		}
		ball_agent.location <- location;
		ball_agent.destination <- location;
		ball_agent.speed <- 0.0;
	}
}

species player skills:[moving] {
	string team <- ""; // value : "blue" (left side) or "red" (right side)
	bool possess_ball <- false;
	int inactivity_time <- 0 update:(inactivity_time<=0) ? 0 : inactivity_time-1;
	int seed <- rnd(100);
	point init_pos;
	point previous_pos;
	
	// strategy
	area defensive_pos;
	area offensive_pos;
	player marked_player;
	float collective_mark <- 0.5 update:(team="red") ? red_collective_play : blue_collective_play;
	float recuperation_mark <- 0.5 update:(team="red") ? red_recuperation_ability : blue_recuperation_ability;
	
	string status <- "";
	
	// moving attributes
	float running_speed_without_ball <- 0.8 update:(team="red") ? red_players_speed*0.8 : blue_players_speed*0.8;
	float running_speed_with_ball <- 0.6 update:(team="red") ? red_players_speed*0.5 : blue_players_speed*0.5;
	float speed <- 0.0 max:1.2;
	point velocity <- {0,0};
	
	float distance_to_closest_ennemy <- 100.0;
	float distance_to_ennemy_goal <- 100.0 update:self distance_to ( (goal where (each.team != team)) at 0);
	float distance_to_ball <- 100.0 update:self distance_to ball_agent;
	
	init {
		init_pos <- location;
		previous_pos <- location;
		create area with:[location::init_pos, team::self.team, position::init_pos] returns:def_pos;
		defensive_pos <- def_pos at 0;
		point offensivePos <- {(team="red") ? init_pos.x-60 : init_pos.x+60,init_pos.y};
		create area with:[location::offensivePos, team::self.team, position::offensivePos] returns:off_pos;
		offensive_pos <- off_pos at 0;
	}
	
	reflex update_status {
		distance_to_closest_ennemy <- 100.0;
		loop pl over:player where (each.team != team) {
			float distance_to_ennemy <- self distance_to pl;
			if (distance_to_ennemy < distance_to_closest_ennemy) {
				distance_to_closest_ennemy <- distance_to_ennemy;
			}
		}
	}
	
	reflex defensive_behavior when:team_possession != team {
		// the ball is not possessed by the team.
		do apply_inertia;
		if (self = closest_red_player_from_the_ball or self = closest_blue_player_from_the_ball) {
			do run_to_ball;
		}
		else {
			do defensive_move;
		}
	}
	
	reflex offensive_behavior when:team_possession = team {
		do apply_inertia;
		if (possess_ball) {
			do run_with_ball;
			if (distance_to_ennemy_goal < 30 and flip(1/(distance_to_ennemy_goal*distance_to_ennemy_goal/10+1))) {
				// shoot !
				do kick_ball_to_goal;
			}
			else {
				// pass !
				if (distance_to_closest_ennemy < 5) {
					if (flip(collective_mark)) {
						do pass_the_ball;
					}
				}
				else if flip(collective_mark/50) {
					do pass_the_ball;
				}
			}
		}
		else if (ball_agent.belong_to_team = "" and
			(self = closest_red_player_from_the_ball or self = closest_blue_player_from_the_ball
			or self = called_player)
		) {
			do run_to_ball;
		}
		else {
			do offensive_move;
		}
	}
	
	reflex choose_player_to_mark when:(cycle mod 20=seed or cycle=0) {
		if ( not empty(player where(each.offensive_pos intersects defensive_pos)) ) {
			marked_player <- 1 among (player where((each.offensive_pos intersects defensive_pos) and (each.team != team))) at 0;
		}
	}
	
	action apply_inertia {
		point prev_pos <- location;
		point inertia_vect <- {(location.x-previous_pos.x)*0.8,(location.y-previous_pos.y)*0.8};
		float max_inertia <- running_speed_without_ball;
		if (norm(inertia_vect) > max_inertia) {
			float inertia_x <-  sqrt(abs(max_inertia*max_inertia-inertia_vect.y*inertia_vect.y));
			float inertia_y <-  sqrt(abs(max_inertia*max_inertia-inertia_vect.x*inertia_vect.x));
			inertia_x <- (inertia_vect.x < 0) ? -inertia_x : inertia_x;
			inertia_y <- (inertia_vect.y < 0) ? -inertia_y : inertia_y;
			inertia_vect <- {inertia_x,inertia_y};
		}
		location <- location + inertia_vect;
		previous_pos <- prev_pos;
	}
	
	action run_to_ball {
		point targetPos;
		if (ball_agent.ball_direction intersects circle(1)) {
			targetPos <- ball_agent.location;
		}
		else {
			targetPos <- (ball_agent.ball_direction closest_points_with self) at 0;
		}
		do goto with:[target::targetPos, speed::running_speed_without_ball];
		
		status <- "run to the ball";
		
		// if close enough, catch the ball
		if (location distance_to ball_agent.location < 1.5#m) {
			if (self = called_player) {
				do take_ball;
			}
			else if (ball_agent.belong_to_team = "") {
				if flip(1/(ball_agent.speed*recuperation_mark+1)) {
					do take_ball;
				}
			}
			else {
				if (team_possession = team) {
					// result of a long pass for instance
					if (flip(recuperation_mark*1.5)) {
						do take_ball;
					}
				}
				else {
					// interception of the ball
					if (flip(recuperation_mark*0.8)) {
						do take_ball;
					}
				}
			}
		}
	}
	
	action run_with_ball {
		status <- "run with the ball";
		point goal_pos;
		ask goal {
			if (self.team != myself.team) {
				goal_pos <- location;
			}
		}
		do goto with:[target::goal_pos, speed::running_speed_with_ball];
		ball_agent.location <- location;
	}
	
	action offensive_move {
		// try to reach an offensive postion
		point target_location;
		geometry possible_pos <- (team="red") ? world inter (rectangle({blue_offside_pos,0},{120,90}))
		: world inter (rectangle({0,0},{red_offside_pos,90}));
		
		
		// check if the player is in the field
		if (location.y < 0) {
			target_location <- {location.x,90};
		}
		else if (location.y > 90) {
			target_location <- {location.x,0};
		}
		// check if the player is in offside position
		else if ( (location.x > red_offside_pos and team = "blue") or (location.x < blue_offside_pos and team = "red")) {
			target_location <- (team="red") ? {120,location.y} : {0,location.y};
			status <- "run to a non offside position";
		}
		else {
			status <- "run to a offensive place";
			if (not (possible_pos intersects offensive_pos)) {
				target_location <- (team="red") ? {blue_offside_pos,location.y} : {red_offside_pos,location.y};
			}
			else
			{
				target_location <- any_location_in(offensive_pos inter possible_pos);
			}
		}
		do goto target:target_location speed:running_speed_without_ball;
	}
	
	action defensive_move {
		// try to mark an ennemy player
		status <- "mark ennemy player";
		if (not (marked_player = nil)) {
			do goto with:[target::marked_player.location+((team="red")?{2+rnd(5.0),rnd(2.0)-1} : {-2-rnd(5.0),rnd(2.0)-1}), speed::running_speed_without_ball];
		}
	}
	
	action kick_ball_to_goal {
		do loose_ball;
		ask ball_agent {
			do shooted speed_atr:4.0 target_position:((goal where (each.team != myself.team)) at 0).location;
		}
		inactivity_time <- 20;
	}
	
	action pass_the_ball {
		float wisest_choice_mark <- -100.0;
		player wisest_target;
		ask player  where(each.team = team and each != self and (self distance_to each > 15)) {
			int number_of_ennemies_on_range <- length(player at_distance 5 where (each.team != myself.team));
			float wise_choice_mark <- 100-distance_to_ball+number_of_ennemies_on_range*10-2*distance_to_ennemy_goal;
			if (wise_choice_mark > wisest_choice_mark) {
				wisest_target <- self;
				wisest_choice_mark <- wise_choice_mark;
			}
		}
		if (wisest_choice_mark > -100.0) {
			// a target has been found
			do loose_ball;
			ask ball_agent {
				do shooted target_position:wisest_target.location speed_atr:wisest_target.distance_to_ball/8;
			}
			called_player <- wisest_target;
			inactivity_time <- 20;
			if (team = "red") {nb_red_pass <- nb_red_pass+1;}
			else {nb_blue_pass <- nb_blue_pass+1;}
		}
	}
	
	action take_ball {
		if (ball_agent.belong_to_team != "" and ball_agent.belong_to_team != team) {
			ask ball_agent.belong_to_player {
				do loose_ball;
			}
		}
		team_possession <- team;
		possess_ball <- true;
		ball_agent.belong_to_team <- team;
		ball_agent.belong_to_player <- self;
		ball_agent.speed <- 0.0;
		if (self = called_player) {
			if (team = "red") {nb_red_pass_succeed <- nb_red_pass_succeed+1;}
			else {nb_blue_pass_succeed <- nb_blue_pass_succeed+1;}
		}
		else {
			if (team = "red") {nb_red_interception <- nb_red_interception+1;}
			else {nb_blue_interception <- nb_blue_interception+1;}
		}
		called_player <- nil;
	}
	
	action loose_ball {
		possess_ball <- false;
		ball_agent.belong_to_team <- "";
	}
	
	aspect base {
		draw circle(1) color:(team="red") ? #red : #blue;
		if (show_status) {
			draw string(status);
			if (marked_player != nil) {
				draw polyline([location,marked_player.location]);
			}
		}
		if (marked_player != nil and show_marked_player) {
			draw polyline([location,marked_player.location]) end_arrow:1 color:#chartreuse;
		}
	}
}

species area {
	string team <- "";
	point position;
	
	init {
		shape <- ((team="red") ? square(red_size_play_area) : square(blue_size_play_area)) inter world;
	}
	
	action update_size {
		location <- position;
		shape <- ((team="red") ? square(red_size_play_area) : square(blue_size_play_area)) inter world;
	}
	
	aspect base {
		if (show_area) {
			draw shape color:rgb(128,128,128,50) border:(team="red") ? rgb(256,0.0,0.0,100) : rgb(0.0,0.0,256.0,100);
		}
	}
}

species ball skills:[moving]{
	string belong_to_team <- "";
	player belong_to_player;
	float speed <- 0.0 update:speed*0.9;
	list<point> nextPlace;
	geometry ball_direction;
	reflex update {
		float future_speed <- speed;
		point tmpPos<-location;
		loop i from:0 to:10 {
			tmpPos <- {tmpPos.x+cos(heading)*speed,tmpPos.y+sin(heading)*speed};
			future_speed <- future_speed*0.9;
		}
		ball_direction <- line([location,tmpPos]);
		if ((location.x+cos(heading)*speed) > 120) {
			blue_score <- blue_score + 1;
			ask world {
				do reinit_phase;
			}
		}
		if ((location.x+cos(heading)*speed) < 0) {
			red_score <- red_score + 1;
			ask world {
				do reinit_phase;
			}
		}
		do wander amplitude:1.0;
	}
	action shooted (point target_position, float speed_atr) {
		speed <- speed_atr;
		do goto target:target_position;
	}
	aspect base {
		draw circle(0.5) color:#white border:#black;
		if (show_ball_indicator) {
			draw ball_direction end_arrow:1 color:#chartreuse;
			if (team_possession="red") {
				draw circle(0.5) color:#darkred border:#black;
			}
			else if (team_possession="blue") {
				draw circle(0.5) color:#darkblue border:#black;
			}
		}
	}
}

species goal {
	string team <- "";
	aspect base {
		draw rectangle(0.1,7.32) color:#black;
	}
}


experiment match type:gui {
	parameter "blue running speed" var:blue_players_speed category:"Blue Team";
	parameter "red running speed" var:red_players_speed category:"Red Team";
	// speed of players when they run. Note that a player which have the ball will run at 50% of his max capacity, and he will run at 80% of his max capacity if he does not have the ball.
	
	parameter "blue collective play" var:blue_collective_play category:"Blue Team";
	parameter "red collective play" var:red_collective_play category:"Red Team";
	// propention of giving the ball to an other player of the team. If the value is 0, the player will never pass the ball.
	
	parameter "blue recuperation ability" var:blue_recuperation_ability category:"Blue Team";
	parameter "red recuperation ability" var:red_recuperation_ability category:"Red Team";
	// ability to catch the ball when no player has it and when a player of the other team has it. 1 for very good hability, 0 for very poor hability.
	
	parameter "blue play area size" var:blue_size_play_area category:"Blue Team";
	parameter "red play area size" var:red_size_play_area category:"Red Team";
	// change the size of the area of each player. The bigger this area is, the less players will maintains their position.
	
	parameter "display status" var:show_status category:"Display";
	// displays or not the current status of the player (his intentions)
	
	parameter "display play area" var:show_area category:"Display";
	// displays or not the offensive and defensive area of each player. The bigger this area is, the less players will maintains their position.
	
	parameter "display marked player" var:show_marked_player category:"Display";
	// displays or not an arrow between the player that are marked each other.
	
	parameter "display ball indicators" var:show_ball_indicator category:"Display";
	// changes the color of the ball if the ball is possessed by the blue or the red team. Display also the direction of the ball when it has been shooted.
	
	parameter "play with offside position" var:play_with_offside category:"Rules";
	// play with or without the "offside" rule.
	
	output {
		display "soccer_field" type:2d{
			// display the field.
			image "../images/soccer_field.png";
			species player aspect:base;
			species ball aspect:base;
			species goal aspect:base;
			species area aspect:base;
			graphics "score_display" {
				draw "Blue side - "+string(blue_score) color:#blue at: {10,5} font: font("Helvetica", 18 * #zoom, #bold) perspective:true;
				draw string(red_score)+" - Red side" color:#red at: {90,5} font: font("Helvetica", 18 * #zoom, #bold) perspective:true;
				if (show_status) {
					draw line([{red_offside_pos,0},{red_offside_pos,90}]) color:#red;
					draw line([{blue_offside_pos,0},{blue_offside_pos,90}]) color:#blue;
				}
			}
		}
		display "match_stats"  type: 2d {
			// display some statistics about the game
			chart "ball_possession" type:pie position:{0,0} size:{0.5,0.5} {
				data "Red possession" value:red_possession color:#red;
				data "Blue possession" value:blue_possession color:#blue;
			}
			chart "red pass" type:pie position:{0,0.5} size:{0.5,0.5} {
				// note that a pass is said "successful" if the player that has the ball was the player called initially. A "failed pass" does not necessary means that the team have lost the ball.
					data "Red pass succeed" value:(nb_red_pass = 0) ? 0 : float(nb_red_pass_succeed)/float(nb_red_pass) color:#red;
					data "Red pass failed" value:(nb_red_pass = 0) ? 0 : 1-float(nb_red_pass_succeed)/float(nb_red_pass) color:#darkred;
			}
			chart "blue pass" type:pie position:{0.5,0.5} size:{0.5,0.5} {
				// note that a pass is said "successful" if the player that has the ball was the player called initially. A "failed pass" does not necessary means that the team have lost the ball.
				data "Blue pass succeed" value:(nb_blue_pass = 0) ? 0 : float(nb_blue_pass_succeed)/float(nb_blue_pass) color:#blue;
				data "Blue pass failed" value:(nb_blue_pass = 0) ? 0 : 1-float(nb_blue_pass_succeed)/float(nb_blue_pass) color:#darkblue;
			}
			chart "number pass" type:series position:{0.5,0} size:{0.5,0.5} {
				data "Number pass red" value:nb_red_pass color:#red;
				data "Number pass blue" value:nb_blue_pass color:#blue;
			}
		}
	}
}