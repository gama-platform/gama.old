/*********************************************************************************************
 *
 * 'DocumentationNode.java, in plugin msi.gama.lang.gaml, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.documentation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import msi.gama.common.interfaces.IGamlDescription;

public class DocumentationNode implements IGamlDescription {

	public static byte[] compress(final String text) throws IOException {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (final OutputStream out = new DeflaterOutputStream(baos, true);) {
			out.write(text.getBytes("ISO-8859-1"));
		}
		return baos.toByteArray();
	}

	public static String decompress(final byte[] bytes) throws IOException {
		final InputStream in = new InflaterInputStream(new ByteArrayInputStream(bytes));
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final byte[] buffer = new byte[8192];
		int len;
		while ((len = in.read(buffer)) > 0) {
			baos.write(buffer, 0, len);
		}
		return new String(baos.toByteArray(), "ISO-8859-1");
	}

	final byte[] title;
	final byte[] doc;

	DocumentationNode(final IGamlDescription desc) throws IOException {

		final String plugin = desc.getDefiningPlugin();
		final String title = desc.getTitle();
		String documentation = desc.getDocumentation();
		if (plugin != null) {
			documentation += "\n<p/><i> [defined in " + plugin + "] </i>";
		}
		doc = compress(documentation);
		this.title = compress(title);
	}

	/**
	 * Method collectMetaInformation()
	 *
	 * @see msi.gama.common.interfaces.IGamlDescription#collectPlugins(java.util.Set)
	 */
	// @Override
	// public void collectMetaInformation(final GamlProperties meta) {}

	@Override
	public String getDocumentation() {
		try {
			return decompress(doc);
		} catch (final IOException e) {
			e.printStackTrace();
			return "Error";
		}
	}

	@Override
	public String getTitle() {
		try {
			return decompress(title);
		} catch (final IOException e) {
			e.printStackTrace();
			return "Error";
		}
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