/**
* Name: FIPA Request (2)
* Author:
* Description: This model demonstrates a usecase of the FIPA Request interaction protocol. 
* (Please see http://www.fipa.org/specs/fipa00026/index.html for the detail description of this protocol).
* 
* 
* The Initiator agent begins the 'fipa-request' conversation/interaction protocol by sending a 'request' 
* message to the Participant agent with 'go sleeping' as content.
* 
* On receiving the 'request' message, the Participant agent replies with two consecutive messages : 
* 
* (1) an 'agree' message indicating that the Participant agent accepts to execute the request of the Initiator agent, 
* 
* (2) a 'failure' message indicating that the Participant agent fails to (can not) execute the requestion of the Initiator agent 
* (in this case, the Participant agent says that it can not go sleeping because the bed is broken!).
* 
* After the Initiator reads the 'failure' message from the Participant, the corresponding conversation ends.
* Tags: fipa
*/
model fipa_request_2

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
	
	reflex read_failure_message when: !(empty(failures)) {
		write 'read failure messages';
		loop f over: failures {
			write 'failure message with content: ' + (string(f.contents));
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
		do agree message: requestFromInitiator contents: ['I will'];
		
		write 'inform the initiator of the failure';
		do failure message: requestFromInitiator contents: ['The bed is broken'];
	}
}



experiment test_request_interaction_protocol type: gui {}