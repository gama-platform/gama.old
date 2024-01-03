/*******************************************************************************************************
 *
 * IDocManager.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.common.interfaces;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

import msi.gaml.descriptions.ModelDescription;
import msi.gaml.interfaces.IGamlDescription;

/**
 * The Interface IDocManager.
 */
// Internal interface instantiated by XText
public interface IDocManager {

	/** The null. */
	IDocManager NULL = new NullImpl();

	/**
	 * The Class DocumentationNode.
	 */
	record DocumentationNode(String title, byte[] doc) implements IGamlDescription {

		/**
		 * Compress.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param data
		 *            the data
		 * @return the byte[]
		 * @throws IOException
		 *             Signals that an I/O exception has occurred.
		 * @date 3 janv. 2024
		 */
		public static byte[] compress(final String data) {
			try (ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length());
					GZIPOutputStream gzip = new GZIPOutputStream(bos)) {
				gzip.write(data.getBytes());
				return bos.toByteArray();
			} catch (IOException e) {
				return null;
			}
		}

		/**
		 * Decompress.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param data
		 *            the data
		 * @return the string
		 * @throws IOException
		 *             Signals that an I/O exception has occurred.
		 * @date 3 janv. 2024
		 */
		public static String decompress(final byte[] data) {
			try (ByteArrayInputStream bos = new ByteArrayInputStream(data);
					GZIPInputStream gzip = new GZIPInputStream(bos)) {
				return new String(gzip.readAllBytes());
			} catch (IOException e) {
				return null;
			}
		}

		/**
		 * Instantiates a new documentation node.
		 *
		 * @param desc
		 *            the desc
		 * @throws IOException
		 *             Signals that an I/O exception has occurred.
		 */
		public DocumentationNode(final IGamlDescription desc) {
			this(desc.getTitle(), compress(desc.getDocumentation().toString()));
		}

		/**
		 * Gets the documentation.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @return the documentation
		 * @date 30 déc. 2023
		 */
		@Override
		public Doc getDocumentation() {
			return new SimpleDoc() {

				@Override
				public String get() {
					return decompress(doc);
				}

			};
		}

		/**
		 * Gets the title.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @return the title
		 * @date 30 déc. 2023
		 */
		@Override
		public String getTitle() { return title; }

	}

	/**
	 * The Class NullImpl.
	 */
	public static class NullImpl implements IDocManager {

		/**
		 * Do document.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param description
		 *            the description
		 * @date 31 déc. 2023
		 */
		@Override
		public void doDocument(final URI uri, final ModelDescription description,
				final Map<EObject, IGamlDescription> additionalExpressions) {}

		@Override
		public IGamlDescription getGamlDocumentation(final EObject o) {
			return null;
		}

		/**
		 * Sets the gaml documentation.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param object
		 *            the object
		 * @param description
		 *            the description
		 * @param replace
		 *            the replace
		 * @param force
		 *            the force
		 * @date 29 déc. 2023
		 */
		@Override
		public void setGamlDocumentation(final URI openResource, final EObject object,
				final IGamlDescription description) {}

		@Override
		public void invalidate(final URI key) {}

	}

	/**
	 * Document. Should be called after validation. Validates both the statements (from ModelDescription) and the
	 * expressions (Map)
	 *
	 * @param description
	 *            the description
	 * @param additionalExpressions
	 */
	void doDocument(URI resource, ModelDescription description, Map<EObject, IGamlDescription> additionalExpressions);

	/**
	 * Gets the gaml documentation.
	 *
	 * @param o
	 *            the o
	 * @return the gaml documentation
	 */
	IGamlDescription getGamlDocumentation(EObject o);

	/**
	 * Sets the gaml documentation.
	 *
	 * @param object
	 *            the object
	 * @param description
	 *            the description
	 * @param replace
	 *            the replace
	 * @param force
	 *            the force
	 */
	void setGamlDocumentation(URI openResource, final EObject object, final IGamlDescription description);

	/**
	 * Invalidate.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param key
	 *            the key
	 * @date 29 déc. 2023
	 */
	void invalidate(URI key);

}