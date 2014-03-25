/**
 *  Poker
 *  Author: Arthur Bernard
 *  Description: 
 */

model Poker

global {
	/**
	 * Nombre de joueurs � la table
	 */
	int nb_joueurs <- 12 min : 2 max : 20;
	
	/**
	 * Argent de d�part pour chaque joueur
	 */
	int argent_init <- 1000 min : 10;
	
	/**
	 * Nombre limite de raises que peut faire un joueur � chaque tour
	 */
	int max_raises <- 3 min : 1 max : 10;
	
	/**
	 * Permet d'arr�ter la simulation au bout d'un certain temps
	 */
	int limite_temps <- 30000;
	
	/**
	 * Deck des 52 cartes.
	 */
	list deck of: int <- [
		101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113,
		201, 202, 203, 204, 205, 206, 207, 208, 209, 210, 211, 212, 213,
		301, 302, 303, 304, 305, 306, 307, 308, 309, 310, 311, 312, 313,
		401, 402, 403, 404, 405, 406, 407, 408, 409, 410, 411, 412, 413
	] const: true;
	
	/**
	 * Correspondance entre les cartes et les images
	 */
	 map cartes_images <- [
	 	101::101, 102::102, 103::103, 104::104, 105::105, 106::106, 107::107, 108::108, 109::109, 110::110, 111::111, 112::112, 113::113,
		201::114, 202::115, 203::116, 204::117, 205::118, 206::119, 207::120, 208::121, 209::122, 210::123, 211::124, 212::125, 213::126,
		301::127, 302::128, 303::129, 304::130, 305::131, 306::132, 307::133, 308::134, 309::135, 310::136, 311::137, 312::138, 313::139,
		401::140, 402::141, 403::142, 404::143, 405::144, 406::145, 407::146, 408::147, 409::148, 410::149, 411::150, 412::151, 413::152
	 ] const: true;
	 
	 /**
	  * Couleurs pour l'affichage de l'histogramme
	  */
	 list couleurs of: rgb <- [rgb("red"), rgb("blue"), rgb("green"), rgb("magenta"), rgb("cyan"), rgb("darkGray"), rgb("gray"), 
	 	rgb("lightGray"), rgb("orange"), rgb("pink"), rgb("yellow"), rgb("black"), rgb([63, 109, 61])
	 ];
	
	/**
	 * Ajouter ici les parts des diff�rents agents � cr�er
	 */
	map partsAgents <- [1::0.0, 2::0.0, 3::0.5, 4::0.5];
	
	/**
	 * Map utilis�e pour contrer le fait qu'une map contenant directement des pairs (species::part) ne marche
	 * pas en param�tres. C'est un peu lourd mais �a permet de faire l'affaire
	 */
	map correspondance <- [1::JoueurZISuiveur, 2::JoueurZIAleatoire, 3::JoueurPrudent, 4::JoueurBluffer];
	
	// ------ Joueurs ZIAl�atoires ------
	/**
	 * Probabilit� de se coucher � chaque tour
	 */
	float proba_coucher_param <- 5.0 min : 0.0 max : 100.0;

	/**
	 * Probabilit� de faire une relance � chaque tour
	 */
	float proba_relance_param <- 20.0 min : 0.0 max : 100.0;

	/**
	 * Probabilit� de suivre � chaque tour
	 */
	float proba_suivre_param <- 75.0 min : 0.0 max : 100.0;
	

	// ------ Joueurs prudents ------
	/**
	 * Seuil de confiance � partir duquel le joueur prudent estime qu'il peut suivre
	 */
	float seuil_suivre_param_prudent <- 40.0 min : 0.0 max : 100.0;
	
	/**
	 * Seuil de confiance � partir duquel le joueur prudent estime qu'il peut relancer
	 */
	float seuil_relance_param_prudent <- 80.0 min : 0.0 max : 100.0;
	
	/**
	 * Part maximale de son argent que le joueur prudent peut claquer dans une relance
	 */
	float max_relance_param_prudent <- 0.1 min : 0.0 max : 1.0;


	// ------ Joueurs bluffers ------
	/**
	 * Seuil de confiance � partir duquel le joueur prudent estime qu'il peut suivre
	 */
	float seuil_suivre_param_bluffer <- 30.0 min : 0.0 max : 100.0;
	
	/**
	 * Seuil de confiance � partir duquel le joueur prudent estime qu'il peut relancer
	 */
	float seuil_relance_param_bluffer <- 70.0 min : 0.0 max : 100.0;
	
	/**
	 * Part maximale de son argent que le joueur prudent peut claquer dans une relance
	 */
	float max_relance_param_bluffer <- 0.25 min : 0.0 max : 1.0;
	
	/**
	 * Part minimale de son argent que le joueur bluffer va mettre dans une relance de bluff
	 */
	float min_relance_param <- 0.25 min : 0.0 max : 1.0;


	
	/**
	 * Liste des joueurs � la table
	 */
	list joueurs <- [] of: Joueur;
	
	/**
	 * Liste initiale de tous les joueurs, par simplicit�
	 */
	list all_joueurs <- [] of: Joueur;
	
	/**
	 * Paquet de cartes courant.
	 */
	list cartes <- [] of: int;
	
	/**
	 * Liste des cartes sur la table communes au joueur
	 */
	list cartes_communes <- [] of: int;
	
	/**
	 * Blind courante
	 */
	 int blind <- 20;
	
	/**
	 * Pot courant
	 */
	int pot <- 0;
	
	/**
	 * Mise courante
	 */
	int miseGlobale <- 0;
	
	/**
	 * Permet de conna�tre le premier joueur (eh oui !)
	 */
	int premier_joueur <- 0;
	
	/**
	 * Joueur courant
	 */
	int joueur_courant <- premier_joueur;
	 
	 /**
	  * Jeton pour faire jouer les joueurs les uns apr�s les autres
	  */
	bool jeton <- false;
	
	/**
	 * Permet de savoir quand les joueurs ont arr�t� de relancer la mise
	 */
	bool no_raise <- true;
	 
	 /**
	  * Bool�en permettant de g�rer le cas assez particulier o� le joueur qui se couche
	  * est le premier joueur.
	  */
	bool pas_de_tour <- true;
	 
	 /**
	  * Permet de savoir quand un tour d'ench�res est termin�
	  */
	bool encheres_finies <- false;
	
	/**
	 * Dealer courant
	 */
	int dealer <- -1;
	
	/**
	 * Permet de conna�tre le stade de la partie :
	 * -1 - Partie pas encore lanc�e
	 * 0 - Pr�-flop
	 * 1 - Flop
	 * 2 - Turn
	 * 3 - River
	 */
	int etape;
	
	/**
	 * Pour stocker le vainqueur de la simulation
	 */
	Joueur vainqueur <- nil;
	
	/**
	 * Initialisation de la simulation
	 */
	init {
		// Ajouter ici la cr�ation des diff�rents agents
		loop paire over : partsAgents.pairs {
			let nbAgents type: int <- round(float(paire.value) * nb_joueurs);
			let agents type: list <- [];
			create (correspondance at paire.key) number: nbAgents returns: agents;
			
			loop joueur over : agents as list {
				add joueur to : joueurs;
			}
		}
				
		// Combler ici pour qu'on ait bien nb_joueurs (si jamais les floors malheureux
		// ont cr�� moins de joueurs). Il est bien �vident pr�f�rable de s'arranger pour
		// que �a n'arrive pas
		if(length(joueurs) < nb_joueurs) {
			// Ici on choisit de combler avec la premi�re esp�ce trouv�e
			let joueurs_en_plus type: list <- [];
			create species_of (joueurs at 0) number : nb_joueurs - length(joueurs) returns : joueurs_en_plus;
			
			loop joueur over : joueurs_en_plus as list {
				add joueur to : joueurs;
			} 
		}
		
		// Si on a merd� au niveau des probabilit�s et qu'il y a trop d'agents, on supprime le surplus
		// � la bourrin (fallait pas merder !)
		if(length(joueurs) > nb_joueurs) {
			let nb_joueurs_remove type: int <- length(joueurs) - nb_joueurs;
			
			loop while : nb_joueurs_remove > 0 {
				remove (joueurs at 0) from : joueurs;
				set nb_joueurs_remove <- nb_joueurs_remove - 1;
			}			
		}
		
		set all_joueurs <- copy(joueurs);
		do set_positions;
		
		do init_partie;
	}


	/**
	 * --- REFLEX ---
	 */

	/**
	 * Arr�te la simulation quand on a trouv� le vainqueur
	 */
	reflex fin_vainqueur when: vainqueur != nil {
		let mess type: string <- "Joueur " + vainqueur + " a gagn� !"; 
		do tell message: mess;
		
		// On log les r�sultats de la simulation
		let log type: list <- [nb_joueurs, argent_init, blind, max_raises, limite_temps];
		let nom_log type: string <- "log";
		
		// On log les parts des diff�rents agents
		loop pair over : partsAgents {
			add pair to: log;
		}
		
		// Puis l'esp�ce du vainqueur
		if(species_of(vainqueur) = JoueurZISuiveur) {
			add 1 to : log;
		}
		else if(species_of(vainqueur) = JoueurZIAleatoire) {
			add 2 to : log;
		}
		else if(species_of(vainqueur) = JoueurPrudent) {
			add 3 to : log;
		}
		else if(species_of(vainqueur) = JoueurBluffer) {
			add 4 to : log;
		}
		else {
			add -1 to : log;
		}
		
		save log to : nom_log + ".csv" type: "csv";
		
		do halt;
	}
	
	/**
	 * Arr�te la simulation quand la limite de temps a �t� atteinte
	 */
	reflex fin_temps when: time >= limite_temps {
		// Tous les joueurs encore en jeu sont consid�r�s � �galit�		
		let mess type: string <- "Les joueurs : ";
		
		let index type: int <- 0;
		loop while : index < length(joueurs) - 1 {
			set mess <- mess + string(joueurs at index) + ", ";
			set index <- index + 1;
		}
		set mess <- mess + string(joueurs at index) + " sont � �galit� !";
		 
		do tell message: mess;
		
		// On log les r�sultats de la simulation
		let log type: list <- [nb_joueurs, argent_init, blind, max_raises, limite_temps];
		let nom_log type: string <- "log";
		
		// On log les parts des diff�rents agents
		loop pair over : partsAgents {
			add pair to: log;
		}

		// Dans un premier temps, on ajoute -1 pour signifier le fait qu'on ait une �galit�
		add -1 to : log;
		
		// On log les joueurs � �galit� avec leur argent, dans l'ordre
		let listOrd type: list of: Joueur <- copy(joueurs);
		set listOrd <- listOrd sort_by each.argent;
		
		set index <- length(listOrd) - 1;
		loop while : index >= 0 {
			let joueur type: Joueur <- listOrd at index;
			if(species_of(joueur) = JoueurZISuiveur) {
				add 1 to : log;
			}
			else if(species_of(joueur) = JoueurZIAleatoire) {
				add 2 to : log;
			}
			else if(species_of(joueur) = JoueurPrudent) {
				add 3 to : log;
			}
			else if(species_of(joueur) = JoueurBluffer) {
				add 4 to : log;
			}
			else {
				add -1 to : log;
			}
			add joueur.argent to : log;
			set index <- index - 1;
		}
		
		save log to: (nom_log + ".csv") type: "csv";
		
		do halt;
	}

	/**
	 * Reflex pour lancer la partie
	 */	
	reflex lancer_partie when: time > 0 and etape = -1 {
		set etape <- 0;
		set jeton <- true;
		
		// On fait jouer le premier joueur
		set joueur_courant <- premier_joueur;
		do donner_jeton joueur : premier_joueur;
	}
	
	/**
	 * Reflex pour passer le jeton au joueur suivant
	 */
	reflex joueur_suivant when: !encheres_finies and jeton {					
		// Si tout le monde ne s'est pas couch�
		if(length(joueurs where (!each.couche)) > 1){
			// On cherche le joueur suivant
			let index type: int <- joueur_courant;
			let joueur_suiv type: int <- -1;
			loop while : joueur_suiv = -1 and !encheres_finies {
				set index <- (index + 1) mod length(joueurs);
				
				// Les joueurs en tapis n'ont pas � miser, donc pas � jouer
				if(!(joueurs at index).couche) {
					if(!(joueurs at index).tapis) {
						set joueur_suiv <- index;
					}
					else {
						// On ne fait pas miser les joueurs en tapis mais il faut v�rifier
						// qu'on ne revienne sur le premier joueur qui peut �tre en tapis
						if(index = premier_joueur) {
							set encheres_finies <- true;
						}
					}
				}
			}
			
			if(joueur_suiv = premier_joueur) {
				if(pas_de_tour) {
					set pas_de_tour <- false;
				}
				else {
					// On a fait un tour complet
					set encheres_finies <- true;
				}
			}
			
			// On fait joueur le joueur suivant
			if(!encheres_finies) {
				set joueur_courant <- joueur_suiv;
				
				let joueurProut type: Joueur <- joueurs at joueur_suiv;
				do donner_jeton joueur : joueur_suiv;
			}
		}
	}
	
	/**
	 * Quand les ench�res sont finies, on lance l'�tape suivante
	 */
	reflex etape_suivante when: encheres_finies and jeton {
		switch(etape) {
			match 0 {
				// Etape de mises de pr�-flop finie
				
				// On r�initialise le nombre de raises pour ce round et les
				// combinaisons trouv�es par les joueurs
				loop joueur over : joueurs {
					set joueur.nb_raises <- 0;
					set joueur.type_meilleure_combinaison <- -1;
				}
				
				// On br�le une carte
				do bruler_carte;
				
				// Et on �tale le flop
				add self pop_card [] to : cartes_communes;
				add self pop_card [] to : cartes_communes;
				add self pop_card [] to : cartes_communes;
				
				// On d�finit le premier joueur :
				// joueur � la gauche du dealer
				let found type: bool <- false;
				let index type: int <- dealer;
				let cpt type: int <- 0;
				loop while : !found and cpt < length(joueurs) {
					set index <- (index + 1) mod length(joueurs);
					
					if(!(joueurs at index).couche and !(joueurs at index).tapis) {
						set premier_joueur <- index;
						set found <- true;
					}
					
					set cpt <- cpt + 1;
				}
				
				set encheres_finies <- false;
				
				// On lance le prochain tour d'ench�res
				set joueur_courant <- premier_joueur;
				set no_raise <- true;
				set pas_de_tour <- false;
				
				// Si jamais tous les joueurs non couch�s sont tapis, on ne
				// lance pas de tour d'ench�re
				if(length(joueurs where (!each.couche and !each.tapis)) = 0) {
					set encheres_finies <- true;
				}
				else {
					do donner_jeton joueur : joueur_courant;
				}
		
				set etape <- etape + 1;
			}
		
			match 1 {
				// Etape de mises de flop finie
				
				// On r�initialise le nombre de raises pour ce round et les
				// combinaisons trouv�es par les joueurs
				loop joueur over : joueurs {
					set joueur.nb_raises <- 0;
					set joueur.type_meilleure_combinaison <- -1;
				}
				
				// On br�le une carte
				do bruler_carte;
				
				// Et on �tale le turn
				add self pop_card [] to : cartes_communes;
				
				// On d�finit le premier joueur :
				// joueur � la gauche du dealer
				let found type: bool <- false;
				let index type: int <- dealer;
				let cpt type: int <- 0;
				loop while : !found and cpt < length(joueurs) {
					set index <- (index + 1) mod length(joueurs);
					
					if(!(joueurs at index).couche and !(joueurs at index).tapis) {
						set premier_joueur <- index;
						set found <- true;
					}
					
					set cpt <- cpt + 1;
				}
				
				set encheres_finies <- false;
				
				// On lance le prochain tour d'ench�res
				set joueur_courant <- premier_joueur;
				set no_raise <- true;
				set pas_de_tour <- false;
				
				// Si jamais tous les joueurs non couch�s sont tapis, on ne
				// lance pas de tour d'ench�re
				if(length(joueurs where (!each.couche and !each.tapis)) = 0) {
					set encheres_finies <- true;
				}
				else {
					do donner_jeton joueur : joueur_courant;
				}
		
				set etape <- etape + 1;
			}
		
			match 2 {
				// Etape de mises de turn finie
				
				// On r�initialise le nombre de raises pour ce round et les
				// combinaisons trouv�es par les joueurs
				loop joueur over : joueurs {
					set joueur.nb_raises <- 0;
					set joueur.type_meilleure_combinaison <- -1;
				}
				
				// On br�le une carte
				do bruler_carte;
				
				// Et on �tale la river
				add self pop_card [] to : cartes_communes;
				
				// On d�finit le premier joueur :
				// joueur � la gauche du dealer
				let found type: bool <- false;
				let index type: int <- dealer;
				let cpt type: int <- 0;
				loop while : !found and cpt < length(joueurs) {
					set index <- (index + 1) mod length(joueurs);
					
					if(!(joueurs at index).couche and !(joueurs at index).tapis) {
						set premier_joueur <- index;
						set found <- true;
					}
					
					set cpt <- cpt + 1;
				}
				
				set encheres_finies <- false;
				
				// On lance le prochain tour d'ench�res
				set joueur_courant <- premier_joueur;
				set no_raise <- true;
				set pas_de_tour <- false;
				
				// Si jamais tous les joueurs non couch�s sont tapis, on ne
				// lance pas de tour d'ench�re
				if(length(joueurs where (!each.couche and !each.tapis)) = 0) {
					set encheres_finies <- true;
				}
				else {
					do donner_jeton joueur : joueur_courant;
				}
						
				set etape <- etape + 1;
			}
		
			match 3 {
				// On cherche les meilleures combinaisons des joueurs pour lesquels il le faut
				loop index from : 0 to : length(joueurs) - 1 {
					if(!(joueurs at index).couche and ((joueurs at index).type_meilleure_combinaison = -1)) {
						do trouver_meilleure_combinaison joueur : index;
					}
				}
				
				// Etape de mises de river finie
				// On passe � l'abattage des cartes
				let meilleur_joueur type: int <- -1;

				// On fait le classement des joueurs
				// Les joueurs sont les cl�s, leur classement les valeurs
				let classement type: list <- [];			
				let index type: int <- 0;
				loop while : index < length(joueurs) {
					if(!(joueurs at index).couche) {
						if(meilleur_joueur = -1) {
							set meilleur_joueur <- index;
							let liste type: list of: int <- [];
							add index to : liste;
							add liste to : classement;
						}
						else {
							// On regarde o� placer ce joueur
							let indexListe type: int <- 0;
							let place type: bool <- false;
							let classementTmp type: list <- [];
							loop while : indexListe < length(classement) {
								let meilleur type: int <- self choisir_meilleur [joueur1 :: index, joueur2 :: (((classement at indexListe) as list) at 0) as int];

								if(!place and meilleur = 1) {
									let liste type: list of: int <- [];
									add index to : liste;
									add liste to : classementTmp;
									//add liste at : indexListe to : classement;
									set place <- true;	
								}
								else if(!place and meilleur = 0) {
									add index to : (classement at indexListe) as list;
									set place <- true;	
								}
								add (classement at indexListe) as list to : classementTmp;

								set indexListe <- indexListe + 1;
							}
							set classement <- classementTmp;
							
							if(!place) {
								let liste type: list of: int <- [];
								add index to : liste;
								add liste to : classement;
							}
						}
					}
					
					set index <- index + 1;
				}
				set etape <- etape + 1;
				
				do terminer_partie classement : classement;
			}
		}
	}


	/**
	 * --- ACTIONS ---
	 */
	
	/**
	 * Action ayant le but uniquement graphique de bien placer les joueurs autour de la table
	 */
	action set_positions {
		let taille_ligne_nord type: int <- 800;
		let position_ligne_nord type: int <- 100;
		let taille_ligne_sud type: int <- 800;
		let position_ligne_sud type: int <- 500;
		let taille_ligne_ouest type: int <- 400;
		let position_ligne_ouest type: int <- 100;
		let taille_ligne_est type: int <- 400;
		let position_ligne_est type: int <- 900;
		
		let nb_joueurs_nord type: int <- round((2/5)*nb_joueurs);
		let nb_joueurs_ouest type: int <- round((1/10)*nb_joueurs);
		let nb_joueurs_est type: int <- round((1/10)*nb_joueurs);
		let nb_joueurs_sud type: int <- nb_joueurs - (nb_joueurs_nord + nb_joueurs_ouest + nb_joueurs_est);

		// On place les joueurs qui sont au Nord		
		let index type: int <- 0;
		let index2 type: int <- 1;
		loop while : index < nb_joueurs_nord {
			let x type: float <- position_ligne_ouest + index2*taille_ligne_nord/(nb_joueurs_nord + 1);
			set (joueurs at index).location <- {x, position_ligne_nord};
			set index <- index + 1;
			set index2 <- index2 + 1;
		}
		
		// On place les joueurs qui sont � l'Est
		if(nb_joueurs_est > 0) {
			set index2 <- 1;
			loop while : index < nb_joueurs_nord + nb_joueurs_est {
				let y type: float <- position_ligne_nord + index2*taille_ligne_est/(nb_joueurs_est + 1);
				set (joueurs at index).location <- {position_ligne_est, y};
				set index <- index + 1;
				set index2 <- index2 + 1;
			}
		}
		
		// On place les joueurs qui sont au Sud		
		set index2 <- 1;
		loop while : index < nb_joueurs_nord + nb_joueurs_est + nb_joueurs_sud {
			let x type: float <- position_ligne_est - index2*taille_ligne_sud/(nb_joueurs_sud + 1);
			set (joueurs at index).location <- {x, position_ligne_sud};
			set index <- index + 1;
			set index2 <- index2 + 1;
		}
		
		// On place les joueurs qui sont � l'Ouest
		if(nb_joueurs_ouest > 0) {
			set index2 <- 1;
			loop while : index < nb_joueurs_nord + nb_joueurs_ouest + nb_joueurs_est + nb_joueurs_sud {
				let y type: float <- position_ligne_sud - index2*taille_ligne_ouest/(nb_joueurs_ouest + 1);
				set (joueurs at index).location <- {position_ligne_ouest, y};
				set index <- index + 1;
				set index2 <- index2 + 1;
			}
		}
	}
		
	/**
	 * Renvoie le meilleur joueur (meilleure main) parmi les deux
	 * pass�s en param�tres. Renvoie 1 si c'est le joueur 1, -1 si
	 * c'est le joueur 2, 0 s'il y a �galit�
	 */
	action choisir_meilleur type: int {
		arg joueur1 type: int;
		arg joueur2 type: int;
		
		// On cherche la meilleur main pour chaque joueur si besoin
		if((joueurs at joueur1).type_meilleure_combinaison = -1) {
			do trouver_meilleure_combinaison joueur : joueur1;
		}
		
		if((joueurs at joueur2).type_meilleure_combinaison = -1) {
			do trouver_meilleure_combinaison joueur : joueur2;
		}
		
		let combi1 type: int <- (joueurs at joueur1).type_meilleure_combinaison;
		let combi2 type: int <- (joueurs at joueur2).type_meilleure_combinaison;
		
		if(combi1 > combi2) {
			return 1;
		}
		else if (combi1 < combi2) {
			return -1;
		}
		else {
			// Ils ont la m�me combinaison, il faut les comparer
			let main_joueur1 type: list of: int <- (joueurs at joueur1).meilleure_combinaison;
			let main_joueur2 type: list of: int <- (joueurs at joueur2).meilleure_combinaison;
			
			return self compare_combinaison [combinaison :: combi1, main1 :: copy(main_joueur1), main2 :: copy(main_joueur2)];
		}
	}
	
	/**
	 * Permet de trouver la meilleure combinaison pour un joueur donn�.
	 * Retourn l'indice de la combinaison en question :
	 * 0 - Carte haute
	 * 1 - Pair
	 * 2 - Double Pair
	 * 3 - Brelan
	 * 4 - Quinte
	 * 5 - Flush
	 * 6 - Full
	 * 7 - Carr�
	 * 8 - Quinte Flush
	 */
	action trouver_meilleure_combinaison type: int {
		arg joueur type: int;
		
		// On part de la meilleure combinaison et on cherche vers les
		// moins bonnes au f�r et � mesure
		
		let cartes_dispo type: list of: int <- [];
		add ((joueurs at joueur).main at 0) to : cartes_dispo; 
		add ((joueurs at joueur).main at 1) to : cartes_dispo;
		loop carte over : cartes_communes {
			add carte to : cartes_dispo;
		}
		
		// On r�cup�re les ensemble de mains possibles
		let liste_mains type: list <- [];
		set liste_mains <- self get_combinaisons [cartes::cartes_dispo];
		
		let meilleure_main type: list of: int <- [];

		
		// --- Quinte flush ---
		loop main over : liste_mains {
			// On regarde s'il y a un flush
			let liste type: list of: int <- main as list;
			
			let old_color type: int <- floor((liste at 0)/100);
			let flush type: bool <- true;
			loop index from : 1 to : length(liste) - 1 {
				let color type: int <- floor((liste at index)/100);
				
				if(color != old_color) {
					set flush <- false;
				}
			}
			
			// C'est un flush, on regarde si c'est une suite
			if(flush) {
				// On enl�ve l'information de couleur
				let listeMod type: list of: int <- copy(liste);
				loop index from : 0 to : length(listeMod) - 1 {
					let couleur type: int <- floor((listeMod at index)/100)*100;
					put listeMod at index - couleur at : index in : listeMod;
				}
				set listeMod <- listeMod sort_by(each);
				
				// On regarde si les quatres derni�res cartes sont dans l'ordre
				// (� cause de l'As qui est particulier)
				let suite type: bool <- true;
				loop index from : 1 to : 3 {
					if(listeMod at index != (listeMod at (index + 1)) - 1) {
						set suite <- false;
					} 
				}
				
				// On regarde pour l'As
				if(suite) {
					if(!((listeMod at 0 = listeMod at 1 - 1) or (listeMod at 0 = 1 and listeMod at 4 = 13))) {
						set suite <- false;
					}
					else {
						// On a une suite et donc une quinte flush
						if(meilleure_main = []) {
							// Si on n'avait pas trouv� d'autre combinaison de ce type, c'est la meilleure
							set meilleure_main <- liste;
						}
						else {
							// On cherche laquelle est la meilleure
							if(self compare_combinaison [combinaison :: 8, main1 :: copy(liste), main2 :: copy(meilleure_main)] = 1) {
								set meilleure_main <- liste;
							}
						}
					}
				}
			}
		}
		
		if(meilleure_main != []) {
			set (joueurs at joueur).type_meilleure_combinaison <- 8;
			set (joueurs at joueur).meilleure_combinaison <- meilleure_main;
			return 8;
		}

		
		// --- Carr� ---
		loop main over : liste_mains {
			let liste type: list of: int <- main as list;

			// On enl�ve l'information de couleur
			let listeMod type: list of: int <- copy(liste);
			loop index from : 0 to : length(listeMod) - 1 {
				let couleur type: int <- floor((listeMod at index)/100)*100;
				put listeMod at index - couleur at : index in : listeMod;
			}
			set listeMod <- listeMod sort_by(each);
						
			let no_duplicates type: list of: int <- remove_duplicates(listeMod);
			let carre type: bool <- false;
			loop valeur over : no_duplicates {
				if(listeMod count (each = valeur) = 4) {
					set carre <- true;
				}
			}
						
			if(carre) {
				if(meilleure_main = []) {
					// Si on n'avait pas trouv� d'autre combinaison de ce type, c'est la meilleure
					set meilleure_main <- liste;
				}
				else {
					// On cherche laquelle est la meilleure
					if(self compare_combinaison [combinaison :: 7, main1 :: copy(liste), main2 :: copy(meilleure_main)] = 1) {
						set meilleure_main <- liste;
					}
				}
			}
		}
		
		if(meilleure_main != []) {
			set (joueurs at joueur).type_meilleure_combinaison <- 7;
			set (joueurs at joueur).meilleure_combinaison <- meilleure_main;
			return 7;
		}	
		
		
		// --- Full House ---
		loop main over : liste_mains {
			let liste type: list of: int <- main as list;

			// On enl�ve l'information de couleur
			let listeMod type: list of: int <- copy(liste);
			loop index from : 0 to : length(listeMod) - 1 {
				let couleur type: int <- floor((listeMod at index)/100)*100;
				put listeMod at index - couleur at : index in : listeMod;
			}
			set listeMod <- listeMod sort_by(each);
			
			if(listeMod at 0 = listeMod at 1 and listeMod at 3 = listeMod at 4) {
				// On n'a plus qu'� v�rifier si la carte "du milieu" vaut une des deux autres
				if(listeMod at 2 = listeMod at 0 or listeMod at 2 = listeMod at 4) {
					if(meilleure_main = []) {
						// Si on n'avait pas trouv� d'autre combinaison de ce type, c'est la meilleure
						set meilleure_main <- liste;
					}
					else {
						// On cherche laquelle est la meilleure
						if(self compare_combinaison [combinaison :: 6, main1 :: copy(liste), main2 :: copy(meilleure_main)] = 1) {
							set meilleure_main <- liste;
						}
					}
				}
			}
		}
		
		if(meilleure_main != []) {
			set (joueurs at joueur).type_meilleure_combinaison <- 6;
			set (joueurs at joueur).meilleure_combinaison <- meilleure_main;
			return 6;
		}	
		
		
		// --- Flush ---
		loop main over : liste_mains {
			let liste type: list of: int <- main as list;
			
			let old_color type: int <- floor((liste at 0)/100);
			let flush type: bool <- true;
			loop index from : 1 to : length(liste) - 1 {
				let color type: int <- floor((liste at index)/100);
				
				if(color != old_color) {
					set flush <- false;
				}
			}
			
			if(flush) {
				if(meilleure_main = []) {
					// Si on n'avait pas trouv� d'autre combinaison de ce type, c'est la meilleure
					set meilleure_main <- liste;
				}
				else {
					// On cherche laquelle est la meilleure
					if(self compare_combinaison [combinaison :: 5, main1 :: copy(liste), main2 :: copy(meilleure_main)] = 1) {
						set meilleure_main <- liste;
					}
				}
			}
		}
		
		if(meilleure_main != []) {
			set (joueurs at joueur).type_meilleure_combinaison <- 5;
			set (joueurs at joueur).meilleure_combinaison <- meilleure_main;
			return 5;
		}	
		
		
		// --- Suite ---
		loop main over : liste_mains {
			let liste type: list of: int <- main as list;

			// On enl�ve l'information de couleur
			let listeMod type: list of: int <- copy(liste);
			loop index from : 0 to : length(listeMod) - 1 {
				let couleur type: int <- floor((listeMod at index)/100)*100;
				put listeMod at index - couleur at : index in : listeMod;
			}
			set listeMod <- listeMod sort_by(each);
			
			// On regarde si les quatres derni�res cartes sont dans l'ordre
			// (� cause de l'As qui est particulier)
			let suite type: bool <- true;
			loop index from : 1 to : 3 {
				if(listeMod at index != (listeMod at (index + 1)) - 1) {
					set suite <- false;
				} 
			}
			
			// On regarde pour l'As
			if(suite) {
				if(!((listeMod at 0 = (listeMod at 1) - 1) or (listeMod at 0 = 1 and listeMod at 4 = 13))) {
					set suite <- false;
				}
				else {
					// On a une suite et donc une quinte flush
					if(meilleure_main = []) {
						// Si on n'avait pas trouv� d'autre combinaison de ce type, c'est la meilleure
						set meilleure_main <- liste;
					}
					else {
						// On cherche laquelle est la meilleure
						if(self compare_combinaison [combinaison :: 4, main1 :: copy(liste), main2 :: copy(meilleure_main)] = 1) {
							set meilleure_main <- liste;
						}
					}
				}
			}
		}
		
		if(meilleure_main != []) {
			set (joueurs at joueur).type_meilleure_combinaison <- 4;
			set (joueurs at joueur).meilleure_combinaison <- meilleure_main;
			return 4;
		}	
		
		
		// --- Brelan ---
		loop main over : liste_mains {
			let liste type: list of: int <- main as list;

			// On enl�ve l'information de couleur
			let listeMod type: list of: int <- copy(liste);
			loop index from : 0 to : length(listeMod) - 1 {
				let couleur type: int <- floor((listeMod at index)/100)*100;
				put listeMod at index - couleur at : index in : listeMod;
			}
			set listeMod <- listeMod sort_by(each);
			
			let no_duplicates type: list of: int <- remove_duplicates(listeMod);
			let brelan type: bool <- false;
			loop valeur over : no_duplicates {
				if(listeMod count (each = valeur) = 3) {
					set brelan <- true;
				}
			}
			
			if(brelan) {
				if(meilleure_main = []) {
					// Si on n'avait pas trouv� d'autre combinaison de ce type, c'est la meilleure
					set meilleure_main <- liste;
				}
				else {
					// On cherche laquelle est la meilleure
					if(self compare_combinaison [combinaison :: 3, main1 :: copy(liste), main2 :: copy(meilleure_main)] = 1) {
						set meilleure_main <- liste;
					}
				}
			}
		}
		
		if(meilleure_main != []) {
			set (joueurs at joueur).type_meilleure_combinaison <- 3;
			set (joueurs at joueur).meilleure_combinaison <- meilleure_main;
			return 3;
		}	
		
		
		// --- Double Pair ---
		loop main over : liste_mains {
			let liste type: list of: int <- main as list;

			// On enl�ve l'information de couleur
			let listeMod type: list of: int <- copy(liste);
			loop index from : 0 to : length(listeMod) - 1 {
				let couleur type: int <- floor((listeMod at index)/100)*100;
				put listeMod at index - couleur at : index in : listeMod;
			}
			set listeMod <- listeMod sort_by(each);
			
			let nb_identique type: int <- 0;
			loop index from : 1 to : length(listeMod) - 1 {
				if(listeMod at index = listeMod at (index - 1)) {
					set nb_identique <- nb_identique + 1;
				}
			}
			
			if(nb_identique = 2) {
				if(meilleure_main = []) {
					// Si on n'avait pas trouv� d'autre combinaison de ce type, c'est la meilleure
					set meilleure_main <- liste;
				}
				else {
					// On cherche laquelle est la meilleure
					if(self compare_combinaison [combinaison :: 2, main1 :: copy(liste), main2 :: copy(meilleure_main)] = 1) {
						set meilleure_main <- liste;
					}
				}
			}
		}
		
		if(meilleure_main != []) {
			set (joueurs at joueur).type_meilleure_combinaison <- 2;
			set (joueurs at joueur).meilleure_combinaison <- meilleure_main;
			return 2;
		}	
		
		
		// --- Pair ---
		loop main over : liste_mains {
			let liste type: list of: int <- main as list;

			// On enl�ve l'information de couleur
			let listeMod type: list of: int <- copy(liste);
			loop index from : 0 to : length(listeMod) - 1 {
				let couleur type: int <- floor((listeMod at index)/100)*100;
				put listeMod at index - couleur at : index in : listeMod;
			}
			set listeMod <- listeMod sort_by(each);
			
			let nb_identique type: int <- 0;
			loop index from : 1 to : length(listeMod) - 1 {
				if(listeMod at index = listeMod at (index - 1)) {
					set nb_identique <- nb_identique + 1;
				}
			}
			
			if(nb_identique = 1) {
				if(meilleure_main = []) {
					// Si on n'avait pas trouv� d'autre combinaison de ce type, c'est la meilleure
					set meilleure_main <- liste;
				}
				else {
					// On cherche laquelle est la meilleure
					if(self compare_combinaison [combinaison :: 1, main1 :: copy(liste), main2 :: copy(meilleure_main)] = 1) {
						set meilleure_main <- liste;
					}
				}
			}
		}
		
		if(meilleure_main != []) {
			set (joueurs at joueur).type_meilleure_combinaison <- 1;
			set (joueurs at joueur).meilleure_combinaison <- meilleure_main;
			return 1;
		}


		// --- Carte haute ---
		loop main over : liste_mains {
			let liste type: list of: int <- main as list;
			
			if(meilleure_main = []) {
				// Si on n'avait pas trouv� d'autre combinaison de ce type, c'est la meilleure
				set meilleure_main <- liste;
			}
			else {
				// On cherche laquelle est la meilleure
				if(self compare_combinaison [combinaison :: 0, main1 :: copy(liste), main2 :: copy(meilleure_main)] = 1) {
					set meilleure_main <- liste;
				}
			}
		}

		// On a forc�ment trouv� une main avec une carte haute		
		set (joueurs at joueur).type_meilleure_combinaison <- 0;
		set (joueurs at joueur).meilleure_combinaison <- meilleure_main;
		return 0;
	}
	
	/**
	 * Compare les deux m�mes combinaisons pass�es en param�tre.
	 * Retourne 1 si celle du joueur 1 est meilleure, -1 si c'est celle
	 * du joueur 2 et 0 s'il y a �galit�. 
	 */
	action compare_combinaison type: int {
		arg combinaison type: int;
		arg main1 type: list;
		arg main2 type: list;
		
		let liste1 type: list of: int <- main1;
		let liste2 type: list of: int <- main2;

		// On enl�ve les informations de couleur		
		loop index from : 0 to : length(liste1) - 1 {
			let couleur type: int <- floor((liste1 at index)/100)*100;
			put liste1 at index - couleur at : index in : liste1;
			
			set couleur <- floor((liste2 at index)/100)*100;
			put liste2 at index - couleur at : index in : liste2;
			
			// L'As oblige � modifier un peu la liste
			if(liste1 at index = 1) {
				put 14 at : index in : liste1;
			}
			if(liste2 at index = 1) {
				put 14 at : index in : liste2;
			}
		}
		set liste1 <- liste1 sort_by(each);
		set liste2 <- liste2 sort_by(each);
		
		switch(combinaison) {
			// --- Carte haute ---
			match 0 {
				// On compare les cartes tri�es
				let index type: int <- 4;
				loop while : index >= 0 {
					if(liste1 at index > liste2 at index) {
						return 1;
					}
					else if(liste1 at index < liste2 at index) {
						return -1;
					}
					set index <- index - 1;
				}
				
				// Egalit�
				return 0;
			}
			
			// --- Pair ---
			match 1 {
				// On cherche la valeur de la pair
				let pair1 type: int <- 0;
				let pair2 type: int <- 0;
				
				loop index from : 1 to : 4 {
					if(pair1 = 0 and liste1 at index = liste1 at (index - 1)) {
						set pair1 <- liste1 at index;
					}
					if(pair2 = 0 and liste2 at index = liste2 at (index - 1)) {
						set pair2 <- liste2 at index;
					}
				}
				
				// On compare d'abord les pairs
				if(pair1 > pair2) {
					return 1;
				}
				else if(pair1 < pair2) {
					return -1;
				}
				else {
					// Et sinon les cartes qui accompagnent
					set liste1 <- liste1 where (each != pair1);
					set liste2 <- liste2 where (each != pair2);
					set liste1 <- liste1 sort_by(each);
					set liste2 <- liste2 sort_by(each);
				
					let index type: int <- 2;
					loop while : index >= 0 {
						if(liste1 at index > liste2 at index) {
							return 1;
						}
						else if(liste1 at index < liste2 at index) {
							return -1;
						}
						
						set index <- index - 1;
					}
					
					// Egalit�
					return 0;
				}
			}
			
			// --- Double Pair ---
			match 2 {
				// On cherche les valeurs de pair haute et basse
				let pairH1 type: int <- 0;
				let pairH2 type: int <- 0;

				let pairB1 type: int <- 0;
				let pairB2 type: int <- 0;
				
				let index type: int <- 4;
				loop while : index >= 1 {
					if(liste1 at index = liste1 at (index - 1)) {
						if(pairH1 = 0) {
							set pairH1 <- liste1 at index;
						}
						else {
							set pairB1 <- liste1 at index;
						}
					}

					if(liste2 at index = liste2 at (index - 1)) {
						if(pairH2 = 0) {
							set pairH2 <- liste2 at index;
						}
						else {
							set pairB2 <- liste2 at index;
						}
					}
					
					set index <- index - 1;
				}
				
				if(pairH1 > pairH2) {
					return 1;
				}
				else if(pairH1 < pairH2) {
					return -1;
				}
				else {
					if(pairB1 > pairB2) {
						return 1;
					}
					else if(pairB1 < pairB2) {
						return -1;
					}
					else {
						set liste1 <- liste1 where (each != pairH1 and each != pairB1);
						set liste2 <- liste2 where (each != pairH2 and each != pairB2);
						
						// Normalement il ne reste qu'une carte
						if(liste1 at 0 > liste2 at 0) {
							return 1;
						}  
						else if(liste1 at 0 < liste2 at 0) {
							return -1;
						}
						else {
							return 0;
						}
					}
				}
			}
			
			// --- Brelan ---
			match 3 {
				// On cherche la valeur de brelan
				let brelan1 type: int <- 0;
				let brelan2 type: int <- 0;
				
				// On sait que d�s qu'on trouve deux cartes identiques elles font parties du brelan
				// (sinon �a serait un full)
				loop index from : 1 to : 4 {
					if(brelan1 = 0 and liste1 at index = liste1 at (index - 1)) {
						set brelan1 <- liste1 at index;
					}
					if(brelan2 = 0 and liste2 at index = liste2 at (index - 1)) {
						set brelan2 <- liste2 at index;
					}
				}
				
				// On compare d'abord les brelans
				if(brelan1 > brelan2) {
					return 1;
				}
				else if(brelan1 < brelan2) {
					return -1;
				}
				else {
					// Si c'est les m�mes (brelan dans le tableau), on compare les cartes restantes
					set liste1 <- liste1 where (each != brelan1);
					set liste1 <- liste1 sort_by(each);
					set liste2 <- liste2 where (each != brelan2);
					set liste2 <- liste2 sort_by(each);
					
					let index type: int <- 1;
					loop while : index >= 0 {
						if(liste1 at index > liste2 at index) {
							return 1;
						}
						else if(liste1 at index < liste2 at index) {
							return -1;
						}
						
						set index <- index - 1;
					}
					
					return 0;
				}
			}
			
			// --- Suite ---
			match 4 {
				// On a juste � regarder la carte haute
				// Mais il faut tout de meme v�rifier si l'As �tait
				// la carte haute ou basse de la suite
				let carte_haute1 type: int <- 0;
				let carte_haute2 type: int <- 0;
				
				if(liste1 at 4 = 14 and liste1 at 3 != 13) {
					// As carte basse
					set carte_haute1 <- liste1 at 3;
				}
				else {
					set carte_haute1 <- liste1 at 4;
				}
				
				if(liste2 at 4 = 14 and liste2 at 3 != 13) {
					// As carte basse
					set carte_haute2 <- liste2 at 3;
				}
				else {
					set carte_haute2 <- liste2 at 4;
				}
				
				if(carte_haute1 > carte_haute2) {
					return 1;
				}
				else if(carte_haute1 < carte_haute2) {
					return -1;
				}
				else {
					return 0;
				}
			}
			
			// --- Flush ---
			match 5 {
				// On compare les cartes tri�es
				let index type: int <- 4;
				loop while : index >= 0 {
					if(liste1 at index > liste2 at index) {
						return 1;
					}
					else if(liste1 at index < liste2 at index) {
						return -1;
					}
					
					set index <- index - 1;
				}
				
				// Egalit�
				return 0;				
			}
			
			// --- Full ---
			match 6 {
				let listeTmp1 type: list of: int <- remove_duplicates(liste1);
				let listeTmp2 type: list of: int <- remove_duplicates(liste2);
				
				let brelan1 type: int <- 0;
				let brelan2 type: int <- 0;
				
				if(liste1 count (each = listeTmp1 at 0) = 3) {
					// C'est le brelan
					set brelan1 <- listeTmp1 at 0;
				}
				else {
					set brelan1 <- listeTmp1 at 1;
				}
				
				if(liste2 count (each = listeTmp2 at 0) = 3) {
					// C'est le brelan
					set brelan2 <- listeTmp2 at 0;
				}
				else {
					set brelan2 <- listeTmp2 at 1;
				}
				
				// On compare les brelans
				if(brelan1 > brelan2) {
					return 1;
				}
				else if(brelan1 < brelan2) {
					return -1;
				}
			}
			
			// --- Carr� ---
			match 7 {
				// On cherche la valeur de carr�
				let carre1 type: int <- 0;
				let carre2 type: int <- 0;
				
				// On sait que d�s qu'on trouve deux cartes identiques elles font parties du carr�
				loop index from : 1 to : 4 {
					if(carre1 = 0 and liste1 at index = liste1 at (index - 1)) {
						set carre1 <- liste1 at index;
					}
					if(carre2 = 0 and liste2 at index = liste2 at (index - 1)) {
						set carre2 <- liste2 at index;
					}
				}
				
				if(carre1 > carre2) {
					return 1;
				}
				else if(carre1 < carre2) {
					return -1;
				}
				else {
					// Si les deux sont identiques (carr� au tableau), on compare
					// la carte restante
					set liste1 <- liste1 where (each != carre1);
					set liste2 <- liste2 where (each != carre2);
					
					if(liste1 at 0 > liste2 at 0) {
						return 1;
					}
					else if(liste1 at 0 < liste2 at 0) {
						return -1;
					}
					else {
						return 0;
					}
				}
			}
			
			// --- Quinte Flush ---
			match 8 {
				// On a juste � regarder la carte haute
				// Mais il faut tout de meme v�rifier si l'As �tait
				// la carte haute ou basse de la suite
				let carte_haute1 type: int <- 0;
				let carte_haute2 type: int <- 0;
				
				if(liste1 at 4 = 14 and liste1 at 3 != 13) {
					// As carte basse
					set carte_haute1 <- liste1 at 3;
				}
				else {
					set carte_haute1 <- liste1 at 4;
				}
				
				if(liste2 at 4 = 14 and liste2 at 3 != 13) {
					// As carte basse
					set carte_haute2 <- liste2 at 3;
				}
				else {
					set carte_haute2 <- liste2 at 4;
				}
				
				if(carte_haute1 > carte_haute2) {
					return 1;
				}
				else if(carte_haute1 < carte_haute2) {
					return -1;
				}
				else {
					return 0;
				}
			}
		}
	}
	
	/**
	 * Permet d'obtenir toutes les combinaisons de 5 cartes parmis un ensemble plus grand
	 * de cartes. Dans notre cas, cette fonction est limit�e aux combinaisons de 5 parmi 6 et
	 * 5 parmi 7 qui sont les seules qui nous serviront.
	 */
	action get_combinaisons type: list {
		arg cartes type: list;
		
		let nb_cartes type: int <- length(cartes);
		let listes_combi type: list <- [];
		
		if(nb_cartes = 5) {
			// Il n'y a en fait qu'une seule combinaison
			add cartes to : listes_combi;
		}
		else {
			loop index1 from : 0 to : nb_cartes - 2 {
				if(nb_cartes - 5 = 2) {
					loop index2 from : index1 + 1 to : nb_cartes - 1 {
						let liste_tmp type: list of: int <- [];
						let exclu type: list of: int <- [cartes at index1, cartes at index2];
						set liste_tmp <- cartes - exclu;
						add liste_tmp to : listes_combi;
					}
				}
				else if(nb_cartes - 5 = 1) {
					let liste_tmp type: list of: int <- [];
					let exclu type: list of: int <- [cartes at index1];
					set liste_tmp <- cartes - exclu;
					add liste_tmp to : listes_combi;
				}
			}
		}
		
		return listes_combi;
	}
	
	/**
	 * Initialisation d'une partie
	 */
	action init_partie {
		set vainqueur <- nil;
		set pot <- 0;
		set miseGlobale <- 0;
		
		// On m�lange les cartes
		set cartes <- shuffle(deck);
		
		set cartes_communes <- [];
		
		// Il y a une chance que des joueurs soient out et que donc la liste
		// joueurs soit modifi�e pendant qu'on la parcourt. Et c'est chiant !
		let liste_tmp type: list of: Joueur <- copy(joueurs);
		loop joueur over : liste_tmp {
			ask joueur {
				do init;
			}
		}
		
		// Si on a d�termin� un vainqueur on arr�te
		if(vainqueur = nil) {
			// Et on les distribue
			do distribuer_mains;
			
			// On choisit le dealer, le small blind et big blind
			do set_dealer;
			
			do mises_initiales;
			
			set etape <- -1;
			set jeton <- false;
			set encheres_finies <- false;
			set pas_de_tour <- false;
		}
	}
		
	/**
	 * Action appel�e quand une partie s'ach�ve
	 */
	 action terminer_partie {
	 	//arg liste_vainqueurs type: list;
	 	arg classement type: list;

		// On parcourt la liste des joueurs en compet pour cr�er les diff�rents pots
		let liste_pots type: list of: pair <- [];
		let pot_tmp type: int <- 0;
		let liste_joueurs_tapis type: list of: int <- ((joueurs where (each.tapis)) as list) sort_by(each.mise);
		let liste_tapis type: list of: int <- liste_joueurs_tapis collect (joueurs index_of each);
		let mise_prec type: int <- 0;
		loop joueur over : liste_tapis {
			let pair_pot type: pair <- 0::0;
			let pot_pair type: int <- 0;
			
			// Le joueur ne peut gagner qu'� hauteur de sa mise pour chaque joueur
			let mise_joueur type: int <- (joueurs at joueur).mise;
			
			// On regarde si un pot �quivalent n'a pas d�j� �t� cr��
			if(liste_pots count (each.key = mise_joueur) = 0) {
				loop joueur2 over : joueurs {
					if(mise_joueur > joueur2.mise) {
						if(mise_prec = 0) {
							set pot_pair <- pot_pair + joueur2.mise;
						}
					}
					else {
						set pot_pair <- pot_pair + (mise_joueur - mise_prec);					
					}
				}
				
				set mise_prec <- mise_joueur;
				set pair_pot <- mise_joueur::pot_pair;
				add pair_pot to : liste_pots;
				set pot_tmp <- pot_tmp + pot_pair;
			}
		}
		add -1::(pot - pot_tmp) to : liste_pots;
		
		let indexClassement type: int <- 0;
		loop while : pot > 0 and indexClassement < length(classement) {
			let joueurs_egalite type: list <- (classement at indexClassement) as list;
			
			// Pour m�moriser les pots qui ont �t� vid� et devront �tre supprim�s de la liste
			let list_pots_remove type: list of: pair <- [];
			loop joueur over : joueurs_egalite {
				// On regarde pour chaque pot, le nombre de joueurs � �galit� devant se le partager
				loop pots over : liste_pots {
					let nb_partage type: int <- 0;
					
					if(!(joueurs at joueur).tapis or ((joueurs at joueur).mise >= int(pots.key) and int(pots.key) > -1)) {
						if(int(pots.key) > -1) {
							set nb_partage <- joueurs_egalite count ((joueurs at each).mise >= int(pots.key));
						}
						else {
							set nb_partage <- joueurs_egalite count (!(joueurs at each).tapis);
						}
						
						// Il prend la part du pot qui lui revient
						set (joueurs at joueur).argent <- (joueurs at joueur).argent + int(int(pots.value)/nb_partage);
						set pot <- pot - int(int(pots.value)/nb_partage);
						
						add pots to : list_pots_remove;
					}
				}
			}
			
			// On supprime les pots vid�s
			loop pots over : list_pots_remove {
				remove pots from : liste_pots;
			}
			
			set indexClassement <- indexClassement + 1;
		}
		
		do init_partie;
	 }
	
	/**
	 * Action appel�e par les joueurs quand ils ont choisir leur action pour
	 * valider leur mise
	 */
	action valider_mise {
		// Mise que le joueur a pay�. -1 s'il se couche
		arg mise type: int;
		
		// Valeur de combien le joueur a relanc�
		arg valeur_supp type: int;

		if(mise != -1) {
			// On v�rifie en premier lieu que ce joueur atteint 
			// au moins la mise globale
			if((joueurs at joueur_courant).mise < miseGlobale and !(joueurs at joueur_courant).tapis) {
				// Sinon on consid�re qu'il se couche et on prend
				// sa mise parce que c'est pas bien de tricher
				set (joueurs at joueur_courant).couche <- true;
			} 
			
			set pot <- pot + mise;
			
			if(valeur_supp > 0 and !(joueurs at joueur_courant).couche) {
				set miseGlobale <- miseGlobale + valeur_supp;
				set no_raise <- false;
				
				// Ce joueur devient le nouveau "premier joueur"
				set premier_joueur <- joueur_courant;
			}	
		}
		
		if(mise = -1) {
			// Si jamais c'est le premier joueur qui s'est couch�
			if(premier_joueur = joueur_courant) {
				// On cherche le joueur suivant
				let index type: int <- joueur_courant;
				let joueur_suiv type: int <- -1;
				loop while : joueur_suiv = -1 {
					set index <- (index + 1) mod length(joueurs);
					
					if(!(joueurs at index).couche) {
						set joueur_suiv <- index;
					}
				}
				
				// Qui devient le premier joueur
				set premier_joueur <- joueur_suiv;
				set pas_de_tour <- true;
			}
		}
		
		// Si jamais tout le monde s'est couch� sauf un joueur
		let liste_en_jeu type: list of: Joueur <- joueurs where (!each.couche);
		if(length(liste_en_jeu) = 1) {
			// La partie est finie
			let classement type: list <- [];
			let liste_vainqueurs type: list of: int <- [];
			loop joueur over : liste_en_jeu {
				add joueurs index_of joueur to : liste_vainqueurs;
			}
			add liste_vainqueurs to : classement;
			do terminer_partie classement : classement;
		}
	}
	
	/**
	 * Distribue deux cartes � chaque joueur
	 */
	action distribuer_mains {
		loop joueur over : joueurs {
			add self pop_card [] to : joueur.main;
			add self pop_card [] to : joueur.main;
		}
	}
	
	/**
	 * Choisit le dealer et les blinds
	 */
	action set_dealer {
		let found type: bool <- false;
		let index type: int <- dealer;
		loop while : !found {
			if(index < length(joueurs) - 1) {
				set index <- index + 1;
			}
			else {
				set index <- 0;
			}
			
			let joueur type: Joueur <- joueurs at index;
			if(!joueur.out) {
				set dealer <- index;
				set joueur.dealer <- true;
				
				if(joueurs count (each) = 2) {
					set joueur.small_blind <- true;
					set (joueurs at ((index + 1) mod length(joueurs))).big_blind <- true;
					
					// A deux, le donneur est petite blind et donc premier joueur
					set premier_joueur <- index;
				}
				else {
					set (joueurs at ((index + 1) mod length(joueurs))).small_blind <- true;
					set (joueurs at ((index + 2) mod length(joueurs))).big_blind <- true;
					
					set premier_joueur <- (index + 3) mod length(joueurs);
				}
				
				set found <- true;
			}
		}
	}
	
	/**
	 * Les mises initialises de petites et grosses blind
	 */
	action mises_initiales {
		let val_mise type: int <- 0; 
		loop joueur over : joueurs {
			if(joueur.small_blind) {
				ask joueur {
					set val_mise <- self mise_obligatoire [valeur :: blind/2];
				}
				set pot <- pot + val_mise;
				
				if(blind/2 > miseGlobale) {
					set miseGlobale <- blind/2;
				}
//				if(val_mise > miseGlobale) {
//					set miseGlobale <- val_mise;
//				}
			}
			else if(joueur.big_blind) {
				ask joueur {
					set val_mise <- self mise_obligatoire [valeur :: blind];
				}
				set pot <- pot + val_mise;

				if(blind > miseGlobale) {
					set miseGlobale <- blind;
				}
//				if(val_mise > miseGlobale) {
//					set miseGlobale <- val_mise;
//				}
			}
		}
	}
	
	/**
	 * Permet au monde de prendre en compte un joueur �limin�
	 */
	action valider_out {
		arg joueur type: Joueur;
		
		// On supprime le joueur de la liste
		remove joueur from : joueurs;
		
		// On regarde s'il ne reste pas qu'un joueur en jeu
		if(length(joueurs) <= 1) {
			if(length(joueurs) > 0) {
				set vainqueur <- joueurs at 0;
			}
		}
	}
	
	/**
	 * Prend la premi�re carte du paquet
	 */
	action pop_card type: int {
		let card type: int <- first(cartes);
		remove card from : cartes;
		return card;
	}
	
	/**
	 * Br�le la premi�re carte du paquet
	 */
	 action bruler_carte {
	 	let card type: int <- first(cartes);
	 	remove card from : cartes;
	 }
	
	/**
	 * Permet de passer la main � un joueur
	 */
	action donner_jeton {
		arg joueur type: int;
		
		set jeton <- false;
		let suivant type: Joueur <- joueurs at joueur;
		set suivant.jeton <- true;
	}
	
	
	aspect default {
		draw text: "Pot : " + string(pot) color: rgb("white") size : 10 at : my location - {0, 10};
		draw text: "Mise courante : " + string(miseGlobale) color: rgb("white") size : 10 at : my location - {0, 0};
		
		// On affiche les cartes communes
		let images type: list of: string <- [];
		loop carte over : cartes_communes {
			let image type: string <- string(cartes_images at carte) + ".png";
			add image to : images; 			
		}

		let ecart type: int <- 6;
		let pos_y type: int <- 25;
		if(length(cartes_communes) = 3) {
			let debut type: int <- -36;
			
			loop index from : 0 to : length(images) - 1 {
				draw image: file("../images/" + images at index) at : my location + {debut + index*36, pos_y} size : 30;
			}
		}
		else if(length(cartes_communes) = 4) {
			let debut type: int <- -54;
			
			loop index from : 0 to : length(images) - 1 {
				draw image: file("../images/" + images at index) at : my location + {debut + index*36, pos_y} size : 30;
			}
		}
		else if(length(cartes_communes) = 5) {
			let debut type: int <- -72;
			
			loop index from : 0 to : length(images) - 1 {
				draw image: file("../images/" + images at index) at : my location + {debut + index*36, pos_y} size : 30;
			}
		}
		else {
			// Pas encore de cartes, on met les dos
			let debut type: int <- -36;
			
			loop index from : 0 to : 2 {
				draw image: "../images/155.png" at : my location + {debut + index*36, pos_y} size : 30;
			}
		}
	}
}

