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

import static msi.gama.common.interfaces.IKeyword.EACH;
import static msi.gaml.expressions.IExpressionParser.*;
import java.util.*;
import msi.gaml.compilation.IOperatorExecuter;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.BinaryOperator.BinaryVarOperator;
import msi.gaml.factories.*;
import msi.gaml.types.*;

/**
 * The static class ExpressionFactory.
 * 
 * @author drogoul
 */

public class GamlExpressionFactory extends SymbolFactory implements IExpressionFactory {

	/**
	 * @param superFactory
	 */
	public GamlExpressionFactory(final ISymbolFactory superFactory) {
		super(superFactory);
	}

	public static IExpression NIL_EXPR;
	public static IExpression TRUE_EXPR;
	public static IExpression FALSE_EXPR;
	public static IVarExpression EACH_EXPR;
	public static IExpression WORLD_EXPR = null;

	static {
		IType bool = Types.get(IType.BOOL);
		IType none = Types.NO_TYPE;
		NIL_EXPR = new ConstantExpression(null, none, none);
		TRUE_EXPR = new ConstantExpression(true, bool, bool);
		FALSE_EXPR = new ConstantExpression(false, bool, bool);
		EACH_EXPR = new EachExpression(EACH, none, none);
	}

	// public static int cacheSize = 100;
	// public final static Map<Object, IExpression> cache = new HashMap(cacheSize);

	IExpressionParser parser;

	@Override
	public void registerParser(final IExpressionParser f) {
		parser = f;
		parser.setFactory(this);
	}

	@Override
	public IExpression createConst(final Object val, final IType type) {
		return createConst(val, type, type.defaultContentType());
	}

	@Override
	public IExpression createConst(final Object val, final IType type, final IType contentType) {
		if ( type == Types.get(IType.SPECIES) ) { return new SpeciesConstantExpression(
			(String) val, type, contentType); }
		// IExpression expr = cache.get(val);
		// if ( expr == null ) {
		// expr = new ConstantExpression(val, type, contentType);
		// cache.put(val, expr);
		// GuiUtils.debug("Constants cache size: " + cache.size());
		// }
		// return expr;
		return new ConstantExpression(val, type, contentType);
	}

	@Override
	public IExpression createExpr(final IExpressionDescription s, final IDescription context) {
		if ( s == null ) { return null; }
		IExpression p = s.getExpression();
		return p == null ? parser.parse(s, context) : p;
	}

	@Override
	public Map<String, IExpressionDescription> createArgumentMap(final IExpressionDescription args,
		final IDescription context) {
		if ( args == null ) { return Collections.EMPTY_MAP; }
		return parser.parseArguments(args, context);
	}

	@Override
	public IVarExpression createVar(final String name, final IType type, final IType contentType,
		final boolean isConst, final int scope) {
		switch (scope) {
			case IVarExpression.GLOBAL:
				return new GlobalVariableExpression(name, type, contentType, isConst);
			case IVarExpression.AGENT:
				return new AgentVariableExpression(name, type, contentType, isConst);
			case IVarExpression.TEMP:
				return new TempVariableExpression(name, type, contentType);
			case IVarExpression.EACH:
				return new EachExpression(name, type, contentType);
			case IVarExpression.WORLD:
				return new WorldExpression(name, type, contentType);
			case IVarExpression.SELF:
				return new SelfExpression(name, type, contentType);
			default:
				return null;
		}
	}

	public ListExpression createList(final List<? extends IExpression> elements) {
		return new ListExpression(elements);
	}

	public MapExpression createMap(final List<? extends IExpression> elements) {
		return new MapExpression(elements);
	}

	private final List<IType> temp_types = new ArrayList(10);

