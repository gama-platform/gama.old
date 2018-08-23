/*********************************************************************************************
 *
 * ISyntacticElement.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform.
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 **********************************************************************************************/
package msi.gaml.compilation.ast;

import java.util.Map;

import org.eclipse.emf.ecore.EObject;

import com.google.common.base.Predicate;

import msi.gama.common.interfaces.IDisposable;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.interfaces.INamed;
import msi.gaml.descriptions.IDescription.IFacetVisitor;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.descriptions.SymbolProto;
import msi.gaml.statements.Facets;

/**
 * Interface ISyntacticElement. Elements representing statements (or symbols), that support a hierarchical
 * representation of a model to form the basis of the AST saved with the XText resource.
 * 
 * @author drogoul
 * @since 9 sept. 2013
 * 
 */
public interface ISyntacticElement extends INamed, IDisposable {

	/**
	 * The Interface SyntacticVisitor.
	 */
	public static interface SyntacticVisitor {

		/**
		 * Visit.
		 *
		 * @param element
		 *            the element
		 */
		void visit(ISyntacticElement element);
	}

	/**
	 * The Constant DISPOSE_VISITOR.
	 */
	public static final SyntacticVisitor DISPOSE_VISITOR = each -> each.dispose();

	/**
	 * The Constant SPECIES_FILTER.
	 */
	public static final Predicate<ISyntacticElement> SPECIES_FILTER =
			each -> each.isSpecies() && !IKeyword.GRID.equals(each.getKeyword());

	/**
	 * The Constant GRID_FILTER.
	 */
	public static final Predicate<ISyntacticElement> GRID_FILTER = each -> IKeyword.GRID.equals(each.getKeyword());

	/**
	 * The Constant EXPERIMENT_FILTER.
	 */
	public static final Predicate<ISyntacticElement> EXPERIMENT_FILTER = each -> each.isExperiment();

	/**
	 * The Constant OTHER_FILTER.
	 */
	public static final Predicate<ISyntacticElement> OTHER_FILTER = each -> !each.isExperiment() && !each.isSpecies();

	/**
	 * Sets the keyword of the element.
	 * 
	 * @param name
	 *            a keyword
	 */
	public abstract void setKeyword(final String name);

	/**
	 * Gets the keyword of the element.
	 *
	 * @return the keyword of the element (or null if it is not set)
	 */
	public abstract String getKeyword();

	/**
	 * Whether the element contains a facet of this name.
	 *
	 * @param name
	 *            name of the facet
	 * @return true if the element contains this facet
	 */
	public abstract boolean hasFacet(final String name);

	/**
	 * Returns the {@link IExpressionDescription} of the facet named after this name.
	 *
	 * @param name
	 *            the name of the facet
	 * @return the expression descrition located at this facet or null if the facet does not exist
	 */
	public abstract IExpressionDescription getExpressionAt(final String name);

	/**
	 * Copy the facets found in the element. The prototype of the symbol is passed so that additional operations can be
	 * made on the facets (transforming labels, etc.). This prototype can be null
	 * 
	 * @param sp
	 *            the prototype of the sympbol represented by this element
	 * @return a new Facets instance, which is guaranteed to hold a clean copy of the facets (no side effects)
	 */
	public abstract Facets copyFacets(SymbolProto sp);

	/**
	 * Adds (or replaces) a facet with the name 'name' and the expression 'expr'.
	 *
	 * @param name
	 *            the name of the facet
	 * @param expr
	 *            the expression of this facet
	 */
	public abstract void setFacet(final String name, final IExpressionDescription expr);

	/**
	 * Allows a {@link IFacetVisitor} to visit the facets of this element.
	 *
	 * @param visitor
	 *            a visitor
	 */
	public abstract void visitFacets(IFacetVisitor visitor);

	/**
	 * Returns the name of this element (usually the value of the expression of facet named {@link IKeyword#NAME}.
	 *
	 * @return the name of the element or null
	 */
	@Override
	public abstract String getName();

	/**
	 * Returns the EMF/Xtext element (an instance of {@link EObject} representing this element.
	 *
	 * @return the EObjet element or null if it is a synthetic element
	 */
	public abstract EObject getElement();

	/**
	 * Adds a child to this element (if this element supports children).
	 *
	 * @param e
	 *            a syntactic element
	 */
	public abstract void addChild(final ISyntacticElement e);

	/**
	 * Returns whether this element represents a species.
	 *
	 * @return true if the element is a species, false otherwise
	 */
	public abstract boolean isSpecies();

	/**
	 * Returns whether this element represents an experiment.
	 *
	 * @return true if the element is an experiment, false otherwise
	 */
	public abstract boolean isExperiment();

	/**
	 * Whether this elements has any facets.
	 *
	 * @return true if the element has at least one facet, false otherwise
	 */
	public abstract boolean hasFacets();

	/**
	 * Compute simple statistics (frequency of classes).
	 *
	 * @param stats
	 *            a map to be filled
	 */
	public abstract void computeStats(Map<String, Integer> stats);

	/**
	 * Allows a visitor to visit this element and its children.
	 *
	 * @param visitor
	 *            the visitor, not null
	 */
	public abstract void visitThisAndAllChildrenRecursively(SyntacticVisitor visitor);

	/**
	 * Allows a visitor to visit the children of this element that are neither species, grids or experiments.
	 *
	 * @param visitor
	 *            the visitor, not null
	 */
	public void visitChildren(final SyntacticVisitor visitor);

	/**
	 * Allows a visitor to visit only the elements that are species (either this element or its children).
	 *
	 * @param visitor
	 *            the visitor, not null
	 */
	public void visitSpecies(final SyntacticVisitor visitor);

	/**
	 * Allows a visitor to visit only the elements that are experiments (either this element or its children).
	 *
	 * @param visitor
	 *            the visitor, not null
	 */
	public void visitExperiments(final SyntacticVisitor visitor);

	/**
	 * Allows a visitor to visit only the elements that are grids (either this element or its children).
	 *
	 * @param visitor
	 *            the visitor, not null
	 */
	public void visitGrids(final SyntacticVisitor visitor);

	/**
	 * Compact the element by (1) setting the facets to null if they are empty; (2) compacting the map behind the facets
	 * to use as less memory as possible.
	 */
	public abstract void compact();

	/**
	 * Whether this element has children or not.
	 *
	 * @return true if this element has children
	 */
	public abstract boolean hasChildren();

	/**
	 * Allows a visitor to visit all the children of this element.
	 *
	 * @param syntacticVisitor
	 *            the syntactic visitor
	 */
	public abstract void visitAllChildren(SyntacticVisitor syntacticVisitor);

}