environment bounds : {1000,600};

entities {
	/**
	 * Agent abstrait permettant de repr�senter tous les concepts de base qui permettent �
	 * un joueur de jouer une partie et permettant ainsi de mettre la partie "intelligence" des
	 * agents dans un agent propre qui h�rite de Joueur. Un agent h�ritant de Joueur doit impl�menter
	 * le reflex choisir_action qui doit aboutir sur l'ex�cution d'une des deux actions : miser ou se_coucher
	 */
	species Joueur {
		/**
		 * Main de cartes du joueur, repr�sent�e sous la forme d'un tableau de deux entiers.
		 */
		list main <- [] of: int;
		
		/**
		 * La mise du joueur pour ce tour
		 */
		int mise <- 0;
		
		/**
		 * Somme d'argent que poss�de l'agent.
		 */
		int argent <- argent_init;
		
		/**
		 * Permet de garder en m�moire la meilleure combinaison de cartes
		 * qu'on ait trouv� pour ce joueur. Ceci �vite de la recalculer inutilement
		 * 0 - Carte haute
		 * 1 - Pair
		 * 2 - Double Pair
		 * 3 - Brelan
		 * 4 - Quinte
		 * 5 - Flush
		 * 6 - Full
		 * 7 - Carr�
		 * 8 - Quinte Flush
		 */
		int type_meilleure_combinaison <- -1;

		/**
		 * Permet de savoir combien de raises a fait ce joueur. Elle sont limit�es �
		 * max_raises � chaque round
		 */
		 int nb_raises <- 0;
		
		/**
		 * On garde en m�moire la meilleure main possible pour le joueur. On n'est oblig�
		 * de garder les 5 cartes si jamais la combinaison est un flush (pour les �galit�s).
		 * On ne garde les cartes que comme des chiffres entre 1 et 13, n'ayant plus besoin de
		 * l'information sur la couleur
		 */
		list meilleure_combinaison <- [] of: int;
		
		/**
		 * Permet de savoir si un joueur s'est couch� ou non.
		 */
		bool couche <- false;
		
		/**
		 * Permet de savoir si un joueur a �t� �limin�.
		 */
		bool out <- false;
		
		/**
		 * Permet de savoir si un joueur a fait tapis.
		 */
		bool tapis <- false;
		
		/**
		 * Joueur dealer.
		 */
		bool dealer <- false;

		/**
		 * Joueur small blind.
		 */
		bool small_blind <- false;

		/**
		 * Joueur big blind.
		 */
		bool big_blind <- false;
		
		/**
		 * Pour que le joueur sache quand il doit jouer
		 */
		bool jeton <- false;
		
		/**
		 * R�flex de base du joueur permettant de choisir son action (sa mise) �
		 * chacun de ses tours
		 */
//		reflex choisir_action when: jeton;

		
		/**
		 * Rend le jeton � l'agent global
		 */
		action rendre_jeton {
			set jeton <- false;
			set world.jeton <- true;
		}
		
		/**
		 * Action appel�e par le world agent lorsque le joueur doit miser sa blind
		 */
		action mise_obligatoire type: int {
			arg valeur type: int;
			
			if(argent - valeur < 0) {
				// Il met tout ce qu'il a
				set tapis <- true;
				let val_max type: int <- argent;
				set argent <- 0;
				set mise <- mise + val_max;
				return val_max;
			}
			else {
				set argent <- argent - valeur;
				set mise <- mise + valeur;
				
				if(argent = 0) {
					set tapis <- true;
				}
				
				return valeur;
			}
		}
		
		/**
		 * Action � appeler pour valider une mise. Cette action permet au monde de la
		 * prendre en compte et de faire continuer le jeu
		 */
		action miser {
			arg valeur type: int;
			
			if(argent - valeur < 0) {
				// On n'a pas le droit de miser plus qu'on a
				do se_coucher;
			}
			else {
				let valeurSupp type: int <- 0;
				if((mise + valeur) > miseGlobale) {
					// On regarde si le joueur n'a pas d�pass� son quota de raises pour ce round
					if(nb_raises < max_raises) {
						set nb_raises <- nb_raises + 1;
					}
					else {
						// Le joueur n'a plus le droit de faire de raise, on consid�re qu'il suit juste
						set valeur <- miseGlobale - mise; 
					}
				}
				
				set argent <- argent - valeur;
				set mise <- mise + valeur;
				
				// Raise
				if(mise > miseGlobale) {
					set valeurSupp <- mise - miseGlobale; 
				}
				
				if(argent = 0) {
					set tapis <- true;
				}
				
				ask world {
					do valider_mise with : [mise :: valeur, valeur_supp :: valeurSupp];
				}
				
				do rendre_jeton;
			}
		}
		
		/**
		 * Action � appeler pour valider le faire de se coucher. Cette action permet au monde de
		 * prendre �a en compte et de faire continuer le jeu
		 */
		action se_coucher {
			set couche <- true;
			
			ask world {
				do valider_mise with : [mise :: -1, valeur_supp :: 0];
			}
			
			do rendre_jeton;
		}
		
		/**
		 * Action permettant de r�initialiser certaines variables du joueur
		 * au d�but de chaque partie
		 */
		action init {
			if(argent = 0) {
				set out <- true;
				ask world {
					do valider_out joueur : myself;
				}
			}
			
			set mise <- 0;
			set couche <- false;
			set jeton <- false;
			set dealer <- false;
			set small_blind <- false;
			set big_blind <- false;
			set type_meilleure_combinaison <- -1;
			set meilleure_combinaison <- [];
			set main <- [];
			set tapis <- false;
			set nb_raises <- 0;
		}
		
		/**
		 * Permet � un joueur de demander volontairement sa meilleure combinaison au monde
		 * (sinon c'est fait � l'abattage)
		 */
		action meilleure_combinaison {
			// Si on n'a pas encore pass� le pr�-flop, la combinaison n'est constitu�e que de notre
			// main
			if(world.etape > 0) {
				ask world {
					do trouver_meilleure_combinaison joueur : joueurs index_of (myself);
				}
			}
			else {
				// Il faut trouver la meilleure combinaison dans notre main de deux cartes

				// On enl�ve les informations de couleur
				let main_tmp type: list of: int <- copy(main);	
				loop index from : 0 to : length(main_tmp) - 1 {
					let couleur type: int <- floor((main_tmp at index)/100)*100;
					put (main_tmp at index) - couleur at : index in : main_tmp;
					
					// L'As oblige � modifier un peu la liste
					if(main_tmp at index = 1) {
						put 14 at : index in : main_tmp;
					}
				}
				set main_tmp <- main_tmp sort_by(each);
				
				// On regarde si c'est une pair
				if((main_tmp at 0) = (main_tmp at 1)) {
					set type_meilleure_combinaison <- 1;
				}
				else {
					set type_meilleure_combinaison <- 0;
				}
				set meilleure_combinaison <- main;
			}
		}

		aspect default {
			// On affiche un rond color� pour l'�tat du joueur
			if(out) {
				draw circle(5) color: rgb("black");
			}
			else {
				if(couche) {
					draw circle(10)  color: rgb("gray");
				}
				else {
					if(tapis) {
						draw circle(10)   color: rgb ("blue");
					}
					else {
						if((joueurs index_of self) = joueur_courant) {
							draw circle(10)  color: rgb ("red");
						}
						else {
							draw circle(10)  color: rgb ("green");
						}
					}
				}
			}
			
			// On affiche le text correspondant � son statut, son argent et sa mise
			if(dealer) {
				if(small_blind) {
					// A deux joueurs
					draw text: "Small blind" color: rgb("white") size : 10 at : my location - {0, 45};
				}
				draw text: "Dealer" color: rgb("white") size : 10 at : my location - {0, 35};
			}
			else if(small_blind) {
				draw text: "Small blind" color: rgb("white") size : 10 at : my location - {0, 35};
			}
			else if(big_blind) {
				draw text: "Big blind" color: rgb("white") size : 10 at : my location - {0, 35};
			}
			draw text: "Argent : " + string(argent) color: rgb("white") size : 10 at : my location - {0, 25};
			draw text: "Mise : " + string(mise) color: rgb("white") size : 10 at : my location - {0, 15};
			draw text: "J " + string(all_joueurs index_of self) + " : " + string(species_of(self)) color: rgb("white") size : 10 at : my location - {0, 5};
			
			// On affiche la main du joueur
			if(length(main) >= 2) {
				let carte1 type: string <- string(cartes_images at (main at 0)) + ".png";
				let carte2 type: string <- string(cartes_images at (main at 1)) + ".png";
	
				draw image: file("../images/" + carte1) at : my location + {-18, 30} size : 30;
				draw image: file("../images/" + carte2) at : my location + {18, 30} size : 30;
			}
		}
	}
	
	
	/**
	 * Agents ZISuiveurs. Le comportement de ces agents est, comme leur nom l'indique, de ne faire que
	 * suivre pour essayer de rester dans le jeu le plus longtemps possible. Pour cela, ils misent toujours,
	 * s'ils peuvent, la diff�rence entre leur mise et la mise globale (ce qui est attendu). Si jamais ils ne
	 * peuvent pas, ils font tapis.
	 */
	species JoueurZISuiveur parent: Joueur {
		reflex choisir_action when: jeton {
			if((miseGlobale - self.mise) <= argent) {
				do miser valeur : (miseGlobale - self.mise);
			}
			else {
				do miser valeur : argent;
			}
		}
	}
	
	
	/**
	 * Agents ZIAleatoires. Ces agents jouent totalement al�atoirement selon des probabilit�s d�finies par
	 * les param�tres de la simulation. Ils ont donc une probabilit� de se coucher, de suivre ou de relancer.
	 * Il est � noter qu'il leur est possible de faire tapis dans le cas o� le fait de suivre ou de relancer am�ne
	 * leur argent � 0. Ceci n'est donc pas r�ellement un choix d'action.
	 * Si jamais ces agents ne sont plus dans la capacit� de relancer, la probabilit� de relance est partag�e entre
	 * les deux autres probabilit�s afin de ne pas faire une action impossible.
	 */
	species JoueurZIAleatoire parent: Joueur {
		/**
		 * Probabilit� de se coucher � chaque tour
		 */
		float proba_coucher <- proba_coucher_param;

		/**
		 * Probabilit� de faire une relance � chaque tour
		 */
		float proba_relance <- proba_relance_param;

		/**
		 * Probabilit� de suivre � chaque tour
		 */
		float proba_suivre <- proba_suivre_param;

		reflex choisir_action when: jeton {
			let rand type: int <- rnd(100);
			
			// On regarde si l'agent est dans la capacit� de relancer
			if(miseGlobale - self.mise >= argent) {
				// Il ne pourra pas relancer, on "partage" cette probabilit� entre
				// les deux actions restantes
				set proba_coucher <- proba_coucher + (proba_relance / 2);
				set proba_suivre <- 100.0 - proba_coucher;
				set proba_relance <- 0.0;
			}
			
			if(rand <= proba_coucher) {
				do se_coucher;
			}
			else if(rand > proba_coucher and rand <= (proba_coucher + proba_relance)) {
				// On choisit al�atoirement de combien on relance
				let mise_minim type: int <- miseGlobale - self.mise;
				let rand_relance type: int <- rnd(argent - mise_minim);
				
				do miser valeur : mise_minim + rand_relance;
			}
			else if(rand > (proba_coucher + proba_relance) and rand <= (proba_coucher + proba_relance + proba_suivre)) {
				if((miseGlobale - self.mise) <= argent) {
					do miser valeur : (miseGlobale - self.mise);
				}
				else {
					do miser valeur : argent;
				}
			}
			else {
				// Ne devrait pas arriver mais c'est histoire d'�liminer le joueur si jamais
				// les probas ne somment pas � 100. Parce que c'est pas bien !
				set argent <- 0;
				do se_coucher;
			}
		}
	}
	
	
	species JoueurPrudent parent: Joueur {
		/**
		 * Force de la main utilis�e lors du calcule de la valeur de relance
		 */
		float force_relance <- 0.0;
		
		/**
		 * Seuil de confiance pour que le joueur prudent suive
		 */
		float seuil_suivre <- seuil_suivre_param_prudent;
		
		/**
		 * Seuil de confiance pour que le joueur prudent relance
		 */
		float seuil_relance <- seuil_relance_param_prudent;
		
		/**
		 * Ratio max de l'argent du joueur qu'il peut utiliser pour une relance
		 */
		float ratio_max_relance <- max_relance_param_prudent;
		
		reflex choisir_action when: jeton {
			// Si ce n'est pas fait (� ce round), on �value la meilleure
			// combinaison du joueur
			if(type_meilleure_combinaison = -1) {
				do meilleure_combinaison;
			}
			
			let confiance type: float <- self calculer_confiance [];
			
			// Ce joueur ne relance que si sa confiance est suffisante ET qu'il peut
			if(confiance >= seuil_relance and ((miseGlobale - mise < argent))) {
				// On calcule de combien il relance par rapport � la force de sa main
				let mise_min type: int <- miseGlobale - mise;
				let relance type: int <- floor((force_relance/100)*ratio_max_relance*(argent - mise_min));
	
				do miser valeur : mise_min + relance;
			}
			else if(confiance >= seuil_suivre) {
				// On prend le risque de suivre
				if((miseGlobale - self.mise) <= argent) {
					do miser valeur : (miseGlobale - self.mise);
				}
				else {
					do miser valeur : argent;
				}
			}
			else {
				// Pas de risque : on se couche
				do se_coucher;
			}
		}
		
		
		/**
		 * Action permettant de calculer la confiance du joueur qui correspond en fait �
		 * une diff�rence entre la force de sa main et le risque encourru par la mise.
		 */
		action calculer_confiance type: float {
			let confiance type: float <- 0.0;
			
			// On calcule la force de la main
			let force type: float <- 0.0;

			// On enl�ve les informations de couleur
			let main_tmp type: list of: int <- copy(meilleure_combinaison);	
			loop index from : 0 to : length(main_tmp) - 1 {
				let couleur type: int <- floor((main_tmp at index)/100)*100;
				put (main_tmp at index) - couleur at : index in : main_tmp;
				
				// L'As oblige � modifier un peu la liste
				if(main_tmp at index = 1) {
					put 14 at : index in : main_tmp;
				}
			}
			set main_tmp <- main_tmp sort_by(each);
			
			// Moyenne des valeurs des cartes de la main
			
			// Il faut faire attention � la valeur de l'As pour une suite (et donc quinte flush)
			if(type_meilleure_combinaison = 4 or type_meilleure_combinaison = 8) {
				if(length(main_tmp) >= 5 and main_tmp at 3 = 5) {
					// C'est le seul cas o� l'As peut valoir 1
					put 1 at : length(main_tmp) - 1 in : main_tmp;
					set main_tmp <- main_tmp sort_by (each);
				}
			}
			let moyenne type: float <- mean(main_tmp);
			
			set force <- (type_meilleure_combinaison)*100 + moyenne;
			
			// On ram�ne la force entre 0 et 100
			let force_max type: float <- 0.0;
			
			if(etape > 0) {
				// Meilleure combinaison possible : Quinte royale
				set force_max <- 812;
			}
			else {
				// Meilleure combinaison possible : Pair d'As
				set force_max <- 114;
			}
			
			// La force de relance est calcul�e par rapport � la meilleure combinaison
			// � la fin du jeu
			set force_relance <-(force/812)*100.0;
			
			set force <- (force/force_max)*100.0;
			
			// On calcule le risque sur la mise
			let risque type: float <- 0.0;
			
			if((miseGlobale - mise) >= argent) {
				set risque <- 100.0;
			}
			else {
				set risque <- ((miseGlobale - mise)/argent)*100.0;
			}
			
			// Confiance entre 0 et 100
			set confiance <- (force - risque + 100)/2;
										
			return confiance;
		}
	}
	
	
	species JoueurBluffer parent: Joueur {
		/**
		 * Force de la main utilis�e lors du calcule de la valeur de relance
		 */
		float force_relance <- 0.0;
		
		/**
		 * Seuil de confiance pour que le joueur suive
		 */
		float seuil_suivre <- seuil_suivre_param_bluffer;
		
		/**
		 * Seuil de confiance pour que le joueur relance
		 */
		float seuil_relance <- seuil_relance_param_bluffer;
		
		/**
		 * Ratio max de l'argent du joueur qu'il peut utiliser pour une relance
		 */
		float ratio_max_relance <- max_relance_param_bluffer;
		
		/**
		 * Ratio min de l'argent du joueur qu'il va utiliser pour relancer lors d'un coup de bluff
		 */
		float ratio_min_relance <- min_relance_param;
		
		/**
		 * Bool�en permettant de savoir que le joueur bluff
		 */
		bool etat_bluff <- false;
		
		reflex choisir_action when: jeton {
			// Si ce n'est pas fait (� ce round), on �value la meilleure
			// combinaison du joueur
			if(type_meilleure_combinaison = -1) {
				do meilleure_combinaison;
			}
			
			let confiance type: float <- self calculer_confiance [];
			
			// Ce joueur ne relance que si sa confiance est suffisante ET qu'il peut
			if(!etat_bluff) {
				if(confiance >= seuil_relance and ((miseGlobale - mise < argent))) {
					// On calcule de combien il relance par rapport � la force de sa main
					let mise_min type: int <- miseGlobale - mise;
					let relance type: int <- floor((force_relance/100)*ratio_max_relance*(argent - mise_min));
		
					do miser valeur : mise_min + relance;
				}
				else if(confiance >= seuil_suivre) {
					// On prend le risque de suivre
					if((miseGlobale - self.mise) <= argent) {
						do miser valeur : (miseGlobale - self.mise);
					}
					else {
						do miser valeur : argent;
					}
				}
				else {
					// L'agent bluffeur a une probabilit� de choisir de bluffer plut�t que 
					// de se coucher
					// On calcule d�j� le nombre de raises qu'il y a eu � ce tour
					let nb_raises_tot type: int <- 0;
					loop joueur over : joueurs {
						if(joueur != self) {
							set nb_raises_tot <- nb_raises_tot + joueur.nb_raises;
						}
					}
					
					let nb_raises_max type: int <- (length(joueurs) - 1)*max_raises;
					let seuil_bluff type: float <- (nb_raises_tot/nb_raises_max)*100;
					
					let rand_bluff type: int <- rnd(100);
					
					if(rand_bluff >= seuil_bluff) {
						// On choisit le bluff � partir de maintenant
						set etat_bluff <- true;
					}
					else {				
						// Pas de risque : on se couche
						do se_coucher;
					}
				}			
			}
			
			if(etat_bluff) {
				// Le joueur va donc encha�ner les relances
				// S'il ne peut pas relancer, il se couche (aucun int�r�t � suivre pour le bluffer)
				let mise_min type: int <- miseGlobale - mise;
				if(mise_min < argent) {
					let min_relance type: float <- ratio_min_relance*(argent - mise_min);
					
					// On tire un nombre entre min_relance et (argent - mise_min)
					let relance type: int <- rnd((argent - mise_min) - min_relance) + min_relance;

					if(relance = 0 and argent > 0) {
						set relance <- 1;
					}
	
					do miser valeur : mise_min + relance;
				}
				else {
					do se_coucher;
				}
			}
		}
		
		
		
		/**
		 * Action permettant de r�initialiser certaines variables du joueur
		 * au d�but de chaque partie
		 */
		action init {
			if(argent = 0) {
				set out <- true;
				ask world {
					do valider_out joueur : myself;
				}
			}
			
			set mise <- 0;
			set couche <- false;
			set jeton <- false;
			set dealer <- false;
			set small_blind <- false;
			set big_blind <- false;
			set type_meilleure_combinaison <- -1;
			set meilleure_combinaison <- [];
			set main <- [];
			set tapis <- false;
			set nb_raises <- 0;
			set etat_bluff <- false;
		}
		
		/**
		 * Action permettant de calculer la confiance du joueur qui correspond en fait �
		 * une diff�rence entre la force de sa main et le risque encourru par la mise.
		 */
		action calculer_confiance type: float {
			let confiance type: float <- 0.0;
			
			// On calcule la force de la main
			let force type: float <- 0.0;

			// On enl�ve les informations de couleur
			let main_tmp type: list of: int <- copy(meilleure_combinaison);	
			loop index from : 0 to : length(main_tmp) - 1 {
				let couleur type: int <- floor((main_tmp at index)/100)*100;
				put (main_tmp at index) - couleur at : index in : main_tmp;
				
				// L'As oblige � modifier un peu la liste
				if(main_tmp at index = 1) {
					put 14 at : index in : main_tmp;
				}
			}
			set main_tmp <- main_tmp sort_by(each);
			
			// Moyenne des valeurs des cartes de la main
			
			// Il faut faire attention � la valeur de l'As pour une suite (et donc quinte flush)
			if(type_meilleure_combinaison = 4 or type_meilleure_combinaison = 8) {
				if(length(main_tmp) >= 5 and main_tmp at 3 = 5) {
					// C'est le seul cas o� l'As peut valoir 1
					put 1 at : length(main_tmp) - 1 in : main_tmp;
					set main_tmp <- main_tmp sort_by (each);
				}
			}
			let moyenne type: float <- mean(main_tmp);
			
			set force <- (type_meilleure_combinaison)*100 + moyenne;
			
			// On ram�ne la force entre 0 et 100
			let force_max type: float <- 0.0;
			
			if(etape > 0) {
				// Meilleure combinaison possible : Quinte royale
				set force_max <- 812;
			}
			else {
				// Meilleure combinaison possible : Pair d'As
				set force_max <- 114;
			}
			
			// La force de relance est calcul�e par rapport � la meilleure combinaison
			// � la fin du jeu
			set force_relance <-(force/812)*100.0;
			
			set force <- (force/force_max)*100.0;
			
			// On calcule le risque sur la mise
			let risque type: float <- 0.0;
			
			if((miseGlobale - mise) >= argent) {
				set risque <- 100.0;
			}
			else {
				set risque <- ((miseGlobale - mise)/argent)*100.0;
			}
			
			// Confiance entre 0 et 100
			set confiance <- (force - risque + 100)/2;
										
			return confiance;
		}
	}
}


