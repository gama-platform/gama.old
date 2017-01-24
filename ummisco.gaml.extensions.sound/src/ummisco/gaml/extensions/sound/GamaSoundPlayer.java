/*********************************************************************************************
 *
 * 'GamaSoundPlayer.java, in plugin ummisco.gaml.extensions.sound, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gaml.extensions.sound;

import java.io.File;
import java.util.Map;

import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayerListener;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

public class GamaSoundPlayer {

	private class MyBasicPlayerListener implements BasicPlayerListener {

		@Override
		public void opened(final Object stream, final Map properties) {}

		@Override
		public void progress(final int bytesread, final long microseconds, final byte[] pcmdata,
				final Map properties) {}

		@Override
		public void stateUpdated(final BasicPlayerEvent event) {

			if (event.getCode() == BasicPlayerEvent.EOM) {

				if (repeat) {
					endOfMedia = true;
				}
			}

			if (event.getCode() == BasicPlayerEvent.STOPPED) {

				if (repeat && endOfMedia) {
					endOfMedia = false;
					repeatSound(GAMA.getRuntimeScope());
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

	private void repeatSound(final IScope scope) {
		synchronized (agentDiedOrSimDisposed) {
			if (!agentDiedOrSimDisposed) {
				try {
					basicPlayer.play();
				} catch (final BasicPlayerException e) {
					throw GamaRuntimeException.error("Failed to replay sound", scope);
				}
			}
		}
	}

	public void play(final IScope scope, final File soundFile, final String playerMode, final boolean repeat)
			throws GamaRuntimeException {
		try {
			final int playerState = basicPlayer.getStatus();

			if (playerState == BasicPlayer.UNKNOWN || playerState == BasicPlayer.STOPPED) {
				basicPlayer.open(soundFile);
				basicPlayer.play();
			} else if ((playerState == BasicPlayer.PLAYING || playerState == BasicPlayer.PAUSED)
					&& playerMode.equals(OVERWRITE_MODE)) {
				basicPlayer.stop();

				basicPlayer.open(soundFile);
				basicPlayer.play();
			}

			// this.soundFile = soundFile;
			// this.soundPlayerMode = playerMode;
			this.repeat = repeat;

		} catch (final BasicPlayerException e) {
			e.printStackTrace();
			throw GamaRuntimeException.error(e.getMessage(), scope);
		}
	}

	public void stop(final IScope scope, final boolean agentDiedOrSimDisposed) throws GamaRuntimeException {
		synchronized (this.agentDiedOrSimDisposed) {
			this.agentDiedOrSimDisposed = agentDiedOrSimDisposed;

			try {
				endOfMedia = true;
				basicPlayer.stop();
			} catch (final BasicPlayerException e) {
				e.printStackTrace();
				throw GamaRuntimeException.error("Failed to stop sound player", scope);
			}
		}
	}

	public void pause(final IScope scope) throws GamaRuntimeException {
		try {
			basicPlayer.pause();
		} catch (final BasicPlayerException e) {
			e.printStackTrace();
			throw GamaRuntimeException.error("Failed to pause sound player", scope);
		}
	}

	public void resume(final IScope scope) throws GamaRuntimeException {
		try {
			basicPlayer.resume();
		} catch (final BasicPlayerException e) {
			e.printStackTrace();
			throw GamaRuntimeException.error("Failed to resume sound player", scope);
		}
	}

	public boolean isRepeat() {
		return repeat;
	}

	public boolean canBeReused() {
		return !repeat && playerStopped;
	}
}
