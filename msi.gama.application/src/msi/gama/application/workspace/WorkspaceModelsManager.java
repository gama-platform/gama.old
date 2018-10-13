/*********************************************************************************************
 *
 * 'WorkspaceModelsManager.java, in plugin msi.gama.application, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.application.workspace;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.equinox.internal.app.CommandLineArgs;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.osgi.framework.Bundle;
import com.google.common.collect.Multimap;
import msi.gama.common.interfaces.IEventLayerDelegate;
import msi.gama.outputs.layers.EventLayerStatement;
import msi.gama.runtime.GAMA;
import msi.gaml.compilation.kernel.GamaBundleLoader;
import ummisco.gama.dev.utils.DEBUG;

/**
 * Class InitialModelOpener.
 *
 * @author drogoul
 * @since 16 nov. 2013
 *
 */
public class WorkspaceModelsManager {

	public final static String GAMA_NATURE = "msi.gama.application.gamaNature";
	public final static String XTEXT_NATURE = "org.eclipse.xtext.ui.shared.xtextNature";
	public final static String PLUGIN_NATURE = "msi.gama.application.pluginNature";
	public final static String TEST_NATURE = "msi.gama.application.testNature";
	public final static String BUILTIN_NATURE = "msi.gama.application.builtinNature";

	public static final QualifiedName BUILTIN_PROPERTY = new QualifiedName("gama.builtin", "models");
	// private static String BUILTIN_VERSION = null;

	public final static WorkspaceModelsManager instance = new WorkspaceModelsManager();
 
