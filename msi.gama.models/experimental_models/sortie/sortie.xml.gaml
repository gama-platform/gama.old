model null
// gen by Xml2Gaml


global {
	int nbCellsVertically init: 50 parameter: 'true';
	int nbCellsHorizontally init: 50 parameter: 'true';
	int Initial_Nb_Of_Tree_Per_Species init: 25 parameter: 'true';
	float New_Seedling_Radius_cm init: 0.1 parameter: 'true';
	float Adult_Radius_cm init: 10 parameter: 'true';
	float Mortality_rate_basis init: 0.01 parameter: 'true';
	map to_create init: specieslist as_map list [each::[]];
	reflex when: !empty(to_create) {
		abstract entry;
		loop over: to_create var: entry {
			let sp type: species value: (key of entry) as species;
			let locations type: list of: point value: (value of entry) as list;
			create species: sp as: Tree number: length(locations) with: [location::locations];
		}
		let to_create value: specieslist as_map list [each::[]];
	}
	map candidates value: ((agents of_generic_species Tree) where (( each as Tree ).r > Adult_Radius_cm)) group_by species(each);
	const colors init: [WP::rgb('pink'), WA::rgb('magenta'), RO:: rgb('gray'), BC::rgb('cyan'),RM::rgb('orange'), SM::rgb('yellow'), Be::rgb('blue'), YB::rgb('green'), Hm::rgb('red')];
	matrix canopy_openness type: matrix size: {24,20} fill_with: 1.0 of: float;
	list neighbours2 init: [] of: Tree;
	list speciesMembers init: [] of: Tree;
	list intersecting_ibeams init: [] of: int;
	list poisson_cdf of: float;
	int k;
	float meanNbChildren;
	float T;
	float distance;
	bool too_close;
	bool on_the_right;
	float dist;
	float neigh_height;
	float neigh_cr;
	float second_term;
	float first_term;
	float zenith_angle_deg;
	float U;
	float m_;
	float x0;
	float x1;
	float y0;
	float y1;
	float h1;
	float h2;
	float neighb_cr;
	float neighb_height;
	float random01;
	float d1;
	const average_zenith_angle_of_sun init: 28.956;
	const b init: 0.009;
	var nbDeadTrees init: 0 type: int;
	int cellSizeX init: 1;
	int cellSizeY init: 1;
	const specieslist init: [Hm, YB, Be, SM, RM, BC, WP, WA, RO];
	init {
		abstract spec;
		loop over: specieslist var: spec {
			create species: spec number: Initial_Nb_Of_Tree_Per_Species as: Tree with: [r::(rnd(50))/10];
		}
	}
}
entities {
	species Tree skills: [situated, visible] {
		float r init: New_Seedling_Radius_cm value: r + p_delta_r;
		float GLI init: 0 value: p_GLI;
		float height init: 0 value: float (parameters at 'H1') * (1 - (exp (- float (parameters at 'H2')/float (parameters at 'H1')*2*r)));
		float cr init: 0 value: float (parameters at 'C1') * r;
		float cd init: 0 value: float (parameters at 'C2') * r;
		float p_GLI init: 0;
		float p_delta_r init: 0.0;
		aspect default {
			draw image: 'original.png' size: cr color: colors at species(self) rotate: rnd(360);
		}
		const parameters init: ['H1'::0, 'H2'::0,'C1'::0,'C2'::0,'G1'::0,'G2'::0,'M1'::0,'M2'::0,'R1'::0,'expmE1'::0];
		action LightInterception {
			put item: 1.0 in: canopy_openness all: true;
			set neighbours2 value: [];
			abstract neigh;
			loop over: (self neighbours_at 1) of_generic_species Tree var: neigh {
				set dist value: self distance_to neigh;
				set neigh_height value: neigh.height;
				set neigh_cr value: neigh.cr;
				if condition: (((dist) > neigh_cr) ? (neigh_height > (height + (((dist) - neigh_cr) * tan(45) ))) : (neigh_height > height)) {
					add item: neigh to: neighbours2;
				}
			}
			abstract i;
			loop from: 1 to: 12 var: i {
				abstract neighb;
				loop over: neighbours2 var: neighb {
					set x0 value: first(location);
					set y0 value: last(location);
					set x1 value: first (neighb.location);
					set y1 value: last (neighb.location);
					set T value: tan ((15*i) - (15/2));
					set neighb_cr value: neighb.cr;
					set distance value: (abs (x1 - (T*y1) + (T*y0) - x0)) / (sqrt (1 + (T^2)));
					set d1 value: self distance_to neighb;
					if condition: distance < neighb_cr {
						set too_close value: neighb_cr > d1;
						set on_the_right value: x1 > ((-1/T) * y1) + x0 + (y0/T);
						set intersecting_ibeams value: [];
						if condition: too_close or on_the_right {
							add to: intersecting_ibeams item: i - 1;
						}
						if condition: too_close or !on_the_right {
							add to: intersecting_ibeams item: i+12 - 1;
						}
						abstract inters_i;
						loop over: intersecting_ibeams var: inters_i {
							abstract j;
							loop from: 1 to: 10 var: j {
								set first_term value: sqrt ((d1^2) - (distance^2));
								set second_term value: sqrt ((neighb_cr^2) - (distance^2));
								set h1 value: first_term - (second_term);
								set h2 value: first_term + (second_term);
								set zenith_angle_deg value: 92.25 - (j * 4.5);
								set U value: tan(zenith_angle_deg);
								set neighb_height value: neighb.height;
								if condition: (neighb_height > (U * h1) + height) and ((neighb_height - (neighb.cd)) < (U * h2) + height) {
									put item: (float (canopy_openness at {inters_i, j})) * (((neighb.parameters) at 'expmE1') ^ ( 0.73936 + (0.009 * zenith_angle_deg))) in: canopy_openness at: {inters_i, j-1};
								}
							}
						}
					}
				}
			}
			set p_GLI value: sum(canopy_openness * 0.462962962962963);
		}
		action Growth {
			set p_delta_r value: r * (float (parameters at 'G1') * p_GLI)/((float (parameters at 'G1')/ float (parameters at 'G2')) + p_GLI);
		}
		action Death {
			set m_ value: float (parameters at 'M1') * exp (- float (parameters at 'M2') * p_delta_r);
			set m_ value: Mortality_rate_basis + (0.99 * ((2 * m_) - (m_^2)));
			if condition: flip(m_) {
				set nbDeadTrees value: nbDeadTrees + 1;
				do action: die;
			}
		}
		reflex {
			do action: LightInterception;
			do action: Growth;
			do action: Death;
		}
	}
	species Hm parent: Tree skills: [situated, visible] {
		const parameters init: ['H1'::0.73, 'H2'::29.6,'C1'::0.1,'C2'::0.846,'G1'::0.229,'G2'::0.051,'M1'::0.077,'M2'::59.7,'R1'::5.991,'expmE1'::(0.064*(0.001))];
	}
	species YB parent: Tree skills: [situated, visible] {
		const parameters init: ['H1'::1.89, 'H2'::23.2,'C1'::0.109,'C2'::0.540,'G1'::0.169,'G2'::0.137,'M1'::0.555,'M2'::26.7,'R1'::0.001,'expmE1'::(0.399*(0.001))];
	}
	species Be parent: Tree skills: [situated, visible] {
		const parameters init: ['H1'::1.06, 'H2'::34.6,'C1'::0.152,'C2'::0.664,'G1'::0.152,'G2'::0.075,'M1'::0.014,'M2'::2,'R1'::1.957,'expmE1'::(0.064*(0.001))];
	}
	species SM parent: Tree skills: [situated, visible] {
		const parameters init: ['H1'::24.8, 'H2'::1.87,'C1'::0.107,'C2'::0.580,'G1'::0.125,'G2'::0.159,'M1'::0.998,'M2'::47.9,'R1'::(0.744*(0.001)),'expmE1'::0.399];
	}
	species RM parent: Tree skills: [situated, visible] {
		const parameters init: ['H1'::25.7, 'H2'::1.89,'C1'::0.108,'C2'::0.490,'G1'::0.167,'G2'::0.027,'M1'::0.912,'M2'::68.8,'R1'::(0.363*(0.001)),'expmE1'::0.399];
	}
	species BC parent: Tree skills: [situated, visible] {
		const parameters init: ['H1'::30.8, 'H2'::1.35,'C1'::0.116,'C2'::0.370,'G1'::0.249,'G2'::0.064,'M1'::0.998,'M2'::48.5,'R1'::(0.775*(0.001)),'expmE1'::0.399];
	}
	species WP parent: Tree skills: [situated, visible] {
		const parameters init: ['H1'::38.4, 'H2'::1.0,'C1'::0.087,'C2'::0.413,'G1'::0.230,'G2'::0.019,'M1'::0.268,'M2'::46.7,'R1'::(0.103*(0.001)),'expmE1'::0.399];
	}
	species WA parent: Tree skills: [situated, visible] {
		const parameters init: ['H1'::32.4, 'H2'::1.68,'C1'::0.095,'C2'::0.319,'G1'::0.225,'G2'::0.025,'M1'::0.999,'M2'::51.5,'R1'::(0.092*(0.001)),'expmE1'::0.399];
	}
	species RO parent: Tree skills: [situated, visible] {
		const parameters init: ['H1'::33.6, 'H2'::1.26,'C1'::0.119,'C2'::0.413,'G1'::0.266,'G2'::0.022,'M1'::0.985,'M2'::93.8,'R1'::(0.607*(0.001)),'expmE1'::0.566];
	}
}
environment width: nbCellsHorizontally * cellSizeX height: nbCellsVertically * cellSizeY {
	grid forest_grid width: nbCellsHorizontally height: nbCellsVertically torus: false {
		set color value: [255, 255, 225 + rnd(30)] as rgb;
		reflex Reproduction {
			loop over: specieslist var: sp {
				set speciesMembers value: candidates at sp;
				set meanNbChildren value: 0.0;
				abstract treei;
				loop over: speciesMembers var: treei {
					set meanNbChildren value: meanNbChildren + (((2*((treei).r)/100)^2) * exp (-1 * float ((treei.parameters) at 'R1') * ((self distance_to (treei))^3)));
				}
				set poisson_cdf value: [(exp (-meanNbChildren))];
				set k value: 1;
				loop while: (last(poisson_cdf)) < 0.9999 {
					add item: (poisson_cdf at (length(poisson_cdf)-1)) + ((meanNbChildren^k)*(exp (-meanNbChildren))/fact(k)) to: poisson_cdf;
					set k value: k+1;
				}
				set random01 value: (rnd(10000))/10000;
				set k value: 0;
				loop while: (k < length(poisson_cdf)) and ((poisson_cdf at k) <= random01) {
					set k value: k+1;
				}
				loop from: 1 to: k var: i {
					add item: any_location_in(shape) to: to_create at sp;
				}
			}
		}
	}
}
output {
	display view refresh_every: 1 {
		grid forest_grid;
		species Hm transparency: 0.4;
		species YB transparency: 0.4;
		species Be transparency: 0.4;
		species SM transparency: 0.4;
		species RM transparency: 0.4;
		species BC transparency: 0.4;
		species WP transparency: 0.4;
		species WA transparency: 0.4;
		species RO transparency: 0.4;
	}
	display Chart refresh_every: 1 {
		chart name: 'Number of trees' type: series background: rgb('black') {
			data Hm value: length (Hm as list) color: rgb('red');
			data YB value: length (YB as list) color: rgb('green');
			data Be value: length (Be as list) color: rgb('blue');
			data SM value: length (SM as list) color: rgb('yellow');
			data RM value: length (RM as list) color: rgb('orange');
			data BC value: length (BC as list) color: rgb('cyan');
			data WP value: length (WP as list) color: rgb('pink');
			data WA value: length (WA as list) color: rgb('magenta');
			data RO value: length (RO as list) color: rgb('gray');
		}
	}
	monitor name: "time" value: (time * 5) as int as string + ' years';
	monitor name: "trees" value: length(agents);
	monitor name: "dead trees" value: nbDeadTrees;
}
