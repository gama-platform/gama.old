/*********************************************************************************************
 * 
 * 
 * 'VariableExpression.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.expressions;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.types.IType;

public abstract class VariableExpression extends AbstractExpression implements IVarExpression {

	protected final boolean isNotModifiable;
	private final IDescription definitionDescription;

	protected VariableExpression(final String n, final IType type, final boolean notModifiable,
		final IDescription definitionDescription) {
		setName(n);
		setType(type);
		isNotModifiable = notModifiable;
		this.definitionDescription = definitionDescription;
	}

	@Override
	public abstract Object value(final IScope scope) throws GamaRuntimeException;

	@Override
	public String serialize(boolean includingBuiltIn) {
		return getName();
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public boolean isNotModifiable() {
		return isNotModifiable;
	}

	@Override
	public boolean isConst() {
		return false;
	}

	public IDescription getDefinitionDescription() {
		return definitionDescription;
	}

	protected void setType(final IType type) {
		this.type = type;
	}

	@Override
	public String getTitle() {
		return isNotModifiable ? "constant" : "variable " + getName() + " of type " + getType() + " defined in " +
			getDefinitionDescription().getTitle();
	}

	@Override
	public boolean shouldBeParenthesized() {
		return false;
	}

}
