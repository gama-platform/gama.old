/*******************************************************************************************************
 *
 * ModelsFinder.java, in ummisco.gama.ui.shared, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.access;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;

import msi.gama.common.GamlFileExtension;

/**
 * The Class ModelsFinder.
 */
public class ModelsFinder {

	/**
	 * Gets the all gama files in project.
	 *
	 * @param project the project
	 * @return the all gama files in project
	 */
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

	/**
	 * Gets the all gama UR is in project.
	 *
	 * @param project the project
	 * @return the all gama UR is in project
	 */
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