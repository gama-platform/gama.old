package ummisco.gama.ui.views.displays;

import static ummisco.gama.ui.bindings.GamaKeyBindings.COMMAND;
import static ummisco.gama.ui.bindings.GamaKeyBindings.format;
import static ummisco.gama.ui.resources.IGamaIcons.DISPLAY_TOOLBAR_SNAPSHOT;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IWorkbenchPage;

import msi.gama.application.workbench.PerspectiveHelper;
import msi.gama.common.interfaces.IGui;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.outputs.LayeredDisplayData.Changes;
import msi.gama.outputs.LayeredDisplayData.DisplayDataListener;
import msi.gama.runtime.GAMA;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.ui.bindings.GamaKeyBindings;
import ummisco.gama.ui.controls.SimulationSpeedContributionItem;
import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.resources.IGamaColors;
import ummisco.gama.ui.resources.IGamaIcons;
import ummisco.gama.ui.utils.WorkbenchHelper;
import ummisco.gama.ui.views.InteractiveConsoleView;
import ummisco.gama.ui.views.toolbar.GamaCommand;
import ummisco.gama.ui.views.toolbar.GamaToolbar2;
import ummisco.gama.ui.views.toolbar.GamaToolbarFactory;

public class LayeredDisplayDecorator implements DisplayDataListener {

	static {
		DEBUG.OFF();
	}

	protected SWTLayeredDisplayMultiListener keyAndMouseListener;
	protected DisplaySurfaceMenu menuManager;
	protected final LayeredDisplayView view;
	ToolItem fs = null;
	protected Composite normalParentOfFullScreenControl;
	int[] sideControlWeights = new int[] { 30, 70 };
	protected Shell fullScreenShell;
	protected Composite sidePanel;
	public DisplayOverlay overlay;
	public GamaToolbar2 toolbar;

	boolean isOverlayTemporaryVisible, sideControlsVisible, interactiveConsoleVisible;
	protected IPerspectiveListener perspectiveListener;
	final GamaCommand toggleSideControls, toggleOverlay, takeSnapshot, toggleFullScreen, toggleInteractiveConsole,
			runExperiment, stepExperiment, closeExperiment, relaunchExperiment;

	LayeredDisplayDecorator(final LayeredDisplayView view) {
		this.view = view;
		toggleSideControls = new GamaCommand("display.layers2", "Toggle side controls " + format(COMMAND, 'L'),
				e -> toggleSideControls());
		toggleOverlay =
				new GamaCommand("display.overlay2", "Toggle overlay " + format(COMMAND, 'O'), e -> toggleOverlay());
		takeSnapshot =
				new GamaCommand(DISPLAY_TOOLBAR_SNAPSHOT, "Take a snapshot", "Take a snapshot", e -> SnapshotMaker
						.getInstance().doSnapshot(view.getOutput(), view.getDisplaySurface(), view.surfaceComposite));
		toggleFullScreen = new GamaCommand("display.fullscreen2", "Toggle fullscreen ESC", e -> toggleFullScreen());
		toggleInteractiveConsole = new GamaCommand("display.presentation2",
				"Toggle interactive console " + format(COMMAND, 'K'), e -> toggleInteractiveConsole());
		runExperiment = new GamaCommand(IGamaIcons.MENU_RUN_ACTION,
				"Run or pause experiment " + GamaKeyBindings.PLAY_STRING, e -> {
					final Item item = (Item) e.widget;
					if (!GAMA.isPaused()) {
						item.setImage(GamaIcons.create(IGamaIcons.MENU_RUN_ACTION).image());
					} else {
						item.setImage(GamaIcons.create("menu.pause4").image());
					}
					GAMA.startPauseFrontmostExperiment();

				});
		stepExperiment = new GamaCommand("menu.step4", "Step experiment " + GamaKeyBindings.STEP_STRING,
				e -> GAMA.stepFrontmostExperiment());
		closeExperiment = new GamaCommand("toolbar.stop2", "Closes experiment " + GamaKeyBindings.QUIT_STRING,
				e -> new Thread(() -> GAMA.closeAllExperiments(true, false)).start());
		relaunchExperiment = new GamaCommand("menu.reload4", "Reload experiment" + GamaKeyBindings.RELOAD_STRING,
				e -> GAMA.reloadFrontmostExperiment());

	}

