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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

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
import msi.gaml.skills.Skill;
import msi.gaml.statements.IExecutable;
import msi.gaml.types.IType;
import ummisco.gama.network.common.IConnector;
import ummisco.gama.network.mqqt.MQTTConnectorSk;
import ummisco.gama.network.tcp.TCPConnector;
import ummisco.gama.network.udp.old.UDPConnector;
import ummisco.gama.serializer.factory.StreamConverter;

@vars({ @var(name = INetworkSkill.NET_AGENT_NAME, type = IType.STRING, doc = @doc("Net ID of the agent")),
@var(name = INetworkSkill.NET_AGENT_GROUPS, type = IType.LIST, doc = @doc("Net ID of the agent")),
@var(name = INetworkSkill.NET_AGENT_SERVER, type = IType.LIST, doc = @doc("Net ID of the agent"))})
@skill(name = INetworkSkill.NETWORK_SKILL, concept = { IConcept.NETWORK, IConcept.COMMUNICATION, IConcept.SKILL })
public class NetworkSkill  extends Skill {
	
	private HashMap<String,IConnector> serverList;
	private boolean registeredToSimulation = false;
	public NetworkSkill()
	{
		serverList = new HashMap<String,IConnector>();
		registeredToSimulation = false;
		StreamConverter.closeXStream();
	}
	
