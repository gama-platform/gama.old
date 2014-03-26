/**
 *  test_start1
 *  Author: voducan
 *  Description: 
 */

model repeat_very_short_wav

/* Insert your model definition here */

global {
	
	// assure that wa don't see concurrent problem on reloading model
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