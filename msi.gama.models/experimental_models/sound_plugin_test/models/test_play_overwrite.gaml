/**
 *  testplayoverwrite
 *  Author: voducan
 *  Description: 
 */

model test_play_overwrite_mode

/* Insert your model definition here */

global {
	
	string sound1 <- '../includes/cat_fight.wav';
	string sound2 <- '../includes/spacemusic.au';
	
	init { create sound_player; }
}


species sound_player {
	
	reflex do_start_wav when: (time = 1) {
		
		start_sound source: sound1 {
			write name + ' starts playing sound file : ' + sound1;
		}
	}

	reflex do_stop_wav when: (time = 1000000) {
		
		// this make the currently played music stop and play "music2" sound
		start_sound source: sound2 mode: overwrite {
			write name + ' start playing sound file : ' + sound2;
		}
	}
}

 
 
experiment test type: gui {}