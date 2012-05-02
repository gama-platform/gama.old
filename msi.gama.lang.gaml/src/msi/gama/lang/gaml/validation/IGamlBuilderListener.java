/**
 * Created by drogoul, 2 mars 2012
 * 
 */
package msi.gama.lang.gaml.validation;

import java.util.Set;

/**
 * The class IGamlBuilder.
 * 
 * @author drogoul
 * @since 2 mars 2012
 * 
 */
public interface IGamlBuilderListener {

	void validationEnded(Set<String> experiments, boolean withErrors);
}
