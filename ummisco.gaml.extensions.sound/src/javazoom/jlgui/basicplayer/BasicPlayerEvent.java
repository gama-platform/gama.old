/*******************************************************************************************************
 *
 * BasicPlayerEvent.java, in ummisco.gaml.extensions.sound, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package javazoom.jlgui.basicplayer;

/**
 * This class implements player events. 
 */
public class BasicPlayerEvent
{
    
    /** The Constant UNKNOWN. */
    public static final int UNKNOWN = -1;
    
    /** The Constant OPENING. */
    public static final int OPENING = 0;
    
    /** The Constant OPENED. */
    public static final int OPENED = 1;
    
    /** The Constant PLAYING. */
    public static final int PLAYING = 2;
    
    /** The Constant STOPPED. */
    public static final int STOPPED = 3;
    
    /** The Constant PAUSED. */
    public static final int PAUSED = 4;
    
    /** The Constant RESUMED. */
    public static final int RESUMED = 5;
    
    /** The Constant SEEKING. */
    public static final int SEEKING = 6;
    
    /** The Constant SEEKED. */
    public static final int SEEKED = 7;
    
    /** The Constant EOM. */
    public static final int EOM = 8;
    
    /** The Constant PAN. */
    public static final int PAN = 9;
    
    /** The Constant GAIN. */
    public static final int GAIN = 10;
    
    /** The code. */
    private int code = UNKNOWN;
    
    /** The position. */
    private int position = -1;
    
    /** The value. */
    private double value = -1.0;
    
    /** The source. */
    private Object source = null;
    
    /** The description. */
    private Object description = null;

    /**
     * Constructor
     * @param source of the event
     * @param code of the envent
     * @param position optional stream position
     * @param value opitional control value
     * @param desc optional description
     */
    public BasicPlayerEvent(Object source, int code, int position, double value, Object desc)
    {
        this.value = value;
        this.position = position;
        this.source = source;
        this.code = code;
        this.description = desc;
    }

    /**
     * Return code of the event triggered.
     * @return
     */
    public int getCode()
    {
        return code;
    }

    /**
     * Return position in the stream when event occured.
     * @return
     */
    public int getPosition()
    {
        return position;
    }

    /**
     * Return value related to event triggered. 
     * @return
     */
    public double getValue()
    {
        return value;
    }

    /**
     * Return description.
     * @return
     */
    public Object getDescription()
    {
        return description;
    }

    /**
     * Gets the source.
     *
     * @return the source
     */
    public Object getSource()
    {
        return source;
    }

    public String toString()
    {
        if (code == OPENED) return "OPENED:" + position;
        else if (code == OPENING) return "OPENING:" + position + ":" + description;
        else if (code == PLAYING) return "PLAYING:" + position;
        else if (code == STOPPED) return "STOPPED:" + position;
        else if (code == PAUSED) return "PAUSED:" + position;
        else if (code == RESUMED) return "RESUMED:" + position;
        else if (code == SEEKING) return "SEEKING:" + position;
        else if (code == SEEKED) return "SEEKED:" + position;
        else if (code == EOM) return "EOM:" + position;
        else if (code == PAN) return "PAN:" + value;
        else if (code == GAIN) return "GAIN:" + value;
        else return "UNKNOWN:" + position;
    }
}
