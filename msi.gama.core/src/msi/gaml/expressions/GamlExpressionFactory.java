/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.expressions;

import static msi.gaml.expressions.IExpressionCompiler.OPERATORS;
import java.util.*;
import msi.gama.common.interfaces.IGamlIssue;
import msi.gaml.descriptions.*;
import msi.gaml.operators.IUnits;
import msi.gaml.types.*;

/**
 * The static class ExpressionFactory.
 * 
 * @author drogoul
 */

public class GamlExpressionFactory implements IExpressionFactory {

	IExpressionCompiler parser;

	public GamlExpressionFactory() {}

	@Override
	public void registerParser(final IExpressionCompiler f) {
		parser = f;
	}

	@Override
	public IExpression createConst(final Object val, final IType type) {
		return createConst(val, type, type.defaultContentType());
	}

	@Override
	public IExpression createConst(final Object val, final IType type, final IType contentType) {
		if ( type == Types.get(IType.SPECIES) ) { return new SpeciesConstantExpression((String) val, type, contentType); }
		if ( val == null ) { return NIL_EXPR; }
		if ( val instanceof Boolean ) { return (Boolean) val ? TRUE_EXPR : FALSE_EXPR; }
		return new ConstantExpression(val, type, contentType);
	}

	@Override
	public IExpression createUnitExpr(final String unit, final IDescription context) {
		// FIXME Special cases (to be automated later)
		if ( unit.equals("pixels") || unit.equals("px") ) { return new PixelUnitExpression(); }
		if ( unit.equals("display_width") ) { return new DisplayWidthUnitExpression(); }
		if ( unit.equals("display_height") ) { return new DisplayHeightUnitExpression(); }
		return createConst(IUnits.UNITS.get(unit), Types.get(IType.FLOAT));
	}

	@Override
	public IExpression createExpr(final IExpressionDescription ied, final IDescription context) {
		if ( ied == null ) { return null; }
		IExpression p = ied.getExpression();
		return p == null ? parser.compile(ied, context) : p;
	}

	@Override
	public IExpression createExpr(final String s, final IDescription context) {
		if ( s == null || s.isEmpty() ) { return null; }
		return parser.compile(StringBasedExpressionDescription.create(s), context);
	}

	@Override
	public Map<String, IExpressionDescription> createArgumentMap(final StatementDescription action,
		final IExpressionDescription args, final IDescription context) {
		if ( args == null ) { return Collections.EMPTY_MAP; }
		return parser.parseArguments(action, args.getTarget(), context);
	}

	@Override
	public IVarExpression createVar(final String name, final IType type, final IType contentType, final IType keyType,
		final boolean isConst, final int scope, final IDescription definitionDescription) {
		switch (scope) {
			case IVarExpression.GLOBAL:
				return new GlobalVariableExpression(name, type, contentType, keyType, isConst,
					definitionDescription.getModelDescription()/* .getWorldSpecies() */);
			case IVarExpression.AGENT:
				return new AgentVariableExpression(name, type, contentType, keyType, isConst, definitionDescription);
			case IVarExpression.TEMP:
				return new TempVariableExpression(name, type, contentType, keyType, definitionDescription);
			case IVarExpression.EACH:
				return new EachExpression(type, contentType, keyType);
			case IVarExpression.WORLD:
				return new WorldExpression(name, type, contentType, keyType,
					definitionDescription.getModelDescription());
			case IVarExpression.SELF:
				return new SelfExpression(type, contentType, keyType);
			default:
				return null;
		}
	}

	@Override
	public ListExpression createList(final List<? extends IExpression> elements) {
		return new ListExpression(elements);
	}

	@Override
	public MapExpression createMap(final List<? extends IExpression> elements) {
		return new MapExpression(elements);
	}

	private final List<Signature> temp_types = new ArrayList(10);

	@Override
	public IExpression createOperator(final String op, final IDescription context, final IExpression ... args) {
		if ( args == null ) { return null; }
		for ( IExpression exp : args ) {
			if ( exp == null ) { return null; }
		}
		if ( OPERATORS.containsKey(op) ) {
			// We get the possible sets of types registered in OPERATORS
			Map<Signature, IOperator> ops = OPERATORS.get(op);
			// We create the signature corresponding to the arguments
			Signature signature = new Signature(args);
			Signature originalSignature = signature;
			// If the signature is not present in the registry
			if ( !ops.containsKey(signature) ) {
				temp_types.clear();
				// We collect all the signatures that are compatible
				for ( Map.Entry<Signature, IOperator> entry : ops.entrySet() ) {
					if ( signature.isCompatibleWith(entry.getKey()) ) {
						temp_types.add(entry.getKey());
					}
				}
				// No signature has been found, we throw an exception
				if ( temp_types.size() == 0 ) {
					context.error("No operator found for applying '" + op + "' to " + signature +
						" (operators available for " + Arrays.toString(ops.keySet().toArray()) + ")",
						IGamlIssue.UNMATCHED_OPERANDS);
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
						args[i] = createOperator(t.toString(), context, args[i]);
					}
				}
			}

			final IOperator helper = ops.get(signature);
			// We finally make a copy of the operator and init it with the arguments
			return helper.copy().init(op, context, args);
		}
		return null;

	}

	@Override
	public IExpression createAction(final String op, final IDescription callerContext,
		final StatementDescription action, final IExpression ... expressions) {
		return new PrimitiveOperator(op).init(op, callerContext, action, expressions[0], expressions[1]);
	}

	// @Override
	// public Set<String> parseLiteralArray(final IExpressionDescription s,
	// final IDescription context, final boolean skills) {
	// if ( s == null ) { return Collections.EMPTY_SET; }
	// return parser.parseLiteralArray(s, context, skills);
	// }

	@Override
	public IExpressionCompiler getParser() {
		return parser;
	}

}
