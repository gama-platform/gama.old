/**
 *  play_aif
 *  Author: voducan
 *  Description: 
 */
model play_aif

/* Insert your model definition here */

global {
	
	string music <- '../includes/destMouse_Peru.aif';
	bool is_repeat <- false;
	
	init play_music_reflex {
		start_music source: music repeat: is_repeat;
	}
}

experiment test type: gui {}