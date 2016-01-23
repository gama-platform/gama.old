/*********************************************************************************************
 *
 *
 * 'ConnectionManager.java', in plugin 'ummisco.gama.communicator', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.communicator.common;

import java.util.*;
import javax.jms.*;
import javax.naming.NamingException;
import org.apache.activemq.ActiveMQConnectionFactory;
import com.thoughtworks.xstream.XStream;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.skills.Skill;
import msi.gaml.types.*;
import ummisco.gama.serializer.gamaType.converters.GamaAgentConverter;
import ummisco.gama.serializer.gamaType.converters.GamaScopeConverter;

public class ConnectionManager extends Skill implements MessageListener {

	protected static String DEFAULT_PORT_NUM = "61616";
	protected String serverURL;
	protected String topicName;
	private MessageProducer producer = null;
	private Session session = null;
	private final HashMap<String, LinkedList<Map<String, Object>>> messages;
	private final XStream xstream;

	public ConnectionManager() {
		this.messages = new HashMap<String, LinkedList<Map<String, Object>>>();
		this.xstream = new XStream();
		// TODO : @Nico : be careful : I needed to add a scope in the GamaAgentConverter in order to reconstruct them...
		this.xstream.registerConverter(new GamaAgentConverter(null));
		this.xstream.registerConverter(new GamaScopeConverter());
	}

	protected Session getCurrentSession() {
		return this.session;
	}

	public void connect(final IScope scope) {
		final IAgent agent = getCurrentAgent(scope);
		readCallParameters(scope);
		String name = (String) scope.getArg(ICommunicatorSkill.WITHNAME, IType.STRING);

		if ( messages.containsKey(name) ) {
			name = name + messages.keySet().size(); // agent identificator is added to the agent name is the agent
													// already exist
		}
		agent.setAttribute(ICommunicatorSkill.NET_AGENT_NAME, name);
		messages.put(name, new LinkedList<Map<String, Object>>());

		if ( session == null ) {
			this.serverURL =
				formatServerURL((String) scope.getArg(ICommunicatorSkill.SERVER_URL, IType.STRING), DEFAULT_PORT_NUM);
			this.topicName = (String) scope.getArg(ICommunicatorSkill.BOX_NAME, IType.STRING);

			try {
				this.listenTopic(scope);
				this.connectToTopic(scope);

			} catch (NamingException e) {
				throw GamaRuntimeException.create(e, scope);

			} catch (JMSException e) {
				throw GamaRuntimeException.create(e, scope);
			}

		}
	}

	private static String formatServerURL(final String url, final String portN) {
		String finalURL = "tcp://" + url;
		if ( !url.contains(":") ) {
			finalURL = finalURL + ':' + portN;
		}
		return finalURL;
	}

	private void readCallParameters(final IScope scope) {
		if ( this.serverURL == null ) {
			this.serverURL =
				formatServerURL((String) scope.getArg(ICommunicatorSkill.SERVER_URL, IType.STRING), DEFAULT_PORT_NUM);
		}
		if ( this.topicName == null ) {
			this.topicName = (String) scope.getArg(ICommunicatorSkill.BOX_NAME, IType.STRING);
		}
	}

	private void listenTopic(final IScope scope) throws NamingException, JMSException {
		readCallParameters(scope);
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(this.serverURL);
		Connection connection = connectionFactory.createConnection();
		connection.start();

		TopicSession subSession = (TopicSession) connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Topic top = subSession.createTopic(this.topicName);

		MessageConsumer subscriber = subSession.createSubscriber(top);
		subscriber.setMessageListener(this);
		System.out.println("LISTENING MESSAGE");
	}

	private void connectToTopic(final IScope scope) throws NamingException, JMSException {
		readCallParameters(scope);
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(this.serverURL);
		Connection connection = connectionFactory.createConnection();
		connection.start();

		this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Destination destination = session.createTopic(this.topicName);
		this.producer = this.session.createProducer(destination);
		System.out.println("AGENT CAN SEND MESSAGE TO OTHER");
	}

	@Override
	public void onMessage(final Message msg) {
		try {
			String to = msg.getStringProperty(ICommunicatorSkill.DEST);
			String from = msg.getStringProperty(ICommunicatorSkill.SENDER);
			if ( !(msg instanceof MapMessage) || to == null ||
				!(this.messages.containsKey(to) || to.equals("all")) ) { return; }
			MapMessage mapMsg = (MapMessage) msg;
			if ( !to.equals("all") ) {
				pushMessage(to, to, from, mapMsg);
			} else {
				broadast(from, mapMsg);
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	private void broadast(final String from, final MapMessage mapMsg) throws JMSException {
		for ( String agtName : this.messages.keySet() ) {
			pushMessage(agtName, ICommunicatorSkill.BROADCAST, from, mapMsg);
		}
	}

	private void pushMessage(final String netAgentName, final String to, final String from, final MapMessage mapMsg)
		throws JMSException {
		LinkedList<Map<String, Object>> myMsgBox = this.messages.get(netAgentName);
		Object res = xstream.fromXML((String) mapMsg.getObject(ICommunicatorSkill.CONTENT));

		Map<String, Object> agentMsg = new TOrderedHashMap<String, Object>();
		agentMsg.put(ICommunicatorSkill.SENDER, from);
		agentMsg.put(ICommunicatorSkill.DEST, to);
		agentMsg.put(ICommunicatorSkill.CONTENT, res);
		System.out.println("Sent message : " + res);
		this.messages.get(netAgentName).addLast(agentMsg);
	}

	protected void send(final String sender, final String dest, final Object content) throws JMSException {
		MapMessage msg = session.createMapMessage();
		msg.setString(ICommunicatorSkill.CONTENT, xstream.toXML(content));
		msg.setStringProperty(ICommunicatorSkill.DEST, dest);
		msg.setStringProperty(ICommunicatorSkill.SENDER, sender);
		producer.send(msg);
	}

	protected GamaMap<String, Object> fetchMyMailBox(final IScope scope, final String myName) {
		if ( !this.messages
			.containsKey(myName) ) { throw GamaRuntimeException.error("Agent has not any mailbox", scope); }

		LinkedList<Map<String, Object>> mList = this.messages.get(myName);
		if ( mList.isEmpty() ) {
			return null;
		} else {
			return GamaMapFactory.create(scope, Types.STRING, Types.NO_TYPE, this.messages.get(myName).pollFirst());
		}
	}

	protected boolean isEmptyMailBox(final IScope scope, final String myName) {
		if ( !this.messages
			.containsKey(myName) ) { throw GamaRuntimeException.error("Agent has not any mailbox", scope); }

		return this.messages.get(myName).size() == 0;
	}

}
