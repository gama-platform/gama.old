/**
* Name: soccerbase
* Author: Julien
* Description: 
* Tags: Tag1, Tag2, TagN
*/

model soccerbase

species soccer_game {
	rgb back_color_team <- #blue;
	rgb front_color_team <- #red;
}

species base_player {
	base_team team;
	soccer_game game;
	
	aspect player {
		draw circle(1) color:(team.position = "back") ? game.back_color_team : game.front_color_team;
	}
}

species base_team {
	string position; // can be "front" or "back".
	list<base_player> players;
	soccer_game game;
	
	list<point> player_init_position;
}