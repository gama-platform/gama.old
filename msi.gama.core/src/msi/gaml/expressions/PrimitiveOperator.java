/*********************************************************************************************
 *
 * 'PrimitiveOperator.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.expressions;

import java.util.Map;

import com.google.common.collect.Iterables;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.ICollector;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.descriptions.OperatorProto;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.descriptions.StatementDescription;
import msi.gaml.descriptions.VariableDescription;
import msi.gaml.operators.Cast;
import msi.gaml.operators.Strings;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.Arguments;
import msi.gaml.statements.IStatement;
import msi.gaml.types.IType;

/**
 * PrimitiveOperator. An operator that wraps a primitive or an action.
 *
 * @author drogoul 4 sept. 07
 */

public class PrimitiveOperator implements IExpression, IOperator {

	final Arguments parameters;
	final IExpression target;
	final StatementDescription action;
	final boolean isSuper;

	public PrimitiveOperator(final IDescription callerContext, final StatementDescription action,
			final IExpression target, final Arguments args, final boolean superInvocation) {
		this.target = target;
		this.action = action;
		isSuper = superInvocation;
		parameters = args;

	}

	@Override
	public String getName() {
		return action.getName();
	}

	@Override
	public Object value(final IScope scope) throws GamaRuntimeException {
		if (scope == null) { return null; }
		final IAgent target = this.target == null ? scope.getAgent() : Cast.asAgent(scope, this.target.value(scope));
		if (target == null) { return null; }
		// AD 13/05/13 The target should not be pushed so early to the scope, as
		// the arguments will be (incorrectly)
		// evaluated in its context, but how to prevent it ? See Issue 401.
		// One way is (1) to gather the executer
		final ISpecies species = isSuper ? target.getSpecies().getParentSpecies() : target.getSpecies();
		final IStatement.WithArgs executer = species.getAction(getName());
		// Then, (2) to set the caller to the actual agent on the scope (in the
		// context of which the arguments need to
		// be evaluated
		if (executer != null) {
			// And finally, (3) to execute the executer on the target (it will
			// be pushed in the scope)
			return scope.execute(executer, target, parameters).getValue();
		}
		return null;
	}

	@Override
	public boolean isConst() {
		return false;
	}

	@Override
	public String getTitle() {
		final StringBuilder sb = new StringBuilder(50);
		sb.append("action ").append(getName()).append(" defined in species ")
				.append(target.getGamlType().getSpeciesName()).append(" returns ").append(getGamlType().getTitle());
		return sb.toString();

	}

	@Override
	public String getDocumentation() {
		return action.getDocumentation();
	}

	@Override
	public String getDefiningPlugin() {
		return action.getDefiningPlugin();
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		final StringBuilder sb = new StringBuilder();
		if (target != null) {
			AbstractExpression.parenthesize(sb, target);
			sb.append(".");
		}
		sb.append(literalValue()).append("(");
		argsToGaml(sb, includingBuiltIn);
		sb.append(")");
		return sb.toString();
	}

	protected String argsToGaml(final StringBuilder sb, final boolean includingBuiltIn) {
		if (parameters == null || parameters.isEmpty()) { return ""; }
		for (final Map.Entry<String, IExpressionDescription> entry : parameters.entrySet()) {
			final String name = entry.getKey();
			final IExpressionDescription expr = entry.getValue();
			if (Strings.isGamaNumber(name)) {
				sb.append(expr.serialize(false));
			} else {
				sb.append(name).append(":").append(expr.serialize(includingBuiltIn));
			}
			sb.append(", ");
		}
		if (sb.length() > 0) {
			sb.setLength(sb.length() - 2);
		}
		return sb.toString();
	}

	/**
	 * Method collectPlugins()
	 * 
	 * @see msi.gama.common.interfaces.IGamlDescription#collectPlugins(java.util.Set)
	 */
	// @Override
	// public void collectMetaInformation(final GamlProperties meta) {
	// meta.put(GamlProperties.PLUGINS, action.getDefiningPlugin());
	// if (action.isBuiltIn()) {
	// meta.put(GamlProperties.ACTIONS, action.getName());
	// }
	// if (parameters != null) {
	// parameters.forEachValue(exp -> {
	// exp.collectMetaInformation(meta);
	// return true;
	// });
	// }
	// }

	@Override
	public void collectUsedVarsOf(final SpeciesDescription species, final ICollector<VariableDescription> result) {
		if (parameters != null) {
			parameters.forEachEntry((name, exp) -> {
				final IExpression expression = exp.getExpression();
				if (expression != null) {
					expression.collectUsedVarsOf(species, result);
				}
				return true;

			});
		}
	}

	@Override
	public void setName(final String newName) {}

	@Override
	public IType<?> getGamlType() {
		return action.getGamlType();
	}

	@Override
	public void dispose() {
		if (parameters != null) {
			parameters.dispose();
		}
	}

	@Override
	public String literalValue() {
		return action.getName();
	}

	@Override
	public IExpression resolveAgainst(final IScope scope) {
		return this;
	}

	@Override
	public boolean shouldBeParenthesized() {
		return true;
	}

	@Override
	public void visitSuboperators(final IOperatorVisitor visitor) {
		if (parameters != null) {
			parameters.forEach((name, exp) -> {
				final IExpression expr = exp.getExpression();
				if (expr instanceof IOperator) {
					visitor.visit((IOperator) expr);
				}
			});
		}

	}

	// TODO The arguments are not ordered...
	@Override
	public IExpression arg(final int i) {
		if (i < 0 || i > parameters.size()) { return null; }
		return Iterables.get(parameters.values(), i).getExpression();
	}

	@Override
	public OperatorProto getPrototype() {
		return null;
	}

}
