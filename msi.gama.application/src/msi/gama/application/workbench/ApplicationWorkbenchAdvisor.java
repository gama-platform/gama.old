/*******************************************************************************************************
 *
 * ApplicationWorkbenchAdvisor.java, in msi.gama.application, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.application.workbench;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IDecoratorManager;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.PluginActionBuilder;
import org.eclipse.ui.internal.ide.application.IDEWorkbenchAdvisor;

import msi.gama.application.Application;
import msi.gama.application.workspace.WorkspaceModelsManager;
import msi.gama.common.interfaces.IEventLayerDelegate;
import msi.gama.common.interfaces.IGui;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.common.util.FileUtils;
import msi.gama.outputs.layers.EventLayerStatement;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.concurrent.GamaExecutorService;
import msi.gama.util.file.IGamaFile;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class ApplicationWorkbenchAdvisor.
 */
public class ApplicationWorkbenchAdvisor extends IDEWorkbenchAdvisor {

	{
		DEBUG.OFF();
	}

	/**
	 * Instantiates a new application workbench advisor.
	 */
	public ApplicationWorkbenchAdvisor() {
		super(Application.getOpenDocumentProcessor());
		//DEBUG.OUT(DEBUG.CALLER() + " is created");
	}

	@Override
	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(final IWorkbenchWindowConfigurer configurer) {
		return new ApplicationWorkbenchWindowAdvisor(this, configurer);
	}

	@Override
	public void initialize(final IWorkbenchConfigurer configurer) {

		ResourcesPlugin.getPlugin().getStateLocation();
		try {
			super.initialize(configurer);

			IDE.registerAdapters();
			configurer.setSaveAndRestore(true);

			final IDecoratorManager dm = configurer.getWorkbench().getDecoratorManager();
			dm.setEnabled("org.eclipse.pde.ui.binaryProjectDecorator", false);
			dm.setEnabled("org.eclipse.team.svn.ui.decorator.SVNLightweightDecorator", false);
			dm.setEnabled("msi.gama.application.decorator", true);
			dm.setEnabled("org.eclipse.ui.LinkedResourceDecorator", false);
			dm.setEnabled("org.eclipse.ui.VirtualResourceDecorator", false);
			dm.setEnabled("org.eclipse.xtext.builder.nature.overlay", false);
			if (Display.getCurrent() != null) {
				Display.getCurrent().getThread().setUncaughtExceptionHandler(GamaExecutorService.EXCEPTION_HANDLER);
			}
		} catch (final CoreException e) {
			// e.printStackTrace();
		}
		PluginActionBuilder.setAllowIdeLogging(false);
		ThemeHelper.install();
	}

	@Override
	public void postStartup() {
		super.postStartup();
		FileUtils.cleanCache();
		final String[] args = Platform.getApplicationArgs();
		// DEBUG.LOG("Arguments received by GAMA : " + DEBUG.TO_STRING(args));
		if (args.length > 0) {
			int i = 0;
			if (args[0].contains("--launcher.defaultAction")) { i += 2; }
			if (i < args.length) {
				String exp = args[i];
				if (!exp.endsWith(".gamr")) {
					WorkspaceModelsManager.instance.openModelPassedAsArgument(args[args.length - 1]);
					return;
				}
				for (final IEventLayerDelegate delegate : EventLayerStatement.delegates) {
					if (delegate.acceptSource(null, "launcher")) {
						delegate.createFrom(null, args[args.length - 1], null);
					}
				}
			}

		}

		if (GamaPreferences.Interface.CORE_STARTUP_MODEL.getValue()) {
			IGamaFile<?, ?> file = GamaPreferences.Interface.CORE_DEFAULT_MODEL.getValue();
			if (file != null && file.exists(null)) {
				StringBuilder name = new StringBuilder().append(file.getPath(null));
				String exp = GamaPreferences.Interface.CORE_DEFAULT_EXPERIMENT.getValue();
				if (exp != null && !exp.isBlank()) { name.append("#").append(exp); }
				WorkspaceModelsManager.instance.openModelPassedAsArgument(name.toString());
			}

		}
	}

