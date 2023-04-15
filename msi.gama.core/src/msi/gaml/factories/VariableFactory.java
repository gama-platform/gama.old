/*******************************************************************************************************
 *
 * VariableFactory.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.factories;

import static msi.gama.common.interfaces.IKeyword.ON_CHANGE;
import static msi.gama.common.interfaces.IKeyword.PARAMETER;
import static msi.gama.common.interfaces.IKeyword.VAR;

import java.util.Map;

import org.eclipse.emf.ecore.EObject;

import msi.gaml.descriptions.ExperimentDescription;
import msi.gaml.descriptions.FacetProto;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.descriptions.SymbolProto;
import msi.gaml.descriptions.VariableDescription;
import msi.gaml.statements.Facets;

/**
 * Written by drogoul Modified on 26 nov. 2008
 *
 * @todo Description
 */
// @factory (
// handles = { ISymbolKind.Variable.CONTAINER, ISymbolKind.Variable.NUMBER, ISymbolKind.Variable.REGULAR,
// ISymbolKind.PARAMETER })
public class VariableFactory extends SymbolFactory {

	@Override
	protected IDescription buildDescription(final String keyword, final Facets facets, final EObject element,
			final Iterable<IDescription> children, final IDescription enclosing, final SymbolProto proto) {
		if (PARAMETER.equals(keyword)) {

			final Map<String, FacetProto> possibleFacets = proto.getPossibleFacets();
			// We copy the relevant facets from the targeted var of the
			// parameter
			VariableDescription targetedVar = enclosing.getModelDescription().getAttribute(facets.getLabel(VAR));
			if (targetedVar == null && enclosing instanceof ExperimentDescription) {
				targetedVar = ((ExperimentDescription) enclosing).getAttribute(facets.getLabel(VAR));
			}
			if (targetedVar != null) {
				for (final String key : possibleFacets.keySet()) {
					if (ON_CHANGE.equals(key)) { continue; }
					final IExpressionDescription expr = targetedVar.getFacet(key);
					if (expr != null) {
						IExpressionDescription copy = expr.cleanCopy();
						facets.putIfAbsent(key, copy);
					}
				}

			}
		}
		return new VariableDescription(keyword, enclosing, element, facets);
	}

}
