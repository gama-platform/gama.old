/*******************************************************************************************************
 *
 * GamlScopeProvider.java, in msi.gama.lang.gaml, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.lang.gaml.scoping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.EObjectDescription;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.ISelectable;

import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

/**
 * This class contains custom scoping description.
 *
 * see : http://www.eclipse.org/Xtext/documentation/latest/xtext.html#scoping on how and when to use it
 *
 */
// @Singleton
public class GamlScopeProvider extends org.eclipse.xtext.scoping.impl.SimpleLocalScopeProvider {

	/**
	 * The Class GamlMultimapBasedSelectable.
	 */
	private class GamlMultimapBasedSelectable implements ISelectable {
		
		/** The descriptions. */
		List<IEObjectDescription> descriptions;
		
		/** The name to objects. */
		private Multimap<QualifiedName, IEObjectDescription> nameToObjects;

		@Override
		public boolean isEmpty() {
			return descriptions == null;
		}

		@Override
		public Iterable<IEObjectDescription> getExportedObjectsByType(final EClass type) {
			if (descriptions == null) { return Collections.emptyList(); }
			return Iterables.filter(descriptions, input -> EcoreUtil2.isAssignableFrom(type, input.getEClass()));
		}

		@Override
		public Iterable<IEObjectDescription> getExportedObjectsByObject(final EObject object) {
			if (descriptions == null) { return Collections.emptyList(); }
			final URI uri = EcoreUtil2.getPlatformResourceOrNormalizedURI(object);
			return Iterables.filter(descriptions, input -> {
				if (input.getEObjectOrProxy() == object) { return true; }
				if (uri.equals(input.getEObjectURI())) { return true; }
				return false;
			});
		}

		@Override
		public Iterable<IEObjectDescription> getExportedObjects(final EClass type, final QualifiedName name,
				final boolean ignoreCase) {
			if (nameToObjects == null) { return Collections.emptyList(); }
			if (nameToObjects.containsKey(name)) {
				for (final IEObjectDescription desc : nameToObjects.get(name)) {
					if (EcoreUtil2.isAssignableFrom(type, desc.getEClass())) { return Collections.singleton(desc); }
				}
			}
			return Collections.emptyList();
		}

		@Override
		public Iterable<IEObjectDescription> getExportedObjects() {
			return descriptions == null ? Collections.EMPTY_LIST : descriptions;
		}

		/**
		 * Adds the.
		 *
		 * @param name the name
		 * @param e the e
		 */
		private void add(final QualifiedName name, final EObjectDescription e) {
			if (descriptions == null) {
				descriptions = new ArrayList<>();
				nameToObjects = LinkedHashMultimap.create();
			}
			descriptions.add(e);
			nameToObjects.put(name, e);
		}

	}

	@Override
	protected ISelectable getAllDescriptions(final Resource resource) {
		final GamlMultimapBasedSelectable result = new GamlMultimapBasedSelectable();
		final IQualifiedNameProvider provider = getNameProvider();
		final Iterator<EObject> iterator = resource.getAllContents();
		while (iterator.hasNext()) {
			final EObject from = iterator.next();
			final QualifiedName qualifiedName = provider.apply(from);
			if (qualifiedName != null) {
				result.add(qualifiedName, new EObjectDescription(qualifiedName, from, null));
			}
		}
		return result;
	}

}