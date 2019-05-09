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
    private String server;
    private int port;
    private BufferedReader receiver;
    private PrintWriter sender;
    private boolean isAlive;
    private IConnector modelConnector;
	
	
    public ClientService(String server, int port, IConnector connector)
    {
    	this.modelConnector = connector;
    	this.port = port;
    	this.server = server;
    }
    public String getRemoteAddress()
    {
    	if(socket==null) return null;
    	return this.socket.getInetAddress()+":"+this.port;
    }
    public String getLocalAddress()
    {
    	if(socket==null) return null;
    	return this.socket.getLocalAddress()+":"+this.port;
    }
	
	public void startService() throws UnknownHostException, IOException
	{
		socket = new Socket(this.server, this.port);
		
		isAlive = true;
		
		this.start();
		
	}
	
	public void stopService() {
		this.isAlive = false;
		if(sender!=null)
		   sender.close();
	        try {
				receiver.close();
		        socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public boolean isOnline() {
		return isAlive;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		try {
			while(this.isAlive) {
				String msg="";
				receiver = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				msg = receiver.readLine();
				msg = msg.replaceAll("@n@", "\n");
				msg = msg.replaceAll("@b@@r@", "\b\r");
				receivedMessage(this.socket.getInetAddress()+":"+this.port,  msg);
			}
		} catch(SocketTimeoutException e)
		{
			DEBUG.LOG("Socket timeout");
		}catch(SocketException e)
		{
			DEBUG.LOG("Socket closed");
		} catch (IOException e1) {
			DEBUG.LOG("Socket error"+e1);
		}

     

	}

	@Override
	public void sendMessage(String message) throws IOException {
		if(socket == null ||!isOnline()) return;
		sender = new PrintWriter(
	              new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);	
		message = message.replaceAll("\n", "@n@" );
		message = message.replaceAll("\b\r", "@b@@r@" );
		sender.println(message+"\n"); 
		sender.flush();
	}
	
	
}
