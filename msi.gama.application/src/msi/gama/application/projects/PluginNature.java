package msi.gama.application.projects;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;

public class PluginNature implements IProjectNature {

	public static final String NATURE_ID = "msi.gama.application.pluginNature";

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
