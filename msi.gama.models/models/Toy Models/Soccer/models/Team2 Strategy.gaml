/**
* Name: _team2strategy
* Author: Julien
* Description: This model contains one of the 2 team strategy. 
* This strategy is very stupid : when you have the ball, run to the ennemy goal, else run to the ball
* Tags: 
*/

model team2strategy

import "Soccer Base.gaml"

species player_stupidTeam parent:base_player {
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
	
	action defensive_behavior {	
		// very basic defensive behavior : run to the ball
		do run_to_ball;
	}
	
	action offensive_behavior {	
		// very basic offensive behavior : run to the ennemy goal, or shoot the ball when close enough from the ennemy goal.
		if ((possess_ball) and (distance_to_goal < 30)) {
			do shoot;
		}
		else {
			do run_to_ennemy_goal;
		}
	}
	
}

species stupidTeam parent:base_team {
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

