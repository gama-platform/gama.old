/*********************************************************************************************
 *
 *
 * 'InputParameter.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.gui.parameters;

import java.util.List;
import msi.gama.kernel.experiment.ParameterAdapter;
import msi.gama.runtime.IScope;
import msi.gaml.types.*;

public class InputParameter extends ParameterAdapter {

	private Object value;
	private final List among;
	private Number min, max;

	InputParameter(final String name, final Object value) {
		this(name, value, Types.get(value == null ? Object.class : (Class<Object>) value.getClass()));
	}

	public InputParameter(final String name, final Object value, final IType type) {
		this(name, value, type, null);
	}

	public InputParameter(final String name, final Object value, final IType type, final List among) {
		super(name, type.id());
		this.value = value;
		this.among = among;
	}

	InputParameter(final String name, final Object value, final Number min, final Number max) {
		this(name, value);
		this.min = min;
		this.max = max;
	}

	InputParameter(final String name, final String unit, final Object value, final Number min, final Number max) {
		this(name, value, min, max);
		unitLabel = unit;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void setValue(final IScope scope, final Object value) {
		this.value = value;
	}

	@Override
	public Number getMinValue() {
		return min;
	}

	@Override
	public Number getMaxValue() {
		return max;
	}

	@Override
	public List getAmongValue() {
		return among;
	}

	@Override
	public Object value() {
		return value;
	}

	@Override
	public Object value(final IScope scope) {
		return value;
	}

	@Override
	public boolean isEditable() {
		return true;
	}

}
