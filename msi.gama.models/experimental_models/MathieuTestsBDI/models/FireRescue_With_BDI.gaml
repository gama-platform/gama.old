/**
 *  new
 *  Author: Arno
 *  Description: 
 */

model FireRescueWithBDI

global {
	int displatTextSize <-4;
	init {
		create fireArea number:3;
		create waterArea number:1;
		create helicopter number: 7;
	}
	
	reflex stop when: length(fireArea) = 0 {
		do halt;
	}
}


species helicopter skills: [moving] control: simple_bdi{	
	rgb color;
	float waterValue;
	grille maCellule <- one_of(grille);
	predicate patrol_desire <- new_predicate("patrol") with_priority 1;
	predicate firePredicate <- new_predicate("fire") with_priority 5;
	predicate fireExtinguish <- new_predicate("fireExtinguish") with_priority 5;
	predicate water_predicate <- new_predicate("has water", ["water"::true]) with_priority 3;
	predicate no_water_predicate <- new_predicate("has water", ["water"::false]) ;
	bool target_fire_extinguish <- false;

	float plan_persistence <- 1.0;
	float intention_persistence <- 0.95;
	bool probabilistic_choice <- true;

	init {
		waterValue <-1.0;
		location<-maCellule.location;
		do add_belief(water_predicate);
		do add_desire(patrol_desire );
		
	}
	reflex perception {
//		loop fire over: fireArea at_distance 10 {
//			if (not has_belief(new_predicate("fire",["firePlace"::fire.location], 2))) {
//				do add_belief(new_predicate("fire", ["firePlace"::fire.location], 2));		
//				do add_desire(new_predicate("fireExtinguish", ["firePlace"::fire.location], 2));
//				do remove_intention(patrol_desire,false);
//				
//			}
//		}
		
		if(has_belief(fireExtinguish)){
			do remove_belief(fireExtinguish);
		}
		
	}
	
	perceive target:fireArea in: 10{
		ask myself{
			if (not has_belief(new_predicate("fire",["firePlace"::myself.location], 2))) {
				do add_belief(new_predicate("fire", ["firePlace"::myself.location], 2));		
				do add_desire(new_predicate("fireExtinguish", ["firePlace"::myself.location], 2));
				do remove_intention(patrol_desire,false);
			}
		}
	}
	
	plan patrolling intention:patrol_desire /*when: is_current_intention(patrol_desire)*/ finished_when: has_belief(firePredicate){
		write "patrolling";
//		do patrouiller;
		do wander;
	}
	
	action patrouiller{
		bool trouve <- false;
			grille temp <- one_of(maCellule neighbors_at(1));
			if(temp.type="vide"){
				
				ask temp {
					type <- "visite";
					color <- #green;
				}
				trouve<-true;
			}
		maCellule<-temp;
        location <- maCellule.location ;
	}
	
	plan stopFire intention: fireExtinguish /*when: is_current_intention(fireExtinguish)*/ finished_when: target_fire_extinguish or has_belief(no_water_predicate) priority:5{
		write "stopFire";
		point target_fire <- point(get_current_intention().values["firePlace"] );
		if (self distance_to target_fire <= 1) {
			write "stopFire2";
			fireArea current_fire <- fireArea first_with (each.location = target_fire);
			if (current_fire != nil) {
				 waterValue <- waterValue - 0.5;
				 if (waterValue <= 0) {
				 	do remove_belief(water_predicate);
				 	do add_belief(no_water_predicate);
				 	do add_desire(water_predicate);
				 }
				 current_fire.size <-  current_fire.size - 1;
				 if ( current_fire.size = 0) {
				 	write "stopFire3";
				 	grille grilleFeu <- first(grille where(location=current_fire.location));
				 	ask grilleFeu {type <- "vide";}
					ask  current_fire {do die;}
					do replace_belief(new_predicate("fire",["firePlace"::target_fire]), new_predicate("fireExtinguish",["firePlace"::target_fire]));
					do remove_intention(fireExtinguish,true);
					target_fire_extinguish <- true;
					
				}	else {
					target_fire_extinguish <- false;
				}	
			}
			
		} else {
			list<grille> voisins <-  (grille(location) neighbors_at (1)) + grille(location);
			path cheminSuivi <- self goto(on: grille,target: target_fire,return_path:true);
//			if (cheminSuivi != nil) and not empty(cheminSuivi.segments) {
//				geometry path_geom <- geometry(cheminSuivi.segments);
//				ask (voisins where (each.shape intersects path_geom)) { color <- rgb('green');}
//			}
		}
	} 

    plan gotoTakeWater intention: water_predicate /*when: is_current_intention(water_predicate)*/ finished_when: has_belief(water_predicate) priority:2 {
    	write "gotoTakeWater";
    	waterArea wa <- first(waterArea);
    	list<grille> voisins <-  (grille(location) neighbors_at (1)) + grille(location);
			path cheminSuivi <- self goto(on: grille,target: wa,return_path:true);
//			if (cheminSuivi != nil) and not empty(cheminSuivi.segments) {
//				geometry path_geom <- geometry(cheminSuivi.segments);
//				ask (voisins where (each.shape intersects path_geom)) { color <- rgb('green');}
//			}
    	if (self distance_to wa <= 1) {
    		waterValue <- waterValue + 1.0;
//    		wa.size <- wa.size - 1.0;
//    		if ( wa.size = 0) {
//				ask  wa {do die;}
//			}
			do add_belief(water_predicate);	
			do remove_belief(no_water_predicate);	
			do remove_intention(water_predicate,true);
		}
    }

	aspect base {
		draw circle(1) color: rgb(0,0,waterValue);	
	}
	
	aspect bdi {
		draw circle(1) color: rgb(0,0,waterValue);
		draw ("B:" + length(belief_base) + ":" + belief_base) color:#black size:displatTextSize; 
		draw ("D:" + length(desire_base) + ":" + desire_base) color:#black size:displatTextSize at:{location.x,location.y+displatTextSize}; 
		draw ("I:" + length(intention_base) + ":" + intention_base) color:#black size:displatTextSize at:{location.x,location.y+2*displatTextSize}; 
		draw ("curIntention:" + get_current_intention()) color:#black size:displatTextSize at:{location.x,location.y+3*displatTextSize}; 	
	}
}

