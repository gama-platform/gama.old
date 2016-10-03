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
package msi.gaml.compilation.ast;

import java.util.Map;

import org.eclipse.emf.ecore.EObject;

import com.google.common.base.Predicate;

import msi.gama.common.interfaces.IKeyword;
import msi.gaml.descriptions.IDescription.FacetVisitor;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.descriptions.SymbolProto;
import msi.gaml.statements.Facets;

/**
 * Class ISyntacticElement.
 * 
 * @author drogoul
 * @since 9 sept. 2013
 * 
 */
public interface ISyntacticElement {

	public static interface SyntacticVisitor {
		void visit(ISyntacticElement element);
	}

	public static final Predicate<ISyntacticElement> SPECIES_FILTER = input -> input.isSpecies()
			&& !IKeyword.GRID.equals(input.getKeyword());

	public static final Predicate<ISyntacticElement> GRID_FILTER = input -> IKeyword.GRID.equals(input.getKeyword());

	public static final Predicate<ISyntacticElement> EXPERIMENT_FILTER = input -> input.isExperiment();

	public static final Predicate<ISyntacticElement> OTHER_FILTER = input -> !input.isExperiment()
			&& !input.isSpecies();

	public abstract void setKeyword(final String name);

	public abstract String getKeyword();

	public abstract boolean hasFacet(final String name);

	public abstract IExpressionDescription getExpressionAt(final String name);

	// Copy the facets found in the element. The prototype of the symbol is
	// passed so that additional operations can be
	// made on the facets (transforming labels, etc.). This prototype can be
	// null.
	public abstract Facets copyFacets(SymbolProto sp);

	public abstract void setFacet(final String string, final IExpressionDescription expr);

	// public abstract void setDependencies(final Set<String> strings);

	// public Set<String> getDependencies();

	public abstract void visitFacets(FacetVisitor visitor);

	public abstract String getName();

	public abstract EObject getElement();

	public abstract void addChild(final ISyntacticElement e);

	public abstract boolean isSpecies();

	public abstract boolean isExperiment();

	public abstract boolean hasFacets();

	public abstract void computeStats(Map<String, Integer> stats);

	public abstract void visitThisAndAllChildrenRecursively(SyntacticVisitor visitor);

	public void visitChildren(final SyntacticVisitor visitor);

	public void visitSpecies(final SyntacticVisitor visitor);

	public void visitExperiments(final SyntacticVisitor visitor);

	public void visitGrids(final SyntacticVisitor visitor);

	public abstract void compact();

	public abstract boolean hasChildren();

	public abstract void visitAllChildren(SyntacticVisitor syntacticVisitor);

}