experiment PokerInterface type: gui {
	/**
	 * Nombre de joueurs dans la partie
	 */
	parameter "Nombre de joueurs" var: nb_joueurs category : "Global";
	
	/**
	 * Blind qui doivent �tre pay�es (mise initiale)
	 */
	parameter "Blind" var: blind category : "Global";
	
	/**
	 * Argent initial avec lequel commence chaque joueur
	 */
	parameter "Argent initial" var: argent_init category : "Global";
	
	/**
	 * Nombre de raises maximal que peut faire un joueur durant une partie
	 */
	parameter "Nombre de raises max" var: max_raises category : "Global";
	
	/**
	 * Limite de temps d'une simulation
	 */
	parameter "Limite de temps" var: limite_temps category : "Global";
	
	/**
	 * Ajouter ici les parts des diff�rents agents � cr�er
	 */
	parameter "Parts des agents" var: partsAgents category : "Agents";
	
	parameter "Probabilit� de se coucher" var: proba_coucher_param category : "ZIAleatoires";
	parameter "Probabilit� de relancer" var: proba_relance_param category : "ZIAleatoires";
	parameter "Probabilit� de suivre" var: proba_suivre_param category : "ZIAleatoires";
	
	parameter "Seuil de suivi" var: seuil_suivre_param_prudent category : "Prudents";
	parameter "Seuil de relance" var: seuil_relance_param_prudent category : "Prudents";
	parameter "Part max de relance" var: max_relance_param_prudent category : "Prudents";
	
	parameter "Seuil de suivi" var: seuil_suivre_param_bluffer  category : "Bluffers";
	parameter "Seuil de relance" var: seuil_relance_param_bluffer  category : "Bluffers";
	parameter "Part max de relance" var: max_relance_param_bluffer category : "Bluffers";
	parameter "Part min de relance" var: min_relance_param category : "Bluffers";
	
	output {
		display main background : rgb([63, 109, 61]) {
			agents all_agents value : all_joueurs aspect : default;
			agents main_agent value : world as list aspect : default;
		}
		
		display charts {
			chart "Argent" type: histogram background : rgb("white") {
				loop index from : 0 to : length(all_joueurs) - 1 {
					data "argent" legend : "J " + string(index) color: couleurs at (index mod length(couleurs)) value : (all_joueurs at index).argent;
				}
			}
		}
	}
}


