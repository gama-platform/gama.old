/***
* Name: switchmatch
* Author: kevinchapuis
* Description: Show several way to use the switch ... match ... statement
* Tags: switch, match, match_one
* 
***/

model switchmatch
 
global {
	string my_play; // parameter:true init:"ROCK" among:RPS;
	file my_image;
	file bot_image;
	
	string the_result;
	
	int win_sign;
	
	init {
		create rps_bot with:[strategy::[1,1,1]];
	}
	
	/*
	 * The argument to match can be constructed from any instruction. Switch statement
	 * accept 3 types of match: (1) match to test the equality (2) match_one for at least one equality
	 * (2) match_between for a test on a range of numerical value
	 */
	reflex play {
		ask rps_bot {do bot_play;}
		switch first(rps_bot).bp+"vs"+my_play {
			match_one ["ROCKvsPAPER", "PAPERvsSCISSORS", "SCISSORSvsROCK"] {
				win_sign <- 1;
			}
			match_one ["ROCKvsSCISSORS", "PAPERvsROCK", "SCISSORSvsPAPER"] {
				win_sign <- -1;
			}
			default {
				win_sign <- 0;
			} 
		}
		bot_image <- file(rps_image(first(rps_bot).bp));
		my_image <- file(rps_image(my_play));
		write rps_result(win_sign);
	}
	
	/*
	 * Switch statement can be used with any type of data like int, string, agent. 
	 * The match will be tested using the main expression type (here is int)
	 */
	string rps_result(int res) {
		switch res {
			match_between [-#infinity,-1] {
				return "You loose";
			} 
			match_one [1,2,3,4,5] {
				return "You win";
			}
			match 0 {
				return "Draw";
			}
		}
	}
	
	string rps_image(string play) {
		switch play {
			match "ROCK" {
				return "img/rock.png";
			}
			match "PAPER" {
				return "img/paper.png";
			}
			match "SCISSORS" {
				return "img/scissors.png";
			}
		}
	}
	
}
 
species rps_bot {
	
	list<float> strategy;
	
	string bp;
	
	action bot_play {
		bp <- ["ROCK","PAPER","SCISSORS"][rnd_choice(strategy)];
	}
	
}

experiment "Rock Paper Scissors" type:gui {
	parameter "My play" var:my_play among:["ROCK","PAPER","SCISSORS"] init:any(["ROCK","PAPER","SCISSORS"]);
	output {
		display my_display type:3d{
			image my_image size:point(0.2) position:{10,40} refresh:true;
			graphics res {
				draw (win_sign = 0 ? "=" : (win_sign < 0 ? "<" : ">")) at:{47,52} font:font("Digit",50,#bold) color:#black;
			}
			image bot_image size:point(0.2) position:{70,40} refresh:true;
		}
	}
}

