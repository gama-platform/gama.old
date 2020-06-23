/*******************************************************************************************************
 *
 * msi.gaml.expressions.GlobalVariableExpression.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.expressions;

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
import msi.gaml.types.IType;

public class GlobalVariableExpression extends VariableExpression implements IVarExpression.Agent {

	public static IExpression create(final String n, final IType<?> type, final boolean notModifiable,
			final IDescription world) {
		final VariableDescription v = ((SpeciesDescription) world).getAttribute(n);
		final IExpression exp = v.getFacetExpr(IKeyword.INIT);
		if (exp != null) {
			final boolean isConst = notModifiable && exp.isConst();
			if (isConst && GamaPreferences.External.CONSTANT_OPTIMIZATION.getValue()) {
				return GAML.getExpressionFactory().createConst(exp.getConstValue(), type, n);
			}
		}
		return new GlobalVariableExpression(n, type, notModifiable, world);
	}

	protected GlobalVariableExpression(final String n, final IType<?> type, final boolean notModifiable,
			final IDescription world) {
		super(n, type, notModifiable, world);
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
		if (scope.hasAccessToGlobalVar(name)) {
			return scope.getGlobalVarValue(name);
		} else {
			final IAgent microAgent = scope.getAgent();
			if (microAgent != null) {
				final IScope agentScope = microAgent.getScope();
				if (agentScope != null) {
					final ITopLevelAgent root = agentScope.getRoot();
					if (root != null) {
						final IScope globalScope = root.getScope();
						if (globalScope != null) { return globalScope.getGlobalVarValue(getName()); }
					}
				}
			}
		}

		return null;
	}

	@Override
	public void setVal(final IScope scope, final Object v, final boolean create) throws GamaRuntimeException {
		if (isNotModifiable) { return; }
		if (scope.hasAccessToGlobalVar(name)) {
			scope.setGlobalVarValue(name, v);
		} else {
			final IAgent sc = scope.getAgent();
			if (sc != null) {
				sc.getScope().getRoot().getScope().setGlobalVarValue(name, v);
			}
		}
	}

	@Override
	public String getTitle() {
		final IDescription desc = getDefinitionDescription();
		final boolean isParameter =
				desc == null ? false : desc.getSpeciesContext().getAttribute(getName()).isParameter();
		return "global " + (isParameter ? "parameter" : isNotModifiable ? "constant" : "attribute") + " " + getName()
				+ " of type " + getGamlType().getTitle();
	}

	@Override
	public String getDocumentation() {
		final IDescription desc = getDefinitionDescription();
		String doc = null;
		String s = "Type " + type.getTitle();
		if (desc != null) {
			final VariableDescription var = desc.getSpeciesContext().getAttribute(name);
			if (var != null) {
				doc = var.getBuiltInDoc();
			}
		} else {
			return s;
		}
		if (doc != null) {
			s += "<br>" + doc;
		}
		final String quality =
				(desc.isBuiltIn() ? "<br>Built In " : doc == null ? "<br>Defined in " : "<br>Redefined in ")
						+ desc.getTitle();

		return s + quality;
	}

	@Override
	public void collectUsedVarsOf(final SpeciesDescription species,
			final ICollector<IVarDescriptionUser> alreadyProcessed, final ICollector<VariableDescription> result) {
		if (alreadyProcessed.contains(this)) { return; }
		alreadyProcessed.add(this);
		final SpeciesDescription sd = this.getDefinitionDescription().getSpeciesContext();
		if (species.equals(sd)) {
			result.add(sd.getAttribute(getName()));
		}
	}

}
