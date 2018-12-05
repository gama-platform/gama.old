/*******************************************************************************************************
 *
 * msi.gaml.extensions.fipa.FIPAQuery.java, in plugin msi.gaml.extensions.fipa, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.extensions.fipa;

import static msi.gaml.extensions.fipa.FIPAConstants.AGENT_ACTION_REQ;
import static msi.gaml.extensions.fipa.FIPAConstants.CONVERSATION_END;
import static msi.gaml.extensions.fipa.FIPAConstants.Performatives.AGREE;
import static msi.gaml.extensions.fipa.FIPAConstants.Performatives.CANCEL;
import static msi.gaml.extensions.fipa.FIPAConstants.Performatives.FAILURE;
import static msi.gaml.extensions.fipa.FIPAConstants.Performatives.INFORM;
import static msi.gaml.extensions.fipa.FIPAConstants.Performatives.QUERY;
import static msi.gaml.extensions.fipa.FIPAConstants.Performatives.REFUSE;

/**
 * Implementation of the FIPA Query interaction protocol. Reference : http://www.fipa.org/specs/fipa00027/SC00027H.html
 */
public class FIPAQuery extends FIPAProtocol {

	/** Definition of protocol model. */
	private static Object[] __after_cancel = { FAILURE, Integer.valueOf(CONVERSATION_END), PARTICIPANT, null, INFORM,
			Integer.valueOf(CONVERSATION_END), PARTICIPANT, null };

	/** The __after_agree. */
	private static Object[] __after_agree = { FAILURE, Integer.valueOf(CONVERSATION_END), PARTICIPANT, null, INFORM,
			Integer.valueOf(CONVERSATION_END), PARTICIPANT, null };

	/** The __after_query. */
	private static Object[] __after_query = { CANCEL, Integer.valueOf(AGENT_ACTION_REQ), INITIATOR, __after_cancel,
			REFUSE, Integer.valueOf(CONVERSATION_END), PARTICIPANT, null, AGREE, Integer.valueOf(AGENT_ACTION_REQ),
			PARTICIPANT, __after_agree };

	/** The roots. */
	public static Object[] roots = {
			// QUERY_REF, new
			// Integer(AGENT_ACTION_REQ), INITIATOR,
			// __after_query,
			QUERY, Integer.valueOf(AGENT_ACTION_REQ), INITIATOR, __after_query };

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.misc.current_development.FIPAProtocol#getName()
	 */
	// @Override
	// public int getIndex() {
	// return FIPAConstants.Protocols.FIPA_QUERY;
	// }

	@Override
	public String getName() {
		return FIPAConstants.Protocols.FIPA_QUERY_STR;
	}
}
