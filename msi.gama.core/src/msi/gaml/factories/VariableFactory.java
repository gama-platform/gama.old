/*******************************************************************************************************
 *
 * msi.gaml.factories.VariableFactory.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
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

import msi.gama.precompiler.GamlAnnotations.factory;
import msi.gama.precompiler.ISymbolKind;
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
@factory (
		handles = { ISymbolKind.Variable.CONTAINER, ISymbolKind.Variable.NUMBER, ISymbolKind.Variable.REGULAR,
				ISymbolKind.Variable.SIGNAL, ISymbolKind.PARAMETER })
public class VariableFactory extends SymbolFactory {

	public VariableFactory(final int... handles) {
		super(handles);
	}

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
					if (expr != null) { facets.putIfAbsent(key, expr); }
				}

			}
		}
		return new VariableDescription(keyword, enclosing, element, facets);
	}

}
