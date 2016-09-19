package msi.gama.util.file;

import org.eclipse.emf.common.util.URI;

import msi.gaml.compilation.ISyntacticElement;

public interface IGamlResourceInfoProvider {

	public GamlFileInfo getInfo(final URI uri, final long stamp);

	public ISyntacticElement getContents(URI uri);

}
