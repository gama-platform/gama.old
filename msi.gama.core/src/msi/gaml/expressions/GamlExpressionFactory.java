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

import static msi.gaml.expressions.IExpressionParser.*;
import java.util.*;
import msi.gama.common.interfaces.IValue;
import msi.gama.runtime.GAMA;
import msi.gaml.compilation.IOperatorExecuter;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.BinaryOperator.BinaryVarOperator;
import msi.gaml.factories.SymbolFactory;
import msi.gaml.types.*;

/**
 * The static class ExpressionFactory.
 * 
 * @author drogoul
 */

public class GamlExpressionFactory extends SymbolFactory implements IExpressionFactory {

	// FIXME HACK to test the new parser

	// private IExpressionParser NEW_PARSER;
	//
	// public void REGISTER_NEW_PARSER(final IExpressionParser p) {
	// NEW_PARSER = p;
	// p.setFactory(this);
	// }
	//
	// public IExpression PARSE_STRING(final String s) {
	// if ( NEW_PARSER == null ) { return null; }
	// return NEW_PARSER.parse(new ExpressionDescription(s, false), null);
	// }
	//
	// FIXME HACK

	IExpressionParser parser;

	// final IDescription defaultParsingContext;

	public GamlExpressionFactory() {
		registerParser(new GamlExpressionParser()); // default
	}

	@Override
	public void registerParser(final IExpressionParser f) {
		parser = f;
		parser.setFactory(this);

	}

	@Override
	public IExpression createConst(final Object val) {
		if ( val == null ) { return createConst((Object) null, Types.NO_TYPE); }
		IType type = val instanceof IValue ? ((IValue) val).type() : Types.get(val.getClass());
		return createConst(val, type);
	}

	@Override
	public IExpression createConst(final Object val, final IType type) {
		return createConst(val, type, type.defaultContentType());
	}

	@Override
	public IExpression createConst(final Object val, final IType type, final IType contentType) {
		if ( type == Types.get(IType.SPECIES) ) { return new SpeciesConstantExpression(val, type,
			contentType); }
		return new ConstantExpression(val, type, contentType);
	}

	@Override
	public IExpression createExpr(final ExpressionDescription s) {
		return createExpr(s, GAMA.getModelContext());
	}

	@Override
	public IExpression createExpr(final ExpressionDescription s, final IDescription context) {
		// FIXME HACK TO TEST THE NEW PARSER
		// if ( NEW_PARSER != null ) { return NEW_PARSER.parse(s, context); }
		if ( s == null || s.size() == 0 ) { return null; }
		// FIXME HACK TO TEST THE NEW PARSER
		// if ( s.getAst() != null && NEW_PARSER != null ) { return NEW_PARSER.parse(s, context); }
		// HACK
		IExpression p = parser.parse(s, context);
		return p;
	}

	@Override
	public IVarExpression createVar(final String name, final IType type, final IType contentType,
		final boolean isConst, final int scope) {
		if ( scope == IVarExpression.GLOBAL ) { return new GlobalVariableExpression(name, type,
			contentType, isConst); }
		if ( scope == IVarExpression.AGENT ) { return new AgentVariableExpression(name, type,
			contentType, isConst); }
		if ( scope == IVarExpression.TEMP ) { return new TempVariableExpression(name, type,
			contentType); }
		if ( scope == IVarExpression.EACH ) { return new EachExpression(name, type, contentType); }
		if ( scope == IVarExpression.WORLD ) { return new WorldExpression(name, type, contentType); }
		if ( scope == IVarExpression.SELF ) { return new SelfExpression(name, type, contentType); }
		return null;
	}

	public ListExpression createList(final List<? extends IExpression> elements) {
		IType contentType = Types.NO_TYPE;
		boolean allTheSame = true;
		int n = elements.size();
		if ( n != 0 ) {
			contentType = elements.get(0).type();
			for ( int i = 1; i < n; i++ ) {
				allTheSame = elements.get(i).type() == contentType;
				if ( !allTheSame ) {
					break;
				}
			}
		}
		ListExpression list = new ListExpression(elements);
		if ( allTheSame ) {
			list.setContentType(contentType);
		}
		return list;
	}

