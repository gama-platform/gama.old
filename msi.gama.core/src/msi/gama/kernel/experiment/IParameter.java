/*********************************************************************************************
 * 
 * 
 * 'IParameter.java', in plugin 'msi.gama.core', is part of the source code of the
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

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 4 juin 2010
 * 
 * @todo Description
 * 
 */
public interface IParameter {

	public abstract String getName();

	public abstract String getTitle();

	public abstract String getCategory();

	public abstract String getUnitLabel(IScope scope);

	public void setUnitLabel(String label);

	// public abstract Integer getDefinitionOrder();

	public abstract void setValue(IScope scope, Object value);

	public abstract Object value(IScope scope) throws GamaRuntimeException;

	public abstract IType getType();

	public String serialize(boolean includingBuiltIn);

	public abstract Object getInitialValue(IScope scope);

	public abstract Number getMinValue(IScope scope);

	public abstract Number getMaxValue(IScope scope);

	public abstract List getAmongValue(IScope scope);

	public abstract boolean isEditable();

	public abstract boolean acceptsSlider(IScope scope);

	public abstract Number getStepValue(IScope scope);

	public boolean isDefined();

	public interface Batch extends IParameter {

		public Object value();

		public void setCategory(String cat);

		public void reinitRandomly(IScope scope);

		public abstract Set<Object> neighborValues(IScope scope) throws GamaRuntimeException;

		public void setEditable(boolean b);

		public boolean canBeExplored();

	}

	/**
	 * @param b
	 */
	public abstract void setDefined(boolean b);

}
