/**
 *  testweka
 *  Author: Patrick Taillandier
 *  Description: shows how to use the Weka operators
 */

model testweka

global {
	classifier chaid_c ;
	classifier j48_c ;
	classifier jrip_c ;
	classifier smo_c ;
	classifier mlp_c ;
	classifier aode_c ;
	classifier bn_c ;
	classifier rf_c ;
	classifier rep_tree_c;
	
	classifier smo_reg ;
	classifier rep_tree_reg;
	classifier gauss_reg;
	classifier rbf_reg;
	init {
		create bug number: 1000;
		chaid_c <- train_chaid(list(bug), ["weight", "size"],"sexe",["sexe"::["M", "F"]],map([]));
		list data <- bug collect ["weight"::each.weight,"size"::each.size,"sexe"::each.sexe];
		j48_c <- train_j48(data, [ "weight", "size"],"sexe",["sexe"::["M", "F"]],map([]));
		jrip_c <- train_jrip(data, [ "weight", "size"],"sexe",["sexe"::["M", "F"]],map([]));
		smo_c <- train_smo(list(bug), [ "weight", "size"],"sexe",["sexe"::["M", "F"]],map([]));
		mlp_c <- train_mlp(data, [ "weight", "size"],"sexe",["sexe"::["M", "F"]],map([]));
		bn_c <- train_bn(data, [ "weight", "size"],"sexe",["sexe"::["M", "F"]]);
		rf_c <- train_rf(data, [ "weight", "size"],"sexe",["sexe"::["M", "F"]]);
		rep_tree_c <- train_reptree(data, [ "weight", "size"],"sexe",["sexe"::["M", "F"]]);
		
		list data2 <- bug collect ["weight"::each.weight,"size"::each.size,"sexe"::each.sexe, "age"::each.age];
		smo_reg <- train_smo_reg(data2, [ "weight", "size","sexe"],"age",["sexe"::["M", "F"]]);
		rep_tree_reg <- train_reptree(data2, [ "weight", "size","sexe"],"age",["sexe"::["M", "F"]]);
		gauss_reg <- train_gauss(data2, [ "weight", "size","sexe"],"age",["sexe"::["M", "F"]]);
		rbf_reg <- train_rbf(data2, [ "weight", "size","sexe"],"age",["sexe"::["M", "F"]]);
		
		
	}
	
	reflex test {
		write " %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% ";
		map<string,float> errors;
		loop times: 100 {
			create bug {
				errors["j48"] <-  errors["j48"] + ((j48_c classify(self) = sexe) ? 0 : 1);
				errors["CHAID"] <-  errors["CHAID"] + ((chaid_c classify(self) = sexe) ? 0 : 1);
				errors["JRIP"] <-  errors["JRIP"] + ((jrip_c classify(self) = sexe) ? 0 : 1);
				errors["SMO"] <-  errors["SMO"] + ((smo_c classify(self) = sexe) ? 0 : 1);
				errors["MLP"] <-  errors["MLP"] + ((mlp_c classify(self) = sexe) ? 0 : 1);
				errors["BN"] <-  errors["BN"] + ((bn_c classify(self) = sexe) ? 0 : 1);
				errors["RF"] <-  errors["RF"] + ((rf_c classify(self) = sexe) ? 0 : 1);
				errors["REP TREE"] <-  errors["REP TREE"] + ((rep_tree_c classify(self) = sexe) ? 0 : 1);
			}
		}
		write "Error (%): " + errors;
		write "--------------";
		ask one_of(bug) {
			write "real:" + sexe;
			write "j48:" + j48_c classify(self);
			write "CHAID:" + chaid_c classify(["weight"::weight,"size"::size]);
			write "JRIP:" + jrip_c classify(["weight"::weight,"size"::size]);
			write "SMO:" + smo_c classify(["weight"::weight,"size"::size]);
			write "MLP:" + mlp_c classify(["weight"::weight,"size"::size]);
			write "BN:" + bn_c classify(["weight"::weight,"size"::size]);
			write "RF:" + rf_c classify(["weight"::weight,"size"::size]);
			write "REP TREE:" + rep_tree_c classify(["weight"::weight,"size"::size]);
			write " ------------------------------------";
			write "age: " + age;
			write "SMOReg: " + smo_reg classify(["weight"::weight,"size"::size, "sexe"::sexe]);
			write "REP_TREE:" + rep_tree_reg  classify(["weight"::weight,"size"::size, "sexe"::sexe]);
			write "GAUSS:" + gauss_reg  classify(["weight"::weight,"size"::size, "sexe"::sexe]);
			write "RBF:" + rbf_reg  classify(["weight"::weight,"size"::size, "sexe"::sexe]);
			
		}
	}
}

species bug {
	bool is_man <- flip(0.5);
	int age <- 18 + rnd(80);
	string sexe <- is_man ? "M" : "F";
	rgb color <- is_man ? #blue : #pink;
	float weight <- gauss((20 - age)/10 + (is_man ? 77: 62),  5) min: 20.0 max: 200.0;
	float size <- gauss((20 - age)/10 +(is_man ? 175: 163),  5) min: 120.0 max: 230.0;
	
	
	
	aspect weight {
		draw circle(2 * (1 + weight - 20.0) / 180.0) color: color;
	}
	aspect size {
		draw circle(2 * (1 + size - 150) / 80.0) color: color;
	}
	
}
experiment testweka type: gui {
	output {
		display map_weight {
			species bug aspect: weight;
		}
		display map_size {
			species bug aspect: size;
		}
		
	}
}
