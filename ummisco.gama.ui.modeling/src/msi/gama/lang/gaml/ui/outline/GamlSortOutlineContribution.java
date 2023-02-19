/*******************************************************************************************************
 *
 * GamlSortOutlineContribution.java, in ummisco.gama.ui.modeling, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.lang.gaml.ui.outline;

import org.eclipse.jface.action.Action;
import org.eclipse.xtext.ui.editor.outline.actions.SortOutlineContribution;

import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.resources.IGamaIcons;

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
		action.setImageDescriptor(GamaIcons.create(IGamaIcons.LEXICAL_SORT).descriptor());
	}

}
