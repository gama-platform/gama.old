/*********************************************************************************************
 *
 *
 * 'GamaNature.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.application.projects;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;

public class GamaNature implements IProjectNature {

	public static final String NATURE_ID = "msi.gama.application.gamaNature";

	private IProject project;

	@Override
	public void configure() throws CoreException {}

	@Override
	public void deconfigure() throws CoreException {

	}

	@Override
	public IProject getProject() {
		return project;
	}

	@Override
	public void setProject(final IProject project) {
		this.project = project;
	}
}