	public void openModelPassedAsArgument(final String modelPath) { 
		// printAllGuaranteedProperties();

		String filePath = modelPath;
		String expName = null;
		if ( filePath.contains("#") ) {
			final String[] segments = filePath.split("#");
			if ( segments.length != 2 ) {
				DEBUG.OUT("Wrong definition of model and experiment in argument '" + filePath + "'");
				return;
			}
			filePath = segments[0];
			expName = segments[1];
		}
		if ( filePath.endsWith(".experiment") && expName == null ) {
			expName = "0";
			// Verify that it works even if the included model defines experiments itself...

		}
		final IFile file = findAndLoadIFile(filePath);
		if ( file != null ) {
			final String en = expName;
//			final Runnable run = () -> {
				try {
					// DEBUG.OUT(Thread.currentThread().getName() + ": Rebuilding the model " + fp);
					// Force the project to rebuild itself in order to load the various XText plugins.
					file.touch(null);
					file.getProject().build(IncrementalProjectBuilder.FULL_BUILD, null);
				} catch (final CoreException e1) {
					DEBUG.OUT(Thread.currentThread().getName() + ": File " + file.getFullPath() + " cannot be built");
					return;
				}
				while (GAMA.getRegularGui() == null) {
					try {
						Thread.sleep(100);
						System.out
							.println(Thread.currentThread().getName() + ": waiting for the GUI to become available");
					} catch (final InterruptedException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
				}
				if ( en == null ) {
					// System.out
					// .println(Thread.currentThread().getName() + ": Opening the model " + fp + " in the editor");
					GAMA.getGui().editModel(null, file);
				} else {
					// DEBUG.OUT(Thread.currentThread().getName() + ": Trying to run experiment " + en);
					GAMA.getGui().runModel(file, en);
				}

//			};
//			new Thread(run, "Automatic opening of " + filePath).start();

		}
	}

	/**
	 * @param filePath
	 * @return
	 */
	private IFile findAndLoadIFile(final String filePath) {
		// GAMA.getGui().debug("WorkspaceModelsManager.findAndLoadIFile " + filePath);
		// No error in case of an empty argument
		if ( isBlank(filePath) ) { return null; }
		final IPath path = new Path(filePath);

		// 1st case: the path can be identified as a file residing in the workspace
		IFile result = findInWorkspace(path);
		if ( result != null ) { return result; }
		// 2nd case: the path is outside the workspace
		result = findOutsideWorkspace(path);
		if ( result != null ) { return result; }
		DEBUG.OUT(
			"File " + filePath + " cannot be located. Please check its name and location. Arguments provided were : " +
				Arrays.toString(CommandLineArgs.getApplicationArgs()));
		return null;
	}

	private boolean isBlank(final String cs) {
		if ( cs == null ) { return true; }
		if ( cs.isEmpty() ) { return true; }
		final int sz = cs.length();
		for ( int i = 0; i < sz; i++ ) {
			if ( !Character.isWhitespace(cs.charAt(i)) ) { return false; }
		}
		return true;
	}

	/**
	 * @param filePath
	 * @return
	 */
	private IFile findInWorkspace(final IPath originalPath) {
		// GAMA.getGui().debug("WorkspaceModelsManager.findInWorkspace " + originalPath);
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IPath workspacePath = new Path(Platform.getInstanceLocation().getURL().getPath());
		final IPath filePath = originalPath.makeRelativeTo(workspacePath);
		IFile file = null;
		try {
			file = workspace.getRoot().getFile(filePath);
		} catch (final Exception e) {
			return null;
		}
		if ( !file.exists() ) { return null; }
		return file;
	}

	private IFile findOutsideWorkspace(final IPath originalPath) {
		// GAMA.getGui().debug("WorkspaceModelsManager.findOutsideWorkspace " + originalPath);
		final File modelFile = new File(originalPath.toOSString());
		// TODO If the file does not exist we return null (might be a good idea to check other locations)
		if ( !modelFile.exists() ) { return null; }

		// We try to find a folder containing the model file which can be considered as a project
		File projectFileBean = new File(modelFile.getPath());
		File dotFile = null;
		while (projectFileBean != null && dotFile == null) {
			projectFileBean = projectFileBean.getParentFile();
			if ( projectFileBean != null ) {
				/* parcours des fils pour trouver le dot file et creer le lien vers le projet */
				final File[] children = projectFileBean.listFiles();
				if ( children != null ) {
					for ( final File element : children ) {
						if ( element.getName().equals(".project") ) {
							dotFile = element;
							break;
						}
					}
				}
			}
		}

		if ( dotFile == null || projectFileBean == null ) {
			MessageDialog.openInformation(Display.getDefault().getActiveShell(), "No project", "The model '" +
				modelFile.getAbsolutePath() +
				"' does not seem to belong to an existing GAML project. You can import it in an existing project or in the 'Unclassified models' project.");
			return createUnclassifiedModelsProjectAndAdd(originalPath);
		}

		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IPath location = new Path(dotFile.getAbsolutePath());
		final String pathToProject = projectFileBean.getName();

		try {
			// We load the project description.
			final IProjectDescription description = workspace.loadProjectDescription(location);
			if ( description != null ) {
				final WorkspaceModifyOperation operation = new WorkspaceModifyOperation() {

					@Override
					protected void execute(final IProgressMonitor monitor)
						throws CoreException, InvocationTargetException, InterruptedException {
						// We try to get the project in the workspace
						IProject proj = workspace.getRoot().getProject(pathToProject);
						// If it does not exist, we create it
						if ( !proj.exists() ) {
							// If a project with the same name exists
							final IProject[] projects = workspace.getRoot().getProjects();
							final String name = description.getName();
							for ( final IProject p : projects ) {
								if ( p.getName().equals(name) ) {
									MessageDialog.openInformation(Display.getDefault().getActiveShell(),
										"Existing project",
										"A project with the same name already exists in the workspace. The model '" +
											modelFile.getAbsolutePath() +
											" will be imported as part of the 'Unclassified models' project.");
									createUnclassifiedModelsProjectAndAdd(originalPath);
									return;
								}
							}

							proj.create(description, monitor);
						} else {
							// project exists but is not accessible, so we delete it and recreate it
							if ( !proj.isAccessible() ) {
								proj.delete(true, null);
								proj = workspace.getRoot().getProject(pathToProject);
								proj.create(description, monitor);
							}
						}
						// We open the project
						proj.open(IResource.NONE, monitor);
						// And we set some properties to it
						setValuesProjectDescription(proj, false, false, false, null);
					}
				};
				operation.run(new NullProgressMonitor() {

					// @Override
					// public void done() {
					// RefreshHandler.run();
					// // scope.getGui().tell("Project " + workspace.getRoot().getProject(pathToProject).getName() +
					// // " has been imported");
					// }

				});
			}
		} catch (final InterruptedException e) {
			return null;
		} catch (final InvocationTargetException e) {
			return null;
		} catch (final CoreException e) {
			GAMA.getGui().error("Error wien importing project: " + e.getMessage());
		}
		final IProject project = workspace.getRoot().getProject(pathToProject);
		final String relativePathToModel =
			project.getName() + modelFile.getAbsolutePath().replace(projectFileBean.getPath(), "");
		return findInWorkspace(new Path(relativePathToModel));
	}

	/**
	 *
	 */

	public static String UNCLASSIFIED_MODELS = "Unclassified Models";

	public IFolder createUnclassifiedModelsProject(final IPath location) throws CoreException {
		// First allow to select a parent folder
		final ContainerSelectionDialog dialog = new ContainerSelectionDialog(Display.getDefault().getActiveShell(),
			null, false, "Select a parent project or cancel to create a new project:");
		dialog.setTitle("Project selection");
		dialog.showClosedProjects(false);

		final int result = dialog.open();
		IProject project;
		IFolder modelFolder;

		if ( result == MessageDialog.CANCEL ) {
			project = createOrUpdateProject(UNCLASSIFIED_MODELS);
			modelFolder = project.getFolder(new Path("models"));
			if ( !modelFolder.exists() ) {
				modelFolder.create(true, true, null);
			}
		} else {
			final IContainer container =
				(IContainer) ResourcesPlugin.getWorkspace().getRoot().findMember((IPath) dialog.getResult()[0]);
			if ( container instanceof IProject ) {
				project = (IProject) container;
				modelFolder = project.getFolder(new Path("models"));
				if ( !modelFolder.exists() ) {
					modelFolder.create(true, true, null);
				}
			} else {
				project = container.getProject();
				modelFolder = (IFolder) container;
			}

		}

		return modelFolder;
	}

	IFile createUnclassifiedModelsProjectAndAdd(final IPath location) {
		IFile iFile = null;
		try {
			final IFolder modelFolder = createUnclassifiedModelsProject(location);
			iFile = modelFolder.getFile(location.lastSegment());
			if ( iFile.exists() ) {
				if ( iFile.isLinked() ) {
					final IPath path = iFile.getLocation();
					if ( path.equals(location) ) {
						// First case, this is a linked resource to the same location. In that case, we simply return
						// its name.
						return iFile;
					} else {
						// Second case, this resource is a link to another location. We create a filename that is
						// guaranteed not to exist and change iFile accordingly.
						iFile = createUniqueFileFrom(iFile, modelFolder);
					}
				} else {
					// Third case, this resource is local and we do not want to overwrite it. We create a filename that
					// is guaranteed not to exist and change iFile accordingly.
					iFile = createUniqueFileFrom(iFile, modelFolder);
				}
			}
			iFile.createLink(location, IResource.NONE, null);
			// RefreshHandler.run();
			return iFile;
		} catch (final CoreException e) {
			e.printStackTrace();
			MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Error in creation",
				"The file " + (iFile == null ? location.lastSegment() : iFile.getFullPath().lastSegment()) +
					" cannot be created because of the following exception " + e.getMessage());
			return null;
		}
	}

