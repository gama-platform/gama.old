package msi.gama.gui.navigator;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;

public class BuiltInProjectNature implements IProjectNature {

	IProject project;

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
