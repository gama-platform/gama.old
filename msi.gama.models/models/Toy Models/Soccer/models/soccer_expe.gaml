/**
* Name: soccerexpe
* Author: Julien
* Description: 
* Tags: Tag1, Tag2, TagN
*/

model soccerexpe

// import the 2 strategies
import "./team1_strategy.gaml"
import "./team2_strategy.gaml"

global schedules:shuffle(player_team1 + player_team2) {
	// set the size of the environment
	geometry shape <- rectangle(90#m,120#m);
	
	rgb back_color_team <- #blue;
	rgb front_color_team <- #red;
	
	init {
		// instantialization of the game (contains global information about the game)
		create soccer_game with:(back_color_team::back_color_team,front_color_team::front_color_team) returns:soccerGame;
		// instantialization of the teams
		create team1 with:(game:first(soccerGame),position:"back");
		create team2 with:(game:first(soccerGame),position:"front");
		add first(team1) to:first(soccerGame).teams;
		add first(team2) to:first(soccerGame).teams;
		// create players of the team1
		list<player_team1> player_list1;
		loop pos over:first(team1).player_init_position {
			// compute the "real position" of each player according to the percentage given in "player_init_position"
			point real_pos <- (first(team1).position = "back") ? {90-pos.x/100*90,pos.y/100*60} : {pos.x/100*90,120-pos.y/100*60};
			create player_team1 with:(team:first(team1),game:first(team1).game,location:real_pos,init_pos_in_percent:pos) returns:pl;
			add first(pl) to:player_list1;
		}
		first(team1).players <- player_list1;
		// create players of the team2
		list<player_team2> player_list2;
		loop pos over:first(team2).player_init_position {
			// compute the "real position" of each player according to the percentage given in "player_init_position"
			point real_pos <- (first(team2).position = "back") ? {90-pos.x/100*90,pos.y/100*60} : {pos.x/100*90,120-pos.y/100*60};
			create player_team2 with:(team:first(team2),game:first(team2).game,location:real_pos,init_pos_in_percent:pos) returns:pl;
			add first(pl) to:player_list2;
		}
		first(team2).players <- player_list2;
		first(soccerGame).players <- player_list1+player_list2;
	}
}

species info_player1 mirrors:player_team1 {
	point location <- target.location update:target.location;
	string status <- target.status update:target.status;
	float speed <- target.speed update:target.speed;
	geometry influence_area <- target.influence_area update:target.influence_area;
	aspect info {
		draw status color:#black;
		draw influence_area color:rgb(200,200,255,0) border:rgb(200,200,255,255);
		write target.name + " " +influence_area.location.y;
	}
}

species info_player2 mirrors:player_team2 {
	point location <- target.location update:target.location;
	string status <- target.status update:target.status;
	float speed <- target.speed update:target.speed;
	//geometry influence_area <- target.influence_area update:target.influence_area;
	aspect info {
		draw status color:#black;
		//draw influence_area color:rgb(255,200,200,0) border:rgb(255,200,200,255);
	}
}

experiment my_experiment type:gui {
	output {
		display "soccer_field"  {
			// display the field.
			image "../images/soccer_field2.png";
			// display the species with their correct aspect
			species player_team1 aspect:player;
			species player_team2 aspect:player;
			species ball_sp aspect:ball;
			species goal_sp aspect:goal;
			species info_player1 aspect:info;
//			species info_player2 aspect:info;
//			species team1 aspect:offside_pos;
//			species team2 aspect:offside_pos;
			
		}
	}
}