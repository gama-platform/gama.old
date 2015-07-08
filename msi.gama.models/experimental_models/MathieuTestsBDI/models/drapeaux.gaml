/**
 *  drapeaux
 *  Author: mathieu
 *  Description: un agent (puis une mulitude) va rejoindre un endroit cible en évitant les trous placés aléatoirement et ce, de façon BDI.
 */

model drapeaux

global{
	int nbBut <-1;
	int nbPeople <-10;
	int nbPeopleBDI <-10;
	int nbTrou <-100;
	
	int nbPeopleAlive -> {length(people)};
	int nbPeopleBDIAlive -> {length(peopleBDI)};
	
	init {
		create but number:nbBut;
		create people number:nbPeople;
		create peopleBDI number:nbPeopleBDI;
		create trous number: nbTrou;
	}
}

species but{
	rgb color <- #red;
	grille maCellule <- one_of(grille);
	list<people> gagnant update: people inside(maCellule);
	list<peopleBDI> gagnantBDI update: peopleBDI inside(maCellule);
	
	init {
		location <-maCellule.location;
	}
	
	reflex gagner when: ((length(gagnant) = length(people)) and (length(gagnantBDI) = length(peopleBDI))){
		ask world {
			do halt;
		}
	}
	
	aspect base{
		draw circle(1) color: color;
	}
}

species people skills:[moving]{
	rgb color <- #green;
	but monBut <- first(but);
	float vitesse <- 2.0;
	
	init {
		location <- one_of(grille).location;
	}
	
	reflex move{
//		do goto on: grille target: monBut;
		list<grille> voisins <-  (grille(location) neighbors_at (1)) + grille(location);
			path cheminSuivi <- self goto(on: grille,target: monBut,speed: vitesse,return_path:true);
			if (cheminSuivi != nil) and not empty(cheminSuivi.segments) {
				geometry path_geom <- geometry(cheminSuivi.segments);
				ask (voisins where (each.shape intersects path_geom)) { color <- rgb('green');}
			}
	}
	
	aspect base{
		draw circle(1) color: color;
	}
}

