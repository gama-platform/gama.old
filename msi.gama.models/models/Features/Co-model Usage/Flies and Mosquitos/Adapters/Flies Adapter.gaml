/**
* Name: flies_coupling
* Author: HUYNH Quang Nghi
* Description: It is NOT supposed to launch. This is the coupling of Flies model. It is used in the "Complex Comodeling Example" as an interface. 
* Tags: comodel
*/
model flies_coupling
import "../Models/Flies.gaml"

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


