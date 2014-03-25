/**
 *  start_and_stop_music
 *  Author: voducan
 *  Description: 
 */

model start_and_stop_wav

global {
	
	string music <- '../includes/cat_fight.wav';
	
	init { create music_player; }
}


species music_player {
	
	reflex do_stwav when: (time = 1) {
		
		start_music source: music  {
			write name + ' starts playing WAV file.';
		}
	}

	reflex do_stop_wav when: (time = 1000000) {
		
		stop_music {
			write name + ' stops playing WAV file.';
		}
	}
}

 
 
experiment test type: gui {}