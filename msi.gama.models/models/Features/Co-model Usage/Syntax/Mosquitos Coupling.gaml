model mosquitos_coupling
import "Mosquitos.gaml"

global
{
}

experiment MosquitosCouplingExperiment parent:MosquitosExperiment type: gui
{
	list<Mosquitos> get_mosquitos{
		return list(Mosquitos);
	}
	
	//if we redefine the output, i.e, a blank output, the displays in parent experiement dont show.
	output
	{
	}

}