	public void toggleFullScreen() {
		if (isFullScreen()) {
			if (interactiveConsoleVisible) {
				toggleInteractiveConsole();
			}
			view.controlToSetFullScreen().setParent(normalParentOfFullScreenControl);
			createOverlay();
			normalParentOfFullScreenControl.layout(true, true);
			destroyFullScreenShell();
			adaptToolbar();
			view.setFocus();
		} else {
			fullScreenShell = createFullScreenShell();
			if (DEBUG.IS_ON()) {
				DEBUG.SECTION(" FULLSCREEN WITH SIZE " + fullScreenShell.getSize());
			}
			normalParentOfFullScreenControl = view.controlToSetFullScreen().getParent();
			final Control display = view.controlToSetFullScreen();
			display.setParent(fullScreenShell);
			createOverlay();
			adaptToolbar();
			fullScreenShell.layout(true, true);
			fullScreenShell.setVisible(true);
			view.fullScreenSet();
			display.setFocus();
		}
	}

	Composite tp;

	public void toggleToolbar() {
		if (toolbar.isVisible()) {
			toolbar.hide();
		} else {
			toolbar.show();
		}
	}

	private void adaptToolbar() {
		final boolean isFullScreen = isFullScreen();
		fs.setImage(GamaIcons.create(isFullScreen ? "display.fullscreen3" : "display.fullscreen2").image());
		toolbar.wipe(SWT.LEFT, true);
		if (isFullScreen) { // We're entering full screen
			toolbar.button(toggleSideControls, SWT.LEFT);
			toolbar.button(toggleOverlay, SWT.LEFT);
			toolbar.button(toggleInteractiveConsole, SWT.LEFT);
			toolbar.sep(GamaToolbarFactory.TOOLBAR_SEP, SWT.LEFT);
			toolbar.button(runExperiment, SWT.LEFT);
			toolbar.button(stepExperiment, SWT.LEFT);
			toolbar.control(SimulationSpeedContributionItem.getInstance().createControl(toolbar.getToolbar(SWT.LEFT)),
					SimulationSpeedContributionItem.totalWidth(), SWT.LEFT);
			toolbar.button(relaunchExperiment, SWT.LEFT);
			toolbar.button(closeExperiment, SWT.LEFT);
			tp = toolbar.getParent();
			final Composite forToolbar =
					GamaToolbarFactory.createToolbarComposite(view.getParentComposite().getParent());
			toolbar.setParent(forToolbar);
		} else { // We're leaving full screen
			final Composite forToolbar = toolbar.getParent();
			toolbar.setParent(tp);
			tp = null;
			if (forToolbar != null && !forToolbar.isDisposed()) {
				forToolbar.dispose();
			}
			view.getParentComposite().getParent().layout(true, true);
		}
		if (toolbar.isVisible()) {
			toolbar.show();
			toolbar.refresh(true);
		} else {
			toolbar.hide();
		}
		toolbar.getParent().layout();

	}

	public void createOverlay() {
		boolean wasVisible = false;
		if (overlay != null) {
			wasVisible = overlay.isVisible();
			overlay.dispose();
		}
		overlay = new DisplayOverlay(view, view.surfaceComposite, view.getOutput().getOverlayProvider());
		if (wasVisible) {
			overlay.setVisible(true);
		}
		overlay.setVisible(GamaPreferences.Displays.CORE_OVERLAY.getValue());
		if (overlay.isVisible()) {
			overlay.update();
		}
	}

	public void createSidePanel(final SashForm form) {

		sidePanel = new Composite(form, SWT.BORDER);
		final GridLayout layout = new GridLayout(1, true);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		sidePanel.setLayout(layout);
		sidePanel.setBackground(IGamaColors.WHITE.color());
	}

	public void createDecorations(final SashForm form) {
		final LayerSideControls side = new LayerSideControls();
		side.fill(sidePanel, view);
		createOverlay();
		addPerspectiveListener();
		keyAndMouseListener = new SWTLayeredDisplayMultiListener(this, view.getDisplaySurface());
		menuManager = new DisplaySurfaceMenu(view.getDisplaySurface(), view.getParentComposite(), presentationMenu());
		if (view.getOutput().getData().fullScreen() > -1) {
			WorkbenchHelper.runInUI("Fullscreen", 100, (m) -> toggleFullScreen());
		}
	}

	public void addPerspectiveListener() {
		perspectiveListener = new IPerspectiveListener() {
			boolean previousState = false;

			@Override
			public void perspectiveChanged(final IWorkbenchPage page, final IPerspectiveDescriptor perspective,
					final String changeId) {}

			@Override
			public void perspectiveActivated(final IWorkbenchPage page, final IPerspectiveDescriptor perspective) {
				if (perspective.getId().equals(PerspectiveHelper.PERSPECTIVE_MODELING_ID)) {
					if (view.getOutput() != null && view.getDisplaySurface() != null) {
						if (!GamaPreferences.Displays.CORE_DISPLAY_PERSPECTIVE.getValue()) {
							previousState = view.getOutput().isPaused();
							view.getOutput().setPaused(true);
						}
					}
					if (overlay != null) {
						overlay.hide();
					}
				} else {
					if (!GamaPreferences.Displays.CORE_DISPLAY_PERSPECTIVE.getValue()) {
						if (view.getOutput() != null && view.getDisplaySurface() != null) {
							view.getOutput().setPaused(previousState);
						}
					}
					if (overlay != null) {
						overlay.update();
					}
				}
			}
		};
		WorkbenchHelper.getWindow().addPerspectiveListener(perspectiveListener);
	}