	/**
	 * @param lastSegment
	 * @param modelFolder
	 * @return
	 */
	private IFile createUniqueFileFrom(final IFile originalFile, final IFolder modelFolder) {
		IFile file = originalFile;
		while (file.exists()) {
			final IPath path = file.getLocation();
			String fName = path.lastSegment();
			final Pattern p = Pattern.compile("(.*?)(\\d+)?(\\..*)?");
			final Matcher m = p.matcher(fName);
			if ( m.matches() ) {// group 1 is the prefix, group 2 is the number, group 3 is the suffix
				fName = m.group(1) + (m.group(2) == null ? 1 : Integer.parseInt(m.group(2)) + 1) +
					(m.group(3) == null ? "" : m.group(3));
			}
			file = modelFolder.getFile(fName);
		}
		return file;

	}

	public static void linkSampleModelsToWorkspace() {

		final WorkspaceJob job = new WorkspaceJob("Updating the Built-in Models Library") {

			@Override
			public IStatus runInWorkspace(final IProgressMonitor monitor) {
				DEBUG.OUT("Asynchronous link of models library...");
				GAMA.getGui().refreshNavigator();
				return GamaBundleLoader.ERRORED ? Status.CANCEL_STATUS : Status.OK_STATUS;
			}

		};
		job.setUser(true);
		job.schedule();

	}

