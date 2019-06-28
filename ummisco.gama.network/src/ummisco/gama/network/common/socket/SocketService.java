package ummisco.gama.network.common.socket;

import java.io.IOException;
import java.net.UnknownHostException;

public interface SocketService {

	void startService() throws UnknownHostException, IOException;

	void stopService();

	void receivedMessage(String sender, String message);

	void sendMessage(String message) throws IOException;

	boolean isOnline();

	String getRemoteAddress();

	String getLocalAddress();

	String END = "END";
	String GAMA_START = "(((GAMA)))";

}
