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

/**
 * Implementation of the FIPA Request interaction protocol. Reference :
 * http://www.fipa.org/specs/fipa00026/SC00026H.html
 */
public class FIPARequest extends FIPAProtocol {

	/** Definition of protocol model. */
	private static Object[] __after_cancel = {
			FIPAConstants.Performatives.FAILURE,
			Integer.valueOf(FIPAConstants.CONVERSATION_END), PARTICIPANT, null,
			FIPAConstants.Performatives.INFORM,
			Integer.valueOf(FIPAConstants.CONVERSATION_END), PARTICIPANT, null };

	/** The __after_agree. */
	private static Object[] __after_agree = {
			FIPAConstants.Performatives.FAILURE,
			Integer.valueOf(FIPAConstants.CONVERSATION_END), PARTICIPANT, null,
			FIPAConstants.Performatives.INFORM,
			Integer.valueOf(FIPAConstants.CONVERSATION_END), PARTICIPANT, null };

	/** The __after_request. */
	private static Object[] __after_request = {
			FIPAConstants.Performatives.NOT_UNDERSTOOD,
			Integer.valueOf(FIPAConstants.CONVERSATION_END), PARTICIPANT, null,
			FIPAConstants.Performatives.CANCEL,
			Integer.valueOf(FIPAConstants.AGENT_ACTION_REQ), INITIATOR,
			__after_cancel, FIPAConstants.Performatives.REFUSE,
			Integer.valueOf(FIPAConstants.CONVERSATION_END), PARTICIPANT, null,
			FIPAConstants.Performatives.AGREE,
			Integer.valueOf(FIPAConstants.AGENT_ACTION_REQ), PARTICIPANT,
			__after_agree };

	/** The roots. */
	public static Object[] roots = { FIPAConstants.Performatives.REQUEST,
			Integer.valueOf(FIPAConstants.AGENT_ACTION_REQ), INITIATOR,
			__after_request };

	/* (non-Javadoc)
	 * @see msi.gama.extensions.fipa.FIPAProtocol#getIndex()
	 */
	@Override
	public int getIndex() {
		return FIPAConstants.Protocols.FIPA_REQUEST;
	}

	@Override
	public String getName() {
		return FIPAConstants.Protocols.FIPA_REQUEST_STR;
	}
}
