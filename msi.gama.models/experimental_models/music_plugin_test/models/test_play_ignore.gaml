/**
 *  test_play_ignore
 *  Author: voducan
 *  Description: 
 */

model test_play_ignore

/* Insert your model definition here */

global {
	
	string music1 <- '../includes/cat_fight.wav';
	string music2 <- '../includes/spacemusic.au';
	
	init { create music_player; }
}


species music_player {
	
	reflex do_stwav when: (time = 1) {
		
		start_music source: music1 {
			write name + ' starts playing music file : ' + music1;
		}
	}

	reflex do_stop_wav when: (time = 1000000) {
		
		// this make the currently played music stop and play "music2" music
		start_music source: music2 mode: ignore {
			write name + ' start playing music file : ' + music2;
		}
	}
}

 
 
experiment test type: gui {}