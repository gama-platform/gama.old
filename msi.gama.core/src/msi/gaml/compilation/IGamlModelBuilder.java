/*******************************************************************************************************
 *
 * IGamlModelBuilder.java, in msi.gama.lang.gaml, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.compilation;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.eclipse.emf.common.util.URI;

import msi.gama.kernel.model.IModel;
import msi.gama.precompiler.GamlProperties;

/**
 * The Interface IGamlModelBuilder.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 15 oct. 2023
 */
public interface IGamlModelBuilder {

	/**
	 * Load UR ls.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param URLs
	 *            the UR ls
	 * @date 15 oct. 2023
	 */
	void loadURLs(final List<URL> URLs);

	/**
	 * Compile.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param uri
	 *            the uri
	 * @param errors
	 *            the errors
	 * @return the i model
	 * @date 15 oct. 2023
	 */
	IModel compile(final URI uri, final List<GamlCompilationError> errors);

	/**
	 * Compiles a file to a GAMA model ready to be experimented
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param myFile
	 *            the my file
	 * @param errors
	 *            a list that will be filled with compilation errors / warnings (can be null)
	 * @param metaProperties
	 *            an instance of GamlProperties that will be filled with the sylmbolic names of bundles required to run
	 *            the model (can be null) and other informations (skills, operators, statements, ...).
	 * @return the compiled model or null if errors occur
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws IllegalArgumentException
	 *             Signals that errors occured
	 * @date 15 oct. 2023
	 */
	IModel compile(final File myFile, final List<GamlCompilationError> errors, final GamlProperties metaProperties)
			throws IOException, IllegalArgumentException;

	/**
	 * Compile.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param url
	 *            the url
	 * @param errors
	 *            the errors
	 * @return the i model
	 * @date 15 oct. 2023
	 */
	IModel compile(final URL url, final List<GamlCompilationError> errors);

}
