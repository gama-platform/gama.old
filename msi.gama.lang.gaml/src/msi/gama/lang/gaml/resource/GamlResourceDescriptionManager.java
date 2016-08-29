/*********************************************************************************************
 * 
 * 
 * 'GamlResourceDescriptionManager.java', in plugin 'msi.gama.lang.gaml', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.lang.gaml.resource;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.DescriptionUtils;
import org.eclipse.xtext.resource.IDefaultResourceDescriptionStrategy;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.IResourceDescription.Delta;
import org.eclipse.xtext.resource.IResourceDescriptions;
import org.eclipse.xtext.resource.impl.DefaultResourceDescriptionManager;
import org.eclipse.xtext.util.Pair;
import org.jgrapht.traverse.BreadthFirstIterator;

import com.google.inject.Inject;

import msi.gama.lang.gaml.validation.GamlJavaValidator;

/**
 * The class GamlResourceDescriptionManager.
 * 
 * @author drogoul
 * @since 20 avr. 2012
 * 
 */
public class GamlResourceDescriptionManager extends DefaultResourceDescriptionManager
		implements IResourceDescription.Manager.AllChangeAware {

	@Inject
	private DescriptionUtils descriptionUtils;

	@Override
	protected IResourceDescription internalGetResourceDescription(final Resource resource,
			final IDefaultResourceDescriptionStrategy strategy) {
		return new GamlResourceDescription(resource, strategy, getCache());
	}

	@Override
	public boolean isAffected(final Collection<Delta> deltas, final IResourceDescription candidate,
			final IResourceDescriptions context) {
		final boolean result = false;
		// if (candidate.getURI().lastSegment().contains("Google")) {
		// System.out.println("FOUND GOOGLE");
		// }
		final URI newUri = URI.createURI(URI.decode(candidate.getURI().toString()));

		if (GamlJavaValidator.IMPORTS_GRAPH.containsVertex(newUri)) {
			final Set<URI> deltaUris = new HashSet();
			for (final Delta d : deltas) {
				final URI uri = URI.createURI(URI.decode(d.getUri().toString()));
				deltaUris.add(uri);
			}
			final BreadthFirstIterator<URI, Pair<URI, URI>> it = new BreadthFirstIterator(
					GamlJavaValidator.IMPORTS_GRAPH, newUri);
			it.next();
			while (it.hasNext()) {
				final URI next = it.next();
				if (deltaUris.contains(next)) {
					// System.out.println(newUri.lastSegment() + " is affected
					// because it imports " + next.lastSegment());
					return true;
				}
			}
		}
		// final Set<URI> imports = new
		// BreadthFirstIterator(GamlJavaValidator.IMPORTS_GRAPH, newUri);
		// if (!imports.isEmpty()) {
		// for (final Delta d : deltas) {
		// final URI uri = URI.createURI(URI.decode(d.getUri().toString()));
		// if (imports.contains(uri)) {
		// // if (d.getUri().lastSegment().contains("Segreg")
		// // && candidate.getURI().lastSegment().contains("Segreg")) {
		// // System.out.println("d");
		// // }
		// //
		// // System.out
		// // .println(d.getUri().lastSegment() + " is imported by " +
		// // candidate.getURI().lastSegment());
		// return true;
		// }
		// }
		// }
		return super.isAffected(deltas, candidate, context);
	}

	@Override
	public boolean isAffectedByAny(final Collection<Delta> deltas, final IResourceDescription candidate,
			final IResourceDescriptions context) throws IllegalArgumentException {
		return isAffected(deltas, candidate, context);
	}
}
