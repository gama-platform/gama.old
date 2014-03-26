/**
 *  test_start1
 *  Author: voducan
 *  Description: 
 */

model test_repeat

/* Insert your model definition here */

global {
	
	string sound <- '../includes/CRASHCYM.wav';
	
	init { create sound_player; }
}

species sound_player {
	
	init {
		start_sound source: sound repeat: true {
			write name + ' starts playing sound';
		}
	}
	
}

 
 
experiment test type: gui {}