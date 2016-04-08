model M1_coupling
import "m1.gaml"

global
{
}

experiment M1_coupling_exp parent:M1_exp type: gui
{
	list<A> getA{
		return list(A);
	}
	
	//if we redefine the output, i.e, a blank output, the displays in parent experiement dont show.
	output
	{
	}

}


