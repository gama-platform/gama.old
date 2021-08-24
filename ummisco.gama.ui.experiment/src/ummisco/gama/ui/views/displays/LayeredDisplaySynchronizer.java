package ummisco.gama.ui.views.displays;

import static ummisco.gama.dev.utils.FLAGS.USE_OLD_SYNC_STRATEGY;

import java.util.concurrent.Semaphore;

import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IDisplaySynchronizer;
import ummisco.gama.dev.utils.DEBUG;

public class LayeredDisplaySynchronizer implements IDisplaySynchronizer {

	static {
		DEBUG.OFF();
	}

	Semaphore viewUpdateLock = new Semaphore(0);
	Semaphore surfaceRenderLock = new Semaphore(1);
	Semaphore surfaceRealisationLock = new Semaphore(0);
	IDisplaySurface surface;

	private void acquireViewLock() throws InterruptedException {
		if (viewUpdateLock.availablePermits() > 0) { viewUpdateLock.drainPermits(); }
		viewUpdateLock.acquire();
	}

	private void releaseViewLock() {
		viewUpdateLock.release();
	}

	private synchronized void acquireLock() throws InterruptedException {
		wait();
	}

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
				while (!surface.isRendered() && !surface.isDisposed()) {
					Thread.sleep(10);
				}
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

	public void setSurface(final IDisplaySurface surface) {
		this.surface = surface;
		surface.setDisplaySynchronizer(this);

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
