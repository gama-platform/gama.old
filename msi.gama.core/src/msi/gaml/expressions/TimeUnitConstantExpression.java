/**
 * Created by drogoul, 22 avr. 2014
 *
 */
package msi.gaml.expressions;

import msi.gaml.types.IType;

/**
 * Class UnitConstantExpression.
 *
 * @author drogoul
 * @since 22 avr. 2014
 *
 */
public class TimeUnitConstantExpression extends UnitConstantExpression {

	public TimeUnitConstantExpression(final Object val, final IType<?> t, final String name, final String doc,
			final String[] names) {
		super(val, t, name, doc, names);
	}

}
