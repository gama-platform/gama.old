/*******************************************************************************************************
 *
 * msi.gama.kernel.experiment.ParameterWrapper.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.kernel.experiment;

import java.util.List;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.IType;

/**
 * Class ParameterWrapper. Wraps entirely another parameter, allowing to
 * redefine some of its methods in anonymous subclasses
 * 
 * @author drogoul
 * @since 2 sept. 2013
 * 
 */
public abstract class ParameterWrapper implements IParameter {

	final IParameter wrapped;

	public ParameterWrapper(final IParameter wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public String getName() {
		return wrapped.getName();
	}

	@Override
	public String getTitle() {
		return wrapped.getTitle();
	}

	@Override
	public String getCategory() {
		return wrapped.getCategory();
	}

	@Override
	public String getUnitLabel(final IScope scope) {
		return wrapped.getUnitLabel(scope);
	}

	@Override
	public void setUnitLabel(final String label) {
		wrapped.setUnitLabel(label);
	}
	//
	// @Override
	// public Integer getDefinitionOrder() {
	// return wrapped.getDefinitionOrder();
	// }

	@Override
	public void setValue(final IScope scope, final Object value) {
		wrapped.setValue(scope, value);
	}

	@Override
	public Object value(final IScope scope) throws GamaRuntimeException {
		return wrapped.value(scope);
	}

	@Override
	public IType getType() {
		return wrapped.getType();
	}

	//
	// @Override
	// public IType getContentType() {
	// return wrapped.getContentType();
	// }

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return wrapped.serialize(includingBuiltIn);
	}

	@Override
	public Object getInitialValue(final IScope scope) {
		return wrapped.getInitialValue(scope);
	}

	@Override
	public Number getMinValue(final IScope scope) {
		return wrapped.getMinValue(scope);
	}

	@Override
	public Number getMaxValue(final IScope scope) {
		return wrapped.getMaxValue(scope);
	}

	@Override
	public List getAmongValue(final IScope scope) {
		return wrapped.getAmongValue(scope);
	}

	@Override
	public boolean isEditable() {
		return wrapped.isEditable();
	}

	@Override
	public Number getStepValue(final IScope scope) {
		return wrapped.getStepValue(scope);
	}

	@Override
	public boolean isDefined() {
		return wrapped.isDefined();
	}

	@Override
	public void setDefined(final boolean defined) {
		wrapped.setDefined(defined);
	}

}
