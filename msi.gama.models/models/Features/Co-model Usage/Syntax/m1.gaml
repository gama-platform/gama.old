model M1


global
{
	int n <- 8;
	init
	{
		create A number: n;
	}

}

species A skills: [moving]
{
	int IQ <- rnd(100);
	reflex dolive
	{
		write "" + "i'm alive !!";
		do wander;
	}

	aspect default
	{
		draw square(1) color: # green;
	}

}

experiment M1_exp type: gui
{
	output
	{
		display "m1_disp"
		{
			species A aspect: default;
		}

	}

}


