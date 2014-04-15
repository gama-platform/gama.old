/*********************************************************************************************
 * 
 *
 * 'UserProjectsFolder.java', in plugin 'msi.gama.application', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.navigator;

import java.util.*;
import org.eclipse.core.resources.*;

public class UserProjectsFolder extends VirtualFolder {

	public UserProjectsFolder(final String name) {
		super(name);
	}

	@Override
	public Object[] getChildren() {
		List<IProject> totalList =
			Arrays.asList(ResourcesPlugin.getWorkspace().getRoot().getProjects());
		List<IProject> resultList = new ArrayList();
		// We only add the projects whose path does not contain the built-in models path
		for ( IProject project : totalList ) {
			String projectPath = project.getLocation().toString();
			// System.out.println("Location to project : " + projectPath);
			if ( !projectPath.contains("msi.gama.models") ) {
				resultList.add(project);
			}
		}
		return resultList.toArray();
	}
}
