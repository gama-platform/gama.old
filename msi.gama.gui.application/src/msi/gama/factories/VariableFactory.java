/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.factories;

import java.util.List;
import msi.gama.interfaces.*;
import msi.gama.internal.descriptions.*;
import msi.gama.internal.expressions.Facets;
import msi.gama.internal.types.Types;
import msi.gama.kernel.exceptions.GamlException;
import msi.gama.lang.utils.ISyntacticElement;
import msi.gama.precompiler.GamlAnnotations.handles;
import msi.gama.precompiler.*;

/**
 * Written by drogoul Modified on 26 nov. 2008
 * 
 * @todo Description
 */
@handles({ ISymbolKind.VARIABLE })
public class VariableFactory extends SymbolFactory {

	public VariableFactory() {
		// registeredSymbols.put(IVariable.VAR, null);
	}

	@Override
	protected String getKeyword(final ISyntacticElement cur) {
		if ( cur.getName().equals(ISymbol.PARAMETER) ) { return super.getKeyword(cur); }
		String keyword = cur.getAttribute(ISymbol.TYPE);
		if ( keyword == null ) {
			keyword = cur.getName();
		}
		if ( Types.get(keyword) == Types.NO_TYPE && !keyword.equals(ISymbol.SIGNAL) ) { return IType.AGENT_STR; }
		return keyword;
		// WARNING : no further test made here; can be totally false.
	}

	@Override
	protected IDescription buildDescription(final ISyntacticElement source, final String keyword,
		final List<IDescription> children, final Facets facets, final IDescription superDesc,
		final SymbolMetaDescription md) throws GamlException {

		if ( keyword.equals(ISymbol.SIGNAL) && facets.containsKey(ISymbol.ENVIRONMENT) ) {
			String env = facets.getString(ISymbol.ENVIRONMENT);
			String decay = facets.getString(ISymbol.DECAY, "0.1");
			String name = facets.getString(ISymbol.NAME);
			final String value = name + " < 0.1 ? 0.0 :" + name + " * ( 1 - " + decay + ")";
			VariableDescription vd =
				(VariableDescription) createDescription(superDesc, null, IType.FLOAT_STR,
					ISymbol.NAME, name, ISymbol.TYPE, IType.FLOAT_STR, ISymbol.VALUE, value,
					ISymbol.MIN, "0");

			SpeciesDescription environment =
				superDesc.getModelDescription().getSpeciesDescription(env);
			environment.addChild(vd);
		}

		return new VariableDescription(keyword, superDesc, facets, children, source);
	}

}
