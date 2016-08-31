/*********************************************************************************************
 *
 *
 * 'GamlResourceDocManager.java', in plugin 'msi.gama.lang.gaml', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.documentation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import gnu.trove.map.hash.THashMap;
import msi.gama.common.interfaces.IGamlDescription;
import msi.gama.lang.gaml.indexer.IModelIndexer;
import msi.gama.lang.gaml.resource.GamlResource;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IDescription.DescriptionVisitor;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.factories.DescriptionFactory.IDocManager;

/**
 * Class GamlResourceDocManager.
 *
 * @author drogoul
 * @since 13 avr. 2014
 *
 */
@Singleton
public class GamlResourceDocManager implements IDocManager {

	public final static GamlResourceDocManager INSTANCE = new GamlResourceDocManager();

	@Inject
	private IModelIndexer indexer;
	private final ConcurrentLinkedQueue<ModelDescription> cleanupTasks = new ConcurrentLinkedQueue();
	private final ConcurrentLinkedQueue<DocumentationTask> documentationQueue = new ConcurrentLinkedQueue();
	private final Job documentationJob = new Job("Documentation") {
		{
			setUser(false);
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

	final DescriptionVisitor documentingVisitor = new DescriptionVisitor() {

		@Override
		public boolean visit(final IDescription desc) {
			document(desc);
			return true;

		}
	};

	public void addCleanupTask(final ModelDescription model) {
		cleanupTasks.add(model);
	}

	static enum StringCompressor {
		;
		public static byte[] compress(final String text) {
			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				final OutputStream out = new DeflaterOutputStream(baos);
				out.write(text.getBytes("ISO-8859-1"));
				out.close();
			} catch (final IOException e) {
				throw new AssertionError(e);
			}
			return baos.toByteArray();
		}

		public static String decompress(final byte[] bytes) {
			final InputStream in = new InflaterInputStream(new ByteArrayInputStream(bytes));
			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				final byte[] buffer = new byte[8192];
				int len;
				while ((len = in.read(buffer)) > 0)
					baos.write(buffer, 0, len);
				return new String(baos.toByteArray(), "ISO-8859-1");
			} catch (final IOException e) {
				throw new AssertionError(e);
			}
		}
	}

	private GamlResourceDocManager() {
		documentationJob.setPriority(Job.SHORT);
	}

	public static IDocManager getInstance() {
		return INSTANCE;
	}

	@Override
	public void setGamlDocumentation(final EObject object, final IGamlDescription description, final boolean replace) {
		if (!shouldDocument(object))
			return;
		// System.out.println("Documenting " + object + " " + " resource: " +
		// object.eResource() + " " + description);
		documentationQueue.add(new DocumentationTask(object, description, this));
		documentationJob.schedule(50);
	}

	THashMap<EObject, IGamlDescription> getDocumentationCache(final Resource resource) {
		return indexer.getDocumentationCache(resource.getURI());

		// if (resource instanceof XtextResource)
		// return ((XtextResource) resource).getCache().get(KEY, resource,
		// new Provider<THashMap<EObject, IGamlDescription>>() {
		//
		// @Override
		// public THashMap<EObject, IGamlDescription> get() {
		// return new THashMap();
		// }
		// });
		// else
		// return CACHE2.get(resource);
	}

	// To be called once the validation has been done
	@Override
	public void document(final IDescription desc) {
		if (desc == null) {
			return;
		}
		final EObject e = desc.getUnderlyingElement(null);
		if (e == null) {
			return;
		}
		// final Resource r = e.eResource();
		//
		// if (r instanceof GamlResource && !((GamlResource)
		// e.eResource()).isEdited())
		// return;

		setGamlDocumentation(e, desc, true);
		desc.visitOwnChildren(documentingVisitor);

	}

	@Override
	public IGamlDescription getGamlDocumentation(final IGamlDescription o) {
		if (o == null) {
			return null;
		}
		try {
			return new DocumentationNode(o);
		} catch (final IOException e) {
			return null;
		}
	}

	@Override
	public IGamlDescription getGamlDocumentation(final EObject object) {
		if (object == null) {
			return null;
		}
		return getDocumentationCache(object.eResource()).get(object);
	}

	private static boolean shouldDocument(final EObject object) {
		if (object == null)
			return false;
		final Resource r = object.eResource();
		if (r == null)
			return false;
		if (r instanceof GamlResource) {
			if (!((GamlResource) r).isEdited())
				return false;
		}
		return true;
	}

	// @Override
	// public void document(final Resource r, final boolean accept) {
	// // CACHE2.putIfAbsent(r, new THashMap());
	// }

}
