/**
 * This model demonstrates a usecase of 'no-protocol' interaction protocol.
 * 
 * 'no-protocol' is a freestyle intecraction protocol in which the modeller
 * 		(1) can send whatever type of message (i.e., message performative) in the corresponding conversation
 * 		(2) is responsible for marking the end of the conversation by sending a message with 'end_conversation' performative. 
 */
model no_protocol_1

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

	reflex say_hello when: (time = 1) {
		do start_conversation with: [ receivers :: [p], protocol :: 'no-protocol', performative :: 'inform', content :: [ ('Hello from ' + name)] ];
	}
	
	reflex read_hello_from_participant when: (time = 3) {
		loop i over: informs {
			write name + ' receives message with content: ' + (string(i.content));
			do inform with: [ message :: i, content :: [ ('Goodbye from ' + name)] ];
		}
	}
	
	reflex read_rebound_goodbye when: (time = 5) {
		loop i over: messages {
			write name + ' receives message with content: ' + (string(i.content));
		}
	}
}

species Participant skills: [communicating] {
	reflex print_debug_infor {
		write name + ' with conversations: ' + (string(conversations)) + '; messages: ' + (string(messages));
	}

	reflex reply_hello when: (time = 2) {
		loop m over: informs {
			write name + ' receives message with content: ' + (string(m.content));
			do inform with: [ message :: m, content :: [ ('Rebound hello from ' + name) ] ];
		}
	}
	
	reflex read_goodbye when: (time = 4) {
		loop i over: informs {
			write name + ' receives message with content: ' + (string(i.content));
			do end_conversation with: [ message :: i, content :: [ ('Rebound goodbye from' + name) ] ];
		}
	}
}


experiment test_no_protocol type: gui {}