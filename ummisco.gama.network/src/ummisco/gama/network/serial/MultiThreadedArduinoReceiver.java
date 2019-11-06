package ummisco.gama.network.serial;

import arduino.Arduino;
import msi.gama.extensions.messaging.GamaMailbox;
import msi.gama.extensions.messaging.GamaMessage;
import msi.gama.extensions.messaging.MessagingSkill;
import msi.gama.metamodel.agent.IAgent;
import ummisco.gama.dev.utils.DEBUG;

public class MultiThreadedArduinoReceiver extends Thread {
	static {
		DEBUG.ON();
	}

	private final IAgent myAgent;
	private volatile boolean closed = false;
	private int timer = 1000;
	private Arduino arduino;
	
	public MultiThreadedArduinoReceiver(final IAgent a, final int _timer, final Arduino ard) {
		myAgent = a;
		arduino = ard;
		timer = _timer;
	}	
	
	@Override
	public void run() {
		System.out.println("START OF THE THREAD");
		
		// Successfully created Server Socket. Now wait for connections.
		while (!closed) {
			System.out.println("enter while");

			try {
				if (myAgent.dead()) {
					this.interrupt();
				}
		//		System.out.println("not dead");

				final String sentence = arduino.serialRead(1);
				
				GamaMailbox mailbox = (GamaMailbox) myAgent.getAttribute(MessagingSkill.MAILBOX_ATTRIBUTE);
				if (mailbox == null) {
					mailbox = new GamaMailbox();
					myAgent.setAttribute(MessagingSkill.MAILBOX_ATTRIBUTE, mailbox);
				}

				
				//IList<ConnectorMessage> msgs = (IList<ConnectorMessage>) myAgent.getAttribute("messages" + myAgent);
				//if (msgs == null) {
				//	msgs = GamaListFactory.create(ConnectorMessage.class);
				//}
				if (myAgent.dead()) {
					this.interrupt();
				}
				
		//		System.out.println("sentence = " + sentence);

				GamaMessage msg = new GamaMessage(myAgent.getScope(), "Arduino", myAgent.getName(), sentence);
				
			//	final NetworkMessage msg = MessageFactory.buildNetworkMessage("Arduino", sentence);
				mailbox.add(msg);
				
				
			//	msgs.addValue(myAgent.getScope(), msg);
			//	System.out.println("sentence = " + msg.getPlainContents());

			//	myAgent.setAttribute("messages" + myAgent, msgs);
		//		System.out.println("not dead");

			} catch (final Exception ioe) {
				closed = true;
				this.interrupt();
				ioe.printStackTrace();
			}
		//	System.out.println("avt wait");

			try {
				sleep(timer);
			} catch (InterruptedException e) {
				// e.printStackTrace();
			}
		//	System.out.println("WAIT off!");
		}
		// System.out.println("stop stop off!");
		
	}	
}
