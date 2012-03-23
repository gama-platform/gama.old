package msi.gama.headless.io;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Bundle;

import msi.gama.common.interfaces.IFileAccess;
import msi.gama.precompiler.GamlProperties;
import msi.gama.runtime.exceptions.GamaStartupException;

public class HeadlessIO implements IFileAccess {
	@Override
	public GamlProperties getGamaProperties(final Bundle plugin, final String pathToAdditions,
		final String fileName) throws GamaStartupException {
		try {
			InputStream inputStream =
				FileLocator.openStream(plugin, new Path(pathToAdditions + "/" + fileName), false);
			GamlProperties mp =
				GamlProperties.loadFrom(inputStream, plugin.getSymbolicName(), fileName);
			return mp;
		} catch (IOException e) {
			throw new GamaStartupException("Impossible to locate the support file " + fileName +
				" for GAML", e);
		}
		// return new MultiProperties();
	}

}
