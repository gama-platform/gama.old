/*******************************************************************************
 * Copyright (c) 2007-2008 SAS Institute Inc., ILOG S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * SAS Institute Inc. - initial API and implementation
 * ILOG S.A. - initial API and implementation
 *******************************************************************************/
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
