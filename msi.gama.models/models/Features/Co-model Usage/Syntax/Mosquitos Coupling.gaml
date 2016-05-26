model mosquitos_coupling
import "Mosquitos.gaml"

global
{
}

experiment Generic type: gui
{
	list<Mosquito> get_mosquitos{
		return list(Mosquito);
	}
	
	//if we redefine the output, i.e, a blank output, the displays in parent experiement dont show.
	output
	{
	}

}


