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
import msi.gama.gui.swt.*;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.graphics.*;

public class UserProjectsFolder extends VirtualContent {

	public UserProjectsFolder(final Object root, final String name) {
		super(root, name);
	}

	@Override
	public Font getFont() {
		return SwtGui.getNavigHeaderFont();
	}

	@Override
	public boolean hasChildren() {
		return getNavigatorChildren().length > 0;
	}

	@Override
	public Object[] getNavigatorChildren() {
		List<IProject> totalList = Arrays.asList(ResourcesPlugin.getWorkspace().getRoot().getProjects());
		List<IProject> resultList = new ArrayList();
		// We only add the projects whose path does not contain the built-in models path
		for ( IProject project : totalList ) {
			if ( isParentOf(project) ) {
				resultList.add(project);
			}
		}
		return resultList.toArray();
	}

	@Override
	public Image getImage() {
		return IGamaIcons.FOLDER_USER.image();
	}

	@Override
	public boolean isParentOf(final Object element) {
		// System.out.println("Location to project : " + projectPath);
		if ( element instanceof IProject ) {
			IPath path = ((IProject) element).getLocation();
			if ( path == null ) { return false; }
			String projectPath = path.toString();
			if ( !projectPath.contains("msi.gama.models/models") ) { return true; }
		}
		return false;

	}

	/**
	 * Method getColor()
	 * @see msi.gama.gui.navigator.VirtualContent#getColor()
	 */
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
