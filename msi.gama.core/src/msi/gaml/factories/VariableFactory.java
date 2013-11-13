/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.factories;

import static msi.gama.common.interfaces.IKeyword.*;
import java.util.*;
import msi.gama.precompiler.GamlAnnotations.factory;
import msi.gama.precompiler.*;
import msi.gaml.descriptions.*;
import msi.gaml.statements.Facets;
import org.eclipse.emf.ecore.EObject;

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
		final ChildrenProvider children, final IDescription enclosing, final SymbolProto proto) {
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
