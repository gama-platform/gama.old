/**
 * Created by drogoul, 15 avr. 2014
 * 
 */
package msi.gaml.compilation;

import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

import msi.gama.kernel.model.IModel;
import msi.gama.util.file.GAMLFile;
import msi.gaml.descriptions.ModelDescription;

/**
 * Class IModelBuilder.
 * 
 * @author drogoul
 * @since 15 avr. 2014
 * 
 */

public interface IModelBuilder {

	/**
	 * Validates the GAML model inside a resource or an URI and returns an
	 * ErrorCollector (which can later be probed for internal errors, imported
	 * errors, warnings or infos)
	 * 
	 * @param resource
	 *            must not be null
	 * @return an instance of ErrorCollector (never null)
	 */

	/**
	 * Builds an IModel from the resource or its URI
	 * 
	 * @param resource
	 *            must not be null
	 * @return an instance of IModel or null if the validation has returned
	 *         errors (use validate(GamlResource) to retrieve them if it is the
	 *         case, or use the alternate form).
	 */
	public abstract IModel compile(Resource resource);

	/**
	 * Builds an IModel from a resource, an URI or an InputStream, listing all
	 * the errors, warnings and infos that occured
	 * 
	 * @param resource
	 *            must not be null
	 * @param a
	 *            list of errors, warnings and infos that occured during the
	 *            build. Must not be null and must accept the addition of new
	 *            elements
	 * @return an instance of IModel or null if the validation has returned
	 *         errors.
	 */
	public abstract IModel compile(Resource resource, List<GamlCompilationError> errors);

	ModelDescription buildModelDescription(URI uri, List<GamlCompilationError> errors);

	public abstract GAMLFile.GamlInfo getInfo(URI uri, long modificationStamp);

	// hqnghi 11/Oct/13 two method for compiling models directly from files
	public abstract ModelDescription createModelDescriptionFromFile(String filepath);

}