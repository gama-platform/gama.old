/**
* Name: Comodel_SIR_Switch
* Author: Lô
* Description: dummy model m2
* Tags: Tag1, Tag2, TagN
*/
model M2


global
{
	int n <- 4;
	init
	{
		create B number: n;
	}

}

species B skills: [moving]
{
	int IQ <- rnd(100);
	reflex dolive
	{
		write "" + "i'm alive !!";
		do wander;
	}

	aspect default
	{
		draw square(1) color: # red;
	}

}

experiment M2_exp type: gui
{
	output
	{
		display "m2_disp"
		{
			species B aspect: default;
		}

	}

}


