/*******************************************************************************************************
 *
 * msi.gaml.descriptions.ConstantExpressionDescription.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.descriptions;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.eclipse.emf.ecore.EObject;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import msi.gaml.expressions.ConstantExpression;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.GamaType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

public class ConstantExpressionDescription extends ConstantExpression implements IExpressionDescription {

	final static Cache<Object, IExpressionDescription> CACHE = CacheBuilder.newBuilder().maximumSize(1000).build();
	public final static ConstantExpressionDescription NULL_EXPR_DESCRIPTION = new ConstantExpressionDescription(null);
	public final static ConstantExpressionDescription TRUE_EXPR_DESCRIPTION = new ConstantExpressionDescription(true);
	public final static ConstantExpressionDescription FALSE_EXPR_DESCRIPTION = new ConstantExpressionDescription(false);

	public static IExpressionDescription create(final Object object) {
		if (object == null) { return NULL_EXPR_DESCRIPTION; }
		try {
			return CACHE.get(object, () -> new ConstantExpressionDescription(object));
		} catch (final ExecutionException e) {
			return null;
		}
	}

	public static IExpressionDescription create(final Integer i) {
		try {
			return CACHE.get(i, () -> new ConstantExpressionDescription(i, Types.INT));
		} catch (final ExecutionException e) {
			return null;
		}

	}

	public static IExpressionDescription create(final Double d) {
		try {
			return CACHE.get(d, () -> new ConstantExpressionDescription(d, Types.FLOAT));
		} catch (final ExecutionException e) {
			return null;
		}

	}

	public static IExpressionDescription create(final Boolean b) {
		return b ? TRUE_EXPR_DESCRIPTION : FALSE_EXPR_DESCRIPTION;
	}

	private ConstantExpressionDescription(final Object object) {
		this(object, GamaType.of(object));
	}

	private ConstantExpressionDescription(final Object object, final IType<?> t) {
		super(object, t);
	}

	@Override
	public boolean isConst() {
		return true;
	}

	@Override
	public void dispose() {}

	@Override
	public IExpression compile(final IDescription context) {
		return this;
	}

	@Override
	public void setExpression(final IExpression expr) {}

	@Override
	public IExpressionDescription cleanCopy() {
		return this;
	}

	@Override
	public IType<?> getDenotedType(final IDescription context) {
		return context.getTypeNamed(literalValue());
	}

	@Override
	public IExpression getExpression() {
		return this;
	}

	@Override
	public IExpressionDescription compileAsLabel() {
		return LabelExpressionDescription.create(literalValue());
	}

	@Override
	public boolean equalsString(final String o) {
		return literalValue().equals(o);
	}

	@Override
	public EObject getTarget() {
		return null;
	}

	@Override
	public void setTarget(final EObject target) {}

	@Override
	public Set<String> getStrings(final IDescription context, final boolean skills) {
		return Collections.EMPTY_SET;
	}

	@Override
	public IType<?> getGamlType() {
		return type;
	}

}
