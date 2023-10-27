/*******************************************************************************************************
 *
 * Facets.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

import msi.gama.common.util.StringUtils;
import msi.gama.util.BiConsumerWithPruning;
import msi.gama.util.Collector;
import msi.gaml.descriptions.BasicExpressionDescription;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.descriptions.LabelExpressionDescription;
import msi.gaml.descriptions.StringBasedExpressionDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.interfaces.IGamlable;
import msi.gaml.types.IType;
import ummisco.gama.dev.utils.DEBUG;

/**
 * Written by drogoul Modified on 27 ao�t 2010
 *
 * Represents a Map of Facet objects. From there, text, tokens and values of facets can be retrieved.
 *
 */
public class Facets implements IGamlable {
	static {
		DEBUG.OFF();
	}

	/** The clean copy. */
	static Function<IExpressionDescription, IExpressionDescription> cleanCopy = IExpressionDescription::cleanCopy;

	/**
	 * The Class Facet.
	 */
	public static class Facet {

		/** The key. */
		public String key;

		/** The value. */
		public IExpressionDescription value;

		/**
		 * Instantiates a new facet.
		 *
		 * @param s
		 *            the s
		 * @param e
		 *            the e
		 */
		Facet(final String s, final IExpressionDescription e) {
			key = s;
			value = e;
		}

		@Override
		public String toString() {
			return key + " : " + value;
		}

		/**
		 * Clean copy.
		 *
		 * @return the facet
		 */
		public Facet cleanCopy() {
			return new Facet(key, value.cleanCopy());
		}

	}

	/** The Constant NULL. */
	public static final Facets NULL = new Facets();

	/** The facets. */
	Collector.AsList<Facet> facets;

	/**
	 * Instantiates a new facets.
	 */
	public Facets() {
		this(0);
	}

	/**
	 * Exists.
	 *
	 * @return true, if successful
	 */
	public boolean exists() {
		return facets != null && !facets.isEmpty();
	}

	/**
	 * Instantiates a new facets.
	 *
	 * @param size
	 *            the size
	 */
	protected Facets(final int size) {
		facets = Collector.getList();
		facets.setSize(size);
	}

	/**
	 * Instantiates a new facets.
	 *
	 * @param strings
	 *            the strings
	 */
	public Facets(final String... strings) {
		this(strings == null ? 0 : strings.length % 2);
		if (strings != null) {
			int index = strings.length % 2 != 0 ? 1 : 0;
			for (; index < strings.length; index += 2) {
				facets.add(new Facet(strings[index], StringBasedExpressionDescription.create(strings[index + 1])));
			}
		}
	}

	/**
	 * Instantiates a new facets.
	 *
	 * @param other
	 *            the other
	 */
	public Facets(final Facets other) {
		this(other == null ? 0 : other.size());
		if (other != null) { this.facets.addAll(other.facets); }
	}

	@Override
	public String toString() {
		return facets.toString();
	}

	/**
	 * Contains key.
	 *
	 * @param key
	 *            the key
	 * @return true, if successful
	 */
	public boolean containsKey(final String key) {
		return getFacet(key) != null;
	}

	/**
	 * Adds all the facets passed in parameter, replacing the existing ones if any
	 *
	 * @param newFacets
	 */
	public void putAll(final Facets newFacets) {
		for (final Facet f : newFacets.facets) { put(f.key, f.value); }
	}

	/**
	 * Complement with.
	 *
	 * @param newFacets
	 *            the new facets
	 */
	/*
	 * Same as putAll(), but without replacing the existing values
	 */
	public void complementWith(final Facets newFacets) {
		for (final Facet f : newFacets.facets) { putIfAbsent(f.key, f.value); }
	}

	/**
	 * Removes the.
	 *
	 * @param key
	 *            the key
	 */
	public void remove(final String key) {
		facets.removeIf(f -> f.key.equals(key));
	}

	/**
	 * Gets the.
	 *
	 * @param key
	 *            the key
	 * @return the i expression description
	 */
	public IExpressionDescription get(final String key) {
		if (key == null) return null;
		for (final Facet f : facets) { if (f.key.equals(key)) return f.value; }
		return null;
	}

	/**
	 * Gets the descr.
	 *
	 * @param keys
	 *            the keys
	 * @return the descr
	 */
	public IExpressionDescription getDescr(final String... keys) {
		for (final String key : keys) {
			final IExpressionDescription result = get(key);
			if (result != null) return result;
		}
		return null;

	}

	/**
	 * Gets the label.
	 *
	 * @param key
	 *            the key
	 * @return the label
	 */
	public String getLabel(final String key) {
		final IExpressionDescription f = get(key);
		if (f == null) return null;
		return StringUtils.toJavaString(f.toString());
	}

	/**
	 * Gets the expr.
	 *
	 * @param key
	 *            the key
	 * @return the expr
	 */
	public IExpression getExpr(final String key) {
		return getExpr(key, null);
	}

	/**
	 * Gets the expr.
	 *
	 * @param keys
	 *            the keys
	 * @return the expr
	 */
	public IExpression getExpr(final String... keys) {
		for (final String s : keys) {
			final IExpression expr = getExpr(s);
			if (expr != null) return expr;
		}
		return null;
	}

