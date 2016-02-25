/*********************************************************************************************
 *
 *
 * 'TempVariableExpression.java', in plugin 'msi.gama.core', is part of the source code of the
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
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GAML;
import msi.gaml.descriptions.IDescription;
import msi.gaml.types.IType;

public class TempVariableExpression extends VariableExpression {

	protected TempVariableExpression(final String n, final IType type, final IDescription definitionDescription) {
		super(n, type, false, definitionDescription);
	}

	@Override
	public Object value(final IScope scope) {
		return scope.getVarValue(getName());
	}

	@Override
	public void setVal(final IScope scope, final Object v, final boolean create) throws GamaRuntimeException {
		Object val = type.cast(scope, v, null, false);
		if ( create ) {
			scope.addVarWithValue(getName(), val);
		} else {
			scope.setVarValue(getName(), val);
		}
	}

	@Override
	public String getTitle() {
		return "temporary variable " + getName() + " of type " + getType().getTitle();
	}

	/**
	 * @see msi.gaml.expressions.IExpression#getDocumentation()
	 */
	@Override
	public String getDocumentation() {
		IDescription desc = getDefinitionDescription();
		return "temporary variable " + getName() + " of type " + getType().getTitle() +
			(desc == null ? "<br>Built In" : "<br>Defined in " + desc.getTitle());
	}

	@Override
	public IExpression resolveAgainst(final IScope scope) {
		return GAML.getExpressionFactory().createConst(value(scope), type, name);
	}

	/**
	 * Method collectPlugins()
	 * @see msi.gaml.descriptions.IGamlDescription#collectPlugins(java.util.Set)
	 */
	@Override
	public void collectMetaInformation(final GamlProperties meta) {}
}
