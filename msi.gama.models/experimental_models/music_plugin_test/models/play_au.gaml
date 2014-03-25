/**
 *  play_au
 *  Author: voducan
 *  Description: 
 */

model play_rmf

/* Insert your model definition here */

global {
	
	string music <- '../includes/spacemusic.au';
	bool is_repeat <- false;
	
	init play_music_reflex {
		start_music source: music repeat: is_repeat;
	}
}

experiment test type: gui {}