	public boolean isFullScreen() {
		return fullScreenShell != null;
	}

	private Shell createFullScreenShell() {
		final int monitorId = view.getOutput().getData().fullScreen();
		return WorkbenchHelper.obtainFullScreenShell(monitorId);
	}

	private void destroyFullScreenShell() {
		if (fullScreenShell == null) { return; }
		fullScreenShell.close();
		fullScreenShell.dispose();
		fullScreenShell = null;
	}

	protected Runnable displayOverlay = () -> {
		if (overlay == null) { return; }
		updateOverlay();
	};

	protected void updateOverlay() {
		if (overlay == null) { return; }
		if (view.forceOverlayVisibility()) {
			if (!overlay.isVisible()) {
				isOverlayTemporaryVisible = true;
				overlay.setVisible(true);
			}
		} else {
			if (isOverlayTemporaryVisible) {
				isOverlayTemporaryVisible = false;
				overlay.setVisible(false);
			}
		}
		if (overlay.isVisible()) {
			overlay.update();
		}

	}

	public void toggleOverlay() {
		overlay.setVisible(!overlay.isVisible());
	}

	public void toggleSideControls() {
		if (sideControlsVisible) {
			sideControlWeights = view.form.getWeights();
			view.form.setMaximizedControl(view.getParentComposite().getParent());
			sideControlsVisible = false;
		} else {
			view.form.setWeights(sideControlWeights);
			view.form.setMaximizedControl(null);
			sideControlsVisible = true;
		}
	}

	public void toggleInteractiveConsole() {
		if (!sideControlsVisible) {
			toggleSideControls();
		}
		final InteractiveConsoleView view =
				(InteractiveConsoleView) WorkbenchHelper.findView(IGui.INTERACTIVE_CONSOLE_VIEW_ID, null, true);
		if (view == null) { return; }
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

	private MenuManager presentationMenu() {
		final MenuManager mm = new MenuManager();

		mm.setMenuText("Presentation");
		mm.setImageDescriptor(GamaIcons.create("display.sidebar2").descriptor());
		mm.add(toggleSideControls.toAction());
		mm.add(toggleOverlay.toAction());
		mm.add(new Action("Toggle toolbar " + GamaKeyBindings.format(GamaKeyBindings.COMMAND, 'T'),
				GamaIcons.create("display.fullscreen.toolbar2").descriptor()) {

			@Override
			public boolean isEnabled() {
				return true;
			}

			@Override
			public void run() {
				toggleToolbar();
			}
		});
		return mm;
	}

	public void createToolItems(final GamaToolbar2 tb) {
		toolbar = tb;
		tb.sep(GamaToolbarFactory.TOOLBAR_SEP, SWT.RIGHT);
		tb.button(takeSnapshot, SWT.RIGHT);
		fs = tb.button(toggleFullScreen, SWT.RIGHT);
		tb.sep(GamaToolbarFactory.TOOLBAR_SEP, SWT.RIGHT);
		tb.menu(IGamaIcons.MENU_POPULATION, "Browse displayed agents by layers", "Browse through all displayed agents",
				trigger -> menuManager.buildToolbarMenu(trigger, (ToolItem) trigger.widget), SWT.RIGHT);
	}

	public void dispose() {
		// FIXME Remove the listeners
		try {
			WorkbenchHelper.getWindow().removePerspectiveListener(perspectiveListener);
		} catch (final Exception e) {

		}
		if (keyAndMouseListener != null) {
			keyAndMouseListener.dispose();
		}
		if (overlay != null) {
			overlay.close();
		}

		if (menuManager != null) {
			menuManager.disposeMenu();
		}
		menuManager = null;
		toolbar = null;
		fs = null;
		tp = null;
		sidePanel = null;
		normalParentOfFullScreenControl = null;
		if (fullScreenShell != null && !fullScreenShell.isDisposed()) {
			fullScreenShell.dispose();
		}
		fullScreenShell = null;
	}

	@Override
	public void changed(final Changes changes, final Object value) {
		switch (changes) {
			case ZOOM:
				WorkbenchHelper.asyncRun(() -> updateOverlay());
				break;
			default:
				break;
		}

	}

}
