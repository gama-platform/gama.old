package ummisco.gaml.extensions.music;

import java.io.File;
import java.util.Map;

import msi.gama.runtime.exceptions.GamaRuntimeException;

import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayerListener;

public class GamaMusicPlayer {
	
	private class MyBasicPlayerListener implements BasicPlayerListener {

		@Override
		public void opened(Object stream, Map properties) {}

		@Override
		public void progress(int bytesread, long microseconds, byte[] pcmdata, Map properties) {}

		@Override
		public void stateUpdated(BasicPlayerEvent event) {
			if (event.getCode()==BasicPlayerEvent.EOM && repeat) { repeatMusic(); }
		}

		@Override
		public void setController(BasicController controller) {}
	}
	
	
	public static final int OVERWRITE_MODE = 1;
	public static final int IGNORE_MODE = 2;

	private BasicPlayer musicPlayer;
	private File musicFile;
	private int musicPlayerMode = OVERWRITE_MODE;
	private boolean repeat = false;
	
	// assure that we don't repeat playing a music file on an already dead agent
	private Boolean agentDiedOrSimDisposed = false; 
	
	
	public GamaMusicPlayer() {
		musicPlayer = new BasicPlayer();
		musicPlayer.addBasicPlayerListener(new MyBasicPlayerListener());
	}
	
	
	private void repeatMusic() {
		synchronized (agentDiedOrSimDisposed) {
			if (!agentDiedOrSimDisposed) {
				try {
					musicPlayer.play();
				} catch (BasicPlayerException e) {
					throw GamaRuntimeException.error("Failed to replay music");
				}
			}
		}
	}



	public void play(final File musicFile, final int gamaMode, final boolean repeat) throws GamaRuntimeException {
		try {
			int playerState = musicPlayer.getStatus();

			if (playerState == BasicPlayer.UNKNOWN || playerState == BasicPlayer.STOPPED) {
				musicPlayer.open(musicFile);
				musicPlayer.play();
			} else if ( (playerState == BasicPlayer.PLAYING) || (playerState == BasicPlayer.PAUSED) && gamaMode == OVERWRITE_MODE ) {
				musicPlayer.stop();
				
				musicPlayer.open(musicFile);
				musicPlayer.play();
			}
			
			this.musicFile = musicFile;
			this.musicPlayerMode = gamaMode;
			this.repeat = repeat;
			
		} catch (BasicPlayerException e) {
			e.printStackTrace();
			throw GamaRuntimeException.error(e.getMessage());
		}
	}

	public void stop(final boolean agentDiedOrSimDisposed) throws GamaRuntimeException {
		synchronized (this.agentDiedOrSimDisposed) {
			this.agentDiedOrSimDisposed = agentDiedOrSimDisposed;

			try {
				musicPlayer.stop();
			} catch (BasicPlayerException e) {
				e.printStackTrace();
				throw GamaRuntimeException.error("Failed to stop music player");
			}
		}
	}

	public void pause() throws GamaRuntimeException {
		try {
			musicPlayer.pause();
		} catch (BasicPlayerException e) {
			e.printStackTrace();
			throw GamaRuntimeException.error("Failed to pause music player");
		}
	}

	public void resume() throws GamaRuntimeException {
		try {
			musicPlayer.resume();
		} catch (BasicPlayerException e) {
			e.printStackTrace();
			throw GamaRuntimeException.error("Failed to resume music player");
		}
	}

	public int getMode() {
		return musicPlayerMode;
	}

	public void setMode(int gamaMode) {
		this.musicPlayerMode = gamaMode;
	}

	public boolean isRepeat() {
		return repeat;
	}

	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
	}
}
