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
import msi.gama.common.util.StringUtils;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.IExpression;

/**
 * Written by drogoul Modified on 27 aožt 2010
 * 
 * Represents a Map of Facet objects. From there, text, tokens and values of facets can be
 * retrieved.
 * 
 */
public class Facets {

	public static class Facet implements Map.Entry<String, IExpressionDescription> {

		String key;
		IExpressionDescription value;

		Facet(final String s, final IExpressionDescription e) {
			key = s;
			value = e;
		}

		@Override
		public String toString() {
			return key + " : " + value;
		}

		@Override
		public String getKey() {
			return key;
		}

		@Override
		public IExpressionDescription getValue() {
			return value;
		}

		@Override
		public IExpressionDescription setValue(final IExpressionDescription value) {
			IExpressionDescription old = this.value;
			this.value = value;
			return old;
		}
	}

	Facet[] facets;

	public Facets() {
		facets = new Facet[0];
	}

	public Facets(final String ... strings) {
		facets = new Facet[strings.length / 2];
		int index = 0;
		if ( !(strings.length % 2 == 0) ) {
			index = 1;
		}
		int i = 0;
		for ( ; index < strings.length; index += 2 ) {
			Facet f =
				new Facet(strings[index], new StringBasedExpressionDescription(strings[index + 1]));
			facets[i++] = f;
		}
	}

	@Override
	public String toString() {
		return Arrays.toString(facets);
	}

	public boolean containsKey(final String key) {
		return indexOf(key) != -1;
	}

	public Facet[] entrySet() {
		return facets;
	}

	/**
	 * Adds all the facets passed in parameter, replacing the existing ones if any
	 * @param newFacets
	 */
	public void putAll(final Facets newFacets) {
		if ( facets.length == 0 ) {
			facets = new Facet[newFacets.facets.length];
			System.arraycopy(newFacets.facets, 0, facets, 0, newFacets.facets.length);
			// facets = new Facet[newFacets.facets.length];
			// for ( int i = 0; i < facets.length; i++ ) {
			// Facet f = newFacets.facets[i];
			// if ( f != null ) {
			// facets[i] = f; // new Facet(f.key, f.value);
			// }
			// }
		} else {
			for ( Facet f : newFacets.entrySet() ) {
				if ( f != null ) {
					put(f.key, f.value);
				}
			}
		}
	}

	/*
	 * Same as putAll(), but without replacing the existing values
	 */
	public void complementWith(final Facets newFacets) {
		for ( Facet f : newFacets.entrySet() ) {
			if ( f != null ) {
				if ( indexOf(f.key) == -1 ) {
					add(f.key, f.value);
				}
			}
		}
	}

	public IExpressionDescription remove(final String key) {
		int i = indexOf(key);
		if ( i == -1 ) { return null; }
		IExpressionDescription expr = facets[i].value;
		facets[i] = null;
		return expr;
	}

	public IExpressionDescription get(final String key) {
		if ( key == null ) { return null; }
		for ( int i = 0; i < facets.length; i++ ) {
			Facet f = facets[i];
			if ( f != null && f.key.equals(key) ) { return f.value; }
		}
		return null;
	}

	public IExpressionDescription get(final Object key) {
		if ( key instanceof String ) { return get((String) key); }
		return null;

	}

	public String getLabel(final String key) {
		IExpressionDescription f = get(key);
		if ( f == null ) { return null; }
		return StringUtils.toJavaString(f.toString());
	}

	public IExpression getExpr(final String key) {
		return getExpr(key, null);
	}

	public IExpression getExpr(final String key, final IExpression ifAbsent) {
		IExpressionDescription f = get(key);
		if ( f == null ) { return ifAbsent; }
		return f.getExpression();
	}

	public IExpressionDescription put(final String key, final String desc) {
		return put(key, new StringBasedExpressionDescription(desc));
	}

	public IExpressionDescription putAsLabel(final String key, final String desc) {
		return put(key, new LabelExpressionDescription(desc));
	}

	public IExpressionDescription put(final String key, final IExpression expr) {
		IExpressionDescription result = get(key);
		if ( result != null ) {
			result.setExpression(expr);
			return result;
		}
		return put(key, new BasicExpressionDescription(expr));
	}

	/**
	 * Adds the facet without performing any check, assuming it is not present in the map
	 * @param key
	 * @param expr
	 * @return
	 */
	private IExpressionDescription add(final String key, final IExpressionDescription expr) {
		Facet f = new Facet(key, expr);
		Facet[] ff = new Facet[facets.length + 1];
		System.arraycopy(facets, 0, ff, 0, facets.length);
		facets = ff;
		facets[facets.length - 1] = f;
		return expr;
	}

	public IExpressionDescription put(final String key, final IExpressionDescription expr) {
		int i = indexOf(key);
		if ( i > -1 ) {
			IExpressionDescription previous = facets[i].value;
			facets[i].value = expr;
			if ( previous.getTarget() != null && expr.getTarget() == null ) {
				expr.setTarget(previous.getTarget());
			}
			return expr;
		}
		return add(key, expr);

	}

	private int indexOf(final String key) {
		if ( key == null ) { return -1; }
		for ( int i = 0; i < facets.length; i++ ) {
			if ( facets[i] != null && facets[i].key.equals(key) ) { return i; }
		}
		return -1;
	}

	public boolean equals(final String key, final String o) {
		IExpressionDescription f = get(key);
		return f == null ? o == null : f.equalsString(o);
	}

	public void dispose() {
		for ( int i = 0; i < facets.length; i++ ) {
			if ( facets[i] != null ) {
				facets[i].value.dispose();
			}
		}
		clear();
	}

	public void clear() {
		facets = new Facet[0];
	}

}
