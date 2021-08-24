/*******************************************************************************************************
 *
 * msi.gaml.expressions.VariableExpression.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.expressions.variables;

import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.AbstractExpression;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.IVarExpression;
import msi.gaml.types.IType;

public abstract class VariableExpression extends AbstractExpression implements IVarExpression {

	protected final String name;
	protected final boolean isNotModifiable;
	private final IDescription enclosingDescription;

	protected VariableExpression(final String n, final IType<?> type, final boolean notModifiable,
			final IDescription definitionDescription) {
		name = n;
		setType(type);
		isNotModifiable = notModifiable;
		this.enclosingDescription = definitionDescription;
	}

	@Override
	public IExpression getOwner() {
		return null;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public VariableExpression getVar() {
		return this;
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
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
		// Consider all variables to be "not const" for the moment, so that they are not optimize
		// Only "species wide" constant should be optimized: for instance gridX and gridY are constant
		// for one agent, but not for the species. So an expression defined at the species level cannot
		// be optimized. Only global variables can be considered as 'const'
		// if (type.isContainer()) { return false; }
		// return isNotModifiable;
	}

	public IDescription getDefinitionDescription() {
		return enclosingDescription;
	}

	protected void setType(final IType<?> type) {
		this.type = type;
	}

	@Override
	public String getTitle() {

		return isNotModifiable ? "constant" : "variable " + getName() + " of type " + getGamlType()
				+ (enclosingDescription != null ? " defined in " + getDefinitionDescription().getTitle() : "");
	}

	@Override
	public boolean shouldBeParenthesized() {
		return false;
	}

	@Override
	public boolean isContextIndependant() {
		return false;
	}

}
