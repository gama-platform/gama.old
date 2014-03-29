
model ville

global {
	init {
		create foyer number: 500;
	}
}

species batiment {
	string type;
	int capacite;
}

species route {
}

species foyer {
	float revenu <- gauss(1500, 500);
	bool est_satisfait ;
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
			species foyer aspect: revenu;
		}
	}
}