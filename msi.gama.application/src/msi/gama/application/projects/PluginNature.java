/*******************************************************************************************************
 *
 * PluginNature.java, in msi.gama.application, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.application.projects;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

/**
 * The Class PluginNature.
 */
public class PluginNature implements IProjectNature {

	/** The project. */
	private IProject project;

	@Override
	public void configure() throws CoreException {}

	@Override
	public void deconfigure() throws CoreException {}

	@Override
	public IProject getProject() {
		return project;
	}

	@Override
	public void setProject(final IProject project) {
		this.project = project;
	}

}
