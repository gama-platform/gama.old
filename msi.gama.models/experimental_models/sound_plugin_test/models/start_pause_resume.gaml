/**
 *  start_pause_resume
 *  Author: voducan
 *  Description: 
 */

model start_pause_resume

/* Insert your model definition here */

global {
	
	string sound <- '../includes/Almost_a_whisper.mp3';
	
	init { create sound_player; }
}

species sound_player {
	
	reflex do_start_sound when: (time = 1) {
		start_sound source: sound {
			write name + ' starts playing sound';
		}
	}
	
	reflex do_pause_sound when: (time = 5000000) {
		pause_sound {
			write name + ' pauses sound';
		}		
	}
	
	reflex do_resume_sound when: (time = 7500000) {
		resume_sound {
			write name + ' pauses sound';
		}		
	}
}

experiment test type: gui {}