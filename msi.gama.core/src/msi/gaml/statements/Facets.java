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
package msi.gaml.statements;

import gnu.trove.function.TObjectFunction;
import gnu.trove.map.hash.THashMap;
import gnu.trove.procedure.*;
import java.util.*;
import msi.gama.common.util.StringUtils;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.*;

/**
 * Written by drogoul Modified on 27 ao�t 2010
 * 
 * Represents a Map of Facet objects. From there, text, tokens and values of facets can be
 * retrieved.
 * 
 */
public class Facets extends THashMap<String, IExpressionDescription> {

	public Facets(final String ... strings) {
		setUp(strings.length / 2);
		int index = 0;
		if ( !(strings.length % 2 == 0) ) {
			index = 1;
		}
		for ( ; index < strings.length; index += 2 ) {
			put(strings[index], StringBasedExpressionDescription.create(strings[index + 1]));
		}
	}

	public Facets(final Facets facets) {
		super(facets);
	}

	/*
	 * Same as putAll(), but without replacing the existing values
	 */
	private class Complement implements TObjectObjectProcedure<String, IExpressionDescription> {

		@Override
		public boolean execute(final String s, final IExpressionDescription e) {
			putIfAbsent(s, e);
			return true;
		}
	};

	public void complementWith(final Facets newFacets) {
		newFacets.forEachEntry(new Complement());
	}

	public String getLabel(final String key) {
		IExpressionDescription f = get(key);
		if ( f == null ) { return null; }
		return StringUtils.toJavaString(f.toString());
	}

	public IExpression getExpr(final String ... keys) {
		IExpressionDescription desc = getDescr(keys);
		return desc == null ? null : desc.getExpression();
	}

	public IExpressionDescription getDescr(final String ... keys) {
		for ( String s : keys ) {
			IExpressionDescription f = get(s);
			if ( f != null ) { return f; }
		}
		return null;
	}

	public IType getTypeDenotedBy(final String key, final IDescription context) {
		return getTypeDenotedBy(key, context, Types.NO_TYPE);
	}

	public IType getTypeDenotedBy(final String key, final IDescription context, final IType noType) {
		IExpressionDescription f = get(key);
		if ( f == null ) { return noType; }
		return f.getDenotedType(context);
	}

	public IExpressionDescription putAsLabel(final String key, final String desc) {
		return put(key, LabelExpressionDescription.create(desc));
	}

	public IExpressionDescription put(final String key, final IExpression expr) {
		IExpressionDescription result = get(key);
		if ( result != null ) {
			result.setExpression(expr);
			return result;
		}
		return add(key, new BasicExpressionDescription(expr));
	}

	/**
	 * Adds the facet without performing any check, assuming it is not present in the map
	 * @param key
	 * @param expr
	 * @return
	 */
	private IExpressionDescription add(final String key, final IExpressionDescription expr) {
		put(key, expr);
		return expr;
	}

	@Override
	public IExpressionDescription put(final String key, final IExpressionDescription expr) {
		IExpressionDescription previous = get(key);
		if ( previous != null ) {
			if ( previous.getTarget() != null && expr.getTarget() == null ) {
				expr.setTarget(previous.getTarget());
			}
		}
		return super.put(key, expr);
	}

	public boolean equals(final String key, final String o) {
		IExpressionDescription f = get(key);
		return f == null ? o == null : f.equalsString(o);
	}

	static TObjectFunction<IExpressionDescription, IExpressionDescription> cleanCopy =
		new TObjectFunction<IExpressionDescription, IExpressionDescription>() {

			@Override
			public IExpressionDescription execute(final IExpressionDescription value) {
				return value.cleanCopy();
			}
		};

	public Facets cleanCopy() {
		Facets result = new Facets(this);
		result.transformValues(cleanCopy);
		return result;
	}

	static TObjectProcedure<IExpressionDescription> dispose = new TObjectProcedure<IExpressionDescription>() {

		@Override
		public boolean execute(final IExpressionDescription object) {
			object.dispose();
			return true;
		}
	};

	public void dispose() {
		forEachValue(dispose);
		clear();
	}

	@Override
	public Set<Map.Entry<String, IExpressionDescription>> entrySet() {
		return super.entrySet();
	}

}
