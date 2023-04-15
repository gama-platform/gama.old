/*******************************************************************************************************
 *
 * PerspectiveHelper.java, in msi.gama.application, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.application.workbench;

import java.util.List;
import java.util.Map;

import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.impl.PerspectiveImpl;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.internal.registry.PerspectiveDescriptor;
import org.eclipse.ui.internal.registry.PerspectiveRegistry;

import msi.gama.common.interfaces.IGui;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.kernel.model.IModel;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class PerspectiveHelper.
 */
public class PerspectiveHelper {

	static {
		DEBUG.OFF();
	}

	/** The Constant BOTTOM_TRIM_ID. */
	// id of the status bar, as defined in the LegacyIDE.e4xmi
	private static final String BOTTOM_TRIM_ID = "org.eclipse.ui.trim.status"; //$NON-NLS-1$

	/** The Constant PERSPECTIVE_MODELING_ID. */
	public static final String PERSPECTIVE_MODELING_ID = IGui.PERSPECTIVE_MODELING_ID;

	/** The Constant PERSPECTIVE_SIMULATION_ID. */
	public static final String PERSPECTIVE_SIMULATION_ID = "msi.gama.application.perspectives.SimulationPerspective";

	/** The Constant PERSPECTIVE_SIMULATION_FRAGMENT. */
	public static final String PERSPECTIVE_SIMULATION_FRAGMENT = "Simulation";

	/** The current perspective id. */
	public static String currentPerspectiveId = PERSPECTIVE_MODELING_ID;

	/** The current simulation perspective. */
	public static volatile SimulationPerspectiveDescriptor currentSimulationPerspective = null;

	/** The active editor. */
	public static IEditorInput activeEditor;

	/**
	 * Matches.
	 *
	 * @param id
	 *            the id
	 * @return true, if successful
	 */
	static boolean matches(final String id) {
		return !PerspectiveHelper.PERSPECTIVE_SIMULATION_ID.equals(id) && id.contains(PERSPECTIVE_SIMULATION_FRAGMENT);
	}

	/**
	 * Clean perspectives.
	 */
	public static void cleanPerspectives() {
		final EModelService e = PlatformUI.getWorkbench().getService(EModelService.class);
		final MApplication a = PlatformUI.getWorkbench().getService(MApplication.class);

		final List<PerspectiveImpl> perspectives = e.findElements(a, PerspectiveImpl.class, EModelService.ANYWHERE,
				element -> matches(element.getElementId()));
		for (final PerspectiveImpl p : perspectives) {
			// DEBUG.OUT("Dirty perspective implementation found and removed: " + p.getElementId());
			p.getParent().getChildren().remove(p);
		}

		final IPerspectiveRegistry reg = PlatformUI.getWorkbench().getPerspectiveRegistry();
		for (final IPerspectiveDescriptor desc : reg.getPerspectives()) {
			if (matches(desc.getId())) {
				// DEBUG.OUT("Dirty perspective descriptor found and removed: " + desc.getId());
				reg.deletePerspective(desc);
			}
		}

		// DEBUG.OUT("Current perspectives: " + listCurrentPerspectives());
	}

	/**
	 * Delete perspective from application.
	 *
	 * @param d
	 *            the d
	 */
	public static void deletePerspectiveFromApplication(final IPerspectiveDescriptor d) {
		final MApplication a = PlatformUI.getWorkbench().getService(MApplication.class);
		final EModelService e = PlatformUI.getWorkbench().getService(EModelService.class);
		final List<PerspectiveImpl> perspectives = e.findElements(a, PerspectiveImpl.class, EModelService.ANYWHERE,
				element -> element.getElementId().contains(d.getId()));
		for (final PerspectiveImpl p : perspectives) {
			// DEBUG.OUT("Dirty perspective implementation found and removed: " + p.getElementId());
			p.getParent().getChildren().remove(p);
		}
	}

	/**
	 * Gets the perspective registry.
	 *
	 * @return the perspective registry
	 */
	public static IPerspectiveRegistry getPerspectiveRegistry() {
		return PlatformUI.getWorkbench().getPerspectiveRegistry();
	}

	/**
	 * Checks if is modeling perspective.
	 *
	 * @return true, if is modeling perspective
	 */
	public static boolean isModelingPerspective() { return PERSPECTIVE_MODELING_ID.equals(currentPerspectiveId); }

	/**
	 * Checks if is simulation perspective.
	 *
	 * @return true, if is simulation perspective
	 */
	public static boolean isSimulationPerspective() { return isSimulationPerspective(currentPerspectiveId); }

