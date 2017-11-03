/*********************************************************************************************
 *
 * 'LayeredDisplayView.java, in plugin ummisco.gama.ui.experiment, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.views.displays;

import java.awt.Color;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.util.List;

import javax.imageio.ImageIO;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

import msi.gama.application.workbench.PerspectiveHelper;
import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.geometry.ICoordinates;
import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IGamaView;
import msi.gama.common.interfaces.IGui;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.interfaces.ILayer;
import msi.gama.common.interfaces.ILayerManager;
import msi.gama.common.interfaces.ItemList;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.common.util.FileUtils;
import msi.gama.common.util.ImageUtils;
import msi.gama.kernel.experiment.ITopLevelAgent;
import msi.gama.metamodel.shape.GamaPoint;
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
import msi.gama.util.GamaColor;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gaml.operators.Files;
import msi.gaml.operators.Maths;
import msi.gaml.types.Types;
import ummisco.gama.ui.controls.ParameterExpandBar;
import ummisco.gama.ui.controls.ParameterExpandItem;
import ummisco.gama.ui.interfaces.EditorListener;
import ummisco.gama.ui.parameters.BooleanEditor;
import ummisco.gama.ui.parameters.ColorEditor;
import ummisco.gama.ui.parameters.EditorFactory;
import ummisco.gama.ui.parameters.FloatEditor;
import ummisco.gama.ui.parameters.IntEditor;
import ummisco.gama.ui.parameters.PointEditor;
import ummisco.gama.ui.parameters.StringEditor;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.resources.IGamaColors;
import ummisco.gama.ui.resources.IGamaIcons;
import ummisco.gama.ui.utils.WorkbenchHelper;
import ummisco.gama.ui.views.GamaViewPart;
import ummisco.gama.ui.views.InteractiveConsoleView;
import ummisco.gama.ui.views.toolbar.GamaToolbar2;
import ummisco.gama.ui.views.toolbar.GamaToolbarFactory;
import ummisco.gama.ui.views.toolbar.IToolbarDecoratedView;

public abstract class LayeredDisplayView extends GamaViewPart implements DisplayDataListener,
		IToolbarDecoratedView.Pausable, IToolbarDecoratedView.Zoomable, IGamaView.Display {

	protected SashForm form;
	public Composite surfaceComposite;
	protected Composite sidePanel;
	protected IPerspectiveListener perspectiveListener;
	public DisplayOverlay overlay;
	protected volatile boolean disposed;
	protected volatile boolean realized = false;
	protected ToolItem overlayItem, sideControlsItem;
	boolean sideControlsVisible = false, interactiveConsoleVisible = false;
	int[] sideControlWeights = new int[] { 30, 70 };
	protected final java.awt.Rectangle surfaceCompositeBounds = new java.awt.Rectangle();
	protected LayeredDisplayMultiListener keyAndMouseListener;
	protected DisplaySurfaceMenu menuManager;
	protected Composite normalParentOfFullScreenControl;
	protected Shell fullScreenShell;

	@Override
	public void toggleFullScreen() {
		if (isFullScreen()) {
			if (interactiveConsoleVisible)
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

	public SashForm getSash() {
		return form;
	}

	public void createOverlay() {
		boolean wasVisible = false;
		if (overlay != null) {
			wasVisible = overlay.isVisible();
			overlay.dispose();
		}
		overlay = new DisplayOverlay(this, surfaceComposite, getOutput().getOverlayProvider());
		if (wasVisible)
			overlay.setVisible(true);
	}

	@Override
	public boolean isFullScreen() {
		return fullScreenShell != null;
	}

	private void createFullScreenShell() {
		if (fullScreenShell != null)
			return;
		final Monitor[] monitors = WorkbenchHelper.getDisplay().getMonitors();
		int monitorId = getOutput().getData().fullScreen();
		if (monitorId < 0)
			monitorId = 0;
		if (monitorId > monitors.length - 1)
			monitorId = monitors.length - 1;
		final Rectangle bounds = monitors[monitorId].getBounds();

		fullScreenShell =
				new Shell(WorkbenchHelper.getDisplay(), (GamaPreferences.Displays.DISPLAY_MODAL_FULLSCREEN.getValue()
						? SWT.ON_TOP | SWT.SYSTEM_MODAL : SWT.APPLICATION_MODAL) | SWT.NO_TRIM);
		fullScreenShell.setBounds(bounds);
		if (GamaPreferences.Displays.DISPLAY_NATIVE_FULLSCREEN.getValue()) {
			fullScreenShell = new Shell(SWT.NO_TRIM | SWT.ON_TOP);
			fullScreenShell.setMaximized(true);
			fullScreenShell.setBounds(bounds);
			fullScreenShell.setFullScreen(true);
		}
		final GridLayout gl = new GridLayout(1, true);
		gl.horizontalSpacing = 0;
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		gl.verticalSpacing = 0;
		fullScreenShell.setLayout(gl);

	}

	private void destroyFullScreenShell() {
		if (fullScreenShell == null)
			return;
		fullScreenShell.close();
		fullScreenShell.dispose();
		fullScreenShell = null;
	}

	protected Runnable displayOverlay = () -> {
		if (overlay == null) { return; }
		updateOverlay();
	};

	protected void updateOverlay() {
		if (overlay != null && overlay.isVisible())
			overlay.update();
	}

	@Override
	public void init(final IViewSite site) throws PartInitException {
		super.init(site);
		if (getOutput() != null) {
			setPartName(getOutput().getName());
		}
	}

	@Override
	public void addOutput(final IDisplayOutput out) {
		super.addOutput(out);
		if (out != null) {
			final IScope scope = out.getScope();

			if (scope != null && scope.getSimulation() != null) {
				final ITopLevelAgent root = scope.getRoot();
				final Color color = root.getColor();
				// String name = root.getClass().getSimpleName() +
				// root.getIndex();
				this.setTitleImage(GamaIcons.createTempColorIcon(GamaColors.get(color)));
			}
		}
	}

	public boolean isOpenGL() {
		if (outputs.isEmpty()) { return false; }
		return getOutput().getData().isOpenGL();
	}

	public ILayerManager getDisplayManager() {
		return getDisplaySurface().getManager();
	}

	public void toggleOverlay() {
		overlay.setVisible(!overlay.isVisible());
	}

	public void toggleSideControls() {
		if (sideControlsVisible) {
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
		if (!sideControlsVisible)
			toggleSideControls();
		final InteractiveConsoleView view =
				(InteractiveConsoleView) WorkbenchHelper.findView(IGui.INTERACTIVE_CONSOLE_VIEW_ID, null, true);
		if (view == null)
			return;
		if (interactiveConsoleVisible) {
			view.getControlToDisplayInFullScreen().setParent(view.getParentOfControlToDisplayFullScreen());
			view.getParentOfControlToDisplayFullScreen().layout();
			interactiveConsoleVisible = false;
		} else {
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
		if (getOutput() == null) { return; }

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
		form.setSashWidth(8);

		sidePanel = new Composite(form, SWT.BORDER);
		final GridLayout layout = new GridLayout(1, true);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		sidePanel.setLayout(layout);
		sidePanel.setBackground(IGamaColors.WHITE.color());
		final Composite centralPanel =
				new Composite(form, GamaPreferences.Displays.CORE_DISPLAY_BORDER.getValue() ? SWT.BORDER : SWT.NONE);
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
				final Rectangle r =
						WorkbenchHelper.getDisplay().map(surfaceComposite, null, surfaceComposite.getBounds());
				surfaceCompositeBounds.setBounds(r.x, r.y, r.width, r.height);
			}
		});

		final GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalIndent = 0;
		gd.verticalIndent = 0;
		surfaceComposite.setLayoutData(gd);
		createOverlay();
		getOutput().setSynchronized(getOutput().isSynchronized() || GamaPreferences.Runtime.CORE_SYNC.getValue());
		getOutput().getData().addListener(this);
		overlay.setVisible(GamaPreferences.Displays.CORE_OVERLAY.getValue());
		if (overlay.isVisible()) {
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
				if (perspective.getId().equals(PerspectiveHelper.PERSPECTIVE_MODELING_ID)) {
					if (getOutput() != null && getDisplaySurface() != null) {
						if (!GamaPreferences.Displays.CORE_DISPLAY_PERSPECTIVE.getValue()) {
							previousState = getOutput().isPaused();
							getOutput().setPaused(true);
						}
					}
					if (overlay != null) {
						overlay.hide();
					}
				} else {
					if (!GamaPreferences.Displays.CORE_DISPLAY_PERSPECTIVE.getValue()) {
						if (getOutput() != null && getDisplaySurface() != null) {
							getOutput().setPaused(previousState);
						}
					}
					if (overlay != null) {
						overlay.update();
					}
				}
			}
		};

		WorkbenchHelper.getWindow().addPerspectiveListener(perspectiveListener);
		keyAndMouseListener = new LayeredDisplayMultiListener(this);
		menuManager = new DisplaySurfaceMenu(getDisplaySurface(), parent, this);
		if (getOutput().getData().fullScreen() > -1) {
			toggleFullScreen();
		}
	}

	@Override
	public void setFocus() {
		if (parent != null && !parent.isDisposed() && !parent.isFocusControl()) {
			parent.forceFocus();
		}
	}

	protected abstract Composite createSurfaceComposite(Composite parent);

	@Override
	public LayeredDisplayOutput getOutput() {
		return (LayeredDisplayOutput) super.getOutput();
	}

	@Override
	public IDisplaySurface getDisplaySurface() {
		final LayeredDisplayOutput out = getOutput();
		if (out != null) { return out.getSurface(); }
		return null;
	}

	@Override
	public void widgetDisposed(final DisposeEvent e) {
		if (disposed) { return; }
		final LayeredDisplayOutput output = getOutput();
		if (output != null) {
			output.getData().listeners.clear();
			final IDisplaySurface s = output.getSurface();
			if (isOpenGL() && s != null) {
				s.dispose();
				output.setSurface(null);
			}
		}
		if (keyAndMouseListener != null) {
			keyAndMouseListener.dispose();
		}
		disposed = true;
		if (surfaceComposite != null) {
			try {
				surfaceComposite.dispose();
			} catch (final RuntimeException ex) {

			}
		}
		releaseLock();
		// }
		if (updateThread != null) {
			updateThread.interrupt();
		}

		if (perspectiveListener != null) {
			WorkbenchHelper.getWindow().removePerspectiveListener(perspectiveListener);
		}
		// FIXME Remove the listeners

		if (overlay != null) {
			overlay.close();
		}

		menuManager = null;

		super.widgetDisposed(e);
	}

	@Override
	public void changed(final Changes changes, final Object value) {
		switch (changes) {
			case ZOOM:
				WorkbenchHelper.asyncRun(() -> overlay.update());
				break;
			default:
				break;
		}

	}

	/*
	 * Between 0 and 100;
	 */
	public int getZoomLevel() {
		if (getOutput() == null) { return 0; }
		final Double dataZoom = getOutput().getData().getZoomLevel();
		if (dataZoom == null) {
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
		if (s == null) { return 1; }
		final double displayWidth = s.getDisplayWidth();
		final double envWidth = s.getEnvWidth();
		return envWidth / displayWidth;
	}

	public void getOverlayCoordInfo(final StringBuilder sb) {
		final LayeredDisplayOutput output = getOutput();
		if (output == null) { return; }
		final boolean paused = output.isPaused();
		final boolean synced = output.getData().isSynchronized();
		final IDisplaySurface surface = getDisplaySurface();
		if (surface != null)
			surface.getModelCoordinatesInfo(sb);
		if (paused)
			sb.append(" | Paused");
		if (synced)
			sb.append(" | Synchronized");
	}

	public void getOverlayZoomInfo(final StringBuilder sb) {
		final IDisplaySurface surface = getDisplaySurface();
		if (surface == null) { return; }
		if (GamaPreferences.Displays.CORE_SHOW_FPS.getValue()) {
			sb.append(surface.getFPS());
			sb.append(" fps | ");
		}
		sb.append("Zoom ").append(getZoomLevel()).append("%");
		if (isOpenGL()) {
			final Envelope3D roi = ((IDisplaySurface.OpenGL) surface).getROIDimensions();
			if (roi != null) {
				sb.append(" ROI [");
				sb.append(Maths.round(roi.getWidth(), 2));
				sb.append(" x ");
				sb.append(Maths.round(roi.getHeight(), 2));
				sb.append("]");
			}
		}
	}

	@Override
	public boolean toolbarVisible() {
		return getOutput().getData().isToolbarVisible();
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
		overlayItem.setSelection(GamaPreferences.Displays.CORE_OVERLAY.getValue());
		tb.sep(GamaToolbarFactory.TOOLBAR_SEP, SWT.LEFT);

		tb.menu(IGamaIcons.MENU_POPULATION, "Browse displayed agents by layers", "Browse through all displayed agents",
				new SelectionAdapter() {

					Menu menu;

					@Override
					public void widgetSelected(final SelectionEvent trigger) {
						// final boolean asMenu = trigger.detail == SWT.ARROW;
						final ToolItem target = (ToolItem) trigger.widget;
						final ToolBar toolBar = target.getParent();
						if (menu != null) {
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
		tb.button(IGamaIcons.DISPLAY_TOOLBAR_SNAPSHOT, "Take a snapshot", "Take a snapshot", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				doSnapshot();
			}

		}, SWT.RIGHT);
	}

	@Override
	public void zoomIn() {
		if (getDisplaySurface() != null)
			getDisplaySurface().zoomIn();
	}

	@Override
	public void zoomOut() {
		if (getDisplaySurface() != null)
			getDisplaySurface().zoomOut();
	}

	@Override
	public void zoomFit() {
		if (getDisplaySurface() != null)
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
				if (getDisplaySurface() == null) { return Status.CANCEL_STATUS; }
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
		if (output.isInInitPhase())
			output.setSynchronized(false);
		// end fix
		if (updateThread == null) {
			updateThread = new Thread(() -> {
				final IDisplaySurface s = getDisplaySurface();
				// if (s != null && !s.isDisposed() && !disposed) {
				// s.updateDisplay(false);
				// }
				while (!disposed) {

					if (s != null && s.isRealized() && !s.isDisposed() && !disposed) {
						acquireLock();
						s.updateDisplay(false);
						if (s.getData().isAutosave()) {
							doSnapshot();
						}
						// Fix for issue #1693
						if (output.isInInitPhase()) {
							output.setInInitPhase(false);
							output.setSynchronized(oldSync);
							// end fix
						}

					}

				}
			});
			updateThread.start();
		}

		if (output.isSynchronized()) {
			final IDisplaySurface s = getDisplaySurface();
			s.updateDisplay(false);
			if (getOutput().getData().isAutosave() && s.isRealized()) {
				doSnapshot();
			}
			while (!s.isRendered() && !s.isDisposed() && !disposed) {
				try {
					Thread.sleep(10);
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}

			}
		} else if (updateThread.isAlive()) {
			releaseLock();
		}

	}

	void doSnapshot() {
		if (getOutput() == null || getDisplaySurface() == null)
			return;
		final IDisplaySurface surface = getDisplaySurface();
		final IScope scope = surface.getScope();
		final LayeredDisplayData data = getOutput().getData();
		final int w = (int) data.getImageDimension().getX();
		final int h = (int) data.getImageDimension().getY();
		final int width = w == -1 ? surface.getWidth() : w;
		final int height = h == -1 ? surface.getHeight() : h;
		BufferedImage snapshot = null;
		if (GamaPreferences.Displays.DISPLAY_FAST_SNAPSHOT.getValue()) {
			try {
				final Robot robot = new Robot();
				snapshot = robot.createScreenCapture(surfaceCompositeBounds);
				snapshot = ImageUtils.resize(snapshot, width, height);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
		// in case it has not worked, snapshot is still null
		if (snapshot == null) {
			snapshot = surface.getImage(width, height);
		}
		if (!scope.interrupted())
			saveSnapshot(scope, snapshot);
	}

	/**
	 * Save this surface into an image passed as a parameter
	 * 
	 * @param scope
	 * @param image
	 */
	public final void saveSnapshot(final IScope scope, final BufferedImage image) {
		// Intentionnaly passing GAMA.getRuntimeScope() to errors in order to
		// prevent the exceptions from being masked.
		if (image == null) { return; }
		try {
			Files.newFolder(scope, IDisplaySurface.SNAPSHOT_FOLDER_NAME);
		} catch (final GamaRuntimeException e1) {
			e1.addContext("Impossible to create folder " + IDisplaySurface.SNAPSHOT_FOLDER_NAME);
			GAMA.reportError(GAMA.getRuntimeScope(), e1, false);
			e1.printStackTrace();
			return;
		}
		final String snapshotFile = FileUtils.constructAbsoluteFilePath(scope, IDisplaySurface.SNAPSHOT_FOLDER_NAME
				+ "/" + GAMA.getModel().getName() + "_display_" + getOutput().getName(), false);

		final String file = snapshotFile + "_size_" + image.getWidth() + "x" + image.getHeight() + "_cycle_"
				+ scope.getClock().getCycle() + "_time_" + java.lang.System.currentTimeMillis() + ".png";
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
				if (os != null) {
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

		final Composite column = new Composite(parent, SWT.NONE);
		column.setBackground(IGamaColors.WHITE.color());
		GridData data = new GridData(SWT.FILL, SWT.TOP, true, false);
		column.setLayoutData(data);
		final GridLayout layout = new GridLayout(2, false);
		layout.verticalSpacing = 0;
		column.setLayout(layout);

		final Composite viewersComposite = new Composite(parent, SWT.None);
		viewersComposite.setBackground(IGamaColors.WHITE.color());
		data = new GridData(SWT.FILL, SWT.FILL, true, true);
		viewersComposite.setLayoutData(data);
		viewersComposite.setLayout(new GridLayout(1, true));

		final ParameterExpandBar propertiesViewer =
				new ParameterExpandBar(viewersComposite, SWT.V_SCROLL, false, false, false, false, null);
		data = new GridData(SWT.FILL, SWT.FILL, true, true);
		propertiesViewer.setLayoutData(data);
		propertiesViewer.setSpacing(5);
		final ItemList<ILayer> list = getDisplayManager();
		final ParameterExpandBar layerViewer =
				new ParameterExpandBar(viewersComposite, SWT.V_SCROLL, false, false, true, true, list);
		data = new GridData(SWT.FILL, SWT.FILL, true, true);
		layerViewer.setLayoutData(data);
		layerViewer.setSpacing(5);
		// Fill the 2 viewers
		fillGeneralParameters(propertiesViewer);
		if (isOpenGL()) {
			fillCameraParameters(propertiesViewer);
			fillKeystoneParameters(propertiesViewer);
		}
		for (final ILayer layer : list.getItems()) {
			fillLayerParameters(layerViewer, layer);
		}

		viewersComposite.layout();
		layerViewer.addListener(SWT.Collapse, e -> viewersComposite.redraw());
		layerViewer.addListener(SWT.Expand, e -> viewersComposite.redraw());
		propertiesViewer.addListener(SWT.Collapse, e -> viewersComposite.redraw());
		propertiesViewer.addListener(SWT.Expand, e -> viewersComposite.redraw());
		parent.layout();
		return viewersComposite;

	}

	PointEditor cameraPos, cameraTarget, cameraUp;
	StringEditor preset;
	IntEditor zoom;
	FloatEditor rotate;

	private void fillCameraParameters(final ParameterExpandBar viewer) {
		final Composite contents = createContentsComposite(viewer);
		final IDisplaySurface ds = getDisplaySurface();
		final IScope scope = getDisplaySurface().getScope();
		final LayeredDisplayData data = getDisplaySurface().getData();

		final boolean cameraLocked = getOutput().getData().cameraInteractionDisabled();
		final BooleanEditor lock = EditorFactory.create(scope, contents, "Lock camera:", cameraLocked,
				(EditorListener<Boolean>) newValue -> {
					preset.setActive(!newValue);
					cameraPos.setActive(!newValue);
					cameraTarget.setActive(!newValue);
					cameraUp.setActive(!newValue);
					zoom.setActive(!newValue);
					getOutput().getData().disableCameraInteractions(newValue);
				});

		preset = EditorFactory.choose(scope, contents, "Preset camera:", "Choose...", true, getCameraNames(),
				(EditorListener<String>) newValue -> {
					if (newValue.isEmpty())
						return;
					data.setPresetCamera(newValue);
					ds.updateDisplay(true);
				});

		cameraPos = EditorFactory.create(scope, contents, "Position:", data.getCameraPos(),
				(EditorListener<ILocation>) newValue -> {
					data.setCameraPos((GamaPoint) newValue);
					ds.updateDisplay(true);
				});
		cameraTarget = EditorFactory.create(scope, contents, "Target:", data.getCameraLookPos(),
				(EditorListener<ILocation>) newValue -> {
					data.setCameraLookPos((GamaPoint) newValue);
					ds.updateDisplay(true);
				});
		cameraUp = EditorFactory.create(scope, contents, "Orientation:", data.getCameraUpVector(),
				(EditorListener<ILocation>) newValue -> {
					data.setCameraUpVector((GamaPoint) newValue, true);
					ds.updateDisplay(true);
				});
		preset.setActive(!cameraLocked);
		cameraPos.setActive(!cameraLocked);
		cameraTarget.setActive(!cameraLocked);
		cameraUp.setActive(!cameraLocked);
		zoom.setActive(!cameraLocked);
		data.addListener((p, v) -> {
			switch (p) {
				case CAMERA_POS:
					cameraPos.getParam().setValue(scope, data.getCameraPos());
					cameraPos.forceUpdateValueAsynchronously();
					break;
				case CAMERA_TARGET:
					cameraTarget.getParam().setValue(scope, data.getCameraLookPos());
					cameraTarget.forceUpdateValueAsynchronously();
					break;
				case CAMERA_UP:
					cameraUp.getParam().setValue(scope, data.getCameraUpVector());
					cameraUp.forceUpdateValueAsynchronously();
					break;
				case CAMERA_PRESET:
					preset.getParam().setValue(scope, "Choose...");
					preset.forceUpdateValueAsynchronously();
					break;
				default:
					;
			}
		});
		final Label l = new Label(contents, SWT.None);
		l.setText("");
		final Button copy = new Button(contents, SWT.PUSH);
		copy.setText("Copy as facets");
		copy.setLayoutData(new GridData(SWT.END, SWT.FILL, false, false));
		copy.setToolTipText(
				"Copy the definition of the camera properties to the clipboard in a format suitable for pasting them in the definition of a display in GAML");
		copy.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final Clipboard cb = new Clipboard(WorkbenchHelper.getDisplay());
				String text = IKeyword.CAMERA_POS + ": " + cameraPos.getCurrentValue().yNegated().serialize(false);
				text += " " + IKeyword.CAMERA_LOOK_POS + ": "
						+ cameraTarget.getCurrentValue().yNegated().serialize(false);
				text += " " + IKeyword.CAMERA_UP_VECTOR + ": " + cameraUp.getCurrentValue().serialize(false);
				final TextTransfer textTransfer = TextTransfer.getInstance();
				cb.setContents(new Object[] { text }, new Transfer[] { textTransfer });

			}

		});
		createItem(viewer, "Camera", null, contents);

	}

	protected abstract List<String> getCameraNames();

	private void fillKeystoneParameters(final ParameterExpandBar viewer) {
		final Composite contents = createContentsComposite(viewer);
		final IDisplaySurface ds = getDisplaySurface();
		final IScope scope = getDisplaySurface().getScope();
		final LayeredDisplayData data = getDisplaySurface().getData();

		final PointEditor[] point = new PointEditor[4];
		final ICoordinates points = data.getKeystone();
		int i = 0;
		for (final GamaPoint p : points) {
			final int j = i;
			i++;
			point[j] = EditorFactory.create(scope, contents, "Point " + j + ":",
					(GamaPoint) data.getKeystone().at(j).clone(), (EditorListener<ILocation>) newValue -> {
						data.getKeystone().at(j).setLocation(newValue);
						data.setKeystone(data.getKeystone());
						ds.updateDisplay(true);
					});
		}

		data.addListener((p, v) -> {
			switch (p) {
				case KEYSTONE:
					for (int k = 0; k < 4; k++) {
						point[k].getParam().setValue(scope, data.getKeystone().at(k));
						point[k].forceUpdateValueAsynchronously();
					}
					break;
				default:
					;
			}

		});
		final Label l = new Label(contents, SWT.None);
		l.setText("");
		final Button copy = new Button(contents, SWT.PUSH);
		copy.setText("Copy as facet");
		copy.setLayoutData(new GridData(SWT.END, SWT.FILL, false, false));
		copy.setToolTipText(
				"Copy the definition of the keystone values to the clipboard in a format suitable for pasting them in the definition of a display in GAML");
		copy.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final Clipboard cb = new Clipboard(WorkbenchHelper.getDisplay());
				final IList<GamaPoint> pp =
						GamaListFactory.create(scope, Types.POINT, data.getKeystone().toCoordinateArray());
				final String text = IKeyword.KEYSTONE + ": " + pp.serialize(false);
				final TextTransfer textTransfer = TextTransfer.getInstance();
				cb.setContents(new Object[] { text }, new Transfer[] { textTransfer });
			}

		});
		createItem(viewer, "Keystone", null, contents);
	}

	private void fillGeneralParameters(final ParameterExpandBar viewer) {
		final Composite contents = createContentsComposite(viewer);
		final IDisplaySurface ds = getDisplaySurface();
		final IScope scope = getDisplaySurface().getScope();
		final LayeredDisplayData data = getDisplaySurface().getData();
		EditorFactory.create(scope, contents, "Antialias:", data.isAntialias(), (EditorListener<Boolean>) newValue -> {
			data.setAntialias(newValue);
			ds.updateDisplay(true);
		});
		final ColorEditor background = EditorFactory.create(scope, contents, "Background:", data.getBackgroundColor(),
				(EditorListener<Color>) newValue -> {
					data.setBackgroundColor(new GamaColor(newValue));
					ds.updateDisplay(true);
				});
		final ColorEditor highlight = EditorFactory.create(scope, contents, "Highlight:", data.getHighlightColor(),
				(EditorListener<Color>) newValue -> {
					data.setHighlightColor(new GamaColor(newValue));
					ds.updateDisplay(true);
				});
		zoom = EditorFactory.create(scope, contents, "Zoom (%):", "",
				Integer.valueOf((int) (data.getZoomLevel() * 100)), 0, null, 1, (EditorListener<Integer>) newValue -> {
					data.setZoomLevel(newValue.doubleValue() / 100d, true);
					ds.updateDisplay(true);
				});
		rotate = EditorFactory.create(scope, contents, "Rotation angle about z-axis (degrees):",
				Double.valueOf(data.getCurrentRotationAboutZ()), null, null, 0.1, false,
				(EditorListener<Double>) newValue -> {
					data.setZRotationAngle(newValue);
					ds.updateDisplay(true);
				});

		createItem(viewer, "General", null, contents);

		ds.getData().addListener((p, v) -> {
			switch (p) {
				case ZOOM:
					zoom.getParam().setValue(scope, (int) (data.getZoomLevel() * 100));
					zoom.forceUpdateValueAsynchronously();
					break;
				case BACKGROUND:
					background.getParam().setValue(scope, data.getBackgroundColor());
					background.forceUpdateValueAsynchronously();
					break;
				case ROTATION:
					rotate.getParam().setValue(scope, (double) v);
					rotate.forceUpdateValueAsynchronously();
					break;
				default:
					;
			}

		});

	}

	public void createItem(final ParameterExpandBar viewer, final String name, final Object data,
			final Composite contents) {
		final ParameterExpandItem i = new ParameterExpandItem(viewer, data, SWT.None, null);
		i.setText(name);
		contents.pack(true);
		contents.layout();
		i.setControl(contents);
		i.setHeight(contents.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		i.setExpanded(false);
	}

	public void fillLayerParameters(final ParameterExpandBar viewer, final ILayer layer) {
		final Composite contents = createContentsComposite(viewer);
		if (layer instanceof AbstractLayer) {
			LayerSideControls.fill(contents, layer, getDisplaySurface());
		}
		createItem(viewer, "Layer " + layer.getName(), layer, contents);
	}

	public Composite createContentsComposite(final ParameterExpandBar viewer) {
		final Composite contents = new Composite(viewer, SWT.NONE);
		contents.setBackground(IGamaColors.WHITE.color());
		final GridLayout layout = new GridLayout(2, false);
		layout.verticalSpacing = 0;
		contents.setLayout(layout);
		return contents;
	}

	/**
	 * Ensures that the overlay tool item is coherent with the state of the overlay
	 * 
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
		if (output == null)
			return;
		if (output == getOutput()) {
			if (isFullScreen()) {
				WorkbenchHelper.run(() -> toggleFullScreen());
			}
		}
		output.dispose();
		outputs.remove(output);
		if (outputs.isEmpty()) {
			close(GAMA.getRuntimeScope());
		}
	}

	private ToolItem message;

	public void setMessage(final String msg) {
		if (message != null) {
			message.dispose();
			message = null;
		}
		if (msg != null)
			toolbar.status((String) null, msg, null, SWT.LEFT);
	}

}
