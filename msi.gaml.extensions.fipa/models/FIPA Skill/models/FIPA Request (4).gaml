/**
 * This model demontrates a usecase of the FIPA Request interaction protocol.
 * (Please see http://www.fipa.org/specs/fipa00026/index.html for the detail description of this protocol).
 * 
 * 
 * The Initiator agent begins the 'fipa-request' conversation/intaction protocol by sending a 'request' 
 * message to the Participant agent with 'go sleeping' as content.
 * 
 * On receiving a 'request' message, the Participant agent replies with an 'inform' message.
 * According to the specification of the FIPA Requestion interaction protocol, the next possible messages 
 * after receiving a 'request' message are either 'refuse' message or 'agree' message.
 * Hence replying with an 'inform' message upon receiving a 'request' message violates the protocol specification.
 * GAMA will hence raise a GamaRuntimeException. 
 * A conversation is automatically ended in case of GamaRuntimeException raised. Hence this conversation ends.
 */
model fipa_request_4

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
		write name + ' sends an inform message';
		
		
		write 'A GamaRuntimeException is raised to inform that the message\'s performative doesn\'t respect the \'request\' interaction protocol\' specification';
		do inform with: [ message :: (messages at 0), content :: ['I don\'t want'] ]; // Attention: note that GAMA will raise an exception because an 'inform' message is not appropriate here.
	}
}


experiment test_request_interaction_protocol type: gui {}