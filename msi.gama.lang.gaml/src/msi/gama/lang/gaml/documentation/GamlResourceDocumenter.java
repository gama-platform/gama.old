/*******************************************************************************************************
 *
 * GamlResourceDocumenter.java, in msi.gama.lang.gaml, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.lang.gaml.documentation;

import static it.unimi.dsi.fastutil.ints.Int2ObjectMaps.synchronize;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntConsumer;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import msi.gama.common.interfaces.IDocManager;
import msi.gama.lang.gaml.resource.GamlResourceServices;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.interfaces.IGamlDescription;
import ummisco.gama.dev.utils.DEBUG;

/**
 * Class GamlResourceDocManager.
 *
 * @author drogoul
 * @since 13 avr. 2014
 *
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamlResourceDocumenter implements IDocManager {

	static {
		DEBUG.ON();
	}

	/** The documentations from EObject to DocumentationNode. Key is the hashcode of the complete URI of the object */
	final Int2ObjectMap<DocumentationNode> docIndexedByObjects = synchronize(new Int2ObjectOpenHashMap());
	/**
	 * The references from Eobject to resources. Key is the hashcode of the complete URI of the object, value is the set
	 * of hashcodes of the URIs of open resources using this object
	 */
	final Int2ObjectMap<IntSet> resourcesIndexedByObjects = synchronize(new Int2ObjectOpenHashMap());
	/**
	 * The references from resources to EObjets. Key is the hashcode of the URI of the resource, value is the set of
	 * hashcodes of the complete URIs of objects present in this resource (they might belong to other resources)
	 */
	final Int2ObjectMap<IntSet> objectsIndexedByResources = synchronize(new Int2ObjectOpenHashMap());

	/** The resource names. */
	final Int2ObjectMap<String> resourceNames = synchronize(new Int2ObjectOpenHashMap());

	/** The documentation queue. */
	final ConcurrentLinkedQueue<Consumer<GamlResourceDocumenter>> documentationQueue = new ConcurrentLinkedQueue();

	/** The documentation job. */
	final Job documentationJob = new Job("Documentation") {
		{
			setUser(false);
			setPriority(Job.LONG);
		}

		@Override
		protected IStatus run(final IProgressMonitor monitor) {
			Consumer<GamlResourceDocumenter> task = documentationQueue.poll();
			while (task != null) {
				task.accept(GamlResourceDocumenter.this);
				task = documentationQueue.poll();
			}
			return Status.OK_STATUS;
		}
	};

	/**
	 * Adds the arbitrary documentation task.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param task
	 *            the task
	 * @date 30 déc. 2023
	 */
	public void addDocumentationTask(final Consumer<GamlResourceDocumenter> task) {
		documentationQueue.add(task);
		documentationJob.schedule(50);
	}

	/**
	 * Sets the gaml documentation.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param object
	 *            the object
	 * @param desc
	 *            the description
	 * @param replace
	 *            the replace
	 * @param force
	 *            the force
	 * @date 29 déc. 2023
	 */
	@Override
	public void setGamlDocumentation(final URI res, final EObject object, final IGamlDescription desc) {
		if (object == null) return;
		if (!isTaskValid(res)) {
			if (DEBUG.IS_ON()) {
				// DEBUG.OUT("Refusing to document " + desc.getTitle() + " in " + (res == null ? "" :
				// res.lastSegment()));
			}
			return;
		}
		addDocumentationTask(d -> internalSetGamlDocumentation(res, object, desc));
	}

	/**
	 * Internal set gaml documentation.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param openResource
	 *            the open resource
	 * @param object
	 *            the object
	 * @param description
	 *            the description
	 * @date 31 déc. 2023
	 */
	boolean internalSetGamlDocumentation(final URI res, final EObject object, final IGamlDescription desc) {
		try {
			if (!isTaskValid(res)) {
				if (DEBUG.IS_ON()) {
					// DEBUG.OUT("Skipping documenting " + desc.getTitle() + " of "
					// + (res == null ? "" : res.lastSegment()));
				}
				return false;
			}
			int fragment = getResourceURIPlusURIFragmentHashCode(object);
			int uri = res.hashCode();
			resourceNames.put(uri, res.lastSegment());
			docIndexedByObjects.put(fragment, new DocumentationNode(desc));
			resourcesIndexedByObjects.computeIfAbsent(fragment, i -> new IntOpenHashSet()).add(uri);
			objectsIndexedByResources.computeIfAbsent(uri, i -> new IntOpenHashSet()).add(fragment);
			return true;
		} catch (final RuntimeException e) {
			DEBUG.ERR("Error in documenting " + res.lastSegment(), e);
			return false;
		}
	}

	// To be called once the validation has been done
	@Override
	public void doDocument(final URI resource, final ModelDescription desc,
			final Map<EObject, IGamlDescription> additionalExpressions) {
		addDocumentationTask(s -> {
			internalDoDocument(resource, desc);
			additionalExpressions.forEach((e, d) -> internalSetGamlDocumentation(resource, e, d));
			if (DEBUG.IS_ON()) { debugStatistics("Documentation of " + resource.lastSegment()); }
		});
	}

	/**
	 * Internal do document.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param resource
	 *            the resource
	 * @param desc
	 *            the desc
	 * @date 31 déc. 2023
	 */
	private boolean internalDoDocument(final URI resource, final IDescription desc) {
		if (desc == null) return false;
		final EObject e = desc.getUnderlyingElement();
		if (e == null) return false;
		if (internalSetGamlDocumentation(resource, e, desc))
			return desc.visitOwnChildren(d -> internalDoDocument(resource, d));
		return false;
	}

	@Override
	public IGamlDescription getGamlDocumentation(final EObject object) {
		if (object == null) return null;
		int key = getResourceURIPlusURIFragmentHashCode(object);
		IGamlDescription doc = docIndexedByObjects.get(key);
		if (doc == null && DEBUG.IS_ON()) {
			DEBUG.OUT("EObject " + object + " in resource "
					+ (object.eResource() == null ? "null" : object.eResource().getURI().lastSegment())
					+ " is not documented ");
		}
		return doc;
	}

	@Override
	public void invalidate(final URI uri) {
		if (uri == null) return;
		addDocumentationTask(d -> {
			int resource = uri.hashCode();
			IntSet objects = objectsIndexedByResources.remove(resource);
			resourceNames.remove(resource);
			if (objects != null) {
				objects.forEach((IntConsumer) object -> {
					IntSet resources = resourcesIndexedByObjects.get(object);
					if (resources != null) {
						resources.remove(resource);
						if (resources.isEmpty()) {
							docIndexedByObjects.remove(object);
							resourcesIndexedByObjects.remove(object);
						}
					}
				});
			}
			if (DEBUG.IS_ON()) { debugStatistics("Invalidation of " + uri.lastSegment()); }
		});

	}

	/**
	 * Debug statistics.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param title
	 *            the title
	 * @date 31 déc. 2023
	 */
	private void debugStatistics(final String title) {
		DEBUG.SECTION(title);
		DEBUG.BANNER("DOC", "docIndexedByObjects", "size", String.valueOf(docIndexedByObjects.size()));
		DEBUG.BANNER("DOC", "eObjectsIndexedByResources", "size", String.valueOf(objectsIndexedByResources.size()));
		DEBUG.BANNER("DOC", "Opened Resources", "names", new HashSet(resourceNames.values()).toString());
		DEBUG.BANNER("DOC", "resourcesIndexedByEObjects", "size", String.valueOf(resourcesIndexedByObjects.size()));
		DEBUG.LINE();
	}

	/**
	 * Invalidate all.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 30 déc. 2023
	 */
	public void invalidateAll() {
		addDocumentationTask(d -> {
			docIndexedByObjects.clear();
			resourcesIndexedByObjects.clear();
			objectsIndexedByResources.clear();
		});
	}

	/**
	 * Gets the resource URI plus URI fragment hash code.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param object
	 *            the object
	 * @return the resource URI plus URI fragment hash code
	 * @date 31 déc. 2023
	 */
	private static int getResourceURIPlusURIFragmentHashCode(final EObject object) {
		if (object == null) return -1;
		if (object.eResource() == null) return object.hashCode();
		return object.eResource().getURI().appendFragment(EcoreUtil2.getURIFragment(object)).toString().hashCode();
	}

	/**
	 * Checks if is task valid.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param resource
	 *            the resource
	 * @return true, if is task valid
	 * @date 1 janv. 2024
	 */
	boolean isTaskValid(final URI resource) {
		return resource != null && GamlResourceServices.isEdited(resource);
	}

}
