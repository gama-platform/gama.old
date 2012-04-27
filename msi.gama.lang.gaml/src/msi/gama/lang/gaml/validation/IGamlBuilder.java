/**
 * Created by drogoul, 2 mars 2012
 * 
 */
package msi.gama.lang.gaml.validation;

import java.util.Set;
import msi.gama.kernel.model.IModel;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * The class IGamlBuilder.
 * 
 * @author drogoul
 * @since 2 mars 2012
 * 
 */
public interface IGamlBuilder {

	public static interface Listener {

		void validationEnded(Set<String> experiments, boolean withErrors);
	}

	IModel build(Resource xtextResource);

	void validate(Resource xtextResource);

	void removeListener(Listener listener);

	void addListener(URI resourceURI, Listener listener);

}
