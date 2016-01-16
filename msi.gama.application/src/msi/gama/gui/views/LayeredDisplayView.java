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

import org.eclipse.core.runtime.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import msi.gama.common.GamaPreferences;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.displays.layers.LayerSideControls;
import msi.gama.gui.swt.*;
import msi.gama.gui.swt.controls.*;
import msi.gama.gui.views.actions.*;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.outputs.*;
import msi.gama.outputs.LayeredDisplayData.*;
import msi.gama.outputs.layers.AbstractLayer;

public abstract class LayeredDisplayView extends GamaViewPart implements DisplayDataListener, IToolbarDecoratedView.Pausable, IToolbarDecoratedView.Zoomable {

	protected SashForm form;
	protected Composite surfaceComposite;
	protected Composite layersPanel;
	protected IPerspectiveListener perspectiveListener;
	protected DisplayOverlay overlay;
	// protected Integer zoomLevel = null;
	protected volatile boolean disposed;
	protected volatile boolean realized = false;
	protected ToolItem overlayItem;

	@Override
	public void init(final IViewSite site) throws PartInitException {
		super.init(site);
		if ( getOutput() != null ) {
			setPartName(getOutput().getName());
		}

	}

	public boolean isOpenGL() {
		if ( outputs.isEmpty() ) { return false; }
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
		if ( getOutput() == null ) { return; }

		Cursor cr = new Cursor(SwtGui.getDisplay(), SWT.CURSOR_WAIT);
		c.setCursor(cr);
		// First create the sashform

		form = new SashForm(c, SWT.HORIZONTAL);
		form.setLayout(new FillLayout());
		form.setBackground(IGamaColors.WHITE.color());
		form.setSashWidth(3);

		layersPanel = new Composite(form, SWT.NONE);
		GridLayout layout = new GridLayout(1, true);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layersPanel.setLayout(layout);
		layersPanel.setBackground(IGamaColors.WHITE.color());

		parent = new Composite(form, SWT.NONE);
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
		getOutput().setSynchronized(GamaPreferences.CORE_SYNC.getValue());
		getOutput().getData().addListener(this);
		// getDisplaySurface().setZoomListener(this);
		overlay.update();
		overlay.setVisible(GamaPreferences.CORE_OVERLAY.getValue());
		parent.layout();

		// Create after the surface composite

		fillLayerSideControls(layersPanel);

		// form.setWeights(new int[] { 30, 70 });
		form.setMaximizedControl(parent);

		c.setCursor(null);
		cr.dispose();

	}

	@Override
	public void setFocus() {
		// if ( surfaceComposite != null ) {
		// surfaceComposite.setFocus();
		// }
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
		if ( getOutput() != null ) {
			getOutput().getData().removeListener(this);
		}
		disposed = true;
		if ( surfaceComposite != null ) {
			surfaceComposite.dispose();
		}
		IDisplaySurface s = getDisplaySurface();
		if ( s != null ) {
			s.releaseLock();
		}
		if ( updateThread != null ) {
			updateThread.interrupt();
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
	public void changed(final Changes changes, final boolean value) {
		switch (changes) {
			case ZOOM:
				GuiUtils.asyncRun(new Runnable() {

					@Override
					public void run() {
						overlay.update();
					}
				});
				break;
			case BACKGROUND:
				break;
			case CAMERA_POS:
				break;
			case CHANGE_CAMERA:
				break;
			case HIGHLIGHT:
				break;
			case SPLIT_LAYER:
				break;
			case THREED_VIEW:
				break;
			default:
				break;
		}
		// this.zoomLevel = (int) (zoomLevel * 100);
		// GuiUtils.asyncRun(new Runnable() {
		//
		// @Override
		// public void run() {
		// overlay.update();
		// }
		// });

	}

	/*
	 * Between 0 and 100;
	 */
	public int getZoomLevel() {
		Double dataZoom = getOutput().getData().getZoomLevel();
		if ( dataZoom == null ) {
			return 1;
		} else {
			return (int) (dataZoom * 100);
		}
	}

	public String getOverlayText() {
		boolean paused = getOutput().isPaused();
		boolean synced = getOutput().getData().isSynchronized();
		boolean openGL = isOpenGL();
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

	public String getOverlayCoordInfo() {
		IDisplayOutput output = getOutput();
		if ( output == null ) { return ""; }
		boolean paused = output.isPaused();
		boolean synced = getOutput().getData().isSynchronized();
		IDisplaySurface surface = getDisplaySurface();
		ILocation point = surface == null ? null : surface.getModelCoordinates();
		String x = point == null ? "N/A" : String.format("%8.2f", point.getX());
		String y = point == null ? "N/A" : String.format("%8.2f", point.getY());
		Object[] objects = new Object[] { x, y };
		return String.format("X%10s | Y%10s" + (paused ? " | Paused" : "") + (synced ? " | Synchronized" : ""),
			objects);

	}

	public String getOverlayZoomInfo() {
		IDisplaySurface surface = getDisplaySurface();
		boolean openGL = isOpenGL();

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
	public void createToolItems(final GamaToolbar2 tb) {
		super.createToolItems(tb);
		// { LAYER_CONTROLS, INSPECT_AGENTS, OPENGL, SEP, SNAPSHOT };

		tb.check("display.layers2", "Toggle layers controls", "Toggle layers controls", new SelectionAdapter() {

			int[] weights = new int[] { 30, 70 };
			boolean visible = false; // TODO Make it a preference

			@Override
			public void widgetSelected(final SelectionEvent e) {
				if ( visible ) {
					weights = form.getWeights();
					form.setMaximizedControl(parent);
					visible = false;
				} else {
					form.setWeights(weights);
					form.setMaximizedControl(null);
					visible = true;
				}
			}

		}, SWT.LEFT);
		overlayItem = tb.check("display.overlay2", "Toggle overlay", "Toggle overlay", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				overlay.setVisible(overlayItem.getSelection());
			}

		}, SWT.LEFT);
		overlayItem.setSelection(GamaPreferences.CORE_OVERLAY.getValue());
		tb.sep(GamaToolbarFactory.TOOLBAR_SEP, SWT.LEFT);
		new DisplayedAgentsMenu().createItem(tb, getDisplaySurface(), isOpenGL());
		if ( isOpenGL() ) {
			new PresentationMenu().createItem(tb, this);
		}
		tb.sep(GamaToolbarFactory.TOOLBAR_SEP, SWT.RIGHT);
		tb.button(IGamaIcons.DISPLAY_TOOLBAR_SNAPSHOT.getCode(), "Take a snapshot", "Take a snapshot",
			new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent e) {
					getDisplaySurface().snapshot();
				}

			}, SWT.RIGHT);
	}

