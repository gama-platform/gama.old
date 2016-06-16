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
package msi.gama.gui.views.displays;

import java.awt.Color;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import javax.imageio.ImageIO;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import msi.gama.common.GamaPreferences;
import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IGui;
import msi.gama.common.interfaces.ILayer;
import msi.gama.common.interfaces.ILayerManager;
import msi.gama.common.interfaces.ItemList;
import msi.gama.common.util.FileUtils;
import msi.gama.common.util.ImageUtils;
import msi.gama.gui.swt.SwtGui;
import msi.gama.gui.swt.controls.ParameterExpandBar;
import msi.gama.gui.swt.controls.ParameterExpandItem;
import msi.gama.gui.views.GamaViewPart;
import msi.gama.gui.views.IToolbarDecoratedView;
import msi.gama.gui.views.InteractiveConsoleView;
import msi.gama.gui.views.actions.GamaToolbarFactory;
import msi.gama.kernel.experiment.ITopLevelAgent;
import msi.gama.metamodel.shape.Envelope3D;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.outputs.IDisplayOutput;
import msi.gama.outputs.LayeredDisplayData;
import msi.gama.outputs.LayeredDisplayData.Changes;
import msi.gama.outputs.LayeredDisplayData.DisplayDataListener;
import msi.gama.outputs.LayeredDisplayOutput;
import msi.gama.outputs.layers.AbstractLayer;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.operators.Files;
import msi.gaml.operators.Maths;
import ummisco.gama.ui.controls.GamaToolbar2;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.resources.IGamaColors;
import ummisco.gama.ui.resources.IGamaIcons;

public abstract class LayeredDisplayView extends GamaViewPart implements DisplayDataListener, IToolbarDecoratedView.Pausable, IToolbarDecoratedView.Zoomable {

	protected SashForm form;
	protected Composite surfaceComposite;
	protected Composite sidePanel;
	protected IPerspectiveListener perspectiveListener;
	public DisplayOverlay overlay;
	protected volatile boolean disposed;
	protected volatile boolean realized = false;
	protected ToolItem overlayItem, sideControlsItem;
	boolean sideControlsVisible = false, interactiveConsoleVisible = false; // TODO Make it a preference
	int[] sideControlWeights = new int[] { 30, 70 };
	protected final java.awt.Rectangle surfaceCompositeBounds = new java.awt.Rectangle();
	protected LayeredDisplayMultiListener keyAndMouseListener;
	protected DisplaySurfaceMenu menuManager;
	protected Composite normalParentOfFullScreenControl;
	protected Shell fullScreenShell;

	public void toggleFullScreen() {
		if ( isFullScreen() ) {
			if ( interactiveConsoleVisible )
				toggleInteractiveConsole();
			controlToSetFullScreen().setParent(normalParentOfFullScreenControl);
			createOverlay();
			normalParentOfFullScreenControl.layout(true, true);
			destroyFullScreenShell();
			this.setFocus();

		} else {
			createFullScreenShell();
			normalParentOfFullScreenControl = controlToSetFullScreen().getParent();
			controlToSetFullScreen().setParent(fullScreenShell);
			createOverlay();
			fullScreenShell.layout(true, true);
			fullScreenShell.setVisible(true);
			getZoomableControls()[0].forceFocus();
		}
	}

	public Control controlToSetFullScreen() {
		return form;
	}

	public void createOverlay() {
		boolean wasVisible = false;
		if ( overlay != null ) {
			wasVisible = overlay.isVisible();
			overlay.dispose();
		}
		overlay = new DisplayOverlay(this, surfaceComposite, getOutput().getOverlayProvider());
		if ( wasVisible )
			overlay.setVisible(true);
	}

	public boolean isFullScreen() {
		return fullScreenShell != null;
	}

	private void createFullScreenShell() {
		if ( fullScreenShell != null )
			return;
		fullScreenShell = new Shell(SwtGui.getDisplay(),
			(GamaPreferences.DISPLAY_MODAL_FULLSCREEN.getValue() ? SWT.ON_TOP : SWT.APPLICATION_MODAL) | SWT.NO_TRIM);
		fullScreenShell.setBounds(SwtGui.getDisplay().getBounds());

		// fullScreenShell.setMaximized(true);
		final GridLayout gl = new GridLayout(1, true);
		gl.horizontalSpacing = 0;
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		gl.verticalSpacing = 0;
		fullScreenShell.setLayout(gl);
		// fullScreenShell.setAlpha(200);
		// fullScreenShell.setFullScreen(true);
	}

