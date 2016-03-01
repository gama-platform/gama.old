
model ville

global {
	file shape_file_batiments <- file("../includes/batiments.shp");
	file shape_file_routes <- file("../includes/routes.shp");
	geometry shape <- envelope(shape_file_routes);
	graph<point, route> reseau_route;
	
	init {
		create route from: shape_file_routes;
		reseau_route <- as_edge_graph(route);
		create batiment from: shape_file_batiments with: [type:: string(read("NATURE"))];
		create foyer number: 500;
	}
} 

species foyer {
	float revenu <- gauss(1500, 500);
	bool est_satisfait update: calculer_satisfaction();
	batiment habitation;
	batiment lieu_travail;
	init {
		lieu_travail <- one_of(batiment where (each.type = "Industrial"));
		habitation <- choisir_batiment(); 
		do emmenager;
	}
	bool calculer_satisfaction {
		list<foyer> voisins <- foyer at_distance 50.0;
		float revenu_moyen <- mean(voisins collect (each.revenu));
		return empty(voisins) or (revenu_moyen > (revenu * 0.7) and revenu_moyen < (revenu / 0.7));
	}
	action emmenager {
		habitation.capacite <- habitation.capacite - 1;
		habitation.foyers << self;
		location <- any_location_in(habitation.shape) + {0,0, habitation.hauteur};
	}
	action demenager {
		habitation.capacite <- habitation.capacite + 1;
		remove self from: habitation.foyers;
	}
	batiment choisir_batiment {
		return one_of(batiment where ((each.capacite >0) and ( each.distances[lieu_travail]< 1000.0)));
	}
	reflex demenagement when: !est_satisfait {
		do demenager;
		habitation <- choisir_batiment();
		do emmenager;
	}
	aspect revenu {
		int val <- int(255 * (revenu / 3000));
		draw sphere(5) color: rgb(255 - val, val, 0);
	}
}
species batiment {
	string type;
	int capacite <- type = "Industrial" ? 0 : int(shape.area / 70.0);
	map<batiment,float> distances;
	int hauteur <- 5 + rnd(10);
	list<foyer> foyers ;
	float revenu_moyen update: empty(foyer) ? 0.0 : mean (foyers collect each.revenu);
	init {
		loop bat over: batiment where (each.type = "Industrial") {
			put (topology(reseau_route) distance_between [self,bat]) at: bat in: distances;
		}
	}
	aspect geometrie {
		draw shape color: type = "Industrial" ? #pink : #gray depth: hauteur;
	}
	aspect information_foyer {
		draw shape color: type = "Industrial" ? #pink : (empty(foyers) ? #gray : rgb(int(255 * (1 - (revenu_moyen / 3000))), int(255 * (revenu_moyen / 3000)), 0)) depth: length(foyers);
	}
}
species route {
	aspect geometrie {
		draw shape color: #black;
	}
}
experiment ville type: gui {
	output {
		display carte_principale type: java2D ambient_light: 100{
			species batiment aspect: geometrie;
			species route aspect: geometrie;
			species foyer aspect: revenu;
		}
		display carte_batiment type: opengl ambient_light: 100{
			species batiment aspect: information_foyer;
		}
	}
}