/**
 * Created by drogoul, 19 janv. 2012
 * 
 */
package msi.gama.gui.views.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 * The class GamaAction.
 * 
 * @author drogoul
 * @since 19 janv. 2012
 * 
 */
public abstract class GamaAction extends Action {

	public GamaAction(final String text, final int style, final ImageDescriptor image) {
		super("", style);
		setToolTipText(text);
		setImageDescriptor(image);
	}

	@Override
	public abstract void run();
}
