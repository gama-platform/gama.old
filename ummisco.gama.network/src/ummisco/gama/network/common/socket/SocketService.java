package ummisco.gama.network.common.socket;

import java.io.IOException;
import java.net.UnknownHostException;

public interface SocketService {

	public void startService() throws UnknownHostException, IOException;
	public void stopService();
	public void receivedMessage(String sender,String message);
	public void sendMessage(String message) throws IOException;
	public boolean isOnline();
	
	public String getRemoteAddress();
	public String getLocalAddress();
	
	public String END="END";
	public String GAMA_START="(((GAMA)))";
	
	public static String compileMessage(String message){
		return GAMA_START+message;
	}
	public static boolean isStopMessage(String message){
		return message.indexOf(GAMA_START+END) == 0;
	}
	public static boolean isGamaMessage(String message){
		return message.indexOf(GAMA_START) == 0;
	}
	
}
