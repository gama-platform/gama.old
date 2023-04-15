/*******************************************************************************************************
 *
 * GamlLinkWithEditorOutlineContribution.java, in ummisco.gama.ui.modeling, is part of the source code of the GAMA
 * modeling and simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.lang.gaml.ui.outline;

import org.eclipse.jface.action.Action;
import org.eclipse.xtext.ui.editor.outline.actions.LinkWithEditorOutlineContribution;

import ummisco.gama.ui.resources.GamaIcon;
import ummisco.gama.ui.resources.IGamaIcons;

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
		action.setImageDescriptor(GamaIcon.named(IGamaIcons.EDITOR_LINK).descriptor());
		action.setDisabledImageDescriptor(GamaIcon.named(IGamaIcons.EDITOR_LINK).disabledDescriptor());

	}

}
