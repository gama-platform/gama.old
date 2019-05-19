/**
* Name: Simple definition of time property in a model
* Author: Patrick Taillandier
* Description: Show how the time is managed is GAMA.
* Tags: date, time
*/
@no_warning
model SimpleTimedefinition

global {
	//redifitiion of the duration of one simulation step to 1 hour. By default the duration is one second.
	float step <- 1 #hour;
	
	
	init {
		//GAMA provides different values for temporal units - these values are given in seconds
		write "1#ms:" + 1#ms;
		write "1#s: " + 1#s;
		write "1#mn: " + 1#mn;
		write "1#hour: " + 1#hour;
		write "1#day: " + 1#day;
		write "1#month: " + 1#month;
		write "1#year: " + 1#year;
		
		//Note that these values are constant: the value #month is always equal to 30 #day. It has been deprecated, as well as #year, so that time units represent exact durations as well.
	}
	reflex info_time {
		write "\n-------------------------------------------";
		//the global variable cycle gives the current step of the simulation
		write "cycle: " + cycle;
		
		//the global variable time gives the current duration (in seconds) since the beginning of the simulation: time = cycle * step
		//The value of the time facet can be seen - in a date-time presentation - in the top-left green info panel (click on the number of cycle to see the time value). 
		//All models, otherwise stated, start at the ISO date of 1970-01-01T00:00Z. 
		//For more realistic accounts of dates, see the Date type and Real dates model in the same folder.
		write "time: " + time;
	}
}

experiment SimpleTimedefinition type: gui ;