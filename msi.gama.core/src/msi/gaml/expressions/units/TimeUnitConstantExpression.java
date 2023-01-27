/*******************************************************************************************************
 *
 * TimeUnitConstantExpression.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.expressions.units;

import java.time.temporal.ChronoUnit;
import java.util.Set;

import msi.gama.util.GamaDate;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

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

	/**
	 * The Interface IFloatExpression.
	 */
	public interface ITimeFloatExpression extends IExpression {

		/**
		 * Gets the gaml type.
		 *
		 * @return the gaml type
		 */
		@Override
		default IType<?> getGamlType() { return Types.FLOAT; }
	}

	/** The Constant UNCOMPUTABLE_DURATIONS. */
	private static final Set<String> UNCOMPUTABLE_DURATIONS = Set.of("month", "year", "months", "years", "y");

	/** The Constant MONTH_EXPR. */
	private final static ITimeFloatExpression MONTH_EXPR =
			scope -> approximalQuery(scope.getClock().getCurrentDate(), ChronoUnit.MONTHS);

	/** The Constant YEAR_EXPR. */
	private final static ITimeFloatExpression YEAR_EXPR =
			scope -> approximalQuery(scope.getClock().getCurrentDate(), ChronoUnit.YEARS);

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
	public IExpression getExpression() { return isTimeDependent ? isMonth ? MONTH_EXPR : YEAR_EXPR : this; }

	@Override
	public boolean isConst() { return !isTimeDependent; }

}
