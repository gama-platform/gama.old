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
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
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
import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IGamaView;
import msi.gama.common.interfaces.IGui;
import msi.gama.common.interfaces.ILayerManager;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.kernel.experiment.ITopLevelAgent;
import msi.gama.outputs.IDisplayOutput;
import msi.gama.outputs.LayeredDisplayData.Changes;
import msi.gama.outputs.LayeredDisplayData.DisplayDataListener;
import msi.gama.outputs.LayeredDisplayOutput;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gaml.operators.Maths;
import ummisco.gama.ui.bindings.GamaKeyBindings;
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
	boolean sideControlsVisible = false, interactiveConsoleVisible = false, simulationControlsVisible = false;
	int[] sideControlWeights = new int[] { 30, 70 };
	protected LayeredDisplayMultiListener keyAndMouseListener;
	protected DisplaySurfaceMenu menuManager;
	protected Composite normalParentOfFullScreenControl;
	protected Shell fullScreenShell;

	@Override
	public void toggleFullScreen() {
		if (isFullScreen()) {
			adaptToolbarToFullScreen(false);
			if (interactiveConsoleVisible)
				toggleInteractiveConsole();
			if (simulationControlsVisible)
				toggleSimulationControls();
			controlToSetFullScreen().setParent(normalParentOfFullScreenControl);
			createOverlay();
			normalParentOfFullScreenControl.layout(true, true);
			destroyFullScreenShell();
			this.setFocus();
		} else {
			adaptToolbarToFullScreen(true);
			fullScreenShell = createFullScreenShell();
			normalParentOfFullScreenControl = controlToSetFullScreen().getParent();
			controlToSetFullScreen().setParent(fullScreenShell);
			createOverlay();
			fullScreenShell.layout(true, true);
			fullScreenShell.setVisible(true);
			getZoomableControls()[0].forceFocus();
			if (GamaPreferences.Displays.DISPLAY_TOOLBAR_FULLSCREEN.getValue())
				toggleSimulationControls();
		}
	}

	private void adaptToolbarToFullScreen(final boolean entering) {
		fs.setImage(GamaIcons.create(entering ? "display.fullscreen3" : "display.fullscreen2").image());

		if (entering) {
			toolbar.button("display.layers2", "Toggle layers controls",
					"Toggle layers controls " + GamaKeyBindings.format(GamaKeyBindings.COMMAND, 'L'),
					new SelectionAdapter() {

						@Override
						public void widgetSelected(final SelectionEvent e) {
							toggleSideControls();
						}

					}, SWT.LEFT);
			toolbar.button("display.overlay2", "Toggle overlay",
					"Toggle bottom overlay " + GamaKeyBindings.format(GamaKeyBindings.COMMAND, 'O'),
					new SelectionAdapter() {

						@Override
						public void widgetSelected(final SelectionEvent e) {
							toggleOverlay();
						}

					}, SWT.LEFT);
			toolbar.button("display.presentation2", "Toggle Interactive Console",
					"Toggle interactive console " + GamaKeyBindings.format(GamaKeyBindings.COMMAND, 'K'),
					new SelectionAdapter() {

						@Override
						public void widgetSelected(final SelectionEvent e) {
							toggleInteractiveConsole();
						}

					}, SWT.LEFT);
			toolbar.sep(GamaToolbarFactory.TOOLBAR_SEP, SWT.LEFT);

			toolbar.button(IGamaIcons.MENU_RUN_ACTION, "Run or pause experiment",
					"Run or pause experiment " + GamaKeyBindings.PLAY_STRING, new SelectionAdapter() {

						@Override
						public void widgetSelected(final SelectionEvent e) {
							final ToolItem item = (ToolItem) e.widget;
							if (!GAMA.isPaused()) {
								item.setImage(GamaIcons.create(IGamaIcons.MENU_RUN_ACTION).image());
								item.setToolTipText("Run experiment " + GamaKeyBindings.PLAY_STRING);
							} else {
								item.setImage(GamaIcons.create("menu.pause4").image());
								item.setToolTipText("Pause experiment " + GamaKeyBindings.PLAY_STRING);
							}
							GAMA.startPauseFrontmostExperiment();

						}

					}, SWT.LEFT);
			toolbar.button("menu.step4", "Step experiment", "Step experiment " + GamaKeyBindings.STEP_STRING,
					new SelectionAdapter() {

						@Override
						public void widgetSelected(final SelectionEvent e) {
							GAMA.stepFrontmostExperiment();

						}

					}, SWT.LEFT);

			toolbar.button("toolbar.stop2", "Closes experiment",
					"Closes experiment (" + GamaKeyBindings.QUIT_STRING + ")", new SelectionAdapter() {

						@Override
						public void widgetSelected(final SelectionEvent e) {
							new Thread(() -> GAMA.closeAllExperiments(true, false)).start();

						}

					}, SWT.LEFT);

		} else
			toolbar.wipe(SWT.LEFT, true);
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

	private Shell createFullScreenShell() {
		final int monitorId = getOutput().getData().fullScreen();
		return WorkbenchHelper.obtainFullScreenShell(monitorId);
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

	@Override
	public void toggleOverlay() {
		overlay.setVisible(!overlay.isVisible());
	}

	@Override
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

	Composite tp;

	public void toggleSimulationControls() {
		if (simulationControlsVisible) {
			toolbar.setParent(tp);
			tp = null;
			parent.getParent().layout(true, true);
			simulationControlsVisible = false;
		} else {
			tp = toolbar.getParent();
			toolbar.setParent(parent.getParent());
			simulationControlsVisible = true;
		}
		toolbar.layout(true, true);
		toolbar.getParent().layout();
	}

	public Composite getSurfaceComposite() {
		return surfaceComposite;
	}

	@Override
	public void ownCreatePartControl(final Composite c) {
		final LayerSideControls side = new LayerSideControls();
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
		side.fill(sidePanel, this);

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

	ToolItem fs = null;

	@Override
	public void createToolItems(final GamaToolbar2 tb) {
		super.createToolItems(tb);

		tb.sep(GamaToolbarFactory.TOOLBAR_SEP, SWT.RIGHT);
		tb.button(IGamaIcons.DISPLAY_TOOLBAR_SNAPSHOT, "Take a snapshot", "Take a snapshot", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				SnapshotMaker.getInstance().doSnapshot(getOutput(), getDisplaySurface(), surfaceComposite);
			}

		}, SWT.RIGHT);

		fs = tb.button("display.fullscreen2", "Toggle fullscreen", "Toggle fullscreen ESC", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				toggleFullScreen();
			}

		}, SWT.RIGHT);
		tb.sep(GamaToolbarFactory.TOOLBAR_SEP, SWT.RIGHT);
		tb.menu(IGamaIcons.MENU_POPULATION, "Browse displayed agents by layers", "Browse through all displayed agents",
				new SelectionAdapter() {

					Menu menu;

					@Override
					public void widgetSelected(final SelectionEvent trigger) {
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
							SnapshotMaker.getInstance().doSnapshot(output, s, surfaceComposite);
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
				SnapshotMaker.getInstance().doSnapshot(output, s, surfaceComposite);
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

	public abstract List<String> getCameraNames();

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

}
