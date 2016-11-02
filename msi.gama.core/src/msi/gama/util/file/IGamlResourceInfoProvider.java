/*********************************************************************************************
 *
 * 'IGamlResourceInfoProvider.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.util.file;

import org.eclipse.emf.common.util.URI;

import msi.gaml.compilation.ast.ISyntacticElement;

public interface IGamlResourceInfoProvider {

	public GamlFileInfo getInfo(final URI uri, final long stamp);

	public ISyntacticElement getContents(URI uri);

}
