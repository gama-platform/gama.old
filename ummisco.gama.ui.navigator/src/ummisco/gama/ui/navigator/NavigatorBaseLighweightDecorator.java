/**
 * Created by drogoul, 11 févr. 2015
 *
 */
package ummisco.gama.ui.navigator;

import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;

import msi.gama.runtime.GAMA;
import msi.gama.util.file.IGamaFileMetaData;
import ummisco.gama.ui.utils.PreferencesHelper;

/**
 * Class NavigatorBaseLighweightDecorator.
 *
 * @author drogoul
 * @since 11 févr. 2015
 *
 */
public class NavigatorBaseLighweightDecorator implements ILightweightLabelDecorator {

	@Override
	public void decorate(final Object element, final IDecoration decoration) {
		if (PreferencesHelper.NAVIGATOR_METADATA.getValue()) {
			final IGamaFileMetaData data = GAMA.getGui().getMetaDataProvider().getMetaData(element, false, false);
			if (data == null) {
				decoration.addSuffix(" ");
				return;
			}
			final String suffix = data.getSuffix();
			if (suffix != null && !suffix.isEmpty()) {
				decoration.addSuffix(" (" + suffix + ")");
			}
		}
	}

	@Override
	public void addListener(final ILabelProviderListener listener) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isLabelProperty(final Object element, final String property) {
		return true;
	}

	@Override
	public void removeListener(final ILabelProviderListener listener) {
	}

}
