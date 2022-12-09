/*******************************************************************************************************
 *
 * IOpenGLInitializer.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.interfaces;

/**
 * The Interface IOpenGLInitializer.
 */
public interface IOpenGLInitializer extends Runnable {

	/**
	 * Run.
	 */
	@Override
	void run();

	/**
	 * Checks if is done.
	 *
	 * @return true, if is done
	 */
	boolean isDone();

}