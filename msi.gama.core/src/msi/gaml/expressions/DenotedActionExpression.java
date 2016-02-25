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

import java.util.Set;
import msi.gama.runtime.IScope;
import msi.gaml.descriptions.IDescription;
import msi.gaml.types.Types;

public class DenotedActionExpression extends VariableExpression {

	IDescription description;

	public DenotedActionExpression(final IDescription action) {
		super(action.getName(), Types.NO_TYPE, true, null);
		this.description = action;
	}

	@Override
	public Object value(final IScope scope) {
		return description;
	}

	@Override
	public String getTitle() {
		return description.getTitle();
	}

	/**
	 * @see msi.gaml.expressions.IExpression#getDocumentation()
	 */
	@Override
	public String getDocumentation() {
		return "This expression denotes  the description of " + getTitle();
	}

	@Override
	public void setVal(final IScope scope, final Object v, final boolean create) {}

	/**
	 * Method collectPlugins()
	 * @see msi.gaml.descriptions.IGamlDescription#collectPlugins(java.util.Set)
	 */
	@Override
	public void collectMetaInformation(final Set<String> plugins) {
		description.collectMetaInformation(plugins);
	}

}
