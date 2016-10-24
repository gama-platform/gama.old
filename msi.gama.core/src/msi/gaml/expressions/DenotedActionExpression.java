/*********************************************************************************************
 *
 *
 * DenotedActionExpression.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.expressions;

import msi.gama.precompiler.GamlProperties;
import msi.gama.runtime.IScope;
import msi.gama.util.ICollector;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.VariableDescription;
import msi.gaml.types.Types;

public class DenotedActionExpression extends VariableExpression {

	public DenotedActionExpression(final IDescription action) {
		super(action.getName(), Types.NO_TYPE, true, action);
	}

	@Override
	public Object value(final IScope scope) {
		return getDefinitionDescription();
	}

	@Override
	public String getTitle() {
		return getDefinitionDescription().getTitle();
	}

	/**
	 * @see msi.gaml.expressions.IExpression#getDocumentation()
	 */
	@Override
	public String getDocumentation() {
		return "This expression denotes  the description of " + getTitle();
	}

	@Override
	public void setVal(final IScope scope, final Object v, final boolean create) {
	}

	/**
	 * Method collectPlugins()
	 * 
	 * @see msi.gama.common.interfaces.IGamlDescription#collectPlugins(java.util.Set)
	 */
	@Override
	public void collectMetaInformation(final GamlProperties meta) {
		getDefinitionDescription().collectMetaInformation(meta);
	}

	@Override
	public void collectUsedVarsOf(final IDescription species, final ICollector<VariableDescription> result) {
	}

}
