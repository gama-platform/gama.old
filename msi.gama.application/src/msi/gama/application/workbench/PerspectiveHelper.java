package msi.gama.application.workbench;

import java.lang.reflect.Field;
import java.util.Map;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.internal.registry.IWorkbenchRegistryConstants;
import org.eclipse.ui.internal.registry.PerspectiveDescriptor;
import org.eclipse.ui.internal.registry.PerspectiveRegistry;
import msi.gama.common.interfaces.IGui;

public class PerspectiveHelper {

	public static final String PERSPECTIVE_MODELING_ID = IGui.PERSPECTIVE_MODELING_ID;
	public static final String PERSPECTIVE_SIMULATION_ID = "msi.gama.application.perspectives.SimulationPerspective";
	public static final String PERSPECTIVE_SIMULATION_FRAGMENT = "Simulation";

	public static String currentPerspectiveId = PERSPECTIVE_MODELING_ID;

	private static void cleanPerspectives() {
		final IPerspectiveRegistry reg = PlatformUI.getWorkbench().getPerspectiveRegistry();
		for ( final IPerspectiveDescriptor desc : reg.getPerspectives() ) {
			if ( desc.getId().contains(PERSPECTIVE_SIMULATION_FRAGMENT) &&
				!desc.getId().equals(PERSPECTIVE_SIMULATION_ID) ) {
				reg.deletePerspective(desc);
			}
		}
	}

	public static PerspectiveRegistry getPerspectiveRegistry() {
		return (PerspectiveRegistry) PlatformUI.getWorkbench().getPerspectiveRegistry();
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
		return openPerspective(PERSPECTIVE_MODELING_ID, immediately, true);
	}

	static PerspectiveDescriptor getSimulationDescriptor() {
		return (PerspectiveDescriptor) getPerspectiveRegistry().findPerspectiveWithId(PERSPECTIVE_SIMULATION_ID);
	}

	private static IPerspectiveDescriptor findOrBuildPerspectiveWithId(final String id) {
		IPerspectiveDescriptor tempDescriptor = getPerspectiveRegistry().findPerspectiveWithId(id);
		if ( tempDescriptor == null ) {
			tempDescriptor = new SimulationPerspectiveDescriptor(id);
		}
		return tempDescriptor;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	static void dirtySavePerspective(final SimulationPerspectiveDescriptor sp) {
		try {
			final Field descField = PerspectiveRegistry.class.getDeclaredField("descriptors");
			descField.setAccessible(true);
			final Map m = (Map) descField.get(getPerspectiveRegistry());
			m.put(sp.getId(), sp);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public static boolean openPerspective(final String perspectiveId, final boolean immediately,
		final boolean withAutoSave) {
		if ( perspectiveId == null )
			return false;
		if ( perspectiveId.equals(currentPerspectiveId) )
			return true;

		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if ( activePage == null )
			try {
				activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().openPage(perspectiveId, null);
			} catch (final WorkbenchException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		if ( activePage == null )
			return false;
		final IPerspectiveDescriptor oldDescriptor = activePage.getPerspective();
		final IPerspectiveDescriptor descriptor = findOrBuildPerspectiveWithId(perspectiveId);
		final IWorkbenchPage page = activePage;
		final Runnable r = () -> {
			try {
				page.setPerspective(descriptor);
			} catch (final NullPointerException e) {
				// System.err.println(
				// "NPE in WorkbenchPage.setPerspective(). See Issue #1602.
				// Working around the bug in e4...");
				page.setPerspective(descriptor);
			}
			activateAutoSave(withAutoSave);
			if ( isSimulationPerspective(currentPerspectiveId) && isSimulationPerspective(perspectiveId) ) {
				page.closePerspective(oldDescriptor, false, false);
			}
			currentPerspectiveId = perspectiveId;
			// System.out.println("Perspective " + perspectiveId + " opened ");
		};
		if ( immediately ) {
			Display.getDefault().syncExec(r);
		} else {
			Display.getDefault().asyncExec(r);
		}
		return true;
	}

	public static void activateAutoSave(final boolean activate) {
		// System.out.println("auto-save activated: " + activate);
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

	private static class SimulationPerspectiveDescriptor extends PerspectiveDescriptor {

		SimulationPerspectiveDescriptor(final String id) {
			super(id, id, getSimulationDescriptor());
			dirtySavePerspective(this);
		}

		@Override
		public IPerspectiveFactory createFactory() {
			try {
				return (IPerspectiveFactory) getSimulationDescriptor().getConfigElement()
					.createExecutableExtension(IWorkbenchRegistryConstants.ATT_CLASS);
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
			return true;
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

	}

	public static String getNewPerspectiveName(final String model, final String experiment) {
		return PERSPECTIVE_SIMULATION_FRAGMENT + ":" + model + ":" + experiment;
	}

}
