/***
* Name: statetransition
* Author: kevinchapuis
* Description: simple finite state machine example to show how make a transition to one state to another
* Tags: fsm, state, transition
***/

model statetransition

global {
	
	int number_of_dummy parameter:true init:10;
	
	init {
		create dummy number:number_of_dummy;
	}
	
}

/**
 * 
 * Finite State Machine control makes it possible to define several possible state of the agent. Each state is associated
 * to behavior and attribute define in the model that can lead to transition between states. 
 * 
 * statement enter : set of instructions to be executed before entering a state
 * 
 * statement transition : [to: the state to transition to] [when: the condition to trigger the transition]
 * 
 * statement exit : set of instructions to be executed after leaving a state
 * 
 * WARNING: this is not a knowledgeable model - the purpose is to explictly state the syntax
 * 
 */
species dummy skills:[moving] control:fsm {
	
	float speed <- 10#m/#s;
	
	int scope <- 1;
	int score;
	
	dummy match;
	
	rgb color <- #orange;
	
	/*
	 * The definition of the state called 'in_seach' which is the initial state of agent:
	 * when created (at the beginning of the simulation), agent will be granted this state
	 * 
	 */
	state in_search initial: true {
		/*
		 * Code to be executed each step when the agent is in this state
		 */
		do wander; 
		match <- (dummy-self) first_with (each distance_to self < scope);
		
		scope <- scope + 1;
		
		/*
		 * Transition to 'settle_down' when a match have been found
		 */
		transition to:settle_down when:not(match=nil) {
			color <- #blue;
		}
		
	}
	
	/**
	 * The definition of the state called 'settle_down' 
	 */
	state settle_down {
		
		do goto target: match;
		score <- score - 1;
		
		/*
		 * The set of instructions to be execute ONCE when agent enter this state
		 */
		enter {
			scope <- 1;
		}
		
		/**
		 * Transition to 'break_up' state when the match agent respond positively to self agent call
		 */
		transition to:break_up when:match.hello(self){
			color <- #green;
		}
		
		/**
		 * Transition to 'in_seach' when score is under or equal to 0
		 */
		transition to:in_search when:score <= 0 { color <- #orange;}
	}
	
	/*
	 * The definition of the state 'break_up'
	 */
	state break_up {
		score <- score + 1;
		
		/**
		 * triggered once when entering this state
		 */
		enter {
			score <- score * int(score/10);
		}
		
		/**
		 * Transition to the state 'in_search' when my score is higher the my match score
		 */
		transition to:in_search when:score > match.score;
		
		/**
		 * Set of instructions to be executed ONCE when leaving the state 
		 */
		exit { 
			score <- (dummy with_min_of each.score).score;
			color <- #orange;
		}
	}
	
	bool hello(dummy paire){
		return score = paire.score ? flip(0.5) : score < paire.score; 
	}
	
	aspect default {
		draw triangle(score) color:color;
		draw circle(scope) color:rgb(color,0.2);
	}
}

experiment dummy_xp {
	output {
		display friendship {
			species dummy;
		}
	}
}
