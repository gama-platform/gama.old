/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.gui.parameters;

// TODO Passer le FloatEditor et le IntEditor au même layout.

import msi.gama.interfaces.*;
import msi.gama.internal.types.Types;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.util.Cast;
import org.eclipse.swt.widgets.Composite;

public class IntEditor extends NumberEditor {

	IntEditor(final IAgent agent, final IParameter param, final boolean canBeNull) {
		super(agent, param, null, canBeNull);
	}

	IntEditor(final Composite parent, final String title, final String unit, final Integer value,
		final Integer min, final Integer max, final Integer step,
		final EditorListener<Integer> whenModified, final boolean canBeNull) {
		super(new SupportParameter(title, unit, value, min, max, step), whenModified, canBeNull);
		createComposite(parent);
	}

	@Override
	protected void computeStepValue() {
		stepValue = param.getStepValue();
		if ( stepValue == null ) {
			stepValue = 1;
		}
	}

	@Override
	protected Integer applyPlus() {
		if ( currentValue == null ) { return 0; }
		Integer i = (Integer) currentValue;
		Integer newVal = i + stepValue.intValue();
		return newVal;
	}

	@Override
	protected Integer applyMinus() {
		if ( currentValue == null ) { return 0; }
		Integer i = (Integer) currentValue;
		Integer newVal = i - stepValue.intValue();
		return newVal;
	}

	@Override
	protected void checkButtons() {
		plus.setEnabled(maxValue == null || applyPlus() < maxValue.intValue());
		minus.setEnabled(minValue == null || applyMinus() > minValue.intValue());
	}

	@Override
	protected Integer normalizeValues() throws GamaRuntimeException {
		Integer valueToConsider = originalValue == null ? 0 : Cast.asInt(originalValue);
		currentValue = originalValue == null ? null : valueToConsider;
		minValue = minValue == null ? null : minValue.intValue();
		maxValue = maxValue == null ? null : maxValue.intValue();
		return valueToConsider;
	}

	@Override
	public IType getExpectedType() {
		return Types.get(IType.INT);
	}

}
