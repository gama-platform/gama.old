/**
 * Created by drogoul, 24 nov. 2014
 * 
 */
package msi.gama.lang.gaml.ui.outline;

import msi.gama.gui.swt.GamaIcons;
import org.eclipse.jface.action.Action;
import org.eclipse.xtext.ui.editor.outline.actions.SortOutlineContribution;

/**
 * The class GamlSortOutlineContribution.
 * 
 * @author drogoul
 * @since 24 nov. 2014
 * 
 */
public class GamlSortOutlineContribution extends SortOutlineContribution {

	/**
	 *
	 */
	public GamlSortOutlineContribution() {}

	@Override
	protected void configureAction(final Action action) {
		super.configureAction(action);
		action.setImageDescriptor(GamaIcons.create("navigator/navigator.sort2").descriptor());
	}

}
