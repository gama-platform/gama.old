/*********************************************************************************************
 * 
 * 
 * 'FloatEditor.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.parameters;

import msi.gama.common.interfaces.EditorListener;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.operators.Cast;
import msi.gaml.types.*;
import org.eclipse.swt.widgets.*;

public class FloatEditor extends NumberEditor<Double> {

	FloatEditor(final IParameter param, final boolean canBeNull) {
		super(param, canBeNull);
	}

	FloatEditor(final IAgent agent, final IParameter param, final boolean canBeNull) {
		this(agent, param, canBeNull, null);
	}

	FloatEditor(final IAgent agent, final IParameter param, final boolean canBeNull, final EditorListener l) {
		super(agent, param, l, canBeNull);
	}

	FloatEditor(final Composite parent, final String title, final Double value, final Double min, final Double max,
		final Double step, final boolean canBeNull, final EditorListener<Double> whenModified) {
		// Convenience method
		super(new InputParameter(title, value, min, max), whenModified, canBeNull);
		this.createComposite(parent);
	}

	@Override
	protected void computeStepValue() {
		stepValue = param.getStepValue();
		if ( stepValue == null ) {
			stepValue = 0.1;
		}
	}

	@Override
	protected void modifyValue(final Double val) throws GamaRuntimeException {
		Double i = Cast.as(val, Double.class, false);
		if ( acceptNull && val == null ) {
			i = null;
		} else {
			if ( minValue != null && i < minValue.doubleValue() ) { throw GamaRuntimeException.error("Value " + i +
				" should be greater than " + minValue); }
			if ( maxValue != null && i > maxValue.doubleValue() ) { throw GamaRuntimeException.error("Value " + i +
				" should be smaller than " + maxValue); }
		}
		super.modifyValue(i);
	}

	// @Override
	// protected void setOriginalValue(final Double val) {
	// // if ( acceptNull && val == null ) {
	// // super.setOriginalValue(val);
	// // }
	// super.setOriginalValue(val);
	// }

	@Override
	protected Double normalizeValues() throws GamaRuntimeException {
		Double valueToConsider = getOriginalValue() == null ? 0.0 : Cast.as(getOriginalValue(), Double.class, false);
		currentValue = getOriginalValue() == null ? null : valueToConsider;
		minValue = minValue == null ? null : minValue.doubleValue();
		maxValue = maxValue == null ? null : maxValue.doubleValue();
		return valueToConsider;
	}

	@Override
	public IType getExpectedType() {
		return Types.FLOAT;
	}

	@Override
	protected Double applyPlus() {
		if ( currentValue == null ) { return 0.0; }
		Double i = currentValue;
		Double newVal = i + stepValue.doubleValue();
		return newVal;
	}

	@Override
	protected Double applyMinus() {
		if ( currentValue == null ) { return 0.0; }
		Double i = currentValue;
		Double newVal = i - stepValue.doubleValue();
		return newVal;
	}

	@Override
	protected void checkButtons() {
		super.checkButtons();
		ToolItem plus = items[PLUS];
		if ( plus != null && !plus.isDisposed() ) {
			plus.setEnabled(param.isDefined() && (maxValue == null || applyPlus() < maxValue.doubleValue()));
		}
		ToolItem minus = items[MINUS];
		if ( minus != null && !minus.isDisposed() ) {
			minus.setEnabled(param.isDefined() && (minValue == null || applyMinus() > minValue.doubleValue()));
		}
	}

}