species peopleBDI skills:[moving] control: simple_bdi{
	rgb color <- #blue;
	bool trouPasse <- false;
	float vitesse <-2.0;
	
	predicate but_desire <- new_predicate("but");
	predicate trou <- new_predicate("trou");
		
	bool probabilistic_choice <- false;	
		
	init {
		location <- one_of(grille).location;
		do add_desire(but_desire);
	}
	
//	reflex perception{
//		
//		/*il faut percevoir les trous*/
//		list<trous> listeTrou <- trous at_distance 2;
//		if(length(listeTrou)!=nil){
//			loop trouTemp over: listeTrou{
//				if(not has_belief(new_predicate("trou",["emplacementTrou"::trouTemp.location]))){
//					trouPasse<-false;
//					do add_belief(new_predicate("trou",["emplacementTrou"::trouTemp.location]));
//					do add_desire(new_predicate("trou",["emplacementTrou"::trouTemp.location]));
//					do remove_intention(but_desire,true);
//					//write("Perception");
//				}
//			}
//		}
//	}
	
	perceive target:trous in:2{
		ask myself{
			if(not has_belief(new_predicate("trou",["emplacementTrou"::myself.location]))){
					trouPasse<-false;
					do add_belief(new_predicate("trou",["emplacementTrou"::myself.location]));
					do add_desire(new_predicate("trou",["emplacementTrou"::myself.location]));
					do remove_intention(but_desire,true);
					//write("Perception");
			}
		}
	}
	
	plan goToBut intention: but_desire finished_when: has_belief(trou) and trouPasse=false{
		//write("avance");
		list<grille> voisins <-  (grille(location) neighbors_at (1)) + grille(location);
		but monBut<- first(but);
			path cheminSuivi <- self goto(on: grille,target: monBut,speed: vitesse,return_path:true);
			if (cheminSuivi != nil) and not empty(cheminSuivi.segments) {
				geometry path_geom <- geometry(cheminSuivi.segments);
				ask (voisins where (each.shape intersects path_geom)) { color <- rgb('blue');}
			}
	}
	
	plan eviteTrou1 intention: trou finished_when: trouPasse = true{
		write("eviteTrou");
		point trouLocation <- point(get_current_intention().values["emplacementTrou"]);
		point butLocation <- first(but).location;
		/*Cas spécial, si l'agent est aligné avec le but et que le trou est sur le coté, on reprend sa marche en avant*/
		if (self.location.x=butLocation.x and trouLocation.x!=butLocation.x){
//			do remove_belief(new_predicate("trou",trouLocation));
			do remove_intention(trou,true);
			do add_desire(but_desire);
			trouPasse <- true;
		}
		if (self.location.y=butLocation.y and trouLocation.y!=butLocation.y){
//			do remove_belief(new_predicate("trou",trouLocation));
			do remove_intention(trou,true);
			do add_desire(but_desire);
			trouPasse <- true;
		}
		/*Faire des chemins particuliers en fonction que le trou se trouve juste au dessus, juste en dessou, juste à gauche ou juste à droite 
		 * On réalise un contournement et on vérifiera que si le trou est "derière" nous, on s'en fou (sachant que les trous ne sont pas détéctés en diagonal)*/
		/*On s'occupe d'abord du cas ou le trou n'est pas entre nous et le but*/
		/*Le trou est à notre droite et le but à notre gauche*/
		if (self.location.x<trouLocation.x and self.location.x>butLocation.x){
//			do remove_belief(new_predicate("trou",trouLocation));
			do remove_intention(trou,true);
			do add_desire(but_desire);
			trouPasse <- true;
		} 
		/*Le trou est à notre gauche et le but à notre droite */
		if (self.location.x>trouLocation.x and self.location.x<butLocation.x){
//			do remove_belief(new_predicate("trou",trouLocation));
			do remove_intention(trou,true);
			do add_desire(but_desire);
			trouPasse <- true;
		} 
		/*Le trou est au dessus et le but en dessous */
		if (self.location.y<trouLocation.y and self.location.y>butLocation.y){
//			do remove_belief(new_predicate("trou",trouLocation));
			do remove_intention(trou,true);
			do add_desire(but_desire);
			trouPasse <- true;
		} 
		/*Le trou est en dessous et le but au dessus */
		if (self.location.y>trouLocation.y and self.location.y<butLocation.y){
//			do remove_belief(new_predicate("trou",trouLocation));
			do remove_intention(trou,true);
			do add_desire(but_desire);
			trouPasse <- true;
		} 
		
		/*Le trous se trouve à ma gauche et le but se trouve en haut à gauche (par rapport à moi) 
		 * On considère que le repère est dans le coin en haut à gauche du cadrillage*/
		if(self.location.x>trouLocation.x and butLocation.y<=self.location.y){
			path cheminEvitement <- path([{self.location.x,self.location.y},{self.location.x,self.location.y-2},{trouLocation.x,trouLocation.y-2}]);
			list<grille> voisins <-  (grille(location) neighbors_at (1)) + grille(location);
			path cheminSuivi <- self follow(path: cheminEvitement,speed: vitesse,return_path:true);
			if (cheminSuivi != nil) and not empty(cheminSuivi.segments) {
				geometry path_geom <- geometry(cheminSuivi.segments);
				ask (voisins where (each.shape intersects path_geom)) { color <- rgb('yellow');}
			}
			do remove_intention(trou,true);
			do add_desire(but_desire);
			trouPasse <- true;
		}
		/*Le trou se trouve à ma gauche et le but se trouve en dessous à gauche */
		if(self.location.x>trouLocation.x and butLocation.y>self.location.y){
			path cheminEvitement <- path([{self.location.x,self.location.y},{self.location.x,self.location.y+2},{trouLocation.x,trouLocation.y+2}]);
			list<grille> voisins <-  (grille(location) neighbors_at (1)) + grille(location);
			path cheminSuivi <- self follow(path: cheminEvitement,speed: vitesse,return_path:true);
			if (cheminSuivi != nil) and not empty(cheminSuivi.segments) {
				geometry path_geom <- geometry(cheminSuivi.segments);
				ask (voisins where (each.shape intersects path_geom)) { color <- rgb('yellow');}
			}
			do remove_intention(trou,true);
			do add_desire(but_desire);
			trouPasse <- true;
		}
		/*le trou se trouve à ma droite et le but en haut à droite */
		if(self.location.x<trouLocation.x and butLocation.y<=self.location.y){
			path cheminEvitement <- path([{self.location.x,self.location.y},{self.location.x,self.location.y-2},{trouLocation.x,trouLocation.y-2}]);
			list<grille> voisins <-  (grille(location) neighbors_at (1)) + grille(location);
			path cheminSuivi <- self follow(path: cheminEvitement,speed: vitesse,return_path:true);
			if (cheminSuivi != nil) and not empty(cheminSuivi.segments) {
				geometry path_geom <- geometry(cheminSuivi.segments);
				ask (voisins where (each.shape intersects path_geom)) { color <- rgb('yellow');}
			}
			do remove_intention(trou,true);
			do add_desire(but_desire);
			trouPasse <- true;
		}
		/*le trou se trouve à ma droite et le but se trouve en bas à droite */
		if(self.location.x<trouLocation.x and butLocation.y>self.location.y){
			path cheminEvitement <- path([{self.location.x,self.location.y},{self.location.x,self.location.y+2},{trouLocation.x,trouLocation.y+2}]);
			list<grille> voisins <-  (grille(location) neighbors_at (1)) + grille(location);
			path cheminSuivi <- self follow(path: cheminEvitement,speed: vitesse,return_path:true);
			if (cheminSuivi != nil) and not empty(cheminSuivi.segments) {
				geometry path_geom <- geometry(cheminSuivi.segments);
				ask (voisins where (each.shape intersects path_geom)) { color <- rgb('yellow');}
			}
			do remove_intention(trou,true);
			do add_desire(but_desire);
			trouPasse <- true;
		}
		/*le trou se trouve au dessus et le but se trouve en haut à gauche */
		if(self.location.y>trouLocation.y and butLocation.x<=self.location.x){
			path cheminEvitement <- path([{self.location.x,self.location.y},{self.location.x-2,self.location.y},{trouLocation.x-2,trouLocation.y}]);
			list<grille> voisins <-  (grille(location) neighbors_at (1)) + grille(location);
			path cheminSuivi <- self follow(path: cheminEvitement,speed: vitesse,return_path:true);
			if (cheminSuivi != nil) and not empty(cheminSuivi.segments) {
				geometry path_geom <- geometry(cheminSuivi.segments);
				ask (voisins where (each.shape intersects path_geom)) { color <- rgb('yellow');}
			}
			do remove_intention(trou,true);
			do add_desire(but_desire);
			trouPasse <- true;
		}
		/*le trou se trouve au dessus et le but se trouve en haut à droite*/
		if(self.location.y>trouLocation.y and butLocation.x>self.location.x){
			path cheminEvitement <- path([{self.location.x,self.location.y},{self.location.x+2,self.location.y},{trouLocation.x+2,trouLocation.y}]);
			list<grille> voisins <-  (grille(location) neighbors_at (1)) + grille(location);
			path cheminSuivi <- self follow(path: cheminEvitement,speed: vitesse,return_path:true);
			if (cheminSuivi != nil) and not empty(cheminSuivi.segments) {
				geometry path_geom <- geometry(cheminSuivi.segments);
				ask (voisins where (each.shape intersects path_geom)) { color <- rgb('yellow');}
			}
			do remove_intention(trou,true);
			do add_desire(but_desire);
			trouPasse <- true;
		}
		/*le trou se trouve en dessous et le but se trouve en dessous à gauche */
		if(self.location.y<trouLocation.y and butLocation.x<=self.location.x){
			path cheminEvitement <- path([{self.location.x,self.location.y},{self.location.x-2,self.location.y},{trouLocation.x-2,trouLocation.y}]);
			list<grille> voisins <-  (grille(location) neighbors_at (1)) + grille(location);
			path cheminSuivi <- self follow(path: cheminEvitement,speed: vitesse,return_path:true);
			if (cheminSuivi != nil) and not empty(cheminSuivi.segments) {
				geometry path_geom <- geometry(cheminSuivi.segments);
				ask (voisins where (each.shape intersects path_geom)) { color <- rgb('yellow');}
			}
			do remove_intention(trou,true);
			do add_desire(but_desire);
			trouPasse <- true;
		}
		/*le trou se trouve en dessous et le but se trouve en dessous à droite */
		if(self.location.y<trouLocation.y and butLocation.x>self.location.x){
			path cheminEvitement <- path([{self.location.x,self.location.y},{self.location.x+2,self.location.y},{trouLocation.x+2,trouLocation.y}]);
			list<grille> voisins <-  (grille(location) neighbors_at (1)) + grille(location);
			path cheminSuivi <- self follow(path: cheminEvitement,speed: vitesse,return_path:true);
			if (cheminSuivi != nil) and not empty(cheminSuivi.segments) {
				geometry path_geom <- geometry(cheminSuivi.segments);
				ask (voisins where (each.shape intersects path_geom)) { color <- rgb('yellow');}
			}
			do remove_intention(trou,true);
			do add_desire(but_desire);
			trouPasse <- true;
		}
	}
		
	aspect base{
		draw circle(1) color: color;
	}
}

