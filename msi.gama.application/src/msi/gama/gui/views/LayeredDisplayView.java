/*********************************************************************************************
 * 
 * 
 * 'LayeredDisplayView.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.views;

import msi.gama.common.GamaPreferences;
import msi.gama.common.interfaces.*;
import msi.gama.common.interfaces.IDisplaySurface.IZoomListener;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.swt.*;
import msi.gama.gui.swt.controls.*;
import msi.gama.gui.views.actions.*;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.outputs.*;
import org.eclipse.core.runtime.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;

public abstract class LayeredDisplayView extends GamaViewPart implements IZoomListener, IToolbarDecoratedView.Pausable, IToolbarDecoratedView.Zoomable {

	protected Composite surfaceComposite;
	protected IPerspectiveListener perspectiveListener;
	protected DisplayOverlay overlay;
	protected Integer zoomLevel = null;
	private final static int PRESENTATION = 0;
	private final static int FOCUS = 1;
	private final static int SNAP = 2;
	protected volatile boolean disposed;

	@Override
	public void init(final IViewSite site) throws PartInitException {
		super.init(site);
		setPartName(output.getViewName());

	}

	@Override
	public Integer[] getToolbarActionsId() {
		return new Integer[] { FOCUS, PRESENTATION, SEP, SNAP };
	}

	public boolean isOpenGL() {
		if ( output == null ) { return false; }
		return getOutput().isOpenGL();
	}

	public ILayerManager getDisplayManager() {
		return getDisplaySurface().getManager();
	}

	public DisplayOverlay getOverlay() {
		return overlay;
	}

	public Composite getSurfaceComposite() {
		return surfaceComposite;
	}

	@Override
	public void ownCreatePartControl(final Composite c) {
		parent = new Composite(c, SWT.NONE);
		GridLayout gl = new GridLayout(1, false);
		gl.horizontalSpacing = 0;
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		gl.verticalSpacing = 0;
		parent.setLayout(gl);
		createSurfaceComposite();
		// createSurface();
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalIndent = 0;
		gd.verticalIndent = 0;
		surfaceComposite.setLayoutData(gd);
		overlay = new DisplayOverlay(this, parent, getOutput().getOverlayProvider());
		// getDisplaySurface().setZoomListener(this);
		getOutput().setSynchronized(GamaPreferences.CORE_SYNC.getValue());
		// getDisplaySurface().setQualityRendering(GamaPreferences.CORE_ANTIALIAS.getValue());
		overlay.update();
		parent.layout();

	}

	@Override
	public void setFocus() {
		surfaceComposite.setFocus();
	}

	protected abstract Composite createSurfaceComposite();

	@Override
	public LayeredDisplayOutput getOutput() {
		return (LayeredDisplayOutput) super.getOutput();
	}

	public IDisplaySurface getDisplaySurface() {
		LayeredDisplayOutput out = getOutput();
		if ( out != null ) { return out.getSurface(); }
		return null;
	}

	@Override
	public void dispose() {
		// FIXME Should not be redefined, but we should add a DisposeListener instead
		if ( disposed ) { return; }
		disposed = true;
		releaseLock();
		if ( updateThread != null ) {
			try {
				updateThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		if ( perspectiveListener != null ) {
			SwtGui.getWindow().removePerspectiveListener(perspectiveListener);
		}
		// FIXME Remove the listeners

		if ( overlay != null ) {
			overlay.close();
		}
		super.dispose();
	}

	@Override
	public void newZoomLevel(final double zoomLevel) {
		this.zoomLevel = (int) (zoomLevel * 100);
		GuiUtils.asyncRun(new Runnable() {

			@Override
			public void run() {
				overlay.update();
			}
		});

	}

	/*
	 * Between 0 and 100;
	 */
	public int getZoomLevel() {
		if ( zoomLevel == null ) {
			if ( getDisplaySurface() != null ) {
				zoomLevel = (int) getDisplaySurface().getZoomLevel() * 100;
			} else {
				zoomLevel = 1;
			}
		}
		return zoomLevel;
	}

	public String getOverlayText() {
		boolean paused = getOutput().isPaused();
		boolean synced = getOutput().getData().isSynchronized();
		boolean openGL = getOutput().isOpenGL();
		ILocation point = getDisplaySurface().getModelCoordinates();
		String x = point == null ? "N/A" : String.format("%8.2f", point.getX());
		String y = point == null ? "N/A" : String.format("%8.2f", point.getY());
		Object[] objects = null;
		if ( !openGL ) {
			objects = new Object[] { x, y, getZoomLevel() };
		} else {
			IDisplaySurface.OpenGL ds = (IDisplaySurface.OpenGL) getDisplaySurface();
			ILocation camera = ds.getCameraPosition();
			objects = new Object[] { x, y, getZoomLevel(), camera.getX(), camera.getY(), camera.getZ() };
		}
		return String.format(" X%10s | Y%10s | Zoom%10d%%" + (paused ? " | Paused" : "") +
			(synced ? " | Synchronized" : "") + (openGL ? " | Camera [%.2f;%.2f;%.2f]" : ""), objects);
	};

	@Override
	public void pauseChanged() {
		overlay.update();
	}

	@Override
	public void synchronizeChanged() {
		overlay.update();
	}

	public double getValueOfOnePixelInModelUnits() {
		IDisplaySurface s = getDisplaySurface();
		if ( s == null ) { return 1; }
		double displayWidth = s.getDisplayWidth();
		double envWidth = s.getEnvWidth();
		return envWidth / displayWidth;
	}

	public void toggleSideBar() {
		LayersOverlay2 layersOverlay = new LayersOverlay2(this, SWT.RESIZE);
		LayersOverlay2 l = layersOverlay;
		l.open();
	}

	public void toggleOverlay() {
		this.overlay.toggle();
	}

	public String getOverlayCoordInfo() {
		IDisplayOutput output = getOutput();
		boolean paused = output.isPaused();
		boolean synced = getOutput().getData().isSynchronized();
		IDisplaySurface surface = getDisplaySurface();
		ILocation point = surface == null ? null : surface.getModelCoordinates();
		String x = point == null ? "N/A" : String.format("%8.2f", point.getX());
		String y = point == null ? "N/A" : String.format("%8.2f", point.getY());
		Object[] objects = new Object[] { x, y };
		return String
			.format("X%10s | Y%10s" + (paused ? " | Paused" : "") + (synced ? " | Synchronized" : ""), objects);

	}

	public String getOverlayZoomInfo() {
		IDisplaySurface surface = getDisplaySurface();
		boolean openGL = getOutput().isOpenGL();

		if ( !openGL ) {
			return "Zoom " + getZoomLevel() + "%";
		} else {
			IDisplaySurface.OpenGL ds = (IDisplaySurface.OpenGL) surface;
			ILocation camera = ds.getCameraPosition();
			return String.format("Zoom %d%% | Camera [%.2f;%.2f;%.2f]", getZoomLevel(), camera.getX(), camera.getY(),
				camera.getZ());
		}
	}

	@Override
	public void createToolItem(final int code, final GamaToolbar2 tb) {
		switch (code) {
			case SNAP:
				tb.button(IGamaIcons.DISPLAY_TOOLBAR_SNAPSHOT.getCode(), "Take a snapshot", "Take a snapshot",
					new SelectionAdapter() {

						@Override
						public void widgetSelected(final SelectionEvent e) {
							getDisplaySurface().snapshot();
						}

					}, SWT.RIGHT);
				break;

			case FOCUS:
				new DisplayedAgentsMenu().createItem(tb, getDisplaySurface(), isOpenGL());
				break;

			case PRESENTATION:
				new PresentationMenu().createItem(tb, this);
				break;

		}
	}

	public void zoom(final int type) {
		getDisplaySurface().waitForUpdateAndRun(new Runnable() {

			@Override
			public void run() {
				switch (type) {
					case -1:
						getDisplaySurface().zoomOut();
						break;
					case 0:
						getDisplaySurface().zoomFit();
						break;
					case 1:
						getDisplaySurface().zoomIn();
						break;
				}
			}
		});
	}

	@Override
	public void zoomIn() {
		zoom(1);
	}

	@Override
	public void zoomOut() {
		zoom(-1);
	}

	@Override
	public void zoomFit() {
		zoom(0);
	}

	@Override
	public Control[] getZoomableControls() {
		return new Control[] { surfaceComposite };
	}

	@Override
	protected GamaUIJob createUpdateJob() {
		return new GamaUIJob() {

			@Override
			protected UpdatePriority jobPriority() {
				return UpdatePriority.HIGHEST;
			}

			@Override
			public IStatus runInUIThread(final IProgressMonitor monitor) {
				getDisplaySurface().updateDisplay(false);
				return Status.OK_STATUS;
			}
		};
	}

	private volatile boolean lockAcquired = false;

	Thread updateThread;

	public synchronized void acquireLock() {
		while (lockAcquired) {
			try {
				wait();
			} catch (final InterruptedException e) {
				// e.printStackTrace();
			}
		}
		lockAcquired = true;
	}

	public synchronized void releaseLock() {
		lockAcquired = false;
		notify();
	}

	@Override
	public void update(final IDisplayOutput output) {
		if ( updateThread == null ) {
			updateThread = new Thread(new Runnable() {

				@Override
				public void run() {
					while (!disposed) {
						acquireLock();
						IDisplaySurface s = getDisplaySurface();
						if ( s != null ) {
							s.updateDisplay(false);
						}
					}
				}
			});
			updateThread.start();
		}
		if ( updateThread.isAlive() ) {
			releaseLock();
		}

	}

	/**
	 * @return
	 */
	protected IDisplaySurface createSurface() {
		return GuiUtils.getDisplaySurfaceFor(getOutput());
	}

}
