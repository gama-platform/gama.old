/*******************************************************************************************************
 *
 * LayeredDisplaySynchronizer.java, in ummisco.gama.ui.experiment, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.8.2).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.views.displays;

import static ummisco.gama.dev.utils.FLAGS.USE_OLD_SYNC_STRATEGY;

import java.util.concurrent.Semaphore;

import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IDisplaySynchronizer;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class LayeredDisplaySynchronizer.
 */
public class LayeredDisplaySynchronizer implements IDisplaySynchronizer {

	static {
		DEBUG.OFF();
	}

	/** The view update lock. */
	Semaphore viewUpdateLock = new Semaphore(0);

	/** The surface render lock. */
	Semaphore surfaceRenderLock = new Semaphore(1);

	/** The surface realisation lock. */
	Semaphore surfaceRealisationLock = new Semaphore(0);

	/** The surface. */
	IDisplaySurface surface;

	/**
	 * Acquire view lock.
	 *
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	private void acquireViewLock() throws InterruptedException {
		if (viewUpdateLock.availablePermits() > 0) { viewUpdateLock.drainPermits(); }
		viewUpdateLock.acquire();
	}

	/**
	 * Release view lock.
	 */
	private void releaseViewLock() {
		viewUpdateLock.release();
	}

	/**
	 * Acquire lock.
	 *
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	private synchronized void acquireLock() throws InterruptedException {
		wait();
	}

	/**
	 * Release lock.
	 */
	private synchronized void releaseLock() {
		notify();
	}

	@Override
	public void waitForViewUpdateAuthorisation() {
		DEBUG.OUT("Waiting for view to update: " + Thread.currentThread().getName());
		try {
			if (USE_OLD_SYNC_STRATEGY) {
				acquireLock();
			} else {
				acquireViewLock();
			}
		} catch (InterruptedException e) {}
	}

	@Override
	public void authorizeViewUpdate() {
		DEBUG.OUT("Signalling that view can be updated: " + Thread.currentThread().getName());
		if (USE_OLD_SYNC_STRATEGY) {
			releaseLock();
		} else {
			releaseViewLock();
		}
	}

	@Override
	public void waitForRenderingToBeFinished() {
		DEBUG.OUT("Waiting for surface to be rendered: " + Thread.currentThread().getName());
		try {
			if (USE_OLD_SYNC_STRATEGY) {
				while (!surface.isRendered() && !surface.isDisposed()) { Thread.sleep(1); }
			} else {
				if (surfaceRenderLock.availablePermits() > 0) { surfaceRenderLock.drainPermits(); }
				surfaceRenderLock.acquire();
			}
		} catch (final InterruptedException e) {}

	}

	@Override
	public void signalRenderingIsFinished() {
		DEBUG.OUT("Signalling that surface is rendered: " + Thread.currentThread().getName());
		surfaceRenderLock.release();
	}

	/**
	 * Sets the surface.
	 *
	 * @param surface
	 *            the new surface
	 */
	public void setSurface(final IDisplaySurface surface) {
		this.surface = surface;
		if (surface != null) { surface.setDisplaySynchronizer(this); }

	}

	@Override
	public void waitForSurfaceToBeRealized() {
		DEBUG.OUT("Waiting for surface to realize: " + Thread.currentThread().getName());
		try {
			surfaceRealisationLock.acquire();
		} catch (InterruptedException e) {}
	}

	@Override
	public void signalSurfaceIsRealized() {
		DEBUG.OUT("Signalling that surface is realized: " + Thread.currentThread().getName());
		surfaceRealisationLock.release();
	}

}
