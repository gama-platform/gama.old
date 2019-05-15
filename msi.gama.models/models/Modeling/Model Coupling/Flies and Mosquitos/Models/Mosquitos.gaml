/**
* Name: Flies
* Author: HUYNH Quang Nghi
* Description: This is a dummy model that show the randomly movement of the flies.
* Tags: comodel
*/
model Mosquitos


global
{
	geometry shape<-square(100);
	file icon<-file("./img/mosquito.png");
	int n <- 1;
	init
	{
		create Mosquito number: n;
	}

}

species Mosquito skills: [moving]
{
	geometry shape<-circle(1);
	int durability<- rnd(100);
	reflex dolive
	{	
		write "I can bite";
		do wander amplitude:rnd(30.0) speed:0.5;		
	}

	aspect default
	{
		draw icon size:4 color: # green rotate:heading+180;
	}

}

experiment Generic type: gui
{ 
	output
	{
		display "Mosquitos display"
		{
			species Mosquito aspect: default;
		}

	}

}


