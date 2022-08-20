/*******************************************************************************************************
 *
 * SelfExpression.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.expressions.variables;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.runtime.IScope;
import msi.gama.util.ICollector;
import msi.gaml.descriptions.IVarDescriptionUser;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.descriptions.VariableDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.IType;

/**
 * The Class SelfExpression.
 */
public class SelfExpression extends VariableExpression {

	/**
	 * Instantiates a new self expression.
	 *
	 * @param type
	 *            the type
	 */
	public SelfExpression(final IType<?> type) {
		super(IKeyword.SELF, type, true, null);
	}

	@Override
	public Object _value(final IScope scope) {
		return scope.getAgent();
	}

	@Override
	public String getTitle() { return "pseudo-variable self of type " + getGamlType().getTitle(); }

	@Override
	public String getDocumentation() { return "Represents the current agent, instance of species " + type.getTitle(); }

	@Override
	public void setVal(final IScope scope, final Object v, final boolean create) {}

	@Override
	public boolean isConst() { return false; }

	@Override
	public void collectUsedVarsOf(final SpeciesDescription species,
			final ICollector<IVarDescriptionUser> alreadyProcessed, final ICollector<VariableDescription> result) {
		if (alreadyProcessed.contains(this)) return;
		alreadyProcessed.add(this);
		// Added to fix a bug introduced in #2869: expressions containing `self` would not correctly initialize their
		// dependencies.
		result.add(species.getAttribute(IKeyword.LOCATION));
	}

	@Override
	public IExpression resolveAgainst(final IScope scope) {
		return this;
	}

}
