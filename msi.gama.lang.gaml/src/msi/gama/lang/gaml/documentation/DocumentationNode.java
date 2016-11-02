/*********************************************************************************************
 *
 * 'DocumentationNode.java, in plugin msi.gama.lang.gaml, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.documentation;

import java.io.IOException;

import msi.gama.common.interfaces.IGamlDescription;
import msi.gama.lang.gaml.documentation.GamlResourceDocumenter.StringCompressor;
import msi.gama.precompiler.GamlProperties;

public class DocumentationNode implements IGamlDescription {

	final byte[] title;
	final byte[] doc;
	// final byte[] plugin;

	DocumentationNode(final IGamlDescription desc) throws IOException {

		final String plugin = desc.getDefiningPlugin();
		final String title = desc.getTitle();
		String documentation = desc.getDocumentation();
		if (plugin != null) {
			documentation += "\n<p/><i> [defined in " + plugin + "] </i>";
		}
		doc = StringCompressor.compress(documentation);
		this.title = StringCompressor.compress(title);
	}

	/**
	 * Method collectMetaInformation()
	 * 
	 * @see msi.gama.common.interfaces.IGamlDescription#collectPlugins(java.util.Set)
	 */
	@Override
	public void collectMetaInformation(final GamlProperties meta) {
	}

	@Override
	public String getDocumentation() {
		return StringCompressor.decompress(doc);
	}

	@Override
	public String getTitle() {
		return StringCompressor.decompress(title);
	}

	@Override
	public String getName() {
		return "Online documentation";
	}

	@Override
	public String getDefiningPlugin() {
		return "";
	}

	@Override
	public void setName(final String name) {
		// Nothing
	}

	@Override
	public String toString() {
		return getTitle() + " - " + getDocumentation();
	}

	/**
	 * Method serialize()
	 * 
	 * @see msi.gama.common.interfaces.IGamlable#serialize(boolean)
	 */
	@Override
	public String serialize(final boolean includingBuiltIn) {
		return toString();
	}

}