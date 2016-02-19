
/**
* Name: Use of the Date variables
* Author:  Patrick Taillandier
* Description: A model to show how to use date variables and more particularly the starting_date and current_date global variables. In this model,
* Tags: Date
*/
model date_model 

global {
	
	//definition of the date of begining of the simulation - defining this date will allows to change the normal date management of the simulation by a more realistic one (using calendar) 
	date starting_date <- date([2011,1,2,1,1,30]);
	
	//be careful, when real dates are used, modelers should not use the #month and #year values that are not consistent with them
	float step <- 1#h;
		
	init {
		write "starting_date: " + starting_date;
		
		//there are several ways to define a date.
		//The simplest consists in using a list of int values: [year,month of the year,day of the month, hour of the day, minute of the hour, second of the minute]
		date my_date <- date([2010,3,23,17,30,10]); //correspond the 23th of March 2010, at 17:30:10
		
		//It is also possible to define a date through a string:
		date my_date2 <- date("2010-3-23T17:30:10+07:00"); 
		write (my_date2);
	
		//it is possible to get the current date by using the "now" string:
		date today <- date("now"); 
		write (today);
		
		write "\n ----------------------------------------------- " ;
		
		//GAMA provides several operator to manipulate dates:	
			
		//for instance, it is possible to compute the duration in seconds between 2 dates:
		float d <- starting_date - my_date;
		write "duration between " + my_date + " and " + starting_date + " : " + d + "s";
		
		write "\n ----------------------------------------------- " ;
		
		//to add or substract a duration (in secondes) to a date:
		 write "my_date2 + 10: " + (my_date2 + 10);
		 write "my_date2 - 10: " + (my_date2 - 10);
		 
		 write "\n ----------------------------------------------- " ;
		 
		 
		 //to add or substract a duration (in years, months, weeks, days, hours, minutes,  secondes) to a date:
		  write "my_date2 add_years 1: " + (my_date2 add_years 1);
		  write "my_date2 add_months 1: " + (my_date2 add_months 1);
		  write "my_date2 add_weeks 1: " + (my_date2 add_weeks 1);
		  write "my_date2 add_days 1: " + (my_date2 add_days 1);
		  write "my_date2 add_hours 1: " + (my_date2 add_hours 1);
		  write "my_date2 add_minutes 1: " + (my_date2 add_minutes 1);
		  write "my_date2 add_seconds 1: " + (my_date2 add_seconds 1);
		  
		  write "my_date2 substract_years 1: " + (my_date2 substract_years 1);
		  write "my_date2 substract_months 1: " + (my_date2 substract_months 1);
		  write "my_date2 substract_weeks 1: " + (my_date2 substract_weeks 1);
		  write "my_date2 substract_days 1: " + (my_date2 substract_days 1);
		  write "my_date2 substract_hours 1: " + (my_date2 substract_hours 1);
		  write "my_date2 substract_minutes 1: " + (my_date2 substract_minutes 1);
		  write "my_date2 substract_seconds 1: " + (my_date2 substract_seconds 1);
	}
	
	reflex info_date {
		//at each simulation step, the current_date is updated - its value can be seen in the top-left green info panel.
		write "current_date at cycle " + cycle + " : " + current_date;
	}
}

experiment main type: gui;