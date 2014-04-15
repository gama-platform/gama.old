/*********************************************************************************************
 * 
 *
 * 'ParameterAdapter.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.kernel.experiment;

import java.util.*;
import msi.gama.common.util.StringUtils;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.*;

public abstract class ParameterAdapter implements IParameter.Batch {

	protected boolean editable;
	// protected boolean tooltip;
	// protected boolean label;
	protected String title;
	protected final IType type;
	protected String category;
	protected String unitLabel;

	public ParameterAdapter(final String title, final int type) {
		this.title = title;
		this.type = Types.get(type);
	}

	public ParameterAdapter(final String title, final String category, final int type) {
		this(title, type);
		this.category = category;
	}

	public ParameterAdapter(final String title, final String category, final String unit, final int type) {
		this(title, category, type);
		this.unitLabel = unit;
	}

	// @Override
	public String serializeToGaml() {
		// GamlSerializer gs = new GamlSerializer();
		return "";
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String getName() {
		return getTitle();
	}

	@Override
	public boolean isEditable() {
		return false;
	}

	//
	// @Override
	// public boolean isLabel() {
	// return false;
	// }

	@Override
	public boolean canBeExplored() {
		return false;
	}

	@Override
	public void setEditable(final boolean editable) {
		this.editable = editable;
	}

	@Override
	public String getCategory() {
		return category;
	}

	@Override
	public String getUnitLabel() {
		return unitLabel;
	}

	@Override
	public Integer getDefinitionOrder() {
		return 0;
	}

	@Override
	public void setValue(final IScope scope, final Object value) {}

	@Override
	public Object value(final IScope iScope) throws GamaRuntimeException {
		return value();
	}

	@Override
	public IType getType() {
		return type;
	}

	// @Override
	// public IType getContentType() {
	// return Types.NO_TYPE;
	// }

	@Override
	public String toGaml() {
		return StringUtils.toGaml(value());
	}

	@Override
	public Object getInitialValue(final IScope scope) {
		return value();
	}

	@Override
	public Number getMinValue() {
		return null;
	}

	@Override
	public Number getMaxValue() {
		return null;
	}

	@Override
	public List getAmongValue() {
		return null;
	}

	@Override
	public abstract Object value();

	@Override
	public void reinitRandomly(final IScope scope) {}

	//
	// @Override
	// public void tryToInit(IScope scope) {}

	@Override
	public Set<Object> neighbourValues() throws GamaRuntimeException {
		return null;
	}

	@Override
	public Number getStepValue() {
		return null;
	}

	@Override
	public void setCategory(final String name) {
		category = name;
	}

	/**
	 * Method setUnitLabel()
	 * @see msi.gama.kernel.experiment.IParameter#setUnitLabel(java.lang.String)
	 */
	@Override
	public void setUnitLabel(final String label) {
		unitLabel = label;
	}

}