	public static void loadModelsLibrary() {
		while (!GamaBundleLoader.LOADED && !GamaBundleLoader.ERRORED) {
			try {
				Thread.sleep(100);
				DEBUG.OUT("Waiting for GAML subsystem to load...");
			} catch (final InterruptedException e) {}
		}
		if ( GamaBundleLoader.ERRORED ) {
			GAMA.getGui().tell("Error in loading GAML language subsystem. Please consult the logs");
			return;
		}
		DEBUG.OUT("Synchronous link of models library...");
		final Multimap<Bundle, String> pluginsWithModels = GamaBundleLoader.getPluginsWithModels();
		for ( final Bundle plugin : pluginsWithModels.keySet() ) {
			for ( final String entry : pluginsWithModels.get(plugin) ) {
				linkModelsToWorkspace(plugin, entry, false);
			}
		}
		final Multimap<Bundle, String> pluginsWithTests = GamaBundleLoader.getPluginsWithTests();
		for ( final Bundle plugin : pluginsWithTests.keySet() ) {
			for ( final String entry : pluginsWithTests.get(plugin) ) {
				linkModelsToWorkspace(plugin, entry, true);
			}
		}
	}

	/**
	 * @param plugin
	 */

	private static void linkModelsToWorkspace(final Bundle bundle, final String path, final boolean tests) {
		DEBUG.OUT("Linking library from bundle " + bundle.getSymbolicName() + " at path " + path);
		final boolean core = bundle.equals(GamaBundleLoader.CORE_MODELS);
		final URL fileURL = bundle.getEntry(path);
		File modelsRep = null;
		try {
			final URL resolvedFileURL = FileLocator.toFileURL(fileURL);
			// We need to use the 3-arg constructor of URI in order to properly escape file system chars
			final URI resolvedURI = new URI(resolvedFileURL.getProtocol(), resolvedFileURL.getPath(), null).normalize();
			modelsRep = new File(resolvedURI);

		} catch (final URISyntaxException e1) {
			e1.printStackTrace();
		} catch (final IOException e1) {
			e1.printStackTrace();
		}

		final Map<File, IPath> foundProjects = new HashMap<>();
		findProjects(modelsRep, foundProjects);
		importBuiltInProjects(bundle, core, tests, foundProjects);

		if ( core ) {
			stampWorkspaceFromModels();
		}

	}

	private static final FilenameFilter isDotFile = (dir, name) -> name.equals(".project");

	private static void findProjects(final File folder, final Map<File, IPath> found) {
		if ( folder == null ) { return; }
		final File[] dotFile = folder.listFiles(isDotFile);
		if ( dotFile == null ) { return; } // not a directory
		if ( dotFile.length == 0 ) { // no .project file
			final File[] files = folder.listFiles();
			if ( files != null ) {
				for ( final File f : folder.listFiles() ) {
					findProjects(f, found);
				}
			}
			return;
		}
		found.put(folder, new Path(dotFile[0].getAbsolutePath()));

	}

	/**
	 * @param plugin
	 * @param core
	 * @param workspace
	 * @param project
	 */
	private static void importBuiltInProjects(final Bundle plugin, final boolean core, final boolean tests,
		final Map<File, IPath> projects) {
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		for ( final Map.Entry<File, IPath> entry : projects.entrySet() ) {
			final File project = entry.getKey();
			final IPath location = entry.getValue();
			final WorkspaceModifyOperation operation = new WorkspaceModifyOperation() {

				@Override
				protected void execute(final IProgressMonitor monitor)
					throws CoreException, InvocationTargetException, InterruptedException {
					IProject proj = workspace.getRoot().getProject(project.getName());
					if ( !proj.exists() ) {
						proj.create(workspace.loadProjectDescription(location), monitor);
					} else {
						// project exists but is not accessible
						if ( !proj.isAccessible() ) {
							proj.delete(true, null);
							proj = workspace.getRoot().getProject(project.getName());
							proj.create(workspace.loadProjectDescription(location), monitor);
						}
					}
					proj.open(IResource.NONE, monitor);
					setValuesProjectDescription(proj, true, !core, tests, plugin);
				}
			};
			try {
				operation.run(null);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			} catch (final InvocationTargetException e) {
				e.printStackTrace();
			}
		}

	}

