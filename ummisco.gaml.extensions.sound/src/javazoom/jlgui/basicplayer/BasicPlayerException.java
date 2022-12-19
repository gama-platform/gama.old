/*******************************************************************************************************
 *
 * BasicPlayerException.java, in ummisco.gaml.extensions.sound, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package javazoom.jlgui.basicplayer;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * This class implements custom exception for basicplayer.
 */
public class BasicPlayerException extends Exception
{
    
    /** The Constant GAINCONTROLNOTSUPPORTED. */
    public static final String GAINCONTROLNOTSUPPORTED = "Gain control not supported";
    
    /** The Constant PANCONTROLNOTSUPPORTED. */
    public static final String PANCONTROLNOTSUPPORTED = "Pan control not supported";
    
    /** The Constant WAITERROR. */
    public static final String WAITERROR = "Wait error";
    
    /** The Constant CANNOTINITLINE. */
    public static final String CANNOTINITLINE = "Cannot init line";
    
    /** The Constant SKIPNOTSUPPORTED. */
    public static final String SKIPNOTSUPPORTED = "Skip not supported";
    
    /** The cause. */
    private Throwable cause = null;

    /**
     * Instantiates a new basic player exception.
     */
    public BasicPlayerException()
    {
        super();
    }

    /**
     * Instantiates a new basic player exception.
     *
     * @param msg the msg
     */
    public BasicPlayerException(String msg)
    {
        super(msg);
    }

    /**
     * Instantiates a new basic player exception.
     *
     * @param cause the cause
     */
    public BasicPlayerException(Throwable cause)
    {
        super();
        this.cause = cause;
    }

    /**
     * Instantiates a new basic player exception.
     *
     * @param msg the msg
     * @param cause the cause
     */
    public BasicPlayerException(String msg, Throwable cause)
    {
        super(msg);
        this.cause = cause;
    }

    public Throwable getCause()
    {
        return cause;
    }

    /**
     * Returns the detail message string of this throwable. If it was
     * created with a null message, returns the following:
     * (cause==null ? null : cause.toString()).
     */
    public String getMessage()
    {
        if (super.getMessage() != null)
        {
            return super.getMessage();
        }
        else if (cause != null)
        {
            return cause.toString();
        }
        else
        {
            return null;
        }
    }

    public void printStackTrace()
    {
        printStackTrace(System.err);
    }

    public void printStackTrace(PrintStream out)
    {
        synchronized (out)
        {
            PrintWriter pw = new PrintWriter(out, false);
            printStackTrace(pw);
            pw.flush();
        }
    }

    public void printStackTrace(PrintWriter out)
    {
        if (cause != null) cause.printStackTrace(out);
    }
}
