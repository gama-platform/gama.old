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
import msi.gama.outputs.LayeredDisplayOutput;
import msi.gaml.descriptions.IDescription;
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
	private final static int SYNC = 3;

	@Override
	public void init(final IViewSite site) throws PartInitException {
		super.init(site);
		setPartName(output.getViewName());

	}

	@Override
	public Integer[] getToolbarActionsId() {
		return new Integer[] { SYNC, SEP, FOCUS, PRESENTATION, SEP, SNAP };
	}

	public boolean isOpenGL() {
		if ( output == null ) { return false; }
		IDescription description = output.getDescription();
		return description.getFacets().equals(IKeyword.TYPE, "opengl") ||
			description.getFacets().equals(IKeyword.TYPE, "3D");
	}

	public ILayerManager getDisplayManager() {
		return getOutput().getSurface().getManager();
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
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalIndent = 0;
		gd.verticalIndent = 0;
		surfaceComposite.setLayoutData(gd);
		overlay = new DisplayOverlay(this, parent, getOutput().getOverlayProvider());
		getDisplaySurface().setZoomListener(this);
		getDisplaySurface().setSynchronized(GamaPreferences.CORE_SYNC.getValue());
		getOutput().getSurface().setQualityRendering(GamaPreferences.CORE_ANTIALIAS.getValue());
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
		return getOutput().getSurface();
	}

	@Override
	public void dispose() {
		// FIXME Should not be redefined, but we should add a DisposeListener instead
		SwtGui.getWindow().removePerspectiveListener(perspectiveListener);
		// FIXME Remove the listeners
		surfaceComposite.dispose();
		if ( overlay != null ) {
			overlay.close();
		}
		super.dispose();
	}

	@Override
	public void newZoomLevel(final double zoomLevel) {
		this.zoomLevel = (int) (zoomLevel * 100);
		GuiUtils.run(new Runnable() {

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
		if ( zoomLevel == null && getOutput().getSurface() != null ) {
			zoomLevel = (int) getOutput().getSurface().getZoomLevel() * 100;
		}
		return zoomLevel;
	}

	public String getOverlayText() {
		IDisplaySurface surface = getOutput().getSurface();
		boolean paused = surface.isPaused();
		boolean synced = surface.isSynchronized();
		boolean openGL = getOutput().isOpenGL();
		ILocation point = surface.getModelCoordinates();
		String x = point == null ? "N/A" : String.format("%8.2f", point.getX());
		String y = point == null ? "N/A" : String.format("%8.2f", point.getY());
		Object[] objects = null;
		if ( !openGL ) {
			objects = new Object[] { x, y, getZoomLevel() };
		} else {
			IDisplaySurface.OpenGL ds = (IDisplaySurface.OpenGL) surface;
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

	public double getValueOfOnePixelInModelUnits() {
		double displayWidth = getOutput().getSurface().getDisplayWidth();
		double envWidth = getOutput().getSurface().getEnvWidth();
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
		IDisplaySurface surface = getOutput().getSurface();
		boolean paused = surface.isPaused();
		boolean synced = surface.isSynchronized();
		ILocation point = surface.getModelCoordinates();
		String x = point == null ? "N/A" : String.format("%8.2f", point.getX());
		String y = point == null ? "N/A" : String.format("%8.2f", point.getY());
		Object[] objects = new Object[] { x, y };
		return String
			.format("X%10s | Y%10s" + (paused ? " | Paused" : "") + (synced ? " | Synchronized" : ""), objects);

	}

	public String getOverlayZoomInfo() {
		IDisplaySurface surface = getOutput().getSurface();
		boolean openGL = getOutput().isOpenGL();
		Object[] objects = null;
		if ( !openGL ) {
			objects = new Object[] { getZoomLevel() };
		} else {
			IDisplaySurface.OpenGL ds = (IDisplaySurface.OpenGL) surface;
			ILocation camera = ds.getCameraPosition();
			objects = new Object[] { getZoomLevel(), camera.getX(), camera.getY(), camera.getZ() };
		}
		return String.format("Zoom %d%%" + (openGL ? " | Camera [%.2f;%.2f;%.2f]" : ""), objects);

	}

	@Override
	public void createToolItem(final int code, final GamaToolbarSimple tb) {
		switch (code) {
			case SNAP:
				tb.button(IGamaIcons.DISPLAY_TOOLBAR_SNAPSHOT.getCode(), "Take a snapshot", "Take a snapshot",
					new SelectionAdapter() {

						@Override
						public void widgetSelected(final SelectionEvent e) {
							getDisplaySurface().snapshot();
						}

					});
				break;

			case FOCUS:
				new DisplayedAgentsMenu().createItem(tb, this);
				break;

			case SYNC:
				tb.check(IGamaIcons.DISPLAY_TOOLBAR_SYNC.getCode(), "Synchronize with simulation", "Synchronize",
					new SelectionAdapter() {

						@Override
						public void widgetSelected(final SelectionEvent e) {
							getDisplaySurface().setSynchronized(((ToolItem) e.widget).getSelection());
							overlay.update();
						}

					});
				break;
			case PRESENTATION:
				new PresentationMenu().createItem(tb, this);
				break;

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
				new DisplayedAgentsMenu().createItem(tb, this);
				break;

			case SYNC:
				tb.check(IGamaIcons.DISPLAY_TOOLBAR_SYNC.getCode(), "Synchronize with simulation", "Synchronize",
					new SelectionAdapter() {

						@Override
						public void widgetSelected(final SelectionEvent e) {
							getDisplaySurface().setSynchronized(((ToolItem) e.widget).getSelection());
							overlay.update();
						}

					}, SWT.RIGHT);
				break;
			case PRESENTATION:
				new PresentationMenu().createItem(tb, this);
				break;

		}
	}

	public void zoom(final int type) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				final IDisplaySurface surface = getDisplaySurface();
				while (!surface.canBeUpdated()) {
					try {
						Thread.sleep(10);
					} catch (final InterruptedException e) {

					}
				}
				switch (type) {
					case -1:
						surface.zoomOut();
						break;
					case 0:
						surface.zoomFit();
						break;
					case 1:
						surface.zoomIn();
						break;
				}
			}
		}).start();
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

}
