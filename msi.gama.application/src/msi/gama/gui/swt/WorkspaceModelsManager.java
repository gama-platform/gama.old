/**
 * Created by drogoul, 16 nov. 2013
 * 
 */
package msi.gama.gui.swt;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.*;
import java.util.regex.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.navigator.FileBean;
import msi.gama.gui.navigator.commands.RefreshHandler;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.equinox.internal.app.CommandLineArgs;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.internal.ide.application.DelayedEventsProcessor;

/**
 * Class InitialModelOpener.
 * 
 * @author drogoul
 * @since 16 nov. 2013
 * 
 */
public class WorkspaceModelsManager {

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

			String[] filePaths = filesToOpen.toArray(new String[filesToOpen.size()]);
			filesToOpen.clear();

			for ( String path : filePaths ) {
				instance.openModelPassedAsArgument(path);
			}
		}
	}

	public static QualifiedName BUILTIN_PROPERTY = new QualifiedName("gama.builtin", "models");
	public static String BUILTIN_VERSION = Platform.getProduct().getDefiningBundle().getVersion().toString();
	public final static String builtInNature = "msi.gama.builtin.model";

	public void openModelPassedAsArgument(final String modelPath) {

		// printAllGuaranteedProperties();

		String filePath = modelPath;
		String expName = null;
		if ( filePath.contains("#") ) {
			String[] segments = filePath.split("#");
			if ( segments.length != 2 ) {
				System.out.println("Wrong definition of model and experiment in argument '" + filePath + "'");
				return;
			}
			filePath = segments[0];
			expName = segments[1];
		}
		IFile file = findAndLoadIFile(filePath);
		if ( file != null ) {
			try {
				// Force the project to rebuild itself in order to load the various XText plugins.
				file.touch(null);
				file.getProject().build(IncrementalProjectBuilder.FULL_BUILD, null);
			} catch (CoreException e) {
				System.out.println("File " + file.getFullPath() + " cannot be built");
				return;
			}
			if ( expName == null ) {
				GuiUtils.editModel(file);
			} else {
				GuiUtils.runModel(file, expName);
			}
		}
	}

	/**
	 * @param filePath
	 * @return
	 */
	private IFile findAndLoadIFile(final String filePath) {
		GuiUtils.debug("WorkspaceModelsManager.findAndLoadIFile " + filePath);
		// No error in case of an empty argument
		if ( filePath == null || filePath.isEmpty() || StringUtils.isWhitespace(filePath) ) { return null; }
		IPath path = new Path(filePath);
		// 1st case: the path can be identified as a file residing in the workspace
		IFile result = findInWorkspace(path);
		if ( result != null ) { return result; }
		// 2nd case: the path is outside the workspace
		result = findOutsideWorkspace(path);
		if ( result != null ) { return result; }
		System.out.println("File " + filePath +
			" cannot be located. Please check its name and location. Arguments provided were : " +
			Arrays.toString(CommandLineArgs.getApplicationArgs()));
		return null;
	}

	/**
	 * @param filePath
	 * @return
	 */
	private IFile findInWorkspace(final IPath originalPath) {
		GuiUtils.debug("WorkspaceModelsManager.findInWorkspace  " + originalPath);
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IPath workspacePath = new Path(Platform.getInstanceLocation().getURL().getPath());
		IPath filePath = originalPath.makeRelativeTo(workspacePath);
		IFile file = null;
		try {
			file = workspace.getRoot().getFile(filePath);
		} catch (Exception e) {
			return null;
		}
		if ( !file.exists() ) { return null; }
		return file;
	}

	private IFile findOutsideWorkspace(final IPath originalPath) {
		GuiUtils.debug("WorkspaceModelsManager.findOutsideWorkspace " + originalPath);
		File modelFile = new File(originalPath.toOSString());
		// TODO If the file does not exist we return null (might be a good idea to check other locations)
		if ( !modelFile.exists() ) { return null; }

		// We try to find a folder containing the model file which can be considered as a project
		FileBean projectFileBean = new FileBean(modelFile);
		File dotFile = null;
		while (projectFileBean != null && dotFile == null) {
			projectFileBean = projectFileBean.getParent();
			if ( projectFileBean != null ) {
				/* parcours des fils pour trouver le dot file et creer le lien vers le projet */
				FileBean[] children = projectFileBean.getChildrenWithHiddenFiles();
				for ( int i = 0; i < children.length; i++ ) {
					if ( children[i].toString().equals(".project") ) {
						dotFile = new File(children[i].getPath());
						break;
					}
				}
			}
		}

		// TODO If no project has been found... we return null, but it might be possible to create a project on the fly
		if ( dotFile == null || projectFileBean == null ) {
			GuiUtils
				.tell("The model '" +
					modelFile.getAbsolutePath() +
					"' does not seem to belong to an existing GAML project. It will be imported as part of the 'Unclassified models' project.");
			return createUnclassifiedModelsProjectAndAdd(originalPath);
		}

		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IPath location = new Path(dotFile.getAbsolutePath());
		final String pathToProject = projectFileBean.toString();

		try {
			// We load the project description.
			final IProjectDescription description = workspace.loadProjectDescription(location);
			if ( description != null ) {
				WorkspaceModifyOperation operation = new WorkspaceModifyOperation() {

					@Override
					protected void execute(final IProgressMonitor monitor) throws CoreException,
						InvocationTargetException, InterruptedException {
						// We try to get the project in the workspace
						IProject proj = workspace.getRoot().getProject(pathToProject);
						// If it does not exist, we create it
						if ( !proj.exists() ) {
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
						setValuesProjectDescription(proj, false);
					}
				};
				operation.run(new NullProgressMonitor() {

					@Override
					public void done() {
						RefreshHandler.run();
						GuiUtils.debug("Project " + workspace.getRoot().getProject(pathToProject).getName() +
							" has been imported");
					}

				});
			}
		} catch (InterruptedException e) {
			return null;
		} catch (InvocationTargetException e) {
			return null;
		} catch (CoreException e) {
			GuiUtils.error("Error wien importing project: " + e.getMessage());
		}
		IProject project = workspace.getRoot().getProject(pathToProject);
		String relativePathToModel =
			project.getName() + modelFile.getAbsolutePath().replace(projectFileBean.getPath(), "");
		return findInWorkspace(new Path(relativePathToModel));
	}

	/**
	 * 
	 */

	public static String UNCLASSIFIED_MODELS = "Unclassified Models";

	private IFile createUnclassifiedModelsProjectAndAdd(final IPath location) {
		IProject project = createOrUpdateProject(UNCLASSIFIED_MODELS);
		IFile iFile = null;
		try {
			IFolder modelFolder = project.getFolder(new Path("models"));
			if ( !modelFolder.exists() ) {
				modelFolder.create(true, true, null);
			}
			iFile = modelFolder.getFile(location.lastSegment());
			if ( iFile.exists() ) {
				if ( iFile.isLinked() ) {
					IPath path = iFile.getLocation();
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
			RefreshHandler.run();
			return iFile;
		} catch (CoreException e) {
			e.printStackTrace();
			GuiUtils.tell("The file " + (iFile == null ? location.lastSegment() : iFile.getFullPath().lastSegment()) +
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
		GuiUtils.debug("WorkspaceModelsManager.createUniqueFileFrom " + originalFile.getLocation() + " in " +
			modelFolder.getFullPath());
		IFile file = originalFile;
		while (file.exists()) {
			IPath path = file.getLocation();
			String fName = path.lastSegment();
			Pattern p = Pattern.compile("(.*?)(\\d+)?(\\..*)?");
			Matcher m = p.matcher(fName);
			if ( m.matches() ) {// group 1 is the prefix, group 2 is the number, group 3 is the suffix
				fName =
					m.group(1) + (m.group(2) == null ? 1 : Integer.parseInt(m.group(2)) + 1) +
						(m.group(3) == null ? "" : m.group(3));
			}
			file = modelFolder.getFile(fName);
		}
		return file;

	}

	public static void linkSampleModelsToWorkspace() {
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
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
		for ( final FileBean project : projects ) {
			File dotFile = null;
			/* parcours des fils pour trouver le dot file et creer le lien vers le projet */
			FileBean[] children = project.getChildrenWithHiddenFiles();
			for ( int i = 0; i < children.length; i++ ) {
				if ( children[i].toString().equals(".project") ) {
					dotFile = new File(children[i].getPath());
					break;
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

			WorkspaceModifyOperation operation = new WorkspaceModifyOperation() {

				@Override
				protected void execute(final IProgressMonitor monitor) throws CoreException, InvocationTargetException,
					InterruptedException {
					IProject proj = workspace.getRoot().getProject(project.toString());
					if ( !proj.exists() ) {
						proj.create(description, monitor);
					} else {
						// project exists but is not accessible
						if ( !proj.isAccessible() ) {
							proj.delete(true, null);
							proj = workspace.getRoot().getProject(project.toString());
							proj.create(description, monitor);
						}
					}
					proj.open(IResource.NONE, monitor);
					setValuesProjectDescription(proj, true);
				}
			};
			try {
				operation.run(null);
				stampWorkspaceFromModels();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}

		}
	}

	static private IProject createOrUpdateProject(final String name) {
		final IWorkspace ws = ResourcesPlugin.getWorkspace();
		final IProject[] projectHandle = new IProject[] { null };
		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {

			@Override
			protected void execute(final IProgressMonitor monitor) throws CoreException {
				monitor.beginTask("Creating or updating " + name, 2000);
				IProject project = ws.getRoot().getProject(name);
				IProjectDescription desc = null;
				if ( project.exists() ) {
					desc = project.getDescription();
				} else {
					desc = ws.newProjectDescription(name);
					project.create(desc, new SubProgressMonitor(monitor, 1000));
				}
				if ( monitor.isCanceled() ) { throw new OperationCanceledException(); }
				project.open(IResource.BACKGROUND_REFRESH, new SubProgressMonitor(monitor, 1000));
				projectHandle[0] = project;
				setValuesProjectDescription(project, false);
			}
		};
		try {
			op.run(null);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return projectHandle[0];
	}

	static public void setValuesProjectDescription(final IProject proj, final boolean builtin) {
		/* Modify the project description */
		IProjectDescription desc = null;
		try {
			desc = proj.getDescription();
			/* Automatically associate GamaNature and Xtext nature to the project */
			// String[] ids = desc.getNatureIds();
			String[] newIds = new String[builtin ? 3 : 2];
			// System.arraycopy(ids, 0, newIds, 0, ids.length);
			newIds[1] = "msi.gama.application.gamaNature";
			newIds[0] = "org.eclipse.xtext.ui.shared.xtextNature";
			// Addition of a special nature to the project.
			if ( builtin ) {
				newIds[2] = "msi.gama.builtin.model";
			}
			desc.setNatureIds(newIds);
			proj.setDescription(desc, IResource.FORCE, null);
			// Addition of a special persistent property to indicate that the project is built-in
			if ( builtin ) {
				proj.setPersistentProperty(BUILTIN_PROPERTY, BUILTIN_VERSION);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	static private IProjectDescription setProjectDescription(final FileBean project) {
		final IProjectDescription description =
			ResourcesPlugin.getWorkspace().newProjectDescription(project.toString());
		final IPath location = new Path(project.getPath());
		description.setLocation(location);
		return description;
	}

	public static void stampWorkspaceFromModels() {
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		try {
			String stamp = getCurrentGamaStampString();
			IWorkspaceRoot root = workspace.getRoot();
			String oldStamp = root.getPersistentProperty(BUILTIN_PROPERTY);
			if ( oldStamp != null ) {
				File stampFile =
					new File(new Path(root.getLocation().toOSString() + File.separator + oldStamp).toOSString());
				if ( stampFile.exists() ) {
					stampFile.delete();
				}
			}
			root.setPersistentProperty(BUILTIN_PROPERTY, stamp);
			File stampFile = new File(new Path(root.getLocation().toOSString() + File.separator + stamp).toOSString());
			if ( !stampFile.exists() ) {
				stampFile.createNewFile();
			}
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getCurrentGamaStampString() {
		String gamaStamp = null;
		try {
			URL urlRep = FileLocator.toFileURL(new URL("platform:/plugin/msi.gama.models/models/"));
			File modelsRep = new File(urlRep.getPath());
			long time = modelsRep.lastModified();
			gamaStamp = ".built_in_models_" + time;
			System.out.println("Version of the models in GAMA = " + gamaStamp);
		} catch (IOException e) {
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