	static public IProject createOrUpdateProject(final String name) {
		final IWorkspace ws = ResourcesPlugin.getWorkspace();
		final IProject[] projectHandle = new IProject[] { null };
		final WorkspaceModifyOperation op = new WorkspaceModifyOperation() {

			@Override
			protected void execute(final IProgressMonitor monitor) throws CoreException {
				final SubMonitor m = SubMonitor.convert(monitor, "Creating or updating " + name, 2000);
				final IProject project = ws.getRoot().getProject(name);
				if ( !project.exists() ) {
					final IProjectDescription desc = ws.newProjectDescription(name);
					project.create(desc, m.split(1000));
				}
				if ( monitor.isCanceled() ) { throw new OperationCanceledException(); }
				project.open(IResource.BACKGROUND_REFRESH, m.split(1000));
				projectHandle[0] = project;
				setValuesProjectDescription(project, false, false, false, null);
			}
		};
		try {
			op.run(null);
		} catch (final InvocationTargetException e) {
			e.printStackTrace();
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
		return projectHandle[0];
	}

	// static public String GET_BUILT_IN_GAMA_VERSION() {
	// if (BUILTIN_VERSION == null) {
	// BUILTIN_VERSION = Platform.getProduct().getDefiningBundle().getVersion().toString();
	// }
	// return BUILTIN_VERSION;
	// }

	static public void setValuesProjectDescription(final IProject proj, final boolean builtin, final boolean inPlugin,
		final boolean inTests, final Bundle bundle) {
		/* Modify the project description */
		IProjectDescription desc = null;
		try {

			final List<String> ids = new ArrayList<>();
			ids.add(XTEXT_NATURE);
			ids.add(GAMA_NATURE);
			if ( inTests ) {
				ids.add(TEST_NATURE);
			} else if ( inPlugin ) {
				ids.add(PLUGIN_NATURE);
			} else if ( builtin ) {
				ids.add(BUILTIN_NATURE);
			}
			desc = proj.getDescription();
			desc.setNatureIds(ids.toArray(new String[0]));
			// Addition of a special nature to the project.
			if ( inTests && bundle == null ) {
				desc.setComment("user defined");
			} else if ( (inPlugin || inTests) && bundle != null ) {
				String name = bundle.getSymbolicName();
				final String[] ss = name.split("\\.");
				name = ss[ss.length - 1] + " plugin";
				desc.setComment(name);
			} else {
				desc.setComment("");
			}
			proj.setDescription(desc, IResource.FORCE, null);
			// Addition of a special persistent property to indicate that the project is built-in
			if ( builtin ) {
				proj.setPersistentProperty(BUILTIN_PROPERTY,
					Platform.getProduct().getDefiningBundle().getVersion().toString());
			}
		} catch (final CoreException e) {
			e.printStackTrace();
		}
	}

	// static private IProjectDescription setProjectDescription(final File project) {
	// final IProjectDescription description = ResourcesPlugin.getWorkspace().newProjectDescription(project.getName());
	// final IPath location = new Path(project.getAbsolutePath());
	// description.setLocation(location);
	// return description;
	// }

	public static void stampWorkspaceFromModels() {
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		try {
			final String stamp = WorkspacePreferences.getCurrentGamaStampString();
			final IWorkspaceRoot root = workspace.getRoot();
			final String oldStamp = root.getPersistentProperty(BUILTIN_PROPERTY);
			if ( oldStamp != null ) {
				final File stampFile =
					new File(new Path(root.getLocation().toOSString() + File.separator + oldStamp).toOSString());
				if ( stampFile.exists() ) {
					stampFile.delete();
				}
			}
			root.setPersistentProperty(BUILTIN_PROPERTY, stamp);
			final File stampFile =
				new File(new Path(root.getLocation().toOSString() + File.separator + stamp).toOSString());
			if ( !stampFile.exists() ) {
				stampFile.createNewFile();
			}
		} catch (final CoreException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isGamaProject(final File f) throws CoreException {
		for ( final String s : f.list() ) {
			if ( s.equals(".project") ) {
				IPath p = new Path(f.getAbsolutePath());
				p = p.append(".project");
				final IProjectDescription pd = ResourcesPlugin.getWorkspace().loadProjectDescription(p);
				if ( pd.hasNature(this.GAMA_NATURE) ) { return true; }
			}
		}
		return false;
	}

}
