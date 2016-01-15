/**
 * This model demontrates a usecase of the FIPA Query interaction protocol.
 * (Please see http://www.fipa.org/specs/fipa00027/SC00027H.html for the detail description of this protocol).
 * 
 * 
 * The Initiator agent begins the 'fipa-query' conversation/interaction protocol by sending a 'query'
 * message to the Participant agent with 'your name?' as content.
 * 
 * On receiving the 'query' message, the Participant agent replies with two consecutive messages :
 * 	(1) an 'agree' message indicating that the Participant agent accepts to execute the query of the Initiator agent,
 *  (2) an 'inform' message informing the Initiation agent of Participant agent's name.
 * 
 * After the Initiator agent read the 'inform' message from the Participant agent, the conversation ends.
 */
model fipa_query_1

global {
	Participant p;
	
	init {
		create Initiator;
		create Participant returns: ps;
		
		set p <- ps at 0;
		
		write 'Step the simulation to observe the outcome in the console';
	}
}

species Initiator skills: [communicating] {
	reflex send_query_message when: (time = 1) {
		write name + ' sends query message';
		do start_conversation with: [ receivers :: [p], protocol :: 'fipa-query', performative :: 'query', content :: ['your name?'] ];
	}
	
	reflex read_inform_message when: !(empty(informs)) {
		write name + ' reads inform messages';
		loop i over: informs {
			write 'inform message with content: ' + (string(i.content));
		}
	}
}

species Participant skills: [communicating] {

	reflex reply_query_messages when: !(empty(queries)) {
		message queryFromInitiator <- queries at 0;
		
		write name + ' reads a query message with content : ' + (string(queryFromInitiator.content));
		
		do agree with: [ message :: queryFromInitiator, content :: ['OK, I will answer you'] ];		
		do inform with: [ message :: queryFromInitiator, content :: [ 'My name is ' + name ] ];
	}
}

experiment test_query_interaction_protocol type: gui {}