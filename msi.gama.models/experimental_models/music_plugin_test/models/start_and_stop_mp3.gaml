/**
 *  start_and_stop_music
 *  Author: voducan
 *  Description: 
 */

model start_and_stop_mp3

global {
	
	string music <- '../includes/Almost_a_whisper.mp3';
	
	init { create music_player; }
}


species music_player {
	
	init {
		start_music source: music {
			write name + ' starts playing MP3 file';
		}
	}
	
	reflex do_stop_music when: (time = 5000000) {
		
		stop_music {
			write name + ' stops playing MP3 file.';
		}
	}
}

 
 
experiment test type: gui {}