/*********************************************************************************************
 *
 *
 * 'WorldExpression.java', in plugin 'msi.gama.core', is part of the source code of the
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
import msi.gama.precompiler.GamlProperties;
import msi.gama.runtime.IScope;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.VariableDescription;
import msi.gaml.types.IType;

public class WorldExpression extends AbstractExpression implements IVarExpression {

	protected WorldExpression(final IType<?> type, final IDescription global) {
		this.type = type;
		// super(IKeyword.WORLD_AGENT_NAME, type, true, global);
	}

	@Override
	public Object value(final IScope scope) {
		// return scope.getSimulationScope();
		final IAgent sc = scope.getAgent();
		return sc.getScope().getRoot().getScope().getSimulation();
	}

	/**
	 * @see msi.gaml.expressions.IExpression#getDocumentation()
	 */
	@Override
	public String getDocumentation() {
		return "Global constant <b>world</b>, represents the current simulation";
	}

	@Override
	public void setVal(final IScope scope, final Object v, final boolean create) {
	}

	/**
	 * Method collectPlugins()
	 * 
	 * @see msi.gama.common.interfaces.IGamlDescription#collectPlugins(java.util.Set)
	 */
	@Override
	public void collectMetaInformation(final GamlProperties meta) {
	}

	@Override
	public void collectUsedVarsOf(final IDescription species, final Set<VariableDescription> result) {
	}

	@Override
	public boolean isConst() {
		return false;
	}

	@Override
	public String getTitle() {
		return "variable world";
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return IKeyword.WORLD_AGENT_NAME;
	}

	@Override
	public boolean isNotModifiable() {
		return true;
	}

	@Override
	public IExpression getOwner() {
		return null;
	}

	@Override
	public VariableExpression getVar() {
		// TODO Auto-generated method stub
		return null;
	}

}
