/*********************************************************************************************
 *
 *
 * 'WorkspaceModelsManager.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.application.projects;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.equinox.internal.app.CommandLineArgs;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.internal.ide.application.DelayedEventsProcessor;
import msi.gama.runtime.GAMA;
import msi.gaml.compilation.GamaBundleLoader;

/**
 * Class InitialModelOpener.
 *
 * @author drogoul
 * @since 16 nov. 2013
 *
 */
public class WorkspaceModelsManager {

	// private final static FileFilter noHiddenFiles = new FileFilter() {
	//
	// @Override
	// public boolean accept(final File f) {
	// return f != null && f.isDirectory() && !f.isHidden() && !f.getName().startsWith(".");
	// }
	// };

	public final static WorkspaceModelsManager instance = new WorkspaceModelsManager();
	public static OpenDocumentEventProcessor processor;

	public static void createProcessor(final Display display) {
		processor = new OpenDocumentEventProcessor(display);
	}

	public static class OpenDocumentEventProcessor extends DelayedEventsProcessor {

		private OpenDocumentEventProcessor(final Display display) {
			super(display);
		}

		private final ArrayList<String> filesToOpen = new ArrayList<String>(1);

		@Override
		public void handleEvent(final Event event) {
			if ( event.text != null ) {
				filesToOpen.add(event.text);
				System.out.println("RECEIVED FILE TO OPEN: " + event.text);
			}
		}

		@Override
		public void catchUp(final Display display) {
			if ( filesToOpen.isEmpty() ) { return; }

			final String[] filePaths = filesToOpen.toArray(new String[filesToOpen.size()]);
			filesToOpen.clear();

			for ( final String path : filePaths ) {
				instance.openModelPassedAsArgument(path);
			}
		}
	}

	public static QualifiedName BUILTIN_PROPERTY = new QualifiedName("gama.builtin", "models");
	public static String BUILTIN_VERSION = Platform.getProduct().getDefiningBundle().getVersion().toString();
	public final static String GAMA_NATURE = GamaNature.NATURE_ID;
	public final static String XTEXT_NATURE = "org.eclipse.xtext.ui.shared.xtextNature";
	public final static String PLUGIN_NATURE = PluginNature.NATURE_ID;
	public final static String BUILTIN_NATURE = BuiltinNature.NATURE_ID;

