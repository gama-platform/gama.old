/**
 *  laby1
 *  Author: carole
 *  Description: 
 */
model laby1


global
{
/** Insert the global definitions, variables and actions here */

// Parameters 
	int nbOfExplorers <- 1;
	int nbOfMonsters <- 3;
	int nbOfWeapons <- 3;
	int nbOfGold <- 30;
	int nbOfPotions <- 10;
	int wallDensity min:0 max:100 <- 20;
	
	//bool t <- true bdiet false; 

	// Environment
	geometry shape <- rectangle(50, 50);

	// graymap, icons and colors
	//const types type: file<int> <- file<int>('../images/sugarscape.pgm');
	
	// constants for types of cells
	const WALL type: int <- 1;
	const OPEN type: int <- 2;
	const DOOR type: int <- 3;
	// and their colors
	const WALL_COLOR type: rgb <- #black;
	const OPEN_COLOR type: rgb <- #white;
	const DOOR_COLOR type: rgb <- #green;
	
	// constants for objects types 
	const GOLD type: int <- 1;
	const POTION type: int <- 2;
	const WEAPON type: int <- 3;
	// and their colors
	const GOLD_COLOR type: rgb <- rgb('gold');
	const POTION_COLOR type: rgb <- #blue;
	const WEAPON_COLOR type: rgb <- #purple;
	
	/** init global **/
	init
	{
		
		//hello dest: 'kaolla' message: 'coucou';
		
		// init cells with open + randomly place some walls and a door
		ask laby_cell {
			color <- OPEN_COLOR;
			labyCellType <- OPEN;	
		}
		ask (wallDensity*25) among (laby_cell where (each.color = #white))
		{
			labyCellType <- WALL;
			color <- WALL_COLOR;
		}

		ask 1 among (laby_cell where (each.grid_x = 0 or each.grid_y = 0 or each.grid_x = 50 or each.grid_y = 50))
		{
			labyCellType <- DOOR;
			color <- DOOR_COLOR;
		}
		
		// create agents
		create explorer number: nbOfExplorers {
			location <- (any(laby_cell where (each.labyCellType != WALL))).location;
		}
		create monster number: nbOfMonsters {
			location <- (any(laby_cell where (each.labyCellType != WALL))).location;
		}
		create object number: (nbOfGold+nbOfPotions+nbOfWeapons){
			location <- (any(laby_cell where (each.labyCellType != WALL))).location;
		}

		// place objects
		ask nbOfGold among (object where (each.objectType=0)) {
			objectType <- GOLD;
			color <- GOLD_COLOR;
			place.containsGold <- true;
		}
		ask nbOfPotions among (object where (each.objectType=0)) {
			objectType <- POTION;
			color <- POTION_COLOR;
			place.containsPotion <- true;
		}
		ask nbOfWeapons among (object where (each.objectType=0)) {
			objectType <- WEAPON;
			color <- WEAPON_COLOR;
			place.containsWeapon <- true;
		}		
				
	}

}

// the labyrinth grid - optimisation facets
grid laby_cell width: 50 height: 50 neighbours: 4 use_individual_shapes: false use_regular_agents: false
{

// type of cell
	int labyCellType among: [WALL, OPEN, DOOR] <- OPEN;
	rgb color; // <- #white update: (labyCellType=WALL) ? #black : ( (labyCellType=OPEN) ? #white : ( (labyCellType = DOOR) ? #red : #orange )) ;
	// function: { };
	//rgb color <- labyCellColor;
	
	// contents (objects)
	bool containsGold <- false;
	bool containsPotion <- false;
	bool containsWeapon <- false;
	
	//rgb color update: [white,FFFFAA,FFFF55,yellow,dark_yellow] at sugar;
	//map<int,list<sugar_cell>> neighbours;
	/**init {
			loop i from: 1 to: maxRange {
				neighbours[i] <- self neighbours_at i; 
			}
		}**/

	/* initialise current laby_cell (done for each) **/
	init
	{
	}

}

// explorers are the (future BDI) agents that explore the labyrinth
// and look for an exit, collect gold, pick up objects and weapons, fight monsters...
species explorer skills: [moving] control:fsm {
	
	/** ATTRIBUTES OF EXPLORERS **/
	
	// movement and location
	float speed <- 2.0;
	laby_cell place update: laby_cell(location);
	
	// list of visible objects
	list<object> visible_objects <- [] update: neighbours_of(topology(laby_cell),self,1) of_species object;
	list<monster> visible_monsters <- [] update: neighbours_of(topology(laby_cell),self,1) of_species monster;
	

	
	// map to simulate knowledge base for now
	map<string,float> belief_base;
	map<string,float> desire_base;
	map<string,string> desires;
	
	// map for emotions and moods
	// mood(name,deg)
	map<string,float> moods;
	// emotion(name,(object,deg))
	map<string,map<string,float>> emotions;
	
	// domain-specific attributes
	bool hasWeapon <- false;
	bool seeWeapon function: {
		length(visible_objects where (each.objectType=WEAPON)) > 0
	} ;
	bool seeMonster function: {
		length(visible_monsters) > 0
	} ;
	
	
	/** GENERIC EMOTIONS FUNCTIONS **/
	string top_mood function: {
		any(moods.keys where (moods[each]=max(moods.values)))
	};
	
	// degrees of emotions
	map<string,float> emotions_degree;
	
	string top_emotion;
	
	// comment renvoyer une paire? type d'emo + objet ?
/*	pair top_emotion function: {
		string emoname <- any(emotions.keys where (emotions[each]=max(moods.values))) :: 'toto')
	};
	comment trouver le max de chaque emotion, puis le max de toutes les emotions
*/

	// comment faire une fonction avec des etapes intermediaires et du code dedans...?
/* 	string test function: {
		string name <- 'blabla ';
		name+'carole' with: 		
		 
	};
*/
	
	/** INIT EACH EXPLORER AGENT **/
	init {
		put 1 at: 'sunny' in: belief_base;
		put 0.77 at: 'sunny' in: desire_base;
		put -1 at: 'daylight' in: belief_base;
		put 0.8 at: 'daylight' in: desire_base;
		
		put '>2' at:'speed' in: desires;
	}
	
	/** REFLEXES OF EXPLORERS **/

	reflex debug {
		//write 'speed variable, value = '+speed;
		//write 'speed var = '+to_gaml('speed');
		//write 'eval gaml = '+eval_gaml('speed');
		//write 'test = '+eval_gaml('speed<=2');
		//write 'speed variable, name = '+name(speed);
		//write 'a';
	}

	// compute joy and sadness
	// sadness: how to put negations in the base? negative degrees!
	reflex joy_appraisal {
		// comment les avoir tous: for - mais comment les combiner?
		string obj <- any(belief_base.keys inter desire_base.keys);
		float new_deg <- belief_base[obj] * desire_base[obj];
		float old_joydeg <- emotions['joy'];
		float old_saddeg <- emotions['sadness'];
		if (new_deg > 0 and new_deg > old_joydeg)  {
			put new_deg at: 'joy' in: emotions;
			//write 'appraised joy about '+obj+' at degree '+new_deg+' at time '+cycle;
		}
		if (new_deg < 0 and -new_deg>old_saddeg) {
			put -new_deg at: 'sadness' in: emotions;
			//write 'appraised sadness about '+obj+' at degree '+-new_deg+' at time '+cycle;
		}
		
		//write 'top emotion at time '+cycle+' is '+top_emotion+' about ? at degree '+emotions[top_emotion];
	}
	
	reflex joy_var {
		loop varname over: desires.keys {
			if (eval_gaml(varname+desires[varname])) {
				//write 'joy about '+varname+' checking condition '+desires[varname];
				// TODO compute degree properly
				put [varname,1] at: 'joy' in: emotions; 
			}
			else {
				//write 'sadness about '+varname+' not checking condition '+desires[varname];
				put [varname,1] at: 'sadness' in: emotions;
			}
		}
	}
	
	// one reflex per emotion type ?
	
	reflex compute_emotiondegrees {
		// max degree of each emotion
		loop emoname over: emotions.keys {
			map<string,float> objdeg <- emotions.values;
			float degree <- max(objdeg.values);
			put degree at: emoname in: emotions_degree;
		}
		top_emotion <- any(emotions_degree.keys where (emotions_degree[each]=max(emotions_degree.values)));
		
		// need to return a pair to also have object of top emotion
		
	//	write 'top emotion is '+top_emotion
	}
	
	
	/** ACTIONS OF EXPLORERS **/
	
	action pickWeapon {
		// select a visible weapon, nil if empty list of visible weapons
		object theWeapon <- any(visible_objects where (each.objectType=WEAPON));
		//write 'the weapon = '+theWeapon;
		
		if (theWeapon != nil) {
			string n <- theWeapon.objectName;
		
			hasWeapon <- true;
			place.containsWeapon <- false;
			theWeapon.picked <- true;
		
			return n+' sword';
		}
	}
	
	action perceiveMonster {
		// select a visible monster
		monster theMonster <- any(visible_monsters); 
		write 'the monster = '+theMonster;
		
		if (theMonster != nil) {
			string n <- theMonster.name;
		
			//seeMonster <- true; // auto, function
			
			return n+' monster';
		}
	}
	
	
	action escapeMonster(string whichMonster) {
		write ' '+self+' escapes monster '+whichMonster;
		// TODO move in opposite direction
		do goto (0,0);
	}
	
	
	action attackMonster (string whichMonster) {
		write ' '+self+' attacks monster '+whichMonster;
	}
	
	
	/** STATES OF EXPLORERS **/
	
	state neutral initial:true {
		write "initial state of "+self;
		transition to: wandering when: true;
	}
	
	state wandering
	{
		//write 'wandering state of '+self;
		do wander amplitude: 120;
		
		
		transition to: picker when: seeWeapon {  //place.containsWeapon {
			do pickWeapon returns:weaponName;
			write 'picked weapon named '+weaponName;
		}
		
		transition to: handleMonster when: seeMonster {
			do perceiveMonster returns: monsterName;
			write " "+self+' sees a monster called '+monsterName;
			write ' and feels '+top_emotion;
		}
		
		//transition to: fight when: hasWeapon
		
		//transition to: flight when: seeMonster;

	}
	
	
	state handleMonster {
		
		write "top emotion in front of monster = "+top_emotion;
		// if fear then do escape
		// else do attack;
		
		transition to: fight when: (top_emotion = "joy") {
			write 'joy transition';
		}
		
		transition to: flight when: (top_emotion = "sadness") {
			write 'sadness transition';	
		}
		
	}
	
	state picker {
		do pickWeapon;
		write 'weapon picked, back to explo';
		transition to: wandering when: hasWeapon;
	}

	state fight {
		write 'fight';
		monster theMonster <- any(visible_monsters);
		write 'fight monster '+theMonster.name;
		do attackMonster(theMonster.name);
		theMonster.killed <- true;
				
		transition to: wandering when: theMonster.killed {
			write 'killed monster '+theMonster.name+', back to explo';
		} 
	}
	
	state flight {
		write 'flight';
		monster theMonster <- any(visible_monsters);
		write 'escape monster'+theMonster.name;
		do escapeMonster(theMonster.name);

		transition to: wandering when: !seeMonster {
			write 'escaped monster '+theMonster.name+', back to explo';
		}
	}

		
		
	
	/** ASPECTS OF EXPLORERS **/
	aspect default
	{
		draw shape: circle(0.5) color: rgb('orange');
	}
}


/*******************************
 * ******* MONSTER SPECY *******
 *******************************/

// monsters wander randomly in the labyrinth and attack explorers
species monster skills:[moving] {
	
	bool killed <- false;
	
	reflex disappear when: killed {  // (place.containsWeapon = false)
		do die;
	}
	
	aspect default {
		draw shape: circle(0.5) color: rgb('red');
	}
}


/******************************
 * ******* OBJECT SPECY *******
 ******************************/

// objects are inanimate things found in the labyrinth
// they can be of several types: gold, weapon, key, potion...
species object {
	int objectType <- 0;
	rgb color;
	bool picked <- false;
	
	laby_cell place update: laby_cell(location);
	
	string objectName function: {
		(objectType=1)? 'gold' : ((objectType=2)?'potion':((objectType=3)?'weapon':'unknown'))
	} ;
	
	init {
		
	}
	
	reflex disappear when: picked {  // (place.containsWeapon = false)
		do die;
	}
	
	aspect default {
		draw shape: triangle(0.5) color: color;
	}
}






/****************************
 * ******* EXPERIMENT *******
 ****************************/

experiment laby1 type: gui
{
	// inputs	
	parameter 'Number of explorers:' var: nbOfExplorers category: 'Model';
	parameter 'Number of monsters:' var: nbOfMonsters category: 'Model';
	parameter 'Number of weapons:' var: nbOfWeapons category: 'Model';
	parameter 'Number of gold:' var: nbOfGold category: 'Model';
	parameter 'Number of potions:' var: nbOfPotions category: 'Model';
	parameter 'Wall density:' var:wallDensity category:'Model';
	
	
	output
	{
	// vue eclipse - fenetre
		display KaolLaby //type: opengl
		{
			grid laby_cell lines: #orange;
			species explorer aspect: default;
			species object aspect:default;
			species monster aspect:default;
		}

		// autre fenetre pour les graphes
/**		display TimeGraph
		{
			chart 'fb_over_time' type: series
			{
				data 'facebook users' value: length(people where (each.hasInfoFB)) color: facebook_blue;
				data 'twitter users' value: length(people where (each.hasInfoTW)) color: twitter_blue;
				data 'tv users' value: length(people where (each.hasInfoTV)) color: tv_red;
			}

		}
**/

	}

}






/*****************************************
 * Things to add for BDI agents
 *   - statement assert : asserts a mental attitude
 *   - mental attitude type, with belief, goal, etc subtypes
 *   - statement check to check validity of a mental attitude?
 *   - KBase data structure part of the BDI architecture?
 *   - plans library? cf Philippe Caillou
 * 
 *   - reasoning: un reflex d'update des bases?
 *   - emotional state qui permette d'etre dans 2 etats a la fois: 
 *       un etat emo (joy, sadness, etc) et un etat "d'action" (wandering, etc)
 * 
 * 	 - a bdiaction / plan statement, inspired from standard action statement
 *     but with additional facets: 
 *        - conditions (of feasibility of action) : list of booleans to check
 *        - expected effects of actions (could be undeterministic with probability) : 
 *             = list of pairs <effect,proba> with 1 if effect is certain
 *   - an intention stack = what the agent intends to do next
 *     replacing the states/transitions
 *    => emotions can be triggered from expectations of effects of intended actions (cf emiliano)
 * 
 * 	 - how do agents observe actions of other agents around them? eg be jealous of another explorer who picks a weapon near them
 */

