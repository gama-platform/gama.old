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

import java.nio.charset.Charset;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.EcoreUtil2;

import gnu.trove.map.hash.THashMap;
import msi.gama.precompiler.GamlProperties;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IGamlDescription;
import msi.gaml.factories.DescriptionFactory.IDocManager;

/**
 * Class GamlResourceDocManager.
 *
 * @author drogoul
 * @since 13 avr. 2014
 *
 */
public class GamlResourceDocManager implements IDocManager {

	private static final ConcurrentLinkedQueue<Runnable> CleanupTasks = new ConcurrentLinkedQueue();
	private static final ConcurrentLinkedQueue<DocumentationTask> DocumentationQueue = new ConcurrentLinkedQueue();
	private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

	public static void addCleanupTask(final Runnable r) {
		CleanupTasks.add(r);
	}

	public static final Job DocumentationJob = new Job("Documentation") {
		{
			setUser(false);
			setSystem(true);
		}

		@Override
		protected IStatus run(final IProgressMonitor monitor) {
			DocumentationTask task = DocumentationQueue.poll();
			while (task != null) {
				task.process();
				task = DocumentationQueue.poll();
			}
			final Runnable r = CleanupTasks.poll();
			while (r != null) {
				r.run();
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
			final URI key = getKey(object);
			if (key == null) {
				return;
			}
			if (CACHE2.contains(key)) {
				CACHE2.get(key).put(EcoreUtil2.getURIFragment(object), new DocumentationNode(description));
			}

		}

	}

	public static byte[] encode(final String string) {
		if (string == null) {
			return null;
		}
		return string.getBytes(UTF8_CHARSET);
	}

	public static String decode(final byte[] bytes) {
		if (bytes == null) {
			return null;
		}
		return new String(bytes, UTF8_CHARSET);
	}

	static int MAX_SIZE = 10000;

	private static THashMap<URI, THashMap<String, IGamlDescription>> CACHE2 = new THashMap();

	private static IDocManager instance;

	public static class DocumentationNode implements IGamlDescription {

		final String doc;
		final String title;
		final String plugin;

		DocumentationNode(final IGamlDescription desc) {
			plugin = desc.getDefiningPlugin();
			title = desc.getTitle();
			String documentation = desc.getDocumentation();
			if (plugin != null) {
				documentation += "\n<p/><i> [defined in " + plugin + "] </i>";
			}
			doc = documentation;

		}

		/**
		 * Method collectPlugins()
		 * 
		 * @see msi.gaml.descriptions.IGamlDescription#collectPlugins(java.util.Set)
		 */
		@Override
		public void collectMetaInformation(final GamlProperties meta) {
		}

		@Override
		public String getDocumentation() {
			return doc;
		}

		@Override
		public String getTitle() {
			return title;
		}

		@Override
		public String getName() {
			return "Online documentation";
		}

		@Override
		public String getDefiningPlugin() {
			return plugin;
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
	public void setGamlDocumentation(final EObject object, final IGamlDescription description) {
		DocumentationQueue.add(new DocumentationTask(object, description));
		// DocumentationJob.schedule();
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
		if (!CACHE2.containsKey(e.eResource().getURI())) {
			return;
		}
		setGamlDocumentation(e, desc);
		for (final IDescription d : desc.getChildren()) {
			document(d);
		}
	}

	@Override
	public IGamlDescription getGamlDocumentation(final IGamlDescription o) {
		if (o == null) {
			return null;
		}
		return new DocumentationNode(o);
	}

	@Override
	public IGamlDescription getGamlDocumentation(final EObject object) {
		if (object == null) {
			return null;
		}
		// int index = getGamlDocIndex(object);
		// if ( index == -1 ) { return null; }
		final URI key = getKey(object);
		if (key == null) {
			return null;
		}
		if (!CACHE2.containsKey(key)) {
			return null;
		}
		return CACHE2.get(key).get(EcoreUtil2.getURIFragment(object));
	}

	private static URI getKey(final EObject object) {
		if (object == null) {
			return null;
		}
		final Resource r = object.eResource();
		if (r == null) {
			return null;
		}
		return r.getURI();
	}

	@Override
	public void document(final Resource gamlResource, final boolean accept) {
		if (accept) {
			CACHE2.putIfAbsent(gamlResource.getURI(), new THashMap(MAX_SIZE, 0.95f));
		} else {
			CACHE2.remove(gamlResource.getURI());
		}
	}

}
