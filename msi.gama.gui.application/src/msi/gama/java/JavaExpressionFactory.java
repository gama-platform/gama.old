/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.java;

import msi.gama.interfaces.*;
import msi.gama.internal.compilation.*;
import msi.gama.internal.expressions.*;
import msi.gama.kernel.exceptions.*;

public class JavaExpressionFactory implements IExpressionFactory {

	@Override
	public IExpression createConst(final Object val) {
		return new JavaConstExpression(val);
	}

	@Override
	public IExpression createConst(final Object val, final IType type) throws GamaRuntimeException {
		return createConst(type.cast(val));
	}

	@Override
	public IExpression createConst(final Object val, final IType type, final IType contentType)
		throws GamaRuntimeException {
		return createConst(val, type);
	}

	@Override
	public IExpression createExpr(final ExpressionDescription s) throws GamlException {
		return null;
		// We'll see later to create an expression based on the parsing of Java code. Although it's
		// not too difficult to imagine (see System.evaluateWith()).
	}

	@Override
	public IExpression createExpr(final ExpressionDescription s, final IDescription context)
		throws GamlException {
		return createExpr(s);
	}

	@Override
	public IVarExpression createVar(final String name, final IType type, final IType contentType,
		final boolean isConst, final int scope) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.internal.expressions.IExpressionFactory#createOperator(java.lang.String,
	 * boolean, boolean, msi.gama.internal.types.IType,
	 * msi.gama.internal.compilation.IOperatorExecuter, boolean, short, short, boolean)
	 */
	@Override
	public IOperator createOperator(final String name, final boolean binary, final boolean var,
		final IType returnType, final IOperatorExecuter helper, final boolean canBeConst,
		final short type, final short contentType, final boolean lazy) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * msi.gama.internal.expressions.IExpressionFactory#createPrimitiveOperator(java.lang.String)
	 */
	@Override
	public IOperator createPrimitiveOperator(final String name) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * msi.gama.internal.expressions.IExpressionFactory#copyPrimitiveOperatorForSpecies(msi.gama
	 * .internal.compilation.IOperator, msi.gama.internal.descriptions.IDescription)
	 */
	@Override
	public IOperator copyPrimitiveOperatorForSpecies(final IOperator op, final IDescription species) {
		return null;
	}

	/**
	 * @see msi.gama.internal.expressions.IExpressionFactory#createUnaryExpr(java.lang.String, msi.gama.interfaces.IExpression)
	 */
	public IExpression createUnaryExpr(final String op, final IExpression c) throws GamlException {
		return null;
	}

}
