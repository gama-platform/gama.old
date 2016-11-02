/*********************************************************************************************
 *
 * 'TimeUnitConstantExpression.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
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
