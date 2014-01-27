
model ville

global {
	file shape_file_batiments <- file("../includes/batiments.shp");
	file shape_file_routes <- file("../includes/routes.shp");
	geometry shape <- envelope(shape_file_routes);
	init {
		create batiment from: shape_file_batiments with: [type:: string(read("NATURE"))];
		create route from: shape_file_routes;
		create foyer number: 500;
	}
}

species batiment {
	string type;
	int capacite <- type = "Industrial" ? 0 : int(shape.area / 60.0);
	aspect geometrie {
		draw shape color: type = "Industrial" ? rgb("pink") : rgb("gray");
	}
}

species route {
	aspect geometrie {
		draw shape color: rgb("black");
	}
}

species foyer {
	float revenu <- gauss(1500, 500);
	batiment habitation;
	batiment lieu_travail;
	
	aspect revenu {
		int val <- int(255 * (revenu / 3000));
		draw circle(5) color: rgb(255 - val, val, 0);
	}
}

experiment ville type: gui {
	output {
		display carte_principale {
			species batiment aspect: geometrie;
			species route aspect: geometrie;
			species foyer aspect: revenu;
		}
	}
}