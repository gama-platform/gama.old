/**
 *  play_au
 *  Author: voducan
 *  Description: 
 */

model play_rmf

/* Insert your model definition here */

global {
	
	string sound <- '../includes/spacemusic.au';
	bool is_repeat <- false;
	
	init play_sound_reflex {
		start_sound source: sound repeat: is_repeat;
	}
}

experiment test type: gui {}