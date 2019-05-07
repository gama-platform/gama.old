package msi.gaml.extensions.fipa;

/**
 * Constants identifying the FIPA performatives. These performatives will be employed to implement the interaction
 * protocol in GAMA.
 */

public enum Performative {

	accept_proposal,
	agree,
	cancel,
	cfp,
	failure,
	inform,
	not_understood,
	propose,
	proxy,
	query,
	refuse,
	reject_proposal,
	request,
	request_when,
	subscribe,
	end_conversation;

}