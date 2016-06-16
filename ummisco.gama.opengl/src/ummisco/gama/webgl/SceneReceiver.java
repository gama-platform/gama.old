package ummisco.gama.webgl;

/**
 * A singleton class that is able to receive simplified scenes
 * 
 * @author drogoul
 *
 */
public class SceneReceiver {

	private final static SceneReceiver instance = new SceneReceiver();

	public static SceneReceiver getInstance() {
		return instance;
	}

	private SceneReceiver() {
	}

	public void receive(final SimpleScene scene) {
	}

	public boolean canReceive() {
		return false;
	}

}
