/*********************************************************************************************
 *
 * 'NavigatorLabelProvider.java, in plugin ummisco.gama.ui.navigator, is part of the source code of the GAMA modeling
 * and simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.navigator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import msi.gama.common.GamlFileExtension;
import msi.gaml.compilation.kernel.GamaBundleLoader;
import ummisco.gama.ui.metadata.FileMetaDataProvider;
import ummisco.gama.ui.resources.GamaFonts;
import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.resources.IGamaColors;
import ummisco.gama.ui.resources.IGamaIcons;

public class NavigatorLabelProvider extends CellLabelProvider implements ILabelProvider, IColorProvider, IFontProvider {

	@Override
	public String getText(final Object element) {
		if (element instanceof VirtualContent) { return ((VirtualContent) element).getName(); }
		return null;
	}

	public static boolean isResource(final IResource r) {
		final boolean[] isGamlFile = new boolean[1];
		try {
			r.accept(proxy -> {
				if (isGamlFile[0])
					return false;
				isGamlFile[0] = proxy.getType() == IResource.FILE && GamlFileExtension.isAny(proxy.getName());
				return !isGamlFile[0];
			}, IResource.NONE);
		} catch (final CoreException e1) {
			e1.printStackTrace();
		}
		return !isGamlFile[0];
	}

	@Override
	public Image getImage(final Object element) {
		if (element instanceof VirtualContent) { return ((VirtualContent) element).getImage(); }
		if (!(element instanceof IResource))
			return null;
		switch (((IResource) element).getType()) {
			case IResource.FOLDER:
				if (isResource((IFolder) element)) {
					return GamaIcons.create(IGamaIcons.FOLDER_RESOURCES).image();
				} else {
					return GamaIcons.create(IGamaIcons.FOLDER_MODEL).image();
				}
			case IResource.FILE:
				final IFile f = (IFile) element;
				final String s = f.getFileExtension();
				if (f.getFileExtension().equals("experiment")) { return GamaIcons.create("file.experiment2").image(); }
				if (isHandled(s)) {
					if (FileMetaDataProvider.isShapeFileSupport(f)) {
						return GamaIcons.create("file.shapesupport2").image();
					} else {
						return null;
					}
				} else {
					return GamaIcons.create("file.text2").image();
				}
			case IResource.PROJECT:
				return GamaIcons.create(IGamaIcons.FOLDER_PROJECT).image();
			default:
				return null;
		}
	}

	/**
	 * @param s
	 * @return
	 */
	private boolean isHandled(final String s) {
		return GamaBundleLoader.HANDLED_FILE_EXTENSIONS.contains(s);
	}

	@Override
	public void addListener(final ILabelProviderListener listener) {}

	@Override
	public void dispose() {}

	@Override
	public boolean isLabelProperty(final Object element, final String property) {
		return false;
	}

	@Override
	public void removeListener(final ILabelProviderListener listener) {}

	/**
	 * Method getFont()
	 * 
	 * @see org.eclipse.jface.viewers.IFontProvider#getFont(java.lang.Object)
	 */
	@Override
	public Font getFont(final Object element) {
		if (element instanceof VirtualContent) { return ((VirtualContent) element).getFont(); }
		if (element instanceof IResource) {
			switch (((IResource) element).getType()) {
				case IResource.FOLDER:
					if (isResource((IFolder) element))
						return GamaFonts.getResourceFont();
					else
						return GamaFonts.getNavigFolderFont();
				case IResource.PROJECT:
					return GamaFonts.getNavigHeaderFont();
				case IResource.FILE:
					return GamaFonts.getNavigFileFont();
			}
		}
		return GamaFonts.getNavigFolderFont();
	}

	/**
	 * Method getForeground()
	 * 
	 * @see org.eclipse.jface.viewers.IColorProvider#getForeground(java.lang.Object)
	 */
	@Override
	public Color getForeground(final Object element) {
		if (element instanceof VirtualContent) { return ((VirtualContent) element).getColor(); }
		if (!(element instanceof IFile)) { return IGamaColors.GRAY_LABEL.color(); }
		return null;
	}

	/**
	 * Method getBackground()
	 * 
	 * @see org.eclipse.jface.viewers.IColorProvider#getBackground(java.lang.Object)
	 */
	@Override
	public Color getBackground(final Object element) {
		return null;
	}

	/**
	 * Method update()
	 * 
	 * @see org.eclipse.jface.viewers.CellLabelProvider#update(org.eclipse.jface.viewers.ViewerCell)
	 */
	@Override
	public void update(final ViewerCell cell) {}

}
