/**
 * Created by drogoul, 7 sept. 2013
 * 
 */
package msi.gaml.expressions;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.IType;

/**
 * Class CastingExpression.
 * 
 * @author drogoul
 * @since 7 sept. 2013
 * 
 */
public class CastingExpression extends AbstractExpression {

	// final IType type;

	public CastingExpression(final IType type) {
		this.type = type;
	}

	/**
	 * Method value()
	 * @see msi.gaml.expressions.IExpression#value(msi.gama.runtime.IScope)
	 */
	@Override
	public Object value(final IScope scope) throws GamaRuntimeException {
		return null;
	}

	/**
	 * Method isConst()
	 * @see msi.gaml.expressions.IExpression#isConst()
	 */
	@Override
	public boolean isConst() {
		return true;
	}

	/**
	 * Method toGaml()
	 * @see msi.gaml.expressions.IExpression#toGaml()
	 */
	@Override
	public String toGaml() {
		return type.toString();
	}

	/**
	 * Method getTitle()
	 * @see msi.gaml.descriptions.IGamlDescription#getTitle()
	 */
	@Override
	public String getTitle() {
		return null;
	}

	/**
	 * Method getDocumentation()
	 * @see msi.gaml.descriptions.IGamlDescription#getDocumentation()
	 */
	@Override
	public String getDocumentation() {
		return null;
	}

	@Override
	public IType getType() {
		return type;
	}

}
