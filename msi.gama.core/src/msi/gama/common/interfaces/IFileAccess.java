/**
 * Created by drogoul, 20 déc. 2011
 * 
 */
package msi.gama.common.interfaces;

import msi.gama.precompiler.GamlProperties;
import msi.gaml.compilation.GamlException;
import org.osgi.framework.Bundle;

/**
 * The class IFileAccess.
 * 
 * @author drogoul
 * @since 20 déc. 2011
 * 
 */
public interface IFileAccess {

	GamlProperties getGamaProperties(final Bundle plugin, final String pathToAdditions,
		final String fileName) throws GamlException;

}
