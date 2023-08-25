/**
* Name: Soccer game (Various strategies)
* Author: Julien
* Description: This model shows a game between two teams, with the same of with different strategies :
* 
* * The "stupidTeam" has a very basic strategy : when a player has the ball, he runs to the ennemy goal, he runs to the ball otherwise.
* 
* * The "intelligentTeam" is composed of players that each one have a strategical place wich they are able to keep. They also do pass when 
* they are in dangerous position, to the "safer" player.
* 
* This model is designed to show how, with the same set of rules and some predefined actions, we can build a strategy of a team. You are free to change 
* the imported files "team1_strategy.gaml" and "team2_strategy.gaml", using the read-only and the read-write attributes of the mother species.
* Tags: sport, inheritence
*/

model soccerexpe

// import the 2 strategies
import "Team1 Strategy.gaml"
import "Team2 Strategy.gaml"

global {
	// set the size of the environment
	geometry shape <- rectangle(90#m,120#m);
	
	bool show_info <- false;
	
	rgb back_color_team <- #blue;
	rgb front_color_team <- #red;
}

species info_player1 mirrors:player_intelligentTeam {
	// mirror species that shows additionnal information about the player
	point location <- target.location update:target.location;
	string status <- target.status update:target.status;
	float speed <- target.speed update:target.speed;
	geometry influence_area <- target.influence_area update:target.influence_area;
	aspect info {
		if (show_info) {
			draw status color:#black;
			draw influence_area color:rgb(200,200,255,0) border:rgb(200,200,255,255);
		}
	}
}

species info_player2 mirrors:player_stupidTeam {
	// mirror species that shows additionnal information about the player
	point location <- target.location update:target.location;
	string status <- target.status update:target.status;
	float speed <- target.speed update:target.speed;
	geometry influence_area <- target.influence_area update:target.influence_area;
	aspect info {
		if (show_info) {
			draw status color:#black;
			draw influence_area color:rgb(255,200,200,0) border:rgb(255,200,200,255);
		}
	}
}

experiment intelligentTeam_vs_stupidTeam type:gui {
	init {
		// instantialization of the game (contains global information about the game)
		create soccer_game with:(back_color_team::back_color_team,front_color_team::front_color_team) returns:soccerGame;
		// instantialization of the teams
		create intelligentTeam with:(game:first(soccerGame),position:"back");
		create stupidTeam with:(game:first(soccerGame),position:"front");
		add first(intelligentTeam) to:first(soccerGame).teams;
		add first(stupidTeam) to:first(soccerGame).teams;
		// create players of the team1
		list<player_intelligentTeam> player_list1;
		loop pos over:first(intelligentTeam).player_init_position {
			// compute the "real position" of each player according to the percentage given in "player_init_position"
			point real_pos <- (first(intelligentTeam).position = "back") ? {90-pos.x/100*90,pos.y/100*60} : {pos.x/100*90,120-pos.y/100*60};
			create player_intelligentTeam with:(team:first(intelligentTeam),game:first(intelligentTeam).game,location:real_pos,init_pos_in_percent:pos) returns:pl;
			add first(pl) to:player_list1;
		}
		first(intelligentTeam).players <- player_list1;
		// create players of the team2
		list<player_stupidTeam> player_list2;
		loop pos over:first(stupidTeam).player_init_position {
			// compute the "real position" of each player according to the percentage given in "player_init_position"
			point real_pos <- (first(stupidTeam).position = "back") ? {90-pos.x/100*90,pos.y/100*60} : {pos.x/100*90,120-pos.y/100*60};
			create player_stupidTeam with:(team:first(stupidTeam),game:first(stupidTeam).game,location:real_pos,init_pos_in_percent:pos) returns:pl;
			add first(pl) to:player_list2;
		}
		first(stupidTeam).players <- player_list2;
		first(soccerGame).players <- player_list1+player_list2;
	}
	parameter "show player info" var:show_info;
	output {
		display "soccer_field" type:2d  {
			// display the field.
			image "../images/soccer_field2.png";
			// display the species with their correct aspect
			species player_intelligentTeam aspect:player;
			species player_stupidTeam aspect:player;
			species ball_sp aspect:ball;
			species goal_sp aspect:goal;
			species goal_keeper aspect:goal_keeper;
			species info_player1 aspect:info;
			species info_player2 aspect:info;
		}
	}
}

experiment intelligentTeam_vs_intelligentTeam type:gui {
	init {
		// instantialization of the game (contains global information about the game)
		create soccer_game with:(back_color_team::back_color_team,front_color_team::front_color_team) returns:soccerGame;
		// instantialization of the teams
		create intelligentTeam with:(game:first(soccerGame),position:"back") returns:backTeam;
		create intelligentTeam with:(game:first(soccerGame),position:"front") returns:frontTeam;
		
		add first(backTeam) to:first(soccerGame).teams;
		add first(frontTeam) to:first(soccerGame).teams;
		// create players of the team1
		list<player_intelligentTeam> player_list1;
		loop pos over:first(backTeam).player_init_position {
			// compute the "real position" of each player according to the percentage given in "player_init_position"
			point real_pos <- (first(backTeam).position = "back") ? {90-pos.x/100*90,pos.y/100*60} : {pos.x/100*90,120-pos.y/100*60};
			create player_intelligentTeam with:(team:first(backTeam),game:first(backTeam).game,location:real_pos,init_pos_in_percent:pos) returns:pl;
			add first(pl) to:player_list1;
		}
		first(backTeam).players <- player_list1;
		// create players of the team2
		list<player_intelligentTeam> player_list2;
		loop pos over:first(frontTeam).player_init_position {
			// compute the "real position" of each player according to the percentage given in "player_init_position"
			point real_pos <- (first(frontTeam).position = "back") ? {90-pos.x/100*90,pos.y/100*60} : {pos.x/100*90,120-pos.y/100*60};
			create player_intelligentTeam with:(team:first(frontTeam),game:first(frontTeam).game,location:real_pos,init_pos_in_percent:pos) returns:pl;
			add first(pl) to:player_list2;
		}
		first(frontTeam).players <- player_list2;
		first(soccerGame).players <- player_list1+player_list2;
	}
	parameter "show player info" var:show_info;
	output {
		display "soccer_field"  type:2d{
			// display the field.
			image "../images/soccer_field2.png";
			// display the species with their correct aspect
			species player_intelligentTeam aspect:player;
			species ball_sp aspect:ball;
			species goal_sp aspect:goal;
			species goal_keeper aspect:goal_keeper;
			species info_player1 aspect:info;
			species info_player2 aspect:info;
		}
	}
}