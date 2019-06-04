/*******************************************************************************************************
 *
 * msi.gama.application.workbench.PerspectiveHelper.java, in plugin msi.gama.application,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8)
 *
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.application.workbench;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.impl.PerspectiveImpl;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
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

public class PerspectiveHelper {

	static {
		DEBUG.OFF();
	}

	// id of the status bar, as defined in the LegacyIDE.e4xmi
	private static final String BOTTOM_TRIM_ID = "org.eclipse.ui.trim.status"; //$NON-NLS-1$

	public static final String PERSPECTIVE_MODELING_ID = IGui.PERSPECTIVE_MODELING_ID;
	public static final String PERSPECTIVE_SIMULATION_ID = "msi.gama.application.perspectives.SimulationPerspective";
	public static final String PERSPECTIVE_SIMULATION_FRAGMENT = "Simulation";

	public static String currentPerspectiveId = PERSPECTIVE_MODELING_ID;
	public static IPerspectiveDescriptor currentSimulationPerspective = null;
	public static IEditorInput activeEditor;

	static boolean matches(final String id) {
		return !id.equals(PerspectiveHelper.PERSPECTIVE_SIMULATION_ID) && id.contains(PERSPECTIVE_SIMULATION_FRAGMENT);
	}

	public static void cleanPerspectives() {
		final EModelService e = PlatformUI.getWorkbench().getService(EModelService.class);
		final MApplication a = PlatformUI.getWorkbench().getService(MApplication.class);

		final List<PerspectiveImpl> perspectives = e.findElements(a, PerspectiveImpl.class, EModelService.ANYWHERE,
			element -> matches(element.getElementId()));
		for ( final PerspectiveImpl p : perspectives ) {
			// DEBUG.OUT("Dirty perspective implementation found and removed: " + p.getElementId());
			p.getParent().getChildren().remove(p);
		}

		final IPerspectiveRegistry reg = PlatformUI.getWorkbench().getPerspectiveRegistry();
		for ( final IPerspectiveDescriptor desc : reg.getPerspectives() ) {
			if ( matches(desc.getId()) ) {
				// DEBUG.OUT("Dirty perspective descriptor found and removed: " + desc.getId());
				reg.deletePerspective(desc);
			}
		}

		// DEBUG.OUT("Current perspectives: " + listCurrentPerspectives());
	}

	public static void deletePerspectiveFromApplication(final IPerspectiveDescriptor d) {
		final MApplication a = PlatformUI.getWorkbench().getService(MApplication.class);
		final EModelService e = PlatformUI.getWorkbench().getService(EModelService.class);
		final List<PerspectiveImpl> perspectives = e.findElements(a, PerspectiveImpl.class, EModelService.ANYWHERE,
			element -> element.getElementId().contains(d.getId()));
		for ( final PerspectiveImpl p : perspectives ) {
			// DEBUG.OUT("Dirty perspective implementation found and removed: " + p.getElementId());
			p.getParent().getChildren().remove(p);
		}
	}

	public static IPerspectiveRegistry getPerspectiveRegistry() {
		return PlatformUI.getWorkbench().getPerspectiveRegistry();
	}

	public static boolean isModelingPerspective() {
		return currentPerspectiveId.equals(PERSPECTIVE_MODELING_ID);
	}

	public static boolean isSimulationPerspective() {
		return isSimulationPerspective(currentPerspectiveId);
	}

	private static boolean isSimulationPerspective(final String perspectiveId) {
		return perspectiveId.contains(PERSPECTIVE_SIMULATION_FRAGMENT);
	}

	public static final boolean openModelingPerspective(final boolean immediately, final boolean memorizeEditors) {
		// AD 08/18: turn off autosave to prevent workspace corruption
		return openPerspective(PERSPECTIVE_MODELING_ID, immediately, false, memorizeEditors);
	}

	/* Get the MUIElement representing the status bar for the given window */
	private static MUIElement getTrimStatus(final WorkbenchWindow window) {
		final EModelService modelService = window.getService(EModelService.class);
		final MUIElement searchRoot = window.getModel();
		return modelService.find(BOTTOM_TRIM_ID, searchRoot);
	}

	public static void showBottomTray(final WorkbenchWindow window, final Boolean show) {

		final MUIElement trimStatus = getTrimStatus(window);
		if ( trimStatus != null ) {
			// toggle statusbar visibility
			trimStatus.setVisible(show);
		}

	}

	public static final boolean switchToSimulationPerspective() {
		if ( currentSimulationPerspective == null ) { return false; }
		IWorkbenchPage activePage = null;
		try {
			activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		} catch (final Exception e) {
			e.printStackTrace();
		}
		if ( activePage == null ) { return false; }
		final IWorkbenchPage page = activePage;
		final WorkbenchWindow window = (WorkbenchWindow) page.getWorkbenchWindow();
		if ( page.getPerspective().equals(currentSimulationPerspective) ) { return true; }
		Display.getDefault().syncExec(() -> {
			memorizeActiveEditor(page);
			try {
				page.setPerspective(currentSimulationPerspective);
			} catch (final NullPointerException e) {
				// DEBUG.ERR(
				// "NPE in WorkbenchPage.setPerspective(). See Issue #1602.
				// Working around the bug in e4...");
				page.setPerspective(currentSimulationPerspective);
			}
			final Boolean showControls = keepControls();
			if ( showControls != null ) {
				window.setCoolBarVisible(showControls);
			}
			final Boolean keepTray = keepTray();
			if ( keepTray != null ) {
				showBottomTray(window, keepTray);
			}
			applyActiveEditor(page);
		});
		currentPerspectiveId = currentSimulationPerspective.getId();
		return true;
	}

	public static final boolean openSimulationPerspective(final IModel model, final String experimentName) {
		if ( model == null ) { return false; }
		final String name = getNewPerspectiveName(model.getName(), experimentName);
		return openPerspective(name, true, false, true);
	}

	static PerspectiveDescriptor getSimulationDescriptor() {
		return (PerspectiveDescriptor) getPerspectiveRegistry().findPerspectiveWithId(PERSPECTIVE_SIMULATION_ID);
	}

	private static IPerspectiveDescriptor findOrBuildPerspectiveWithId(final String id) {
		if ( currentSimulationPerspective != null && currentSimulationPerspective.getId().equals(id) ) {
			return currentSimulationPerspective;
		}
		final PerspectiveRegistry pr = (PerspectiveRegistry) getPerspectiveRegistry();
		IPerspectiveDescriptor tempDescriptor = pr.findPerspectiveWithId(id);
		if ( tempDescriptor == null ) {
			getPerspectiveRegistry()
				.revertPerspective(getPerspectiveRegistry().findPerspectiveWithId(PERSPECTIVE_SIMULATION_ID));
			tempDescriptor = new SimulationPerspectiveDescriptor(id);
		}
		return tempDescriptor;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
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

	public static Set<String> listCurrentPerspectives() {
		final IPerspectiveRegistry reg = PlatformUI.getWorkbench().getPerspectiveRegistry();
		final Set<String> result = new HashSet<>();
		for ( final IPerspectiveDescriptor desc : reg.getPerspectives() ) {
			result.add(desc.getId());
		}
		return result;
	}

	public static boolean openPerspective(final String perspectiveId, final boolean immediately,
		final boolean withAutoSave, final boolean memorizeEditors) {
		if ( perspectiveId == null ) { return false; }
		if ( perspectiveId.equals(currentPerspectiveId) ) { return true; }

		IWorkbenchPage activePage = null;
		try {
			activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		} catch (final Exception e) {
			try {
				activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().openPage(perspectiveId, null);
			} catch (final Exception e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		if ( activePage == null ) { return false; }

		if ( GamaPreferences.Modeling.EDITOR_PERSPECTIVE_SAVE.getValue() ) {
			activePage.saveAllEditors(false);
		}

		if ( memorizeEditors ) {
			memorizeActiveEditor(activePage);
		}

		final IPerspectiveDescriptor oldDescriptor = activePage.getPerspective();
		final IPerspectiveDescriptor descriptor = findOrBuildPerspectiveWithId(perspectiveId);
		final IWorkbenchPage page = activePage;
		final WorkbenchWindow window = (WorkbenchWindow) page.getWorkbenchWindow();
		final Runnable r = () -> {
			try {
				page.setPerspective(descriptor);
			} catch (final NullPointerException e) {
				// DEBUG.ERR(
				// "NPE in WorkbenchPage.setPerspective(). See Issue #1602.
				// Working around the bug in e4...");
				page.setPerspective(descriptor);
			}
			activateAutoSave(withAutoSave);
			if ( isSimulationPerspective(currentPerspectiveId) && isSimulationPerspective(perspectiveId) ) {
				// DEBUG.OUT("Destroying perspective " + oldDescriptor.getId());
				page.closePerspective(oldDescriptor, false, false);
				getPerspectiveRegistry().deletePerspective(oldDescriptor);
			}

			currentPerspectiveId = perspectiveId;
			if ( isSimulationPerspective(perspectiveId) && !descriptor.equals(currentSimulationPerspective) ) {
				// Early activation or deactivation of editors based on the global preference
				page.setEditorAreaVisible(!GamaPreferences.Modeling.EDITOR_PERSPECTIVE_HIDE.getValue());
				deleteCurrentSimulationPerspective();
				currentSimulationPerspective = descriptor;
			}
			applyActiveEditor(page);
			final Boolean showControls = keepControls();
			if ( showControls != null ) {
				window.setCoolBarVisible(showControls);
			}
			final Boolean keepTray = keepTray();
			if ( keepTray != null ) {
				showBottomTray(window, keepTray);
			}
			// DEBUG.OUT("Perspective " + perspectiveId + " opened ");
		};
		if ( immediately ) {
			Display.getDefault().syncExec(r);
		} else {
			Display.getDefault().asyncExec(r);
		}
		return true;
	}

	private static void applyActiveEditor(final IWorkbenchPage page) {
		if ( activeEditor == null ) { return; }
		final IEditorPart part = page.findEditor(activeEditor);
		if ( part != null ) {
			page.activate(part);
			// DEBUG.OUT("Applying memorized editor to " + page.getPerspective().getId() + " = " + activeEditor.getName());
			// page.bringToTop(part);
		}

	}

	private static void memorizeActiveEditor(final IWorkbenchPage page) {
		// DEBUG.OUT("Trying to memorize editor in " + page.getPerspective().getId());
		final IEditorPart part = page.isEditorAreaVisible() ? page.getActiveEditor() : null;
		if ( part == null ) { return; }
		activeEditor = part.getEditorInput();
		// DEBUG.OUT("Memorized editor in " + page.getPerspective().getId() + " = " + activeEditor.getName());

	}

	public static void activateAutoSave(final boolean activate) {
		// DEBUG.OUT("auto-save activated: " + activate);
		Workbench.getInstance().setEnableAutoSave(activate);
		// ApplicationWorkbenchAdvisor.CONFIGURER.setSaveAndRestore(activate);
	}

	public final static IPerspectiveDescriptor getActivePerspective() {
		final IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		final IPerspectiveDescriptor currentDescriptor = activePage.getPerspective();
		return currentDescriptor;

	}

	public final static String getActivePerspectiveName() {
		return getActivePerspective().getId();

	}

	public final static Boolean keepTabs() {
		final IPerspectiveDescriptor d = getActivePerspective();
		if ( d instanceof SimulationPerspectiveDescriptor ) {
			return ((SimulationPerspectiveDescriptor) d).keepTabs;
		} else {
			return true;
		}
	}

	public final static Boolean keepToolbars() {
		final IPerspectiveDescriptor d = getActivePerspective();
		if ( d instanceof SimulationPerspectiveDescriptor ) {
			return ((SimulationPerspectiveDescriptor) d).keepToolbars();
		} else {
			return null;
		}
	}

	public final static Boolean keepControls() {
		final IPerspectiveDescriptor d = getActivePerspective();
		if ( d instanceof SimulationPerspectiveDescriptor ) {
			return ((SimulationPerspectiveDescriptor) d).keepControls();
		} else {
			return true;
		}
	}

	public final static Boolean keepTray() {
		final IPerspectiveDescriptor d = getActivePerspective();
		if ( d instanceof SimulationPerspectiveDescriptor ) {
			return ((SimulationPerspectiveDescriptor) d).keepTray();
		} else {
			return true;
		}
	}

	public static boolean showOverlays() {
		return GamaPreferences.Displays.CORE_OVERLAY.getValue();
	}

	public static class SimulationPerspectiveFactory implements IPerspectiveFactory {

		final IPerspectiveFactory original;

		SimulationPerspectiveFactory(final IPerspectiveFactory original) {
			this.original = original;
		}

		@Override
		public void createInitialLayout(final IPageLayout layout) {
			original.createInitialLayout(layout);
			// TODO do the rest... See SimulationPerspective
		}

	}

	public static class SimulationPerspectiveDescriptor extends PerspectiveDescriptor {

		Boolean keepTabs = true;
		Boolean keepToolbars = null;
		Boolean keepControls = true;
		Boolean keepTray = true;

		SimulationPerspectiveDescriptor(final String id) {
			super(id, id, getSimulationDescriptor());
			dirtySavePerspective(this);
		}

		@Override
		public IPerspectiveFactory createFactory() {

			try {
				return new SimulationPerspectiveFactory(
					(IPerspectiveFactory) getConfigElement().createExecutableExtension("class"));
			} catch (final CoreException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}

		@Override
		public boolean hasCustomDefinition() {
			return true;
		}

		@Override
		public boolean isPredefined() {
			return false;
		}

		@Override
		public IConfigurationElement getConfigElement() {
			return getSimulationDescriptor().getConfigElement();
		}

		@Override
		public String getDescription() {
			return "Perspective for " + getId();
		}

		@Override
		public String getOriginalId() {
			return getId();
		}

		@Override
		public String getPluginId() {
			return getSimulationDescriptor().getPluginId();
		}

		public Boolean keepTabs() {
			return keepTabs;
		}

		public void keepTabs(final Boolean b) {
			keepTabs = b;
		}

		public Boolean keepToolbars() {
			return keepToolbars;
		}

		public void keepToolbars(final Boolean b) {
			keepToolbars = b;
		}

		public void keepControls(final Boolean b) {
			keepControls = b;
		}

		public Boolean keepControls() {
			return keepControls;
		}

		public void keepTray(final Boolean b) {
			keepTray = b;
		}

		public Boolean keepTray() {
			return keepTray;
		}

	}

	public static String getNewPerspectiveName(final String model, final String experiment) {
		return PERSPECTIVE_SIMULATION_FRAGMENT + ":" + model + ":" + experiment;
	}

	public static void deleteCurrentSimulationPerspective() {
		if ( currentSimulationPerspective != null ) {
			final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			if ( page != null ) {
				page.closePerspective(currentSimulationPerspective, false, false);
				getPerspectiveRegistry().deletePerspective(currentSimulationPerspective);
				deletePerspectiveFromApplication(currentSimulationPerspective);
				// DEBUG.OUT("Perspective destroyed: " + currentSimulationPerspective.getId());
			}
			currentSimulationPerspective = null;
		}

	}

	public static SimulationPerspectiveDescriptor getActiveSimulationPerspective() {
		final IPerspectiveDescriptor d = getActivePerspective();
		if ( d instanceof SimulationPerspectiveDescriptor ) {
			return (SimulationPerspectiveDescriptor) d;
		} else {
			return null;
		}
	}

}
