/*********************************************************************************************
 *
 * 'NetworkSkill.java, in plugin ummisco.gama.network, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.network.skills;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import msi.gama.extensions.messaging.GamaMailbox;
import msi.gama.extensions.messaging.GamaMessage;
import msi.gama.extensions.messaging.MessagingSkill;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gaml.types.IType;
import ummisco.gama.network.common.ConnectorMessage;
import ummisco.gama.network.common.IConnector;
import ummisco.gama.network.mqqt.MQTTConnector;
import ummisco.gama.network.tcp.TCPConnector;
import ummisco.gama.network.udp.UDPConnector;

@vars({ @variable(name = INetworkSkill.NET_AGENT_NAME, type = IType.STRING, doc = @doc("Net ID of the agent")),
		@variable(name = INetworkSkill.NET_AGENT_GROUPS, type = IType.LIST, doc = @doc("Net ID of the agent")),
		@variable(name = INetworkSkill.NET_AGENT_SERVER, type = IType.LIST, doc = @doc("Net ID of the agent")) })
@skill(name = INetworkSkill.NETWORK_SKILL, concept = { IConcept.NETWORK, IConcept.COMMUNICATION, IConcept.SKILL })
public class NetworkSkill extends MessagingSkill {
	final static String REGISTERED_AGENTS = "registred_agents";
	final static String REGISTRED_SERVER = "registred_servers";

	
	
	@action(name = "execute", args = {
			@arg(name = "command", type = IType.STRING, doc = @doc("command to execute"))}, doc = @doc(value = "", returns = "", examples = {
					@example("") }))
	public String systemExec(final IScope scope) {
		final IAgent agent = scope.getAgent();
		final String commandToExecute = (String) scope.getArg("command", IType.STRING);
		
		String res = "";
		
		Process p;
		try {
			p = Runtime.getRuntime().exec(commandToExecute);
		
			BufferedReader stdError = new BufferedReader(new 
            InputStreamReader(p.getErrorStream()));
			return stdError.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
				
	}

	
	@SuppressWarnings("unchecked")
	@action(name = INetworkSkill.CONNECT_TOPIC, args = {
			@arg(name = INetworkSkill.PROTOCOL, type = IType.STRING, doc = @doc("protocol type (udp, tcp, mqqt)")),
			@arg(name = INetworkSkill.PORT, type = IType.INT, doc = @doc("port number")),
			@arg(name = INetworkSkill.WITHNAME, type = IType.STRING, optional = true, doc = @doc("server nameL")),
			@arg(name = INetworkSkill.LOGIN, type = IType.STRING, optional = true, doc = @doc("server nameL")),
			@arg(name = INetworkSkill.PASSWORD, type = IType.STRING, optional = true, doc = @doc("server nameL")),
			@arg(name = INetworkSkill.SERVER_URL, type = IType.STRING, optional = true, doc = @doc("server URL")) }, doc = @doc(value = "", returns = "", examples = {
					@example("") }))
	public void connectToServer(final IScope scope) throws GamaRuntimeException {
		if (!scope.getSimulation().getAttributes().keySet().contains(REGISTRED_SERVER))
			this.startSkill(scope);
		final IAgent agt = scope.getAgent();
		final String serverURL = (String) scope.getArg(INetworkSkill.SERVER_URL, IType.STRING);
		final String login = (String) scope.getArg(INetworkSkill.LOGIN, IType.STRING);
		final String password = (String) scope.getArg(INetworkSkill.PASSWORD, IType.STRING);
		final String networkName = (String) scope.getArg(INetworkSkill.WITHNAME, IType.STRING);
		final String protocol = (String) scope.getArg(INetworkSkill.PROTOCOL, IType.STRING);
		final Integer port = (Integer) scope.getArg(INetworkSkill.PORT, IType.INT);

		final Map<String, IConnector> myConnectors = this.getRegisteredServers(scope);
		IConnector connector = myConnectors.get(serverURL);
		if (connector == null) {

			if (protocol != null && protocol.equals(INetworkSkill.UDP_SERVER)) {
				connector = new UDPConnector(scope, true);
				connector.configure(IConnector.SERVER_URL, serverURL);
				connector.configure(IConnector.SERVER_PORT, "" + port);
			} else if (protocol != null && protocol.equals(INetworkSkill.UDP_CLIENT)) {
				System.out.println("create udp client");
				connector = new UDPConnector(scope, false);
				connector.configure(IConnector.SERVER_URL, serverURL);
				connector.configure(IConnector.SERVER_PORT, "" + port);
			} else if (protocol != null && protocol.equals(INetworkSkill.TCP_SERVER)) {
				System.out.println("create tcp serveur");
				connector = new TCPConnector(scope, true);
				connector.configure(IConnector.SERVER_URL, serverURL);
				connector.configure(IConnector.SERVER_PORT, "" + port);

			} else if (protocol != null && protocol.equals(INetworkSkill.TCP_CLIENT)) {
				System.out.println("create tcp client");
				connector = new TCPConnector(scope, false);
				connector.configure(IConnector.SERVER_URL, serverURL);
				connector.configure(IConnector.SERVER_PORT, "" + port);
			} else // if(protocol.equals( INetworkSkill.MQTT))
			{
				System.out.println("create mqtt serveur " + login + " " + password);
				connector = new MQTTConnector(scope);
				if (serverURL != null)
					connector.configure(IConnector.SERVER_URL, serverURL);
				if (login != null) {
					connector.configure(IConnector.LOGIN, login);
				}
				if (password != null)
					connector.configure(IConnector.PASSWORD, password);
			}
			myConnectors.put(serverURL, connector);

		}

		if (agt.getAttribute(INetworkSkill.NET_AGENT_NAME) == null)
			agt.setAttribute(INetworkSkill.NET_AGENT_NAME, networkName);

		List<String> serverList = (List<String>) agt.getAttribute(INetworkSkill.NET_AGENT_SERVER);
		if (serverList == null) {
			serverList = new ArrayList<String>();
			agt.setAttribute(INetworkSkill.NET_AGENT_SERVER, serverList);
		}

		try {
			connector.connect(agt);
			serverList.add(serverURL);

		} catch (final Exception e) {
			e.printStackTrace();
		}

		// register connected agent to global groups;
		for (final String grp : INetworkSkill.DEFAULT_GROUP)
			connector.joinAGroup(agt, grp);
	}

	@action(name = INetworkSkill.FETCH_MESSAGE)
	public GamaMessage fetchMessage(final IScope scope) {
		final IAgent agent = scope.getAgent();
		final GamaMailbox box = getMailbox(agent);
		final GamaMessage msg = box.get(0);
		box.remove(0);
		return msg;
	}

	@action(name = INetworkSkill.HAS_MORE_MESSAGE_IN_BOX)
	public boolean hasMoreMessage(final IScope scope) {
		final IAgent agent = scope.getAgent();
		final GamaMailbox box = getMailbox(agent);
		return !box.isEmpty();
	}

	/*
	 * @action(name = INetworkSkill.RESGISTER_TO_GROUP, args = {
	 * 
	 * @arg(name = INetworkSkill.TO, type = IType.STRING, optional = true, doc
	 * = @doc("")) }, doc = @doc(value = "", returns = "", examples = {
	 * 
	 * @example("") })) public void registerToGroup(final IScope scope) { IAgent
	 * agent = scope.getAgentScope(); String serverName = (String)
	 * agent.getAttribute(INetworkSkill.NET_AGENT_SERVER); String groupName =
	 * (String)scope.getArg(INetworkSkill.TO, IType.STRING); IConnector
	 * connector=getRegisteredServers(scope).get(serverName);
	 * connector.joinAGroup(agent, groupName); }
	 */

	@action(name = INetworkSkill.LEAVE_THE_GROUP, args = {
			@arg(name = INetworkSkill.WITHNAME, type = IType.STRING, optional = true, doc = @doc("name of the group agent want to leave")) }, doc = @doc(value = "leave a group of agent", returns = "", examples = {
					@example("") }))
	public void leaveTheGroup(final IScope scope) {
		final IAgent agent = scope.getAgent();
		final String serverName = (String) agent.getAttribute(INetworkSkill.NET_AGENT_SERVER);
		final String groupName = (String) scope.getArg(INetworkSkill.TO, IType.STRING);
		final IConnector connector = getRegisteredServers(scope).get(serverName);
		connector.leaveTheGroup(agent, groupName);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void effectiveSend(final IScope scope, final GamaMessage message, final Object receiver) {
		if (receiver instanceof IList) {
			for (final Object o : ((IList) receiver).iterable(scope)) {
				effectiveSend(scope, message.copy(scope), o);
			}
		}
		String destName = receiver.toString();
		if (receiver instanceof IAgent && getRegisteredAgents(scope).contains(receiver)) {
			final IAgent mReceiver = (IAgent) receiver;
			destName = (String) mReceiver.getAttribute(INetworkSkill.NET_AGENT_SERVER);
		}

		final IAgent agent = scope.getAgent();
		final List<String> serverNames = (List<String>) agent.getAttribute(INetworkSkill.NET_AGENT_SERVER);
		final Map<String, IConnector> connections = getRegisteredServers(scope);
		for (final String servName : serverNames)
			connections.get(servName).send(agent, destName, message);
	}

	private void fetchMessagesOfAgents(final IScope scope) {

		for (final IConnector connection : getRegisteredServers(scope).values()) {
			final Map<IAgent, LinkedList<ConnectorMessage>> messages = connection.fetchAllMessages();
			for (final IAgent agt : messages.keySet()) {
				final GamaMailbox mailbox = (GamaMailbox) agt.getAttribute(MAILBOX_ATTRIBUTE);
				if (!(connection instanceof MQTTConnector)) {
					mailbox.clear();
				}
				for (final ConnectorMessage msg : messages.get(agt)) {
					mailbox.addMessage(scope, msg.getContents(scope));
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected List<IAgent> getRegisteredAgents(final IScope scope) {
		return (List<IAgent>) scope.getSimulation().getAttribute(REGISTERED_AGENTS);
	}

	private void registeredAgent(final IScope scope, final IAgent agt) {
		getRegisteredAgents(scope).add(agt);
	}

	@SuppressWarnings("unchecked")
	protected Map<String, IConnector> getRegisteredServers(final IScope scope) {
		return (Map<String, IConnector>) scope.getSimulation().getAttribute(REGISTRED_SERVER);
	}

	private void initialize(final IScope scope) {
		scope.getSimulation().setAttribute(REGISTRED_SERVER, new HashMap<String, IConnector>());
		scope.getSimulation().setAttribute(REGISTERED_AGENTS, new ArrayList<IAgent>());
	}

	private void startSkill(final IScope scope) {
		initialize(scope);
		registerSimulationEvent(scope);
	}

	private void registerSimulationEvent(final IScope scope) {
		scope.getSimulation().postEndAction(scope1 -> {
			fetchMessagesOfAgents(scope1);
			return null;
		});

		scope.getSimulation().postDisposeAction(scope1 -> {
			closeAllConnection(scope1);
			return null;
		});
	}

	private void closeAllConnection(final IScope scope) {
		for (final IConnector connection : this.getRegisteredServers(scope).values()) {
			connection.close(scope);
		}
		this.initialize(scope);
	}

}
