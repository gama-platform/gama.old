/*******************************************************************************************************
 *
 * ArduinoConnector.java, in ummisco.gama.network, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.network.serial;

import arduino.Arduino;
import arduino.PortDropdownMenu;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import ummisco.gama.network.common.Connector;
import ummisco.gama.network.common.GamaNetworkException;
import ummisco.gama.network.common.socket.SocketService;

/**
 * The Class ArduinoConnector.
 */
public class ArduinoConnector extends Connector {

	/** The arduino. */
	Arduino arduino;
	
	/** The port. */
	String PORT = "";
	
	/** The baud. */
	int BAUD = 9600;	
	
	/** The ss thread. */
	MultiThreadedArduinoReceiver ssThread;	
	
	/**
	 * Instantiates a new arduino connector.
	 *
	 * @param scope the scope
	 */
	public ArduinoConnector(final IScope scope) {}	
	
	@Override
	protected void connectToServer(IAgent agent) throws GamaNetworkException {
		PortDropdownMenu portList = new PortDropdownMenu();
		portList.refreshMenu();
		
		// cu.usbmodem1441012		
		for(int i = 0; i < portList.getItemCount(); i++) {
			System.out.println(portList.getItemAt(i));
			if(portList.getItemAt(i).contains("cu.usbmodem")) {
				System.out.println(portList.getItemAt(i));
				PORT = portList.getItemAt(i);
			}
		}		
		arduino = new Arduino(PORT,BAUD);
		
		if(arduino.openConnection()){
			System.out.println("CONNECTION OPENED");
		}

		ssThread = new MultiThreadedArduinoReceiver(agent, 100, arduino);
		ssThread.start();
	}

	@Override
	protected boolean isAlive(IAgent agent) throws GamaNetworkException {
		return true;
		// TODO Auto-generated method stub
		// return false;
	}

	@Override
	protected void subscribeToGroup(IAgent agt, String boxName) throws GamaNetworkException {}

	@Override
	protected void unsubscribeGroup(IAgent agt, String boxName) throws GamaNetworkException {}

	@Override
	protected void releaseConnection(IScope scope) throws GamaNetworkException {
		if (ssThread != null) {
			ssThread.interrupt();
		}
		
		arduino.closeConnection();
		System.out.println("CONNECTION CLOSED");		
	}

	@Override
	protected void sendMessage(IAgent sender, String receiver, String content) throws GamaNetworkException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public SocketService getSocketService() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
