/*******************************************************************************************************
 *
 * ArduinoConnector.java, in ummisco.gama.network, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.network.serial;
 
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import ummisco.gama.network.common.Connector;
import ummisco.gama.network.common.GamaNetworkException;
import ummisco.gama.network.common.socket.SocketService;

/**
 * The Class ArduinoConnector.
 */
public class ArduinoConnector extends Connector {

	/** The arduino. */
	MyArduino arduino;
	
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
		MyPortDropdownMenu portList = new MyPortDropdownMenu();
		portList.refreshMenu();
		
		// cu.usbmodem1441012		
		for(int i = 0; i < portList.getItemCount(); i++) {
			System.out.println(portList.getItemAt(i));
			if(portList.getItemAt(i).contains("cu.usbmodem")) {
				System.out.println(portList.getItemAt(i));
				PORT = portList.getItemAt(i);
			}
		}		
		if("".equals(PORT)) {
			PORT=this.getConfigurationParameter(SERVER_URL);
		}
		try {
		arduino = new MyArduino(PORT,BAUD);
		}catch(Exception ex) {}
		if(arduino==null) {
			GAMA.reportError(agent.getScope(), GamaRuntimeException.warning("Cannot connect Arduino to Port: " + PORT, agent.getScope()), false);
			return;
		}else
		if(arduino.openConnection()){
			System.out.println("CONNECTION OPENED");
		}

		ssThread = new MultiThreadedArduinoReceiver(agent, 100, arduino);
		ssThread.start();
	}

	@Override
	protected boolean isAlive(IAgent agent) throws GamaNetworkException {
		return true;
		
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
		if(arduino!=null) {
			arduino.closeConnection();
			System.out.println("CONNECTION CLOSED");
		}
				
	}

	@Override
	protected void sendMessage(IAgent sender, String receiver, String content) throws GamaNetworkException {
		
		
	}

	@Override
	public SocketService getSocketService() {
		
		return null;
	}

	
}