	private void destroyFullScreenShell() {
		if ( fullScreenShell == null )
			return;
		fullScreenShell.close();
		fullScreenShell.dispose();
		fullScreenShell = null;
	}

	protected Runnable displayOverlay = new Runnable() {

		@Override
		public void run() {
			if ( overlay == null ) { return; }
			updateOverlay();
		}
	};

	protected void updateOverlay() {
		if ( overlay.isVisible() )
			overlay.update();
	}

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
			final IScope scope = out.getScope();

			if ( scope != null && scope.getSimulationScope() != null ) {
				final ITopLevelAgent root = scope.getRoot();
				final Color color = root.getColor();
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

	public void toggleOverlay() {
		overlay.setVisible(!overlay.isVisible());
	}

	public void toggleSideControls() {
		if ( sideControlsVisible ) {
			sideControlWeights = form.getWeights();
			form.setMaximizedControl(parent.getParent());
			sideControlsVisible = false;
		} else {
			form.setWeights(sideControlWeights);
			form.setMaximizedControl(null);
			sideControlsVisible = true;
		}
		sideControlsItem.setSelection(sideControlsVisible);
	}

	public void toggleInteractiveConsole() {
		// TODO Reprogram it a little bit more carefully ('view.parent', etc.)
		if ( !sideControlsVisible )
			toggleSideControls();
		if ( interactiveConsoleVisible ) {
			final InteractiveConsoleView view = (InteractiveConsoleView) GAMA.getGui().getInteractiveConsole();
			if ( view == null )
				return;
			view.getControlToDisplayInFullScreen().setParent(view.getParentOfControlToDisplayFullScreen());
			view.getParentOfControlToDisplayFullScreen().layout();
			interactiveConsoleVisible = false;
		} else {
			final InteractiveConsoleView view = (InteractiveConsoleView) GAMA.getGui().getInteractiveConsole();
			if ( view == null )
				return;
			view.getControlToDisplayInFullScreen().setParent(sidePanel);
			interactiveConsoleVisible = true;
		}
		sidePanel.layout(true, true);

	}

	public Composite getSurfaceComposite() {
		return surfaceComposite;
	}

	@Override
	public void ownCreatePartControl(final Composite c) {
		if ( getOutput() == null ) { return; }

		final GridLayout ll = new GridLayout(1, true);
		ll.horizontalSpacing = 0;
		ll.verticalSpacing = 0;
		ll.marginHeight = 0;
		ll.marginWidth = 0;
		c.setLayout(ll);

		// First create the sashform

		form = new SashForm(c, SWT.HORIZONTAL);
		form.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		form.setBackground(IGamaColors.WHITE.color());
		form.setSashWidth(4);

		sidePanel = new Composite(form, SWT.BORDER);
		final GridLayout layout = new GridLayout(1, true);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		sidePanel.setLayout(layout);
		sidePanel.setBackground(IGamaColors.WHITE.color());
		final Composite centralPanel =
			new Composite(form, GamaPreferences.CORE_DISPLAY_BORDER.getValue() ? SWT.BORDER : SWT.NONE);
		GridLayout gl = new GridLayout(1, true);
		gl.horizontalSpacing = 0;
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		gl.verticalSpacing = 0;
		centralPanel.setLayout(gl);
		parent = new Composite(centralPanel, SWT.NONE) {

			@Override
			public boolean setFocus() {
				return forceFocus();
			}

		};

		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		gl = new GridLayout(1, true);
		gl.horizontalSpacing = 0;
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		gl.verticalSpacing = 0;
		parent.setLayout(gl);
		createSurfaceComposite(parent);

		surfaceComposite.addControlListener(new ControlAdapter() {

			@Override
			public void controlResized(final ControlEvent e) {
				final Rectangle r = Display.getCurrent().map(surfaceComposite, null, surfaceComposite.getBounds());
				surfaceCompositeBounds.setBounds(r.x, r.y, r.width, r.height);
			}
		});

		final GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalIndent = 0;
		gd.verticalIndent = 0;
		surfaceComposite.setLayoutData(gd);
		createOverlay();
		getOutput().setSynchronized(GamaPreferences.CORE_SYNC.getValue());
		getOutput().getData().addListener(this);
		overlay.setVisible(GamaPreferences.CORE_OVERLAY.getValue());
		if ( overlay.isVisible() ) {
			overlay.update();
		}
		// Create after the surface composite
		fillLayerSideControls(sidePanel);

		// form.setWeights(new int[] { 30, 70 });
		form.setMaximizedControl(centralPanel);
		c.layout();

		perspectiveListener = new IPerspectiveListener() {

			boolean previousState = false;

			@Override
			public void perspectiveChanged(final IWorkbenchPage page, final IPerspectiveDescriptor perspective,
				final String changeId) {}

			@Override
			public void perspectiveActivated(final IWorkbenchPage page, final IPerspectiveDescriptor perspective) {
				if ( perspective.getId().equals(IGui.PERSPECTIVE_MODELING_ID) ) {
					if ( getOutput() != null && getDisplaySurface() != null ) {
						if ( !GamaPreferences.CORE_DISPLAY_PERSPECTIVE.getValue() ) {
							previousState = getOutput().isPaused();
							getOutput().setPaused(true);
						}
					}
					if ( overlay != null ) {
						overlay.hide();
					}
				} else {
					if ( !GamaPreferences.CORE_DISPLAY_PERSPECTIVE.getValue() ) {
						if ( getOutput() != null && getDisplaySurface() != null ) {
							getOutput().setPaused(previousState);
						}
					}
					if ( overlay != null ) {
						overlay.update();
					}
				}
			}
		};

		SwtGui.getWindow().addPerspectiveListener(perspectiveListener);
		keyAndMouseListener = new LayeredDisplayMultiListener(this);
		menuManager = new DisplaySurfaceMenu(getDisplaySurface(), parent, this);
		if ( getOutput().getData().isFullScreen() ) {
			toggleFullScreen();
		}
	}

	@Override
	public void setFocus() {
		if ( parent != null && !parent.isDisposed() && !parent.isFocusControl() ) {
			parent.forceFocus();
		}
	}

	protected abstract Composite createSurfaceComposite(Composite parent);

	@Override
	public LayeredDisplayOutput getOutput() {
		return (LayeredDisplayOutput) super.getOutput();
	}

	public IDisplaySurface getDisplaySurface() {
		final LayeredDisplayOutput out = getOutput();
		if ( out != null ) { return out.getSurface(); }
		return null;
	}

	@Override
	public void widgetDisposed(final DisposeEvent e) {
		if ( disposed ) { return; }
		if ( getOutput() != null ) {
			getOutput().getData().removeListener(this);
		}
		if ( keyAndMouseListener != null ) {
			keyAndMouseListener.dispose();
		}
		disposed = true;
		if ( surfaceComposite != null ) {
			surfaceComposite.dispose();
		}
		// final IDisplaySurface s = getDisplaySurface();
		// if ( s != null ) {
		releaseLock();
		// }
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

		menuManager = null;

		super.widgetDisposed(e);
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
		final Double dataZoom = getOutput().getData().getZoomLevel();
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
		final IDisplaySurface s = getDisplaySurface();
		if ( s == null ) { return 1; }
		final double displayWidth = s.getDisplayWidth();
		final double envWidth = s.getEnvWidth();
		return envWidth / displayWidth;
	}

	public String getOverlayCoordInfo() {
		final LayeredDisplayOutput output = getOutput();
		if ( output == null ) { return ""; }
		final boolean paused = output.isPaused();
		final boolean synced = output.getData().isSynchronized();
		final IDisplaySurface surface = getDisplaySurface();
		final String point = surface == null ? null : surface.getModelCoordinatesInfo();
		return point + (paused ? " | Paused" : "") + (synced ? " | Synchronized" : "");
	}

	public String getOverlayZoomInfo() {
		final IDisplaySurface surface = getDisplaySurface();
		if ( surface == null ) { return ""; }
		final boolean openGL = isOpenGL();
		String result = GamaPreferences.CORE_SHOW_FPS.getValue() ? String.valueOf(surface.getFPS()) + " fps | " : "";
		if ( !openGL ) {
			return result + "Zoom " + getZoomLevel() + "%";
		} else {
			final Envelope3D roi = ((IDisplaySurface.OpenGL) surface).getROIDimensions();
			final IDisplaySurface.OpenGL ds = (IDisplaySurface.OpenGL) surface;
			final ILocation camera = ds.getCameraPosition();
			result = result + String.format("Zoom %d%% | Camera [%.2f;%.2f;%.2f]", getZoomLevel(), camera.getX(),
				camera.getY(), camera.getZ()/* , camera.getTheta(), camera.getPhi() */);
			if ( roi != null ) {
				result =
					result + " ROI [" + Maths.round(roi.getWidth(), 2) + " x " + Maths.round(roi.getHeight(), 2) + "]";
			}
			return result;
		}
	}

	@Override
	public void createToolItems(final GamaToolbar2 tb) {
		super.createToolItems(tb);
		// { LAYER_CONTROLS, INSPECT_AGENTS, OPENGL, SEP, SNAPSHOT };

		sideControlsItem = tb.check("display.layers2", "Toggle layers controls",
			"Toggle layers controls (CTRL+L or COMMAND+L)", new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent e) {
					toggleSideControls();
				}

			}, SWT.LEFT);
		overlayItem = tb.check("display.overlay2", "Toggle overlay", "Toggle bottom overlay (CTRL+O or COMMAND+O)",
			new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent e) {
					overlay.setVisible(overlayItem.getSelection());
				}

			}, SWT.LEFT);
		overlayItem.setSelection(GamaPreferences.CORE_OVERLAY.getValue());
		tb.sep(GamaToolbarFactory.TOOLBAR_SEP, SWT.LEFT);

		tb.menu(IGamaIcons.MENU_POPULATION.getCode(), "Browse displayed agents by layers",
			"Browse through all displayed agents", new SelectionAdapter() {

				Menu menu;

				@Override
				public void widgetSelected(final SelectionEvent trigger) {
					// final boolean asMenu = trigger.detail == SWT.ARROW;
					final ToolItem target = (ToolItem) trigger.widget;
					final ToolBar toolBar = target.getParent();
					if ( menu != null ) {
						menu.dispose();
					}
					menu = new Menu(toolBar.getShell(), SWT.POP_UP);
					menuManager.buildToolbarMenu(menu);
					final Point point = toolBar.toDisplay(new Point(trigger.x, trigger.y));
					menu.setLocation(point.x, point.y);
					menu.setVisible(true);

				}
			}, SWT.LEFT);

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
		if ( getDisplaySurface() != null )
			getDisplaySurface().zoomIn();
	}

	@Override
	public void zoomOut() {
		if ( getDisplaySurface() != null )
			getDisplaySurface().zoomOut();
	}

	@Override
	public void zoomFit() {
		if ( getDisplaySurface() != null )
			getDisplaySurface().zoomFit();
	}

	@Override
	public Control[] getZoomableControls() {
		return new Control[] { parent };
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

		// Fix for issue #1693
		final boolean oldSync = output.isSynchronized();
		if ( output.isInInitPhase() )
			output.setSynchronized(false);
		// end fix
		if ( updateThread == null ) {
			updateThread = new Thread(new Runnable() {

				@Override
				public void run() {
					final IDisplaySurface s = getDisplaySurface();
					if ( s != null && !s.isDisposed() && !disposed ) {
						s.updateDisplay(false);
					}
					while (!disposed) {
						acquireLock();
						if ( s != null && s.isRealized() && !s.isDisposed() && !disposed ) {
							if ( s.getData().isAutosave() ) {
								doSnapshot();
							}
							s.updateDisplay(false);
							// Fix for issue #1693
							if ( output.isInInitPhase() ) {
								output.setInInitPhase(false);
								output.setSynchronized(oldSync);
								// end fix
							}
						}
					}
				}
			});
			updateThread.start();
		}

		if ( output.isSynchronized() ) {
			final IDisplaySurface s = getDisplaySurface();
			s.updateDisplay(false);
			if ( getOutput().getData().isAutosave() && s.isRealized() ) {
				doSnapshot();
			}
			while (!s.isRendered() && !s.isDisposed() && !disposed) {
				try {
					Thread.sleep(10);
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}

			}
		} else if ( updateThread.isAlive() ) {
			releaseLock();
		}

	}

	void doSnapshot() {
		final LayeredDisplayData data = getOutput().getData();
		final int w = (int) data.getImageDimension().getX();
		final int h = (int) data.getImageDimension().getY();
		final int previousWidth = getDisplaySurface().getWidth();
		final int previousHeight = getDisplaySurface().getHeight();
		final int width = w == -1 ? previousWidth : w;
		final int height = h == -1 ? previousHeight : h;
		BufferedImage snapshot = null;
		if ( GamaPreferences.DISPLAY_FAST_SNAPSHOT.getValue() ) {
			try {
				final Robot robot = new Robot();
				snapshot = robot.createScreenCapture(surfaceCompositeBounds);
				snapshot = ImageUtils.resize(snapshot, width, height);
			} catch (final Exception e) {
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
		} catch (final GamaRuntimeException e1) {
			e1.addContext("Impossible to create folder " + IDisplaySurface.SNAPSHOT_FOLDER_NAME);
			GAMA.reportError(GAMA.getRuntimeScope(), e1, false);
			e1.printStackTrace();
			return;
		}
		final String snapshotFile = FileUtils.constructAbsoluteFilePath(scope, IDisplaySurface.SNAPSHOT_FOLDER_NAME +
			"/" + GAMA.getModel().getName() + "_display_" + getOutput().getName(), false);

		final String file = snapshotFile + "_size_" + image.getWidth() + "x" + image.getHeight() + "_cycle_" +
			scope.getClock().getCycle() + "_time_" + java.lang.System.currentTimeMillis() + ".png";
		DataOutputStream os = null;
		try {
			os = new DataOutputStream(new FileOutputStream(file));
			ImageIO.write(image, "png", os);
			image.flush();
		} catch (final java.io.IOException ex) {
			final GamaRuntimeException e = GamaRuntimeException.create(ex, scope);
			e.addContext("Unable to create output stream for snapshot image");
			GAMA.reportError(GAMA.getRuntimeScope(), e, false);
		} finally {
			try {
				if ( os != null ) {
					os.close();
				}
			} catch (final Exception ex) {
				final GamaRuntimeException e = GamaRuntimeException.create(ex, scope);
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
		final ItemList<ILayer> list = getDisplayManager();
		final ParameterExpandBar viewer = new ParameterExpandBar(content, SWT.V_SCROLL, false, false, true, true, list);
		data = new GridData(SWT.FILL, SWT.FILL, true, true);
		viewer.setLayoutData(data);
		viewer.setSpacing(5);

		for ( final ILayer layer : list.getItems() ) {
			final String name = "Layer " + layer.getName();
			compo = new Composite(viewer, SWT.NONE);
			compo.setBackground(IGamaColors.WHITE.color());
			layout = new GridLayout(2, false);
			layout.verticalSpacing = 0;
			compo.setLayout(layout);
			if ( layer instanceof AbstractLayer ) {
				LayerSideControls.fill(compo, layer, getDisplaySurface());
			}
			final Composite control = compo;
			final ParameterExpandItem i = new ParameterExpandItem(viewer, layer, SWT.None, null);
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

	@Override
	public boolean zoomWhenScrolling() {
		return true;
	}

	@Override
	public void removeOutput(final IDisplayOutput output) {
		if ( output == null )
			return;
		if ( output == getOutput() ) {
			if ( isFullScreen() ) {
				GAMA.getGui().run(new Runnable() {

					@Override
					public void run() {
						toggleFullScreen();

					}
				});
			}
		}
		output.dispose();
		outputs.remove(output);
		if ( outputs.isEmpty() ) {
			close();
		}
	}

}