	public void openModelPassedAsArgument(final String modelPath) {

		// printAllGuaranteedProperties();

		String filePath = modelPath;
		String expName = null;
		if ( filePath.contains("#") ) {
			final String[] segments = filePath.split("#");
			if ( segments.length != 2 ) {
				System.out.println("Wrong definition of model and experiment in argument '" + filePath + "'");
				return;
			}
			filePath = segments[0];
			expName = segments[1];
		}
		final IFile file = findAndLoadIFile(filePath);
		if ( file != null ) {
			try {
				System.out.println("Rebuilding the model " + filePath);
				// Force the project to rebuild itself in order to load the various XText plugins.
				file.touch(null);
				file.getProject().build(IncrementalProjectBuilder.FULL_BUILD, null);
			} catch (final CoreException e) {
				System.out.println("File " + file.getFullPath() + " cannot be built");
				return;
			}
			if ( expName == null ) {
				System.out.println("Opening the model " + filePath + " in the editor");
				GAMA.getGui().editModel(file);
			} else {
				try {
					System.out.println("Trying to run experiment " + expName);
					GAMA.getGui().runModel(file, expName);
				} catch (final CoreException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * @param filePath
	 * @return
	 */
	private IFile findAndLoadIFile(final String filePath) {
		GAMA.getGui().debug("WorkspaceModelsManager.findAndLoadIFile " + filePath);
		// No error in case of an empty argument
		if ( filePath == null || filePath.isEmpty() || StringUtils.isWhitespace(filePath) ) { return null; }
		final IPath path = new Path(filePath);

		// 1st case: the path can be identified as a file residing in the workspace
		IFile result = findInWorkspace(path);
		if ( result != null ) { return result; }
		// 2nd case: the path is outside the workspace
		result = findOutsideWorkspace(path);
		if ( result != null ) { return result; }
		System.out.println(
			"File " + filePath + " cannot be located. Please check its name and location. Arguments provided were : " +
				Arrays.toString(CommandLineArgs.getApplicationArgs()));
		return null;
	}

	/**
	 * @param filePath
	 * @return
	 */
	private IFile findInWorkspace(final IPath originalPath) {
		GAMA.getGui().debug("WorkspaceModelsManager.findInWorkspace  " + originalPath);
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
		GAMA.getGui().debug("WorkspaceModelsManager.findOutsideWorkspace " + originalPath);
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
				for ( int i = 0; i < children.length; i++ ) {
					if ( children[i].getName().equals(".project") ) {
						dotFile = children[i];
						break;
					}
				}
			}
		}

		if ( dotFile == null || projectFileBean == null ) {
			GAMA.getGui().tell("The model '" + modelFile.getAbsolutePath() +
				"' does not seem to belong to an existing GAML project. It will be imported as part of the 'Unclassified models' project.");
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
									GAMA.getGui().tell(
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
						setValuesProjectDescription(proj, false, false, null);
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

	private IFile createUnclassifiedModelsProjectAndAdd(final IPath location) {
		final IProject project = createOrUpdateProject(UNCLASSIFIED_MODELS);
		IFile iFile = null;
		try {
			final IFolder modelFolder = project.getFolder(new Path("models"));
			if ( !modelFolder.exists() ) {
				modelFolder.create(true, true, null);
			}
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
			GAMA.getGui()
				.tell("The file " + (iFile == null ? location.lastSegment() : iFile.getFullPath().lastSegment()) +
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
		GAMA.getGui().debug("WorkspaceModelsManager.createUniqueFileFrom " + originalFile.getLocation() + " in " +
			modelFolder.getFullPath());
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

	private static void linkPluginsModelsToWorkspace() {
		for ( final String plugin : GamaBundleLoader.getPluginsWithModels().keySet() ) {
			linkModelsToWorkspace(plugin, GamaBundleLoader.getPluginsWithModels().get(plugin), false);
		}
	}

	public static void linkSampleModelsToWorkspace() {
		linkModelsToWorkspace("msi.gama.models", "models", true);
		linkPluginsModelsToWorkspace();
	}

	/**
	 * @param plugin
	 */

	private static void linkModelsToWorkspace(final String plugin, final String path, final boolean core) {
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final URL urlRep = null;
		File modelsRep = null;
		try {
			final String ext = path == "." ? "/" : "/" + path + "/";
			// urlRep = FileLocator.toFileURL(new URL("platform:/plugin/" + plugin + ext));
			// urlRep = urlRep.toURI().normalize().toURL();

			// urlRep = FileLocator.resolve(new URL("platform:/plugin/" + plugin + ext));

			final URL new_url = FileLocator.resolve(new URL("platform:/plugin/" + plugin + ext));
			final String path_s = new_url.getPath().replaceFirst("^/(.:/)", "$1");
			final java.nio.file.Path normalizedPath = Paths.get(path_s).normalize();
			// urlRep = normalizedPath.toUri().toURL();
			modelsRep = normalizedPath.toFile();

		} catch (final IOException e) {
			e.printStackTrace();
			return;
		} /*
			 * catch (URISyntaxException e) {
			 * e.printStackTrace();
			 * }
			 */

		// File modelsRep = new File(urlRep.getPath());
		System.out.println("chargemen" + modelsRep.getAbsolutePath());
		final Map<File, IPath> foundProjects = new HashMap();
		findProjects(modelsRep, foundProjects);
		importBuiltInProjects(plugin, core, workspace, foundProjects);

		stampWorkspaceFromModels();

	}

	private static final FilenameFilter isDotFile = new FilenameFilter() {

		@Override
		public boolean accept(final File dir, final String name) {
			return name.equals(".project");
		}

	};

	private static void findProjects(final File folder, final Map<File, IPath> found) {
		if ( folder == null ) { return; }
		final File[] dotFile = folder.listFiles(isDotFile);
		if ( dotFile == null ) { return; } // not a directory
		if ( dotFile.length == 0 ) { // no .project file
			for ( final File f : folder.listFiles() ) {
				findProjects(f, found);
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
	private static void importBuiltInProjects(final String plugin, final boolean core, final IWorkspace workspace,
		final Map<File, IPath> projects) {

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
					setValuesProjectDescription(proj, true, !core, plugin);
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

	static private IProject createOrUpdateProject(final String name) {
		final IWorkspace ws = ResourcesPlugin.getWorkspace();
		final IProject[] projectHandle = new IProject[] { null };
		final WorkspaceModifyOperation op = new WorkspaceModifyOperation() {

			@Override
			protected void execute(final IProgressMonitor monitor) throws CoreException {
				monitor.beginTask("Creating or updating " + name, 2000);
				final IProject project = ws.getRoot().getProject(name);
				// IProjectDescription desc = null;
				if ( !project.exists() ) {
					// desc = project.getDescription();
					// } else {
					final IProjectDescription desc = ws.newProjectDescription(name);
					project.create(desc, new SubProgressMonitor(monitor, 1000));
				}
				if ( monitor.isCanceled() ) { throw new OperationCanceledException(); }
				project.open(IResource.BACKGROUND_REFRESH, new SubProgressMonitor(monitor, 1000));
				projectHandle[0] = project;
				setValuesProjectDescription(project, false, false, null);
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

	static public void setValuesProjectDescription(final IProject proj, final boolean builtin, final boolean inPlugin,
		final String pluginName) {
		/* Modify the project description */
		IProjectDescription desc = null;
		try {
			desc = proj.getDescription();
			/* Automatically associate GamaNature and Xtext nature to the project */
			// String[] ids = desc.getNatureIds();
			final String[] newIds = new String[builtin ? 3 : 2];
			// System.arraycopy(ids, 0, newIds, 0, ids.length);
			newIds[1] = GAMA_NATURE;
			newIds[0] = XTEXT_NATURE;
			// Addition of a special nature to the project.
			if ( builtin ) {
				newIds[2] = inPlugin ? PLUGIN_NATURE : BUILTIN_NATURE;
			}
			desc.setNatureIds(newIds);
			if ( inPlugin && pluginName != null ) {
				desc.setComment(pluginName);
			} else {
				desc.setComment("");
			}
			proj.setDescription(desc, IResource.FORCE, null);
			// Addition of a special persistent property to indicate that the project is built-in
			if ( builtin ) {
				proj.setPersistentProperty(BUILTIN_PROPERTY, BUILTIN_VERSION);
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
			final String stamp = getCurrentGamaStampString();
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

	public static String getCurrentGamaStampString() {
		String gamaStamp = null;
		try {
			final URL tmpURL = new URL("platform:/plugin/msi.gama.models/models/");
			final URL new_url = FileLocator.resolve(tmpURL);
			final String path_s = new_url.getPath().replaceFirst("^/(.:/)", "$1");
			final java.nio.file.Path normalizedPath = Paths.get(path_s).normalize();
			final File modelsRep = normalizedPath.toFile();

			// loading file from URL Path is not a good idea. There are some bugs
			// File modelsRep = new File(urlRep.getPath());

			final long time = modelsRep.lastModified();
			gamaStamp = ".built_in_models_" + time;

			System.out.println("Version of the models in GAMA = " + gamaStamp);
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return gamaStamp;
	}

	public static void printAllGuaranteedProperties() {
		System.out.println("Arguments received by GAMA: " + Arrays.toString(Platform.getApplicationArgs()));
		System.out.println("Platform.instanceLocation: " + Platform.getInstanceLocation().getURL());
		System.out.println("Platform.configurationLocation: " + Platform.getConfigurationLocation().getURL());

		System.out.println("Platform.installLocation: " + Platform.getInstallLocation().getURL());

		System.out.println("Platform.location: " + Platform.getLocation());
		System.out.println("Location of '.'" + new File(".").getAbsolutePath());
		printAProperty("java.version", "Java version number");
		printAProperty("java.vendor", "Java vendor specific string");
		printAProperty("java.vendor.url", "Java vendor URL");
		printAProperty("java.home", "Java installation directory");
		printAProperty("java.class.version", "Java class version number");
		printAProperty("java.class.path", "Java classpath");
		printAProperty("os.name", "Operating System Name");
		printAProperty("os.arch", "Operating System Architecture");
		printAProperty("os.version", "Operating System Version");
		printAProperty("file.separator", "File separator");
		printAProperty("path.separator", "Path separator");
		printAProperty("line.separator", "Line separator");
		printAProperty("user.name", "User account name");
		printAProperty("user.home", "User home directory");
		printAProperty("user.dir", "User's current working directory");
	}

	public static void printAProperty(final String propName, final String desc) {
		System.out.println(desc + " = " + System.getProperty(propName) + ".");
	}

}