	public MapExpression createMap(final List<? extends IExpression> elements) {
		MapExpression map = new MapExpression(elements);
		return map;
	}

	@Override
	public IExpression createUnaryExpr(final String op, final IExpression c,
		final IDescription context) {
		IExpression child = c;

		if ( child == null ) {
			context.flagError("Operand of '" + op + "' is malformed");
			return null;
		}
		if ( UNARIES.containsKey(op) ) {
			Map<IType, IOperator> ops = UNARIES.get(op);
			IType childType = child.type();
			IType originalChildType = childType;

			if ( !ops.containsKey(childType) ) {
				List<IType> types = new ArrayList();
				for ( Map.Entry<IType, IOperator> entry : ops.entrySet() ) {
					if ( entry.getKey().isAssignableFrom(childType) ) {
						types.add(entry.getKey());
					}
				}
				if ( types.size() == 0 ) {
					context.flagError("No operator found for applying '" + op + "' to " +
						childType + " (operators available for " +
						Arrays.toString(ops.keySet().toArray()) + ")");
					return null;
				}
				childType = types.get(0);
				int dist = childType.distanceTo(originalChildType);
				for ( int i = 1, n = types.size(); i < n; i++ ) {
					int d = types.get(i).distanceTo(originalChildType);
					if ( d < dist ) {
						childType = types.get(i);
						dist = d;
					}
				}
				IType coercingType = childType.coerce(child.type(), context);
				if ( coercingType != null ) {
					child = createUnaryExpr(coercingType.toString(), child, context);
				}
			}

			final IOperator helper = ops.get(childType);
			// if ( originalChildType != childType ) {
			// ops.put(originalChildType, helper);
			// }
			return helper.copy().init(op, child, null, context);
		}
		context.flagError("Unary operator " + op + " does not exist");
		return null;

	}

	public IExpression createBinaryExpr(final String op, final IExpression l, final IExpression r,
		final IDescription context /* useful as a workaround for primitive operators */) {
		IExpression left = l;
		IExpression right = r;
		if ( left == null ) {
			context.flagError("Left member of '" + op + "' is malformed");
			return null;
		}
		if ( right == null ) {
			context.flagError("Right member of '" + op + "' is malformed");
			return null;
		}

		if ( BINARIES.containsKey(op) ) {
			// We get the possible pairs of types registered
			Map<TypePair, IOperator> map = BINARIES.get(op);
			IType leftType = left.type();
			IType rightType = right.type();
			List<TypePair> list = new ArrayList();
			// We filter the one(s) compatible with the operand types
			for ( TypePair p : map.keySet() ) {
				if ( p.isCompatibleWith(leftType, rightType) ) {
					list.add(p);
				}
			}
			if ( list.size() == 0 ) {
				// No pair is matching the operand types, we throw an exception
				context.flagError("No binary operator found for applying '" + op +
					"' to left operand " + leftType.toString() + " and " + rightType.toString() +
					" (operators available for " + map.keySet() + ")");
				return null;
			}
			// We gather the first one
			TypePair pair = list.get(0);
			IOperator operator = map.get(pair);
			if ( list.size() > 1 ) {
				// If multiple candidates are present, we choose the one with the smallest distance
				// to the operand types.
				int min = pair.distanceTo(leftType, rightType);
				for ( int i = 1, n = list.size(); i < n; i++ ) {
					TypePair p = list.get(i);
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
			// We add the species context in case this operator is a primitive
			if ( operator instanceof PrimitiveOperator ) {
				((PrimitiveOperator) operator).setTargetSpecies(context.getSpeciesContext());
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
	public IOperator createPrimitiveOperator(final String name) {
		return new PrimitiveOperator(name);
	}

	@Override
	public IOperator copyPrimitiveOperatorForSpecies(final IOperator op, final IDescription species) {
		IOperator copy = op.copy();
		((PrimitiveOperator) copy).setTargetSpecies(species);
		return copy;
	}

}
