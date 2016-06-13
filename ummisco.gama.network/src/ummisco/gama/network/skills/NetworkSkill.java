/*********************************************************************************************
 * 
 * 
 * 'Communication.java', in plugin 'ummisco.gama.communicator', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package ummisco.gama.network.skills;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import msi.gama.extensions.messaging.GamaMailbox;
import msi.gama.extensions.messaging.GamaMessage;
import msi.gama.extensions.messaging.MessagingSkill;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMap;
import msi.gama.util.IList;
import msi.gaml.skills.Skill;
import msi.gaml.statements.IExecutable;
import msi.gaml.types.IType;
import ummisco.gama.network.common.CompositeGamaMessage;
import ummisco.gama.network.common.ConnectorMessage;
import ummisco.gama.network.common.IConnector;
import ummisco.gama.network.mqqt.MQTTConnector;
import ummisco.gama.network.mqqt.MQTTConnectorSk;
import ummisco.gama.network.tcp.TCPConnector;
import ummisco.gama.network.udp.UDPConnector;
import ummisco.gama.serializer.factory.StreamConverter;

@vars({ @var(name = INetworkSkill.NET_AGENT_NAME, type = IType.STRING, doc = @doc("Net ID of the agent")),
@var(name = INetworkSkill.NET_AGENT_GROUPS, type = IType.LIST, doc = @doc("Net ID of the agent")),
@var(name = INetworkSkill.NET_AGENT_SERVER, type = IType.LIST, doc = @doc("Net ID of the agent"))})
@skill(name = INetworkSkill.NETWORK_SKILL, concept = { IConcept.NETWORK, IConcept.COMMUNICATION, IConcept.SKILL })
public class NetworkSkill  extends MessagingSkill {
	final static String REGISTERED_AGENTS = "registred_agents";
	final static String REGISTRED_SERVER= "registred_servers";
	
	
	@action(name = INetworkSkill.CONNECT_TOPIC, args = {
		@arg(name = INetworkSkill.PROTOCOL, type = IType.STRING, doc = @doc("protocol type (udp, tcp, mqqt)")),
		@arg(name = INetworkSkill.PORT, type = IType.INT, doc = @doc("port number")),
		@arg(name = INetworkSkill.WITHNAME, type = IType.STRING, optional = true, doc = @doc("server nameL")),
		@arg(name = INetworkSkill.SERVER_URL, type = IType.STRING, optional = false, doc = @doc("server URL")) }, doc = @doc(value = "", returns = "", examples = { @example("") }))
	public void connectToServer(final IScope scope) throws GamaRuntimeException {
		if(!scope.getSimulationScope().getAttributes().keySet().contains(REGISTRED_SERVER))
			this.startSkill(scope);
		IAgent agt = scope.getAgentScope();
		
		String serverURL = (String) scope.getArg(INetworkSkill.SERVER_URL, IType.STRING);
		String networkName = (String) scope.getArg(INetworkSkill.WITHNAME, IType.STRING);
		String protocol = (String) scope.getArg(INetworkSkill.PROTOCOL, IType.STRING);
		Integer port = (Integer) scope.getArg(INetworkSkill.PORT, IType.INT);
		
		Map<String,IConnector> myConnectors = this.getRegisteredServers(scope);
		IConnector connector =  myConnectors.get(serverURL);
		if(connector == null)
		{
			
			if(protocol != null && protocol.equals( INetworkSkill.UDP_SERVER)){
				System.out.println("create udp serveur");
				connector = new UDPConnector(scope,true);
				connector.configure(IConnector.SERVER_URL,serverURL);
				connector.configure(IConnector.SERVER_PORT,""+port);
			} 
			else if(protocol != null && protocol.equals( INetworkSkill.UDP_CLIENT)){
				System.out.println("create udp client");
				connector = new UDPConnector(scope,false);
				connector.configure(IConnector.SERVER_URL,serverURL);
				connector.configure(IConnector.SERVER_PORT,""+port);
			}
			else if(protocol != null && protocol.equals( INetworkSkill.TCP_SERVER)){
				System.out.println("create tcp serveur");
				connector = new TCPConnector(scope, true);
				connector.configure(IConnector.SERVER_URL,serverURL);
				connector.configure(IConnector.SERVER_PORT,""+port);

			}
			else if(protocol != null && protocol.equals( INetworkSkill.TCP_CLIENT)){
				System.out.println("create tcp client");
				connector = new TCPConnector(scope, false);
				connector.configure(IConnector.SERVER_URL,serverURL);
				connector.configure(IConnector.SERVER_PORT,""+port);
			}
			else //if(protocol.equals( INetworkSkill.MQTT))
			{
				System.out.println("create mqtt serveur");
				connector = new MQTTConnector(scope);
				connector.configure(IConnector.LOGIN,"admin");
				connector.configure(IConnector.PASSWORD,"password");
			}			
		    if(connector != null){
		    	myConnectors.put(serverURL,connector);
		    	
			}
		}

		if(agt.getAttribute(INetworkSkill.NET_AGENT_NAME)==null)
			agt.setAttribute(INetworkSkill.NET_AGENT_NAME, networkName);
		
		List<String> serverList = (List<String>) agt.getAttribute(INetworkSkill.NET_AGENT_SERVER); 
		if(serverList==null)
		{
			serverList =new ArrayList<String>();
			agt.setAttribute(INetworkSkill.NET_AGENT_SERVER,serverList );
		}
			
		try {
			connector.connect(agt);
			serverList.add(serverURL);

			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//register connected agent to global groups;
		for(String grp:INetworkSkill.DEFAULT_GROUP)
			connector.joinAGroup(agt, grp);
	}

	@action(name = INetworkSkill.FETCH_MESSAGE)
	public GamaMessage fetchMessage(final IScope scope)
	{
		IAgent agent = scope.getAgentScope();
		GamaMailbox box = getMailbox(agent);
		GamaMessage msg = box.get(0);
		box.remove(0);
		return msg;
	}
	
	@action(name = INetworkSkill.HAS_MORE_MESSAGE_IN_BOX)
	public boolean hasMoreMessage(final IScope scope)
	{
		IAgent agent = scope.getAgentScope();
		GamaMailbox box = getMailbox(agent);
		return !box.isEmpty();
	}	
	
/*	
	@action(name = INetworkSkill.RESGISTER_TO_GROUP, args = {
	@arg(name = INetworkSkill.TO, type = IType.STRING, optional = true, doc = @doc("")) }, doc = @doc(value = "", returns = "", examples = {
	@example("") }))
	public void registerToGroup(final IScope scope)
	{
		IAgent agent = scope.getAgentScope();
		String serverName = (String)  agent.getAttribute(INetworkSkill.NET_AGENT_SERVER);
		String groupName = (String)scope.getArg(INetworkSkill.TO, IType.STRING);
		IConnector connector=getRegisteredServers(scope).get(serverName);
		connector.joinAGroup(agent, groupName);
	}
	*/
	
	
	@action(name = INetworkSkill.LEAVE_THE_GROUP, args = {
			@arg(name = INetworkSkill.WITHNAME, type = IType.STRING, optional = true, doc = @doc("name of the group agent want to leave")) }, doc = @doc(value = "leave a group of agent", returns = "", examples = {
			@example("") }))
	public void leaveTheGroup(final IScope scope) {
		IAgent agent = scope.getAgentScope();
		String serverName = (String)  agent.getAttribute(INetworkSkill.NET_AGENT_SERVER);
		String groupName = (String)scope.getArg(INetworkSkill.TO, IType.STRING);
		IConnector connector=getRegisteredServers(scope).get(serverName);
		connector.leaveTheGroup(agent, groupName);
	}

	
	protected void effectiveSend(final IScope scope, final GamaMessage message, final Object receiver) {
		if (receiver instanceof IList) {
			for (final Object o : ((IList) receiver).iterable(scope)) {
				effectiveSend(scope, message.copy(scope), o);
			}
		}
		String destName = receiver.toString();
		if(receiver instanceof IAgent && getRegisteredAgents(scope).contains((IAgent)receiver))
		{
			IAgent mReceiver = (IAgent)receiver;
			destName=(String) mReceiver.getAttribute(INetworkSkill.NET_AGENT_SERVER);
		}
		
		IAgent agent = scope.getAgentScope();
		List<String> serverNames = (List<String>)  agent.getAttribute(INetworkSkill.NET_AGENT_SERVER);
		Map<String,IConnector>  connections = getRegisteredServers(scope);
		for(String servName:serverNames)
			connections.get(servName).send(agent, destName, message);
	}

	
	private void fetchMessagesOfAgents(IScope scope)
	{
		
		
		for(IConnector connection:getRegisteredServers(scope).values())
		{
			Map<IAgent,LinkedList<ConnectorMessage>> messages = connection.fetchAllMessages();
			for(IAgent agt:messages.keySet())
			{
				final GamaMailbox mailbox = (GamaMailbox) agt.getAttribute(MAILBOX_ATTRIBUTE);
				if(!(connection instanceof MQTTConnector)){ mailbox.clear();}
				for(ConnectorMessage msg:messages.get(agt))
				{
					mailbox.addMessage(scope, msg.getContents(scope));
				}
			}
		}
	}
	
	
	@SuppressWarnings("unchecked")
	private List<IAgent> getRegisteredAgents(IScope scope){
		return (List<IAgent>)scope.getSimulationScope().getAttribute(REGISTERED_AGENTS);
	}
	
	private void registeredAgent(IScope scope, IAgent agt){
		getRegisteredAgents(scope).add(agt);
	}
	
	@SuppressWarnings("unchecked")
	private Map<String,IConnector> getRegisteredServers(IScope scope)
	{
		return (Map<String,IConnector>)scope.getSimulationScope().getAttribute(REGISTRED_SERVER);
	}
	
	private void initialize(IScope scope){
		scope.getSimulationScope().setAttribute(REGISTRED_SERVER,  new HashMap<String,IConnector>());
		scope.getSimulationScope().setAttribute(REGISTERED_AGENTS, new ArrayList<IAgent>());
	}
	
	
	
	private void startSkill(IScope scope)
	{
		initialize(scope);
		registerSimulationEvent(scope);
	}
	private void registerSimulationEvent(IScope scope)
	{
		scope.getSimulationScope().postEndAction(new IExecutable() {
			@Override
			public Object executeOn(IScope scope) throws GamaRuntimeException {
				fetchMessagesOfAgents(scope);
				return null;
			}
		});
	
		scope.getSimulationScope().postDisposeAction(new IExecutable() {
			@Override
			public Object executeOn(IScope scope) throws GamaRuntimeException {
				closeAllConnection(scope);
				return null;
			}
		});
	}
	private void closeAllConnection(IScope scope)
	{
		for(IConnector connection:this.getRegisteredServers(scope).values())
		{
			connection.close(scope);
		}
		this.initialize(scope);
	}
	
	
}
