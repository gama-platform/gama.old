/*********************************************************************************************
 * 
 *
 * 'IParameterEditor.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.common.interfaces;

import msi.gama.kernel.experiment.IParameter;
import msi.gaml.types.IType;

/**
 * The class IParameterEditor.
 * 
 * @author drogoul
 * @since 18 d�c. 2011
 * 
 */
public interface IParameterEditor {

	public abstract IType getExpectedType();

	public abstract boolean isValueModified();

	public abstract void revertToDefaultValue();

	public abstract IParameter getParam();

	public abstract void updateValue();

	public abstract void setActive(Boolean value);

}