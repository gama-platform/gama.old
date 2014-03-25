/**
 *  world_plays_music
 *  Author: voducan
 *  Description: 
 */

model world_plays_music

/* Insert your model definition here */

global {
	
	string music <- '../includes/Almost_a_whisper.mp3';
	
	init play_music_reflex {
		start_music source: music repeat: true;
	}
}

experiment test type: gui {}