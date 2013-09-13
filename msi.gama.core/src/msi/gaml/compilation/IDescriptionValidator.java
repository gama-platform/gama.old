/**
 * Created by drogoul, 13 sept. 2013
 * 
 */
package msi.gaml.compilation;

import msi.gaml.descriptions.IDescription;

/**
 * Class IDescriptionValidator. This interface is intended to be used for individual validation of symbols. An instance
 * is typically known by a SymbolProto and called after the core of the validation has made its job.
 * 
 * @author drogoul
 * @since 13 sept. 2013
 * 
 */
public interface IDescriptionValidator {

	/**
	 * Called at the end of the validation process. The enclosing description, the children and the facets of the
	 * description have all been already validated (and their expressions compiled), so everything is accessible here to
	 * make a finer validation with respect to the specificites of the symbol. This interface is not supposed to change
	 * the description unless it is absolutely necessary. It is supposed to attach warnings and errors to the
	 * description instead.
	 * @param description
	 */
	public void validate(IDescription description);
}
