/*********************************************************************************************
 *
 * 'GamaSoundPlayer.java, in plugin ummisco.gaml.extensions.sound, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gaml.extensions.sound;

import java.io.File;
import java.util.Map;
import javazoom.jlgui.basicplayer.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.runtime.exceptions.GamaRuntimeException;

public class GamaSoundPlayer {

	private class MyBasicPlayerListener implements BasicPlayerListener {

		@Override
		public void opened(final Object stream, final Map properties) {}

		@Override
		public void progress(final int bytesread, final long microseconds, final byte[] pcmdata, final Map properties) {}

		@Override
		public void stateUpdated(final BasicPlayerEvent event) {

			if ( event.getCode() == BasicPlayerEvent.EOM ) {

				if ( repeat ) {
					endOfMedia = true;
				}
			}

			if ( event.getCode() == BasicPlayerEvent.STOPPED ) {

				if ( repeat && endOfMedia ) {
					endOfMedia = false;
					repeatSound();
				}

				playerStopped = true;

			}
		}

		@Override
		public void setController(final BasicController controller) {}
	}

	public static final String OVERWRITE_MODE = IKeyword.OVERWRITE;
	public static final String IGNORE_MODE = IKeyword.IGNORE;

	private final BasicPlayer basicPlayer;
	// private File soundFile;
	// private String soundPlayerMode = OVERWRITE_MODE;
	private Boolean repeat = false;

	// assure that we don't repeat playing a music file on an already dead agent
	private Boolean agentDiedOrSimDisposed = false;

	private Boolean endOfMedia = false;
	private Boolean playerStopped = false;

	public GamaSoundPlayer() {
		basicPlayer = new BasicPlayer();
		basicPlayer.addBasicPlayerListener(new MyBasicPlayerListener());
	}

	private void repeatSound() {
		synchronized (agentDiedOrSimDisposed) {
			if ( !agentDiedOrSimDisposed ) {
				try {
					basicPlayer.play();
				} catch (BasicPlayerException e) {
					throw GamaRuntimeException.error("Failed to replay sound");
				}
			}
		}
	}

	public void play(final File soundFile, final String playerMode, final boolean repeat) throws GamaRuntimeException {
		try {
			int playerState = basicPlayer.getStatus();

			if ( playerState == BasicPlayer.UNKNOWN || playerState == BasicPlayer.STOPPED ) {
				basicPlayer.open(soundFile);
				basicPlayer.play();
			} else if ( (playerState == BasicPlayer.PLAYING || playerState == BasicPlayer.PAUSED) &&
				playerMode.equals(OVERWRITE_MODE) ) {
				basicPlayer.stop();

				basicPlayer.open(soundFile);
				basicPlayer.play();
			}

			// this.soundFile = soundFile;
			// this.soundPlayerMode = playerMode;
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
				endOfMedia = true;
				basicPlayer.stop();
			} catch (BasicPlayerException e) {
				e.printStackTrace();
				throw GamaRuntimeException.error("Failed to stop sound player");
			}
		}
	}

	public void pause() throws GamaRuntimeException {
		try {
			basicPlayer.pause();
		} catch (BasicPlayerException e) {
			e.printStackTrace();
			throw GamaRuntimeException.error("Failed to pause sound player");
		}
	}

	public void resume() throws GamaRuntimeException {
		try {
			basicPlayer.resume();
		} catch (BasicPlayerException e) {
			e.printStackTrace();
			throw GamaRuntimeException.error("Failed to resume sound player");
		}
	}

	public boolean isRepeat() {
		return repeat;
	}

	public boolean canBeReused() {
		return !repeat && playerStopped;
	}
}
