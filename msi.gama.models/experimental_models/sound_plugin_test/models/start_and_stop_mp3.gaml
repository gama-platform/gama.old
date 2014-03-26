/**
 *  start_and_stop_music
 *  Author: voducan
 *  Description: 
 */

model start_and_stop_mp3

global {
	
	string sound <- '../includes/Almost_a_whisper.mp3';
	
	init { create sound_player; }
}


species sound_player {
	
	init {
		start_sound source: sound {
			write name + ' starts playing MP3 file';
		}
	}
	
	reflex do_stop_sound when: (time = 5000000) {
		
		stop_sound {
			write name + ' stops playing MP3 file.';
		}
	}
}

 
 
experiment test type: gui {}