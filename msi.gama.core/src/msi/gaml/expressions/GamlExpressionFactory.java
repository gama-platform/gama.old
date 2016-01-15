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
import java.util.*;
import org.eclipse.emf.ecore.EObject;
import msi.gama.common.interfaces.*;
import msi.gaml.descriptions.*;
import msi.gaml.statements.Arguments;
import msi.gaml.types.*;

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
		if ( parser.get() == null ) {
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
		if ( isInitialized() ) {
			getParser().reset();
		}
	}

	/**
	 * Method createUnit()
	 * @see msi.gaml.expressions.IExpressionFactory#createUnit(java.lang.Object, msi.gaml.types.IType, java.lang.String)
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
		if ( type.getType() != Types.SPECIES ) { return null; }
		SpeciesDescription sd = type.getContentType().getSpecies();
		if ( sd == null ) { return null; }
		return new SpeciesConstantExpression(sd.getName(), type);
	}

	@Override
	public ConstantExpression createConst(final Object val, final IType type, final String name) {
		if ( type.getType() == Types.SPECIES ) { return createSpeciesConstant(type); }
		if ( type == Types.SKILL ) { return new SkillConstantExpression((String) val, type); }
		if ( val == null ) { return NIL_EXPR; }
		if ( val instanceof Boolean ) { return (Boolean) val ? TRUE_EXPR : FALSE_EXPR; }
		return new ConstantExpression(val, type, name);
	}

	@Override
	public ConstantExpression getUnitExpr(final String unit) {
		return UNITS_EXPR.get(unit);
	}

	@Override
	public IExpression createExpr(final IExpressionDescription ied, final IDescription context) {
		if ( ied == null ) { return null; }
		IExpression p = ied.getExpression();
		return p == null ? getParser().compile(ied, context) : p;
	}

	@Override
	public IExpression createExpr(final String s, final IDescription context) {
		if ( s == null || s.isEmpty() ) { return null; }
		return getParser().compile(StringBasedExpressionDescription.create(s), context);
	}

	@Override
	public Map<String, IExpressionDescription> createArgumentMap(final StatementDescription action,
		final IExpressionDescription args, final IDescription context) {
		if ( args == null ) { return Collections.EMPTY_MAP; }
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
		final IExpression ... args) {
		if ( args == null ) { return null; }
		for ( IExpression exp : args ) {
			if ( exp == null ) { return null; }
		}
		if ( OPERATORS.containsKey(op) ) {
			// We get the possible sets of types registered in OPERATORS
			Map<Signature, OperatorProto> ops = OPERATORS.get(op);
			// We create the signature corresponding to the arguments
			// 19/02/14 Only the simplified signature is used now
			Signature signature = new Signature(args).simplified();
			Signature originalSignature = signature;
			// If the signature is not present in the registry
			if ( !ops.containsKey(signature) ) {
				final List<Signature> temp_types = new ArrayList(10);
				// temp_types.clear();
				// We collect all the signatures that are compatible
				for ( Map.Entry<Signature, OperatorProto> entry : ops.entrySet() ) {
					if ( signature.isCompatibleWith(entry.getKey()) ) {
						temp_types.add(entry.getKey());
					}
				}
				// No signature has been found, we throw an exception
				if ( temp_types.size() == 0 ) {
					context.error(
						"No operator found for applying '" + op + "' to " + signature + " (operators available for " +
							Arrays.toString(ops.keySet().toArray()) + ")",
						IGamlIssue.UNMATCHED_OPERANDS, currentEObject);
					return null;
				}
				signature = temp_types.get(0);
				// We find the one with the minimum distance to the arguments
				int dist = signature.distanceTo(originalSignature);
				for ( int i = 1, n = temp_types.size(); i < n; i++ ) {
					int d = temp_types.get(i).distanceTo(originalSignature);
					if ( d < dist ) {
						signature = temp_types.get(i);
						dist = d;
					}
				}
				// We coerce the types if necessary, by wrapping the original expressions in a
				// casting expression
				IType[] coercingTypes = signature.coerce(originalSignature, context);
				for ( int i = 0; i < coercingTypes.length; i++ ) {
					IType t = coercingTypes[i];
					if ( t != null ) {
						// Emits a warning when a float is truncated. See Issue 735.
						if ( t.id() == IType.INT ) {
							// 20/1/14 Changed to info to avoid having too many harmless warnings
							context.info(t.toString() + " expected. '" + args[i].serialize(false) +
								"' will be  truncated to int.", IGamlIssue.UNMATCHED_OPERANDS, currentEObject);
						}
						args[i] =
							createOperator(IKeyword.AS, context, currentEObject, args[i], createTypeExpression(t));
						// args[i] = createOperator(t.toString(), context, currentEObject, args[i]);
					}
				}
			}

			final OperatorProto proto = ops.get(signature);
			// We finally make an instance of the operator and init it with the arguments
			IExpression copy = proto.create(context, args);
			if ( copy != null ) {
				String ged = proto.getDeprecated();
				if ( ged != null ) {
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
		if ( action.verifyArgs(callerContext,
			arguments) ) { return new PrimitiveOperator(null, callerContext, action, call, arguments); }
		return null;
	}

	/**
	 * Method createCastingExpression()
	 * @see msi.gaml.expressions.IExpressionFactory#createCastingExpression(msi.gaml.types.IType)
	 */
	@Override
	public IExpression createTypeExpression(final IType type) {
		return new TypeExpression(type);
	}

	/**
	 * Method getFacetExpression()
	 * @see msi.gaml.expressions.IExpressionFactory#getFacetExpression(msi.gaml.descriptions.IDescription, java.lang.Object)
	 */
	@Override
	public EObject getFacetExpression(final IDescription context, final EObject facet) {
		return getParser().getFacetExpression(context, facet);
	}

}