// --- Batchs pour les exp�riences ---
// Ces diff�rentes exp�riences sont comment�es pour �viter de surcharger l'interface

/**
 * Premier type d'exp�rience o� l'on �tudie quel type d'agent sort vainqueur lorsqu'on les fait s'affronter les uns les autres
 */
//experiment PokerExp1 type: batch repeat : 100 until : (time >= limite_temps or vainqueur != nil) {
//	parameter "Limite Temps" var: limite_temps <- 20000;
//	parameter "Nombre de joueurs" var: nb_joueurs <- 12;
//	parameter "Argent" var: argent_init <- 1000;
//	parameter "Blind" var: blind <- 10;
//	parameter "Max relance" var: max_raises <- 3;
//
//	parameter "Part des agents" var: partsAgents among : [
//		[1::1.0, 2::0.0, 3::0.0, 4::0.0], [1::0.0, 2::1.0, 3::0.0, 4::0.0], [1::0.0, 2::0.0, 3::1.0, 4::0.0], [1::0.0, 2::0.0, 3::0.0, 4::1.0],
//		[1::0.5, 2::0.5, 3::0.0, 4::0.0], [1::0.5, 2::0.0, 3::0.5, 4::0.0], [1::0.5, 2::0.0, 3::0.0, 4::0.5], [1::0.0, 2::0.5, 3::0.0, 4::0.5], [1::0.0, 2::0.5, 3::0.5, 4::0.0], [1::0.0, 2::0.0, 3::0.5, 4::0.5],
//		[1::0.33, 2::0.33, 3::0.33, 4::0.0], [1::0.33, 2::0.33, 3::0.0, 4::0.33], [1::0.33, 2::0.0, 3::0.33, 4::0.33], [1::0.0, 2::0.33, 3::0.33, 4::0.33],
//		[1::0.25, 2::0.25, 3::0.25, 4::0.25]  
//	];
//}

