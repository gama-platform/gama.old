/**
 *  test_start1
 *  Author: voducan
 *  Description: 
 */

model test_repeat

/* Insert your model definition here */

global {
	
//	string music <- '../includes/Almost_a_whisper.mp3';
	string music <- '../includes/CRASHCYM.wav';
//	string music <- '../includes/cat_fight.wav';
	
	init { create music_player; }
}

species music_player {
	
	init {
		start_music source: music repeat: true {
			write name + ' starts playing music';
		}
	}
	
}

 
 
experiment test type: gui {}