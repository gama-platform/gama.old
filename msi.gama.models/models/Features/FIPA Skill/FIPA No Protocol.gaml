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
		do send with: [ receivers :: [p], protocol :: 'no-protocol', performative :: 'inform', content :: [ ('Hello from ' + name)] ];
	}
	
	reflex read_hello_from_participant when: (time = 3) {
		loop i over: informs {
			write name + ' receives message with content: ' + (string(i.content));
			do reply with: [ message :: i, performative :: 'inform', content :: [ ('Goodbye from ' + name)] ];
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
			do reply with: [ message :: m, performative :: 'inform', content :: [ ('Rebound hello from ' + name) ] ];
		}
	}
	
	reflex read_goodbye when: (time = 4) {
		loop i over: informs {
			write name + ' receives message with content: ' + (string(i.content));
			do reply with: [ message :: i, performative :: 'end_conversation', content :: [ ('Rebound goobye from' + name) ] ];
		}
	}
}


experiment test type: gui {}