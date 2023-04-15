/*******************************************************************************************************
 *
 * GamlUtils.java, in msi.gama.lang.gaml, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.lang.gaml.validation;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.Job;

/**
 * The Class GamlUtils.
 */
public class GamlUtils {

	/**
	 * Root.
	 *
	 * @return the i workspace root
	 */
	public static IWorkspaceRoot root() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}

	/**
	 * Creates the project.
	 *
	 * @param name the name
	 * @return the i project
	 * @throws CoreException the core exception
	 * @throws InvocationTargetException the invocation target exception
	 * @throws InterruptedException the interrupted exception
	 */
	public static IProject createProject(final String name)
			throws CoreException, InvocationTargetException, InterruptedException {
		final IProject project = root().getProject(name);
		createProject(project);
		return project;
	}

	/**
	 * Creates the project.
	 *
	 * @param project the project
	 * @return the i project
	 * @throws CoreException the core exception
	 */
	public static IProject createProject(final IProject project) throws CoreException {
		if (!project.exists()) {
			project.create(monitor());
		}
		project.open(monitor());
		return project;
	}

	// public static void assertNoErrorsInWorkspace() throws CoreException {
	// IMarker[] findMarkers = ResourcesPlugin.getWorkspace().getRoot().findMarkers(IMarker.PROBLEM, true,
	// IResource.DEPTH_INFINITE);
	// String msg = "";
	// for (IMarker iMarker : findMarkers) {
	// if (MarkerUtilities.getSeverity(iMarker) == IMarker.SEVERITY_ERROR)
	// msg += "\n - "+iMarker.getResource().getName()+":"+MarkerUtilities.getLineNumber(iMarker)+" -
	// "+MarkerUtilities.getMessage(iMarker) + "("+MarkerUtilities.getMarkerType(iMarker)+")";
	// }
	// if (msg.length()>0)
	// Assert.fail("Workspace contained errors: "+msg);
	// }

	/**
	 * Adds the nature.
	 *
	 * @param project the project
	 * @param nature the nature
	 * @throws CoreException the core exception
	 */
	public static void addNature(final IProject project, final String nature) throws CoreException {
		final IProjectDescription description = project.getDescription();
		final String[] natures = description.getNatureIds();

		// Add the nature
		final String[] newNatures = new String[natures.length + 1];
		System.arraycopy(natures, 0, newNatures, 0, natures.length);
		newNatures[natures.length] = nature;
		description.setNatureIds(newNatures);
		project.setDescription(description, null);
	}

	/**
	 * Adds the builder.
	 *
	 * @param project the project
	 * @param builderId the builder id
	 * @throws CoreException the core exception
	 */
	public static void addBuilder(final IProject project, final String builderId) throws CoreException {
		final IProjectDescription description = project.getDescription();
		final ICommand[] specs = description.getBuildSpec();
		final ICommand command = description.newCommand();
		command.setBuilderName(builderId);
		// Add the nature
		final ICommand[] specsModified = new ICommand[specs.length + 1];
		System.arraycopy(specs, 0, specsModified, 0, specs.length);
		specsModified[specs.length] = command;
		description.setBuildSpec(specsModified);
		project.setDescription(description, monitor());
	}

	/**
	 * Removes the nature.
	 *
	 * @param project the project
	 * @param nature the nature
	 * @throws CoreException the core exception
	 */
	public static void removeNature(final IProject project, final String nature) throws CoreException {
		final IProjectDescription description = project.getDescription();
		final String[] natures = description.getNatureIds();

		for (int i = 0; i < natures.length; ++i) {
			if (nature.equals(natures[i])) {
				// Remove the nature
				final String[] newNatures = new String[natures.length - 1];
				System.arraycopy(natures, 0, newNatures, 0, i);
				System.arraycopy(natures, i + 1, newNatures, i, natures.length - i - 1);
				description.setNatureIds(newNatures);
				project.setDescription(description, null);
				return;
			}
		}

	}

	/**
	 * Removes the builder.
	 *
	 * @param project the project
	 * @param builderId the builder id
	 * @throws CoreException the core exception
	 */
	public static void removeBuilder(final IProject project, final String builderId) throws CoreException {
		final IProjectDescription description = project.getDescription();
		final ICommand[] builderSpecs = description.getBuildSpec();

		for (int i = 0; i < builderSpecs.length; ++i) {
			if (builderId.equals(builderSpecs[i].getBuilderName())) {
				// Remove the builder
				final ICommand[] modifiedSpecs = new ICommand[builderSpecs.length - 1];
				System.arraycopy(builderSpecs, 0, modifiedSpecs, 0, i);
				System.arraycopy(builderSpecs, i + 1, modifiedSpecs, i, builderSpecs.length - i - 1);
				description.setBuildSpec(modifiedSpecs);
				project.setDescription(description, null);
				return;
			}
		}

	}

	// public static void setReference(final IProject from, final IProject to)
	// throws CoreException, InvocationTargetException,
	// InterruptedException {
	// new WorkspaceModifyOperation() {
	//
	// @Override
	// protected void execute(IProgressMonitor monitor)
	// throws CoreException, InvocationTargetException,
	// InterruptedException {
	// IProjectDescription projectDescription = from.getDescription();
	// IProject[] projects = projectDescription
	// .getReferencedProjects();
	// IProject[] newProjects = new IProject[projects.length + 1];
	// System.arraycopy(projects, 0, newProjects, 0, projects.length);
	// newProjects[projects.length] = to;
	// projectDescription.setReferencedProjects(newProjects);
	// from.setDescription(projectDescription, monitor());
	// }
	// }.run(monitor());
	// }

	// public static void removeReference(final IProject from, final IProject to)
	// throws CoreException, InvocationTargetException,
	// InterruptedException {
	// new WorkspaceModifyOperation() {
	//
	// @Override
	// protected void execute(IProgressMonitor monitor)
	// throws CoreException, InvocationTargetException,
	// InterruptedException {
	// IProjectDescription projectDescription = from.getDescription();
	// IProject[] projects = projectDescription
	// .getReferencedProjects();
	// for (int i = 0; i < projects.length; ++i) {
	// if (to.equals(projects[i])) {
	// // Remove the nature
	// IProject[] newProjects = new IProject[projects.length - 1];
	// System.arraycopy(projects, 0, newProjects, 0, i);
	// System.arraycopy(projects, i + 1, newProjects, i, projects.length
	// - i - 1);
	// projectDescription.setReferencedProjects(newProjects);
	// from.setDescription(projectDescription, null);
	// return;
	// }
	// }
	// }
	// }.run(monitor());
	// }

	// public static IFolder createFolder(String wsRelativePath) throws InvocationTargetException, InterruptedException
	// {
	// return createFolder(new Path(wsRelativePath));
	// }
	//
	// public static IFolder createFolder(IPath wsRelativePath) throws InvocationTargetException, InterruptedException {
	// final IFolder folder = root().getFolder(wsRelativePath);
	// new WorkspaceModifyOperation() {
	//
	// @Override
	// protected void execute(IProgressMonitor monitor)
	// throws CoreException, InvocationTargetException,
	// InterruptedException {
	// create(folder.getParent());
	// folder.delete(true, monitor());
	// folder.create(true, true, monitor());
	// }
	//
	// }.run(monitor());
	// return folder;
	// }
	//
	// public static IFile createFile(final String wsRelativePath, final String s)
	// throws CoreException, InvocationTargetException, InterruptedException {
	// return createFile(new Path(wsRelativePath), s);
	// }
	//
	// public static IFile createFile(final IPath wsRelativePath, final String s)
	// throws CoreException, InvocationTargetException, InterruptedException {
	// final IFile file = root().getFile(wsRelativePath);
	// new WorkspaceModifyOperation() {
	//
	// @Override
	// protected void execute(final IProgressMonitor monitor)
	// throws CoreException, InvocationTargetException, InterruptedException {
	// create(file.getParent());
	// file.delete(true, monitor());
	// try {
	// file.create(new StringInputStream(s, file.getCharset(true)), true, monitor());
	// } catch (final UnsupportedEncodingException exc) {
	// throw new CoreException(
	// new Status(IStatus.ERROR, "org.eclipse.xtext.junit4", exc.getMessage(), exc));
	// }
	// }
	//
	// }.run(monitor());
	// return file;
	// }

	// public static IResource file(final String path) {
	// return root().findMember(new Path(path));
	// }

	// public static byte[] fileToByteArray(final IFile file) throws CoreException, IOException {
	// try (InputStream contents = file.getContents()) {
	// return ByteStreams.toByteArray(contents);
	// }
	// }

	// public static String fileToString(final IFile file) throws CoreException, IOException {
	// return new String(fileToByteArray(file), file.getCharset());
	// }

	// public static boolean fileIsEmpty(final IFile file) throws IOException, CoreException {
	// try (InputStream contents = file.getContents()) {
	// return contents.read() == -1;
	// }
	// }

	// private static void create(final IContainer container)
	// throws CoreException, InvocationTargetException, InterruptedException {
	// new WorkspaceModifyOperation() {
	//
	// @Override
	// protected void execute(final IProgressMonitor monitor)
	// throws CoreException, InvocationTargetException, InterruptedException {
	// if (!container.exists()) {
	// create(container.getParent());
	// if (container instanceof IFolder) {
	// ((IFolder) container).create(true, true, monitor());
	// } else {
	// final IProject iProject = (IProject) container;
	// createProject(iProject);
	// }
	// }
	// }
	// }.run(monitor());
	// }

	/**
	 * Monitor.
	 *
	 * @return the i progress monitor
	 */
	public static IProgressMonitor monitor() {
		return new NullProgressMonitor();
	}

	/**
	 * Full build.
	 *
	 * @throws CoreException the core exception
	 */
	public static void fullBuild() throws CoreException {
		ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.FULL_BUILD, monitor());
	}

	/**
	 * Clean build.
	 *
	 * @throws CoreException the core exception
	 */
	public static void cleanBuild() throws CoreException {
		ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.CLEAN_BUILD, monitor());
	}

	/**
	 * @deprecated clients should use {@link #waitForBuild()} since it is much faster. Clients that really depend on the
	 *             delay before the build can use {@link #reallyWaitForAutoBuild()}.
	 */
	@Deprecated
	public static void waitForAutoBuild() {
		reallyWaitForAutoBuild();
	}

	/**
	 * A test that really should test the mechanism including the delay after the resource change event, could wait for
	 * the auto build.
	 */
	public static void reallyWaitForAutoBuild() {
		boolean wasInterrupted = false;
		do {
			try {
				Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);
				wasInterrupted = false;
			} catch (final OperationCanceledException e) {
				e.printStackTrace();
			} catch (final InterruptedException e) {
				wasInterrupted = true;
			}
		} while (wasInterrupted);
	}

	/**
	 * Wait for build.
	 */
	public static void waitForBuild() {
		waitForBuild(null);
	}

	/**
	 * Wait for build.
	 *
	 * @param monitor the monitor
	 */
	public static void waitForBuild(final IProgressMonitor monitor) {
		try {
			ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, monitor);
		} catch (final CoreException e) {
			throw new OperationCanceledException(e.getMessage());
		}
	}

	// public static void cleanWorkspace() throws CoreException {
	// try {
	// new WorkspaceModifyOperation() {
	//
	// @Override
	// protected void execute(final IProgressMonitor monitor)
	// throws CoreException, InvocationTargetException, InterruptedException {
	// final IProject[] visibleProjects = root().getProjects();
	// deleteProjects(visibleProjects);
	// final IProject[] hiddenProjects = root().getProjects(IContainer.INCLUDE_HIDDEN);
	// deleteProjects(hiddenProjects);
	// }
	// }.run(monitor());
	// } catch (final InvocationTargetException e) {
	// Exceptions.sneakyThrow(e.getCause());
	// } catch (final Exception e) {
	// throw new RuntimeException();
	// }
	// }

	/**
	 * Delete projects.
	 *
	 * @param projects the projects
	 * @throws CoreException the core exception
	 */
	protected static void deleteProjects(final IProject[] projects) throws CoreException {
		for (final IProject iProject : projects) {
			if (iProject.exists()) {
				iProject.delete(true, true, monitor());
			}
		}
	}

	/**
	 * Prints the marker.
	 *
	 * @param markers the markers
	 * @return the string
	 * @throws CoreException the core exception
	 */
	public static String printMarker(final IMarker[] markers) throws CoreException {
		final StringBuilder result = new StringBuilder();
		for (final IMarker marker : markers) {
			if (result.length() != 0) {
				result.append(", ");
			}
			result.append(marker.getAttribute(IMarker.MESSAGE));
		}
		return result.toString();
	}
}