/**
* Name: FIPA Request (3)
* Author:
* Description: This model demonstrates a usecase of the FIPA Request interaction protocol. 
* (Please see http://www.fipa.org/specs/fipa00026/index.html for the detail description of this protocol).
* 
* 
* The Initiator agent begins the 'fipa-request' conversation/interaction protocol by sending a 'request'  
* message to the Participant agent with 'go sleeping' as content.
* 
* On receiving the 'request' message, the Participant replies with two consecutive messages :
* 
* (1) an 'agree' message indicating that the Participant agent accepts to execute the request of the Initiator agent,
* 
* (2) an 'inform' message indicating that the Participant agent has already executed the request of the Initiator agent 
* (in this case, the 'inform' message informs the Initiator that the Participant agent has already gone to bed!).
* 
* After the Initiator agent reads the 'inform' message, the conversation ends.
* Tags: fipa
*/
model fipa_request_3

global {
	Participant p;
	
	init {
		create Initiator;
		create Participant returns: ps;
		
		p <- ps at 0;
		
		write 'Step the simulation to observe the outcome in the console';
	}
}

species Initiator skills: [fipa] {
	reflex print_debug_infor {
		write name + ' with conversations: ' + string(conversations) + '; messages: ' + string(mailbox);
	}
	
	reflex send_request when: (time = 1) {
		write 'send message';
		do start_conversation to: [p] protocol: 'fipa-request' performative: 'request' contents: ['go sleeping'] ;
	}

	reflex read_agree_message when: !(empty(agrees)) {
		write 'read agree messages';
		loop a over: agrees {
			write 'agree message with content: ' + string(a.contents);
		}
	}
	
	reflex read_inform_message when: !(empty(informs)) {
		write 'read inform messages';
		loop i over: informs {
			write 'inform message with content: ' + string(i.contents);
		}
	}
}

species Participant skills: [fipa] {
	reflex print_debug_infor {
		write name + ' with conversations: ' + string(conversations) + '; messages: ' + string(mailbox);
	}

	reflex reply_messages when: (!empty(requests)) {
		message requestFromInitiator <- (requests at 0);
		write 'agree message';
		do agree message: requestFromInitiator contents: ['I will'] ;
		
		write 'inform the initiator';
		do inform message: requestFromInitiator contents: ['I\'m in bed already'] ;
	}
}



experiment test_request_interaction_protocol type: gui {}