	/**
	 * Gets the expr.
	 *
	 * @param key
	 *            the key
	 * @param ifAbsent
	 *            the if absent
	 * @return the expr
	 */
	public IExpression getExpr(final String key, final IExpression ifAbsent) {
		final IExpressionDescription f = get(key);
		if (f == null) return ifAbsent;
		return f.getExpression();
	}

	/**
	 * Put as label.
	 *
	 * @param key
	 *            the key
	 * @param desc
	 *            the desc
	 */
	public void putAsLabel(final String key, final String desc) {
		put(key, LabelExpressionDescription.create(desc));
	}

	/**
	 * Put.
	 *
	 * @param key
	 *            the key
	 * @param expr
	 *            the expr
	 */
	public void put(final String key, final IExpression expr) {
		final IExpressionDescription result = get(key);
		if (result != null) {
			result.setExpression(expr);
		} else {
			add(key, new BasicExpressionDescription(expr));
		}
	}

	/**
	 * Adds the facet without performing any check, assuming it is not present in the map
	 *
	 * @param key
	 * @param expr
	 * @return
	 */
	private IExpressionDescription add(final String key, final IExpressionDescription expr) {
		final Facet f = new Facet(key, expr);
		facets.add(f);
		return expr;
	}

	/**
	 * Put.
	 *
	 * @param key
	 *            the key
	 * @param expr
	 *            the expr
	 */
	public void put(final String key, final IExpressionDescription expr) {
		final Facet existing = getFacet(key);
		if (existing != null) {
			existing.value = expr;
		} else {
			add(key, expr);
		}

	}

	/**
	 * Equals.
	 *
	 * @param key
	 *            the key
	 * @param o
	 *            the o
	 * @return true, if successful
	 */
	public boolean equals(final String key, final String o) {
		final IExpressionDescription f = get(key);
		return f == null ? o == null : f.equalsString(o);
	}

	/**
	 * Clear.
	 */
	public void clear() {
		facets.clear();
	}

	/**
	 * @return
	 */
	public Facets cleanCopy() {
		final Facets result = new Facets(size());
		for (final Facet f : facets) { result.facets.add(f.cleanCopy()); }
		return result;
	}

	/**
	 *
	 */
	public void dispose() {
		for (final Facet facet : facets) { if (facet.value != null) { facet.value.dispose(); } }
		Collector.release(facets);
		facets = null;
	}

	/**
	 * For each facet.
	 *
	 * @param visitor
	 *            the visitor
	 * @return true, if successful
	 */
	public boolean forEachFacet(final BiConsumerWithPruning<String, IExpressionDescription> visitor) {
		for (final Facet f : facets) { if (!visitor.process(f.key, f.value)) return false; }
		return true;
	}

	/**
	 * For each facet in.
	 *
	 * @param names
	 *            the names
	 * @param visitor
	 *            the visitor
	 * @return true, if successful
	 */
	public boolean forEachFacetIn(final Set<String> names,
			final BiConsumerWithPruning<String, IExpressionDescription> visitor) {
		if (names == null) return forEachFacet(visitor);
		for (final Facet f : facets) { if (names.contains(f.key) && !visitor.process(f.key, f.value)) return false; }
		return true;
	}

	/**
	 * Checks if is empty.
	 *
	 * @return true, if is empty
	 */
	public boolean isEmpty() { return facets.isEmpty(); }

	/**
	 * Size.
	 *
	 * @return the int
	 */
	public int size() {
		return facets.size();
	}

	/**
	 * Transform values.
	 *
	 * @param function
	 *            the function
	 */
	public void transformValues(final Function<IExpressionDescription, IExpressionDescription> function) {
		facets.forEach(f -> f.value = function.apply(f.value));
	}

	/**
	 * Put if absent.
	 *
	 * @param key
	 *            the key
	 * @param expr
	 *            the expr
	 */
	public void putIfAbsent(final String key, final IExpressionDescription expr) {
		if (!containsKey(key)) { put(key, expr); }
	}

	/**
	 * Gets the expr.
	 *
	 * @param index
	 *            the index
	 * @return the expr
	 */
	public IExpression getExpr(final int index) {
		if (index > facets.size() || index < 0) return null;
		return facets.items().get(index).value.getExpression();
	}

	/**
	 * Gets the first existing among.
	 *
	 * @param strings
	 *            the strings
	 * @return the first existing among
	 */
	public String getFirstExistingAmong(final String... strings) {
		for (final String s : strings) { if (containsKey(s)) return s; }
		return null;
	}

	/**
	 * Gets the type denoted by.
	 *
	 * @param key
	 *            the key
	 * @param context
	 *            the context
	 * @param noType
	 *            the no type
	 * @return the type denoted by
	 */
	public IType<?> getTypeDenotedBy(final String key, final IDescription context, final IType<?> noType) {
		final IExpressionDescription f = get(key);
		if (f == null) return noType;
		// DEBUG.OUT("Looking for the type of facet " + key);
		return f.getDenotedType(context);
	}

	/**
	 * Gets the facets.
	 *
	 * @return the facets
	 */
	public Collection<Facet> getFacets() { return facets; }

	/**
	 * Gets the facet.
	 *
	 * @param key
	 *            the key
	 * @return the facet
	 */
	protected Facet getFacet(final String key) {
		for (final Facet f : facets) { if (f.key.equals(key)) return f; }
		return null;
	}

}
