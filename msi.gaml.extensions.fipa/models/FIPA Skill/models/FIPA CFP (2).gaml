/**
 * This model demonstrates a use-case of the FIPA Contract Net interaction protocol. 
 * 		
 * One initiator sends a 'cfp' message to 5 participants.
 * 
 * The first participants (participant0) replies with a refuse message.
 * Four participants (parcicipant1, parcicipant2, parcicipant3, parcicipant4) reply with four propose messages.
 * 
 * When the initiator receives the propose messages, it proceeds as follows:
 * The initiator replies the participant1 with a reject_proposal message.
 * The initiator replies participant2, participant3, participant4 with three accept_proposal messages respectively.
 * 
 * When participant2, participant3 and participant4 receive the accept_proposal messages from the initiator, they proceed as follows:
 * Participant2 replies with a failure message.
 * Participant3 replies with an inform_done message.
 * Participant4 replies with an inform_result message.
 */
model cfp_cfp_2

global {
	int nbOfParticipants <- 5;
	participant refuser;
	list<participant> proposers <- [];
	participant reject_proposal_participant;
	list<participant> accept_proposal_participants <- [];
	participant failure_participant;
	participant inform_done_participant;
	participant inform_result_participant;
	
	
	init {
		create initiator;
		create participant number: nbOfParticipants returns: ps;
		
		refuser <- ps[0];
		
		add ps all: true to: proposers;
		remove refuser from: proposers;
		
		reject_proposal_participant <- proposers[0];
		
		add proposers all: true to: accept_proposal_participants;
		remove reject_proposal_participant from: accept_proposal_participants;
		
		failure_participant <- accept_proposal_participants[0];
		inform_done_participant <- accept_proposal_participants[1];
		inform_result_participant <- accept_proposal_participants[2];
		
		write 'Please step the simulation to observe the outcome in the console';
	}
}

species initiator skills: [communicating] {
	
	reflex send_cfp_to_participants when: (time = 1) {
		
		write '(Time ' + time + '): ' + name + ' sends a cfp message to all participants';
		do start_conversation with: [ receivers :: list(participant), protocol :: 'fipa-contract-net', performative :: 'cfp', content :: ['Go swimming'] ];
	}
	
	reflex receive_refuse_messages when: !empty(refuses) {
		write '(Time ' + time + '): ' + name + ' receives refuse messages';
		
		loop r over: refuses {
			write '\t' + name + ' receives a refuse message from ' + r.sender.name + ' with content ' + r.content ;
		}
	}
	
	reflex receive_propose_messages when: !empty(proposes) {
		write '(Time ' + time + '): ' + name + ' receives propose messages';
		
		loop p over: proposes {
			write '\t' + name + ' receives a propose message from ' + p.sender.name + ' with content ' + p.content ;
			
			if (p.sender = reject_proposal_participant) {
				write '\t' + name + ' sends a reject_proposal message to ' + p.sender;
				do reject_proposal with: [ message :: p, content :: ['Not interested in your proposal'] ];
			} else {
				write '\t' + name + ' sends a accept_proposal message to ' + p.sender;
				do accept_proposal with: [ message :: p, content :: ['Interesting proposal. Go do it'] ];
			}
		}
	}
	
	reflex receive_failure_messages when: !empty(failures) {
		message f <- failures[0];
		write '\t' + name + ' receives a failure message from ' + f.sender.name + ' with content ' + f.content ;
	}
	
	reflex receive_inform_messages when: !empty(informs) {
		write '(Time ' + time + '): ' + name + ' receives inform messages';
		
		loop i over: informs {
			write '\t' + name + ' receives a inform message from ' + i.sender.name + ' with content ' + i.content ;
		}
	}
}

species participant skills: [communicating] {
	
	reflex receive_cfp_from_initiator when: !empty(cfps) {
		
		message proposalFromInitiator <- cfps[0];
		write '(Time ' + time + '): ' + name + ' receives a cfp message from ' + proposalFromInitiator.sender.name + ' with content ' + proposalFromInitiator.content;
		
		if (self = refuser) {
			write '\t' + name + ' sends a refuse message to ' + proposalFromInitiator.sender.name;
			do refuse with: [ message :: proposalFromInitiator, content :: ['I am busy today'] ];
		}
		
		if (self in proposers) {
			write '\t' + name + ' sends a propose message to ' + proposalFromInitiator.sender.name;
			do propose with: [ message :: proposalFromInitiator, content :: ['Ok. That sound interesting'] ];
		}
	}
	
	reflex receive_reject_proposals when: !empty(reject_proposals) {
		message r <- reject_proposals[0];
		write '(Time ' + time + '): ' + name + ' receives a reject_proposal message from ' + r.sender.name + ' with content ' + r.content;
	}
	
	reflex receive_accept_proposals when: !empty(accept_proposals) {
		message a <- accept_proposals[0];
		write '(Time ' + time + '): ' + name + ' receives a accept_proposal message from ' + a.sender.name + ' with content ' + a.content;
		
		if (self = failure_participant) {
			write '\t' + name + ' sends a failure message to ' + a.sender.name;
			do failure with: [ message :: a, content :: ['Failure'] ];
		}
		
		if (self = inform_done_participant) {
			write '\t' + name + ' sends an inform_done message to ' + a.sender.name;
			do inform with: [ message :: a, content :: ['Inform done'] ];
		}
		
		if (self = inform_result_participant) {
			write '\t' + name + ' sends an inform_result message to ' + a.sender.name;
			do inform with: [ message :: a, content :: ['Inform result'] ];
		}
	}
}

experiment test type: gui {
	output {
		
	}
}