/**
 * Created by drogoul, 5 févr. 2015
 *
 */
package msi.gama.gui.navigator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.ui.*;
import org.eclipse.ui.part.FileEditorInput;
import msi.gama.gui.swt.*;
import ummisco.gama.ui.resources.GamaColors;

/**
 * Class WrappedFile.
 *
 * @author drogoul
 * @since 5 févr. 2015
 *
 */
public class WrappedFile extends VirtualContent implements IAdaptable {

	final IFile file;

	/**
	 * @param root
	 * @param name
	 */
	public WrappedFile(final VirtualContent root, final IFile wrapped) {
		super(root, wrapped.getName());
		file = wrapped;
	}

	@Override
	public boolean canBeDecorated() {
		return true;
	}

	/**
	 * Method hasChildren()
	 * @see msi.gama.gui.navigator.VirtualContent#hasChildren()
	 */
	@Override
	public boolean hasChildren() {
		return false;
	}

	@Override
	public Font getFont() {
		return SwtGui.getNavigLinkFont(); // by default
	}

	/**
	 * Method getNavigatorChildren()
	 * @see msi.gama.gui.navigator.VirtualContent#getNavigatorChildren()
	 */
	@Override
	public Object[] getNavigatorChildren() {
		return EMPTY;
	}

	/**
	 * Method getImage()
	 * @see msi.gama.gui.navigator.VirtualContent#getImage()
	 */
	@Override
	public Image getImage() {
		// should be handled by the label provider
		return null;
	}

	/**
	 * Method getColor()
	 * @see msi.gama.gui.navigator.VirtualContent#getColor()
	 */
	@Override
	public Color getColor() {
		return GamaColors.system(SWT.COLOR_BLACK);
	}

	/**
	 * Method isParentOf()
	 * @see msi.gama.gui.navigator.VirtualContent#isParentOf(java.lang.Object)
	 */
	// @Override
	// public boolean isParentOf(final Object element) {
	// return false;
	// }

	@Override
	public boolean handleDoubleClick() {
		IEditorInput editorInput = new FileEditorInput(file);
		IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(file.getName());
		if ( desc == null ) { return false; }
		IWorkbenchPage page = SwtGui.getPage();
		try {
			page.openEditor(editorInput, desc.getId());
		} catch (PartInitException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * @return
	 */
	public IFile getFile() {
		return file;
	}

	/**
	 * Method getAdapter()
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@Override
	public Object getAdapter(final Class adapter) {
		return adapter == IFile.class ? file : null;
	}

}
