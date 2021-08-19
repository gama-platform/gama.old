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
	 * rendering processes or when this surface is disposed)
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

	void waitForSurfaceToBeRealized();

	void signalSurfaceIsRealized();

}
