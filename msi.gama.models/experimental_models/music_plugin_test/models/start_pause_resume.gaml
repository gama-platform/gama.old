/**
 *  start_pause_resume
 *  Author: voducan
 *  Description: 
 */

model start_pause_resume

/* Insert your model definition here */

global {
	
	string music <- '../includes/Almost_a_whisper.mp3';
	
	init { create music_player; }
}

species music_player {
	
	reflex do_start_music when: (time = 1) {
		start_music source: music {
			write name + ' starts playing music';
		}
	}
	
	reflex do_pause_music when: (time = 5000000) {
		pause_music {
			write name + ' pauses music';
		}		
	}
	
	reflex do_resume_music when: (time = 7500000) {
		resume_music {
			write name + ' pauses music';
		}		
	}
}

experiment test type: gui {}