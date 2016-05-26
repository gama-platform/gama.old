model flies_coupling
import "Flies.gaml"

global
{
}

experiment FliesCouplingExperiment parent:FliesExperiment type: gui
{
	list<Flies> get_flies{
		return list(Flies);
	}
	
	//if we redefine the output, i.e, a blank output, the displays in parent experiement dont show.
	output
	{
	}

}


