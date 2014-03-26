/**
 *  world_plays_music
 *  Author: voducan
 *  Description: 
 */

model world_plays_music

/* Insert your model definition here */

global {
	
	string sound <- '../includes/Almost_a_whisper.mp3';
	
	init play_sound_reflex {
		start_sound source: sound repeat: true;
	}
}

experiment test type: gui {}