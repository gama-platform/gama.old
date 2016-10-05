/**
 * Created by drogoul, 27 mai 2015
 *
 */
package msi.gama.common.interfaces;

import java.util.List;
import java.util.Map;

import msi.gama.runtime.IScope;
import msi.gaml.statements.Arguments;
import msi.gaml.statements.CreateStatement;
import msi.gaml.types.IType;

/**
 * Class ICreateDelegate.
 *
 * @author drogoul
 * @since 27 mai 2015
 *
 */
public interface ICreateDelegate {

	/**
	 * Returns whether or not this delegate accepts to create agents from this
	 * source.
	 * 
	 * @param source
	 * @return
	 */

	boolean acceptSource(Object source);

	/**
	 * Fills the list of maps with the initial values read from the source.
	 * Returns true if all the inits have been correctly filled
	 * 
	 * @param scope
	 * @param inits
	 * @param max
	 *            can be null (in that case, the maximum number of agents to
	 *            create is ignored)
	 * @param source
	 * @return
	 */

	@SuppressWarnings("rawtypes")
	boolean createFrom(IScope scope, List<Map> inits, Integer max, Object source, Arguments init,
			CreateStatement statement);

	/**
	 * Returns the type expected in the 'from:' facet of 'create' statement.
	 * Should not be null and should be different from IType.NO_TYPE (in order
	 * to be able to check the validity of create statements at compile time-
	 * 
	 * @return a GAML type representing the type of the source expected by this
	 *         ICreateDelegate
	 */
	@SuppressWarnings("rawtypes")
	IType fromFacetType();

}
