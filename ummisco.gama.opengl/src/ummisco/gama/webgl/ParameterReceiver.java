package ummisco.gama.webgl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class ParameterReceiver implements Runnable {

	private static ParameterReceiver instance = new ParameterReceiver();
	public static ParameterReceiver getInstance(){return  instance;}
	public boolean finished = false;
	
	private ParameterReceiver(){}
	
	public void run (){
		try {
			// TODO // get back the good listening port
			Socket tcpReception = new Socket("localhost", 6001);
			BufferedReader in = new BufferedReader(new InputStreamReader(tcpReception.getInputStream()));
			String temp = "";
			while (!finished) {
				if (in.ready()) {
					temp = in.readLine();
					System.out.println(temp);
				}
			}
			in.close();
			tcpReception.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}