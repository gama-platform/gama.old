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
package msi.gaml.compilation;

import java.util.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.*;
import msi.gaml.descriptions.SymbolMetaDescription.FacetMetaDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.*;

/**
 * Written by drogoul Modified on 13 mai 2010 A simple class to serve as the root of all Gaml
 * Symbols
 * 
 * @todo Description
 * 
 */
public abstract class Symbol implements ISymbol {

	protected String name;
	protected final IDescription description;

	@Override
	public IDescription getDescription() {
		return description;
	}

	public Symbol(final IDescription description) {
		this.description = description;
		initFields();
	}

	protected void initFields() {}

	protected void verifyFacetType(final String facet) {
		final IExpression expr = getFacet(facet);
		if ( expr == null ) { return; }
		SymbolMetaDescription smd = description.getMeta();
		if ( smd == null ) { return; }
		FacetMetaDescription fmd = smd.getPossibleFacets().get(facet);
		if ( fmd == null ) { return; }
		if ( fmd.values.length == 0 ) {
			boolean compatible = false;
			IType actualType = expr.type();
			ModelDescription md = (ModelDescription) description.getModelDescription();
			TypesManager tm = md.getTypesManager();
			for ( String type : fmd.types ) {
				compatible = compatible || tm.get(type).isAssignableFrom(actualType);
			}
			if ( !compatible ) {
				description.flagWarning(new GamlException("Facet '" + facet + "' is expecting " +
					Arrays.toString(fmd.types) + " instead of " + actualType, description
					.getSourceInformation()));
			}
		} else {
			String s = expr.literalValue();
			boolean compatible = false;

			for ( String value : fmd.values ) {
				compatible = compatible || value.equals(s);
			}
			if ( !compatible ) {
				description.flagWarning(new GamlException("Facet '" + facet +
					"' is expecting a value among " + Arrays.toString(fmd.values) + " instead of " +
					s, description.getSourceInformation()));
			}

		}

	}

	@Override
	public IExpression getFacet(final String key) {
		if ( description == null ) { return null; }
		if ( !description.getFacets().containsKey(key) ) { return null; }
		IExpression result = description.getFacets().getExpr(key);
		if ( result == null ) {
			description.flagError(new GamlException("Facet " + key + " could not be compiled.",
				description.getSourceInformation()));
		}
		return result;
	}

	public IExpression getFacet(final String key, final IExpression ifAbsent) {
		return description == null ? ifAbsent : description.getFacets().getExpr(key, ifAbsent);
	}

	public Object getFacetValue(final IScope scope, final String key) throws GamaRuntimeException {
		return getFacetValue(scope, key, null);
	}

	public Object getFacetValue(final IScope scope, final String key, final Object defaultValue)
		throws GamaRuntimeException {
		IExpression exp = getFacet(key);
		return exp == null ? defaultValue : exp.value(scope);
	}

	public String getLiteral(final String key) {
		return getLiteral(key, null);
	}

	public String getLiteral(final String key, final String defaultValue) {
		IExpression exp = getFacet(key);
		return exp == null ? defaultValue : exp.literalValue();
	}

	protected void setFacet(final String key, final IExpression expr) {
		if ( description == null ) { return; }
		description.getFacets().put(key, expr);
	}

	@Override
	public boolean hasFacet(final String s) {
		return getFacet(s) != null;
	}

	@Override
	public abstract void setChildren(final List<? extends ISymbol> commands) throws GamlException;

	@Override
	public void setName(final String n) {
		name = n;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void dispose() {}

}
