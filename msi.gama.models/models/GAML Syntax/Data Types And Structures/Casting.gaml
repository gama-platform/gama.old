/**
* Name: Casting Syntax
* Author: A. Drogoul & P. Taillandier
* Description: different ways of transforming objects and agents in GAML.
* Tags: cast, type
*/

model Casting

species cast_to_int {
	init {
		write sample(int(1));
		write sample(int(1.0));
		write sample(int("1"));
		write sample(int("1.0"));
		write sample(int(#pink));
		write sample(int(true));
		write sample(int(false));
		write sample(int(self));
		write sample(int([]));
		write sample(int([10]));
		write sample(int({1,2,3}));
		write sample(int(1::2));
		write sample(int([1::2]));
		write sample(int(#meter));				
	}
}


species cast_to_float{
	init {
		write sample(float(1));
		write sample(float(1.0));
		write sample(float("1"));
		write sample(float("1.0"));
		write sample(float(#pink));
		write sample(float(true));
		write sample(float(false));
		write sample(float(self));
		write sample(float([]));
		write sample(float([1]));
		write sample(float(1::2));
		write sample(float([1::2]));
		write sample(float({1,2,3}));
		write sample(float(#meter));				
	}
}


species cast_to_string{
	init {
		write sample(string(1));
		write sample(string(1.0));
		write sample(string("1"));
		write sample(string("1.0"));
		write sample(string(#pink));
		write sample(string(true));
		write sample(string(false));
		write sample(string(self));
		write sample(string([]));
		write sample(string([0]));
		write sample(string({0,0,0}));
		write sample(string(#meter));				
	}
}

species cast_to_point{
	init {
		write sample(point(1));
		write sample(point(1.0));
		write sample(point("1"));
		write sample(point("1.0"));
		write sample(point(#pink));
		write sample(point(true));
		write sample(point(false));
		write sample(point(self));
		write sample(point([]));
		write sample(point([2]));
		write sample(point(1::2));
		write sample(point([1::2]));
		write sample(point({1,2,3}));
		write sample(point(#meter));				
	}
}
species cast_to_list{
	init {
		write sample(list(1));
		write sample(list(1.0));
		write sample(list("1"));
		write sample(list("1.0"));
		write sample(list(#pink));
		write sample(list(true));
		write sample(list(false));
		write sample(list(self));
		write sample(list([]));
		write sample(list([0]));
		write sample(list(1::2));
		write sample(list([1::2]));	
		write sample(list({1,2,3}));
		write sample(list(#meter));				
	}
}
species cast_to_pair{
	init {
		write sample(pair(1));
		write sample(pair(1.0));
		write sample(pair("1"));
		write sample(pair("1.0"));
		write sample(pair(#pink));
		write sample(pair(true));
		write sample(pair(false));
		write sample(pair(self));
		write sample(pair([]));
		write sample(pair([1]));
		write sample(pair(1::2));
		write sample(pair([1::2]));
		write sample(pair({1,2,3}));
		write sample(pair(#meter));				
	}
}
species cast_to_map{
	init {
		write sample(map<float, int>(1.5#meter));
		write sample(map(1));
		write sample(map(1.5));
		write sample(map("1"));
		write sample(map("1.0"));
		write sample(map(#pink));
		write sample(map(true));
		write sample(map(false));
		write sample(map(self));
		write sample(map([]));
		write sample(map([1]));
		write sample(map([1,2,3]));
		write sample(map(1::2));
		write sample(map([1::2]));
		write sample(map({1,2,3}));
		write sample(map<int,string>(1));				
	}
}
experiment Casting type: gui{
	user_command "to int" {create cast_to_int;}
	user_command "to float" {create cast_to_float;}
	user_command "to string" {create cast_to_string;}
	user_command "to point" {create cast_to_point;}
	user_command "to list" {create cast_to_list;}
	user_command "to pair" {create cast_to_pair;}
	user_command "to map" {create cast_to_map;}
}