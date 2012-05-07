/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
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

import java.util.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;

/**
 * The Class MessageBroker.
 * 
 * @author drogoul
 */
public class MessageBroker {

	/** The messages to deliver. */

	private final Map<IAgent, List<Message>> messagesToDeliver = new HashMap();

	/** The instance. */
	private static MessageBroker instance;

	/**
	 * @throws GamaRuntimeException Deliver message.
	 * 
	 * @param m the m
	 * 
	 * @throws GamlException the gaml exception
	 */
	public List<Message> deliverMessagesFor(final IAgent a) throws GamaRuntimeException {
		final List<Message> result = messagesToDeliver.get(a);
		if ( result == null ) { return Collections.EMPTY_LIST; }
		for ( Message m : messagesToDeliver.get(a) ) {
			Message message = m;
			Conversation conv = m.getConversation();
			try {
				conv.addMessage(m);
			} catch (GamaRuntimeException e) {
				result.remove(m);
				message = failureMessageInReplyTo(m);
				conv.end();
			} finally {
				result.add(message);
			}

			// Pas sur que ça produise le même résultat qu'avant...
		}
		messagesToDeliver.remove(a);
		return result;
	}

	/**
	 * @throws GamaRuntimeException Deliver failure in reply to.
	 * 
	 * @param m the m
	 * 
	 * @throws GamlException the gaml exception
	 */
	protected Message failureMessageInReplyTo(final Message m) throws GamaRuntimeException {
		if ( m.getPerformative() == FIPAConstants.Performatives.FAILURE ) { return null; }

		final Message f = new Message();
		f.setSender(null);
		final GamaList<IAgent> receivers = new GamaList();
		receivers.add(m.getSender());
		f.setReceivers(receivers);
		f.setPerformative(FIPAConstants.Performatives.FAILURE);
		f.setConversation(m.getConversation());
		f.setContent(m.getContent());
		return f;
	}

	/**
	 * Schedule for delivery.
	 * 
	 * @param m the m
	 */
	public void scheduleForDelivery(final Message m) {
		for ( IAgent a : m.getReceivers() ) {
			scheduleForDelivery(m, a);
		}
	}

	private void scheduleForDelivery(final Message m, final IAgent agent) {
		List<Message> messages = messagesToDeliver.get(agent);
		if ( messages == null ) {
			messages = new ArrayList();
			messagesToDeliver.put(agent, messages);
		}
		messages.add(m);
	}

	/**
	 * @throws GamaRuntimeException Schedule for delivery.
	 * 
	 * @param m the m
	 * @param protocol the protocol
	 * 
	 * @throws UnknownProtocolException the unknown protocol exception
	 * @throws ProtocolErrorException the protocol error exception
	 * @throws GamlException the gaml exception
	 */
	public void scheduleForDelivery(final Message m, final Integer protocol) {
		Conversation conv;
		conv = new Conversation(protocol, m);
		m.setConversation(conv);
		scheduleForDelivery(m);
	}

	/**
	 * Gets the single instance of MessageBroker.
	 * 
	 * @param sim the sim
	 * 
	 * @return single instance of MessageBroker
	 */
	public static MessageBroker getInstance() {
		if ( instance == null ) {
			instance = new MessageBroker();
		}
		return instance;
		// TODO Il faudrait pouvoir en g√©rer plusieurs (par simulation)
	}

	public void dispose() {
		messagesToDeliver.clear();
	}
}
