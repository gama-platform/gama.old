/*********************************************************************************************
 * 
 *
 * 'ModelsLibraryFolder.java', in plugin 'msi.gama.application', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.navigator;

import java.util.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.swt.WorkspaceModelsManager;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;

/** @author Romain Lavaud */
public class ModelsLibraryFolder extends VirtualFolder {

	public ModelsLibraryFolder(final String name) {
		super(name);
	}

	@Override
	public Object[] getChildren() {
		List<IProject> totalList = Arrays.asList(ResourcesPlugin.getWorkspace().getRoot().getProjects());
		List<IProject> resultList = new ArrayList();
		for ( IProject project : totalList ) {
			try {
				if ( project.isAccessible() && project.isOpen() ) {
					IProjectDescription desc = project.getDescription();
					for ( String s : desc.getNatureIds() ) {
						if ( s.equals(WorkspaceModelsManager.builtInNature) ) {
							resultList.add(project);
							break;
						}
					}
				}

			} catch (CoreException e) {
				GuiUtils.debug(e);
			}
		}
		return resultList.toArray();
	}
}
