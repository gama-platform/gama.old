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

import java.util.List;
import msi.gama.kernel.simulation.ISimulation;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gaml.compilation.GamlException;

/**
 * The Class MessageBroker.
 * 
 * @author drogoul
 */
public class MessageBroker {

	/** The messages to deliver. */
	private final List<Message> messagesToDeliver = new GamaList();

	/** The instance. */
	private static MessageBroker instance;

	/**
	 * Instantiates a new message broker.
	 * 
	 * @param sim the sim
	 */
	private MessageBroker(final ISimulation sim) {
		sim.getScheduler().insertBeginAction(this, "deliverMessages");
	}

	/**
	 * @throws GamaRuntimeException Deliver messages.
	 * 
	 * @throws GamlException the gaml exception
	 */
	public void deliverMessages() throws GamlException, GamaRuntimeException {
		for ( final Message m : messagesToDeliver ) {
			deliverMessage(m);
		}
		messagesToDeliver.clear();
	}

	/**
	 * @throws GamaRuntimeException Deliver message.
	 * 
	 * @param m the m
	 * 
	 * @throws GamlException the gaml exception
	 */
	public void deliverMessage(final Message m) throws GamlException, GamaRuntimeException {
		final Conversation conv = (Conversation) m.getConversation();
		try {
			conv.addMessage(m);
		} catch (final Exception e) {
			e.printStackTrace();
			deliverFailureInReplyTo(m);
			final Conversation c = (Conversation) m.getConversation();
			c.end();
		}
		final List<IAgent> receivers = m.getReceivers();
		//
		// for ( final IAgent a : receivers ) {
		// if ( a == null || a.isDead() ) {
		// continue;
		// }
		// final CommunicatingSkill c =
		// (CommunicatingSkill) ((IGamlAgent) a).getSpecies().getSharedSkill(
		// CommunicatingSkill.class);
		// if ( c != null ) {
		// c.receiveMessage(m.getSimulationScope(), m);
		// }
		// }
	}

	/**
	 * @throws GamaRuntimeException Deliver failure in reply to.
	 * 
	 * @param m the m
	 * 
	 * @throws GamlException the gaml exception
	 */
	protected void deliverFailureInReplyTo(final Message m) throws GamlException,
		GamaRuntimeException {
		if ( m.getPerformative() == FIPAConstants.Performatives.FAILURE ) { return; }

		final Message f =
			new Message(m.getSimulation(), m.getSimulation().getWorld()
				.getPopulationFor(m.getSpecies()));
		f.setSender(null);
		final GamaList<IAgent> receivers = new GamaList();
		receivers.add(m.getSender());
		f.setReceivers(receivers);
		f.setPerformative(FIPAConstants.Performatives.FAILURE);
		f.setConversation(m.getConversation());
		f.setContent(m.getContent());
		deliverMessage(f);
	}

	/**
	 * Schedule for delivery.
	 * 
	 * @param m the m
	 */
	public void scheduleForDelivery(final Message m) {
		messagesToDeliver.add(m);
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
	public void scheduleForDelivery(final Message m, final Integer protocol)
		throws GamaRuntimeException {
		Conversation conv;
		try {
			conv = new Conversation(m.getSimulation(), protocol, m);
		} catch (GamlException e) {
			e.printStackTrace();
			return;
		}
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
	public static MessageBroker getInstance(final ISimulation sim) {
		if ( instance == null ) {
			instance = new MessageBroker(sim);
		}
		return instance;
		// TODO Il faudrait pouvoir en gérer plusieurs (par simulation)
	}

	public void dispose() {
		messagesToDeliver.clear();
	}
}
