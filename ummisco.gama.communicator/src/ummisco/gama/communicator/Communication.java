package ummisco.gama.communicator;

import java.io.ObjectOutputStream;
import java.util.LinkedList;

import javax.jms.JMSException;
import javax.jms.MapMessage;

import msi.gama.metamodel.agent.IAgent;
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
import msi.gaml.types.IType;

@vars({@var(name = ICommunicatorSkill.NET_AGENT_NAME, type= IType.STRING, doc = @doc("Net ID of the agent"))
})
@skill(name=ICommunicatorSkill.NETWORK_SKILL)
public class Communication  extends CommunicatingSkill 
{

	
	@action(name = ICommunicatorSkill.CONNECT_TOPIC, args = {
			@arg(name = ICommunicatorSkill.BOX_NAME, type = IType.STRING, optional = false, doc = @doc("server URL")),
			@arg(name = ICommunicatorSkill.SERVER_URL, type = IType.STRING, optional = false, doc = @doc("server URL")),
			@arg(name = "withName", type = IType.STRING, optional = false, doc = @doc("agent Name"))},
			doc = @doc(value = "moves the agent towards the target passed in the arguments.", returns = "the path followed by the agent.", examples = { @example("do action: goto{\n arg target value: one_of (list (species (self))); \n arg speed value: speed * 2; \n arg on value: road_network;}") }))
	public void connectToServer(IScope scope)
	{
		this.connect(scope);
	}
	
	
	@action(name = ICommunicatorSkill.SEND_MESSAGE, args = {
			@arg(name = ICommunicatorSkill.DEST, type = IType.STRING, optional = true, doc = @doc("The network ID of the agent who receive the message")),
			@arg(name = ICommunicatorSkill.CONTENT, type = IType.NONE, optional = true, doc = @doc("The content of the message"))},
			doc = @doc(value = "Send a message to a destination.", returns = "the path followed by the agent.", examples = { @example(value="do action: goto{\n arg target value: one_of (list (species (self))); \n arg speed value: speed * 2; \n arg on value: road_network;}") }))
	public void sendMessage(final IScope scope) throws GamaRuntimeException 
	{
	   final IAgent agent = getCurrentAgent(scope);
	   String dest = (String) scope.getArg(ICommunicatorSkill.DEST, IType.STRING);
	   String sender =(String) agent.getAttribute(ICommunicatorSkill.NET_AGENT_NAME);
	   Object messageContent=	scope.getArg(ICommunicatorSkill.CONTENT, IType.NONE);
	   try {
			this.send(sender, dest, messageContent);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 	 
	     
	}

	@action(name = "fetchMessage", args = {},
			doc = @doc(value = "moves the agent towards the target passed in the arguments.", returns = "the path followed by the agent.", examples = { @example("do action: goto{\n arg target value: one_of (list (species (self))); \n arg speed value: speed * 2; \n arg on value: road_network;}") }))
	public GamaMap<String, Object> fetchMessage(IScope scope)
	{
	 	final IAgent agent = getCurrentAgent(scope);
	   	String tmpName =(String) agent.getAttribute(ICommunicatorSkill.NET_AGENT_NAME);
	   return fetchMyMailBox(tmpName);
	   	
	}
	
	@action(name = "emptyMessageBox", args = {},
			doc = @doc(value = "moves the agent towards the target passed in the arguments.", returns = "the path followed by the agent.", examples = { @example("do action: goto{\n arg target value: one_of (list (species (self))); \n arg speed value: speed * 2; \n arg on value: road_network;}") }))
	public boolean emptyMessage(IScope scope)
	{
		final IAgent agent = getCurrentAgent(scope);
	   	String tmpName =(String) agent.getAttribute(ICommunicatorSkill.NET_AGENT_NAME);
	   	return isEmptyMailBox(tmpName);
	}
	

}
