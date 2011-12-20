model Incoherent_ExecutionStack

import "platform:/plugin/msi.gama.application/generated/std.gaml"


/**
 * Pourriez-vous faire stepper ce modle dans le mode dŽbug de Eclipse pour voir le problme?  
 */
global {
	const environment_size type: int init: 100;
	
	init {
		create species: species_1 number: 1;
		create species: species_2 number: 1;
	}
}

entities {
	species species_1 skills: [situated, moving] {
		var shape type: geometry init: ((square (2.0)) at_location {rnd (environment_size), rnd (environment_size)});
		
		reflex move_around {
			do action: wander {
				/**
				 * ConsidŽrons le contenu de la pile d'exŽcution (une instance de l'AbstractStack)
				 * 
				 * 1. Au pas i (> 0), cet agent bouge avec la vitesse (par dŽfaut) 1m/seconde. Il met la couple {amplitude = 120} au dŽbut du tableau "vars".
				 * 2. Voir 2 en-dessous
				 * 3. Au pas (i+1), le contenu de la variable "varIndex" contient encore la couple {speed = 0}.
				 * 		Et le contenu du tableau "vars" commence par [{amplitude = 120}, ... ].
				 * 		Dans la mŽthode "primMoveRandomly" de la classe MovingSkill, on vŽrifie si l'argument "speed" existe par le code "scope.hasArg(SPEED)".
				 * 		Comme "varIndex" contient la couple {speed = 0} donc on "suppose" que la valeur de la variable "speed" est ˆ la position 0 du tableau "vars".
				 * 		Donc si on rŽcupre la valeur au position 0 du tableau "vars", on recevera 120 ce qui est en fait la valeur de "amplitude".
				 * 		a explique pourquoi cet agent bouge seulment au premier pas de la simulation, comme aprs, la valeur 120 dŽpasse la taille de l'environnement non-torus!
				 * 
				 * A c™tŽ de ce problme, je vois les information dans les deux variables "vars" et "varIndex" sont incohŽrentes! 
				 * Le "myself" par exemple a parfois des fausse valeur, mais je suis pas sur que a peut causer des problmes?  
				 */
				arg amplitude value: 120 ;
			}
		}
		
		aspect default {
			draw shape: geometry color: rgb ('green');
		}
	}
	
	species species_2 skills: [situated, moving] {
		var shape type: geometry init: ((circle (2.0)) at_location {rnd (environment_size), rnd (environment_size)});

		reflex move_around {
			do action: wander {
				/**
				 * 2. Au pas i (> 0), cette agent bouge et met {speed = 0} dans la varialbe "varIndex" 
				 * pour indiquer que la position de la variable "speed" est ˆ la position 0 du tableau "vars".
				 */
				arg name: speed value: 1;
			}
		}

		aspect default {
			draw shape: geometry color: rgb ('red');
		}
	}
}

environment width: environment_size height: environment_size;

experiment default_experiment type: gui {
	output {
		display Standard_Display {
			species species_1;
			species species_2;
		}
	}
}
