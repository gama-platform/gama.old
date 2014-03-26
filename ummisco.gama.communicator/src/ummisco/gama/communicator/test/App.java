package ummisco.gama.communicator.test;

import java.util.Hashtable;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class App {

	private static String PORTNUM="61616";
    
    private static String url ="tcp://localhost:61616"; // ActiveMQConnection.DEFAULT_BROKER_URL;

    // Name of the queue we will be sending messages to
    private static String subject = "sampleTopic";

    public static void main(String[] args) throws JMSException {
    	
    	System.out.println("coucocu "+ url );
    	  // Getting JMS connection from the server
        ConnectionFactory connectionFactory
            = new ActiveMQConnectionFactory(url);
        Connection connection = connectionFactory.createConnection();
        connection.start();

        // Creating session for seding messages
        Session session = connection.createSession(false,
            Session.AUTO_ACKNOWLEDGE);

        // Getting the queue 'TESTQUEUE'
        Destination destination = session.createTopic("sampleTopic");

        // MessageConsumer is used for receiving (consuming) messages
        MessageConsumer consumer = session.createConsumer(destination);

        // Here we receive the message.
        // By default this call is blocking, which means it will wait
        // for a message to arrive on the queue.
        Message message = consumer.receive();

        // There are many types of Message and TextMessage
        // is just one of them. Producer sent us a TextMessage
        // so we must cast to it to get access to its .getText()
        // method.
        if (message instanceof MapMessage) {
        	MapMessage textMessage = (MapMessage) message;
            System.out.println("Received message '"
                + textMessage.toString() + "'");
        }
        connection.close();
    }
    	
    	
    	
    	
    	
    	
//        // Getting JMS connection from the server and starting it
//        ConnectionFactory connectionFactory =
//            new ActiveMQConnectionFactory(url);
//        Connection connection = connectionFactory.createConnection();
//        connection.start();
//
//        // JMS messages are sent and received using a Session. We will
//        // create here a non-transactional session object. If you want
//        // to use transactions you should set the first parameter to 'true'
//        Session session = connection.createSession(false,
//            Session.AUTO_ACKNOWLEDGE);
//
//        // Destination represents here our queue 'TESTQUEUE' on the
//        // JMS server. You don't have to do anything special on the
//        // server to create it, it will be created automatically.
//        Destination destination = session.createTopic(subject);
//
//        // MessageProducer is used for sending messages (as opposed
//        // to MessageConsumer which is used for receiving them)
//        MessageProducer producer = session.createProducer(destination);
//
//        // We will send a small text message saying 'Hello' in Japanese
//        TextMessage message = session.createTextMessage("こんにちは");
//
//        // Here we are sending the message!
//        producer.send(message);
//        System.out.println("Sent message '" + message.getText() + "'");
//
//        connection.close();
    }

