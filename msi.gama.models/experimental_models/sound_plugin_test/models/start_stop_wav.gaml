/**
 *  start_and_stop_music
 *  Author: voducan
 *  Description: 
 */

model start_and_stop_wav

global {
	
	string sound <- '../includes/cat_fight.wav';
	
	init { create sound_player; }
}


species sound_player {
	
	reflex do_start_wav when: (time = 1) {
		
		start_sound source: sound  {
			write name + ' starts playing WAV file.';
		}
	}

	reflex do_stop_wav when: (time = 1000000) {
		
		stop_sound {
			write name + ' stops playing WAV file.';
		}
	}
}

 
 
experiment test type: gui {}