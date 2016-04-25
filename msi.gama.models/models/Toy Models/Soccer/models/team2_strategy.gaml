/**
* Name: team1strategy
* Author: Julien
* Description: 
* Tags: Tag1, Tag2, TagN
*/

model team1strategy

import "./soccer_base.gaml"

species player_team2 parent:base_player {
	
	float position_mark <- 0.0 update: 100 - distance_to_goal - 20*number_of_ennemy_player_in_range;		
	
	action defensive_behavior {	
		do run_to_ball;
	}
	
	action offensive_behavior {	
		if ((team.player_with_ball = self) and (self.distance_to_goal < 20)) {
			do shoot;
		}
		else {
			do run_to_ennemy_goal;
		}
	}
	
}

species team2 parent:base_team {
	list<point> player_init_position <- [{20,20},{50,20},{80,20},{30,50},{70,50},{50,70},{30,90},{50,90},{70,90}];
	
	aspect offside_pos {
		draw polyline([{0,offside_pos},{90,offside_pos}]) color:#blue;
	}
}

