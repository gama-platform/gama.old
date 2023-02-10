/*******************************************************************************************************
 *
 * LayeredDisplayDecorator.java, in ummisco.gama.ui.experiment, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.views.displays;

import static ummisco.gama.ui.bindings.GamaKeyBindings.COMMAND;
import static ummisco.gama.ui.bindings.GamaKeyBindings.format;
import static ummisco.gama.ui.controls.SimulationSpeedContributionItem.create;
import static ummisco.gama.ui.controls.SimulationSpeedContributionItem.totalWidth;
import static ummisco.gama.ui.resources.IGamaIcons.DISPLAY_TOOLBAR_SNAPSHOT;

import java.util.function.Function;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;

import msi.gama.application.workbench.PerspectiveHelper;
import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IDisposable;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.outputs.LayeredDisplayData.Changes;
import msi.gama.outputs.LayeredDisplayData.DisplayDataListener;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.PlatformHelper;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.ui.bindings.GamaKeyBindings;
import ummisco.gama.ui.menus.GamaColorMenu;
import ummisco.gama.ui.menus.GamaMenu;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.resources.IGamaIcons;
import ummisco.gama.ui.utils.ViewsHelper;
import ummisco.gama.ui.utils.WorkbenchHelper;
import ummisco.gama.ui.views.toolbar.GamaCommand;
import ummisco.gama.ui.views.toolbar.GamaToolbar2;
import ummisco.gama.ui.views.toolbar.GamaToolbarFactory;

/**
 * The Class LayeredDisplayDecorator.
 */
public class LayeredDisplayDecorator implements DisplayDataListener {

	static {
		DEBUG.OFF();
	}

	/** The key and mouse listener. */
	protected IDisposable keyAndMouseListener;

	/** The menu manager. */
	protected DisplaySurfaceMenu menuManager;

	/** The view. */
	public final LayeredDisplayView view;

	/** The fs. */
	ToolItem fs = null;

	/** The normal parent of full screen control. */
	protected Composite normalParentOfFullScreenControl, normalParentOfToolbar;

	/** The side control weights. */
	int[] sideControlWeights = { 30, 70 };

	/** The full screen shell. */
	protected Shell fullScreenShell;

	/** The overlay. */
	public DisplayOverlay overlay;

	/** The toolbar. */
	public GamaToolbar2 toolbar;

	/** The interactive console visible. */
	boolean isOverlayTemporaryVisible, sideControlsVisible, interactiveConsoleVisible;

	/** The perspective listener. */
	protected IPerspectiveListener perspectiveListener;

	/** The relaunch experiment. */
	GamaCommand toggleOverlay, takeSnapshot, antiAlias, toggleFullScreen, runExperiment, stepExperiment,
			closeExperiment, relaunchExperiment;

	/**
	 * Instantiates a new layered display decorator.
	 *
	 * @param view
	 *            the view
	 */
	LayeredDisplayDecorator(final LayeredDisplayView view) {
		this.view = view;
		createCommands();
		final IPartService ps = ((IWorkbenchPart) view).getSite().getService(IPartService.class);
		ps.addPartListener(overlayListener);

	}

