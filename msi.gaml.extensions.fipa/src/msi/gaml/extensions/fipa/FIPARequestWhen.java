/*********************************************************************************************
 * 
 *
 * 'FIPARequestWhen.java', in plugin 'msi.gaml.extensions.fipa', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.extensions.fipa;

/**
 * Implementation of the FIPA Request When interaction protocol.
 * Reference : http://www.fipa.org/specs/fipa00028/SC00028H.html
 */

import static msi.gaml.extensions.fipa.FIPAConstants.*;
import static msi.gaml.extensions.fipa.FIPAConstants.Performatives.*;

/**
 * The Class FIPARequestWhen.
 */
public class FIPARequestWhen extends FIPAProtocol {

	/** Definition of protocol model. */
	private static Object[] __after_cancel = { FAILURE,
			Integer.valueOf(CONVERSATION_END), PARTICIPANT, null, INFORM,
			Integer.valueOf(CONVERSATION_END), PARTICIPANT, null };

	/** The __after_agree. */
	private static Object[] __after_agree = { FAILURE,
			Integer.valueOf(CONVERSATION_END), PARTICIPANT, null, INFORM,
			Integer.valueOf(CONVERSATION_END), PARTICIPANT, null };

	/** The __after_request. */
	private static Object[] __after_request = { CANCEL,
			Integer.valueOf(AGENT_ACTION_REQ), INITIATOR, __after_cancel, REFUSE,
			Integer.valueOf(CONVERSATION_END), PARTICIPANT, null, AGREE,
			Integer.valueOf(NO_AGENT_ACTION_REQ), PARTICIPANT, __after_agree };

	/** The roots. */
	public static Object[] roots = { REQUEST_WHEN,
			Integer.valueOf(AGENT_ACTION_REQ), INITIATOR, __after_request };

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.misc.current_development.FIPAProtocol#getName()
	 */
	@Override
	public int getIndex() {
		return FIPAConstants.Protocols.FIPA_REQUEST_WHEN;
	}

	@Override
	public String getName() {
		return FIPAConstants.Protocols.FIPA_REQUEST_WHEN_STR;
	}
}
