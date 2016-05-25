/*********************************************************************************************
 *
 *
 * 'GamlExpressionFactory.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.expressions;

import static msi.gaml.expressions.IExpressionCompiler.OPERATORS;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;

import gnu.trove.map.hash.THashMap;
import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.descriptions.OperatorProto;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.descriptions.StatementDescription;
import msi.gaml.descriptions.StringBasedExpressionDescription;
import msi.gaml.factories.ChildrenProvider;
import msi.gaml.factories.DescriptionFactory;
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

public class GamlExpressionFactory implements IExpressionFactory {

	static IExpressionCompilerProvider parserProvider;
	ThreadLocal<IExpressionCompiler> parser;

	public GamlExpressionFactory() {
		parser = new ThreadLocal();
	}

	public static void registerParserProvider(final IExpressionCompilerProvider f) {
		parserProvider = f;
	}

	@Override
	public IExpressionCompiler getParser() {
		if (parser.get() == null) {
			parser.set(parserProvider.newParser());
		}
		return parser.get();
	}

	@Override
	public boolean isInitialized() {
		return parser.get() != null;
	}

	@Override
	public void resetParser() {
		if (isInitialized()) {
			getParser().reset();
		}
	}

	/**
	 * Method createUnit()
	 * 
	 * @see msi.gaml.expressions.IExpressionFactory#createUnit(java.lang.Object,
	 *      msi.gaml.types.IType, java.lang.String)
	 */
	@Override
	public UnitConstantExpression createUnit(final Object value, final IType t, final String name, final String doc,
			final String[] names) {
		return UnitConstantExpression.create(value, t, name, doc, names);
	}

	@Override
	public ConstantExpression createConst(final Object val, final IType type) {
		return createConst(val, type, null);
	}

	@Override
	public SpeciesConstantExpression createSpeciesConstant(final IType type) {
		if (type.getType() != Types.SPECIES) {
			return null;
		}
		final SpeciesDescription sd = type.getContentType().getSpecies();
		if (sd == null) {
			return null;
		}
		return new SpeciesConstantExpression(sd.getName(), type);
	}

	@Override
	public ConstantExpression createConst(final Object val, final IType type, final String name) {
		if (type.getType() == Types.SPECIES) {
			return createSpeciesConstant(type);
		}
		if (type == Types.SKILL) {
			return new SkillConstantExpression((String) val, type);
		}
		if (val == null) {
			return NIL_EXPR;
		}
		if (val instanceof Boolean) {
			return (Boolean) val ? TRUE_EXPR : FALSE_EXPR;
		}
		return new ConstantExpression(val, type, name);
	}

	@Override
	public ConstantExpression getUnitExpr(final String unit) {
		return UNITS_EXPR.get(unit);
	}

	@Override
	public IExpression createExpr(final IExpressionDescription ied, final IDescription context) {
		if (ied == null) {
			return null;
		}
		final IExpression p = ied.getExpression();
		return p == null ? getParser().compile(ied, context) : p;
	}

	@Override
	public IExpression createExpr(final String s, final IDescription context) {
		if (s == null || s.isEmpty()) {
			return null;
		}
		return getParser().compile(StringBasedExpressionDescription.create(s), context);
	}

	@Override
	public Map<String, IExpressionDescription> createArgumentMap(final StatementDescription action,
			final IExpressionDescription args, final IDescription context) {
		if (args == null) {
			return Collections.EMPTY_MAP;
		}
		return getParser().parseArguments(action, args.getTarget(), context, false);
	}

	@Override
	public IExpression createVar(final String name, final IType type, final boolean isConst, final int scope,
			final IDescription definitionDescription) {
		switch (scope) {
		case IVarExpression.GLOBAL:
			return GlobalVariableExpression.create(name, type, isConst, definitionDescription.getModelDescription());
		case IVarExpression.AGENT:
			return new AgentVariableExpression(name, type, isConst, definitionDescription);
		case IVarExpression.TEMP:
			return new TempVariableExpression(name, type, definitionDescription);
		case IVarExpression.EACH:
			return new EachExpression(type);
		case IVarExpression.WORLD:
			return new WorldExpression(name, type, definitionDescription.getModelDescription());
		case IVarExpression.SELF:
			return new SelfExpression(type);
		default:
			return null;
		}
	}

	@Override
	public IExpression createList(final List<? extends IExpression> elements) {
		return ListExpression.create(elements);
	}

	@Override
	public IExpression createMap(final List<? extends IExpression> elements) {
		return MapExpression.create(elements);
	}

	@Override
	public IExpression createOperator(final String op, final IDescription context, final EObject currentEObject,
			final IExpression... args) {
		if (args == null) {
			return null;
		}
		for (final IExpression exp : args) {
			if (exp == null) {
				return null;
			}
		}
		if (OPERATORS.containsKey(op)) {
			// We get the possible sets of types registered in OPERATORS
			final THashMap<Signature, OperatorProto> ops = OPERATORS.get(op);
			// We create the signature corresponding to the arguments
			// 19/02/14 Only the simplified signature is used now
			Signature signature = new Signature(args).simplified();
			final Signature originalSignature = signature;
			// If the signature is not present in the registry
			if (!ops.containsKey(signature)) {
				final Iterable<Signature> filtered = Iterables.filter(ops.keySet(), new Predicate<Signature>() {

					@Override
					public boolean apply(final Signature input) {
						return originalSignature.isCompatibleWith(input);
					}
				});
				if (Iterables.isEmpty(filtered)) {
					context.error(
							"No operator found for applying '" + op + "' to " + signature + " (operators available for "
									+ Arrays.toString(ops.keySet().toArray()) + ")",
							IGamlIssue.UNMATCHED_OPERANDS, currentEObject);
					return null;
				}
				signature = Ordering.from(new Comparator<Signature>() {

					@Override
					public int compare(final Signature o1, final Signature o2) {
						return o1.distanceTo(originalSignature) - o2.distanceTo(originalSignature);
					}
				}).min(filtered);

				// final List<Signature> temp_types = new ArrayList(10);
				// temp_types.clear();
				// We collect all the signatures that are compatible
				// ops.forEachEntry(new TObjectObjectProcedure<Signature,
				// OperatorProto>() {
				//
				// @Override
				// public boolean execute(final Signature a, final OperatorProto
				// b) {
				// if (originalSignature.isCompatibleWith(a)) {
				// temp_types.add(a);
				// }
				// return true;
				// }
				// });
				// No signature has been found, we throw an exception
				// if (temp_types.size() == 0) {
				// if (Iterables.isEmpty(filtered)) {
				// context.error(
				// "No operator found for applying '" + op + "' to " + signature
				// + " (operators available for "
				// + Arrays.toString(ops.keySet().toArray()) + ")",
				// IGamlIssue.UNMATCHED_OPERANDS, currentEObject);
				// return null;
				// }
				// signature = temp_types.get(0);
				// We find the one with the minimum distance to the arguments
				// int dist = signature.distanceTo(originalSignature);
				// for (int i = 1, n = temp_types.size(); i < n; i++) {
				// final int d =
				// temp_types.get(i).distanceTo(originalSignature);
				// if (d < dist) {
				// signature = temp_types.get(i);
				// dist = d;
				// }
				// }
				// We coerce the types if necessary, by wrapping the original
				// expressions in a
				// casting expression
				final IType[] coercingTypes = signature.coerce(originalSignature, context);
				for (int i = 0; i < coercingTypes.length; i++) {
					final IType t = coercingTypes[i];
					if (t != null) {
						// Emits a warning when a float is truncated. See Issue
						// 735.
						if (t.id() == IType.INT) {
							// 20/1/14 Changed to info to avoid having too many
							// harmless warnings
							context.info(
									t.toString() + " expected. '" + args[i].serialize(false)
											+ "' will be  truncated to int.",
									IGamlIssue.UNMATCHED_OPERANDS, currentEObject);
						}
						args[i] = createOperator(IKeyword.AS, context, currentEObject, args[i],
								createTypeExpression(t));
						// args[i] = createOperator(t.toString(), context,
						// currentEObject, args[i]);
					}
				}
			}
			final OperatorProto proto = ops.get(signature);
			// We finally make an instance of the operator and init it with the
			// arguments
			final IExpression copy = proto.create(context, currentEObject, args);
			if (copy != null) {
				final String ged = proto.getDeprecated();
				if (ged != null) {
					context.warning(proto.getName() + " is deprecated: " + ged, IGamlIssue.DEPRECATED, currentEObject);
				}
			}
			return copy;
		}
		return null;

	}

	@Override
	public IExpression createAction(final String op, final IDescription callerContext,
			final StatementDescription action, final IExpression call, final Arguments arguments) {
		// Arguments args = createArgs(arguments);
		if (action.verifyArgs(callerContext, arguments)) {
			return new PrimitiveOperator(null, callerContext, action, call, arguments);
		}
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

	/**
	 * Method getFacetExpression()
	 * 
	 * @see msi.gaml.expressions.IExpressionFactory#getFacetExpression(msi.gaml.descriptions.IDescription,
	 *      java.lang.Object)
	 */
	@Override
	public EObject getFacetExpression(final IDescription context, final EObject facet) {
		return getParser().getFacetExpression(context, facet);
	}

	@Override
	public IExpression createTemporaryActionForAgent(final IAgent agent, final String action) {
		final IDescription context = agent.getSpecies().getDescription();
		final IDescription desc = DescriptionFactory.create(IKeyword.ACTION, context, ChildrenProvider.FUTURE,
				IKeyword.TYPE, IKeyword.STRING, IKeyword.NAME, "temporary_action");
		final List<IDescription> children = getParser().compileBlock(action, context);
		for (final IDescription child : children) {
			desc.addChild(child);
		}
		desc.validate();
		context.addChild(desc);
		final ActionStatement a = (ActionStatement) desc.compile();
		agent.getSpecies().addTemporaryAction(a);
		return getParser().compile("temporary_action()", context);
	}

}
