/**
 *  vote
 *  Author: MAPS TEAM (Frederic Amblard, Thomas Louail, Romain Reulier, Paul Salze et Patrick Taillandier)
 *  Description: 
 */

model vote

global {
	int nb_electors <- 1500;
	int nb_candidates <- 7;
	int poids_candidats <- 50;
	int seuil_attraction_candidats <- 80;
	int seuil_repulsion_candidats <- 200;
	int seuil_attraction_electeurs <- 20;
	
	float distance_parcourue <- 7.0;
	string distribution_electeurs <- "Uniforme" among: ["Uniforme", "Normale"];
	string distribution_candidats <- "Polygone" among: ["Aleatoire", "Polygone", "Ligne", "Diagonale"];
	string strategie_candidats <- "Fixe" among: ["Fixe", "Faire les marches", "Distinction", "Groupe", "Se rapprocher du meilleur","Aleatoire" ];
	int compteur_groupe_max <- 5;
	int compteur_groupe <- compteur_groupe_max;
	float entropie;
	list candidats_en_course of: candidat;
	init {
		create electeur number: nb_electors;
		do creation_candidats;
	}
	
	action creation_candidats {
			switch distribution_candidats {
				match "Polygone" {
					list<point> liste_points <- list(nb_candidates points_at 50.0);
					int cpt <- 0;
					create candidat number: nb_candidates{
						 couleur <- rgb (rnd(255), rnd(255), rnd(255)); 
						 location <- liste_points at cpt;
						 cpt <- cpt + 1; 
					}
				}
				match "Ligne" {
					int cpt  <- 0;
					create candidat number: nb_candidates{
						couleur <- rgb ([rnd(255), rnd(255), rnd(255)]); 
						float x_cord  <- 200 * cpt / nb_candidates;
						float y_cord <- 100;
						location <- {x_cord, y_cord};
						cpt <- cpt + 1;
					}
				}
				match "Diagonale" {
					int cpt <- 0;
					create candidat number: nb_candidates{
						couleur <- rgb ([rnd(255), rnd(255), rnd(255)]); 
						float x_cord <- 200 * cpt / nb_candidates;
						float y_cord <- x_cord;
						location <- {x_cord, y_cord};
						cpt <- cpt + 1;
					}
				}
			}
			candidats_en_course <- list(copy(candidat));	
	}
	
	reflex dynamique {
		ask electeur {
			do deplacement;
		}
		ask candidats_en_course{
			do deplacement;
			mes_electeurs <- [];
		}
		ask electeur {
			do definition_candidat;
		}
		int nb_electors_max <- 0;
		candidat candidat_elu <- nil; 
		ask candidats_en_course{ 
			int nb_el <- length(mes_electeurs) ;
			pourcentage_vote <- (nb_el/nb_electors * 100) with_precision 2;
			if (nb_el > nb_electors_max) {
				nb_electors_max <- nb_el;
			 	candidat_elu <- self;
			}
		}
		ask candidat {
			est_elu <- false; 
		}
		ask candidat_elu {
			est_elu <- true; 
		}
	}
	
	reflex second_tour when: time = 52 {
		do tell message: "le second tour va commercer !!!"; 
		candidat finaliste1 <- candidats_en_course with_max_of (each.pourcentage_vote);
		candidat finaliste2 <- (candidats_en_course - finaliste1) with_max_of (each.pourcentage_vote);
		ask (candidats_en_course) {
			if (self != finaliste1 and self != finaliste2) {
				set actif <- false;
				set pourcentage_vote <- 0;
				remove self from: candidats_en_course;
			}
		}	
		
	}
	
	reflex resultats_finaux when: time = 72 {
		candidat elu <- candidats_en_course with_max_of (each.pourcentage_vote);
		do tell message: "The winner is " + elu.name; 
		do halt;
	}
	
	reflex creation_groupe when: (strategie_candidats in ["Groupe", "Aleatoire"]) {
		 if (compteur_groupe = compteur_groupe_max) {
		 	ask groupe_electeurs as list {
		 	do die;
		 	}
			list<list> groupes<- [];
			geometry geoms <- union(electeur collect ((each.shape) buffer map(["distance"::float(seuil_attraction_electeurs) , "quadrantSegments"::4, "endCapStyle"::1])));
			loop geom over: geoms.geometries { 
				if (geom != nil and !empty(geom.points)) {
					geom <- geom simplification 0.1;
					list els  <- (electeur inside geom); 
					add els to: groupes;
				}
			}
			
			loop gp over: groupes {
			 	create groupe_electeurs {
					 effectif <- length(gp);
			 		 electeurs_dans_groupe <- gp;
			 		 location <- mean(electeurs_dans_groupe collect (each.location)) ;
			 	}
			 }	 
		}
		 compteur_groupe <- compteur_groupe - 1;
		 if (compteur_groupe = 0) { compteur_groupe <- compteur_groupe_max;}	
	}
	
	reflex calcule_entropie {
		entropie <- 0;
		float abst <- (nb_electors - sum (candidats_en_course  collect (length(each.mes_electeurs)))) / nb_electors;
		if (abst > 0) {
			entropie <- entropie - (abst * ln(abst));
		}
		ask candidats_en_course {
			float p <- length(mes_electeurs) / nb_electors;
			if (p > 0) {
				entropie <- entropie - (p * ln(p));
			}
		}
		entropie <- entropie / ln (length(candidats_en_course) + 1);
	}
}