species fireArea{
	float size <-1.0;
	
	init{
		grille place <- one_of(grille);
		location <- place.location;
		ask place{
			type <- "feu";
		}
	}
	
	reflex propagation{
		//modifier pour les cases sur les bords qui ont moins de 4 voisins.
		if(self.location.x>1 and self.location.x<99 and self.location.y>1 and self.location.y<99){
			list<grille> voisins <-  (grille(location) neighbors_at (1));
			int indiceRandom <- rnd(3);
			grille choisie <- voisins[indiceRandom];
			if(choisie.type="vide"){
				if(flip(0.005)){
					create fireArea number: 1{
						location <- choisie.location;
					}
					ask choisie {type<-"feu";}
				}
			}
		
		}
	}
	
	aspect base {
	  draw circle(size) color: #red;
	}
}

species waterArea{
	float size <-10.0;
	
	init {
		grille place <- one_of(grille);
		location <- place.location;
		ask place{
			type <- "eau";
		}
	}
	aspect base {
	  draw circle(size) color: #blue;		
	}
}

grid grille width: 50 height: 50 neighbours:4{
	string type;/*visite, feu ou eau*/
	rgb color;
	
	init {
		type <- "vide";
	}
}


experiment RESCUE type: gui {

	output {		//Display the textured grid in 3D with the cell altitude corresponding to its grid_value.				
		display view1 /*type:opengl*/{ 
			grid grille lines: #black;
			species fireArea aspect:base;
			species waterArea aspect:base;
			species helicopter aspect: bdi;
		}
	}

}