	@action(name = INetworkSkill.CONNECT_TOPIC, args = {
		@arg(name = INetworkSkill.PROTOCOL, type = IType.STRING, doc = @doc("protocol type (udp, tcp, mqqt)")),
		@arg(name = INetworkSkill.PORT, type = IType.INT, doc = @doc("port number")),
		@arg(name = INetworkSkill.WITHNAME, type = IType.STRING, optional = true, doc = @doc("server nameL")),
		@arg(name = INetworkSkill.SERVER_URL, type = IType.STRING, optional = false, doc = @doc("server URL")) }, doc = @doc(value = "", returns = "", examples = { @example("") }))
	public void connectToServer(final IScope scope) throws GamaRuntimeException {
		IAgent agt = scope.getAgentScope();
		String serverURL = (String) scope.getArg(INetworkSkill.SERVER_URL, IType.STRING);
		String dest = (String) scope.getArg(INetworkSkill.WITHNAME, IType.STRING);
		String protocol = (String) scope.getArg(INetworkSkill.PROTOCOL, IType.STRING);
		Integer port = (Integer) scope.getArg(INetworkSkill.PORT, IType.INT);
		scope.getAgentScope().setAttribute("ip", serverURL);
		scope.getAgentScope().setAttribute("port", port);
		IConnector connector =  serverList.get(serverURL);
		if(connector == null)
		{
			
			if(protocol != null && protocol.equals( INetworkSkill.UDP_SERVER)){
				System.out.println("create udp serveur");
				connector = new UDPConnector(scope,true);
			} 
			else if(protocol != null && protocol.equals( INetworkSkill.UDP_CLIENT)){
				System.out.println("create udp client");
				connector = new UDPConnector(scope,false);
			}
			else if(protocol != null && protocol.equals( INetworkSkill.TCP_SERVER)){
				System.out.println("create tcp serveur");
				connector = new TCPConnector(scope, true);
			}
			else if(protocol != null && protocol.equals( INetworkSkill.TCP_CLIENT)){
				System.out.println("create tcp client");
				connector = new TCPConnector(scope, false);
			}
			else //if(protocol.equals( INetworkSkill.MQTT))
			{
				System.out.println("create mqtt serveur");
				connector = new MQTTConnectorSk();
			}			
		    if(connector != null){
		    	serverList.put(serverURL,connector);
			}
		}

		scope.getAgentScope().setAttribute(INetworkSkill.NET_AGENT_NAME, dest);
		scope.getAgentScope().setAttribute(INetworkSkill.NET_AGENT_SERVER, serverURL);
		try {
			connector.connectToServer(agt, dest, serverURL, port);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(this.registeredToSimulation == false)
		{
			registeredToSimulation = true;
			scope.getSimulationScope().postDisposeAction(new IExecutable() {
				@Override
				public Object executeOn(IScope scope) throws GamaRuntimeException {
					closeAllConnection(scope);
					StreamConverter.closeXStream();
					return null;
				}
			});
		}
		//register connected agent to global groups;
		for(String grp:INetworkSkill.DEFAULT_GROUP)
			connector.registerToGroup(agt, grp);
	}

	@action(name = INetworkSkill.SEND_MESSAGE, args = {
		@arg(name = INetworkSkill.TO, type = IType.STRING, optional = true, doc = @doc("The network ID of the agent who receive the message")),
		@arg(name = INetworkSkill.CONTENT, type = IType.NONE, optional = true, doc = @doc("The content of the message")) }, doc = @doc(value = "Send a message to a destination.", returns = "the path followed by the agent.", examples = { @example(value = "do action: goto{\n arg target value: one_of (list (species (self))); \n arg speed value: speed * 2; \n arg on value: road_network;}") }))
	public void sendMessage(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		String dest = (String) scope.getArg(INetworkSkill.TO, IType.STRING);
		String serverName = (String)  agent.getAttribute(INetworkSkill.NET_AGENT_SERVER);
		String sender = (String) agent.getAttribute(INetworkSkill.NET_AGENT_NAME);
		Object messageContent = scope.getArg(INetworkSkill.CONTENT, IType.NONE);
		IConnector connector=this.serverList.get(serverName);
		connector.sendMessage(agent,dest, messageContent);
	}

	@action(name = INetworkSkill.FETCH_MESSAGE, args = {
			@arg(name = INetworkSkill.FROM, type = IType.STRING, optional = true, doc = @doc("The network ID of the agent who receive the message")) }, doc = @doc(value = "", returns = "", examples = {
					@example("") }))
	public GamaMap<String, Object> fetchMessage(final IScope scope) {
		final IAgent agent = getCurrentAgent(scope);
		String serverName = (String)  agent.getAttribute(INetworkSkill.NET_AGENT_SERVER);
		String src = (String) scope.getArg(INetworkSkill.FROM, IType.STRING);
		IConnector connector=this.serverList.get(serverName);
		GamaMap<String, Object>  res = connector.fetchMessageBox(agent);
		return res; 
	}
	
	@action(name = INetworkSkill.RESGISTER_TO_GROUP, args = {
	@arg(name = INetworkSkill.TO, type = IType.STRING, optional = true, doc = @doc("")) }, doc = @doc(value = "", returns = "", examples = {
	@example("") }))
	public void registerToGroup(final IScope scope)
	{
		IAgent agent = scope.getAgentScope();
		String serverName = (String)  agent.getAttribute(INetworkSkill.NET_AGENT_SERVER);
		String groupName = (String)scope.getArg(INetworkSkill.TO, IType.STRING);
		IConnector connector=this.serverList.get(serverName);
		connector.registerToGroup(agent, groupName);
	}
	
	@action(name = INetworkSkill.LEAVE_THE_GROUP, args = {
			@arg(name = INetworkSkill.WITHNAME, type = IType.STRING, optional = true, doc = @doc("name of the group agent want to leave")) }, doc = @doc(value = "leave a group of agent", returns = "", examples = {
			@example("") }))
	public void leaveTheGroup(final IScope scope) {
		IAgent agent = scope.getAgentScope();
		String serverName = (String)  agent.getAttribute(INetworkSkill.NET_AGENT_SERVER);
		String groupName = (String)scope.getArg(INetworkSkill.TO, IType.STRING);
		IConnector connector=this.serverList.get(serverName);
		connector.leaveTheGroup(agent, groupName);
	}
	
	private void closeAllConnection(IScope scope)
	{
		for(IConnector connection:serverList.values())
		{
			connection.close(scope);
		}
		serverList = new HashMap<String,IConnector>();
	}
	
	@action(name = INetworkSkill.HAS_MORE_MESSAGE_IN_BOX, args = {}, doc = @doc(value = "", returns = "", examples = { @example("") }))
	public boolean notEmptyMessageBox(final IScope scope) {
		final IAgent agent = getCurrentAgent(scope);
		String serverName = (String)  agent.getAttribute(INetworkSkill.NET_AGENT_SERVER);
		IConnector connector=this.serverList.get(serverName);
		
		return !connector.emptyMessageBox(agent);
	}

}
