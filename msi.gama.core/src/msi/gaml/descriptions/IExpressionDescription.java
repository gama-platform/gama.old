/**
 * Created by drogoul, 31 mars 2012
 * 
 */
package msi.gaml.descriptions;

import msi.gaml.expressions.*;

/**
 * The class IExpressionDescription.
 * 
 * @author drogoul
 * @since 31 mars 2012
 * 
 */
public interface IExpressionDescription {

	public abstract void setExpression(final IExpression expr);

	public abstract IExpression compile(final IDescription context, final IExpressionFactory factory);

	public abstract Object getAst();

	public abstract IExpression getExpression();

	public abstract void dispose();

	public abstract IExpressionDescription compileAsLabel();

	public abstract boolean equalsString(String o);

	public abstract void setAst(Object ast);

}