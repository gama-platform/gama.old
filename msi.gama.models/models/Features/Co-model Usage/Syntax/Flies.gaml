model Flies


global
{
	geometry shape<-square(100);
	file icon<-file("../img/fly.gif");
	int n <- 50;
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
		do wander amplitude:200;		
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


