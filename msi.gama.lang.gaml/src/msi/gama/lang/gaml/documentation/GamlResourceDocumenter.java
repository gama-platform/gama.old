/*******************************************************************************************************
 *
 * GamlResourceDocumenter.java, in msi.gama.lang.gaml, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.lang.gaml.documentation;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import msi.gama.common.interfaces.IDocManager;
import msi.gama.common.interfaces.IGamlDescription;
import msi.gama.lang.gaml.resource.GamlResourceServices;
import msi.gama.util.IMap;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IDescription.DescriptionVisitor;
import msi.gaml.descriptions.ModelDescription;
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

	/** The cleanup tasks. */
	final ConcurrentLinkedQueue<ModelDescription> cleanupTasks = new ConcurrentLinkedQueue();

	/** The documentation queue. */
	final ConcurrentLinkedQueue<DocumentationTask> documentationQueue = new ConcurrentLinkedQueue();

	/** The documentation job. */
	final Job documentationJob = new Job("Documentation") {
		{
			setUser(false);
			setPriority(Job.SHORT);
		}

		@Override
		protected IStatus run(final IProgressMonitor monitor) {
			DocumentationTask task = documentationQueue.poll();
			while (task != null) {
				task.process();
				task = documentationQueue.poll();
			}
			ModelDescription r = cleanupTasks.poll();
			while (r != null) {
				r.dispose();
				r = cleanupTasks.poll();
			}
			return Status.OK_STATUS;
		}
	};

	/** The documenting visitor. */
	final DescriptionVisitor<IDescription> documentingVisitor = desc -> {
		document(desc);
		return true;
	};

	@Override
	public void addCleanupTask(final ModelDescription model) {
		cleanupTasks.add(model);
	}

	@Override
	public void setGamlDocumentation(final EObject object, final IGamlDescription description, final boolean replace,
			final boolean force) {
		if (!force && !shouldDocument(object)) return;

		documentationQueue.add(new DocumentationTask(object, description, this));
		documentationJob.schedule(50);
	}

	/**
	 * Gets the documentation cache.
	 *
	 * @param resource
	 *            the resource
	 * @return the documentation cache
	 */
	IMap<EObject, IGamlDescription> getDocumentationCache(final Resource resource) {
		if (resource == null) return null;
		return GamlResourceServices.getDocumentationCache(resource);
	}

	// To be called once the validation has been done
	@Override
	public void document(final IDescription desc) {
		if (desc == null) return;
		final EObject e = desc.getUnderlyingElement();
		if (e == null) return;
		setGamlDocumentation(e, desc, true);
		desc.visitOwnChildren(documentingVisitor);

	}

	@Override
	public IGamlDescription getGamlDocumentation(final IGamlDescription o) {
		if (o == null || o instanceof DocumentationNode) return o;
		try {
			return new DocumentationNode(o);
		} catch (final Exception e) {
			DEBUG.ERR("GamlResourceDocumenter.getGamlDocumentation(): " + e.getMessage() + " for " + o.getTitle());
			return null;
		}
	}

	@Override
	public IGamlDescription getGamlDocumentation(final EObject object) {
		if (object == null) return null;
		Resource r = object.eResource();
		final IMap<EObject, IGamlDescription> map = getDocumentationCache(r);
		if (map == null) return null;
		return map.get(object);
	}

	/**
	 * Should document.
	 *
	 * @param object
	 *            the object
	 * @return true, if successful
	 */
	private static boolean shouldDocument(final EObject object) {
		if (object == null) return false;
		final Resource r = object.eResource();
		if (r == null || !GamlResourceServices.isEdited(r)) return false;
		return true;
	}

}
