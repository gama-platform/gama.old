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

import java.util.Map;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.internal.registry.PerspectiveDescriptor;
import org.eclipse.ui.internal.registry.PerspectiveRegistry;
import msi.gama.common.interfaces.IGui;

public class PerspectiveHelper {

	public static final String PERSPECTIVE_MODELING_ID = IGui.PERSPECTIVE_MODELING_ID;
	public static final String PERSPECTIVE_SIMULATION_ID = "msi.gama.application.perspectives.SimulationPerspective";
	public static final String PERSPECTIVE_SIMULATION_FRAGMENT = "Simulation";

	public static String currentPerspectiveId = PERSPECTIVE_MODELING_ID;
	public static IPerspectiveDescriptor currentSimulationPerspective = null;

	// private static void cleanPerspectives() {
	// final IPerspectiveRegistry reg = PlatformUI.getWorkbench().getPerspectiveRegistry();
	// for ( final IPerspectiveDescriptor desc : reg.getPerspectives() ) {
	// if ( desc.getId().contains(PERSPECTIVE_SIMULATION_FRAGMENT) &&
	// !desc.getId().equals(PERSPECTIVE_SIMULATION_ID) ) {
	// reg.deletePerspective(desc);
	// }
	// }
	// }

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
		return openPerspective(PERSPECTIVE_MODELING_ID, immediately, true);
	}

	static PerspectiveDescriptor getSimulationDescriptor() {
		return (PerspectiveDescriptor) getPerspectiveRegistry().findPerspectiveWithId(PERSPECTIVE_SIMULATION_ID);
	}

	private static IPerspectiveDescriptor findOrBuildPerspectiveWithId(final String id) {
		if ( currentSimulationPerspective != null &&
			currentSimulationPerspective.getId().equals(id) ) { return currentSimulationPerspective; }
		final PerspectiveRegistry pr = ((PerspectiveRegistry) getPerspectiveRegistry());
		IPerspectiveDescriptor tempDescriptor = pr.findPerspectiveWithId(id);
		if ( tempDescriptor == null ) {
			// tempDescriptor = pr.createPerspective(id, getSimulationDescriptor());
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

	public static boolean openPerspective(final String perspectiveId, final boolean immediately,
		final boolean withAutoSave) {
		if ( perspectiveId == null ) { return false; }
		if ( perspectiveId.equals(currentPerspectiveId) ) { return true; }

		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if ( activePage == null ) {
			try {
				activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().openPage(perspectiveId, null);
			} catch (final WorkbenchException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		if ( activePage == null ) { return false; }
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
				getPerspectiveRegistry().deletePerspective(oldDescriptor);
			}

			currentPerspectiveId = perspectiveId;
			if ( isSimulationPerspective(perspectiveId) && !descriptor.equals(currentSimulationPerspective) ) {
				deleteLastSimulationPerspective();
				currentSimulationPerspective = descriptor;
			}
			System.out.println("Perspective " + perspectiveId + " opened ");
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
			return keepTabs;
		}

		public void keepToolbars(final boolean b) {
			keepTabs = b;
		}

	}

	public static String getNewPerspectiveName(final String model, final String experiment) {
		return PERSPECTIVE_SIMULATION_FRAGMENT + ":" + model + ":" + experiment;
	}

	public static void deleteLastSimulationPerspective() {
		if ( currentSimulationPerspective != null ) {
			// final IPerspectiveDescriptor formerDescriptor =
			// getPerspectiveRegistry().findPerspectiveWithId(formerSimulationPerspectiveId);
			// if ( formerDescriptor != null ) {
			final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			if ( page != null ) {
				page.closePerspective(currentSimulationPerspective, false, false);
				// System.out.println("Perspective destroyed: " + currentSimulationPerspective.getId());
			}
			// getPerspectiveRegistry().deletePerspective(formerDescriptor);
			// }
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
