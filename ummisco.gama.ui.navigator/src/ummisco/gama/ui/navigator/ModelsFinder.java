package ummisco.gama.ui.navigator;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;

import msi.gama.common.GamlFileExtension;

public class ModelsFinder {

	public static List<IFile> getAllGamaFilesInProject(final IProject project) {
		final ArrayList<IFile> allGamaFiles = new ArrayList<>();
		try {
			if (project != null)
				project.accept(iR -> {
					if (iR.getType() == IResource.FILE && GamlFileExtension.isAny(iR.getName()))
						allGamaFiles.add((IFile) iR);
					return true;
				});
		} catch (final CoreException e) {}
		return allGamaFiles;
	}

	public static ArrayList<URI> getAllGamaFilesInProject(final IProject project, final URI without) {
		final ArrayList<URI> allGamaFiles = new ArrayList<>();
		final IWorkspaceRoot myWorkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		final IPath path = project.getLocation();
		recursiveFindGamaFiles(allGamaFiles, path, myWorkspaceRoot, without);
		return allGamaFiles;
	}

	private static void recursiveFindGamaFiles(final ArrayList<URI> allGamaFiles, final IPath path,
			final IWorkspaceRoot myWorkspaceRoot, final URI without) {
		final IContainer container = myWorkspaceRoot.getContainerForLocation(path);
		if (container != null)
			try {
				final IResource[] iResources = container.members();
				if (iResources != null)
					for (final IResource iR : iResources) {
						// for gama files
						if (GamlFileExtension.isAny(iR.getName())) {
							final URI uri = URI.createPlatformResourceURI(iR.getFullPath().toString(), true);
							if (!uri.equals(without)) {
								allGamaFiles.add(uri);
							}
						}
						if (iR.getType() == IResource.FOLDER) {
							final IPath tempPath = iR.getLocation();
							recursiveFindGamaFiles(allGamaFiles, tempPath, myWorkspaceRoot, without);
						}
					}
			} catch (final CoreException e) {
				e.printStackTrace();
			}
	}

}
