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
	list candidats of: candidat -> {candidat as list};
	list electeurs of: electeur; 
	int compteur_groupe_max <- 5;
	int compteur_groupe <- compteur_groupe_max;
	float entropie;
	bool candidat_joue <- false;
	list candidats_en_course of: candidat;
	init {
		create electeur number: nb_electors;
		do creation_candidats;
		set electeurs <- electeur as list;
		if (candidat_joue) {
			create candidat_joueur;
		}
	}
	
	action creation_candidats {
			switch distribution_candidats {
				match "Polygone" {
					let liste_points type: list of: point <- nb_candidates points_at 50.0;
					let cpt type: int <- 0;
					create candidat number: nb_candidates{
						set couleur <- rgb ([rnd(255), rnd(255), rnd(255)]); 
						set location <- liste_points at cpt;
						set cpt <- cpt + 1;
					}
				}
				match "Ligne" {
					let cpt type: int <- 0;
					create candidat number: nb_candidates{
						set couleur <- rgb ([rnd(255), rnd(255), rnd(255)]); 
						let x_cord type: float <- 200 * cpt / nb_candidates;
						let y_cord type: float <- 100;
						set location <- {x_cord, y_cord};
						set cpt <- cpt + 1;
					}
				}
				match "Diagonale" {
					let cpt type: int <- 0;
					create candidat number: nb_candidates{
						set couleur <- rgb ([rnd(255), rnd(255), rnd(255)]); 
						let x_cord type: float <- 200 * cpt / nb_candidates;
						let y_cord type: float <- x_cord;
						set location <- {x_cord, y_cord};
						set cpt <- cpt + 1;
					}
				}
			}
			set candidats_en_course <- candidats;	
	}
	
	reflex dynamique {
		ask electeurs {
			do deplacement;
		}
		ask candidats_en_course{
			do deplacement;
			set mes_electeurs <- [];
		}
		ask electeurs {
			do definition_candidat;
		}
		let nb_electors_max type: int <- 0;
		let candidat_elu type: candidat<- nil; 
		ask candidats_en_course{ 
			let nb_el type: int <- length(mes_electeurs) ;
			set pourcentage_vote <- (nb_el/nb_electors * 100) with_precision 2;
			if (nb_el > nb_electors_max) {
				set nb_electors_max <- nb_el;
				set candidat_elu <- self;
			}
		}
		ask candidats {
			set est_elu <- false; 
		}
		ask candidat_elu {
			set est_elu <- true; 
		}
	}
	
	reflex second_tour when: time = 52 {
		do tell message: "le second tour va commercer !!!"; 
		let finaliste1 type: candidat <- candidats_en_course with_max_of (each.pourcentage_vote);
		let finaliste2 type: candidat <- (candidats_en_course - finaliste1) with_max_of (each.pourcentage_vote);
		ask (candidats_en_course) {
			if (self != finaliste1 and self != finaliste2) {
				set actif <- false;
				set pourcentage_vote <- 0;
				remove self from: candidats_en_course;
			}
		}	
		
	}
	
	reflex resultats_finaux when: time = 72 {
		let elu type: candidat <- candidats_en_course with_max_of (each.pourcentage_vote);
		do tell message: "The winner is " + elu.name; 
		do halt;
	}
	
	reflex creation_groupe when: (strategie_candidats in ["Groupe", "Aleatoire"]) {
		 if (compteur_groupe = compteur_groupe_max) {
		 	ask groupe_electeurs as list {
		 	do die;
		 	}
			let groupes type: list of: list <- [];//<- (electeurs simple_clustering_by_envelope_distance seuil_attraction_electeurs);
			let geoms <- union(electeurs collect ((each.shape) buffer map(["distance"::float(seuil_attraction_electeurs) , "quadrantSegments"::4, "endCapStyle"::1])));
			loop geom over: geoms.geometries { 
				if (geom != nil and !empty(geom.points)) {
					set geom <- geom simplification 0.1;
					let els type: list <- (electeurs inside geom); 
					add els to: groupes;
				}
			}
			
			loop gp over: groupes {
			 	create groupe_electeurs {
					set effectif <- length(gp);
			 		set electeurs_dans_groupe <- gp;
			 		set location <- mean(electeurs_dans_groupe collect (each.location)) ;
			 	}
			 }	 
		}
		 set compteur_groupe <- compteur_groupe - 1;
		 if (compteur_groupe = 0) {set compteur_groupe <- compteur_groupe_max;}	
	}
	
	reflex calcule_entropie {
		set entropie <- 0;
		let abst type: float <- (nb_electors - sum (candidats_en_course  collect (length(each.mes_electeurs)))) / nb_electors;
		if (abst > 0) {
			set entropie <- entropie - (abst * ln(abst));
		}
		ask candidats_en_course {
			let p type: float <- length(mes_electeurs) / nb_electors;
			if (p > 0) {
				set entropie <- entropie - (p * ln(p));
			}
		}
		set entropie <- entropie / ln (length(candidats_en_course) + 1);
	}
}


