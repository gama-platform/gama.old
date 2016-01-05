/*********************************************************************************************
 *
 *
 * 'VariableFactory.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.factories;

import static msi.gama.common.interfaces.IKeyword.*;
import java.util.*;
import org.eclipse.emf.ecore.EObject;
import msi.gama.precompiler.GamlAnnotations.factory;
import msi.gama.precompiler.ISymbolKind;
import msi.gaml.descriptions.*;
import msi.gaml.statements.Facets;

/**
 * Written by drogoul Modified on 26 nov. 2008
 *
 * @todo Description
 */
@factory(handles = { ISymbolKind.Variable.CONTAINER, ISymbolKind.Variable.NUMBER, ISymbolKind.Variable.REGULAR,
	ISymbolKind.Variable.SIGNAL, ISymbolKind.PARAMETER })
public class VariableFactory extends SymbolFactory {

	public VariableFactory(final List<Integer> handles) {
		super(handles);
	}

	@Override
	protected IDescription buildDescription(final String keyword, final Facets facets, final EObject element,
		final ChildrenProvider children, final IDescription enclosing, final SymbolProto proto, final String plugin) {
		if ( keyword.equals(PARAMETER) ) {

			final Map<String, FacetProto> possibleFacets = proto.getPossibleFacets();
			// We copy the relevant facets from the targeted var of the parameter
			final VariableDescription targetedVar = enclosing.getModelDescription().getVariable(facets.getLabel(VAR));
			if ( targetedVar != null ) {
				Facets targetFacets = targetedVar.getFacets();
				for ( String key : possibleFacets.keySet() ) {
					IExpressionDescription expr = targetFacets.get(key);
					if ( expr != null ) {
						facets.putIfAbsent(key, expr);
					}
				}

			}
		}
		return new VariableDescription(keyword, enclosing, children, element, facets);
	}

}
