/*********************************************************************************************
 *
 * 'WrappedFile.java, in plugin ummisco.gama.ui.navigator, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.navigator.contents;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.GamaFonts;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * Class WrappedFile.
 *
 * @author drogoul
 * @since 5 févr. 2015
 *
 */
public class LinkedFile extends VirtualContent implements IAdaptable {

	final IFile file;

	/**
	 * @param root
	 * @param name
	 */
	public LinkedFile(final VirtualContent root, final IFile wrapped) {
		super(root, wrapped.getName());
		file = wrapped;
	}

	/**
	 * Method hasChildren()
	 * 
	 * @see ummisco.gama.ui.navigator.contents.VirtualContent#hasChildren()
	 */
	@Override
	public boolean hasChildren() {
		return false;
	}

	@Override
	public Font getFont() {
		return GamaFonts.getNavigLinkFont(); // by default
	}

	/**
	 * Method getNavigatorChildren()
	 * 
	 * @see ummisco.gama.ui.navigator.contents.VirtualContent#getNavigatorChildren()
	 */
	@Override
	public Object[] getNavigatorChildren() {
		return EMPTY;
	}

	/**
	 * Method getImage()
	 * 
	 * @see ummisco.gama.ui.navigator.contents.VirtualContent#getImage()
	 */
	@Override
	public Image getImage() {
		return DEFAULT_LABEL_PROVIDER.getImage(getFile());
	}

	/**
	 * Method getColor()
	 * 
	 * @see ummisco.gama.ui.navigator.contents.VirtualContent#getColor()
	 */
	@Override
	public Color getColor() {
		return GamaColors.system(SWT.COLOR_BLACK);
	}

	/**
	 * Method isParentOf()
	 * 
	 * @see ummisco.gama.ui.navigator.contents.VirtualContent#isParentOf(java.lang.Object)
	 */
	// @Override
	// public boolean isParentOf(final Object element) {
	// return false;
	// }

	@Override
	public boolean handleDoubleClick() {
		final IEditorInput editorInput = new FileEditorInput(file);
		final IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(file.getName());
		if (desc == null) { return false; }
		final IWorkbenchPage page = WorkbenchHelper.getPage();
		try {
			page.openEditor(editorInput, desc.getId());
		} catch (final PartInitException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public IFile getFile() {
		return file;
	}

	/**
	 * Method getAdapter()
	 * 
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings ({ "unchecked", "rawtypes" })
	@Override
	public Object getAdapter(final Class adapter) {
		return adapter == IResource.class || adapter == IFile.class ? file : null;
	}

	@Override
	public int findMaxProblemSeverity() {
		final IFile file = getFile();
		try {
			return file.findMaxProblemSeverity(IMarker.PROBLEM, true, IResource.DEPTH_ZERO);
		} catch (final CoreException e) {}
		return -1;
	}

	@Override
	public void getSuffix(final StringBuilder sb) {}

	@Override
	public ImageDescriptor getOverlay() {
		return null;
	}

	@Override
	public VirtualContentType getType() {
		return VirtualContentType.FILE_REFERENCE;
	}
}
