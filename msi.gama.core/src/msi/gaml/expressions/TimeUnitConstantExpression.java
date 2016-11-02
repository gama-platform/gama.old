/**
 * Created by drogoul, 22 avr. 2014
 *
 */
package msi.gaml.expressions;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import msi.gaml.types.IType;

/**
 * Class UnitConstantExpression.
 *
 * @author drogoul
 * @since 22 avr. 2014
 *
 */
public class TimeUnitConstantExpression extends UnitConstantExpression {

	public static final Set<String> UNCOMPUTABLE_DURATIONS = ImmutableSet.of("month", "year", "months", "years", "y");

	final boolean isTimeDependent;

	public TimeUnitConstantExpression(final Object val, final IType<?> t, final String name, final String doc,
			final String[] names) {
		super(val, t, name, doc, names);
		isTimeDependent = UNCOMPUTABLE_DURATIONS.contains(name);
	}

	@Override
	public boolean isConst() {
		return !isTimeDependent;
	}

}
