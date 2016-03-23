/**
* Name: Ants_coupling
* Author: Lô
* Description: This is the coupling that manipulate the elements inside Ants  model and propose the function would be used outside.
* Tags: Tag1, Tag2, TagN
*/

model Ants_coupling

import "../../../Toy Models/Ants (Foraging and Sorting)/models/Ant Foraging (Complex).gaml" 


experiment Ants_coupling_exp type:gui  parent:Complex{
	
	list<ant> getAnts{
		return list(ant);
	}
	
	
	output{
		display Ants2D type: java2D {
			image '../images/soil.jpg' position: { 0.05, 0.05 } size: { 0.9, 0.9 };
			agents "agents" transparency: 0.7 position: { 0.05, 0.05 } size: { 0.9, 0.9 } value: (ant_grid as list) where ((each.food > 0) or (each.road > 0) or (each.is_nest)) ;
			species ant position: { 0.05, 0.05 } size: { 0.9, 0.9 } aspect: icon;					
		}
	}
}