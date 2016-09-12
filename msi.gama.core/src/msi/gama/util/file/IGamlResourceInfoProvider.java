package msi.gama.util.file;

import org.eclipse.emf.common.util.URI;

public interface IGamlResourceInfoProvider {

	public GamlFileInfo getInfo(final URI uri, final long stamp);

}
