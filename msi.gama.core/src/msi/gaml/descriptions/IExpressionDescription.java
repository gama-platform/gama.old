/**
 * Created by drogoul, 31 mars 2012
 * 
 */
package msi.gaml.descriptions;

import java.util.Set;
import msi.gaml.expressions.IExpression;
import org.eclipse.emf.ecore.EObject;

/**
 * The class IExpressionDescription.
 * 
 * @author drogoul
 * @since 31 mars 2012
 * 
 */
public interface IExpressionDescription {

	public abstract void setExpression(final IExpression expr);

	public abstract IExpression compile(final IDescription context);

	public abstract IExpression getExpression();

	public abstract IExpressionDescription compileAsLabel();

	public abstract boolean equalsString(String o);

	public void dispose();

	public EObject getTarget();

	public void setTarget(EObject target);

	public boolean isConstant();

	public Set<String> getStrings(IDescription context, boolean skills);

	public abstract IExpressionDescription cleanCopy();

}