/*******************************************************************************************************
 *
 * msi.gaml.expressions.SelfExpression.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling
 * and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.expressions;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.runtime.IScope;
import msi.gama.util.ICollector;
import msi.gaml.descriptions.IVarDescriptionUser;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.descriptions.VariableDescription;
import msi.gaml.types.IType;

public class SelfExpression extends VariableExpression {

	protected SelfExpression(final IType<?> type) {
		super(IKeyword.SELF, type, true, null);
	}

	@Override
	public Object _value(final IScope scope) {
		return scope.getAgent();
	}

	@Override
	public String getTitle() {
		return "pseudo-variable self of type " + getGamlType().getTitle();
	}

	@Override
	public String getDocumentation() {
		return "Represents the current agent, instance of species " + type.getTitle();
	}

	@Override
	public void setVal(final IScope scope, final Object v, final boolean create) {}

	@Override
	public boolean isConst() {
		return false;
	}

	/**
	 * Method collectPlugins()
	 *
	 * @see msi.gama.common.interfaces.IGamlDescription#collectPlugins(java.util.Set)
	 */
	// @Override
	// public void collectMetaInformation(final GamlProperties meta) {}

	@Override
	public void collectUsedVarsOf(final SpeciesDescription species,
			final ICollector<IVarDescriptionUser> alreadyProcessed, final ICollector<VariableDescription> result) {
		if (alreadyProcessed.contains(this)) { return; }
		alreadyProcessed.add(this);
		// Added to fix a bug introduced in #2869: expressions containing `self` would not correctly initialize their
		// dependencies.
		result.add(species.getAttribute(IKeyword.LOCATION));
	}

}
