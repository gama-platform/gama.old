package ummisco.gama.network.tcp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.network.common.IConnector;
import ummisco.gama.network.common.socket.SocketService;

public abstract class ClientService extends Thread implements SocketService {
	private Socket socket;
	private final String server;
	private final int port;
	private BufferedReader receiver;
	private PrintWriter sender;
	private boolean isAlive;
	// private IConnector modelConnector;

	public ClientService(final String server, final int port, final IConnector connector) {
		// this.modelConnector = connector;
		this.port = port;
		this.server = server;
	}

	@Override
	public String getRemoteAddress() {
		if (socket == null) { return null; }
		return this.socket.getInetAddress() + ":" + this.port;
	}

	@Override
	public String getLocalAddress() {
		if (socket == null) { return null; }
		return this.socket.getLocalAddress() + ":" + this.port;
	}

	@Override
	public void startService() throws UnknownHostException, IOException {
		socket = new Socket(this.server, this.port);

		isAlive = true;

		this.start();

	}

	@Override
	public void stopService() {
		this.isAlive = false;
		if (sender != null) {
			sender.close();
		}
		try {
			if (receiver != null) {
				receiver.close();
			}
			socket.close();
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean isOnline() {
		return isAlive;
	}

	@SuppressWarnings ("unchecked")
	@Override
	public void run() {
		try {
			while (this.isAlive) {
				String msg = "";
				receiver = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				msg = receiver.readLine();
				msg = msg.replaceAll("@n@", "\n");
				msg = msg.replaceAll("@b@@r@", "\b\r");
				receivedMessage(this.socket.getInetAddress() + ":" + this.port, msg);
			}
		} catch (final SocketTimeoutException e) {
			DEBUG.LOG("Socket timeout");
		} catch (final SocketException e) {
			DEBUG.LOG("Socket closed");
		} catch (final IOException e1) {
			DEBUG.LOG("Socket error" + e1);
		}

	}

	@Override
	public void sendMessage(final String message) throws IOException {
		if (socket == null || !isOnline()) { return; }
		sender = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
		final String msg = message.replaceAll("\n", "@n@").replaceAll("\b\r", "@b@@r@");
		sender.println(msg + "\n");
		sender.flush();
	}

}
