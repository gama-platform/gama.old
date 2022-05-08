/**
* Name: FIPA Propose (2)
* Author:
* Description: This model demonstrates a usecase of the FIPA Propose interaction protocol. 
* (Please see http://www.fipa.org/specs/fipa00036/SC00036H.html for the detail description of this protocol).
* 
* 
* The Initiator agent begins the conversation/interaction protocol by sending a 'propose' 
* message to the Participant agent with 'Go swimming?' as content.
* 
* On receiving the 'propose' message, the participant agent replies with an 'reject_proposal' message 
* indicating that it refuses to go swimming.
* 
* After the Initiator agent reads the 'reject_proposal' message, the conversation ends.
* Tags: fipa
*/
model fipa_propose_2

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
	reflex send_propose_message when: (time = 1) {
		write name + ' sends a propose message';
		do start_conversation to: [p] protocol: 'fipa-propose' performative: 'propose' contents: ['Go swimming?'] ;
	}

	reflex read_accept_proposals when: !(empty(reject_proposals)) {
		write name + ' receives reject_proposal messages';
		loop i over: reject_proposals {
			write 'reject_proposal message with content: ' + string(i.contents);
		}
	}
}

species Participant skills: [fipa] {
	reflex accept_proposal when: !(empty(proposes)) {
		message proposalFromInitiator <- proposes at 0;
		
		do reject_proposal message: proposalFromInitiator contents: ['No! It \'s too cold today!'] ;
	}
}

experiment test_propose_interaction_protocol type: gui {}
