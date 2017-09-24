/*********************************************************************************************
 *
 * 'GlobalVariableExpression.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.expressions;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.ITopLevelAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GAML;
import msi.gama.util.ICollector;
import msi.gaml.compilation.AbstractGamlAdditions;
import msi.gaml.descriptions.IDescription;
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
			if (isConst) {
				final IExpression e = GAML.getExpressionFactory().createConst(exp.value(null), type, n);
				// System.out.println(" ==== Simplification of global " + n + "
				// into " + e.toGaml());
				return e;
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
	public Object value(final IScope scope) throws GamaRuntimeException {
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
						if (globalScope != null)
							return globalScope.getGlobalVarValue(getName());
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
			if (sc != null)
				sc.getScope().getRoot().getScope().setGlobalVarValue(name, v);
		}
	}

	@Override
	public String getTitle() {
		if (name.equals("plantGrow_simu")) {
			System.out.println("plantGrow_simu in GlobalVariableExpression.getTitle()");
		}
		final IDescription desc = getDefinitionDescription();
		final boolean isParameter =
				desc == null ? false : desc.getSpeciesContext().getAttribute(getName()).isParameter();
		return "global " + (isParameter ? "parameter" : isNotModifiable ? "constant" : "attribute") + " " + getName()
				+ " of type " + getType().getTitle();
	}

	@Override
	public String getDocumentation() {
		final IDescription desc = getDefinitionDescription();
		String s = "Type " + type.getTitle();
		final String doc = AbstractGamlAdditions.TEMPORARY_BUILT_IN_VARS_DOCUMENTATION.get(name);
		if (doc != null)
			s += "<br>" + doc;
		if (desc == null)
			return s;
		final String quality =
				(desc.isBuiltIn() ? "<br>Built In " : doc == null ? "<br>Defined in " : "<br>Redefined in ")
						+ desc.getTitle();

		return s + quality;
	}

	@Override
	public void collectUsedVarsOf(final IDescription species, final ICollector<VariableDescription> result) {
		if (species.equals(this.getDefinitionDescription().getSpeciesContext()))
			result.add(getDefinitionDescription().getSpeciesContext().getAttribute(getName()));
	}

}