/**
 * Exp�rience o� l'on cherche les meilleurs seuils pour le joueur bluffer
 */
//experiment PokerExpBestBluffer type: batch repeat : 100 until : (time >= limite_temps or vainqueur != nil) {
//	parameter "Limite Temps" var: limite_temps <- 20000;
//	parameter "Nombre de joueurs" var: nb_joueurs <- 12;
//	parameter "Argent" var: argent_init <- 1000;
//	parameter "Blind" var: blind <- 10;
//	parameter "Max relance" var: max_raises <- 3;
//	parameter "Part des agents" var: partsAgents <- [1::0.25, 2::0.25, 3::0.25, 4::0.25];
//	
//	parameter "Seuil suivre" var: seuil_suivre_param_bluffer among : [20.0, 30.0, 40.0, 60.0];
//	parameter "Seuil relance" var: seuil_relance_param_bluffer among : [60.0, 70.0, 80.0, 90.0];
//}

/**
 * Exp�rience o� l'on cherche les meilleurs seuils pour le joueur prudent
 */
//experiment PokerExpBestPrudent type: batch repeat : 100 until : (time >= limite_temps or vainqueur != nil) {
//	parameter "Limite Temps" var: limite_temps <- 20000;
//	parameter "Nombre de joueurs" var: nb_joueurs <- 12;
//	parameter "Argent" var: argent_init <- 1000;
//	parameter "Blind" var: blind <- 10;
//	parameter "Max relance" var: max_raises <- 3;
//	parameter "Part des agents" var: partsAgents <- [1::0.25, 2::0.25, 3::0.25, 4::0.25];
//	
//	parameter "Seuil suivre" var: seuil_suivre_param_prudent among : [20.0, 30.0, 40.0, 60.0];
//	parameter "Seuil relance" var: seuil_relance_param_prudent among : [60.0, 70.0, 80.0, 90.0];
//}

