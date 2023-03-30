/**
* Name: minimalgospmatrix
* Based on the internal empty template. 
* Author: kevinchapuis
* Tags: 
*/


model minimalgospmatrix


global {
	
	init {	
		generate species:people number: 1000
		from:{2,3} matrix_with rnd(1.0) 
		attributes:["Age"::["young","adult"],"Gender"::["-1","0", "1"]];
						
	}
}

species people {
	string Age;
	int Gender;

	aspect default { 
		draw circle(2) color: #red border: #black;
	}
}

experiment Rouentemplate type: gui {
	output {
		display map { species people; }
		display c  type: 2d { chart "ages" type: histogram { loop i over:["young","adult"] { data ""+i value: people count(each.Age = i); } } }
		display s  type: 2d { chart "sex" type: pie {  loop se over: ["-1","0", "1"] { data se value: people count(each.Gender = int(se)); } } }
	}
}

