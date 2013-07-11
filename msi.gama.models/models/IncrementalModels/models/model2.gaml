/**
 *  model2
 *  Author: hqnghi
 *  Description: 
 */ model model2 /* Insert your model definition here */ global {
	file shape_file_buildings <- file('../includes/building.shp');
	geometry shape <- envelope(shape_file_buildings);
	init {
		create Buildings from: shape_file_buildings with: [type:: string(read('NATURE')), company::string(read('COMPANY'))] {
			if type = 'Industrial' {
				mycolor <- rgb('blue');
			} else if type = 'Residential' {
				mycolor <- rgb('red');
			}

		}

	}

}

entities {
	species Buildings {
		string type;
		string company;
		rgb mycolor;
		aspect asp1 {
			draw shape color: mycolor;
			if (company != "") {
				draw "" + company at: location size: 20 color:rgb('green');
			}

		}

	}

}

experiment exp1 type: gui {
	output {
		display disp1 refresh_every: 1 {
			species Buildings aspect: asp1;
		}

	}

}