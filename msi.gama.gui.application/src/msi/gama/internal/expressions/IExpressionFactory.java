/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC 
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.internal.expressions;

import msi.gama.interfaces.*;
import msi.gama.internal.compilation.*;
import msi.gama.internal.expressions.*;
import msi.gama.kernel.exceptions.*;

/**
 * Written by drogoul Modified on 27 déc. 2010
 * 
 * @todo Description
 * 
 */
public interface IExpressionFactory {

	public abstract IExpression createConst(final Object val) throws GamaRuntimeException;

	public abstract IExpression createConst(final Object val, final IType type)
		throws GamaRuntimeException;

	public abstract IExpression createConst(final Object val, final IType type,
		final IType contentType) throws GamaRuntimeException;

	public abstract IExpression createExpr(final ExpressionDescription s) throws GamlException;

	public abstract IExpression createExpr(final ExpressionDescription s, final IDescription context)
		throws GamlException;

	public abstract IVarExpression createVar(final String name, final IType type,
		final IType contentType, final boolean isConst, final int scope);

	public abstract IOperator createOperator(final String name, final boolean binary,
		final boolean var, final IType returnType, final IOperatorExecuter helper,
		final boolean canBeConst, final short type, final short contentType, final boolean lazy);

	public abstract IOperator createPrimitiveOperator(final String name);

	public abstract IOperator copyPrimitiveOperatorForSpecies(IOperator op, IDescription species);

	public abstract IExpression createUnaryExpr(final String op, final IExpression c) throws GamlException;

}