	@Override
	public void zoomIn() {
		getDisplaySurface().zoomIn();
	}

	@Override
	public void zoomOut() {
		getDisplaySurface().zoomOut();
	}

	@Override
	public void zoomFit() {
		getDisplaySurface().zoomFit();
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
				if ( getDisplaySurface() == null ) { return Status.CANCEL_STATUS; }
				getDisplaySurface().updateDisplay(false);
				return Status.OK_STATUS;
			}
		};
	}

	Thread updateThread;

	@Override
	public void update(final IDisplayOutput output) {
		if ( updateThread == null ) {
			updateThread = new Thread(new Runnable() {

				@Override
				public void run() {
					while (!disposed) {
						IDisplaySurface s = getDisplaySurface();
						if ( s != null ) {
							s.acquireLock();
							s.updateDisplay(false);
						}
					}
				}
			});
			updateThread.start();
		}
		if ( updateThread.isAlive() ) {
			IDisplaySurface s = getDisplaySurface();
			if ( s != null ) {
				s.releaseLock();
			}
		}

	}

	public Composite fillLayerSideControls(final Composite parent) {
		//

		Composite compo = new Composite(parent, SWT.NONE);
		compo.setBackground(IGamaColors.WHITE.color());
		GridData data = new GridData(SWT.FILL, SWT.TOP, true, false);
		compo.setLayoutData(data);
		GridLayout layout = new GridLayout(2, false);
		layout.verticalSpacing = 0;
		compo.setLayout(layout);
		LayerSideControls.fill(compo, getDisplaySurface());

		final Composite content = new Composite(parent, SWT.None);
		content.setBackground(IGamaColors.WHITE.color());
		data = new GridData(SWT.FILL, SWT.FILL, true, true);
		content.setLayoutData(data);
		content.setLayout(new GridLayout());
		ItemList<ILayer> list = getDisplayManager();
		ParameterExpandBar viewer = new ParameterExpandBar(content, SWT.V_SCROLL, false, false, true, true, list);
		data = new GridData(SWT.FILL, SWT.FILL, true, true);
		viewer.setLayoutData(data);
		viewer.setSpacing(5);

		for ( ILayer layer : list.getItems() ) {
			String name = "Layer " + layer.getName();
			compo = new Composite(viewer, SWT.NONE);
			compo.setBackground(IGamaColors.WHITE.color());
			layout = new GridLayout(2, false);
			layout.verticalSpacing = 0;
			compo.setLayout(layout);
			if ( layer instanceof AbstractLayer ) {
				LayerSideControls.fill(compo, layer, getDisplaySurface());
			}
			Composite control = compo;
			ParameterExpandItem i = new ParameterExpandItem(viewer, layer, SWT.None, null);
			if ( name != null ) {
				i.setText(name);
			}
			control.pack(true);
			control.layout();
			i.setControl(control);
			i.setHeight(control.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
			i.setExpanded(false);
		}
		content.layout();
		viewer.addListener(SWT.Collapse, new Listener() {

			@Override
			public void handleEvent(final Event e) {
				content.redraw();
			}

		});
		viewer.addListener(SWT.Expand, new Listener() {

			@Override
			public void handleEvent(final Event e) {
				content.redraw();
			}

		});
		parent.layout();
		return content;

	}

	/**
	 * Ensures that the overlay tool item is coherent with the state of the overlay
	 * @param hidden
	 */
	public void overlayChanged() {
		overlayItem.setSelection(overlay.isVisible());
	}

	public boolean isRealized() {
		return realized;
	}

	public void setRealized(final boolean r) {
		realized = r;
	}

}
