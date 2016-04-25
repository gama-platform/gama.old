/**
* Name: soccerexpe
* Author: Julien
* Description: 
* Tags: Tag1, Tag2, TagN
*/

model soccerexpe

import "./team1_strategy.gaml"
import "./team2_strategy.gaml"

global {
	geometry shape <- rectangle(90#m,120#m);
	
	rgb back_color_team <- #blue;
	rgb front_color_team <- #red;
	init {
		create soccer_game with:(back_color_team::back_color_team,front_color_team::front_color_team) returns:soccerGame;
		create team1 with:(game:first(soccerGame),position:"back");
		create team2 with:(game:first(soccerGame),position:"front");
		loop pos over:first(team1).player_init_position {
			// compute the "real position" of each player according to the percentage given in "player_init_position"
			point real_pos <- (first(team1).position = "back") ? {90-pos.y/100*90,pos.x/100*60} : {pos.y/100*90,120-pos.x/100*60};
			create player_team1 with:(team:first(team1),game:first(team1).game,location:real_pos);
		}
		loop pos over:first(team2).player_init_position {
			// compute the "real position" of each player according to the percentage given in "player_init_position"
			point real_pos <- (first(team2).position = "back") ? {90-pos.y/100*90,pos.x/100*60} : {pos.y/100*90,120-pos.x/100*60};
			create player_team1 with:(team:first(team2),game:first(team2).game,location:real_pos);
		}
	}
}

experiment my_experiment type:gui {
	output {
		display "soccer_field"  {
			// display the field.
			image "../images/soccer_field2.png";
			species player_team1 aspect:player;
			species player_team2 aspect:player;
		}
	}
}