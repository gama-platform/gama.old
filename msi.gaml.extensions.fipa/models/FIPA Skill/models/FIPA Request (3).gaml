/**
 * This model demontrates a usecase of the FIPA Request interaction protocol.
 * (Please see http://www.fipa.org/specs/fipa00026/index.html for the detail description of this protocol).
 * 
 * 
 * The Initiator agent begins the 'fipa-request' conversation/interaction protocol by sending a 'request' 
 * message to the Participant agent with 'go sleeping' as content.
 * 
 * On receiving the 'request' message, the Participant replies with two consecutive messages :
 * 	(1) an 'agree' message indicating that the Participant agent accepts to execute the request of the Initiator agent,
 *  (2) an 'inform' message indicating that the Participant agent has already executed the request of the Initiator agent
 * 		(in this case, the 'inform' message informs the Initiator that the Participant agent has already gone to bed!).
 * 
 * After the Initiator agent reads the 'inform' message, the conversation ends.
 */
model fipa_request_3

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
	reflex print_debug_infor {
		write name + ' with conversations: ' + (string(conversations)) + '; messages: ' + (string(messages));
	}
	
	reflex send_request when: (time = 1) {
		write 'send message';
		do start_conversation with: [ receivers :: [p], protocol :: 'fipa-request', performative :: 'request', content :: ['go sleeping'] ];
	}

	reflex read_agree_message when: !(empty(agrees)) {
		write 'read agree messages';
		loop a over: agrees {
			write 'agree message with content: ' + string(a.content);
		}
	}
	
	reflex read_inform_message when: !(empty(informs)) {
		write 'read inform messages';
		loop i over: informs {
			write 'inform message with content: ' + (string(i.content));
		}
	}
}

species Participant skills: [communicating] {
	reflex print_debug_infor {
		write name + ' with conversations: ' + (string(conversations)) + '; messages: ' + (string(messages));
	}

	reflex reply_messages when: (!empty(messages)) {
		message requestFromInitiator <- (messages at 0);
		write 'agree message';
		do agree with: [ message :: requestFromInitiator, content :: ['I will'] ];
		
		write 'inform the initiator';
		do inform with: [ message :: requestFromInitiator, content :: ['I\'m in bed already'] ];
	}
}



experiment test_request_interaction_protocol type: gui {}