package ummisco.gama.webgl;

import java.io.DataOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import org.eclipse.core.runtime.Platform;

import com.google.gson.Gson;

/**
 * A singleton class that is able to receive simplified scenes
 * 
 * @author drogoul
 *
 */
public class SceneReceiver {

	private final static SceneReceiver instance = new SceneReceiver();

	private boolean canReceive = false;
	private Socket tcpClient;
	private DataOutputStream os;
	private PrintWriter pw;
	
	public static SceneReceiver getInstance() {
		return instance;
	}

	private SceneReceiver() {
		try {
			//System.out.println(Platform.getApplicationArgs());
			for(String arg : Platform.getApplicationArgs()) {
				System.out.println("argument : " + arg);
			}
			if (canReceive) {
				tcpClient = new Socket("localhost", 6000);
				os = new DataOutputStream(tcpClient.getOutputStream());
				pw = new PrintWriter(os,false);
				Thread parameterReceiver = new Thread (ParameterReceiver.getInstance());
				parameterReceiver.start();
			}
		} catch (Exception e) {
			e.getMessage();
		}
	}

	public void receive(final SimpleScene scene) {
		reception(false);
		if (scene != null) {
			try {
				Gson gson = new Gson();
				String sceneSend = gson.toJson(scene);
				sceneSend = sceneSend+"@end";
				pw.print(sceneSend);
				System.out.println(sceneSend);
				pw.flush();
				//	pw.flush();
				//System.out.println(sceneSend+'\n');
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		reception(true);
	}

	private void reception(boolean canReceive) {
		this.canReceive = canReceive;
	}
	
	public boolean canReceive() {
		return canReceive;
	}

}