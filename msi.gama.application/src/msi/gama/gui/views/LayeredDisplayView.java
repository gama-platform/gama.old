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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import org.eclipse.core.runtime.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.*;
import msi.gama.common.GamaPreferences;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.*;
import msi.gama.gui.displays.layers.LayerSideControls;
import msi.gama.gui.swt.*;
import msi.gama.gui.swt.controls.*;
import msi.gama.gui.views.actions.*;
import msi.gama.kernel.experiment.ITopLevelAgent;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.outputs.*;
import msi.gama.outputs.LayeredDisplayData.*;
import msi.gama.outputs.layers.AbstractLayer;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.operators.Files;

public abstract class LayeredDisplayView extends GamaViewPart implements DisplayDataListener, IToolbarDecoratedView.Pausable, IToolbarDecoratedView.Zoomable {

	protected SashForm form;
	protected Composite surfaceComposite;
	protected Composite layersPanel;
	protected IPerspectiveListener perspectiveListener;
	public DisplayOverlay overlay;
	protected volatile boolean disposed;
	protected volatile boolean realized = false;
	protected ToolItem overlayItem;
	protected final java.awt.Rectangle surfaceCompositeBounds = new java.awt.Rectangle();

	// private LayeredDisplayKeyboardListener globalKeyboardListener;

	@Override
	public void init(final IViewSite site) throws PartInitException {
		super.init(site);
		if ( getOutput() != null ) {
			setPartName(getOutput().getName());
		}

	}

