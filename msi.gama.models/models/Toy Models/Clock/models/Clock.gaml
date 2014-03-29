/**
 *  model for Simulation Clock
 *  Author: JD 
 *  Description: This model supports defining a clock whose minutes corresponds to the tick of the simulation
 *  By default 1 millisecond correspond to 1 cycle. 
 */
model Clock

global {
	const clock_normal     type: file <- image_file("../images/clock.png");
	const clock_big_hand   type: file <- image_file("../images/big_hand.png");
	const clock_small_hand type: file <- image_file("../images/small_hand.png");
	const clock_alarm 	   type: file <- image_file("../images/alarm_hand.png");
	int zoom <- 4 min:1 max:100;
	int rate <- 10 min:1 max:3600000;
	int alarm_days <- 0 min:0 max:365;
	int alarm_hours <- 2 min:0 max:11;
	int alarm_minutes <- 0 min:0 max:59;
	int alarm_seconds <- 0 min:0 max:59;
	bool alarm_am <- true;
	int timeElapsed;
	int alarmCycle <-  int((alarm_seconds+alarm_minutes*60+alarm_hours*3600 + (alarm_am ? 0 : 3600*12) + alarm_days*3600*24) * 1000 / rate);
	string reflexType <-"";
	
	init {
		
		create clock number: 1 {
			location <- {-world.shape.width/5,world.shape.height/5};
		}
	}
}

species  clock { 
	
		aspect default {
			timeElapsed <- cycle * rate ;// in seconds
			
			draw string("#cycles: " + cycle + " cycles")  size:zoom/2 font:"times" color:°black at:{-world.shape.width/3,0};
			draw clock_normal size: 10*zoom;
			draw clock_big_hand rotate:   (timeElapsed/10000 + 90) size:7 * zoom;
			draw clock_small_hand rotate: (timeElapsed/120000 + 90) size:5*zoom ;			
			draw clock_alarm rotate:      (alarmCycle/12000)  size: zoom/3 ; // Alarm time
			
		}
		reflex update {
			if (cycle = alarmCycle) {
				 write "Time to leave" ; 

				 // Un comment the following statement to play the Alarm.mp3
				 // But firstly, you need to go to "Help -> Install New Software..." to install the "Audio" feature (which is still in the experimental stage). 
				//start_sound source:"../includes/Alarm.mp3" ;
			}
		}
 
}

experiment Display type: gui {
	parameter 'Zoom: ' var: zoom category: 'Init' ;
	parameter 'Milliseconds/cycle' var: rate category: 'Init';
	parameter 'alarm Day' var: alarm_days;
	parameter 'alarm Hour' var: alarm_hours;
	parameter 'alerm Am' var: alarm_am;
	parameter 'alarm Minutes' var: alarm_minutes;
	parameter 'alarm Seconds' var: alarm_seconds;
	output {
		display ClockView type: opengl ambient_light:50 diffuse_light:100 { 
		species clock ;//position: {0.1,0} //{world.shape.width/10,world.shape.height/10} ;
		}
	}

}

