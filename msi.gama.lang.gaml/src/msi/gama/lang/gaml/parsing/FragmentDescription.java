package msi.gama.lang.gaml.parsing;

import msi.gaml.descriptions.*;
import msi.gaml.factories.IChildrenProvider;
import msi.gaml.statements.Facets;
import org.eclipse.emf.ecore.EObject;

/**
 * 
 * The class PartialDescription. A partial description of a complete model, which cannot serve for validation but only
 * for building a complete description.
 * 
 * @author drogoul
 * @since 27 avr. 2013
 * 
 */
public class FragmentDescription extends SymbolDescription {

	public FragmentDescription(String keyword, IDescription superDesc, IChildrenProvider cp, EObject source,
		Facets facets) {
		super(keyword, superDesc, cp, source, facets);
	}

}
