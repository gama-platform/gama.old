/*********************************************************************************************
 * 
 * 
 * 'Facets.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.statements;

import java.util.Map;
import java.util.Set;

import gnu.trove.function.TObjectFunction;
import gnu.trove.map.hash.THashMap;
import gnu.trove.procedure.TObjectObjectProcedure;
import gnu.trove.procedure.TObjectProcedure;
import msi.gama.common.interfaces.IGamlable;
import msi.gama.common.util.StringUtils;
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
 * Represents a Map of Facet objects. From there, text, tokens and values of
 * facets can be retrieved.
 * 
 */
public class Facets extends THashMap<String, IExpressionDescription> implements IGamlable {

	public static final Facets NULL = new Facets();

	public Facets(final String... strings) {
		if (strings != null) {
			setUp(strings.length / 2);
			int index = 0;
			if (!(strings.length % 2 == 0)) {
				index = 1;
			}
			for (; index < strings.length; index += 2) {
				put(strings[index], StringBasedExpressionDescription.create(strings[index + 1]));
			}
		}
	}

	public Facets(final Facets facets) {
		super(facets == null ? NULL : facets);
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
		final IExpressionDescription f = get(key);
		if (f == null) {
			return null;
		}
		return StringUtils.toJavaString(f.toString());
	}

	public IExpression getExpr(final String... keys) {
		final IExpressionDescription desc = getDescr(keys);
		return desc == null ? null : desc.getExpression();
	}

	public IExpressionDescription getDescr(final String... keys) {
		for (final String s : keys) {
			final IExpressionDescription f = get(s);
			if (f != null) {
				return f;
			}
		}
		return null;
	}

	public String getFirstExistingAmong(final String... keys) {
		for (final String s : keys) {
			if (containsKey(s)) {
				return s;
			}
		}
		return null;
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		final StringBuilder sb = new StringBuilder(size() * 20);
		for (final Map.Entry<String, IExpressionDescription> e : entrySet()) {
			if (e != null && e.getKey() != null) {
				final IExpressionDescription ed = e.getValue();
				final String exprString = ed == null ? "N/A" : ed.serialize(includingBuiltIn);
				sb.append(e.getKey()).append(": ").append(exprString).append(" ");
			}
		}
		return sb.toString();
	}

	public IType getTypeDenotedBy(final String key, final IDescription context) {
		return getTypeDenotedBy(key, context, Types.NO_TYPE);
	}

	public IType getTypeDenotedBy(final String key, final IDescription context, final IType noType) {
		final IExpressionDescription f = get(key);
		if (f == null) {
			return noType;
		}
		return f.getDenotedType(context);
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
	 * Adds the facet without performing any check, assuming it is not present
	 * in the map
	 * 
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
		final IExpressionDescription previous = get(key);
		if (previous != null) {
			if (previous.getTarget() != null && expr.getTarget() == null) {
				expr.setTarget(previous.getTarget());
			}
		}
		return super.put(key, expr);
	}

	public boolean equals(final String key, final String o) {
		final IExpressionDescription f = get(key);
		return f == null ? o == null : f.equalsString(o);
	}

	static TObjectFunction<IExpressionDescription, IExpressionDescription> cleanCopy = new TObjectFunction<IExpressionDescription, IExpressionDescription>() {

		@Override
		public IExpressionDescription execute(final IExpressionDescription value) {
			return value.cleanCopy();
		}
	};

	public Facets cleanCopy() {
		final Facets result = new Facets(this);
		result.transformValues(cleanCopy);
		result.compact();
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
