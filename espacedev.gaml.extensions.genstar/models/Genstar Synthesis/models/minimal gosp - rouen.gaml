/**
* Name: minimal generation of synthetic population (gosp) exemple 
* Author: chapuisk
* Description: Provide minimal exemple on how to use aggregated statistical data (x2 attributes) to generate a synthtic population in Gama
* Tags: Tag1, Tag2, TagN
*/

model minimal_gosp

global {
	
	init {	
		generate species:people number: 10000 
		from:[csv_file("../includes/Age & Sexe-Tableau 1.csv",";")] 
		attributes:["Age"::["Moins de 5 ans", "5 à 9 ans", "10 à 14 ans", "15 à 19 ans", "20 à 24 ans", 
					  				"25 à 29 ans", "30 à 34 ans", "35 à 39 ans", "40 à 44 ans", "45 à 49 ans", 
									"50 à 54 ans", "55 à 59 ans", "60 à 64 ans", "65 à 69 ans", "70 à 74 ans", "75 à 79 ans", 
									"80 à 84 ans", "85 à 89 ans", "90 à 94 ans", "95 à 99 ans", "100 ans ou plus"],
						"Sexe"::["Hommes", "Femmes"]];
						
	}
}

species people {
	int Age;
	string Sexe;

	aspect default { 
		draw circle(0.5) color: #red border: #black;
	}
}

experiment Rouentemplate type: gui {
	output {
		display map {
			species people;
		}
		
		display c  type: 2d {
			chart "ages" type: histogram {
				loop i from: 0 to: 110 {
					data ""+i value: people count(each.Age = i);
				}
			}
		}
		
		display s  type: 2d {
			chart "sex" type: pie {
				loop se over: ["Hommes", "Femmes"] {
					data se value: people count(each.Sexe = se);
				}
			}
		}
	}
}