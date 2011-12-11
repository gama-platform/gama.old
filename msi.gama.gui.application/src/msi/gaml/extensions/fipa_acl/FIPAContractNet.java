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
package msi.gaml.extensions.fipa_acl;

/**
 * Implementation of the FIPA Contract Net interaction protocol. Reference :
 * http://www.fipa.org/specs/fipa00029/SC00029H.html
 */
public class FIPAContractNet extends FIPAProtocol {

	/** Definition of protocol model. */
	private static Object[] __after_cancel = {
			FIPAConstants.Performatives.FAILURE,
			Integer.valueOf(FIPAConstants.CONVERSATION_END), PARTICIPANT, null,
			FIPAConstants.Performatives.INFORM,
			Integer.valueOf(FIPAConstants.CONVERSATION_END), PARTICIPANT, null };

	/** The __after_accept. */
	private static Object[] __after_accept = {
			FIPAConstants.Performatives.FAILURE,
			Integer.valueOf(FIPAConstants.CONVERSATION_END), PARTICIPANT, null,
			FIPAConstants.Performatives.INFORM,
			Integer.valueOf(FIPAConstants.CONVERSATION_END), PARTICIPANT, null };

	/** The __after_propose. */
	private static Object[] __after_propose = {
			FIPAConstants.Performatives.FAILURE,
			Integer.valueOf(FIPAConstants.CONVERSATION_END), INITIATOR, null,
			FIPAConstants.Performatives.CANCEL,
			Integer.valueOf(FIPAConstants.AGENT_ACTION_REQ), INITIATOR,
			__after_cancel, FIPAConstants.Performatives.ACCEPT_PROPOSAL,
			Integer.valueOf(FIPAConstants.AGENT_ACTION_REQ), INITIATOR,
			__after_accept, FIPAConstants.Performatives.REJECT_PROPOSAL,
			Integer.valueOf(FIPAConstants.CONVERSATION_END), INITIATOR, null, };

	/** The __after_cfp. */
	private static Object[] __after_cfp = {
			FIPAConstants.Performatives.FAILURE,
			Integer.valueOf(FIPAConstants.CONVERSATION_END), PARTICIPANT, null,
			FIPAConstants.Performatives.CANCEL,
			Integer.valueOf(FIPAConstants.AGENT_ACTION_REQ), INITIATOR,
			__after_cancel, FIPAConstants.Performatives.REFUSE,
			Integer.valueOf(FIPAConstants.CONVERSATION_END), PARTICIPANT, null,
			FIPAConstants.Performatives.PROPOSE,
			Integer.valueOf(FIPAConstants.AGENT_ACTION_REQ), PARTICIPANT,
			__after_propose };

	/** The roots. */
	public static Object[] roots = { FIPAConstants.Performatives.CFP,
			Integer.valueOf(FIPAConstants.AGENT_ACTION_REQ), INITIATOR, __after_cfp };

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.misc.current_development.FIPAProtocol#getName()
	 */
	@Override
	public int getIndex() {
		return FIPAConstants.Protocols.FIPA_CONTRACT_NET;
	}

}