	/**
	 * Checks if is simulation perspective.
	 *
	 * @param perspectiveId
	 *            the perspective id
	 * @return true, if is simulation perspective
	 */
	private static boolean isSimulationPerspective(final String perspectiveId) {
		return perspectiveId.contains(PERSPECTIVE_SIMULATION_FRAGMENT);
	}

	/**
	 * Open modeling perspective.
	 *
	 * @param immediately
	 *            the immediately
	 * @param memorizeEditors
	 *            the memorize editors
	 * @return true, if successful
	 */
	public static final boolean openModelingPerspective(final boolean immediately, final boolean memorizeEditors) {
		// AD 08/18: turn off autosave to prevent workspace corruption
		return openPerspective(PERSPECTIVE_MODELING_ID, immediately, false, memorizeEditors);
	}

	/**
	 * Gets the trim status.
	 *
	 * @param window
	 *            the window
	 * @return the trim status
	 */
	/* Get the MUIElement representing the status bar for the given window */
	private static MUIElement getTrimStatus(final WorkbenchWindow window) {
		final EModelService modelService = window.getService(EModelService.class);
		final MUIElement searchRoot = window.getModel();
		return modelService.find(BOTTOM_TRIM_ID, searchRoot);
	}

	/**
	 * Show bottom tray.
	 *
	 * @param window
	 *            the window
	 * @param show
	 *            the show
	 */
	public static void showBottomTray(final WorkbenchWindow window, final Boolean show) {

		final MUIElement trimStatus = getTrimStatus(window);
		if (trimStatus != null) {
			// toggle statusbar visibility
			trimStatus.setVisible(show);
		}

	}

	/**
	 * Switch to simulation perspective.
	 *
	 * @return true, if successful
	 */
	public static final boolean switchToSimulationPerspective() {
		if (currentSimulationPerspective == null) return false;
		IWorkbenchPage activePage = null;
		try {
			activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		} catch (final Exception e) {
			e.printStackTrace();
		}
		if (activePage == null) return false;
		final IWorkbenchPage page = activePage;
		final WorkbenchWindow window = (WorkbenchWindow) page.getWorkbenchWindow();
		if (page.getPerspective().equals(currentSimulationPerspective)) return true;
		Display.getDefault().asyncExec(() -> {
			memorizeActiveEditor(page);
			try {
				DEBUG.OUT("Switching to " + currentSimulationPerspective.getId());
				page.setPerspective(currentSimulationPerspective);
			} catch (final NullPointerException e) {
				// DEBUG.ERR(
				// "NPE in WorkbenchPage.setPerspective(). See Issue #1602.
				// Working around the bug in e4...");
				page.setPerspective(currentSimulationPerspective);
			} catch (final Exception e) {
				DEBUG.OUT("Error in setPerspective():" + e.getMessage());
			}
			final Boolean showControls = keepControls();
			if (showControls != null) { window.setCoolBarVisible(showControls); }
			final Boolean keepTray = keepTray();
			if (keepTray != null) { showBottomTray(window, keepTray); }
			applyActiveEditor(page);
		});
		currentPerspectiveId = currentSimulationPerspective.getId();
		return true;
	}

	/**
	 * Open simulation perspective.
	 *
	 * @param model
	 *            the model
	 * @param experimentName
	 *            the experiment name
	 * @return true, if successful
	 */
	public static final boolean openSimulationPerspective(final IModel model, final String experimentName) {
		if (model == null) return false;
		final String name = getNewPerspectiveName(model.getName(), experimentName);
		return openPerspective(name, true, false, true);
	}

	/**
	 * Gets the simulation descriptor.
	 *
	 * @return the simulation descriptor
	 */
	static PerspectiveDescriptor getSimulationDescriptor() {
		return (PerspectiveDescriptor) getPerspectiveRegistry().findPerspectiveWithId(PERSPECTIVE_SIMULATION_ID);
	}

	/**
	 * Find or build perspective with id.
	 *
	 * @param id
	 *            the id
	 * @return the i perspective descriptor
	 */
	private static IPerspectiveDescriptor findOrBuildPerspectiveWithId(final String id) {
		if (currentSimulationPerspective != null && currentSimulationPerspective.getId().equals(id))
			return currentSimulationPerspective;
		final PerspectiveRegistry pr = (PerspectiveRegistry) getPerspectiveRegistry();
		IPerspectiveDescriptor tempDescriptor = pr.findPerspectiveWithId(id);
		if (tempDescriptor == null) {
			getPerspectiveRegistry()
					.revertPerspective(getPerspectiveRegistry().findPerspectiveWithId(PERSPECTIVE_SIMULATION_ID));
			tempDescriptor = new SimulationPerspectiveDescriptor(id);
		}
		return tempDescriptor;
	}

