/*******************************************************************************************************
 *
 * msi.gaml.factories.SymbolFactory.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.factories;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;

import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.SymbolProto;
import msi.gaml.statements.Facets;

/**
 * Written by Alexis Drogoul Modified on 11 mai 2010
 *
 * @todo Description
 *
 */
public abstract class SymbolFactory {

	protected final Set<Integer> kindsHandled;

	public SymbolFactory(final int... handles) {
		kindsHandled = new HashSet<>(handles.length);
		for (final int i : handles) {
			kindsHandled.add(i);
		}
	}

	Set<Integer> getHandles() {
		return kindsHandled;
	}

	protected abstract IDescription buildDescription(String keyword, Facets facets, EObject element,
			Iterable<IDescription> children, IDescription enclosing, SymbolProto proto);

}
