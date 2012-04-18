/**
 * Created by drogoul, 2 mars 2012
 * 
 */
package msi.gama.common.interfaces;

import msi.gama.kernel.model.IModel;
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

		void validationEnded(Resource xtextResource);
	}

	IModel build(Resource xtextResource);

	void validate(Resource xtextResource);

	void addListener(Resource xtextResource, Listener listener);

	void removeListener(Listener listener);

}