	@Override
	public IExpression createUnaryExpr(final String op, final IExpression c,
		final IDescription context) {
		IExpression child = c;

		if ( child == null ) {
			// context.flagError("Operand of '" + op + "' is malformed");
			return null;
		}
		if ( UNARIES.containsKey(op) ) {
			Map<IType, IOperator> ops = UNARIES.get(op);
			IType childType = child.type();
			IType originalChildType = childType;

			if ( !ops.containsKey(childType) ) {
				temp_types.clear();
				for ( Map.Entry<IType, IOperator> entry : ops.entrySet() ) {
					if ( entry.getKey().isAssignableFrom(childType) ) {
						temp_types.add(entry.getKey());
					}
				}
				if ( temp_types.size() == 0 ) {
					context.flagError("No operator found for applying '" + op + "' to " +
						childType + " (operators available for " +
						Arrays.toString(ops.keySet().toArray()) + ")");
					return null;
				}
				childType = temp_types.get(0);
				int dist = childType.distanceTo(originalChildType);
				for ( int i = 1, n = temp_types.size(); i < n; i++ ) {
					int d = temp_types.get(i).distanceTo(originalChildType);
					if ( d < dist ) {
						childType = temp_types.get(i);
						dist = d;
					}
				}
				IType coercingType = childType.coerce(child.type(), context);
				if ( coercingType != null ) {
					child = createUnaryExpr(coercingType.toString(), child, context);
				}
			}

			final IOperator helper = ops.get(childType);
			return helper.copy().init(op, child, null, context);
		}
		context.flagError("Unary operator " + op + " does not exist");
		return null;

	}

	private final List<TypePair> temp_pairs = new ArrayList(10);

	public IExpression createBinaryExpr(final String op, final IExpression l, final IExpression r,
		final IDescription context /* useful as a workaround for primitive operators */) {
		IExpression left = l;
		IExpression right = r;
		if ( left == null ) {
			// context.flagError("Left member of '" + op + "' is malformed");
			return null;
		}
		if ( right == null ) {
			// context.flagError("Right member of '" + op + "' is malformed");
			return null;
		}

		if ( BINARIES.containsKey(op) ) {
			// We get the possible pairs of types registered
			Map<TypePair, IOperator> map = BINARIES.get(op);
			IType leftType = left.type();
			IType rightType = right.type();
			temp_pairs.clear();
			// We filter the one(s) compatible with the operand types
			for ( TypePair p : map.keySet() ) {
				if ( p.isCompatibleWith(leftType, rightType) ) {
					temp_pairs.add(p);
				}
			}
			if ( temp_pairs.size() == 0 ) {
				// No pair is matching the operand types, we throw an exception
				context.flagError("No binary operator found for applying '" + op +
					"' to left operand " + leftType.toString() + " and " + rightType.toString() +
					" (operators available for " + map.keySet() + ")");
				return null;
			}
			// We gather the first one
			TypePair pair = temp_pairs.get(0);
			IOperator operator = map.get(pair);
			if ( temp_pairs.size() > 1 ) {
				// If multiple candidates are present, we choose the one with the smallest distance
				// to the operand types.
				int min = pair.distanceTo(leftType, rightType);
				for ( int i = 1, n = temp_pairs.size(); i < n; i++ ) {
					TypePair p = temp_pairs.get(i);
					int dist = p.distanceTo(leftType, rightType);
					if ( dist < min ) {
						min = dist;
						pair = p;
						operator = map.get(p);
					}
				}
			}
			// We make a copy of the operator object.
			operator = operator.copy();
			// We coerce the two expressions to closely match the pair of types declared
			IType coercingType = pair.left().coerce(left.type(), context);
			if ( coercingType != null ) {
				left = createUnaryExpr(coercingType.toString(), left, context);
			}
			coercingType = pair.right().coerce(right.type(), context);
			if ( coercingType != null ) {
				right = createUnaryExpr(coercingType.toString(), right, context);
			}
			// And we return it.
			return operator.init(op, left, right, context);
		}
		context.flagError("Operator: " + op + " does not exist");
		return null;
	}

	@Override
	public IOperator createOperator(final String name, final boolean binary, final boolean var,
		final IType returnType, final IOperatorExecuter helper, final boolean canBeConst,
		final short type, final short contentType, final boolean lazy) {
		IOperator op;
		if ( binary ) {
			if ( var ) {
				op = new BinaryVarOperator(returnType, helper, canBeConst, type, contentType, lazy);
			} else {
				op = new BinaryOperator(returnType, helper, canBeConst, type, contentType, lazy);
			}
		} else {
			op = new UnaryOperator(returnType, helper, canBeConst, type, contentType);
		}
		op.setName(name);
		return op;
	}

	@Override
	public List<String> parseLiteralArray(final IExpressionDescription s, final IDescription context) {
		if ( s == null ) { return new ArrayList(); }
		return parser.parseLiteralArray(s, context);
	}

	@Override
	public IExpressionParser getParser() {
		return parser;
	}

}
