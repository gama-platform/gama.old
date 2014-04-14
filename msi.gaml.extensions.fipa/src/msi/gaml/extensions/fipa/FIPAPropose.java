/*********************************************************************************************
 * 
 *
 * 'FIPAPropose.java', in plugin 'msi.gaml.extensions.fipa', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.extensions.fipa;

/**
 * Implementation of the FIPA Propose interaction protocol. Reference :
 * http://www.fipa.org/specs/fipa00036/SC00036H.html
 */
public class FIPAPropose extends FIPAProtocol {

	/** Definition of protocol model. */
	private static Object[] __after_cancel = {
			FIPAConstants.Performatives.FAILURE,
			Integer.valueOf(FIPAConstants.CONVERSATION_END), PARTICIPANT, null,
			FIPAConstants.Performatives.INFORM,
			Integer.valueOf(FIPAConstants.CONVERSATION_END), PARTICIPANT, null };

	/** The __after_prop. */
	private static Object[] __after_prop = {
			FIPAConstants.Performatives.REJECT_PROPOSAL,
			Integer.valueOf(FIPAConstants.CONVERSATION_END), PARTICIPANT, null,
			FIPAConstants.Performatives.ACCEPT_PROPOSAL,
			Integer.valueOf(FIPAConstants.CONVERSATION_END), PARTICIPANT, null,
			FIPAConstants.Performatives.CANCEL,
			Integer.valueOf(FIPAConstants.CONVERSATION_END), INITIATOR,
			__after_cancel };

	/** The roots. */
	public static Object[] roots = { FIPAConstants.Performatives.PROPOSE,
			Integer.valueOf(FIPAConstants.AGENT_ACTION_REQ), INITIATOR,
			__after_prop };

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.misc.current_development.FIPAProtocol#getName()
	 */
	@Override
	public int getIndex() {
		return FIPAConstants.Protocols.FIPA_PROPOSE;
	}

	@Override
	public String getName() {
		return FIPAConstants.Protocols.FIPA_PROPOSE_STR;
	}

}
