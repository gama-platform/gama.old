/**
 *  play_aif
 *  Author: voducan
 *  Description: 
 */
model play_aif

/* Insert your model definition here */

global {
	
	string sound <- '../includes/destMouse_Peru.aif';
	bool is_repeat <- false;
	
	init play_sound_reflex {
		start_sound source: sound repeat: is_repeat;
	}
}

experiment test type: gui {}