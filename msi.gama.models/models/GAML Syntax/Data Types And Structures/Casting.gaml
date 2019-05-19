/**
* Name: Casting Syntax
* Author: A. Drogoul & P. Taillandier
* Description: different ways of transforming objects and agents in GAML.
* Tags: cast, type
*/

model Casting

species to_int {
	init {
		write sample(int(1));
		write sample(int(1.0));
		write sample(int("1"));
		write sample(int("1.0"));
		write sample(int(°pink));
		write sample(int(true));
		write sample(int(self));
		write sample(int([]));
		write sample(int([0]));
		write sample(int({0,0,0}));
		write sample(int(0::0));
		write sample(int([0::0]));
		write sample(int(°meter));				
	}
}


species to_float{
	init {
		write sample(float(1));
		write sample(float(1.0));
		write sample(float("1"));
		write sample(float("1.0"));
		write sample(float(°pink));
		write sample(float(true));
		write sample(float(self));
		write sample(float([]));
		write sample(float([0]));
		write sample(float(0::0));
		write sample(float([0::0]));
		write sample(float({0,0,0}));
		write sample(float(°meter));				
	}
}


species to_string{
	init {
		write sample(string(1));
		write sample(string(1.0));
		write sample(string("1"));
		write sample(string("1.0"));
		write sample(string(°pink));
		write sample(string(true));
		write sample(string(self));
		write sample(string([]));
		write sample(string([0]));
		write sample(string({0,0,0}));
		write sample(string(°meter));				
	}
}

species to_point{
	init {
		write sample(point(1));
		write sample(point(1.0));
		write sample(point("1"));
		write sample(point("1.0"));
		write sample(point(°pink));
		write sample(point(true));
		write sample(point(self));
		write sample(point([]));
		write sample(point([0]));
		write sample(point(0::0));
		write sample(point([0::0]));
		write sample(point({0,0,0}));
		write sample(point(°meter));				
	}
}
species to_list{
	init {
		write sample(list(1));
		write sample(list(1.0));
		write sample(list("1"));
		write sample(list("1.0"));
		write sample(list(°pink));
		write sample(list(true));
		write sample(list(self));
		write sample(list([]));
		write sample(list([0]));
		write sample(list(0::0));
		write sample(list([0::0]));	
		write sample(list({0,0,0}));
		write sample(list(°meter));				
	}
}
species to_pair{
	init {
		write sample(pair(1));
		write sample(pair(1.0));
		write sample(pair("1"));
		write sample(pair("1.0"));
		write sample(pair(°pink));
		write sample(pair(true));
		write sample(pair(self));
		write sample(pair([]));
		write sample(pair([0]));
		write sample(pair(0::0));
		write sample(pair([0::0]));
		write sample(pair({0,0,0}));
		write sample(pair(°meter));				
	}
}
species to_map{
	init {
		write sample(map<float, int>(°meter));
		write sample(map(1));
		write sample(map(1.0));
		write sample(map("1"));
		write sample(map("1.0"));
		write sample(map(°pink));
		write sample(map(true));
		write sample(map(self));
		write sample(map([]));
		write sample(map([0]));
		write sample(map(0::0));
		write sample(map([0::0]));
		write sample(map({0,0,0}));
		write sample(map<int,string>(1));				
	}
}
experiment Casting type: gui{
	user_command "to int" {create to_int;}
	user_command "to float" {create to_float;}
	user_command "to string" {create to_string;}
	user_command "to point" {create to_point;}
	user_command "to list" {create to_list;}
	user_command "to pair" {create to_pair;}
	user_command "to map" {create to_map;}
}