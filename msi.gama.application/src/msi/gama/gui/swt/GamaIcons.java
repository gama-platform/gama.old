/**
 * Created by drogoul, 12 sept. 2013
 * 
 */
package msi.gama.gui.swt;

import java.util.*;
import msi.gama.common.interfaces.IGui;
import msi.gama.common.util.GuiUtils;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.*;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Class GamaIcons.
 * 
 * @author drogoul
 * @since 12 sept. 2013
 * 
 */
public class GamaIcons /* implements IGamaIcons */{

	static String DEFAULT_PATH = "/icons/";

	static Map<String, GamaIcon> icons = new HashMap();

	public static class GamaIcon {

		static Map<String, Image> cache = new HashMap();

		String code;
		String path;
		ImageDescriptor descriptor;

		GamaIcon(final String c, final String p) {
			code = c;
			path = p;
		}

		public ImageDescriptor descriptor() {
			if ( descriptor == null ) {
				descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(IGui.PLUGIN_ID, DEFAULT_PATH + path + ".png");
				if ( descriptor == null ) {
					GuiUtils.debug("ERROR: Cannot find icon " + path);
					descriptor = getEclipseIconDescriptor(ISharedImages.IMG_OBJS_ERROR_TSK);
				}
			}
			return descriptor;
		}

		public Image image() {
			Image image = cache.get(code);
			if ( image == null ) {
				image = descriptor().createImage();
				cache.put(code, image);
			}
			return image;
		}
	}

	public static GamaIcon create(final String s) {
		GamaIcon result = new GamaIcon(s, s);
		icons.put(s, result);
		return result;
	}

	/*
	 * Use "ISharedImages.field"
	 */
	public static final ImageDescriptor getEclipseIconDescriptor(final String icon) {
		return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(icon);
	}

	public static final Image getEclipseIcon(final String icon) {
		return PlatformUI.getWorkbench().getSharedImages().getImage(icon);
	}

}