species trous{
	rgb color <- #black;
	grille maCellule <- one_of(grille);
	list<people> tombeur update: people inside(maCellule);
	list<peopleBDI> tombeurBDI update: peopleBDI inside(maCellule);
	init {
		location <- maCellule.location;
	}
	
	reflex tomber when: tombeur != nil{
		ask one_of(tombeur) {
			do die;
		}
	}
	
	reflex tomberBDI when: tombeurBDI != nil{
		if(length(tombeurBDI)!=0){
			write(one_of(tombeurBDI).name);
		}
		ask one_of(tombeurBDI) {
			do die;
		}
	}
	
	aspect base{
		draw square(2) color: color;
	}
}

grid grille width:50 height:50 neighbours:4{
	
}

experiment drapeau type: gui{
	parameter "nbPeople" var:nbPeople category:"Main";
	parameter "nbPeopleBDI" var:nbPeopleBDI category:"Main";
	parameter "nbTrou" var:nbTrou category:"Main";
	
	output{
		display main{
			grid grille lines:#black;
			species but aspect:base;
			species people aspect:base;
			species peopleBDI aspect:base;
			species trous aspect:base;
		}
		display charts{
			chart "nbPeopleAlive" type: series size:{1,1} position:{0,0}{
				data "nbPeopleAlive" value: nbPeopleAlive style: line color:#green;
				data "nbPeopleBDIAlive" value: nbPeopleBDIAlive style: line color: #blue;
			}
		}
	}
}
