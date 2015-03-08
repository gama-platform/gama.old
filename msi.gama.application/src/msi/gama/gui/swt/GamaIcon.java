/**
 * Created by drogoul, 15 févr. 2015
 * 
 */
package msi.gama.gui.swt;

import msi.gama.common.interfaces.IGui;
import msi.gama.common.util.GuiUtils;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class GamaIcon {

	String code;
	String path;
	ImageDescriptor descriptor;

	GamaIcon(final String c, final String p) {
		code = c;
		path = p;
	}

	public ImageDescriptor descriptor() {
		if ( descriptor == null ) {
			descriptor =
				AbstractUIPlugin.imageDescriptorFromPlugin(IGui.PLUGIN_ID, GamaIcons.DEFAULT_PATH + path + ".png");

			if ( descriptor == null ) {
				GuiUtils.debug("ERROR: Cannot find icon " + GamaIcons.DEFAULT_PATH + path + ".png");
				descriptor = ImageDescriptor.getMissingImageDescriptor();
			}
		}
		return descriptor;
	}

	public Image image() {
		Image image = GamaIcons.getInstance().getImageInCache(code);
		if ( image == null ) {
			image = GamaIcons.getInstance().putImageInCache(code, descriptor().createImage());
		}
		return image;
	}

	public String getCode() {
		return code;
	}
}