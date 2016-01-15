/**
 * This model demontrates a usecase of the FIPA Query interaction protocol.
 * (Please see http://www.fipa.org/specs/fipa00027/SC00027H.html for the detail description of this protocol).
 * 
 * 
 * The Intiator agent begins the 'fipa-query' conversation/interaction protocol by sending a 'query'
 * message to the Participant agent with 'your name?' as content.
 * 
 * On receiving the 'query' message, the Participant agent replies with a 'refuse' message indicating that its name is a secret!
 * 
 * After the Initiator agent reads the 'refuse' message, the conversation ends.
 */
model fipa_query_2

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
		write name + ' sends a query message';
		do start_conversation with: [ receivers :: [p], protocol :: 'fipa-query', performative :: 'query', content :: ['your name?'] ];
	}
	
	reflex read_refuse_messages when: !(empty(refuses)) {
		write name + ' receives refuse messages';
		loop i over: refuses {
			write 'refuse message with content: ' + (string(i.content));
		}
	}
}

species Participant skills: [communicating] {
	reflex reply_query_messages when: !(empty(queries)) {
		message queryFromInitiator  <- queries at 0;
		
		write name + ' reads a query message with content : ' + (string(queryFromInitiator.content));
		
		do refuse with: [ message :: queryFromInitiator, content :: ['No! That is a secret!'] ];		
	}
}

experiment test_query_interaction_protocol type: gui {}