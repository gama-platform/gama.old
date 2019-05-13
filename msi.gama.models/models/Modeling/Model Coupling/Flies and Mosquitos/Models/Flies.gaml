/**
* Name: Flies
* Author: HUYNH Quang Nghi
* Description: This is a dummy model that show the randomly movement of the flies.
* Tags: comodel
*/
model Flies


global
{
	geometry shape<-square(100);
	file icon<-file("./img/fly.gif");
	int n <- 1;
	init
	{
		create Fly number: n;
	}

}

species Fly skills: [moving]
{
	geometry shape<-circle(1);
	int durability<- rnd(100);
	reflex dolive
	{	
		write "I can fly";
		do wander amplitude:200.0;		
	}

	aspect default
	{
		draw icon size:4 color: # green rotate:heading+180;
	}

}

experiment Simple type: gui
{ 
	output
	{
		display "Flies display"
		{
			species Fly aspect: default;
		}

	}

}


