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
package msi.gaml.commands;

import java.util.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.*;

/**
 * Written by drogoul Modified on 27 aožt 2010
 * 
 * Represents a Map of Facet objects. From there, text, tokens and values of facets can be
 * retrieved.
 * 
 */
public class Facets extends HashMap<String, ExpressionDescription> {

	// public final HashMap<String, Facet> facets = new HashMap();

	public Facets(final String ... strings) {
		addAll(strings);
	}

	public String getString(final String key) {
		return getString(key, null);
	}

	public String getString(final String key, final String ifAbsent) {
		ExpressionDescription f = get(key);
		if ( f == null ) { return ifAbsent; }
		return f.literalValue();
		// TODO Penser ˆ la facette nulle ?
	}

	public ExpressionDescription getTokens(final String key) {
		return get(key);
		// TODO Penser ˆ la facette nulle ?
	}

	public IExpression getExpr(final String key) {
		return getExpr(key, null);
		// TODO Penser ˆ la facette nulle ?
	}

	public IExpression getExpr(final String key, final IExpression ifAbsent) {
		ExpressionDescription f = get(key);
		if ( f == null ) { return ifAbsent; }
		return f.getExpression();
	}

	public void put(final String key, final String desc) {
		put(key, new ExpressionDescription(desc));
	}

	@Override
	public ExpressionDescription put(final String key, final ExpressionDescription desc) {
		super.put(key, desc);
		return desc;

	}

	public void putAsLabel(final String key, final String desc) {
		put(key, new ExpressionDescription(desc, false).setLabel());
	}

	public void put(final String key, final IExpression expr) {
		if ( containsKey(key) ) {
			get(key).setExpression(expr);
		} else {
			put(key, new ExpressionDescription(expr));
		}
	}

	public void putIfAbsent(final String key, final String desc) {
		if ( !containsKey(key) ) {
			put(key, desc);
		}
	}

	public void putIfAbsent(final String key, final IExpression expr) {
		if ( containsKey(key) ) { return; }
		put(key, new ExpressionDescription(expr));
	}

	public void addAll(final String ... strings) {
		int index = 0;
		if ( !(strings.length % 2 == 0) ) {
			index = 1;
		}
		for ( ; index < strings.length; index += 2 ) {
			put(strings[index], strings[index + 1]);
		}
	}

	public void addAll(final Facets ff) {
		// Without replacing
		for ( Map.Entry<String, ExpressionDescription> entry : ff.entrySet() ) {
			if ( !containsKey(entry.getKey()) ) {
				put(entry.getKey(), entry.getValue());
			}
		}
	}

	public boolean equals(final String key, final Object o) {
		ExpressionDescription f = get(key);
		return f == null ? o == null : f.equals(o);
	}

	public Object value(final String key, final IScope scope) throws GamaRuntimeException {
		return value(key, scope, null);
	}

	public Object value(final String key, final IScope scope, final Object ifAbsent)
		throws GamaRuntimeException {
		ExpressionDescription f = get(key);
		if ( f != null ) { return f.value(scope); }
		return ifAbsent;
	}

	public void dispose() {
		for ( Map.Entry<String, ExpressionDescription> entry : entrySet() ) {
			entry.getValue().dispose();
		}
		clear();
	}

	public Collection<ExpressionDescription> getTokens() {
		final Set<ExpressionDescription> set = new HashSet();
		for ( Map.Entry<String, ExpressionDescription> entry : entrySet() ) {
			ExpressionDescription ed = entry.getValue();
			if ( ed != null ) {
				set.add(ed);
			}
		}
		return set;
	}

	public IExpression compile(final String key, final IDescription context,
		final IExpressionFactory factory) throws GamaRuntimeException {
		return compile(key, context, null, factory);
	}

	public IExpression compileAsLabel(final String key) {
		ExpressionDescription f = get(key);
		if ( f == null ) { return null; }
		return f.setLabel().getExpression();
	}

	/**
	 * @throws GamaRuntimeException
	 * 
	 * @param key
	 * @param context
	 * @param ifAbsent
	 * @param factory
	 * @return
	 * @throws GamlException
	 */
	public IExpression compile(final String key, final IDescription context,
		final IExpression ifAbsent, final IExpressionFactory factory) throws GamaRuntimeException {
		if ( containsKey(key) ) { return get(key).compile(context, factory); }
		return ifAbsent;
	}

}
