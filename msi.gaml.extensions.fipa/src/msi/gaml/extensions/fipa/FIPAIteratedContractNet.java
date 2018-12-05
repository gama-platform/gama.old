/*******************************************************************************************************
 *
 * msi.gaml.extensions.fipa.FIPAIteratedContractNet.java, in plugin msi.gaml.extensions.fipa,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.extensions.fipa;

/**
 * Implementation of the FIPA Iterated Contract Net interaction protocol. Reference :
 * http://www.fipa.org/specs/fipa00030/SC00030H.html
 */
public class FIPAIteratedContractNet extends FIPAProtocol {

	/** Definition of protocol model. */
	private static Object[] __after_cancel = { FIPAConstants.Performatives.FAILURE,
			Integer.valueOf(FIPAConstants.CONVERSATION_END), PARTICIPANT, null, FIPAConstants.Performatives.INFORM,
			Integer.valueOf(FIPAConstants.CONVERSATION_END), PARTICIPANT, null };

	/** The __after_accept. */
	private static Object[] __after_accept = { FIPAConstants.Performatives.FAILURE,
			Integer.valueOf(FIPAConstants.CONVERSATION_END), PARTICIPANT, null, FIPAConstants.Performatives.INFORM,
			Integer.valueOf(FIPAConstants.CONVERSATION_END), PARTICIPANT, null };

	/** The __after_propose. */
	private static Object[] __after_propose = { FIPAConstants.Performatives.ACCEPT_PROPOSAL,
			Integer.valueOf(FIPAConstants.AGENT_ACTION_REQ), INITIATOR, __after_accept,
			FIPAConstants.Performatives.REJECT_PROPOSAL, Integer.valueOf(FIPAConstants.AGENT_ACTION_REQ), INITIATOR,
			null, FIPAConstants.Performatives.CANCEL, Integer.valueOf(FIPAConstants.AGENT_ACTION_REQ), INITIATOR,
			__after_cancel };

	/** The __after_cfp. */
	private static Object[] __after_cfp =
			{ FIPAConstants.Performatives.CANCEL, Integer.valueOf(FIPAConstants.AGENT_ACTION_REQ), INITIATOR,
					__after_cancel, FIPAConstants.Performatives.REFUSE, Integer.valueOf(FIPAConstants.CONVERSATION_END),
					PARTICIPANT, null, FIPAConstants.Performatives.PROPOSE,
					Integer.valueOf(FIPAConstants.AGENT_ACTION_REQ), PARTICIPANT, __after_propose };

	/** The roots. */
	public static Object[] roots = { FIPAConstants.Performatives.CFP, Integer.valueOf(FIPAConstants.AGENT_ACTION_REQ),
			INITIATOR, __after_cfp };

	static {
		// Setup loop in protocol
		__after_propose[7] = roots;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.misc.current_development.FIPAProtocol#getName()
	 */
	// @Override
	// public int getIndex() {
	// return FIPAConstants.Protocols.FIPA_ITERATED_CONTRACT_NET;
	// }

	@Override
	public String getName() {
		return FIPAConstants.Protocols.FIPA_ITERATED_CONTRACT_NET_STR;
	}
}
