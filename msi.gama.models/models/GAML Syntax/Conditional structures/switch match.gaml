/***
* Name: switchmatch
* Author: kevinchapuis
* Description: 
* Tags: Tag1, Tag2, TagN
***/

model switchmatch

global {
	
	list RPS <- ["ROCK","PAPER","SCISSORS"];
	 
	string my_play;// parameter:true init:"ROCK" among:RPS;
	
	file my_image;
	file bot_image;
	
	string the_result;
	
	int win_sign;
	
	init {
		create rps_bot with:[strategy::[1,1,1]];
	}
	
	reflex play {
		ask rps_bot {do bot_play;}
		switch first(rps_bot).bp+"vs"+my_play {
			match "ROCKvsPAPER" {
				win_sign <- 1;
			}
			match "ROCKvsSCISSORS" {
				win_sign <- -1;
			}
			match "PAPERvsROCK" {
				win_sign <- -1;
			}
			match "PAPERvsSCISSORS" {
				win_sign <- 1;
			}
			match "SCISSORSvsPAPER" {
				win_sign <- -1;
			}
			match "SCISSORSvsROCK" {
				win_sign <- 1;
			}
			default {
				win_sign <- 0;
			} 
		}
		bot_image <- file(rps_image(first(rps_bot).bp));
		my_image <- file(rps_image(my_play));
		write rps_result(win_sign);
	}
	
	string rps_result(int res) {
		switch res {
			match -1 {
				return "You loose";
			} 
			match 1 {
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
		bp <- RPS[rnd_choice(strategy)];
	}
	
}

experiment rock_paper_scissors type:gui {
	parameter "My play" var:my_play among:RPS init:any(RPS);
	output {
		display my_display {
			image my_image size:point(0.2) position:{10,40};
			graphics res {
				draw (win_sign = 0 ? "=" : (win_sign < 0 ? "<" : ">")) at:{47,52} font:font("Digit",50,#bold) color:#black;
			}
			image bot_image size:point(0.2) position:{70,40};
		}
	}
}

