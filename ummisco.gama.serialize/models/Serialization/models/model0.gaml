/**
* Name: allTypes
* Author: bgaudou
* Description: Describe here the model and its experiments
* Tag : Tag1, Tag2, TagN
*/

model allTypes

global {
	int i <- 0;
	float f <- 3.0;
 	string s <- "hello";
	unknown u <- "bonjour";
	point po <- {4.6, 5.9, 10.9};
	rgb co <- rnd_color(255);
	geometry ge <- polyline([{34,67},{23,90}]); 
		
	list<int> li <- [1,2,7];
	// revoir matrix ...
	matrix<int> m <- matrix([[1,2],[3,4]]);
	map<string,int> mmmp <- map(["a"::1,"b"::6]);
	map<string, list<int>> mp <- map(['hello'::[1,2], 'titi'::[5,6]]);
	pair<int,string> p <- 3.4::"toto";

	graph g <- graph([]);
//	file fi ;
//	file f_csv <- csv_file("../includes/Bary_farmers_list.csv",",",true);
	
	// path
	// topology 	
	
	init {
		create people;	
	}
	
	reflex t when: true {
		write "i " + serialize(i);
		write "i " + unserialize(serialize(i));

		write "f " + serialize(f);
		write "f " + unserialize(serialize(f));
		
		write "po " + serialize(po);
		write "po " + unserialize(serialize(po));
		
		write "co " + serialize(co);
		write "co " + unserialize(serialize(co));	
		
		write "ge " + serialize(ge);
		write "ge " + unserialize(serialize(ge));
		
		write "li " + serialize(li);
		write "li " + unserialize(serialize(li));			

		write "m " + serialize(m);
		write "m " + unserialize(serialize(m));
		
		write "mmmp " + serialize(mmmp);
		write "mmmp " + unserialize(serialize(mmmp));	

		write "mp " + serialize(mp);
		write "mp " + unserialize(serialize(mp));	

		write "people(0) " + serialize(first(people));
		write "people(0) " + unserialize(serialize(first(people)));		

		write "people " + serialize(people);
		write "people " + unserialize(serialize(people));						
	}
	
}

species people {}

experiment simple type: gui {}

