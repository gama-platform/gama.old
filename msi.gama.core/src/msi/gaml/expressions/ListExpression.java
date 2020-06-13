/*******************************************************************************************************
 *
 * msi.gaml.expressions.ListExpression.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling
 * and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.expressions;

import java.util.Arrays;
import java.util.function.Predicate;

import com.google.common.collect.Iterables;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.ICollector;
import msi.gama.util.IList;
import msi.gaml.descriptions.IVarDescriptionUser;
import msi.gaml.descriptions.OperatorProto;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.descriptions.VariableDescription;
import msi.gaml.types.GamaType;
import msi.gaml.types.Types;

/**
 * ListValueExpr.
 *
 * @author drogoul 23 août 07
 */
@SuppressWarnings ({ "rawtypes" })
public class ListExpression extends AbstractExpression implements IOperator {

	public static IExpression create(final Iterable<? extends IExpression> elements) {
		final ListExpression u = new ListExpression(elements);

		// if (u.isConst() && GamaPreferences.CONSTANT_OPTIMIZATION.getValue())
		// {
		// final IExpression e =
		// GAML.getExpressionFactory().createConst(u.getConstValue(), u.getType(),
		// u.serialize(false));
		// // DEBUG.LOG(" ==== Simplification of " + u.toGaml() + "
		// // into " + e.toGaml());
		// return e;
		// }
		return u;
	}

	public static IExpression create(final IExpression... elements) {
		return new ListExpression(elements);
	}

	final IExpression[] elements;

	// private final Object[] values;
	// private boolean isConst;
	// private boolean computed;

	ListExpression(final IExpression... elements) {
		this.elements = elements;
		type = Types.LIST.of(GamaType.findCommonType(this.elements, GamaType.TYPE));
	}

	ListExpression(final Iterable<? extends IExpression> elements) {
		this(Iterables.toArray(elements, IExpression.class));
	}

	public IExpression[] getElements() {
		return elements;
	}

	public boolean containsValue(final Object o) {
		if (o == null) { return false; }
		for (final IExpression exp : elements) {
			if (exp == null) {
				continue;
			}
			if (!exp.isConst()) { return false; }
			final Object e = exp.getConstValue();
			if (o.equals(e)) { return true; }
		}
		return false;
	}

	@Override
	public IExpression resolveAgainst(final IScope scope) {
		final ListExpression copy = new ListExpression(Arrays.asList(elements));
		for (int i = 0; i < elements.length; i++) {
			final IExpression exp = elements[i];
			if (exp != null) {
				copy.elements[i] = exp.resolveAgainst(scope);
			}
		}
		return copy;
	}

	@Override
	public IList _value(final IScope scope) throws GamaRuntimeException {
		final IList<Object> result = GamaListFactory.create(getGamlType().getContentType());
		for (final IExpression exp : elements) {
			if (exp != null) {
				result.add(exp.value(scope));
			}
		}
		return result;
		// if ( isConst && computed ) { return
		// GamaListFactory.createWithoutCasting(getType().getContentType(),
		// values); }
		// final Object[] values = new Object[elements.length];
		// for (int i = 0; i < elements.length; i++) {
		// if (elements[i] == null) {
		// // computed = false;
		// return GamaListFactory.create();
		// }
		// values[i] = elements[i].value(scope);
		// }
		// // computed = true;
		// // Important NOT to return the reference to values (but a copy of it).
		// return GamaListFactory.createWithoutCasting(getGamlType().getContentType(), values);
	}

	@Override
	public String toString() {
		return Arrays.toString(elements);
	}

	@Override
	public boolean isConst() {
		return false;

		// for ( final IExpression e : elements ) {
		// // indicates a former problem in the compilation of the expression
		// if ( e == null ) { return false; }
		// if ( !e.isConst() ) { return false; }
		// }
		// isConst = true;
		// return true;
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		final StringBuilder sb = new StringBuilder();
		surround(sb, '[', ']', elements);
		return sb.toString();
	}

	@Override
	public String getTitle() {
		return "literal list of type " + getGamlType().getTitle();
	}

	@Override
	public String getDocumentation() {
		return "Constant " + isConst() + "<br>Contains elements of type " + type.getContentType().getTitle();
	}

	/**
	 * @return
	 */
	public boolean isEmpty() {
		return elements.length == 0;
	}

	/**
	 * Method collectPlugins()
	 *
	 * @see msi.gama.common.interfaces.IGamlDescription#collectPlugins(java.util.Set)
	 */
	// @Override
	// public void collectMetaInformation(final GamlProperties meta) {
	// for (final IExpression e : elements) {
	// if (e != null) {
	// e.collectMetaInformation(meta);
	// }
	// }
	// }

	@Override
	public void collectUsedVarsOf(final SpeciesDescription species,
			final ICollector<IVarDescriptionUser> alreadyProcessed, final ICollector<VariableDescription> result) {
		if (alreadyProcessed.contains(this)) { return; }
		alreadyProcessed.add(this);
		for (final IExpression e : elements) {
			if (e != null) {
				e.collectUsedVarsOf(species, alreadyProcessed, result);
			}
		}

	}

	@Override
	public boolean isContextIndependant() {
		for (final IExpression e : elements) {
			if (e != null) {
				if (!e.isContextIndependant()) { return false; }
			}
		}
		return true;
	}

	@Override
	public void visitSuboperators(final IOperatorVisitor visitor) {
		for (final IExpression e : elements) {
			if (e instanceof IOperator) {
				visitor.visit((IOperator) e);
			}
		}

	}

	@Override
	public IExpression arg(final int i) {
		if (i < 0 || i > elements.length) { return null; }
		return elements[i];
	}

	@Override
	public OperatorProto getPrototype() {
		return null;
	}

	@Override
	public boolean findAny(final Predicate<IExpression> predicate) {
		if (predicate.test(this)) { return true; }
		if (elements != null) {
			for (final IExpression e : elements) {
				if (e.findAny(predicate)) { return true; }
			}
		}
		return false;
	}

}
