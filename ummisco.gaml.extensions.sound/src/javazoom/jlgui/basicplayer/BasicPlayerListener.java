/*******************************************************************************************************
 *
 * BasicPlayerListener.java, in ummisco.gaml.extensions.sound, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package javazoom.jlgui.basicplayer;

import java.util.Map;

/**
 * This interface defines callbacks methods that will be notified
 * for all registered BasicPlayerListener of BasicPlayer.
 */
public interface BasicPlayerListener
{
    /**
     * Open callback, stream is ready to play.
     *
     * properties map includes audio format dependant features such as
     * bitrate, duration, frequency, channels, number of frames, vbr flag,
     * id3v2/id3v1 (for MP3 only), comments (for Ogg Vorbis), ...  
     *
     * @param stream could be File, URL or InputStream
     * @param properties audio stream properties.
     */
    public void opened(Object stream, Map properties);

    /**
     * Progress callback while playing.
     * 
     * This method is called severals time per seconds while playing.
     * properties map includes audio format features such as
     * instant bitrate, microseconds position, current frame number, ... 
     * 
     * @param bytesread from encoded stream.
     * @param microseconds elapsed (<b>reseted after a seek !</b>).
     * @param pcmdata PCM samples.
     * @param properties audio stream parameters.
     */
    public void progress(int bytesread, long microseconds, byte[] pcmdata, Map properties);

    /**
     * Notification callback for basicplayer events such as opened, eom ...
     *  
     * @param event
     */
    public void stateUpdated(BasicPlayerEvent event);

    /**
     * A handle to the BasicPlayer, plugins may control the player through
     * the controller (play, stop, ...)
     * @param controller : a handle to the player
     */
    public void setController(BasicController controller);
}
