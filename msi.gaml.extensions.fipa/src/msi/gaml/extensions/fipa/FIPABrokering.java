/*********************************************************************************************
 * 
 *
 * 'FIPABrokering.java', in plugin 'msi.gaml.extensions.fipa', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.extensions.fipa;

/**
 * Implementation of the FIPA Brokering interaction protocol. Reference :
 * http://www.fipa.org/specs/fipa00033/SC00033H.html
 */
public class FIPABrokering extends FIPAProtocol {

	/** Definition of protocol model. */
	private static Object[] __after_cancel = {
			FIPAConstants.Performatives.FAILURE,
			Integer.valueOf(FIPAConstants.CONVERSATION_END),  Integer.valueOf(1), null,
			FIPAConstants.Performatives.INFORM,
			Integer.valueOf(FIPAConstants.CONVERSATION_END),  Integer.valueOf(1), null };

	/** The __after_inform. */
	private static Object[] __after_inform = {
			FIPAConstants.Performatives.FAILURE,
			Integer.valueOf(FIPAConstants.CONVERSATION_END),  Integer.valueOf(1), null,
			FIPAConstants.Performatives.INFORM,
			Integer.valueOf(FIPAConstants.CONVERSATION_END), Integer.valueOf(1), null };

	/** The __after_agree. */
	private static Object[] __after_agree = {
			FIPAConstants.Performatives.CANCEL,
			Integer.valueOf(FIPAConstants.AGENT_ACTION_REQ), INITIATOR,
			__after_cancel, FIPAConstants.Performatives.FAILURE,
			Integer.valueOf(FIPAConstants.CONVERSATION_END),  Integer.valueOf(1), null,
			FIPAConstants.Performatives.INFORM,
			Integer.valueOf(FIPAConstants.AGENT_ACTION_REQ),  Integer.valueOf(1),
			__after_inform };

	/** The __after_req. */
	private static Object[] __after_req = { FIPAConstants.Performatives.CANCEL,
		Integer.valueOf(FIPAConstants.AGENT_ACTION_REQ), INITIATOR,
			__after_cancel, FIPAConstants.Performatives.REFUSE,
			Integer.valueOf(FIPAConstants.CONVERSATION_END), Integer.valueOf(1), null,
			FIPAConstants.Performatives.AGREE,
			Integer.valueOf(FIPAConstants.NO_AGENT_ACTION_REQ), Integer.valueOf(1),
			__after_agree };

	/** The roots. */
	public static Object[] roots = { FIPAConstants.Performatives.PROXY,
			 Integer.valueOf(FIPAConstants.AGENT_ACTION_REQ), INITIATOR, __after_req };

	static {
		__after_req[3] = roots;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.misc.current_development.FIPAProtocol#getName()
	 */
	@Override
	public int getIndex() {
		return FIPAConstants.Protocols.FIPA_BROKERING;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return FIPAConstants.Protocols.FIPA_BROKERING_STR;
	}
}
