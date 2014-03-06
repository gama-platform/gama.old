/**
 * Created by drogoul, 28 janv. 2014
 * 
 */
package msi.gaml.types;

import msi.gama.runtime.IScope;
import msi.gama.util.IContainer;
import msi.gaml.expressions.IExpression;

/**
 * Class IContainerType.
 * 
 * @author drogoul
 * @since 28 janv. 2014
 * 
 */
public interface IContainerType<T extends IContainer> extends IType<T> {

	@Override
	public IContainerType getType();

	@Override
	public IContainerType typeIfCasting(final IExpression exp);

	@Override
	public T cast(IScope scope, Object obj, Object param);

	@Override
	public T cast(IScope scope, Object obj, Object param, IType keyType, IType contentType);

}
