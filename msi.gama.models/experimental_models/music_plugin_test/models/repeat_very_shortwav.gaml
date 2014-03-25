/**
 *  test_start1
 *  Author: voducan
 *  Description: 
 */

model repeat_very_short_wav

/* Insert your model definition here */

global {
	
	// assure that wa don't see concurrent problem on reloading model
	string music <- '../includes/CRASHCYM.wav';
	
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