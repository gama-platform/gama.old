/**
 * Created by drogoul, 20 déc. 2011
 * 
 */
package msi.gama.gui.swt;

import java.io.*;
import msi.gama.common.interfaces.IFileAccess;
import msi.gama.precompiler.GamlProperties;
import msi.gaml.compilation.GamlException;
import org.eclipse.core.runtime.*;
import org.osgi.framework.Bundle;

/**
 * The class SwtIO.
 * 
 * @author drogoul
 * @since 20 déc. 2011
 * 
 */
public class SwtIO implements IFileAccess {

	@Override
	public GamlProperties getGamaProperties(final Bundle plugin, final String pathToAdditions,
		final String fileName) throws GamlException {
		try {
			InputStream inputStream =
				FileLocator.openStream(plugin, new Path(pathToAdditions + "/" + fileName), false);
			GamlProperties mp =
				GamlProperties.loadFrom(inputStream, plugin.getSymbolicName(), fileName);
			return mp;
		} catch (IOException e) {
			throw new GamlException("Impossible to locate the support file " + fileName +
				" for GAML");
		}
		// return new MultiProperties();
	}

}
