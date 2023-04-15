/*******************************************************************************************************
 *
 * DocumentationTask.java, in msi.gama.lang.gaml, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.lang.gaml.documentation;

import org.eclipse.emf.ecore.EObject;

import msi.gama.common.interfaces.IGamlDescription;
import msi.gama.lang.gaml.resource.GamlResource;
import msi.gama.util.IMap;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class DocumentationTask.
 */
class DocumentationTask {

	/** The object. */
	final EObject object;

	/** The description. */
	final IGamlDescription description;

	/** The documenter. */
	final GamlResourceDocumenter documenter;

	/**
	 * Instantiates a new documentation task.
	 *
	 * @param object
	 *            the object
	 * @param description
	 *            the description
	 * @param documenter
	 *            the documenter
	 */
	public DocumentationTask(final EObject object, final IGamlDescription description,
			final GamlResourceDocumenter documenter) {
		this.object = object;
		this.description = description;
		this.documenter = documenter;
	}

	/**
	 * Process.
	 */
	public void process() {
		// DEBUG.LOG("Documenting " + description.getName());
		if (description == null || object == null) return;
		final GamlResource key = (GamlResource) object.eResource();
		if (key == null) return;

		IGamlDescription node = null;
		try {
			node = description instanceof DocumentationNode ? description : new DocumentationNode(description);
		} catch (final Exception e) {
			DEBUG.ERR("DocumentationTask.process()" + e.getMessage() + " for " + description.getTitle());
		}
		if (node != null) {
			try {
				final IMap<EObject, IGamlDescription> map = documenter.getDocumentationCache(key);
				if (map != null) { map.put(object, node); }
			} catch (final RuntimeException e) {}
		}

	}

}