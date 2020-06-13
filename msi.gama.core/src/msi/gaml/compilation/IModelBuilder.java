/*******************************************************************************************************
 *
 * msi.gaml.compilation.IModelBuilder.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.compilation;

import java.util.List;

import org.eclipse.emf.common.util.URI;

import msi.gama.kernel.model.IModel;

/**
 * Class IModelBuilder.
 * 
 * @author drogoul
 * @since 15 avr. 2014
 * 
 */

public interface IModelBuilder {

	/**
	 * Builds an IModel an URI , listing all the errors, warnings and infos that
	 * occured
	 * 
	 * @param uri
	 *            must not be null
	 * @param errors
	 *            list of errors, warnings and infos that occured during the
	 *            build. Must not be null and must accept the addition of new
	 *            elements
	 * @return an instance of IModel or null if the validation has returned
	 *         errors.
	 */

	public abstract IModel compile(URI uri, List<GamlCompilationError> errors);

}