entities {
	
	species groupe_electeurs {
		int effectif <- 0;
		list<electeur> electeurs_dans_groupe ;
		aspect default {
			draw square(2) color: rgb("orange");
		} 
		
	}
	
	species electeur skills: [moving]{
		
		init {
			if (distribution_electeurs = "Normale") {
				float x_cord <- max([0.0, min([200.0, gauss ({100, 35})])]);
				float y_cord <- max([0.0, min([200.0, gauss ({100, 35})])]);
				location <- {x_cord, y_cord};
			}
		}
		rgb couleur <- rgb('white');
		candidat mon_candidat;  
		
		aspect default {
			draw triangle(2) color: couleur ;
		} 
		action definition_candidat {
			mon_candidat <- candidats_en_course with_min_of (self distance_to each);
			mon_candidat <- (self distance_to mon_candidat < seuil_attraction_candidats) ? mon_candidat : nil;
			if (mon_candidat != nil) {
				add self to: mon_candidat.mes_electeurs; 
				couleur <- mon_candidat.couleur;
			}
		}
		action deplacement {
			if ( rnd(100) > (poids_candidats)) {
				electeur mon_electeur <- shuffle(electeur) first_with ((self distance_to each) < seuil_attraction_electeurs);
				if (mon_electeur != nil) {
					do goto target:mon_electeur speed: distance_parcourue;
				} 
			} else {
				candidat le_candidat <- one_of(candidat) ;
				if (le_candidat != nil) {
					float dist <- self distance_to le_candidat;
					if dist < seuil_attraction_candidats {
						do goto target: le_candidat speed: distance_parcourue;
					} else if dist > seuil_repulsion_candidats {
						do goto target: point(location + location - le_candidat.location) speed: distance_parcourue;
					}	
				}
			}
			
		} 
		
	}
	
	species candidat skills:[moving]{
		rgb couleur <- rgb([100 + rnd(155),100 + rnd(155),100 + rnd(155)]);
		bool actif <- true;
		float pourcentage_vote; 
		list mes_electeurs of: electeur;
		bool est_elu <- false;
		aspect default {
			draw circle(3) color: couleur;
		} 
		aspect dynamique {
			if (actif) {
				float rayon  <- 1 + (pourcentage_vote / 4.0);
				if (est_elu) {
					draw square( rayon *1.5) color: rgb("red"); 
					draw circle(rayon) color: couleur;
				} else {
					draw circle(rayon) color: couleur;
				}
				draw string(pourcentage_vote) size: 5 color: rgb("white");
			}
		}
		
		action deplacement {
			switch strategie_candidats {
				match "Fixe" {}
				match "Faire les marches" {do strategie_1;}
				match "Distinction" {do strategie_2;}
				match "Groupe" {do strategie_3;}
				match "Se rapprocher du meilleur" {do strategie_4;}
				match "Aleatoire" { 
					switch (rnd(4)) {
						match 0 {}
						match 1 {do strategie_1;}	
						match 2 {do strategie_2;}	
						match 3 {do strategie_3;}	
						match 4 {do strategie_4;}		
					}
				}
			}
		}
		
		action strategie_1 {
			//se rapprocher des votants
			electeur mon_electeur <- shuffle(electeur) first_with ((self distance_to each) < seuil_attraction_electeurs);
			if (mon_electeur != nil) {
				do goto target:mon_electeur speed: distance_parcourue;
			} 
		}
		
		action strategie_2 {
			//s'eloigner des autres candidats
			list<candidat> cands <- list(copy(candidat));
			remove self from: cands;
			candidat le_candidat <- one_of(cands) ;
			if (le_candidat != nil) {
				do goto target: point(location + location - le_candidat.location) speed: distance_parcourue;	
			}
		}
		
		action strategie_3 {
			//s'approcher d'un groupe
			groupe_electeurs mon_groupe  <- (groupe_electeurs where ((self distance_to each) < seuil_attraction_electeurs)) with_max_of (each.effectif);
			if (mon_groupe != nil) {
				do goto target:mon_groupe speed: distance_parcourue;
			} 
		}
		
		action strategie_4 {
			//s'approcher du candidat qui a le plus de voix
			candidat le_candidat <- candidat with_max_of (pourcentage_vote) ;
			if (le_candidat != nil) {
				do goto target:le_candidat speed: distance_parcourue;	
			}
		}
		
	}
}

