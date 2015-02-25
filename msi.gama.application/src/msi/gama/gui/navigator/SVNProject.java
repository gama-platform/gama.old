/**
 * Created by drogoul, 19 nov. 2014
 * 
 */
package msi.gama.gui.navigator;

import java.io.File;
import java.net.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.swt.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;

/**
 * Class SVNProject.
 * 
 * @author drogoul
 * @since 19 nov. 2014
 * 
 */
public class SVNProject extends VirtualContent {

	final File resource;

	/**
	 * @param root
	 * @param name
	 */
	public SVNProject(final Object root, final File resource) {
		super(root, "Project " + resource.getName().replace(".html", ""));
		this.resource = resource;
	}

	/**
	 * Method hasChildren()
	 * @see msi.gama.gui.navigator.VirtualContent#hasChildren()
	 */
	@Override
	public boolean hasChildren() {
		return false;
	}

	/**
	 * Method getChildren()
	 * @see msi.gama.gui.navigator.VirtualContent#getChildren()
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
		return GamaIcons.create("file.svn2").image();
	}

	/**
	 * Method getColor()
	 * @see msi.gama.gui.navigator.VirtualContent#getColor()
	 */
	@Override
	public Color getColor() {
		return SwtGui.getDisplay().getSystemColor(SWT.COLOR_BLACK);
	}

	/**
	 * Method isParentOf()
	 * @see msi.gama.gui.navigator.VirtualContent#isParentOf(java.lang.Object)
	 */
	@Override
	public boolean isParentOf(final Object element) {
		return false;
	}

	@Override
	public Font getFont() {
		return SwtGui.getNavigLinkFont(); // by default
	}

	/**
	 * @return
	 */
	public File getFile() {
		return resource;
	}

	/**
	 * Method handleDoubleClick()
	 * @see msi.gama.gui.navigator.VirtualContent#handleDoubleClick()
	 */
	@Override
	public boolean handleDoubleClick() {
		// IFileStore fileLocation = EFS.getLocalFileSystem().getStore(new URI("file://" + resource.getAbsolutePath()).toURL());
		// FileStoreEditorInput fileStoreEditorInput = new FileStoreEditorInput(fileLocation);
		// IWorkbenchPage page = SwtGui.getPage();
		try {
			URL url = new URI("file://" + resource.getAbsolutePath()).toURL();
			GuiUtils.showWebEditor(url.toString(), null);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return true;
	}

}