	@Override
	public void addOutput(final IDisplayOutput out) {
		super.addOutput(out);
		if ( out != null ) {
			IScope scope = out.getScope();

			if ( scope != null && scope.getSimulationScope() != null ) {
				ITopLevelAgent root = scope.getRoot();
				Color color = root.getColor();
				// String name = root.getClass().getSimpleName() + root.getIndex();
				this.setTitleImage(GamaIcons.createTempColorIcon(GamaColors.get(color)));
			}
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

		surfaceComposite.addControlListener(new ControlAdapter() {

			@Override
			public void controlResized(final ControlEvent e) {
				Rectangle r = Display.getCurrent().map(surfaceComposite, null, surfaceComposite.getBounds());
				surfaceCompositeBounds.setBounds(r.x, r.y, r.width, r.height);
			}
		});

		// surfaceComposite.addFocusListener(new FocusAdapter() {
		//
		// /**
		// * Method focusGained()
		// * @see org.eclipse.swt.events.FocusAdapter#focusGained(org.eclipse.swt.events.FocusEvent)
		// */
		// @Override
		// public void focusGained(final FocusEvent e) {
		// System.out.println("Focus gained for display. Attaching a listener ");
		// SwtGui.getDisplay().addFilter(SWT.KeyUp, getGlobalKeyboardListener());
		// SwtGui.getDisplay().addFilter(SWT.KeyDown, getGlobalKeyboardListener());
		// super.focusGained(e);
		// }
		//
		// /**
		// * Method focusLost()
		// * @see org.eclipse.swt.events.FocusAdapter#focusLost(org.eclipse.swt.events.FocusEvent)
		// */
		// @Override
		// public void focusLost(final FocusEvent e) {
		// System.out.println("Focus lost for display");
		// SwtGui.getDisplay().removeFilter(SWT.KeyUp, getGlobalKeyboardListener());
		// SwtGui.getDisplay().removeFilter(SWT.KeyDown, getGlobalKeyboardListener());
		// super.focusLost(e);
		// }
		//
		// });
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalIndent = 0;
		gd.verticalIndent = 0;
		surfaceComposite.setLayoutData(gd);
		overlay = new DisplayOverlay(this, parent, getOutput().getOverlayProvider());
		getOutput().setSynchronized(GamaPreferences.CORE_SYNC.getValue());
		getOutput().getData().addListener(this);
		overlay.setVisible(GamaPreferences.CORE_OVERLAY.getValue());
		if ( overlay.isVisible() ) {
			overlay.update();
		}
		// parent.setLayoutDeferred(false);


		// Create after the surface composite

		fillLayerSideControls(layersPanel);

		// form.setWeights(new int[] { 30, 70 });
		form.setMaximizedControl(parent);
		c.layout();

		// c.setCursor(null);
		// cr.dispose();

	}

	// LayeredDisplayKeyboardListener getGlobalKeyboardListener() {
	// if ( globalKeyboardListener == null && getDisplaySurface() != null ) {
	// globalKeyboardListener = new LayeredDisplayKeyboardListener(getDisplaySurface());
	// }
	// return globalKeyboardListener;
	// }

	@Override
	public void setFocus() {
		if ( surfaceComposite != null ) {
			surfaceComposite.setFocus();
		}
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
			releaseLock();
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
				GAMA.getGui().asyncRun(new Runnable() {

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

	}

	/*
	 * Between 0 and 100;
	 */
	public int getZoomLevel() {
		if ( getOutput() == null ) { return 0; }
		Double dataZoom = getOutput().getData().getZoomLevel();
		if ( dataZoom == null ) {
			return 1;
		} else {
			return (int) (dataZoom * 100);
		}
	}


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
		String point = surface == null ? null : surface.getModelCoordinatesInfo();
		return point + (paused ? " | Paused" : "") + (synced ? " | Synchronized" : "");
	}

	public String getOverlayZoomInfo() {
		IDisplaySurface surface = getDisplaySurface();
		if ( surface == null ) { return ""; }
		boolean openGL = isOpenGL();
		String result = GamaPreferences.CORE_SHOW_FPS.getValue() ? String.valueOf(surface.getFPS()) + " fps | " : "";
		if ( !openGL ) {
			return result + "Zoom " + getZoomLevel() + "%";
		} else {
			IDisplaySurface.OpenGL ds = (IDisplaySurface.OpenGL) surface;
			ILocation camera = ds.getCameraPosition();
			return result + String.format("Zoom %d%% | Camera [%.2f;%.2f;%.2f]", getZoomLevel(), camera.getX(),
				camera.getY(), camera.getZ()/*, camera.getTheta(), camera.getPhi()*/);
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
				doSnapshot();
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
		final IDisplaySurface s = getDisplaySurface();
		if ( updateThread == null ) {
			updateThread = new Thread(new Runnable() {

				@Override
				public void run() {
					if ( s != null && !s.isDisposed() && !disposed ) {
						s.updateDisplay(false);
					}
					while (!disposed) {
						acquireLock();
						if ( s != null && s.isRealized() && !s.isDisposed() && !disposed ) {
							if (  s.getData().isAutosave() ) {
								doSnapshot();
							}
							s.updateDisplay(false);
						}
					}
				}
			});
			updateThread.start();
		}

		if ( output.isSynchronized() ) {
			s.updateDisplay(false);
			if ( getOutput().getData().isAutosave() && s.isRealized() ) {
				doSnapshot();
			}
			while (!s.isRendered() && !s.isDisposed() && !disposed) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		} else if ( updateThread.isAlive() ) {
			releaseLock();
		}

	}

	void doSnapshot() {
		LayeredDisplayData data = getOutput().getData();
		int w = (int) data.getImageDimension().getX();
		int h = (int) data.getImageDimension().getY();
		final int previousWidth = getDisplaySurface().getWidth();
		final int previousHeight = getDisplaySurface().getHeight();
		final int width = w == -1 ? previousWidth : w;
		final int height = h == -1 ? previousHeight : h;
		BufferedImage snapshot = null;
		if ( GamaPreferences.DISPLAY_FAST_SNAPSHOT.getValue() ) {
			try {
				Robot robot = new Robot();
				snapshot = robot.createScreenCapture(surfaceCompositeBounds);
				snapshot = ImageUtils.resize(snapshot, width, height);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// in case it has not worked, snapshot is still null
		if ( snapshot == null ) {
			snapshot = getDisplaySurface().getImage(width, height);
		}
		saveSnapshot(getDisplaySurface().getDisplayScope(), snapshot);
	}

	/**
	 * Save this surface into an image passed as a parameter
	 * @param scope
	 * @param image
	 */
	public final void saveSnapshot(final IScope scope, final BufferedImage image) {
		// Intentionnaly passing GAMA.getRuntimeScope() to errors in order to prevent the exceptions from being masked.
		if ( image == null ) { return; }
		try {
			Files.newFolder(scope, IDisplaySurface.SNAPSHOT_FOLDER_NAME);
		} catch (GamaRuntimeException e1) {
			e1.addContext("Impossible to create folder " + IDisplaySurface.SNAPSHOT_FOLDER_NAME);
			GAMA.reportError(GAMA.getRuntimeScope(), e1, false);
			e1.printStackTrace();
			return;
		}
		String snapshotFile = FileUtils.constructAbsoluteFilePath(scope, IDisplaySurface.SNAPSHOT_FOLDER_NAME + "/" +
			GAMA.getModel().getName() + "_display_" + getOutput().getName(), false);

		String file = snapshotFile + "_size_" + image.getWidth() + "x" + image.getHeight() + "_cycle_" +
			scope.getClock().getCycle() + "_time_" + java.lang.System.currentTimeMillis() + ".png";
		DataOutputStream os = null;
		try {
			os = new DataOutputStream(new FileOutputStream(file));
			ImageIO.write(image, "png", os);
			image.flush();
		} catch (java.io.IOException ex) {
			GamaRuntimeException e = GamaRuntimeException.create(ex, scope);
			e.addContext("Unable to create output stream for snapshot image");
			GAMA.reportError(GAMA.getRuntimeScope(), e, false);
		} finally {
			try {
				if ( os != null ) {
					os.close();
				}
			} catch (Exception ex) {
				GamaRuntimeException e = GamaRuntimeException.create(ex, scope);
				e.addContext("Unable to close output stream for snapshot image");
				GAMA.reportError(GAMA.getRuntimeScope(), e, false);
			}
		}
	}

	private volatile boolean lockAcquired = false;

	synchronized void acquireLock() {
		while (lockAcquired) {
			try {
				wait();
			} catch (final InterruptedException e) {
				// e.printStackTrace();
			}
		}
		lockAcquired = true;
	}

	private synchronized void releaseLock() {
		lockAcquired = false;
		notify();
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
			i.setText(name);
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

}
