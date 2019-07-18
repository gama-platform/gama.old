/*******************************************************************************************************
 *
 * msi.gaml.statements.FacetsArray.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8)
 *
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import msi.gama.common.interfaces.BiConsumerWithPruning;
import msi.gama.common.interfaces.IGamlable;
import msi.gama.common.util.StringUtils;
import msi.gama.util.Collector;
import msi.gaml.descriptions.BasicExpressionDescription;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.descriptions.LabelExpressionDescription;
import msi.gaml.descriptions.StringBasedExpressionDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * Written by drogoul Modified on 27 ao�t 2010
 *
 * Represents a Map of Facet objects. From there, text, tokens and values of facets can be retrieved.
 *
 */
public class Facets implements IGamlable {

	static Function<IExpressionDescription, IExpressionDescription> cleanCopy = value -> value.cleanCopy();

	public static class Facet {

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

		public String getKey() {
			return key;
		}

		public IExpressionDescription getValue() {
			return value;
		}

		public IExpressionDescription setValue(final IExpressionDescription value) {
			final IExpressionDescription old = this.value;
			this.value = value;
			return old;
		}

		public Facet cleanCopy() {
			return new Facet(key, value.cleanCopy());
		}
	}

	public static final Facets NULL = new Facets();

	final Collector<Facet, List<Facet>> facets = new Collector.Ordered<>();

	public Facets() {}

	public Facets(final String... strings) {
		this();
		if (strings != null) {
			int index = strings.length % 2 != 0 ? 1 : 0;
			for (; index < strings.length; index += 2) {
				facets.add(new Facet(strings[index], StringBasedExpressionDescription.create(strings[index + 1])));
			}
		}
	}

	public Facets(final Facets other) {
		this();
		this.facets.addAll(other.facets);
	}

	@Override
	public String toString() {
		return facets.toString();
	}

	public boolean containsKey(final String key) {
		for (final Facet f : facets) {
			if (f.key.equals(key)) { return true; }
		}
		return false;
	}

	/**
	 * Adds all the facets passed in parameter, replacing the existing ones if any
	 *
	 * @param newFacets
	 */
	public void putAll(final Facets newFacets) {
		for (final Facet f : newFacets.facets) {
			put(f.key, f.value);
		}
	}

	/*
	 * Same as putAll(), but without replacing the existing values
	 */
	public void complementWith(final Facets newFacets) {
		for (final Facet f : newFacets.facets) {
			putIfAbsent(f.key, f.value);
		}

	}

	public void remove(final String key) {
		facets.removeIf(f -> f.key.equals(key));
	}

	public IExpressionDescription get(final String key) {
		if (key == null) { return null; }
		for (final Facet f : facets) {
			if (f.key.equals(key)) { return f.value; }
		}
		return null;
	}

	public IExpressionDescription getDescr(final String... keys) {
		for (final Facet f : facets) {
			for (final String key : keys) {
				if (f.key.equals(key)) { return f.value; }
			}
		}
		return null;

	}

	public String getLabel(final String key) {
		final IExpressionDescription f = get(key);
		if (f == null) { return null; }
		return StringUtils.toJavaString(f.toString());
	}

	public IExpression getExpr(final String key) {
		return getExpr(key, null);
	}

	public IExpression getExpr(final String... keys) {
		for (final String s : keys) {
			final IExpression expr = getExpr(s);
			if (expr != null) { return expr; }
		}
		return null;
	}

	public IExpression getExpr(final String key, final IExpression ifAbsent) {
		final IExpressionDescription f = get(key);
		if (f == null) { return ifAbsent; }
		return f.getExpression();
	}

	public IExpressionDescription putAsLabel(final String key, final String desc) {
		return put(key, LabelExpressionDescription.create(desc));
	}

	public IExpressionDescription put(final String key, final IExpression expr) {
		final IExpressionDescription result = get(key);
		if (result != null) {
			result.setExpression(expr);
			return result;
		}
		return add(key, new BasicExpressionDescription(expr));
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

	public IExpressionDescription put(final String key, final IExpressionDescription expr) {
		final IExpressionDescription existing = get(key);
		if (existing != null) {
			remove(key);
		}
		return add(key, expr);

	}

	public boolean equals(final String key, final String o) {
		final IExpressionDescription f = get(key);
		return f == null ? o == null : f.equalsString(o);
	}

	public void clear() {
		facets.clear();
	}

	/**
	 * @return
	 */
	public Facets cleanCopy() {
		final Facets result = new Facets();
		for (final Facet f : facets) {
			result.facets.add(f.cleanCopy());
		}
		return result;
	}

	/**
	 *
	 */
	public void dispose() {
		for (final Facet facet : facets) {
			if (facet.value != null) {
				facet.value.dispose();
			}
		}
		clear();
	}

	public void compact() {}

	public boolean forEachFacet(final BiConsumerWithPruning<String, IExpressionDescription> visitor) {
		for (final Facet f : facets) {
			if (!visitor.process(f.key, f.value)) { return false; }
		}
		return true;
	}

	public boolean isEmpty() {
		return facets.isEmpty();
	}

	public int size() {
		return facets.size();
	}

	public void transformValues(final Function<IExpressionDescription, IExpressionDescription> function) {
		facets.forEach(f -> f.value = function.apply(f.value));
	}

	public void putIfAbsent(final String key, final IExpressionDescription expr) {
		if (!containsKey(key)) {
			put(key, expr);
		}
	}

	public IExpression getExpr(final int index) {
		if (index > facets.size() || index < 0) { return null; }
		return facets.items().get(index).value.getExpression();
	}

	public String getFirstExistingAmong(final String... strings) {
		for (final String s : strings) {
			if (containsKey(s)) { return s; }
		}
		return null;
	}

	public IType<?> getTypeDenotedBy(final String key, final IDescription context) {
		return getTypeDenotedBy(key, context, Types.NO_TYPE);
	}

	public IType<?> getTypeDenotedBy(final String key, final IDescription context, final IType<?> noType) {
		final IExpressionDescription f = get(key);
		if (f == null) { return noType; }
		return f.getDenotedType(context);
	}

	public Collection<Facet> getFacets() {
		return facets;
	}
}
