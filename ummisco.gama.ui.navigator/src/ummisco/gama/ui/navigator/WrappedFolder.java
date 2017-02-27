/*********************************************************************************************
 *
 * 'WrappedFolder.java, in plugin ummisco.gama.ui.navigator, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.navigator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import ummisco.gama.ui.resources.GamaFonts;
import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.resources.IGamaColors;

/**
 * Class ImportFolder.
 *
 * @author drogoul
 * @since 5 févr. 2015
 *
 */
public class WrappedFolder extends VirtualContent {

	final Collection<String> fileNames;

	/**
	 * @param root
	 * @param name
	 */
	public WrappedFolder(final IFile root, final Collection<String> object, final String name) {
		super(root, name);
		fileNames = object;
	}

	/**
	 * Method hasChildren()
	 * 
	 * @see ummisco.gama.ui.navigator.VirtualContent#hasChildren()
	 */
	@Override
	public boolean hasChildren() {
		return !fileNames.isEmpty();
	}

	@Override
	public Font getFont() {
		return GamaFonts.getSmallFont(); // by default
	}

	/**
	 * Method getNavigatorChildren()
	 * 
	 * @see ummisco.gama.ui.navigator.VirtualContent#getNavigatorChildren()
	 */
	@Override
	public Object[] getNavigatorChildren() {
		if (fileNames.isEmpty()) { return EMPTY; }
		final List<WrappedFile> files = new ArrayList<>();
		final IFile file = (IFile) getParent();
		final IPath filePath = file.getLocation();
		final IPath projectPath = file.getProject().getLocation();
		for (final String s : fileNames) {
			if (s.startsWith("http"))
				continue;
			IPath resPath = new Path(s);
			if (!resPath.isAbsolute()) {
				final URI fileURI = URI.createFileURI(filePath.toString());
				final URI resURI = URI.createURI(resPath.toString()).resolve(fileURI);
				resPath = new Path(resURI.toFileString()).makeRelativeTo(projectPath);
			} else {
				resPath = resPath.makeRelativeTo(projectPath);
			}
			IFile newFile = null;
			try {
				newFile = file.getProject().getFile(resPath);
			} catch (final IllegalArgumentException e) {}
			if (newFile != null && newFile.exists()) {
				final WrappedFile proxy = new WrappedFile(this, newFile);
				files.add(proxy);
			}
		}
		return files.toArray();
	}

	/**
	 * Method getImage()
	 * 
	 * @see ummisco.gama.ui.navigator.VirtualContent#getImage()
	 */
	@Override
	public Image getImage() {
		return GamaIcons.create("gaml/_" + getName().toLowerCase()).image();
	}

	/**
	 * Method getColor()
	 * 
	 * @see ummisco.gama.ui.navigator.VirtualContent#getColor()
	 */
	@Override
	public Color getColor() {
		return IGamaColors.BLACK.color();
	}

}
