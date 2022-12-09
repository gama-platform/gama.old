/*******************************************************************************************************
 *
 * BasicController.java, in ummisco.gaml.extensions.sound, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package javazoom.jlgui.basicplayer;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

/**
 * This interface defines player controls available.  
 */
public interface BasicController
{
    /**
     * Open inputstream to play.
     * @param in
     * @throws BasicPlayerException
     */
    public void open(InputStream in) throws BasicPlayerException;

    /**
     * Open file to play.
     * @param file
     * @throws BasicPlayerException
     */
    public void open(File file) throws BasicPlayerException;

    /**
     * Open URL to play.
     * @param url
     * @throws BasicPlayerException
     */
    public void open(URL url) throws BasicPlayerException;

    /**
     * Skip bytes.
     * @param bytes
     * @return bytes skipped according to audio frames constraint.
     * @throws BasicPlayerException
     */
    public long seek(long bytes) throws BasicPlayerException;

    /**
     * Start playback.
     * @throws BasicPlayerException
     */
    public void play() throws BasicPlayerException;

    /**
     * Stop playback. 
     * @throws BasicPlayerException
     */
    public void stop() throws BasicPlayerException;

    /**
     * Pause playback. 
     * @throws BasicPlayerException
     */
    public void pause() throws BasicPlayerException;

    /**
     * Resume playback. 
     * @throws BasicPlayerException
     */
    public void resume() throws BasicPlayerException;

    /**
     * Sets Pan (Balance) value.
     * Linear scale : -1.0 <--> +1.0
     * @param pan value from -1.0 to +1.0
     * @throws BasicPlayerException
     */
    public void setPan(double pan) throws BasicPlayerException;

    /**
     * Sets Gain value.
     * Linear scale 0.0  <-->  1.0
     * @param gain value from 0.0 to 1.0
     * @throws BasicPlayerException
     */
    public void setGain(double gain) throws BasicPlayerException;
}
