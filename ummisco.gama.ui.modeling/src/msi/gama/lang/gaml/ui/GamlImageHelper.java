/**
 * Created by drogoul, 5 févr. 2015
 *
 */
package msi.gama.lang.gaml.ui;

import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.ui.IImageHelper;
import org.eclipse.xtext.ui.IImageHelper.IImageDescriptorHelper;

import com.google.common.collect.Maps;
import com.google.inject.Singleton;

import ummisco.gama.ui.resources.GamaIcons;

/**
 * The class GamlImageHelper.
 *
 * @author drogoul
 * @since 5 févr. 2015
 *
 */
@Singleton
public class GamlImageHelper implements IImageHelper, IImageDescriptorHelper {

	private static final String path = "gaml";
	private final Map<ImageDescriptor, Image> registry = Maps.newHashMapWithExpectedSize(10);

	public boolean exist(final String name) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(GamaIcons.PLUGIN_ID,
				GamaIcons.DEFAULT_PATH + name + ".png") != null;
	}

	/**
	 * @see org.eclipse.xtext.ui.IImageHelper.IImageDescriptorHelper#getImageDescriptor(java.lang.String)
	 */
	@Override
	public ImageDescriptor getImageDescriptor(final String name) {
		String s = name;
		if (s.endsWith(".png")) {
			s = s.replace(".png", "");
		}
		if (exist(path + "/" + s)) {
			return GamaIcons.create(path + "/" + s).descriptor();
		} else {
			return GamaIcons.create(path + "/_agent").descriptor();
		}

	}

	/**
	 * @see org.eclipse.xtext.ui.IImageHelper.IImageDescriptorHelper#getImageDescriptor(org.eclipse.swt.graphics.Image)
	 */
	@Override
	public ImageDescriptor getImageDescriptor(final Image image) {
		for (final Map.Entry<ImageDescriptor, Image> entry : registry.entrySet()) {
			if (entry.getValue().equals(image)) {
				return entry.getKey();
			}
		}
		final ImageDescriptor newDescriptor = ImageDescriptor.createFromImage(image);
		registry.put(newDescriptor, image);
		return newDescriptor;

	}

	/**
	 * @see org.eclipse.xtext.ui.IImageHelper#getImage(java.lang.String)
	 */
	@Override
	public Image getImage(final String name) {
		String s = name;
		if (s.endsWith(".png")) {
			s = s.replace(".png", "");
		}
		if (exist(path + "/" + s)) {
			return GamaIcons.create(path + "/" + s).image();
		} else {
			return GamaIcons.create(path + "/_agent").image();
		}
	}

	/**
	 * @see org.eclipse.xtext.ui.IImageHelper#getImage(org.eclipse.jface.resource.ImageDescriptor)
	 */
	@Override
	public Image getImage(ImageDescriptor descriptor) {
		if (descriptor == null) {
			descriptor = ImageDescriptor.getMissingImageDescriptor();
		}

		Image result = registry.get(descriptor);
		if (result != null) {
			return result;
		}
		result = descriptor.createImage();
		if (result != null) {
			registry.put(descriptor, result);
		}
		return result;
	}

}
