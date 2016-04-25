/**
* Name: team2strategy
* Author: Julien
* Description: 
* Tags: Tag1, Tag2, TagN
*/

model team2strategy

import "./soccer_base.gaml"

species player_team2 parent:base_player {

}

species team2 parent:base_team {
	list<point> player_init_position <- [{20,20},{20,50},{20,80},{50,30},{50,70},{70,30},{70,70},{70,20},{90,50}];
}

