/*********************************************************************************************
 *
 * 'NowUnitExpression.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.expressions;

import java.time.LocalDateTime;

import msi.gama.runtime.IScope;
import msi.gama.util.GamaDate;
import msi.gaml.types.Types;

public class NowUnitExpression extends UnitConstantExpression {

	public NowUnitExpression(final String name, final String doc) {
		super(1.0, Types.DATE, name, doc, null);
	}

	@Override
	public GamaDate value(final IScope scope) {
		return GamaDate.of(LocalDateTime.now());
	}

	@Override
	public boolean isConst() {
		return false;
	}

}
