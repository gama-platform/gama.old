/**
* Name: mosquitos_coupling
* Author: HUYNH Quang Nghi
* Description: It is NOT supposed to launch. This is the coupling of Mosquitos model. It is used in the "Complex Comodeling Example" as an interface. 
* Tags: comodel
*/
model mosquitos_coupling
import "../Models/Mosquitos.gaml"

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


