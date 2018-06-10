/*********************************************************************************************
 *
 * 'IParameter.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
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
public interface IParameter extends IExperimentDisplayable {

	public static interface ParameterChangeListener {
		void changed(IScope scope, Object newValue);
	}

	public final String[] EMPTY_STRINGS = new String[0];

	// public abstract Integer getDefinitionOrder();

	public abstract void setValue(IScope scope, Object value);

	public abstract Object value(IScope scope) throws GamaRuntimeException;

	@SuppressWarnings ("rawtypes")
	public abstract IType getType();

	public String serialize(boolean includingBuiltIn);

	public abstract Object getInitialValue(IScope scope);

	public abstract Number getMinValue(IScope scope);

	public abstract Number getMaxValue(IScope scope);

	@SuppressWarnings ("rawtypes")
	public abstract List getAmongValue(IScope scope);

	public abstract boolean isEditable();

	public abstract boolean acceptsSlider(IScope scope);

	public abstract Number getStepValue(IScope scope);

	public boolean isDefined();

	public default String[] getEnablement() {
		return EMPTY_STRINGS;
	}

	public default String[] getDisablement() {
		return EMPTY_STRINGS;
	}

	public default void addChangeListener(final ParameterChangeListener listener) {
		// Nothing to do by default
	}

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
