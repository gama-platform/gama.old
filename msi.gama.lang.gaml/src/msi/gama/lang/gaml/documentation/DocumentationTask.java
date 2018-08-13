/*********************************************************************************************
 *
 * 'DocumentationTask.java, in plugin msi.gama.lang.gaml, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.documentation;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import gnu.trove.map.hash.THashMap;
import msi.gama.common.interfaces.IGamlDescription;
import ummisco.gama.dev.utils.DEBUG;

class DocumentationTask {
	final EObject object;
	final IGamlDescription description;
	final GamlResourceDocumenter documenter;

	public DocumentationTask(final EObject object, final IGamlDescription description,
			final GamlResourceDocumenter documenter) {
		super();
		this.object = object;
		this.description = description;
		this.documenter = documenter;
	}

	public void process() {
		// DEBUG.LOG("Documenting " + description.getName());
		if (description == null) { return; }
		if (object == null) { return; }
		final Resource key = object.eResource();
		if (key == null) { return; }

		DocumentationNode node = null;
		try {
			node = new DocumentationNode(description);
		} catch (final Exception e) {
			DEBUG.ERR("DocumentationTask.process()" + e.getMessage() + " for " + description.getTitle());
		}
		if (node != null) {
			try {
				final THashMap<EObject, IGamlDescription> map = documenter.getDocumentationCache(key);
				if (map != null) {
					map.put(object, node);
				}
			} catch (final RuntimeException e) {}
		}

	}

}