	/**
	 * Creates the commands.
	 *
	 * @param view
	 *            the view
	 */
	private void createCommands() {
		toggleOverlay =
				new GamaCommand("display.overlay2", "Toggle overlay " + format(COMMAND, 'O'), e -> toggleOverlay());
		takeSnapshot = new GamaCommand(DISPLAY_TOOLBAR_SNAPSHOT, "Take a snapshot", e -> view.takeSnapshot());
		antiAlias = new GamaCommand("display.antialias", "Turn antialias on/off",
				e -> view.getOutput().getData().setAntialias(!view.getOutput().getData().isAntialias()));
		toggleFullScreen = new GamaCommand("display.fullscreen2", "Toggle fullscreen ESC", e -> toggleFullScreen());
		runExperiment = new GamaCommand(IGamaIcons.MENU_RUN_ACTION,
				"Run or pause experiment " + GamaKeyBindings.PLAY_STRING, e -> {

					final Item item = (Item) e.widget;
					if (!GAMA.isPaused()) {
						item.setImage(GamaIcons.create(IGamaIcons.MENU_RUN_ACTION).image());
					} else {
						item.setImage(GamaIcons.create(IGamaIcons.MENU_PAUSE_ACTION).image());
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

	/** The overlay listener. */
	private final IPartListener2 overlayListener = new IPartListener2() {

		private boolean ok(final IWorkbenchPartReference partRef) {
			return partRef.getPart(false) == view && view.surfaceComposite != null
					&& !view.surfaceComposite.isDisposed() && !view.isFullScreen();
		}

		@Override
		public void partActivated(final IWorkbenchPartReference partRef) {
			if (ok(partRef)) {
				// DEBUG.STACK();
				WorkbenchHelper.runInUI("", 0, m -> {
					DEBUG.OUT("Part Activated:" + partRef.getTitle());
					view.showCanvas();
					if (overlay != null) { overlay.display(); }
				});
			}
		}

		@Override
		public void partClosed(final IWorkbenchPartReference partRef) {
			if (ok(partRef) && overlay != null) { overlay.close(); }
		}

		@Override
		public void partDeactivated(final IWorkbenchPartReference partRef) {
			// On macOS, this event is wrongly sent when tabs are not displayed for the views and another display is
			// selected
			// if (PlatformHelper.isMac() && !PerspectiveHelper.keepTabs()) return;
			// if (ok(partRef)) {
			// DEBUG.OUT("Part Deactivated:" + partRef.getTitle());
			// WorkbenchHelper.asyncRun(() -> {
			// if (overlay != null) { overlay.hide(); }
			// view.hideCanvas();
			// });
			// }
		}

		@Override
		public void partHidden(final IWorkbenchPartReference partRef) {
			// On macOS, this event is wrongly sent when tabs are not displayed for the views and another display is
			// selected. After tests, the same happens on Linux and Windows -- so the test is generalized.
			if (/* (PlatformHelper.isMac() || PlatformHelper.isLinux()) && */ !PerspectiveHelper.keepTabs()) return;
			if (ok(partRef)) {
				WorkbenchHelper.runInUI("", 0, m -> {
					DEBUG.OUT("Part hidden:" + partRef.getTitle());
					view.hideCanvas();
					if (overlay != null) { overlay.hide(); }
				});
			}
		}

		@Override
		public void partVisible(final IWorkbenchPartReference partRef) {
			if (ok(partRef)) {
				WorkbenchHelper.runInUI("", 0, m -> {
					DEBUG.OUT("Part Visible:" + partRef.getTitle());
					view.showCanvas();
					IDisplaySurface s = view.getDisplaySurface();
					if (s != null) { s.getOutput().update(); }
					if (overlay != null) { overlay.display(); }
				});
			}
		}

	};

	/**
	 * Toggle full screen.
	 */
	public void toggleFullScreen() {
		if (isFullScreen()) {
			// DEBUG.OUT("Is already full screen in display thread " + WorkbenchHelper.isDisplayThread());
			fs.setImage(GamaIcons.create("display.fullscreen2").image());
			// Toolbar
			if (!toolbar.isDisposed()) {
				toolbar.wipe(SWT.LEFT, true);
				toolbar.setParent(normalParentOfToolbar);
				normalParentOfToolbar.requestLayout();
			}
			view.getCentralPanel().setParent(normalParentOfFullScreenControl);
			createOverlay();
			normalParentOfFullScreenControl.requestLayout();
			destroyFullScreenShell();
		} else {
			ViewsHelper.activate(view);
			fullScreenShell = createFullScreenShell();
			if (fullScreenShell == null) return;
			fs.setImage(GamaIcons.create("display.fullscreen3").image());
			normalParentOfFullScreenControl = view.getCentralPanel().getParent();
			view.getCentralPanel().setParent(fullScreenShell);
			fullScreenShell.layout(true, true);
			fullScreenShell.setVisible(true);
			createOverlay();
			// Toolbar
			if (!toolbar.isDisposed()) {
				toolbar.wipe(SWT.LEFT, true);
				addFullscreenToolbarCommands();
				normalParentOfToolbar = toolbar.getParent();
				toolbar.setParent(fullScreenShell);
			}
		}
		toolbar.refresh(true);
		if (overlay.isVisible()) {
			WorkbenchHelper.runInUI("Overlay", 50, m -> {
				toggleOverlay();
				toggleOverlay();
			});
		}
		view.focusCanvas();
	}

	/**
	 * Toggle toolbar.
	 */
	public void toggleToolbar() {
		// If in fullscreen the hierarchy is simplified
		if (isFullScreen()) {
			boolean visible = toolbar.isVisible();
			toolbar.setVisible(!visible);
			((GridData) toolbar.getLayoutData()).exclude = visible;
		} else if (toolbar.isVisible()) {
			toolbar.hide();
		} else {
			toolbar.show();
		}
		toolbar.getParent().requestLayout();
	}

	/**
	 * Adds the fullscreen toolbar commands.
	 */
	public void addFullscreenToolbarCommands() {
		toolbar.button(toggleOverlay, SWT.LEFT);
		toolbar.sep(GamaToolbarFactory.TOOLBAR_SEP, SWT.LEFT);
		final ToolItem item = toolbar.button(runExperiment, SWT.LEFT);
		if (GAMA.isPaused()) {
			item.setImage(GamaIcons.create(IGamaIcons.MENU_RUN_ACTION).image());
		} else {
			item.setImage(GamaIcons.create(IGamaIcons.MENU_PAUSE_ACTION).image());
		}
		toolbar.button(stepExperiment, SWT.LEFT);
		toolbar.control(create(toolbar.getToolbar(SWT.LEFT)), totalWidth(), SWT.LEFT);
		toolbar.button(relaunchExperiment, SWT.LEFT);
		toolbar.button(closeExperiment, SWT.LEFT);
	}

	/**
	 * Creates the overlay.
	 */
	public void createOverlay() {
		boolean wasVisible = false;
		if (overlay != null) {
			wasVisible = overlay.isVisible();
			overlay.dispose();
		}
		overlay = new DisplayOverlay(view, view.surfaceComposite, view.getOutput().getOverlayProvider());
		if (wasVisible) { overlay.setVisible(true); }

		if (overlay.isVisible()) {
			overlay.relocate();
			overlay.update();
		}
	}

	/**
	 * Creates the decorations.
	 *
	 * @param form
	 *            the form
	 */
	public void createDecorations() {
		createOverlay();
		addPerspectiveListener();
		keyAndMouseListener = view.getMultiListener();
		menuManager = new DisplaySurfaceMenu(view.getDisplaySurface(), view.getParentComposite(), presentationMenu());
		final boolean tbVisible = view.getOutput().getData().isToolbarVisible();
		WorkbenchHelper.runInUI("Toolbar", 0, m -> {
			if (tbVisible) {
				toolbar.show();
			} else {
				toolbar.hide();
			}
		});

	}

	/**
	 * Adds the perspective listener.
	 */
	private void addPerspectiveListener() {
		perspectiveListener = new IPerspectiveListener() {
			boolean previousState = false;

			@Override
			public void perspectiveChanged(final IWorkbenchPage page, final IPerspectiveDescriptor perspective,
					final String changeId) {}

			@Override
			public void perspectiveActivated(final IWorkbenchPage page, final IPerspectiveDescriptor perspective) {
				if (PerspectiveHelper.PERSPECTIVE_MODELING_ID.equals(perspective.getId())) {
					if (view.getOutput() != null && view.getDisplaySurface() != null
							&& !GamaPreferences.Displays.CORE_DISPLAY_PERSPECTIVE.getValue()) {
						previousState = view.getOutput().isPaused();
						view.getOutput().setPaused(true);
					}
					// Seems necessary in addition to the IPartListener
					WorkbenchHelper.run(() -> {
						if (PlatformHelper.isMac() && overlay != null) { overlay.hide(); }
						view.hideCanvas();
					});
				} else {
					// Issue #2639
					if (PlatformHelper.isMac() && !view.isOpenGL()) {
						final IDisplaySurface ds = view.getDisplaySurface();
						if (ds != null) { ds.updateDisplay(true); }
					}
					if (!GamaPreferences.Displays.CORE_DISPLAY_PERSPECTIVE.getValue() && view.getOutput() != null
							&& view.getDisplaySurface() != null) {
						view.getOutput().setPaused(previousState);
					}
					// Necessary in addition to the IPartListener as there is no way to distinguish between the wrong
					// "hidden" event and the good one when there are no tabs.
					WorkbenchHelper.asyncRun(() -> {
						if (PlatformHelper.isMac() && overlay != null) { overlay.display(); }
						// view.showCanvas();
					});
				}

			}
		};
		WorkbenchHelper.getWindow().addPerspectiveListener(perspectiveListener);
	}

	/**
	 * Checks if is full screen.
	 *
	 * @return true, if is full screen
	 */
	public boolean isFullScreen() { return fullScreenShell != null; }

	/**
	 * Creates the full screen shell.
	 *
	 * @return the shell
	 */
	private Shell createFullScreenShell() {
		final int monitorId = view.getOutput().getData().fullScreen();
		final Monitor[] monitors = WorkbenchHelper.getDisplay().getMonitors();
		int monitorId1 = Math.min(monitors.length - 1, Math.max(0, monitorId));
		final Rectangle bounds = monitors[monitorId1].getBounds();
		if (ViewsHelper.registerFullScreenView(monitorId1, view)) {
			final Shell fullScreenShell = new Shell(WorkbenchHelper.getDisplay(), SWT.NO_TRIM | SWT.ON_TOP);
			fullScreenShell.setBounds(bounds);
			// For DEBUG purposes:
			// fullScreenShell.setBounds(new Rectangle(0, 0, bounds.width / 2, bounds.height / 2));
			fullScreenShell.setLayout(shellLayout());
			return fullScreenShell;
		}
		return null;
	}

	/**
	 * Destroy full screen shell.
	 */
	private void destroyFullScreenShell() {
		if (fullScreenShell == null) return;
		fullScreenShell.close();
		fullScreenShell.dispose();
		fullScreenShell = null;
		ViewsHelper.unregisterFullScreenView(view);
		ViewsHelper.activate(view);
	}

	/**
	 * Shell layout.
	 *
	 * @return the layout used for the contents of the shell
	 */
	private GridLayout shellLayout() {
		final GridLayout layout = new GridLayout(1, false);
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		layout.marginWidth = 0;
		final int margin = 0; // REDUCED_VIEW_TOOLBAR_HEIGHT.getValue() ? -1 : 0;
		layout.marginTop = margin;
		layout.marginBottom = margin;
		layout.marginHeight = margin;
		return layout;
	}

	/** The display overlay. */
	protected Runnable displayOverlay = () -> {
		if (overlay == null) return;
		updateOverlay();
	};

	/**
	 * Update overlay.
	 */
	protected void updateOverlay() {
		if (overlay == null) return;
		if (view.forceOverlayVisibility()) {
			if (!overlay.isVisible()) {
				isOverlayTemporaryVisible = true;
				overlay.setVisible(true);
			}
		} else if (isOverlayTemporaryVisible) {
			isOverlayTemporaryVisible = false;
			overlay.setVisible(false);
		}
		if (overlay.isVisible()) { overlay.update(); }

	}

	/**
	 * Toggle overlay.
	 */
	public void toggleOverlay() {
		overlay.setVisible(!overlay.isVisible());
	}

	/**
	 * Presentation menu.
	 *
	 * @return the menu manager
	 */
	private Function<Menu, Menu> presentationMenu() {

		return parentMenu -> {
			Menu sub = GamaMenu.sub(parentMenu, "Presentation", "", GamaIcons.create("display.sidebar2").image());

			toggleOverlay.toItem(sub);
			GamaMenu.action(sub, "Toggle toolbar " + GamaKeyBindings.format(GamaKeyBindings.COMMAND, 'T'),
					t -> toggleToolbar(),
					GamaIcons.create(this.isFullScreen() ? "display.fullscreen.toolbar2" : "display.toolbar2").image());
			GamaColorMenu.addColorSubmenuTo(sub, "Background", c -> {
				view.getDisplaySurface().getData().setBackgroundColor(c);
				view.getDisplaySurface().updateDisplay(true);
			});
			return sub;
		};

	}

	/**
	 * Creates the tool items.
	 *
	 * @param tb
	 *            the tb
	 */
	public void createToolItems(final GamaToolbar2 tb) {
		toolbar = tb;
		tb.setBackgroundColor(GamaColors.get(view.getOutput().getData().getToolbarColor()).color());
		tb.sep(GamaToolbarFactory.TOOLBAR_SEP, SWT.RIGHT);
		tb.button(takeSnapshot, SWT.RIGHT);
		ToolItem item = tb.check(antiAlias, SWT.RIGHT);
		tb.setSelection(item, view.getOutput().getData().isAntialias());
		fs = tb.button(toggleFullScreen, SWT.RIGHT);
		tb.sep(GamaToolbarFactory.TOOLBAR_SEP, SWT.RIGHT);
		tb.menu("display.layers", "Browse displayed agents by layers", "Properties and contents of layers",
				trigger -> menuManager.buildToolbarMenu(trigger, (ToolItem) trigger.widget), SWT.RIGHT);

	}

	/**
	 * Dispose.
	 */
	public void dispose() {
		// FIXME Remove the listeners
		try {
			WorkbenchHelper.getWindow().removePerspectiveListener(perspectiveListener);
			WorkbenchHelper.getPage().removePartListener(overlayListener);
		} catch (final Exception e) {}
		if (keyAndMouseListener != null) {
			keyAndMouseListener.dispose();
			keyAndMouseListener = null;
		}
		if (overlay != null) {
			overlay.close();
			overlay = null;
		}

		if (menuManager != null) {
			menuManager.disposeMenu();
			menuManager = null;
		}
		if (toolbar != null && !toolbar.isDisposed()) {
			toolbar.dispose();
			toolbar = null;
		}

		fs = null;
		normalParentOfToolbar = null;
		// sidePanel = null;
		normalParentOfFullScreenControl = null;
		if (fullScreenShell != null && !fullScreenShell.isDisposed()) {
			fullScreenShell.dispose();
			fullScreenShell = null;
		}

	}

	@Override
	public void changed(final Changes changes, final Object value) {
		switch (changes) {
			case ZOOM:
				WorkbenchHelper.asyncRun(this::updateOverlay);
				break;
			default:
				break;
		}

	}

}