	/**
	 * Dirty save perspective.
	 *
	 * @param sp
	 *            the sp
	 */
	@SuppressWarnings ({ "rawtypes", "unchecked" })
	static void dirtySavePerspective(final SimulationPerspectiveDescriptor sp) {
		try {
			final java.lang.reflect.Field descField = PerspectiveRegistry.class.getDeclaredField("descriptors");
			descField.setAccessible(true);
			final Map m = (Map) descField.get(getPerspectiveRegistry());
			m.put(sp.getId(), sp);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open perspective.
	 *
	 * @param perspectiveId
	 *            the perspective id
	 * @param immediately
	 *            the immediately
	 * @param withAutoSave
	 *            the with auto save
	 * @param memorizeEditors
	 *            the memorize editors
	 * @return true, if successful
	 */
	public static boolean openPerspective(final String perspectiveId, final boolean immediately,
			final boolean withAutoSave, final boolean memorizeEditors) {
		if (perspectiveId == null) return false;
		if (perspectiveId.equals(currentPerspectiveId)) return true;
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		// } catch (final Exception e) {
		// try {
		// page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().openPage(perspectiveId, null);
		// } catch (final Exception e1) {
		// e1.printStackTrace();
		// }
		// e.printStackTrace();
		// }
		if (page == null) return false;

		if (GamaPreferences.Modeling.EDITOR_PERSPECTIVE_SAVE.getValue()) { page.saveAllEditors(false); }

		if (memorizeEditors) { memorizeActiveEditor(page); }

		final IPerspectiveDescriptor oldDescriptor = page.getPerspective();
		final IPerspectiveDescriptor descriptor = findOrBuildPerspectiveWithId(perspectiveId);
		final WorkbenchWindow window = (WorkbenchWindow) page.getWorkbenchWindow();

		final Runnable r = () -> {
			// if (PlatformHelper.isMac() && !isSimulationPerspective(perspectiveId)) {
			// List<IGamaView.Display> displays = StreamEx.of(page.getViewReferences()).map(a -> a.getView(false))
			// .select(IGamaView.Display.class).toList();
			// if (displays.size() > 0) { page.activate((IWorkbenchPart) displays.get(0)); }
			// }
			try {

				page.setPerspective(descriptor);
			} catch (final NullPointerException e) {
				DEBUG.ERR("NPE in WorkbenchPage.setPerspective(). See Issue #1602. Working around the bug in e4...");
				page.setPerspective(descriptor);
			}
			activateAutoSave(withAutoSave);
			if (isSimulationPerspective(currentPerspectiveId) && isSimulationPerspective(perspectiveId)) {
				DEBUG.OUT("Destroying perspective " + oldDescriptor.getId());
				page.closePerspective(oldDescriptor, false, false);
				getPerspectiveRegistry().deletePerspective(oldDescriptor);
			}

			currentPerspectiveId = perspectiveId;
			if (isSimulationPerspective(perspectiveId) && !descriptor.equals(currentSimulationPerspective)) {
				// Early activation or deactivation of editors based on the global preference
				page.setEditorAreaVisible(!GamaPreferences.Modeling.EDITOR_PERSPECTIVE_HIDE.getValue());
				deleteCurrentSimulationPerspective();
				currentSimulationPerspective = (SimulationPerspectiveDescriptor) descriptor;
			}
			applyActiveEditor(page);
			final Boolean showControls = keepControls();
			if (showControls != null) { window.setCoolBarVisible(showControls); }
			final Boolean keepTray = keepTray();
			if (keepTray != null) { showBottomTray(window, keepTray); }
			// DEBUG.OUT("Perspective " + perspectiveId + " opened ");
		};
		if (immediately) {
			Display.getDefault().syncExec(r);
		} else {
			Display.getDefault().asyncExec(r);
		}
		return true;
	}

	/**
	 * Apply active editor.
	 *
	 * @param page
	 *            the page
	 */
	private static void applyActiveEditor(final IWorkbenchPage page) {
		if (activeEditor == null) return;
		final IEditorPart part = page.findEditor(activeEditor);
		if (part != null) {
			page.activate(part);
			// DEBUG.OUT("Applying memorized editor to " + page.getPerspective().getId() + " = " +
			// activeEditor.getName());
			// page.bringToTop(part);
		}

	}

	/**
	 * Memorize active editor.
	 *
	 * @param page
	 *            the page
	 */
	private static void memorizeActiveEditor(final IWorkbenchPage page) {
		// DEBUG.OUT("Trying to memorize editor in " + page.getPerspective().getId());
		final IEditorPart part = page.isEditorAreaVisible() ? page.getActiveEditor() : null;
		if (part == null) return;
		activeEditor = part.getEditorInput();
		// DEBUG.OUT("Memorized editor in " + page.getPerspective().getId() + " = " + activeEditor.getName());

	}

	/**
	 * Activate auto save.
	 *
	 * @param activate
	 *            the activate
	 */
	public static void activateAutoSave(final boolean activate) {
		// DEBUG.OUT("auto-save activated: " + activate);
		Workbench.getInstance().setEnableAutoSave(activate);
		// ApplicationWorkbenchAdvisor.CONFIGURER.setSaveAndRestore(activate);
	}

	/**
	 * Gets the active perspective.
	 *
	 * @return the active perspective
	 */
	public final static IPerspectiveDescriptor getActivePerspective() {
		final IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		return activePage.getPerspective();

	}

	/**
	 * Keep tabs.
	 *
	 * @return the boolean
	 */
	public final static Boolean keepTabs() {
		final IPerspectiveDescriptor d = getActivePerspective();
		if (d instanceof SimulationPerspectiveDescriptor) return ((SimulationPerspectiveDescriptor) d).keepTabs;
		return true;
	}

	/**
	 * Keep toolbars.
	 *
	 * @return the boolean
	 */
	public final static Boolean keepToolbars() {
		final IPerspectiveDescriptor d = getActivePerspective();
		if (d instanceof SimulationPerspectiveDescriptor) return ((SimulationPerspectiveDescriptor) d).keepToolbars();
		return null;
	}

	/**
	 * Keep controls.
	 *
	 * @return the boolean
	 */
	public final static Boolean keepControls() {
		final IPerspectiveDescriptor d = getActivePerspective();
		if (d instanceof SimulationPerspectiveDescriptor) return ((SimulationPerspectiveDescriptor) d).keepControls();
		return true;
	}

	/**
	 * Keep tray.
	 *
	 * @return the boolean
	 */
	public final static Boolean keepTray() {
		final IPerspectiveDescriptor d = getActivePerspective();
		if (d instanceof SimulationPerspectiveDescriptor) return ((SimulationPerspectiveDescriptor) d).keepTray();
		return true;
	}

	/**
	 * Show consoles.
	 *
	 * @return true, if successful
	 */
	public static boolean showConsoles() {
		final IPerspectiveDescriptor d = getActivePerspective();
		if (d instanceof SimulationPerspectiveDescriptor) return ((SimulationPerspectiveDescriptor) d).showConsoles();
		return true;
	}

	/**
	 * Gets the background.
	 *
	 * @return the background
	 */
	public final static Color getBackground() {
		final IPerspectiveDescriptor d = getActivePerspective();
		if (d instanceof SimulationPerspectiveDescriptor) return ((SimulationPerspectiveDescriptor) d).getBackground();
		return null;
	}

	/**
	 * Show overlays.
	 *
	 * @return true, if successful
	 */
	public static boolean showOverlays() {
		return GamaPreferences.Displays.CORE_OVERLAY.getValue();
	}

	/**
	 * A factory for creating SimulationPerspective objects.
	 */
	public static class SimulationPerspectiveFactory implements IPerspectiveFactory {

		/** The original. */
		final IPerspectiveFactory original;

		/**
		 * Instantiates a new simulation perspective factory.
		 *
		 * @param original
		 *            the original
		 */
		SimulationPerspectiveFactory(final IPerspectiveFactory original) {
			this.original = original;
		}

		@Override
		public void createInitialLayout(final IPageLayout layout) {
			original.createInitialLayout(layout);
			// TODO do the rest... See SimulationPerspective
		}

	}

	/**
	 * Gets the new perspective name.
	 *
	 * @param model
	 *            the model
	 * @param experiment
	 *            the experiment
	 * @return the new perspective name
	 */
	public static String getNewPerspectiveName(final String model, final String experiment) {
		return PERSPECTIVE_SIMULATION_FRAGMENT + ":" + model + ":" + experiment;
	}

	/**
	 * Delete current simulation perspective.
	 */
	public static void deleteCurrentSimulationPerspective() {
		if (currentSimulationPerspective != null) {
			final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			if (page != null) {
				page.closePerspective(currentSimulationPerspective, false, false);
				getPerspectiveRegistry().deletePerspective(currentSimulationPerspective);
				currentSimulationPerspective.dispose();
				deletePerspectiveFromApplication(currentSimulationPerspective);
				// DEBUG.OUT("Perspective destroyed: " + currentSimulationPerspective.getId());
			}
			currentSimulationPerspective = null;
		}

	}

	/**
	 * Gets the active simulation perspective.
	 *
	 * @return the active simulation perspective
	 */
	public static SimulationPerspectiveDescriptor getActiveSimulationPerspective() {
		final IPerspectiveDescriptor d = getActivePerspective();
		if (d instanceof SimulationPerspectiveDescriptor) return (SimulationPerspectiveDescriptor) d;
		return null;
	}

}
