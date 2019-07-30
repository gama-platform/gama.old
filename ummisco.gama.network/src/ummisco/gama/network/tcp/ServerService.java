package ummisco.gama.network.tcp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.network.common.socket.SocketService;

public abstract class ServerService extends Thread implements SocketService {
	private ServerSocket serverSocket;
	private final int port;
	private boolean isAlive;
	private boolean isOnline;
	private PrintWriter sender;
	private Socket currentSocket;
	BufferedReader receiver = null;

	public ServerService(final int port) {
		this.port = port;
		this.isAlive = false;
		this.isOnline = false;
	}

	@Override
	public String getRemoteAddress() {
		if (currentSocket == null) { return null; }
		return this.currentSocket.getInetAddress() + ":" + this.port;
	}

	@Override
	public String getLocalAddress() {
		if (currentSocket == null) { return null; }
		return this.currentSocket.getLocalAddress() + ":" + this.port;
	}

	@Override
	public void startService() throws UnknownHostException, IOException {
		this.serverSocket = new ServerSocket(port);
		this.isAlive = true;
		this.isOnline = true;
		this.start();
	}

	@Override
	public void run() {
		while (this.isAlive) {
			isOnline = true;
			try {
				DEBUG.OUT("before accept wait...");
				currentSocket = this.serverSocket.accept();
				String msg = "";
				do {
					DEBUG.OUT("wait message ...........");
					receiver = new BufferedReader(new InputStreamReader(currentSocket.getInputStream()));
					msg = receiver.readLine();
					msg = msg.replaceAll("@n@", "\n");
					msg = msg.replaceAll("@b@@r@", "\b\r");
					receivedMessage(this.currentSocket.getInetAddress() + ":" + this.port, msg);
					DEBUG.OUT("fin traitement message ..." + this.isOnline);
				} while (isOnline);

			} catch (final SocketTimeoutException e) {
				DEBUG.LOG("Socket timeout");
			} catch (final SocketException e) {
				DEBUG.LOG("Socket closed");
			} catch (final IOException e1) {
				DEBUG.LOG("Socket error" + e1);
				/// isOnline = false;
			}
		}
	}

	@Override
	public boolean isOnline() {
		return isAlive && isOnline;
	}

	@Override
	public void stopService() {
		isOnline = false;
		isAlive = false;

		if (sender != null) {
			sender.close();
		}
		try {
			if (receiver != null) {
				receiver.close();
			}
			if (currentSocket != null) {
				currentSocket.close();
			}
			if (serverSocket != null) {
				serverSocket.close();
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
		this.interrupt();
	}

	@Override
	public void sendMessage(final String msg) throws IOException {
		String message = msg;
		if (currentSocket == null || !isOnline()) { return; }
		sender = new PrintWriter(new BufferedWriter(new OutputStreamWriter(currentSocket.getOutputStream())), true);
		message = message.replaceAll("\n", "@n@");
		message = message.replaceAll("\b\r", "@b@@r@");
		sender.println(message + "\n");
		sender.flush();

	}

}
