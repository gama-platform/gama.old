/**
 * Created by drogoul, 24 nov. 2014
 * 
 */
package msi.gama.lang.gaml.ui.outline;

import org.eclipse.jface.action.Action;
import org.eclipse.xtext.ui.editor.outline.actions.LinkWithEditorOutlineContribution;

import ummisco.gama.ui.resources.GamaIcons;

/**
 * The class GamlLinkWithEditorOutlineContribution.
 * 
 * @author drogoul
 * @since 24 nov. 2014
 * 
 */
public class GamlLinkWithEditorOutlineContribution extends LinkWithEditorOutlineContribution {

	/**
	 *
	 */
	public GamlLinkWithEditorOutlineContribution() {}

	@Override
	protected void configureAction(final Action action) {
		super.configureAction(action);
		action.setImageDescriptor(GamaIcons.create("navigator/navigator.link2").descriptor());
	}

}
