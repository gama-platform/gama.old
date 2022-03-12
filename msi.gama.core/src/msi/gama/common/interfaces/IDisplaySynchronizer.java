/*******************************************************************************************************
 *
 * IDisplaySynchronizer.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.common.interfaces;

/**
 * A simple object that can synchronize the 3 threads in charge of the drawing of the displays: simulation thread
 * (simulation-view update mechanism), update thread, and rendering thread
 *
 * @author drogoul
 *
 */
public interface IDisplaySynchronizer {
	/**
	 * Allows any object calling this method to release the thread waiting for the scene to be rendered (called by the
	 * rendering processes or when this surface is disposed). Nothing to do by default
	 */
	void signalRenderingIsFinished();

	/**
	 * Makes any thread calling this method wait until either the scene is rendered or the surface is disposed
	 */
	void waitForRenderingToBeFinished();

	/**
	 * Allows any object calling this method to release the thread waiting for the view to be updated
	 */
	void authorizeViewUpdate();

	/**
	 * Makes any thread calling this method wait until can be updated
	 */
	void waitForViewUpdateAuthorisation();

	/**
	 * Wait for surface to be realized.
	 */
	void waitForSurfaceToBeRealized();

	/**
	 * Signal surface is realized.
	 */
	void signalSurfaceIsRealized();

	/**
	 * Sets the surface.
	 *
	 * @param displaySurface
	 *            the new surface
	 */
	void setSurface(IDisplaySurface displaySurface);

}
