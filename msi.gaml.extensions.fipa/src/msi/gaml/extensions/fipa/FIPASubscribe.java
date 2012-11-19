/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC 
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gaml.extensions.fipa;

import static msi.gaml.extensions.fipa.FIPAConstants.*;
import static msi.gaml.extensions.fipa.FIPAConstants.Performatives.*;

/**
 * Implementation of the FIPA Subscribe interaction protocol. Reference :
 * http://www.fipa.org/specs/fipa00035/SC00035H.html
 */
public class FIPASubscribe extends FIPAProtocol {

	/** Definition of protocol model. */
	private static Object[] __after_cancel = { FAILURE,
			Integer.valueOf(CONVERSATION_END), PARTICIPANT, null, INFORM,
			Integer.valueOf(CONVERSATION_END), PARTICIPANT, null };

	/** The __after_agree. */
	private static Object[] __after_agree = { INFORM,
			Integer.valueOf(AGENT_ACTION_REQ), PARTICIPANT, null, CANCEL,
			Integer.valueOf(CONVERSATION_END), INITIATOR, __after_cancel, FAILURE,
			Integer.valueOf(CONVERSATION_END), PARTICIPANT, null };

	/** The __after_request. */
	private static Object[] __after_request = { REFUSE,
			Integer.valueOf(CONVERSATION_END), PARTICIPANT, null, AGREE,
			Integer.valueOf(AGENT_ACTION_REQ), PARTICIPANT, __after_agree, CANCEL,
			Integer.valueOf(CONVERSATION_END), INITIATOR, __after_cancel };

	/** The roots. */
	public static Object[] roots = { SUBSCRIBE,  Integer.valueOf(AGENT_ACTION_REQ),
			INITIATOR, __after_request };

	static {
		__after_agree[3] = __after_agree;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.misc.current_development.FIPAProtocol#getName()
	 */
	@Override
	public int getIndex() {
		return FIPAConstants.Protocols.FIPA_SUBSCRIBE;
	}

	@Override
	public String getName() {
		return FIPAConstants.Protocols.FIPA_SUBSCRIBE_STR;
	}
}