	/**
	 * Check copy of built in models.
	 *
	 * @return true, if successful
	 */
	protected boolean checkCopyOfBuiltInModels() {

		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IProject[] projects = workspace.getRoot().getProjects();
		// If no projects are registered at all, we are facing a fresh new workspace
		if (projects.length == 0) return true;
		return false;
		// Following is not ready for prime time !
		// // If there are projects, we must be careful to distinguish user projects from built-in projects
		// List<IProject> builtInProjects = new ArrayList<>();
		// for ( IProject p : projects ) {
		// try {
		// // Assumption here : a non-accessible / linked project means a built-in model that is not accessible
		// // anymore. Maybe false sometimes... But how to check ?
		// DEBUG.OUT("Project = " + p.getName());
		// DEBUG.OUT(" ==== > Accessible : " + p.isAccessible());
		// DEBUG.OUT(" ==== > Open : " + p.isOpen());
		// DEBUG.OUT(" ==== > Linked : " + p.isLinked());
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
		// DEBUG.OUT("Version of the models in workspace = " + workspaceStamp);
		// } catch (CoreException e) {
		// e.printStackTrace();
		// }
		// String gamaStamp = getCurrentGamaStampString();
		// // We dont know when the builtin models have been created -- there is probably a problem, but we do not try
		// to
		// // solve it
		// if ( gamaStamp == null ) {
		// DEBUG.ERR("Problem when trying to gather the date of creation of built-in models");
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
		// "A new version of the built-in library of models is available. Would you like to update the ones present in
		// the workspace?");
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
	public String getInitialWindowPerspectiveId() { return IGui.PERSPECTIVE_MODELING_ID; }

	/**
	 * A workbench pre-shutdown method calls to prompt a confirmation of the shutdown and perform a saving of the
	 * workspace
	 */
	@Override
	public boolean preShutdown() {
		try {
			GAMA.closeAllExperiments(true, true);
			PerspectiveHelper.deleteCurrentSimulationPerspective();
			// So that they are not saved to the workbench.xmi file
			PerspectiveHelper.cleanPerspectives();
		} catch (final Exception e) {
			e.printStackTrace();
		}

		return super.preShutdown();

	}

	@Override
	public void postShutdown() {
		try {
			super.postShutdown();
		} catch (final Exception e) {
			// Remove the trace of exceptions
			// e.printStackTrace();
		}
	}

	@Override
	public void preStartup() {
		// Suspend background jobs while we startup
		Job.getJobManager().suspend();
		// super.preStartup();
		/* Linking the stock models with the workspace if they are not already */
		if (checkCopyOfBuiltInModels()) { WorkspaceModelsManager.instance.linkSampleModelsToWorkspace(); }

	}

	/**
	 * Method getWorkbenchErrorHandler()
	 *
	 * @see org.eclipse.ui.internal.ide.application.IDEWorkbenchAdvisor#getWorkbenchErrorHandler()
	 */
	// @Override
	// public synchronized AbstractStatusHandler getWorkbenchErrorHandler() {
	// return new AbstractStatusHandler() {
	//
	// @Override
	// public void handle(final StatusAdapter statusAdapter, final int style) {
	// final int severity = statusAdapter.getStatus().getSeverity();
	// if (severity == IStatus.INFO || severity == IStatus.CANCEL) return;
	// final Throwable e = statusAdapter.getStatus().getException();
	// if (e instanceof OutOfMemoryError) {
	// GamaExecutorService.EXCEPTION_HANDLER.uncaughtException(Thread.currentThread(), e);
	// }
	// final String message = statusAdapter.getStatus().getMessage();
	// // Stupid Eclipse
	// if (!message.contains("File toolbar contribution item") && !message.contains("Duplicate template id")) {
	// DEBUG.OUT("GAMA caught a workbench message : " + message);
	// }
	// if (e != null) { DEBUG.OUT("GAMA caught an error in the main application loop: " + e.getMessage()); }
	// }
	// };
	// }

	@Override
	public void eventLoopException(final Throwable t) {
		DEBUG.OUT("GAMA caught an error in the main application loop: " + t.getMessage());
	}

}
