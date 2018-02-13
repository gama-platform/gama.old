package ummisco.gama.ui.access;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;

import msi.gama.common.GamlFileExtension;

public class ModelsFinder {

	public static List<IFile> getAllGamaFilesInProject(final IProject project) {
		final ArrayList<IFile> allGamaFiles = new ArrayList<>();
		try {
			if (project != null)
				project.accept(iR -> {
					if (GamlFileExtension.isAny(iR.getName()))
						allGamaFiles.add((IFile) iR.requestResource());
					return true;
				}, IResource.FILE);
		} catch (final CoreException e) {}
		return allGamaFiles;
	}

	public static List<URI> getAllGamaURIsInProject(final IProject project) {
		final ArrayList<URI> allGamaFiles = new ArrayList<>();
		try {
			if (project != null)
				project.accept(iR -> {
					if (GamlFileExtension.isAny(iR.getName())) {
						final URI uri = URI.createPlatformResourceURI(iR.requestFullPath().toString(), true);
						allGamaFiles.add(uri);
					}
					return true;
				}, IResource.FILE);
		} catch (final CoreException e) {}
		return allGamaFiles;
	}
}