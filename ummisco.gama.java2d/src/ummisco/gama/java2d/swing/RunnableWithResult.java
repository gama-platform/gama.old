/*********************************************************************************************
 *
 * 'RunnableWithResult.java, in plugin ummisco.gama.java2d, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.java2d.swing;

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
	@Override
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
	protected void setResult(final Object result) {
		this.result = result;
	}

}
