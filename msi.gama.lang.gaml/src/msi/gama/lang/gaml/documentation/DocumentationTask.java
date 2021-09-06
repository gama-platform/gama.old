/*******************************************************************************************************
 *
 * DocumentationTask.java, in msi.gama.lang.gaml, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.8.2).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.lang.gaml.documentation;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import msi.gama.common.interfaces.IGamlDescription;
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
		final Resource key = object.eResource();
		if (key == null) return;

		DocumentationNode node = null;
		try {
			node = new DocumentationNode(description);
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