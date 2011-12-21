/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.swt;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import msi.gama.gui.navigator.FileBean;
import msi.gama.gui.swt.perspectives.*;
import msi.gama.lang.gaml.descript.GamlDescriptIO;
import msi.gama.runtime.GAMA;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.dialogs.*;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.*;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.application.*;
import org.eclipse.ui.ide.IDE;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	private static final String PERSPECTIVE_MODELING_ID =
		"msi.gama.application.perspectives.ModelingPerspective";

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
		// GamlDescriptIO.getInstance().setCallback(new GamlCompilCallback());
		// OutputManager.debug("setCallback = " + (System.nanoTime() - start) / 1000000.0 + "ms");
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		// TODO dirty.. find another way to do that
		if ( workspace.getRoot().getProjects().length == 0 ) {
			linkSampleModelsToWorkspace(workspace);
		}

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
			new MultiStatus(SwtGui.PLUGIN_ID, 0, "Saving Workspace....", null);

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

	private void linkSampleModelsToWorkspace(final IWorkspace workspace) {
		URL urlRep = null;
		try {
			urlRep = FileLocator.toFileURL(new URL("platform:/plugin/msi.gama.models/models/"));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		File modelsRep = new File(urlRep.getPath());
		FileBean gFile = new FileBean(modelsRep);
		FileBean[] projects = gFile.getChildren();
		for ( FileBean project : projects ) {
			File dotFile = null;
			/* parcours des fils pour trouver le dot file et creer le lien vers le projet */
			FileBean[] children = project.getChildrenWithHiddenFiles();
			for ( int i = 0; i < children.length; i++ ) {
				if ( children[i].toString().equals(".project") ) {
					dotFile = new File(children[i].getPath());
				}
			}
			IProjectDescription tempDescription = null;
			/* If the '.project' doesn't exists we create one */
			if ( dotFile == null ) {
				/* Initialize file content */
				tempDescription = setProjectDescription(project);
			} else {
				final IPath location = new Path(dotFile.getAbsolutePath());
				try {
					tempDescription = workspace.loadProjectDescription(location);
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
			final IProjectDescription description = tempDescription;

			final IProject proj = workspace.getRoot().getProject(project.toString());
			WorkspaceModifyOperation operation = new WorkspaceModifyOperation() {

				@Override
				protected void execute(final IProgressMonitor monitor) throws CoreException,
					InvocationTargetException, InterruptedException {
					if ( !proj.exists() ) {
						proj.create(description, monitor);
					}
					proj.open(IResource.BACKGROUND_REFRESH, monitor);
				}
			};
			try {
				operation.run(null);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}

			setValuesProjectDescription(proj);
		}
	}

	private IProjectDescription setProjectDescription(final FileBean project) {
		final IProjectDescription description =
			ResourcesPlugin.getWorkspace().newProjectDescription(project.toString());
		final IPath location = new Path(project.getPath());
		description.setLocation(location);
		return description;
	}

	private void setValuesProjectDescription(final IProject proj) {
		/* Modify the project description */
		IProjectDescription desc = null;
		try {
			desc = proj.getDescription();
			/* Associate GamaNature et xtext nature to the project */
			String[] ids = desc.getNatureIds();
			String[] newIds = new String[ids.length + 2];
			System.arraycopy(ids, 0, newIds, 0, ids.length);
			newIds[ids.length] = "msi.gama.application.gamaNature";
			newIds[ids.length + 1] = "org.eclipse.xtext.ui.shared.xtextNature";
			desc.setNatureIds(newIds);
			proj.setDescription(desc, IResource.FORCE, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	public static void closeSimulationViews() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IViewReference[] views = page.getViewReferences();

		for ( IViewReference view : views ) {
			if ( view.getId().startsWith("msi.gama.application.") ) {
				page.hideView(view);
			}
		}
	}
}
