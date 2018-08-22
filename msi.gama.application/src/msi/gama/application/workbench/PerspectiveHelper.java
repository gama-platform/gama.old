/*********************************************************************************************
 *
 * 'PerspectiveHelper.java, in plugin msi.gama.application, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.application.workbench;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.e4.ui.model.application.MApplication;
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
			DEBUG.OUT("Dirty perspective implementation found and removed: " + p.getElementId());
			p.getParent().getChildren().remove(p);
		}

		final IPerspectiveRegistry reg = PlatformUI.getWorkbench().getPerspectiveRegistry();
		for ( final IPerspectiveDescriptor desc : reg.getPerspectives() ) {
			if ( matches(desc.getId()) ) {
				DEBUG.OUT("Dirty perspective descriptor found and removed: " + desc.getId());
				reg.deletePerspective(desc);
			}
		}

		DEBUG.OUT("Current perspectives: " + listCurrentPerspectives());
	}

	public static void deletePerspectiveFromApplication(final IPerspectiveDescriptor d) {
		final MApplication a = PlatformUI.getWorkbench().getService(MApplication.class);
		final EModelService e = PlatformUI.getWorkbench().getService(EModelService.class);
		final List<PerspectiveImpl> perspectives = e.findElements(a, PerspectiveImpl.class, EModelService.ANYWHERE,
			element -> element.getElementId().contains(d.getId()));
		for ( final PerspectiveImpl p : perspectives ) {
			DEBUG.OUT("Dirty perspective implementation found and removed: " + p.getElementId());
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

	public static final boolean openModelingPerspective(final boolean immediately) {
		// AD 08/18: turn off autosave to prevent workspace corruption
		return openPerspective(PERSPECTIVE_MODELING_ID, immediately, false);
	}

	public static final boolean openSimulationPerspective() {
		if ( currentSimulationPerspective == null ) { return false; }
		IWorkbenchPage activePage = null;
		try {
			activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		} catch (final Exception e) {
			e.printStackTrace();
		}
		if ( activePage == null ) { return false; }
		final IWorkbenchPage page = activePage;
		if ( page.getPerspective().equals(currentSimulationPerspective) ) { return true; }
		Display.getDefault().syncExec(() -> {
			try {
				memorizeActiveEditor(page);
				page.setPerspective(currentSimulationPerspective);
				applyActiveEditor(page);
			} catch (final NullPointerException e) {
				// DEBUG.ERR(
				// "NPE in WorkbenchPage.setPerspective(). See Issue #1602.
				// Working around the bug in e4...");
				page.setPerspective(currentSimulationPerspective);
			}
		});
		currentPerspectiveId = currentSimulationPerspective.getId();
		return true;
	}

	public static final boolean openSimulationPerspective(final IModel model, final String experimentName) {
		if ( model == null ) { return false; }
		final String name = getNewPerspectiveName(model.getName(), experimentName);
		return openPerspective(name, true, false);
	}

	static PerspectiveDescriptor getSimulationDescriptor() {
		return (PerspectiveDescriptor) getPerspectiveRegistry().findPerspectiveWithId(PERSPECTIVE_SIMULATION_ID);
	}

	private static IPerspectiveDescriptor findOrBuildPerspectiveWithId(final String id) {
		if ( currentSimulationPerspective != null &&
			currentSimulationPerspective.getId().equals(id) ) { return currentSimulationPerspective; }
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
		final boolean withAutoSave) {
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

		memorizeActiveEditor(activePage);

		final IPerspectiveDescriptor oldDescriptor = activePage.getPerspective();
		final IPerspectiveDescriptor descriptor = findOrBuildPerspectiveWithId(perspectiveId);
		final IWorkbenchPage page = activePage;
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
				DEBUG.OUT("Destroying perspective " + oldDescriptor.getId());
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
			DEBUG.OUT("Perspective " + perspectiveId + " opened ");
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
			// page.bringToTop(part);
		}

	}

	private static void memorizeActiveEditor(final IWorkbenchPage page) {
		final IEditorPart part = page.getActiveEditor();
		if ( part == null ) { return; }
		activeEditor = part.getEditorInput();

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

	public final static boolean keepTabs() {
		final IPerspectiveDescriptor d = getActivePerspective();
		if ( d instanceof SimulationPerspectiveDescriptor ) {
			return ((SimulationPerspectiveDescriptor) d).keepTabs;
		} else {
			return true;
		}
	}

	public final static boolean keepToolbars() {
		final IPerspectiveDescriptor d = getActivePerspective();
		if ( d instanceof SimulationPerspectiveDescriptor ) {
			return ((SimulationPerspectiveDescriptor) d).keepToolbars;
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

		boolean keepTabs = true;
		boolean keepToolbars = true;

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

		public boolean keepTabs() {
			return keepTabs;
		}

		public void keepTabs(final boolean b) {
			keepTabs = b;
		}

		public boolean keepToolbars() {
			return keepToolbars;
		}

		public void keepToolbars(final boolean b) {
			keepToolbars = b;
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
				DEBUG.OUT("Perspective destroyed: " + currentSimulationPerspective.getId());
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
