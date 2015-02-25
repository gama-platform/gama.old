/*********************************************************************************************
 * 
 * 
 * 'VirtualSharedModelsFolder.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.navigator;

import java.io.*;
import msi.gama.gui.swt.*;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.graphics.*;

public class VirtualSharedModelsFolder extends VirtualContent {

	File folder;

	private final FileFilter filter = new FileFilter() {

		@Override
		public boolean accept(final File arg0) {
			File f = arg0;
			return !f.isDirectory() && !f.getName().startsWith(".");
		}
	};

	public VirtualSharedModelsFolder(final Object root, final String name) {
		super(root, name);
		String path = Platform.getInstanceLocation().getURL().getPath() + ".svn_models/";
		folder = new File(path);
		if ( !folder.exists() ) {
			folder.mkdir();
		}
	}

	@Override
	public Font getFont() {
		return SwtGui.getNavigHeaderFont();
	}

	@Override
	public boolean hasChildren() {
		return folder.listFiles(filter).length > 0;

	}

	@Override
	public Object[] getNavigatorChildren() {
		File[] rr = folder.listFiles(filter);
		Object[] result = new Object[rr.length];
		for ( int i = 0; i < rr.length; i++ ) {
			result[i] = new SVNProject(this, rr[i]);
		}
		return result;
	}

	@Override
	public Image getImage() {
		return IGamaIcons.FOLDER_SHARED.image();
	}

	@Override
	public boolean isParentOf(final Object element) {
		return element instanceof SVNProject;
	}

	@Override
	public Color getColor() {
		return IGamaColors.GRAY_LABEL.color();
	}
}
