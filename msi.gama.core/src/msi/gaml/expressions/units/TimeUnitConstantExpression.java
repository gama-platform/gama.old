/*******************************************************************************************************
 *
 * TimeUnitConstantExpression.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.expressions.units;

import java.time.temporal.ChronoUnit;
import java.util.Set;

import msi.gama.runtime.IScope;
import msi.gama.util.GamaDate;
import msi.gaml.types.IType;

/**
 * Class UnitConstantExpression.
 *
 * @author drogoul
 * @since 22 avr. 2014
 *
 */
public class TimeUnitConstantExpression extends UnitConstantExpression {

	/**
	 * Approximal query.
	 *
	 * @param date
	 *            the date
	 * @param right
	 *            the right
	 * @return the double
	 */
	public static double approximalQuery(final GamaDate date, final ChronoUnit right) {
		return date.until(date.plus(1l, right), ChronoUnit.SECONDS);
	}

	/** The Constant UNCOMPUTABLE_DURATIONS. */
	private static final Set<String> UNCOMPUTABLE_DURATIONS = Set.of("month", "year", "months", "years", "y");

	/** The is time dependent. */
	final boolean isTimeDependent, isMonth;

	/**
	 * Instantiates a new time unit constant expression.
	 *
	 * @param val
	 *            the val
	 * @param t
	 *            the t
	 * @param name
	 *            the name
	 * @param doc
	 *            the doc
	 * @param names
	 *            the names
	 */
	public TimeUnitConstantExpression(final Object val, final IType<?> t, final String name, final String doc,
			final String[] names) {
		super(val, t, name, doc, names);
		isTimeDependent = UNCOMPUTABLE_DURATIONS.contains(name);
		isMonth = name.startsWith("m");
	}

	@Override
	public Object _value(final IScope scope) {
		return isTimeDependent
				? approximalQuery(scope.getClock().getCurrentDate(), isMonth ? ChronoUnit.MONTHS : ChronoUnit.YEARS)
				: super._value(scope);
	}

	@Override
	public boolean isConst() { return !isTimeDependent; }

	@Override
	public boolean isAllowedInParameters() { return !isTimeDependent; }

}
