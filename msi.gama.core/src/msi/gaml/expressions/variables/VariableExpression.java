/*******************************************************************************************************
 *
 * VariableExpression.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
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

/**
 * The Class VariableExpression.
 */
public abstract class VariableExpression extends AbstractExpression implements IVarExpression {

	/** The name. */
	protected final String name;
	
	/** The is not modifiable. */
	protected final boolean isNotModifiable;
	
	/** The enclosing description. */
	private final IDescription enclosingDescription;

	/**
	 * Instantiates a new variable expression.
	 *
	 * @param n the n
	 * @param type the type
	 * @param notModifiable the not modifiable
	 * @param definitionDescription the definition description
	 */
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

	/**
	 * Gets the definition description.
	 *
	 * @return the definition description
	 */
	public IDescription getDefinitionDescription() {
		return enclosingDescription;
	}

	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
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
