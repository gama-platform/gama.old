/**
 * Created by drogoul, 20 déc. 2011
 * 
 */
package msi.gama.common.interfaces;

import msi.gama.precompiler.MultiProperties;
import msi.gaml.compilation.GamlException;

/**
 * The class IFileAccess.
 * 
 * @author drogoul
 * @since 20 déc. 2011
 * 
 */
public interface IFileAccess {

	MultiProperties getGamaProperties(final String fileName) throws GamlException;

}