/**
 * Exp�rience o� on cherche quel est le meilleur entre le joueur prudent et le joueur bluffer avec leur configuration optimale
 */
//experiment PokerExpBestBlufferVSBestPrudent type: batch repeat : 1000 until : (time >= limite_temps or vainqueur != nil) {
//	parameter "Limite Temps" var: limite_temps <- 30000;
//	parameter "Nombre de joueurs" var: nb_joueurs <- 12;
//	parameter "Argent" var: argent_init <- 1000;
//	parameter "Blind" var: blind <- 10;
//	parameter "Max relance" var: max_raises <- 3;
//	parameter "Part des agents" var: partsAgents <- [1::0.25, 2::0.25, 3::0.25, 4::0.25];
//	
//	parameter "Seuil suivre prudent" var: seuil_suivre_param_prudent among : [40.0];
//	parameter "Seuil relance prudent" var: seuil_relance_param_prudent among : [60.0];
//	parameter "Seuil suivre bluffer" var: seuil_suivre_param_bluffer among : [30.0];
//	parameter "Seuil relance bluffer" var: seuil_relance_param_bluffer among : [60.0];
//}

/**
 * Exp�rience o� l'on �tudie l'influence de l'argent initial et des blinds sur l'efficacit� des agents
 */
//experiment PokerExpArgent type: batch repeat : 100 until : (time >= limite_temps or vainqueur != nil) {
//	parameter "Limite Temps" var: limite_temps <- 2000;
//	parameter "Nombre de joueurs" var: nb_joueurs <- 12;
//	parameter "Max relance" var: max_raises <- 3;
//	parameter "Part des agents" var: partsAgents <- [1::0.25, 2::0.25, 3::0.25, 4::0.25];	
//	parameter "Seuil suivre prudent" var: seuil_suivre_param_prudent <- 40.0;
//	parameter "Seuil relance prudent" var: seuil_relance_param_prudent <- 60.0;
//	parameter "Seuil suivre bluffer" var: seuil_suivre_param_bluffer <- 30.0;
//	parameter "Seuil relance bluffer" var: seuil_relance_param_bluffer <- 60.0;
//
//	parameter "Argent" var: argent_init among : [1000, 2000, 5000, 10000];
//	parameter "Blind" var: blind among : [10, 20, 50, 100];
//}
