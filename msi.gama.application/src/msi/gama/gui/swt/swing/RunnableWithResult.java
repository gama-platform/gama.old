/*********************************************************************************************
 * 
 *
 * 'RunnableWithResult.java', in plugin 'msi.gama.application', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.swt.swing;

/**
 * A Runnable that produces a result, but nevertheless can be used as a
 * {@link Runnable}.
 */
public abstract class RunnableWithResult implements Runnable {
    private Object result;

    /**
     * Executes the user-defined code.
     * It should call {@link #setResult} to assign a result.
     */
    abstract public void run();

    /**
     * Returns the result.
     * This method can be called after {@link run()} was executed.
     */
    public Object getResult() {
        return result;
    }

    /**
     * Assigns a result. This method should be called once during
     * {@link #run}.
     */
    protected void setResult(Object result) {
        this.result = result;
    }

}
