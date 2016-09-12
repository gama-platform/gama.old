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

import gnu.trove.map.hash.THashMap;
import msi.gama.common.interfaces.IDocManager;
import msi.gama.common.interfaces.IGamlDescription;
import msi.gama.lang.gaml.resource.GamlResourceServices;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IDescription.DescriptionVisitor;
import msi.gaml.descriptions.ModelDescription;

/**
 * Class GamlResourceDocManager.
 *
 * @author drogoul
 * @since 13 avr. 2014
 *
 */
public class GamlResourceDocumenter implements IDocManager {

	private final ConcurrentLinkedQueue<ModelDescription> cleanupTasks = new ConcurrentLinkedQueue();
	private final ConcurrentLinkedQueue<DocumentationTask> documentationQueue = new ConcurrentLinkedQueue();
	private final Job documentationJob = new Job("Documentation") {
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

	final DescriptionVisitor documentingVisitor = new DescriptionVisitor() {

		@Override
		public boolean visit(final IDescription desc) {
			document(desc);
			return true;

		}
	};

	@Override
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

	@Override
	public void setGamlDocumentation(final EObject object, final IGamlDescription description, final boolean replace) {
		if (!shouldDocument(object))
			return;
		documentationQueue.add(new DocumentationTask(object, description, this));
		documentationJob.schedule(50);
	}

	THashMap<EObject, IGamlDescription> getDocumentationCache(final Resource resource) {
		if (resource == null)
			return null;
		return GamlResourceServices.getDocumentationCache(resource);
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
		final THashMap<EObject, IGamlDescription> map = getDocumentationCache(object.eResource());
		if (map == null)
			return null;
		return map.get(object);
	}

	private static boolean shouldDocument(final EObject object) {
		if (object == null)
			return false;
		final Resource r = object.eResource();
		if (r == null)
			return false;
		if (!GamlResourceServices.isEdited(r))
			return false;
		return true;
	}

}
