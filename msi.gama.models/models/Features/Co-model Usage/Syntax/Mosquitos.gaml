model Mosquitos


global
{
	geometry shape<-square(100);
	file icon<-file("../img/mosquito.png");
	int n <- 50;
	init
	{
		create Mosquitos number: n;
	}

}

species Mosquitos skills: [moving]
{
	geometry shape<-circle(1);
	int durability<- rnd(100);
	reflex dolive
	{	
		do wander amplitude:90;		
	}

	aspect default
	{
		draw icon size:5 color: # green rotate:heading+180;
	}

}

experiment MosquitosExperiment type: gui
{ 
	output
	{
		display "Mosquitos display"
		{
			species Mosquitos aspect: default;
		}

	}

}


