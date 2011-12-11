/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC 
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.gui.application;

import java.lang.reflect.InvocationTargetException;
import msi.gama.gui.application.perspectives.*;
import msi.gama.internal.compilation.GamlCompilCallback;
import msi.gama.kernel.GAMA;
import msi.gama.lang.gaml.descript.GamlDescriptIO;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.dialogs.*;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.*;
import org.eclipse.ui.application.*;
import org.eclipse.ui.ide.IDE;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	private static final String PERSPECTIVE_MODELING_ID =
		"msi.gama.gui.application.perspectives.ModelingPerspective";

	@Override
	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
		final IWorkbenchWindowConfigurer configurer) {
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}

	@Override
	public void postStartup() {
		super.postStartup();
	}

	@Override
	public void initialize(final IWorkbenchConfigurer configurer) {
		super.initialize(configurer);
		configurer.setSaveAndRestore(true);
		/* Show heap memory status in status bar */
		PlatformUI.getPreferenceStore().setValue(IWorkbenchPreferenceConstants.SHOW_MEMORY_MONITOR,
			true);
		// PlatformUI.getPreferenceStore().setValue(
		// SVNTeamPreferences.DECORATION_USE_FONT_COLORS_DECOR_NAME, true);
		PlatformUI.getPreferenceStore().setValue(
			IWorkbenchPreferenceConstants.SHOW_TRADITIONAL_STYLE_TABS, false);
		IDE.registerAdapters();

		// Gaml editor validation callback (parse and compil)
		// long start = System.nanoTime(); // debug
		GamlDescriptIO.getInstance().setCallback(new GamlCompilCallback());
		// OutputManager.debug("setCallback = " + (System.nanoTime() - start) / 1000000.0 + "ms");
	}

	@Override
	public String getInitialWindowPerspectiveId() {
		return PERSPECTIVE_MODELING_ID;
	}

	/**
	 * A workbench pre-shutdown method calls to prompt a confirmation of the shutdown and perform a
	 * saving of the workspace
	 */
	@Override
	public boolean preShutdown() {

		/* Save workspace before closing the application */
		final MultiStatus status =
			new MultiStatus(Activator.PLUGIN_ID, 0, "Saving Workspace....", null);

		IRunnableWithProgress runnable = new IRunnableWithProgress() {

			@Override
			public void run(final IProgressMonitor monitor) {
				try {
					IWorkspace ws = ResourcesPlugin.getWorkspace();
					status.merge(ws.save(true, monitor));
				} catch (CoreException e) {
					status.merge(e.getStatus());
				}
			}
		};
		try {
			new ProgressMonitorDialog(null).run(false, false, runnable);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if ( !status.isOK() ) {
			ErrorDialog.openError(Display.getDefault().getActiveShell(), "Error...",
				"Error while saving workspace", status);
		}

		try {
			GamlDescriptIO.getInstance().canRun(false); // stop builder
			GAMA.closeCurrentExperiment();
		} catch (Exception e) {
			e.printStackTrace();
		}
		/* We have to close all views made from simulation */
		// TODO temporary really dirty .. a refaire plus proprement (trouver
		// comment recupérer toutes les vues simulation ouvertes de toutes les
		// perspectives et les disposer)
		// a voir plus tard comment on utilisera le batch
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		String idCurrentPerspective = window.getActivePage().getPerspective().getId();
		try {
			if ( idCurrentPerspective.equals(SimulationPerspective.ID) ) {
				closeSimulationViews();
				window.getWorkbench().showPerspective(ModelingPerspective.ID, window);
			} else {
				window.getWorkbench().showPerspective(SimulationPerspective.ID, window);
				closeSimulationViews();
				window.getWorkbench().showPerspective(ModelingPerspective.ID, window);
			}
		} catch (WorkbenchException e) {
			e.printStackTrace();
		}
		return super.preShutdown();
	}

	@Override
	public IAdaptable getDefaultPageInput() {
		return ResourcesPlugin.getWorkspace().getRoot();

	}

	public static void closeSimulationViews() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IViewReference[] views = page.getViewReferences();

		for ( IViewReference view : views ) {
			if ( view.getId().startsWith("msi.gama.gui.application.view.") ) {
				page.hideView(view);
			}
		}
	}
}
