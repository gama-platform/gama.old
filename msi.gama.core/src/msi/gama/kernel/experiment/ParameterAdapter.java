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

import java.util.List;
import java.util.Set;

import msi.gama.common.util.StringUtils;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

public abstract class ParameterAdapter implements IParameter.Batch {

	protected boolean editable;
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
	public String getUnitLabel(final IScope scope) {
		return unitLabel;
	}

	// @Override
	// public Integer getDefinitionOrder() {
	// return 0;
	// }

	@Override
	public void setValue(final IScope scope, final Object value) {
	}

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
	public String serialize(final boolean includingBuiltIn) {
		return StringUtils.toGaml(value(), includingBuiltIn);
	}

	@Override
	public Object getInitialValue(final IScope scope) {
		return value(scope);
	}

	@Override
	public Number getMinValue(final IScope scope) {
		return null;
	}

	@Override
	public Number getMaxValue(final IScope scope) {
		return null;
	}

	@Override
	public List getAmongValue(final IScope scope) {
		return null;
	}

	@Override
	public abstract Object value();

	@Override
	public void reinitRandomly(final IScope scope) {
	}

	//
	// @Override
	// public void tryToInit(IScope scope) {}

	@Override
	public Set<Object> neighborValues(final IScope scope) throws GamaRuntimeException {
		return null;
	}

	@Override
	public Number getStepValue(final IScope scope) {
		return null;
	}

	@Override
	public void setCategory(final String name) {
		category = name;
	}

	/**
	 * Method setUnitLabel()
	 * 
	 * @see msi.gama.kernel.experiment.IParameter#setUnitLabel(java.lang.String)
	 */
	@Override
	public void setUnitLabel(final String label) {
		unitLabel = label;
	}

	@Override
	public boolean isDefined() {
		return true;
	}

	@Override
	public void setDefined(final boolean defined) {
	}

	@Override
	public boolean acceptsSlider(final IScope scope) {
		return false;
	}

}
