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
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
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
import msi.gama.precompiler.IUnits;
import msi.gaml.descriptions.*;
import msi.gaml.types.*;

/**
 * The static class ExpressionFactory.
 * 
 * @author drogoul
 */

public class GamlExpressionFactory implements IExpressionFactory {

	public GamlExpressionFactory() {}

	public static IExpression TRUE_EXPR;
	public static IExpression FALSE_EXPR;
	public static IExpression WORLD_EXPR = null;

	static {
		IType bool = Types.get(IType.BOOL);
		TRUE_EXPR = new ConstantExpression(true, bool, bool);
		FALSE_EXPR = new ConstantExpression(false, bool, bool);
	}

	IExpressionCompiler parser;

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
		if ( type == Types.get(IType.SPECIES) ) { return new SpeciesConstantExpression(
			(String) val, type, contentType); }
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
	public IExpression createExpr(final IExpressionDescription s, final IDescription context) {
		if ( s == null ) { return null; }
		IExpression p = s.getExpression();
		return p == null ? parser.compile(s, context) : p;
	}

	@Override
	public IExpression createExpr(final String s, final IDescription context) {
		if ( s == null || s.isEmpty() ) { return null; }
		return parser.compile(new StringBasedExpressionDescription(s), context);
	}

	@Override
	public Map<String, IExpressionDescription> createArgumentMap(final IExpressionDescription args,
		final IDescription context) {
		if ( args == null ) { return Collections.EMPTY_MAP; }
		return parser.parseArguments(args, context);
	}

	@Override
	public IVarExpression createVar(final String name, final IType type, final IType contentType,
		final boolean isConst, final int scope, final IDescription definitionDescription) {
		switch (scope) {
			case IVarExpression.GLOBAL:
				return new GlobalVariableExpression(name, type, contentType, isConst,
					definitionDescription.getModelDescription().getWorldSpecies());
			case IVarExpression.AGENT:
				return new AgentVariableExpression(name, type, contentType, isConst,
					definitionDescription);
			case IVarExpression.TEMP:
				return new TempVariableExpression(name, type, contentType, definitionDescription);
			case IVarExpression.EACH:
				return new EachExpression(name, type, contentType);
			case IVarExpression.WORLD:
				return new WorldExpression(name, type, contentType,
					definitionDescription.getModelDescription());
			case IVarExpression.SELF:
				return new SelfExpression(name, type, contentType);
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
	public IExpression createOperator(final String op, final IDescription context,
		final IExpression ... args) {
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
					context.flagError(
						"No operator found for applying '" + op + "' to " + signature +
							" (operators available for " + Arrays.toString(ops.keySet().toArray()) +
							")", IGamlIssue.UNMATCHED_OPERANDS);
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
		// If no operator has been found, we throw an exception
		context.flagError("Operator " + op + " does not exist", IGamlIssue.UNKNOWN_UNARY, null, op);
		return null;

	}

	@Override
	public IExpression createAction(final String op, final IDescription context,
		final IExpression ... expressions) {
		return new PrimitiveOperator(op).init(op, context, expressions[0], expressions[1]);
	}

	@Override
	public Set<String> parseLiteralArray(final IExpressionDescription s, final IDescription context) {
		if ( s == null ) { return Collections.EMPTY_SET; }
		return parser.parseLiteralArray(s, context);
	}

	@Override
	public IExpressionCompiler getParser() {
		return parser;
	}

}
