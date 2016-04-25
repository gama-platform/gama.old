/**
* Name: team1strategy
* Author: Julien
* Description: 
* Tags: Tag1, Tag2, TagN
*/

model team1strategy

import "./soccer_base.gaml"

species player_team1 parent:base_player {
	
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
	
	reflex update_influence_area when:cycle>0 {
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
			x_ratio <- 0.5 - 0.3 * cos( (y_ratio-0.5)*90 ); // the "side" wings are more marked if the player is in the center of the field.
		}
		else {
			x_ratio <- 0.5 + 0.3 * cos( (y_ratio-0.5)*90 );
		}
		influence_area <- circle(15,{getXPos(x_ratio),getYPos(y_ratio)});
	}
	
	action defensive_behavior {	
		if ((self = team.closest_player_to_ball) or (self = team.called_player)) {
			do run_to_ball;
		}
		else {
			if ( length(self.ennemy_team.players where (each intersects influence_area)) != 0 ) {
				do mark_player( first(1 among (self.ennemy_team.players where (each intersects influence_area))));
			}
			else {
				do run_to(influence_area.location);
			}
		}
	}
	
	action offensive_behavior {	
		if (team.player_with_ball = self) {
			if (self.distance_to_goal < 20) {
				do shoot;
			}
			if ( (position_mark = max( team.players collect (each.position_mark) )) or (distance_to_closest_ennemy_player > 5) )
			{
				do run_to_ennemy_goal;
			}
			else {
				do pass_the_ball( first(team.players where (each.position_mark = max( team.players collect (each.position_mark)) ) ) );
			}
		}
		else if (self = team.closest_player_to_ball) {
			do run_to_ball;
		}
		else if (self = team.called_player) {
			
		}
		else {
			do run_to({influence_area.location.x,influence_area.location.y+getYPos(20.0)});
		}
	}
	
}

species team1 parent:base_team {
	list<point> player_init_position <- [{20,20},{50,20},{80,20},{30,50},{70,50},{50,70},{30,90},{50,90},{70,90}];
	
	aspect offside_pos {
		draw polyline([{0,offside_pos},{90,offside_pos}]) color:#blue;
	}
}

