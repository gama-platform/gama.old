/*******************************************************************************************************
 *
 * AgentVariableExpression.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.expressions.variables;

import msi.gama.precompiler.GamlProperties;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.ICollector;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IVarDescriptionUser;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.descriptions.VariableDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.IVarExpression;
import msi.gaml.types.IType;

/**
 * The Class AgentVariableExpression.
 */
public class AgentVariableExpression extends VariableExpression implements IVarExpression.Agent {

	/**
	 * Instantiates a new agent variable expression.
	 *
	 * @param n
	 *            the n
	 * @param type
	 *            the type
	 * @param notModifiable
	 *            the not modifiable
	 * @param def
	 *            the def
	 */
	@SuppressWarnings ("rawtypes")
	public AgentVariableExpression(final String n, final IType type, final boolean notModifiable,
			final IDescription def) {
		super(n, type, notModifiable, def);
	}

	@Override
	public IExpression getOwner() {
		return new SelfExpression(this.getDefinitionDescription().getSpeciesContext().getGamlType());
	}

	@Override
	public Object _value(final IScope scope) throws GamaRuntimeException {
		return scope.getAgentVarValue(scope.getAgent(), getName());
	}

	@Override
	public void setVal(final IScope scope, final Object v, final boolean create) throws GamaRuntimeException {
		scope.setAgentVarValue(scope.getAgent(), getName(), v);
	}

	@Override
	public Doc getDocumentation() {
		final IDescription desc = getDefinitionDescription();
		if (desc == null) return new ConstantDoc("Type " + type.getTitle());
		Doc doc = new RegularDoc(new StringBuilder());
		final VariableDescription var = desc.getSpeciesContext().getAttribute(name);
		doc.append("Type ").append(type.getTitle()).append("<br/>");
		String builtInDoc = null;
		if (var != null) { builtInDoc = var.getBuiltInDoc(); }
		if (builtInDoc != null) { doc.append(builtInDoc).append("<br/>"); }
		doc.append(desc.isBuiltIn() ? "Built in " : builtInDoc == null ? "Defined in " : "Redefined in ")
				.append(desc.getTitle());
		return doc;
	}

	/**
	 * Method collectPlugins()
	 *
	 * @see msi.gama.common.interfaces.IGamlDescription#collectPlugins(java.util.Set)
	 */
	@Override
	public void collectMetaInformation(final GamlProperties meta) {
		if (getDefinitionDescription().isBuiltIn()) { meta.put(GamlProperties.ATTRIBUTES, getName()); }
	}

	@Override
	public void collectUsedVarsOf(final SpeciesDescription species,
			final ICollector<IVarDescriptionUser> alreadyProcessed, final ICollector<VariableDescription> result) {
		if (alreadyProcessed.contains(this)) return;
		alreadyProcessed.add(this);
		final SpeciesDescription sd = this.getDefinitionDescription().getSpeciesContext();
		if (species.equals(sd) || species.hasParent(sd)) { result.add(sd.getAttribute(getName())); }
	}

	@Override
	public IExpression resolveAgainst(final IScope scope) {
		return this;
	}

}
