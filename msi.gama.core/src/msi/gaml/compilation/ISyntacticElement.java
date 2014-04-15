/*********************************************************************************************
 * 
 *
 * 'ISyntacticElement.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.compilation;

import msi.gaml.descriptions.*;
import msi.gaml.statements.Facets;
import org.eclipse.emf.ecore.EObject;

/**
 * Class ISyntacticElement.
 * 
 * @author drogoul
 * @since 9 sept. 2013
 * 
 */
public interface ISyntacticElement {

	final static ISyntacticElement[] EMPTY_ARRAY = new ISyntacticElement[0];

	public abstract void setKeyword(final String name);

	public abstract void dump();

	public abstract String getKeyword();

	public abstract boolean hasFacet(final String name);

	public abstract IExpressionDescription getExpressionAt(final String name);

	// Copy the facets found in the element. The prototype of the symbol is passed so that additional operations can be
	// made on the facets (transforming labels, etc.). This prototype can be null.
	public abstract Facets copyFacets(SymbolProto sp);

	public abstract void setFacet(final String string, final IExpressionDescription expr);

	public abstract ISyntacticElement[] getChildren();

	public abstract String getName();

	public abstract EObject getElement();

	public abstract void addChild(final ISyntacticElement e);

	public abstract boolean isSynthetic();

	public abstract boolean isSpecies();

	public abstract boolean isGlobal();

	public abstract boolean isExperiment();

}