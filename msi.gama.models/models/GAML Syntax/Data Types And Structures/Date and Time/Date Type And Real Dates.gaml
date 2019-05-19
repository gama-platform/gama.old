/**
* Name: Use of the Date variables
* Author:  Patrick Taillandier
* Description: A model to show how to use date variables and more particularly the starting_date and current_date global variables.
* Tags: date, time
*/
@no_warning
model date_model 

global {
	
	//definition of the date of begining of the simulation - defining this date will allows to change the normal date management of the simulation by a more realistic one (using calendar) 
	date starting_date <- date([2011,1,2,1,1,30]);
	
	//be careful, when real dates are used, modelers should not use the #month and #year values that are not consistent with them
	//#ms, #s, #mn, #h, #day represent exact durations that can be used in combination with the date values
	
	
	float step <- 2#year; 
		
	init {
		write "starting_date: " + starting_date;
		
		//there are several ways to define a date.
		//The simplest consists in using a list of int values: [year,month of the year,day of the month, hour of the day, minute of the hour, second of the minute]
		date my_date <- date([2010,3,23,17,30,10]); //correspond the 23th of March 2010, at 17:30:10
		
		//It is also possible to define a date through a string that respects the ISO format
		date my_date2 <- date("2010-03-23T17:30:10+07:00"); 
		write sample(my_date2);
		//The format can omit the 'T' for time
		date my_date3 <- date("2010-03-23 17:30:10+07:00"); 
		//And also omit time zones / offset informations
		my_date3 <- date("2010-03-23 17:30:10"); 
		//Dates (without time) can be defined also using the ISO basic format with no separators
		 my_date3 <- date("20100323");
		
		//Or the normal ISO date format with no time / timezone information
		my_date3 <- date("2013-03-23");
		write sample(my_date2);
		write sample(my_date3);
		// Finally, dates can also be parsed using a custom pattern if one encounters it in a data file, for instance (see https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#patterns) 
		write(sample(date("01 23 2000","MM dd yyyy")));
		// Parsed dates that only represent hours will be parsed as departing from the starting_date of the model
		write(sample(date("01 23 20","HH mm ss")));
		//it is possible to get the current date by using the "now" string:
		date today <- date("now"); 
		write (today);
		
		write "\n ----------------------------------------------- " ;
		
		//GAMA provides several operator to manipulate dates:	
			
		//for instance, it is possible to compute the duration in seconds between 2 dates:
		float d <- starting_date - my_date;
		write "duration between " + my_date2 + " and " + starting_date + " : " + d + "s";
		
		write "\n ----------------------------------------------- " ;
		
		//to add or subtract a duration (in secondes) to a date:
		 write sample(my_date2 + 10);
		 write sample(my_date2 - 10);
		 
		 write "\n ----------------------------------------------- " ;
		 
		 // Dates can be compared
		 write sample(my_date2 > my_date3);
		 write sample(#now < my_date3);
		 
		 // Dates can be easily casted from and to other values
		 write "Casting to float returns the number of seconds of this date since the starting_date of the model: " + sample(float(my_date3));
		 write "Casting to list returns a list with the year, month, day, hour, minute, second: " + sample(list(my_date2));
		 
		 //to add or subtract a duration (in years, months, weeks, days, hours, minutes,  secondes) to a date, use the plus_* (or add_*) / minus_* (or subtract_*) operators
		  write sample(my_date2 plus_years 1);
		  write sample(my_date2 plus_months 1);
		  write sample(my_date2 plus_weeks 1);
		  write sample(my_date2 plus_days 1);
		  write sample(my_date2 plus_hours 1);
		  write sample(my_date2 plus_minutes 1);
		  write sample(my_date2 plus_seconds 1);
		  // The three latter examples are equivalent to using the built-in GAMA units
		  write(sample(my_date2 + 1#d));
		  write(sample(my_date2 + 1#h));
		  write(sample(my_date2 + 1#mn));
		  write(sample(my_date2 + 1#s));
		  // Adding milliseconds can be done too
		  write(sample((my_date2 + 0.7) + 0.7)); // should add a second and 400 milliseconds
		  // Subtraction follows the same rules
		  write sample(my_date2 minus_years 1);
		  write sample(my_date2 minus_months 1);
		  write sample(my_date2 minus_weeks 1);
		  write sample(my_date2 minus_days 1);
		  write sample(my_date2 minus_hours 1);
		  write sample(my_date2 minus_minutes 1);
		  write sample(my_date2 minus_seconds 1);
		  // And the same equivalence with the 4 last ones
		  write(sample(my_date2 - 1#day));
		  write(sample(my_date2 - 1#h));
		  write(sample(my_date2 - 1#mn));
		  write(sample(my_date2 - 1#s));
		  // As the units #month and #year represent ideal durations (and not actual months or years in a chronology) they cannot be used like #day, #h, #mn or #s.
		  // For instance, these two expressions return false
		  write(sample((#now plus_months 5 plus_days 2) = (#now + 5#months + 2#days)));
		  write(sample(#now - (#now minus_months 5) = 5#month));
		  
		  // Dates can be formatted in different ways (see https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#patterns) 
		  
		  write  string(my_date2, "MM dd yyyy");
		  write  string(my_date2, "HH:mm:ss");
		  write  string(my_date2, "'Week 'w' of 'yyyy");
		  
		  // For models that do not define a starting_date, GAMA uses a pseudo-starting date represented by the ISO notion of the 'epoch' day (1970-01-01T00:00Z), accessible in GAMA with the constant #epoch
		  // This allows to output a current date that represents the duration from this zero-date. 
		  starting_date <- #epoch;
		  write sample(current_date + 3#day);
		  starting_date <- #now;
		  
		 // Consistency with existing variables in enforced.  For instance, machine_time can now be obtained in the following way:
		 write "Machine time value = " + sample(machine_time);
		 write "Other way to obtain the same value = " + sample(milliseconds_between(#epoch, #now));
		 
		 // This allows precise computations of hours, minutes, seconds, etc. 
		 write "Hours between " + my_date2 + " and " + #now + " = " + int(milliseconds_between(my_date2, #now) / #hours);
		 write "Days between " + my_date2 + " and " + #now + " = " + int(milliseconds_between(my_date2, #now) / #days);
		 // However, months and years have to use dedicated operators, since they dont capture the notion of duration correctly
		 write "Months between " + my_date2 + " and " + #now + " = " + (months_between(my_date2, #now)); 
		  write "Years between " + my_date2 + " and " + #now + " = " + (years_between(my_date2, #now)); 
		  write "Milliseconds between now and ... now = " + milliseconds_between(#now, #now);
	}
	
	reflex info_date {
		//at each simulation step, the current_date is updated - its value can be seen in the top-left info panel. 
		// If a starting date is defined, the current date is printed. If not, its equivalent duration is printed instead
		// current_date is always equal to starting_date + time or starting_date + cycle * step
		write "current_date at cycle " + cycle + " : " + string(current_date, "dd MMMM yyyy HH:mm:ss");
	}
}

experiment main type: gui;