environment bounds: {200, 200};

experiment vote type: gui {
	/** Insert here the definition of the input and output of the model */
	parameter "Nombre d'electeurs : " var: nb_electors category: "Electeur";
	parameter "Vitesse de deplacement des electeurs vers un autre electeur : " var: distance_parcourue category: "Electeur";
	parameter "Distance d'attraction entre electeurs : " var: seuil_attraction_electeurs category: "Electeur";
	parameter "Nombre de candidats : " var: nb_candidates category: "Candidat";
	parameter "Distance d'attraction des candidats sur les electeurs : " var: seuil_attraction_candidats category: "Electeur";
	
	parameter "Distance de respulsion des candidats sur les electeurs : " var: seuil_repulsion_candidats category: "Electeur";
	parameter "Poids des candidats : " var: poids_candidats category: "Candidat";
	
	parameter "Type de distribution des electeurs : " var: distribution_electeurs category: "Electeur";
	parameter "Type de distribution des candidats : " var: distribution_candidats category: "Candidat";
	parameter "Strategie des candidats : " var: strategie_candidats category: "Candidat";
	
	output {
		display main background: rgb("black") {
			species electeur aspect: default;
			
			species candidat aspect: dynamique;
			species groupe_electeurs;
			
		}
		display votants {
			chart "Repartition des votants" type: pie background: rgb('white')  {
			 	loop cand over: candidat {
			 		data legend: cand.name value:cand.pourcentage_vote color: cand.couleur ;
			 	}	
			}
			
			}
		display indicateurs {
			chart "Entropie de Shannon" type: series background: rgb('white') size: {1,0.5} position: {0, 0} {
				data "entropie" value: entropie color: rgb('blue') ;
			}
			chart "Richesse du debat public" type: series background: rgb('white') size: {1,0.5} position: {0, 0.5} {
				data "taux_couverture_espace" value: (union(candidat collect (each.shape buffer seuil_attraction_candidats)) intersection world.shape).area / 40000 color: rgb('blue') ;
			}
		}
	}
}
