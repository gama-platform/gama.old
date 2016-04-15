model M2_coupling
import "m2.gaml"

global
{
}

experiment M2_coupling_exp parent:M2_exp type: gui
{
	list<B> getB{
		return list(B);
	}
	
	//if we redefine the output, i.e, a blank output, the displays in parent experiement dont show.
	output
	{
	}

}


