/*********************************************************************************************
 * 
 *
 * 'SyntacticModelElement.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.compilation;

import gnu.trove.set.hash.TLinkedHashSet;
import java.util.*;
import msi.gaml.statements.Facets;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

/**
 * Class SyntacticModelElement.
 * 
 * @author drogoul
 * @since 12 avr. 2014
 * 
 */
public class SyntacticModelElement extends SyntacticComposedElement {

	final Set<URI> imports;
	boolean urisFixed = false;

	public SyntacticModelElement(final String keyword, final Facets facets, final EObject statement,
		final Object ... imports) {
		super(keyword, facets, statement);
		if ( imports.length == 0 ) {
			this.imports = Collections.EMPTY_SET;
		} else {
			this.imports = new TLinkedHashSet();
			for ( Object o : imports ) {
				this.imports.add((URI) o);
			}
		}
	}

	public Set<URI> getImports() {
		return imports;
	}

	public boolean areURIFixed() {
		return urisFixed;
	}

	public void setImports(final Set<URI> uris) {
		imports.clear();
		imports.addAll(uris);
		urisFixed = true;
	}

}
