/*******************************************************************************************************
 *
 * GlobalVariableExpression.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.expressions.variables;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.kernel.experiment.ITopLevelAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.ICollector;
import msi.gaml.compilation.GAML;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IVarDescriptionUser;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.descriptions.VariableDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.IVarExpression;
import msi.gaml.types.IType;

/**
 * The Class GlobalVariableExpression.
 */
public class GlobalVariableExpression extends VariableExpression implements IVarExpression.Agent {

	/**
	 * Creates the.
	 *
	 * @param n
	 *            the n
	 * @param type
	 *            the type
	 * @param notModifiable
	 *            the not modifiable
	 * @param world
	 *            the world
	 * @return the i expression
	 */
	public static IExpression create(final String n, final IType<?> type, final boolean notModifiable,
			final IDescription world) {
		final VariableDescription v = ((SpeciesDescription) world).getAttribute(n);
		final IExpression exp = v.getFacetExpr(IKeyword.INIT);
		if (exp != null) {
			// AD Addition of a test on whether the variable is a function or not
			final boolean isConst = notModifiable && exp.isConst() && !v.isFunction();
			if (isConst && GamaPreferences.External.CONSTANT_OPTIMIZATION.getValue())
				return GAML.getExpressionFactory().createConst(exp.getConstValue(), type, n);
		}
		return new GlobalVariableExpression(n, type, notModifiable, world);
	}

	/**
	 * Instantiates a new global variable expression.
	 *
	 * @param n
	 *            the n
	 * @param type
	 *            the type
	 * @param notModifiable
	 *            the not modifiable
	 * @param world
	 *            the world
	 */
	protected GlobalVariableExpression(final String n, final IType<?> type, final boolean notModifiable,
			final IDescription world) {
		super(n, type, notModifiable, world);
	}

	@Override
	public boolean isConst() {
		// Allow global variables to report that they are constant if they are noted so (except if they are containers).
		if (type.isContainer()) return false;
		VariableDescription vd = getDefinitionDescription().getSpeciesContext().getAttribute(name);
		if (vd == null || vd.isFunction()) return false;
		return isNotModifiable;
	}

	@Override
	public IExpression getOwner() {
		return this.getDefinitionDescription().getModelDescription().getVarExpr(IKeyword.WORLD_AGENT_NAME, false);
	}

	@Override
	public Object _value(final IScope scope) throws GamaRuntimeException {
		final String name = getName();
		// We first try in the 'normal' scope (so that regular global vars are still accessed by agents of micro-models,
		// see #2238)
		if (scope.hasAccessToGlobalVar(name)) return scope.getGlobalVarValue(name);
		final IAgent microAgent = scope.getAgent();
		if (microAgent != null) {
			final IScope agentScope = microAgent.getScope();
			if (agentScope != null) {
				final ITopLevelAgent root = agentScope.getRoot();
				if (root != null) {
					final IScope globalScope = root.getScope();
					if (globalScope != null) return globalScope.getGlobalVarValue(getName());
				}
			}
		}

		return null;
	}

	@Override
	public void setVal(final IScope scope, final Object v, final boolean create) throws GamaRuntimeException {
		if (isNotModifiable) return;
		if (scope.hasAccessToGlobalVar(name)) {
			scope.setGlobalVarValue(name, v);
		} else {
			final IAgent sc = scope.getAgent();
			if (sc != null) { sc.getScope().getRoot().getScope().setGlobalVarValue(name, v); }
		}
	}

	@Override
	public String getTitle() {
		final IDescription desc = getDefinitionDescription();
		boolean isParameter;
		if (desc != null) {
			VariableDescription vd = desc.getSpeciesContext().getAttribute(getName());
			isParameter = vd != null && vd.isParameter();
		} else {
			isParameter = false;
		}
		return "global " + (isParameter ? "parameter" : isNotModifiable ? "constant" : "attribute") + " " + getName()
				+ " of type " + getGamlType().getTitle();
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

	@Override
	public void collectUsedVarsOf(final SpeciesDescription species,
			final ICollector<IVarDescriptionUser> alreadyProcessed, final ICollector<VariableDescription> result) {
		if (alreadyProcessed.contains(this)) return;
		alreadyProcessed.add(this);
		final SpeciesDescription sd = this.getDefinitionDescription().getSpeciesContext();
		if (species.equals(sd)) { result.add(sd.getAttribute(getName())); }
	}

	@Override
	public IExpression resolveAgainst(final IScope scope) {
		return this;
	}

}
