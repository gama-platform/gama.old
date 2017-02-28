/**
* Name: Clock
* Author: JD 
*  Description: This model supports defining a clock whose minutes corresponds to the tick of the simulation 
*  	By default 1000 milliseconds correspond to 1 cycle. 
*  Tags: date
*/
model Clock

global {
	//Background of the clock
	file clock_normal     const: true <- image_file("../images/clock.png");
	//Image for the big hand 
	file clock_big_hand   const: true <- image_file("../images/big_hand.png");
	//Image for the small hand
	file clock_small_hand const: true <- image_file("../images/small_hand.png");
	//Image for the clock alarm
	file clock_alarm 	  const: true <- image_file("../images/alarm_hand.png");
	//Zoom to take in consideration the zoom in the display, to better write the cycle values
	int zoom <- 4 min:1 max:100;
	//Time value for a cycle
	float step<-360000.0#ms min: 1.0 max: 360000.0;
	
	//Alarm parameters
	int alarm_days <- 0 min:0 max:365;
	int alarm_hours <- 2 min:0 max:11;
	int alarm_minutes <- 0 min:0 max:59;
	int alarm_seconds <- 0 min:0 max:59;
	bool alarm_am <- true;
	int alarmCycle <-  int((alarm_seconds+alarm_minutes*60+alarm_hours*3600 + (alarm_am ? 0 : 3600*12) + alarm_days*3600*24) * 1000 / step);
	
	//Time elapsed since the beginning of the experiment
	int timeElapsed <- 0 update:  int(cycle * step);
	string reflexType <-"";
	init {
		//Creation of the clock
		create clock number: 1 {
			location <- {world.shape.width/5,world.shape.height/5};
		}
	}
}
//Species that will represent the clock
species  clock { 
		float nb_minutes<-0.0 update: ((timeElapsed mod 3600000#ms))/60000#ms; //Mod with 60 minutes or 1 hour, then divided by one minute value to get the number of minutes
		float nb_hours<-0.0 update:((timeElapsed mod 43200000#ms))/3600000#ms;
		reflex update {
			write string(nb_hours)+" : "+nb_minutes;
			if (cycle = alarmCycle) 
			{
				 write "Time to leave" ; 

				 // Uncomment the following statement to play the Alarm.mp3
				 // But firstly, you need to go to "Help -> Install New Software..." to install the "Audio" feature (which is still in the experimental stage). 
				//start_sound source:"../includes/Alarm.mp3" ;
			}
		}
		aspect default {
			draw string("#cycles: " + cycle + " cycles")  size:zoom/2 font:"times" color:°black at:{world.shape.width/3,0};
			draw clock_big_hand rotate: nb_minutes*(360/60)  + 90  size: {7 * zoom, 2} at:location + {0,0,0.1}; //Modulo with the representation of a minute in ms and divided by 10000 to get the degree of rotation
			draw clock_small_hand rotate: nb_hours*(360/12)  + 90  size:{5*zoom, 2} at:location + {0,0,0.1};			
			draw clock_alarm rotate:      (alarmCycle/12000)  size: zoom/3 at:location + {0,0,0.1}; // Alarm time
		}
 
}

experiment Display type: gui {
	float minimum_cycle_duration <- 0.1#s;
	parameter 'Zoom: ' var: zoom category: 'Init' ;
	parameter 'Milliseconds/cycle' var: step category: 'Init';
	parameter 'alarm Day' var: alarm_days;
	parameter 'alarm Hour' var: alarm_hours;
	parameter 'alarm Am' var: alarm_am;
	parameter 'alarm Minutes' var: alarm_minutes;
	parameter 'alarm Seconds' var: alarm_seconds;
	output {
		display ClockView type: opengl { 
			graphics "c" refresh: false {draw clock_normal size: 10*zoom at:{world.shape.width/5,world.shape.height/5} ;}
			species clock ;
		}
	}

}

