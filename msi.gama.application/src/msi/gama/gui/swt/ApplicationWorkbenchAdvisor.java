/*********************************************************************************************
 *
 *
 * 'ApplicationWorkbenchAdvisor.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.gui.swt;

import java.util.Arrays;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.ui.IDecoratorManager;
import org.eclipse.ui.application.*;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.ide.application.IDEWorkbenchAdvisor;
import org.eclipse.ui.statushandlers.*;
import msi.gama.application.projects.WorkspaceModelsManager;
import msi.gama.gui.swt.perspectives.ModelingPerspective;
import msi.gama.runtime.GAMA;
import msi.gaml.compilation.GamaBundleLoader;

public class ApplicationWorkbenchAdvisor extends IDEWorkbenchAdvisor {

	public ApplicationWorkbenchAdvisor() {
		super(WorkspaceModelsManager.processor);
		System.out.println("Welcome to GAMA version " + WorkspaceModelsManager.BUILTIN_VERSION);
		// openDocProcessor = openProcessor;
	}

	@Override
	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(final IWorkbenchWindowConfigurer configurer) {
		return new ApplicationWorkbenchWindowAdvisor(this, configurer);
	}

	@Override
	public void initialize(final IWorkbenchConfigurer configurer) {
		ResourcesPlugin.getPlugin().getStateLocation();
		super.initialize(configurer);
		IDE.registerAdapters();
		configurer.setSaveAndRestore(true);
		try {
			IDecoratorManager dm = configurer.getWorkbench().getDecoratorManager();
			dm.setEnabled("org.eclipse.pde.ui.binaryProjectDecorator", false);
			dm.setEnabled("org.eclipse.team.svn.ui.decorator.SVNLightweightDecorator", false);
			dm.setEnabled("msi.gama.application.decorator", true);
			dm.setEnabled("org.eclipse.ui.LinkedResourceDecorator", false);
			dm.setEnabled("org.eclipse.ui.VirtualResourceDecorator", false);
			dm.setEnabled("org.eclipse.xtext.builder.nature.overlay", false);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		/* Early build of the contributions made by plugins to GAMA */
		GamaBundleLoader.preBuildContributions();
		/* Linking the stock models with the workspace if they are not already */
		if ( checkCopyOfBuiltInModels() ) {
			WorkspaceModelsManager.linkSampleModelsToWorkspace();
		}

	}

	@Override
	public void postStartup() {
		super.postStartup();
		String[] args = Platform.getApplicationArgs();
		System.out.println("Arguments received by GAMA : " + Arrays.toString(args));
		if ( args.length >= 1 ) {
			WorkspaceModelsManager.instance.openModelPassedAsArgument(args[args.length - 1]);
		}
	}

	protected boolean checkCopyOfBuiltInModels() {
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject[] projects = workspace.getRoot().getProjects();
		// If no projects are registered at all, we are facing a fresh new workspace
		if ( projects.length == 0 ) { return true; }
		return false;
		// Following is not ready for prime time !
		// // If there are projects, we must be careful to distinguish user projects from built-in projects
		// List<IProject> builtInProjects = new ArrayList();
		// for ( IProject p : projects ) {
		// try {
		// // Assumption here : a non-accessible / linked project means a built-in model that is not accessible
		// // anymore. Maybe false sometimes... But how to check ?
		// System.out.println("Project = " + p.getName());
		// System.out.println(" ==== > Accessible : " + p.isAccessible());
		// System.out.println(" ==== > Open : " + p.isOpen());
		// System.out.println(" ==== > Linked : " + p.isLinked());
		// if ( !p.isAccessible() && p.isLinked() ) {
		// builtInProjects.add(p);
		// } else if ( p.isOpen() && p.getPersistentProperty(BUILTIN_PROPERTY) != null ) {
		// builtInProjects.add(p);
		// }
		// } catch (CoreException e) {
		// e.printStackTrace();
		// }
		// }
		// if ( builtInProjects.isEmpty() ) {
		// // only user projects there
		// return true;
		// }
		// String workspaceStamp = null;
		// try {
		// workspaceStamp = workspace.getRoot().getPersistentProperty(BUILTIN_PROPERTY);
		// System.out.println("Version of the models in workspace = " + workspaceStamp);
		// } catch (CoreException e) {
		// e.printStackTrace();
		// }
		// String gamaStamp = getCurrentGamaStampString();
		// // We dont know when the builtin models have been created -- there is probably a problem, but we do not try
		// to
		// // solve it
		// if ( gamaStamp == null ) {
		// System.err.println("Problem when trying to gather the date of creation of built-in models");
		// return false;
		// }
		// if ( gamaStamp.equals(workspaceStamp) ) {
		// // It's ok. The models in the workspace and in GAMA have the same time stamp
		// return false;
		// }
		// // We now have to (1) ask the user if he/she wants to update the models
		// boolean create =
		// MessageDialog
		// .openConfirm(
		// Display.getDefault().getActiveShell(),
		// "Update the models library",
		// "A new version of the built-in library of models is available. Would you like to update the ones present in the workspace?");
		// // (2) erase the built-in projects from the workspace
		// if ( !create ) { return false; }
		// for ( IProject p : builtInProjects ) {
		// try {
		// p.delete(true, null);
		// } catch (CoreException e) {
		// e.printStackTrace();
		// }
		// }
		// return true;
	}

	@Override
	public String getInitialWindowPerspectiveId() {
		return ModelingPerspective.ID;
	}

	/**
	 * A workbench pre-shutdown method calls to prompt a confirmation of the shutdown and perform a
	 * saving of the workspace
	 */
	@Override
	public boolean preShutdown() {
		try {
			GAMA.closeAllExperiments(false);
			// end-hqnghi
		} catch (Exception e) {
			e.printStackTrace();
		}

		return super.preShutdown();

	}

	/**
	 * Method getWorkbenchErrorHandler()
	 * @see org.eclipse.ui.internal.ide.application.IDEWorkbenchAdvisor#getWorkbenchErrorHandler()
	 */
	@Override
	public synchronized AbstractStatusHandler getWorkbenchErrorHandler() {
		return new AbstractStatusHandler() {

			@Override
			public void handle(final StatusAdapter statusAdapter, final int style) {
				System.err.println("GAMA Caught a workbench message : " + statusAdapter.getStatus().getMessage());
			}
		};
	}

}
