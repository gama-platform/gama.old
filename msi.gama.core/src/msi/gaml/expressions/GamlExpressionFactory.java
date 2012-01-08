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
import msi.gama.common.interfaces.*;
import msi.gama.precompiler.GamlAnnotations.handles;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.BinaryOperator.BinaryVarOperator;
import msi.gaml.factories.SymbolFactory;
import msi.gaml.types.*;

/**
 * The static class ExpressionFactory.
 * 
 * @author drogoul
 */
@handles({ ISymbolKind.GAML_LANGUAGE })
@symbol(name = IKeyword.GAML, kind = ISymbolKind.GAML_LANGUAGE)
public class GamlExpressionFactory extends SymbolFactory implements IExpressionFactory, ISymbol {

	IExpressionParser parser;
	final IDescription defaultParsingContext;

	public GamlExpressionFactory(final IDescription context) {
		registerParser(new GamlExpressionParser());
		defaultParsingContext = context;
	}

	public GamlExpressionFactory() {
		registerParser(new GamlExpressionParser()); // default
		defaultParsingContext = null;
	}

	public void registerParser(final IExpressionParser f) {
		// f is normally the default expression parser to use.
		parser = f;
		parser.setFactory(this);

	}

	public IDescription getDefaultParsingContext() {
		return defaultParsingContext;
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
	public IExpression createExpr(final ExpressionDescription s) throws GamlException {
		return createExpr(s, defaultParsingContext);
	}

	@Override
	public IExpression createExpr(final ExpressionDescription s, final IDescription context)
		throws GamlException {
		if ( s == null || s.length() == 0 ) { return null; }
		IExpression p = null;
		try {
			p = parser.parse(s, context);
		} catch (final GamlException e) {
			e.addContext("in expression : " + s);
			throw e;
		}
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
	public IExpression createUnaryExpr(final String op, final IExpression c) throws GamlException {
		IExpression child = c;

		if ( child == null ) { throw new GamlException("Operand of '" + op + "' is malformed"); }
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
				if ( types.size() == 0 ) { throw new GamlException(
					"No operator found for applying '" + op + "' to " + childType +
						" (operators available for " + Arrays.toString(ops.keySet().toArray()) +
						")"); }
				childType = types.get(0);
				int dist = childType.distanceTo(originalChildType);
				for ( int i = 1, n = types.size(); i < n; i++ ) {
					int d = types.get(i).distanceTo(originalChildType);
					if ( d < dist ) {
						childType = types.get(i);
						dist = d;
					}
				}
				IType coercingType = childType.coerce(child.type());
				if ( coercingType != null ) {
					child = createUnaryExpr(coercingType.toString(), child);
				}
			}

			final IOperator helper = ops.get(childType);
			// if ( originalChildType != childType ) {
			// ops.put(originalChildType, helper);
			// }
			return helper.copy().init(op, child, null);
		}
		throw new GamlException("Unary operator " + op + " does not exist");

	}

	public IExpression createBinaryExpr(final String op, final IExpression l, final IExpression r,
		final IDescription context /* useful as a workaround for primitive operators */)
		throws GamlException {
		IExpression left = l;
		IExpression right = r;
		if ( left == null ) { throw new GamlException("Left member of '" + op + "' is malformed"); }
		if ( right == null ) { throw new GamlException("Right member of '" + op + "' is malformed"); }

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
				throw new GamlException("No binary operator found for applying '" + op +
					"' to left operand " + leftType.toString() + " and " + rightType.toString() +
					" (operators available for " + map.keySet() + ")");
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
			IType coercingType = pair.left().coerce(left.type());
			if ( coercingType != null ) {
				left = createUnaryExpr(coercingType.toString(), left);
			}
			coercingType = pair.right().coerce(right.type());
			if ( coercingType != null ) {
				right = createUnaryExpr(coercingType.toString(), right);
			}
			// We add the species context in case this operator is a primitive
			if ( operator instanceof PrimitiveOperator ) {
				((PrimitiveOperator) operator).setTargetSpecies(context.getSpeciesContext());
			}
			// And we return it.
			return operator.init(op, left, right);
		}
		throw new GamlException("Operator: " + op + " does not exist");
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "GAML Expression Factory";
	}

	@Override
	public void setName(final String newName) {

	}

	@Override
	public IExpression getFacet(final String key) {
		return null;
	}

	@Override
	public boolean hasFacet(final String key) {
		return false;
	}

	@Override
	public void setChildren(final List<? extends ISymbol> commands) throws GamlException {

	}

	@Override
	public IDescription getDescription() {
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

	@Override
	public void dispose() {}
}
