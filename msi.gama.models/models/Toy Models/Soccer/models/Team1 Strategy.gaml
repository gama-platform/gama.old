/**
* Name: _team1strategy
* Author: Julien
* Description: This model contains one of the 2 team strategy. 
* This strategy is quite advanced, attributing role for each player, with a custom influence_area and a custom position_mark.
* Tags:
*/

model team1strategy

import "Soccer Base.gaml"

species player_intelligentTeam parent:base_player {
	// READ ONLY ATTRIBUTES :
	// position : can be "front" or "back".
	// players : list of all the players of the team.
	// game
	//closest_player_to_ball
	// called_player : the player called for a pass
	// possess_ball : true or false
	// player_with_ball : player currently with the ball
	
	// READ AND WRITE ATTRIBUTES :
	// position_mark
	// status : the current status of the player (can be useful to build the model)
	// influence_area : the area of interest of the player. By default, this area is a circle 15m diameter centered in the player location.
	
	float position_mark <- 0.0 update: location.y - 20*number_of_ennemy_player_in_range + self.distance_to_closest_ennemy_player;	
	string role; // a value between "defense", "mid" and "attack".
	string wing; // a value between "left", "center" and "right".
	geometry influence_area <- circle(15,init_pos);
	
	float defense_mid_pos <- 30.0; // the y percent chosed to separate the defense from the mid position.
	float mid_attack_pos <- 60.0; // the y percent chosen to separate the mid from the attack position.
	
	init {
		// set the role of the player (between "defense", "mid" and "attack").
		if ( init_pos_in_percent.y < defense_mid_pos ) {
			role <- "defense";
		}
		else if ( (init_pos_in_percent.y > mid_attack_pos ) ) {
			role <- "attack";
		}
		else {
			role <- "mid";
		}
		// set the wing of the player (between "left", "center" and "right")
		if ( init_pos_in_percent.x < 40 ) {
			wing <- "left";
		}
		else if ( init_pos_in_percent.x > 60 ) {
			wing <- "right";
		}
		else {
			wing <- "center";
		}
	}
	
	action update_influence_area {
		status <- wing + " " + role;
		float y_ratio;
		if (role = "defense") { // defense position from 0% to 70% from the own goal, multiplied by the percentage of advancement of the ball
			y_ratio <- 0.7 * ball_advancement;
		}
		if (role = "mid") { // mid position from 15% to 85% from the own goal, multiplied by the percentage of advancement of the ball
			y_ratio <- 0.15 + 0.7 * ball_advancement;
		}
		if (role = "attack") { // attack position from 30% to 100% from the own goal, multiplied by the percentage of advancement of the ball
			y_ratio <- 0.3 + 0.7 * ball_advancement;
		}
		float x_ratio;
		if (wing = "center") {
			x_ratio <- 0.5;
		}
		else if (wing = "left") {
			x_ratio <- 0.5 - 0.3 * cos( (y_ratio-0.5)*120 ); // the "side" wings are more marked if the player is in the center of the field.
		}
		else {
			x_ratio <- 0.5 + 0.3 * cos( (y_ratio-0.5)*120 );
		}
		influence_area <- circle(15,{getXPos(x_ratio),getYPos(y_ratio)});
	}
	
	action defensive_behavior {	
		do update_influence_area;
		// advanced defensive behavior
		// run to the ball if the player is the closest player from the ball.
		if ((self = team.closest_player_to_ball) or (self distance_to ball < 5)) {
			status <- getStatus("run to ball");
			do run_to_ball;
		}
		else {
			// if there is an ennemy player in the influence area, mark the player.
			if ( length(self.ennemy_team.players where (each intersects influence_area)) != 0 ) {
				base_player marked_player <- first(1 among (self.ennemy_team.players where (each intersects influence_area)));
				status <- getStatus("mark player "+marked_player);
				do mark_player( marked_player );
			}
			// if there is no ennemy player in the influence area, stay in influence area.
			else {
				status <- getStatus("run to influence area");
				do run_to(influence_area.location);
			}
		}
	}
	
	action offensive_behavior {	
		do update_influence_area;
		// advanced offensive behavior
		if (possess_ball) {
			// if the player has the ball and is close enough to the ennemy goal, shoot.
			if (distance_to_goal < 35 and flip(1/(0.1+(self.distance_to_goal/10)^2))) {
				status <- getStatus("shoot the ball");
				do shoot;
			}
			// if the player has the ball and is in a safe position, run to the ennemy goal.
			else if ( (position_mark = max( team.players collect (each.position_mark) )) or (distance_to_closest_ennemy_player > 2) )
			{
				status <- getStatus("run to ennemy goal");
				do run_to_ennemy_goal;
			}
			// if the player has the ball but is in a dangerous situation, pass the ball to another player.
			else {
				base_player target_player <- first(team.players where (each.position_mark = max( team.players collect (each.position_mark)) ) );
				status <- getStatus("pass the ball to "+target_player);
				do pass_the_ball_ahead ( target_player,10.0 );
			}
		}
		// if the player has not the ball but is the called player, run to the ball.
		else if (self = team.called_player) {
			status <- getStatus("run to ball");
			do run_to_ball;
			status <- "called player";
		}
		// else, run to influence area.
		else {
			status <- getStatus("run to influence area");
			do run_to(influence_area.location);
		}
	}
	
	string getStatus(string str) {
		return wing + " " + role + "| action : " + str;
	}
	
}

species intelligentTeam parent:base_team {
	// READ ONLY ATTRIBUTES :
	// position : can be "front" or "back".
	// players : list of all the players of the team.
	// game.
	// closest_player_to_ball.
	// called_player : the player called for a pass.
	// possess_ball : true when a player of the team possess the ball.
	// player_with_ball
	
	// READ AND WRITE ATTRIBUTES :
	// player_init_position
	
	// initial position of the player in percentage : for each point,
	//    the first value corresponds to the percentage from left to right (0 for the point the most in the left side)
	//    the second value corresponds to the percentage from the goal position to the mid position (0 for the goal position)
	list<point> player_init_position <- [{20,20},{50,20},{80,20},{30,50},{70,50},{50,70},{30,90},{50,90},{70,90}];
}

