package msi.gama.lang.gaml.documentation;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import gnu.trove.map.hash.THashMap;
import msi.gama.common.interfaces.IGamlDescription;

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
		// System.out.println("Documenting " + description.getName());
		if (description == null)
			return;
		if (object == null)
			return;
		final Resource key = object.eResource();
		if (key == null) {
			return;
		}

		DocumentationNode node = null;
		try {
			node = new DocumentationNode(description);
		} catch (final Exception e) {
		}
		if (node != null) {
			try {
				final THashMap map = documenter.getDocumentationCache(key);
				if (map != null)
					map.put(object, node);
			} catch (final RuntimeException e) {
			}
		}

	}

}