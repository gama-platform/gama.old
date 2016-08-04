/*********************************************************************************************
 *
 *
 * 'PrimitiveOperator.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.expressions;

import java.util.Map;

import gnu.trove.procedure.TObjectProcedure;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlProperties;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.descriptions.OperatorProto;
import msi.gaml.descriptions.StatementDescription;
import msi.gaml.operators.Cast;
import msi.gaml.operators.Strings;
import msi.gaml.statements.Arguments;
import msi.gaml.statements.IStatement;
import msi.gaml.types.IType;

/**
 * PrimitiveOperator. An operator that wraps a primitive or an action.
 *
 * @author drogoul 4 sept. 07
 */

public class PrimitiveOperator extends AbstractNAryOperator {

	final Arguments parameters;
	final StatementDescription action;

	public PrimitiveOperator(final OperatorProto proto, final IDescription callerContext,
			final StatementDescription action, final IExpression call, final Arguments args) {
		super(proto, call);
		name = action.getName();
		type = action.getType();
		this.action = action;
		parameters = args == null ? new Arguments() : args;

	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Object value(final IScope scope) throws GamaRuntimeException {
		if (scope == null) {
			return null;
		}
		final IAgent target = numArg() == 0 ? scope.getAgent() : Cast.asAgent(scope, arg(0).value(scope));
		if (target == null) {
			return null;
		}
		// AD 13/05/13 The target should not be pushed so early to the scope, as
		// the arguments will be (incorrectly)
		// evaluated in its context, but how to prevent it ? See Issue 401.
		// One way is (1) to gather the executer
		final IStatement.WithArgs executer = target.getSpecies().getAction(getName());
		// Then, (2) to set the caller to the actual agent on the scope (in the
		// context of which the arguments need to
		// be evaluated
		if (executer != null) {
			// Now done by the scope itself:
			// parameters.setCaller(scope.getAgentScope());
			// And finally, (3) to execute the executer on the target (it will
			// be pushed in the scope)
			final Object[] result = new Object[1];
			scope.execute(executer, target, parameters, result);
			return result[0];
		}
		return null;
	}

	@Override
	public PrimitiveOperator copy() {
		// See what impact it has got.
		return this;
	}

	@Override
	protected IType computeType(final int t, final IType def, final int kind) {
		return def;
	}

	@Override
	public boolean isConst() {
		return false;
	}

	@Override
	public String getTitle() {
		final StringBuilder sb = new StringBuilder(50);
		sb.append("action ").append(getName()).append(" defined in species ").append(arg(0).getType().getSpeciesName())
				.append(" returns ").append(getType().getTitle());
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
		parenthesize(sb, exprs[0]);
		sb.append(".").append(literalValue()).append("(");
		argsToGaml(sb, includingBuiltIn);
		sb.append(")");
		return sb.toString();
	}

	protected String argsToGaml(final StringBuilder sb, final boolean includingBuiltIn) {
		if (parameters == null || parameters.isEmpty()) {
			return "";
		}
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
	@Override
	public void collectMetaInformation(final GamlProperties meta) {
		meta.put(GamlProperties.PLUGINS, action.getDefiningPlugin());
		if (action.isBuiltIn()) {
			meta.put(GamlProperties.ACTIONS, action.getName());
		}
		parameters.forEachValue(new TObjectProcedure<IExpressionDescription>() {

			@Override
			public boolean execute(final IExpressionDescription exp) {
				exp.collectMetaInformation(meta);
				return true;
			}
		});
	}
}
