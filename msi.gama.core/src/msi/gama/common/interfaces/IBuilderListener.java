/**
 * Created by drogoul, 8 mars 2012
 * 
 */
package msi.gama.common.interfaces;

import msi.gama.kernel.model.IModel;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * The class IBuilderListener.
 * 
 * @author drogoul
 * @since 8 mars 2012
 * 
 */
public interface IBuilderListener {

	void beforeBuilding(Resource resource);

	void afterBuilding(Resource resource, IModel model);

}
