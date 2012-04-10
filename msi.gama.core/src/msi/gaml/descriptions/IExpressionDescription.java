/**
 * Created by drogoul, 31 mars 2012
 * 
 */
package msi.gaml.descriptions;

import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.expressions.*;

/**
 * The class IExpressionDescription.
 * 
 * @author drogoul
 * @since 31 mars 2012
 * 
 */
public interface IExpressionDescription /* extends IExpression */{

	public abstract void setExpression(final IExpression expr);

	public abstract IExpression compile(final IDescription context, final IExpressionFactory factory)
		throws GamaRuntimeException;

	public abstract Object getAst();

	public abstract IExpression getExpression();

	public abstract void dispose();

	public abstract IExpressionDescription compileAsLabel();

	/**
	 * @param o
	 * @return
	 */
	public abstract boolean equalsString(String o);

}