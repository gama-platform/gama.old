model flies_coupling
import "Flies.gaml"

global
{
}

experiment Simple type: gui
{
	list<Fly> get_flies{
		return list(Fly);
	}
	
	//if we redefine the output, i.e, a blank output, the displays in parent experiement dont show.
	output
	{
	}

}


