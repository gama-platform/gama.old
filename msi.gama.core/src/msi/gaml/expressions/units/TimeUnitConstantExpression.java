/*******************************************************************************************************
 *
 * TimeUnitConstantExpression.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.expressions.units;

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

	/** The Constant UNCOMPUTABLE_DURATIONS. */
	public static final Set<String> UNCOMPUTABLE_DURATIONS = ImmutableSet.of("month", "year", "months", "years", "y");

	/** The is time dependent. */
	final boolean isTimeDependent;

	/**
	 * Instantiates a new time unit constant expression.
	 *
	 * @param val the val
	 * @param t the t
	 * @param name the name
	 * @param doc the doc
	 * @param names the names
	 */
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
