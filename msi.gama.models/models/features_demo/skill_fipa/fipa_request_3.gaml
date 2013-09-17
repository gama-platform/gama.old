model fipa_request_3

global {
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
			let requestFromInitiator <- (messages at 0) type: message;
			write 'agree message';
			do agree with: [ message :: requestFromInitiator, content :: ['je vais le faire'] ];
			
			write 'inform the initiator';
			do inform with: [ message :: requestFromInitiator, content :: ['je me trouve dans le lit'] ];
		}
	}
}


experiment test type: gui {}