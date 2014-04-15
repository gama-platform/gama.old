/*********************************************************************************************
 * 
 *
 * 'IntEditor.java', in plugin 'msi.gama.application', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.parameters;

// TODO Passer le FloatEditor et le IntEditor au m�me layout.

import msi.gama.common.interfaces.EditorListener;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.operators.Cast;
import msi.gaml.types.*;
import org.eclipse.swt.widgets.Composite;

public class IntEditor extends NumberEditor {

	IntEditor(final IAgent agent, final IParameter param, final boolean canBeNull) {
		this(agent, param, canBeNull, null);
	}

	IntEditor(final IAgent agent, final IParameter param, final boolean canBeNull, final EditorListener l) {
		super(agent, param, l, canBeNull);
	}

	IntEditor(final Composite parent, final String title, final String unit, final Integer value, final Integer min,
		final Integer max, final Integer step, final EditorListener<Integer> whenModified, final boolean canBeNull) {
		super(new InputParameter(title, unit, value, min, max), whenModified, canBeNull);
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
	protected void modifyValue(final Object val) throws GamaRuntimeException {
		Integer i = Cast.as(val, Integer.class);
		if ( minValue != null && i < minValue.intValue() ) { throw GamaRuntimeException.error("Value " + i +
			" should be greater than " + minValue); }
		if ( maxValue != null && i > maxValue.intValue() ) { throw GamaRuntimeException.error("Value " + i +
			" should be smaller than " + maxValue); }
		currentValue = i;
		titleLabel.setBackground(isValueModified() ? changed_bg : normal_bg);
		if ( !internalModification ) {
			setParameterValue(i);
		}
	}

	@Override
	protected void checkButtons() {
		plus.setEnabled(maxValue == null || applyPlus() < maxValue.intValue());
		minus.setEnabled(minValue == null || applyMinus() > minValue.intValue());
	}

	@Override
	protected Integer normalizeValues() throws GamaRuntimeException {
		Integer valueToConsider = getOriginalValue() == null ? 0 : Cast.as(getOriginalValue(), Integer.class);
		currentValue = getOriginalValue() == null ? null : valueToConsider;
		minValue = minValue == null ? null : minValue.intValue();
		maxValue = maxValue == null ? null : maxValue.intValue();
		return valueToConsider;
	}

	@Override
	public IType getExpectedType() {
		return Types.get(IType.INT);
	}

}
