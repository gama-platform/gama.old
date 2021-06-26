/*******************************************************************************************************
 *
 * msi.gaml.expressions.GamlExpressionFactory.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.expressions;

import static com.google.common.collect.Iterables.any;
import static com.google.common.collect.Iterables.filter;
import static msi.gaml.expressions.IExpressionCompiler.OPERATORS;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

import com.google.common.collect.Iterables;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IExecutionContext;
import msi.gama.util.IMap;
import msi.gaml.descriptions.ActionDescription;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.descriptions.OperatorProto;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.descriptions.StringBasedExpressionDescription;
import msi.gaml.expressions.TempVariableExpression.MyselfExpression;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.operators.IUnits;
import msi.gaml.statements.ActionStatement;
import msi.gaml.statements.Arguments;
import msi.gaml.types.IType;
import msi.gaml.types.Signature;
import msi.gaml.types.Types;

/**
 * The static class ExpressionFactory.
 *
 * @author drogoul
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamlExpressionFactory implements IExpressionFactory {

	public interface ParserProvider {
		IExpressionCompiler get();
	}

	static ThreadLocal<IExpressionCompiler> parser;

	public static void registerParserProvider(final ParserProvider f) {
		parser = new ThreadLocal() {
			@Override
			protected IExpressionCompiler initialValue() {
				return f.get();
			}
		};
	}

	@Override
	public IExpressionCompiler getParser() {
		return parser.get();
	}

	@Override
	public void resetParser() {
		parser.get().dispose();
		parser.remove();
		// getParser().reset();
	}

	/**
	 * Method createUnit()
	 *
	 * @see msi.gaml.expressions.IExpressionFactory#createUnit(java.lang.Object, msi.gaml.types.IType, java.lang.String)
	 */
	@Override
	public UnitConstantExpression createUnit(final Object value, final IType t, final String name, final String doc,
			final String deprecated, final boolean isTime, final String[] names) {
		final UnitConstantExpression exp = UnitConstantExpression.create(value, t, name, doc, isTime, names);
		if (deprecated != null && !deprecated.isEmpty()) { exp.setDeprecated(deprecated); }
		return exp;

	}

	@Override
	public ConstantExpression createConst(final Object val, final IType type) {
		return createConst(val, type, null);
	}

	@Override
	public SpeciesConstantExpression createSpeciesConstant(final IType type) {
		if (type.getGamlType() != Types.SPECIES) return null;
		final SpeciesDescription sd = type.getContentType().getSpecies();
		if (sd == null) return null;
		return new SpeciesConstantExpression(sd.getName(), type);
	}

	@Override
	public ConstantExpression createConst(final Object val, final IType type, final String name) {
		if (type.getGamlType() == Types.SPECIES) return createSpeciesConstant(type);
		if (type == Types.SKILL) return new SkillConstantExpression((String) val, type);
		if (val == null) return NIL_EXPR;
		if (val instanceof Boolean) return (Boolean) val ? TRUE_EXPR : FALSE_EXPR;
		return new ConstantExpression(val, type, name);
	}

	@Override
	public UnitConstantExpression getUnitExpr(final String unit) {
		return IUnits.UNITS_EXPR.get(unit);
	}

	@Override
	public IExpression createExpr(final IExpressionDescription ied, final IDescription context) {
		if (ied == null) return null;
		final IExpression p = ied.getExpression();
		return p == null ? getParser().compile(ied, context) : p;
	}

	@Override
	public IExpression createExpr(final String s, final IDescription context) {
		if (s == null || s.isEmpty()) return null;
		return getParser().compile(StringBasedExpressionDescription.create(s), context);
	}

	@Override
	public IExpression createExpr(final String s, final IDescription context,
			final IExecutionContext additionalContext) {
		if (s == null || s.isEmpty()) return null;
		return getParser().compile(s, context, additionalContext);
	}

	@Override
	public Arguments createArgumentMap(final ActionDescription action, final IExpressionDescription args,
			final IDescription context) {
		if (args == null) return null;
		return getParser().parseArguments(action, args.getTarget(), context, false);
	}

	@Override
	public IExpression createVar(final String name, final IType type, final boolean isConst, final int scope,
			final IDescription definitionDescription) {
		switch (scope) {
			case IVarExpression.GLOBAL:
				return GlobalVariableExpression.create(name, type, isConst,
						definitionDescription.getModelDescription());
			case IVarExpression.AGENT:
				return new AgentVariableExpression(name, type, isConst, definitionDescription);
			case IVarExpression.TEMP:
				return new TempVariableExpression(name, type, definitionDescription);
			case IVarExpression.EACH:
				return new EachExpression(name, type);
			case IVarExpression.SELF:
				return new SelfExpression(type);
			case IVarExpression.SUPER:
				return new SuperExpression(type);
			case IVarExpression.MYSELF:
				return new MyselfExpression(type, definitionDescription);
			default:
				return null;
		}
	}

	@Override
	public IExpression createList(final Iterable<? extends IExpression> elements) {
		return ListExpression.create(elements);
	}

	public IExpression createList(final IExpression[] elements) {
		return ListExpression.create(elements);
	}

	@Override
	public IExpression createMap(final Iterable<? extends IExpression> elements) {
		return MapExpression.create(elements);
	}

	@Override
	public boolean hasOperator(final String op, final IDescription context, final EObject object,
			final IExpression... args) {
		// If arguments are invalid, we have no match
		if (args == null || args.length == 0) return false;
		for (final IExpression exp : args) {
			if (exp == null) return false;
		}
		// If the operator is not known, we have no match
		if (!OPERATORS.containsKey(op)) return false;
		final IMap<Signature, OperatorProto> ops = OPERATORS.get(op);
		final Signature sig = new Signature(args).simplified();
		// Does any known operator signature match with the signatue of the expressions ?
		boolean matches = any(ops.keySet(), s -> sig.matchesDesiredSignature(s));
		if (!matches) {
			// Check if a varArg is not a possibility
			matches = any(ops.keySet(), s -> Signature.varArgFrom(sig).matchesDesiredSignature(s));
		}
		return matches;
	}

	@Override
	public IExpression createOperator(final String op, final IDescription context, final EObject eObject,
			final IExpression... args) {
		if (!hasOperator(op, context, eObject, args)) {
			final IMap<Signature, OperatorProto> ops = OPERATORS.get(op);
			final Signature userSignature = new Signature(args).simplified();
			String msg = "No operator found for applying '" + op + "' to " + userSignature;
			if (ops != null) { msg += " (operators available for " + Arrays.toString(ops.keySet().toArray()) + ")"; }
			context.error(msg, IGamlIssue.UNMATCHED_OPERANDS, eObject);
			return null;
		}
		// We get the possible sets of types registered in OPERATORS
		final IMap<Signature, OperatorProto> ops = OPERATORS.get(op);
		// We create the signature corresponding to the arguments
		// 19/02/14 Only the simplified signature is used now
		Signature userSignature = new Signature(args).simplified();
		final Signature originalUserSignature = userSignature;
		// If the signature is not present in the registry
		if (!ops.containsKey(userSignature)) {
			final Signature[] matching = Iterables.toArray(
					filter(ops.keySet(), s -> originalUserSignature.matchesDesiredSignature(s)), Signature.class);
			final int size = matching.length;
			if (size == 0)
				// It is a varArg, we call recursively the method
				return createOperator(op, context, eObject, createList(args));
			else if (size == 1) {
				// Only one choice
				userSignature = matching[0];
			} else {
				// Several choices, we take the closest
				int distance = Integer.MAX_VALUE;
				for (final Signature s : matching) {
					final int dist = s.distanceTo(originalUserSignature);
					if (dist == 0) {
						userSignature = s;
						break;
					} else if (dist < distance) {
						distance = dist;
						userSignature = s;
					}
				}
			}

			// We coerce the types if necessary, by wrapping the original
			// expressions in a casting expression
			final IType[] coercingTypes = userSignature.coerce(originalUserSignature, context);

			for (int i = 0; i < coercingTypes.length; i++) {
				final IType t = coercingTypes[i];
				if (t != null) {
					// Emits an info when a float is truncated. See Issue 735.
					if (t.id() == IType.INT) {
						context.info("'" + args[i].serialize(false) + "' will be  truncated to int.",
								IGamlIssue.UNMATCHED_OPERANDS, eObject);
					}
					args[i] = createAs(context, args[i], createTypeExpression(t));
				}
			}
		}

		final OperatorProto proto = ops.get(userSignature);
		return createDirectly(context, eObject, proto, args);
	}

	@Override
	public IExpression createAs(final IDescription context, final IExpression toCast, final IExpression type) {
		return OperatorProto.AS.create(context, null, toCast, type);
	}

	private IExpression createDirectly(final IDescription context, final EObject eObject, final OperatorProto proto,
			final IExpression... args) {
		// We finally make an instance of the operator and init it with the arguments
		final IExpression copy = proto.create(context, eObject, args);
		if (copy != null) {
			// We verify that it is not deprecated
			final String ged = proto.getDeprecated();
			if (ged != null) {
				context.warning(proto.getName() + " is deprecated: " + ged, IGamlIssue.DEPRECATED, eObject);
			}
		}
		return copy;
	}

	@Override
	public IExpression createAction(final String op, final IDescription callerContext, final ActionDescription action,
			final IExpression call, final Arguments arguments) {
		if (action.verifyArgs(callerContext, arguments))
			return new PrimitiveOperator(callerContext, action, call, arguments, call instanceof SuperExpression);
		return null;
	}

	/**
	 * Method createCastingExpression()
	 *
	 * @see msi.gaml.expressions.IExpressionFactory#createCastingExpression(msi.gaml.types.IType)
	 */
	@Override
	public IExpression createTypeExpression(final IType type) {
		return new TypeExpression(type);
	}

	@Override
	public IExpression createTemporaryActionForAgent(final IAgent agent, final String action,
			final IExecutionContext tempContext) {
		final SpeciesDescription context = agent.getSpecies().getDescription();
		final ActionDescription desc = (ActionDescription) DescriptionFactory.create(IKeyword.ACTION, context,
				Collections.EMPTY_LIST, IKeyword.TYPE, IKeyword.UNKNOWN, IKeyword.NAME, TEMPORARY_ACTION_NAME);
		final List<IDescription> children = getParser().compileBlock(action, context, tempContext);
		for (final IDescription child : children) {
			desc.addChild(child);
		}
		desc.validate();
		context.addChild(desc);
		final ActionStatement a = (ActionStatement) desc.compile();
		agent.getSpecies().addTemporaryAction(a);
		return getParser().compile(TEMPORARY_ACTION_NAME + "()", context, null);
	}

}
