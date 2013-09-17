model fipa_request_1

global {
	Initiator i;
	Participant p;
	
	init {
		create Initiator returns: is;
		create Participant returns: ps;
		
		set p <- ps at 0;
		
		write 'Step the simulation to observe the outcome in the console';
	}
}

entities {
	species Initiator skills: [communicating] {
		reflex print_debug_infor {
			write name + ' with conversations: ' + (string(conversations)) + '; messages: ' + (string(messages));
		}
		
		reflex send_request when: (time = 1) {
			write 'send message';
			do send with: [ receivers :: [p], protocol :: 'fipa-request', performative :: 'request', content :: ['aller dormir'] ];
		}
		
		reflex read_refuse_message when: !(empty(refuses)) {
			write 'read refuse messages';
			loop r over: refuses {
				write 'refuse message with content: ' + string(r.content);
			}
		}
	}
	
	species Participant skills: [communicating] {
		reflex print_debug_infor {
			write name + ' with conversations: ' + (string(conversations)) + '; messages: ' + (string(messages));
		}

		reflex reply_messages when: (!empty(messages)) {
			write 'refuse message';
			do inform with: [ message :: (messages at 0), content :: ['je ne veux pas'] ];
		}
	}
}

experiment test type: gui {}