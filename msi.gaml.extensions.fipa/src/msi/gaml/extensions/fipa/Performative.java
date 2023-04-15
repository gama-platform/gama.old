/*******************************************************************************************************
 *
 * Performative.java, in msi.gaml.extensions.fipa, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.extensions.fipa;

/**
 * Constants identifying the FIPA performatives. These performatives will be employed to implement the interaction
 * protocol in GAMA.
 */

public enum Performative {

	/** The accept proposal. */
	accept_proposal,
	
	/** The agree. */
	agree,
	
	/** The cancel. */
	cancel,
	
	/** The cfp. */
	cfp,
	
	/** The failure. */
	failure,
	
	/** The inform. */
	inform,
	
	/** The not understood. */
	not_understood,
	
	/** The propose. */
	propose,
	
	/** The proxy. */
	proxy,
	
	/** The query. */
	query,
	
	/** The refuse. */
	refuse,
	
	/** The reject proposal. */
	reject_proposal,
	
	/** The request. */
	request,
	
	/** The request when. */
	request_when,
	
	/** The subscribe. */
	subscribe,
	
	/** The end conversation. */
	end_conversation;

}