entities {
	
	
	species groupe_electeurs {
		int effectif <- 0;
		list electeurs_dans_groupe of: electeur;
		
		
		aspect default {
			draw square(2) color: rgb("orange");
		} 
		
	}
	
	species electeur skills: [moving]{
		
		init {
			if (distribution_electeurs = "Normale") {
				let x_cord type: float <- max([0, min([200, gauss ({100, 35})])]);
				let y_cord type: float <- max([0, min([200, gauss ({100, 35})])]);
				set location <- {x_cord, y_cord};
			}
		}
		rgb couleur <- rgb('white');
		candidat mon_candidat;  
		
		aspect default {
			draw triangle(2) color: couleur ;
		} 
		action definition_candidat {
			set mon_candidat <- candidats_en_course with_min_of (self distance_to each);
			set mon_candidat <- (self distance_to mon_candidat < seuil_attraction_candidats) ? mon_candidat : nil;
			if (mon_candidat != nil) {
				add self to: mon_candidat.mes_electeurs; 
				set couleur <- mon_candidat.couleur;
			}
		}
		action deplacement {
			if ( rnd(100) > (poids_candidats)) {
				let mon_electeur type: electeur <- shuffle(electeurs) first_with ((self distance_to each) < seuil_attraction_electeurs);
				if (mon_electeur != nil) {
					do goto target:mon_electeur speed: distance_parcourue;
				} 
			} else {
				let le_candidat type: candidat <- one_of(candidats) ;
				if (le_candidat != nil) {
					let dist type: float <- self distance_to le_candidat;
					if dist < seuil_attraction_candidats {
						do goto target: le_candidat speed: distance_parcourue;
					} else if dist > seuil_repulsion_candidats {
						do goto target: point(location + location - le_candidat.location) speed: distance_parcourue;
					}	
				}
			}
			
		} 
		
	}
	species candidat_joueur parent: candidat control:user_only{
		user_panel "Control du candidat" {
			user_command "Haut" {
				do move heading: 0 speed: distance_parcourue;
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
				let rayon type: float <- 1 + (pourcentage_vote / 4.0);
				if (est_elu) {
					draw geometry:square( rayon *1.5) color: rgb("red"); 
					draw circle(rayon) color: couleur;
				} else {
					draw circle(rayon) color: couleur;
				}
				draw text:string(pourcentage_vote) size: 5 color: rgb("white");
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
			let mon_electeur type: electeur <- shuffle(electeurs) first_with ((self distance_to each) < seuil_attraction_electeurs);
			if (mon_electeur != nil) {
				do goto target:mon_electeur speed: distance_parcourue;
			} 
		}
		
		action strategie_2 {
			//s'�loigner des autres candidats
			let le_candidat type: candidat <- one_of(candidats - self) ;
			if (le_candidat != nil) {
				do goto target: point(location + location - le_candidat.location) speed: distance_parcourue;	
			}
		}
		
		action strategie_3 {
			//s'approcher d'un groupe
			let mon_groupe type: groupe_electeurs <- ((groupe_electeurs as list) where ((self distance_to each) < seuil_attraction_electeurs)) with_max_of (each.effectif);
			if (mon_groupe != nil) {
				do goto target:mon_groupe speed: distance_parcourue;
			} 
		}
		
		action strategie_4 {
			//s'approcher du candidat qui a le plus de voix
			let le_candidat type: candidat <- candidats with_max_of (pourcentage_vote) ;
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
	parameter "Vitesse de d�placement des electeurs vers un autre electeur : " var: distance_parcourue category: "Electeur";
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
			 	loop cand over: candidats {
			 		data legend: cand.name value:cand.pourcentage_vote color: cand.couleur ;
			 	}	
			}
			
			}
		display indicateurs {
			chart "Entropie de Shannon" type: series background: rgb('white') size: {1,0.5} position: {0, 0} {
				data "entropie" value: entropie color: rgb('blue') ;
			}
			chart "Richesse du d�bat public" type: series background: rgb('white') size: {1,0.5} position: {0, 0.5} {
				data "taux_couverture_espace" value: (union(candidats collect (each.shape buffer seuil_attraction_candidats)) intersection world.shape).area / 40000 color: rgb('blue') ;
			}
		}
	}
}
