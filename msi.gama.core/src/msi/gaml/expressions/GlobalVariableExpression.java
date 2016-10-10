/*********************************************************************************************
 *
 *
 * 'GlobalVariableExpression.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.expressions;

import java.util.Set;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GAML;
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
		// return scope.getGlobalVarValue(getName());
		final IAgent sc = scope.getAgent();
		return sc.getScope().getRoot().getScope().getGlobalVarValue(getName());
	}

	@Override
	public void setVal(final IScope scope, final Object v, final boolean create) throws GamaRuntimeException {
		if (isNotModifiable) {
			return;
		}
		final IAgent sc = scope.getAgent();
		sc.getScope().getRoot().getScope().setGlobalVarValue(getName(), v);
	}

	@Override
	public String getTitle() {
		final IDescription desc = getDefinitionDescription();
		final boolean isParameter = desc == null ? false
				: desc.getSpeciesContext().getAttribute(getName()).isParameter();
		return "global " + (isParameter ? "parameter" : isNotModifiable ? "constant" : "attribute") + " " + getName()
				+ " of type " + getType().getTitle();
	}

	/**
	 * @see msi.gaml.expressions.IExpression#getDocumentation()
	 */
	@Override
	public String getDocumentation() {
		final IDescription desc = getDefinitionDescription();
		return "Of type: " + type.getTitle() + (desc == null ? "<br>Built In" : "<br>Defined in " + desc.getTitle());
	}

	@Override
	public void collectUsedVarsOf(final IDescription species, final Set<VariableDescription> result) {
		if (species.equals(this.getDefinitionDescription().getSpeciesContext()))
			result.add(getDefinitionDescription().getSpeciesContext().getAttribute(getName()));
	}

}
