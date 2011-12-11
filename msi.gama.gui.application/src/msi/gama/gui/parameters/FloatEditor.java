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

import msi.gama.interfaces.*;
import msi.gama.internal.types.Types;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.util.Cast;
import org.eclipse.swt.widgets.Composite;

public class FloatEditor extends NumberEditor {

	FloatEditor(final IParameter param, final boolean canBeNull) {
		super(param, canBeNull);
	}

	FloatEditor(final IAgent agent, final IParameter param, final boolean canBeNull) {
		super(agent, param, null, canBeNull);
	}

	FloatEditor(final Composite parent, final String title, final Double value, final Double min,
		final Double max, final Double step, final boolean canBeNull,
		final EditorListener<Double> whenModified) {
		// Convenience method
		super(new SupportParameter(title, value, min, max, step), whenModified, canBeNull);
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
	protected Double normalizeValues() throws GamaRuntimeException {
		Double valueToConsider = originalValue == null ? 0.0 : Cast.asFloat(originalValue);
		currentValue = originalValue == null ? null : valueToConsider;
		minValue = minValue == null ? null : minValue.doubleValue();
		maxValue = maxValue == null ? null : maxValue.doubleValue();
		return valueToConsider;
	}

	@Override
	public IType getExpectedType() {
		return Types.get(IType.FLOAT);
	}

	@Override
	protected Double applyPlus() {
		if ( currentValue == null ) { return 0.0; }
		Double i = (Double) currentValue;
		Double newVal = i + stepValue.doubleValue();
		return newVal;
	}

	@Override
	protected Double applyMinus() {
		if ( currentValue == null ) { return 0.0; }
		Double i = (Double) currentValue;
		Double newVal = i - stepValue.doubleValue();
		return newVal;
	}

	@Override
	protected void checkButtons() {
		plus.setEnabled(maxValue == null || applyPlus() < maxValue.doubleValue());
		minus.setEnabled(minValue == null || applyMinus() > minValue.doubleValue());
	}

}
