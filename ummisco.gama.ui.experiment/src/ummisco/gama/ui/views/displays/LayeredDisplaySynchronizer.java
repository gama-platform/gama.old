/*******************************************************************************************************
 *
 * LayeredDisplaySynchronizer.java, in ummisco.gama.ui.experiment, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.views.displays;

import java.util.concurrent.ArrayBlockingQueue;

import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IDisplaySynchronizer;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class LayeredDisplaySynchronizer.
 */
public class LayeredDisplaySynchronizer implements IDisplaySynchronizer {

	/** The Constant TOKEN. */
	static final Integer TOKEN = 0;

	static {
		DEBUG.OFF();
	}

	/** The queues. */
	private final ArrayBlockingQueue<Integer> realisationQueue = new ArrayBlockingQueue<>(1),
			updateQueue = new ArrayBlockingQueue<>(1), renderQueue = new ArrayBlockingQueue<>(1);

	/** The surface. */
	IDisplaySurface surface;

	/**
	 * Sets the surface.
	 *
	 * @param surface
	 *            the new surface
	 */
	@Override
	public void setSurface(final IDisplaySurface surface) {
		this.surface = surface;
		if (surface != null) { surface.setDisplaySynchronizer(this); }

	}

	@Override
	public void waitForSurfaceToBeRealized() {
		// DEBUG.OUT("Waiting for surface to realize: " + Thread.currentThread().getName());
		try {
			realisationQueue.take();
		} catch (InterruptedException e) {}
	}

	@Override
	public void signalSurfaceIsRealized() {
		// DEBUG.OUT("Signalling that surface is realized: " + Thread.currentThread().getName());
		realisationQueue.offer(TOKEN);
	}

	@Override
	public void signalRenderingIsFinished() {
		// DEBUG.OUT("Signalling that surface is rendered: " + Thread.currentThread().getName());
		renderQueue.offer(TOKEN);
	}

	@Override
	public void waitForRenderingToBeFinished() {
		// DEBUG.OUT("Waiting for surface to be rendered: " + Thread.currentThread().getName());
		try {
			renderQueue.take();
		} catch (final InterruptedException e) {}
	}

	@Override
	public void waitForViewUpdateAuthorisation() {
		// DEBUG.OUT("Waiting for view to update: " + Thread.currentThread().getName());
		try {
			updateQueue.take();
		} catch (InterruptedException e) {}
	}

	@Override
	public void authorizeViewUpdate() {
		// DEBUG.OUT("Signalling that view can be updated: " + Thread.currentThread().getName());
		updateQueue.offer(TOKEN);
	}

}
