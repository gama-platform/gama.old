package ummisco.gama.communicator;

import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicSession;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.activemq.ActiveMQConnectionFactory;

import com.thoughtworks.xstream.XStream;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMap;
import msi.gaml.skills.Skill;
import msi.gaml.types.IType;


public class CommunicatingSkill extends Skill implements MessageListener {

	protected static String DEFAULT_PORT_NUM="61616";
	protected String serverURL;
	protected String topicName;
	private MessageProducer producer = null;
	private Session session=null;
	private HashMap<String,LinkedList<GamaMap<String,Object>>> messages;
	private XStream xstream ;

	public CommunicatingSkill()
	{
		this.messages= new HashMap<String, LinkedList<GamaMap<String,Object>>>();
  	  	this.xstream = new XStream();
  	  	this.xstream.registerConverter(new GamaAgentConverter());
  	  	this.xstream.registerConverter(new GamaScopeConverter()); 
	}
	
	protected Session getCurrentSession()
	{
		return this.session;
	}
	
	
	
	public void connect(final IScope scope)
	{
		final IAgent agent = getCurrentAgent(scope);
		readCallParameters(scope);
		String name = (String) scope.getArg(ICommunicatorSkill.WITHNAME, IType.STRING);
		
		if(messages.containsKey(name))
		{
			name = name + messages.keySet().size(); // agent identificator is added to the agent name is the agent already exist
		}
		agent.setAttribute(ICommunicatorSkill.NET_AGENT_NAME, name);
		messages.put(name, new LinkedList<GamaMap<String,Object>>());
		
		
		if(session==null)
		{
			this.serverURL = formatServerURL((String) scope.getArg(ICommunicatorSkill.SERVER_URL, IType.STRING),DEFAULT_PORT_NUM);
			this.topicName = (String) scope.getArg(ICommunicatorSkill.BOX_NAME, IType.STRING);

			try {
				this.listenTopic(scope);
				this.connectToTopic(scope);

			} catch (NamingException e) {
				throw new GamaRuntimeException(e);

			} catch (JMSException e) {
				throw new GamaRuntimeException(e);
			} 

		}
	}
	
	
	private static String formatServerURL(final String url, String portN)
	{
		String finalURL = "tcp://"+url;
		if(!url.contains(":"))
			finalURL = finalURL +':'+ portN;
		return finalURL;
	}
	
	private void readCallParameters(IScope scope)
	{
		if( this.serverURL == null)
			this.serverURL = formatServerURL((String) scope.getArg(ICommunicatorSkill.SERVER_URL, IType.STRING),DEFAULT_PORT_NUM);
		if(this.topicName == null)
			this.topicName = (String) scope.getArg(ICommunicatorSkill.BOX_NAME, IType.STRING);
	}
	
	
	private void listenTopic(IScope scope) throws NamingException, JMSException
	{
		readCallParameters(scope);
		ConnectionFactory connectionFactory  = new ActiveMQConnectionFactory(this.serverURL);
		Connection connection = connectionFactory.createConnection();
		connection.start();

		TopicSession subSession= (TopicSession)connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Topic top = subSession.createTopic(this.topicName);

		MessageConsumer subscriber =  subSession.createSubscriber(top);
		subscriber.setMessageListener(this);
		System.out.println("LISTENING MESSAGE");
	}

	private void connectToTopic(IScope scope) throws NamingException, JMSException
	{
	   readCallParameters(scope);
	   ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(this.serverURL);
       Connection connection = connectionFactory.createConnection();
       connection.start();
       
       this.session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
       Destination destination = session.createTopic(this.topicName);
       this.producer = this.session.createProducer(destination);
       System.out.println("AGENT CAN SEND MESSAGE TO OTHER");
   }
	
	
	@Override
	public void onMessage(Message msg) {
		try {
				String to = msg.getStringProperty(ICommunicatorSkill.DEST);
				String from = msg.getStringProperty(ICommunicatorSkill.SENDER);
				if(!(msg instanceof MapMessage) || (to == null) || (!(this.messages.containsKey(to)||to.equals("all"))))
					return;
				MapMessage mapMsg = (MapMessage)msg;
				if(!to.equals("all"))
					pushMessage(to,to,from,mapMsg);
				else
				{
					broadast(from,mapMsg);
				}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	private void broadast(String from, MapMessage mapMsg ) throws JMSException
	{
		for(String agtName:this.messages.keySet())
		{
				pushMessage(agtName,ICommunicatorSkill.BROADCAST,from,mapMsg);
		}
	}
	
	private void pushMessage(String netAgentName, String to, String from, MapMessage mapMsg ) throws JMSException
	{
		LinkedList<GamaMap<String, Object>>	myMsgBox = this.messages.get(netAgentName);
		Object res = xstream.fromXML((String)mapMsg.getObject(ICommunicatorSkill.CONTENT));
		
		GamaMap<String, Object> agentMsg=  new GamaMap<String, Object>();
		agentMsg.put(ICommunicatorSkill.SENDER, from);
		agentMsg.put(ICommunicatorSkill.DEST, to);
		agentMsg.put(ICommunicatorSkill.CONTENT,res);
		System.out.println("Sent message : "  + res);
		this.messages.get(netAgentName).addLast(agentMsg);
	}
	
	protected void send(String sender, String dest, Object content) throws JMSException
	{
	   	 MapMessage msg = session.createMapMessage();
	   	 msg.setString(ICommunicatorSkill.CONTENT, xstream.toXML(content));
	     msg.setStringProperty(ICommunicatorSkill.DEST, dest);
	     msg.setStringProperty(ICommunicatorSkill.SENDER,sender );
	   	 producer.send(msg);
	}

	protected GamaMap<String, Object> fetchMyMailBox(String myName)
	{
		if(!this.messages.containsKey(myName))
			throw new GamaRuntimeException(new Exception("Agent has not any mailbox"));
		
	   	LinkedList<GamaMap<String,Object>> mList=this.messages.get(myName);
	   	if(mList.isEmpty())
	   		return null;
	   	else
	   		return this.messages.get(myName).pollFirst();
	}
	
	protected boolean isEmptyMailBox(String myName)
	{
		if(!this.messages.containsKey(myName))
			throw new GamaRuntimeException(new Exception("Agent has not any mailbox"));
		
		return this.messages.get(myName).size()==0;
	}
	
	
	
}
