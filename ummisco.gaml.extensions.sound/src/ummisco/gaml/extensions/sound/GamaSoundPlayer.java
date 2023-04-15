/*******************************************************************************************************
 *
 * GamaSoundPlayer.java, in ummisco.gaml.extensions.sound, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
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

/**
 * The Class GamaSoundPlayer.
 */
public class GamaSoundPlayer {

	/**
	 * The listener interface for receiving myBasicPlayer events.
	 * The class that is interested in processing a myBasicPlayer
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addMyBasicPlayerListener<code> method. When
	 * the myBasicPlayer event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see MyBasicPlayerEvent
	 */
	class MyBasicPlayerListener implements BasicPlayerListener {

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

	/** The Constant OVERWRITE_MODE. */
	public static final String OVERWRITE_MODE = IKeyword.OVERWRITE;

	/** The basic player. */
	final BasicPlayer basicPlayer;
	// private File soundFile;
	/** The repeat. */
	// private String soundPlayerMode = OVERWRITE_MODE;
	Boolean repeat = false;

	/** The agent died or sim disposed. */
	// assure that we don't repeat playing a music file on an already dead agent
	volatile boolean agentDiedOrSimDisposed = false;

	/** The end of media. */
	Boolean endOfMedia = false;
	
	/** The player stopped. */
	Boolean playerStopped = false;

	/**
	 * Instantiates a new gama sound player.
	 */
	public GamaSoundPlayer() {
		basicPlayer = new BasicPlayer();
		basicPlayer.addBasicPlayerListener(new MyBasicPlayerListener());
	}

	/**
	 * Repeat sound.
	 *
	 * @param scope the scope
	 */
	void repeatSound(final IScope scope) {

		if (!agentDiedOrSimDisposed) {
			try {
				basicPlayer.play();
			} catch (final BasicPlayerException e) {
				throw GamaRuntimeException.error("Failed to replay sound", scope);
			}
		}
	}

	/**
	 * Play.
	 *
	 * @param scope the scope
	 * @param soundFile the sound file
	 * @param playerMode the player mode
	 * @param repeat the repeat
	 * @throws GamaRuntimeException the gama runtime exception
	 */
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

	/**
	 * Stop.
	 *
	 * @param scope the scope
	 * @param agentDiedOrSimDisposed the agent died or sim disposed
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	public void stop(final IScope scope, final boolean agentDiedOrSimDisposed) throws GamaRuntimeException {

		this.agentDiedOrSimDisposed = agentDiedOrSimDisposed;

		try {
			endOfMedia = true;
			basicPlayer.stop();
		} catch (final BasicPlayerException e) {
			e.printStackTrace();
			throw GamaRuntimeException.error("Failed to stop sound player", scope);
		}

	}

	/**
	 * Pause.
	 *
	 * @param scope the scope
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	public void pause(final IScope scope) throws GamaRuntimeException {
		try {
			basicPlayer.pause();
		} catch (final BasicPlayerException e) {
			e.printStackTrace();
			throw GamaRuntimeException.error("Failed to pause sound player", scope);
		}
	}

	/**
	 * Resume.
	 *
	 * @param scope the scope
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	public void resume(final IScope scope) throws GamaRuntimeException {
		try {
			basicPlayer.resume();
		} catch (final BasicPlayerException e) {
			e.printStackTrace();
			throw GamaRuntimeException.error("Failed to resume sound player", scope);
		}
	}

	/**
	 * Checks if is repeat.
	 *
	 * @return true, if is repeat
	 */
	public boolean isRepeat() {
		return repeat;
	}

	/**
	 * Can be reused.
	 *
	 * @return true, if successful
	 */
	public boolean canBeReused() {
		return !repeat && playerStopped;
	}
}
