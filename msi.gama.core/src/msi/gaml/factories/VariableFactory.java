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
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.factories;

import java.util.List;
import msi.gama.common.interfaces.*;
import msi.gaml.commands.Facets;
import msi.gama.precompiler.GamlAnnotations.handles;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.*;
import msi.gaml.types.*;

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
		if ( cur.getName().equals(IKeyword.PARAMETER) ) { return super.getKeyword(cur); }
		String keyword = cur.getAttribute(IKeyword.TYPE);
		if ( keyword == null ) {
			keyword = cur.getName();
		}
		if ( Types.get(keyword) == Types.NO_TYPE && !keyword.equals(IKeyword.SIGNAL) ) { return IType.AGENT_STR; }
		return keyword;
		// WARNING : no further test made here; can be totally false.
	}

	@Override
	protected IDescription buildDescription(final ISyntacticElement source, final String keyword,
		final List<IDescription> children, final Facets facets, final IDescription superDesc,
		final SymbolMetaDescription md) throws GamlException {

		if ( keyword.equals(IKeyword.SIGNAL) && facets.containsKey(IKeyword.ENVIRONMENT) ) {
			String env = facets.getString(IKeyword.ENVIRONMENT);
			String decay = facets.getString(IKeyword.DECAY, "0.1");
			String name = facets.getString(IKeyword.NAME);
			final String value = name + " < 0.1 ? 0.0 :" + name + " * ( 1 - " + decay + ")";
			VariableDescription vd =
				(VariableDescription) createDescription(superDesc, null, IType.FLOAT_STR,
					IKeyword.NAME, name, IKeyword.TYPE, IType.FLOAT_STR, IKeyword.VALUE, value,
					IKeyword.MIN, "0");

			SpeciesDescription environment =
				(SpeciesDescription) superDesc.getModelDescription().getSpeciesDescription(env);
			environment.addChild(vd);
		}

		return new VariableDescription(keyword, superDesc, facets, children, source);
	}

}
