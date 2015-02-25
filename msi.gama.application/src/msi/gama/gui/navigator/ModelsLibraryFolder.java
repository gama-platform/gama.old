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
import msi.gama.gui.swt.*;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.*;

public class ModelsLibraryFolder extends VirtualContent {

	public ModelsLibraryFolder(final Object root, final String name) {
		super(root, name);
	}

	@Override
	public boolean hasChildren() {
		return getNavigatorChildren().length > 0;
	}

	@Override
	public Font getFont() {
		return SwtGui.getNavigHeaderFont();
	}

	@Override
	public Object[] getNavigatorChildren() {
		List<IProject> totalList = Arrays.asList(ResourcesPlugin.getWorkspace().getRoot().getProjects());
		List<IProject> resultList = new ArrayList();
		for ( IProject project : totalList ) {
			if ( isParentOf(project) ) {
				resultList.add(project);
			}
		}
		return resultList.toArray();
	}

	@Override
	public Image getImage() {
		return IGamaIcons.FOLDER_BUILTIN.image();
	}

	/**
	 * Method isParentOf()
	 * @see msi.gama.gui.navigator.VirtualContent#isParentOf(java.lang.Object)
	 */
	@Override
	public boolean isParentOf(final Object element) {
		if ( !(element instanceof IProject) ) { return false; }
		IProject project = (IProject) element;
		if ( project.isAccessible() ) {
			IProjectDescription desc;
			try {
				desc = project.getDescription();
				for ( String s : desc.getNatureIds() ) {
					if ( s.equals(WorkspaceModelsManager.builtInNature) ) { return true; }
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}

		}
		return false;
	}

	@Override
	public Color getColor() {
		return IGamaColors.GRAY_LABEL.color();
	}

	/**
	 * Method canBeDecorated()
	 * @see msi.gama.gui.navigator.VirtualContent#canBeDecorated()
	 */
	@Override
	public boolean canBeDecorated() {
		return true;
	}
}
