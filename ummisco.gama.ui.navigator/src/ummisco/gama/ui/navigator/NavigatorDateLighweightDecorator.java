/**
 * Created by drogoul, 11 févr. 2015
 * 
 */
package ummisco.gama.ui.navigator;

import java.text.DateFormat;
import java.util.Date;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.*;

/**
 * Class NavigatorBaseLighweightDecorator.
 * 
 * @author drogoul
 * @since 11 févr. 2015
 * 
 */
public class NavigatorDateLighweightDecorator implements ILightweightLabelDecorator {

	static final public String ID = "msi.gama.application.decorator";

	@Override
	public void decorate(final Object element, final IDecoration decoration) {
		if ( element instanceof IResource ) {
			IResource r = (IResource) element;
			long date = r.getLocalTimeStamp();
			DateFormat df = DateFormat.getInstance();
			String dateStr = df.format(new Date(date));
			decoration.addSuffix(" - " + dateStr);
		}
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
}
