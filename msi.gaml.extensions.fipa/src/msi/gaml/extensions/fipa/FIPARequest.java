/*******************************************************************************************************
 *
 * msi.gaml.extensions.fipa.FIPARequest.java, in plugin msi.gaml.extensions.fipa,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.extensions.fipa;

/**
 * Implementation of the FIPA Request interaction protocol. Reference :
 * http://www.fipa.org/specs/fipa00026/SC00026H.html
 */
public class FIPARequest extends FIPAProtocol {

	/** Definition of protocol model. */
	private static Object[] __after_cancel = { FIPAConstants.Performatives.FAILURE,
			Integer.valueOf(FIPAConstants.CONVERSATION_END), PARTICIPANT, null, FIPAConstants.Performatives.INFORM,
			Integer.valueOf(FIPAConstants.CONVERSATION_END), PARTICIPANT, null };

	/** The __after_agree. */
	private static Object[] __after_agree = { FIPAConstants.Performatives.FAILURE,
			Integer.valueOf(FIPAConstants.CONVERSATION_END), PARTICIPANT, null, FIPAConstants.Performatives.INFORM,
			Integer.valueOf(FIPAConstants.CONVERSATION_END), PARTICIPANT, null };

	/** The __after_request. */
	private static Object[] __after_request = { FIPAConstants.Performatives.NOT_UNDERSTOOD,
			Integer.valueOf(FIPAConstants.CONVERSATION_END), PARTICIPANT, null, FIPAConstants.Performatives.CANCEL,
			Integer.valueOf(FIPAConstants.AGENT_ACTION_REQ), INITIATOR, __after_cancel,
			FIPAConstants.Performatives.REFUSE, Integer.valueOf(FIPAConstants.CONVERSATION_END), PARTICIPANT, null,
			FIPAConstants.Performatives.AGREE, Integer.valueOf(FIPAConstants.AGENT_ACTION_REQ), PARTICIPANT,
			__after_agree };

	/** The roots. */
	public static Object[] roots = { FIPAConstants.Performatives.REQUEST,
			Integer.valueOf(FIPAConstants.AGENT_ACTION_REQ), INITIATOR, __after_request };

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.extensions.fipa.FIPAProtocol#getIndex()
	 */
	// @Override
	// public int getIndex() {
	// return FIPAConstants.Protocols.FIPA_REQUEST;
	// }

	@Override
	public String getName() {
		return FIPAConstants.Protocols.FIPA_REQUEST_STR;
	}
}
