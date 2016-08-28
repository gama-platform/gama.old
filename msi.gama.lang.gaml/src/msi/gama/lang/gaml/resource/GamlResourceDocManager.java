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
package msi.gama.lang.gaml.resource;

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
import org.eclipse.xtext.resource.XtextResource;

import com.google.inject.Provider;

import gnu.trove.map.hash.THashMap;
import msi.gama.common.interfaces.IGamlDescription;
import msi.gama.precompiler.GamlProperties;
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
public class GamlResourceDocManager implements IDocManager {

	private static final ConcurrentLinkedQueue<ModelDescription> CleanupTasks = new ConcurrentLinkedQueue();
	private static final ConcurrentLinkedQueue<DocumentationTask> DocumentationQueue = new ConcurrentLinkedQueue();

	final DescriptionVisitor documeningVisitor = new DescriptionVisitor() {

		@Override
		public void visit(final IDescription desc) {
			document(desc);

		}
	};

	public static void addCleanupTask(final ModelDescription model) {
		CleanupTasks.add(model);
	}

	public static final Job DocumentationJob = new Job("Documentation") {
		{
			setUser(false);
		}

		@Override
		protected IStatus run(final IProgressMonitor monitor) {
			DocumentationTask task = DocumentationQueue.poll();
			while (task != null) {
				task.process();
				task = DocumentationQueue.poll();
			}
			ModelDescription r = CleanupTasks.poll();
			while (r != null) {
				r.dispose();
				r = CleanupTasks.poll();
			}
			return Status.OK_STATUS;
		}
	};

	private static class DocumentationTask {
		EObject object;
		IGamlDescription description;

		public DocumentationTask(final EObject object, final IGamlDescription description) {
			super();
			this.object = object;
			this.description = description;
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
					getDocumentationCache(key).put(object, node);
				} catch (final RuntimeException e) {
				}
			}

		}

	}

	// static int MAX_SIZE = 10000;

	private static THashMap<Resource, THashMap<EObject, IGamlDescription>> CACHE2 = new THashMap();

	private static volatile IDocManager instance;

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

	public static class DocumentationNode implements IGamlDescription {

		final byte[] title;
		final byte[] doc;
		// final byte[] plugin;

		DocumentationNode(final IGamlDescription desc) throws IOException {
			final String plugin = desc.getDefiningPlugin();
			final String title = desc.getTitle();
			String documentation = desc.getDocumentation();
			if (plugin != null) {
				documentation += "\n<p/><i> [defined in " + plugin + "] </i>";
			}
			doc = StringCompressor.compress(documentation);
			this.title = StringCompressor.compress(title);
		}

		/**
		 * Method collectMetaInformation()
		 * 
		 * @see msi.gama.common.interfaces.IGamlDescription#collectPlugins(java.util.Set)
		 */
		@Override
		public void collectMetaInformation(final GamlProperties meta) {
		}

		@Override
		public String getDocumentation() {
			return StringCompressor.decompress(doc);
		}

		@Override
		public String getTitle() {
			return StringCompressor.decompress(title);
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

	private GamlResourceDocManager() {
		DocumentationJob.setPriority(Job.SHORT);
	}

	public static IDocManager getInstance() {
		if (instance == null) {
			instance = new GamlResourceDocManager();
		}
		return instance;
	}

	@Override
	public void setGamlDocumentation(final EObject object, final IGamlDescription description, final boolean replace) {
		if (!shouldDocument(object))
			return;
		// System.out.println("Documenting " + object + " " + " resource: " +
		// object.eResource() + " " + description);
		DocumentationQueue.add(new DocumentationTask(object, description));
		DocumentationJob.schedule(50);
	}

	private static THashMap<EObject, IGamlDescription> getDocumentationCache(final Resource resource) {
		if (resource instanceof XtextResource)
			return ((XtextResource) resource).getCache().get(KEY, resource, new Provider<THashMap<EObject, IGamlDescription>>() {

				@Override
				public THashMap<EObject, IGamlDescription> get() {
					return new THashMap();
				}
			});
		else
			return CACHE2.get(resource);
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
		final Resource r = e.eResource();

		if (r instanceof GamlResource && !((GamlResource) e.eResource()).isEdited())
			return;

		setGamlDocumentation(e, desc, true);
		desc.visitOwnChildren(documeningVisitor);

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

	@Override
	public void document(final Resource r, final boolean accept) {
		CACHE2.putIfAbsent(r, new THashMap());
	}

}
