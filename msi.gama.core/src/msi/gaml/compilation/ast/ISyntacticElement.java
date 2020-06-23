/*******************************************************************************************************
 *
 * msi.gaml.compilation.ast.ISyntacticElement.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.compilation.ast;

import java.util.Map;
import java.util.function.Predicate;

import org.eclipse.emf.ecore.EObject;

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
	public interface SyntacticVisitor {

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
	SyntacticVisitor DISPOSE_VISITOR = each -> each.dispose();

	/**
	 * The Constant SPECIES_FILTER.
	 */
	Predicate<ISyntacticElement> SPECIES_FILTER = each -> each.isSpecies() && !IKeyword.GRID.equals(each.getKeyword());

	/**
	 * The Constant GRID_FILTER.
	 */
	Predicate<ISyntacticElement> GRID_FILTER = each -> IKeyword.GRID.equals(each.getKeyword());

	/**
	 * The Constant EXPERIMENT_FILTER.
	 */
	Predicate<ISyntacticElement> EXPERIMENT_FILTER = each -> each.isExperiment();

	/**
	 * The Constant OTHER_FILTER.
	 */
	Predicate<ISyntacticElement> OTHER_FILTER = each -> !each.isExperiment() && !each.isSpecies();

	/**
	 * Sets the keyword of the element.
	 *
	 * @param name
	 *            a keyword
	 */
	void setKeyword(final String name);

	/**
	 * Gets the keyword of the element.
	 *
	 * @return the keyword of the element (or null if it is not set)
	 */
	String getKeyword();

	/**
	 * Whether the element contains a facet of this name.
	 *
	 * @param name
	 *            name of the facet
	 * @return true if the element contains this facet
	 */
	boolean hasFacet(final String name);

	/**
	 * Returns the {@link IExpressionDescription} of the facet named after this name.
	 *
	 * @param name
	 *            the name of the facet
	 * @return the expression descrition located at this facet or null if the facet does not exist
	 */
	IExpressionDescription getExpressionAt(final String name);

	/**
	 * Copy the facets found in the element. The prototype of the symbol is passed so that additional operations can be
	 * made on the facets (transforming labels, etc.). This prototype can be null
	 *
	 * @param sp
	 *            the prototype of the sympbol represented by this element
	 * @return a new Facets instance, which is guaranteed to hold a clean copy of the facets (no side effects)
	 */
	Facets copyFacets(SymbolProto sp);

	/**
	 * Adds (or replaces) a facet with the name 'name' and the expression 'expr'.
	 *
	 * @param name
	 *            the name of the facet
	 * @param expr
	 *            the expression of this facet
	 */
	void setFacet(final String name, final IExpressionDescription expr);

	/**
	 * Allows a {@link IFacetVisitor} to visit the facets of this element.
	 *
	 * @param visitor
	 *            a visitor
	 */
	void visitFacets(IFacetVisitor visitor);

	/**
	 * Returns the name of this element (usually the value of the expression of facet named {@link IKeyword#NAME}.
	 *
	 * @return the name of the element or null
	 */
	@Override
	String getName();

	/**
	 * Returns the EMF/Xtext element (an instance of {@link EObject} representing this element.
	 *
	 * @return the EObjet element or null if it is a synthetic element
	 */
	EObject getElement();

	/**
	 * Adds a child to this element (if this element supports children).
	 *
	 * @param e
	 *            a syntactic element
	 */
	void addChild(final ISyntacticElement e);

	/**
	 * Returns whether this element represents a species.
	 *
	 * @return true if the element is a species, false otherwise
	 */
	boolean isSpecies();

	/**
	 * Returns whether this element represents an experiment.
	 *
	 * @return true if the element is an experiment, false otherwise
	 */
	boolean isExperiment();

	/**
	 * Whether this elements has any facets.
	 *
	 * @return true if the element has at least one facet, false otherwise
	 */
	boolean hasFacets();

	/**
	 * Compute simple statistics (frequency of classes).
	 *
	 * @param stats
	 *            a map to be filled
	 */
	void computeStats(Map<String, Integer> stats);

	/**
	 * Allows a visitor to visit this element and its children.
	 *
	 * @param visitor
	 *            the visitor, not null
	 */
	void visitThisAndAllChildrenRecursively(SyntacticVisitor visitor);

	/**
	 * Allows a visitor to visit the children of this element that are neither species, grids or experiments.
	 *
	 * @param visitor
	 *            the visitor, not null
	 */
	void visitChildren(final SyntacticVisitor visitor);

	/**
	 * Allows a visitor to visit only the elements that are species (either this element or its children).
	 *
	 * @param visitor
	 *            the visitor, not null
	 */
	void visitSpecies(final SyntacticVisitor visitor);

	/**
	 * Allows a visitor to visit only the elements that are experiments (either this element or its children).
	 *
	 * @param visitor
	 *            the visitor, not null
	 */
	void visitExperiments(final SyntacticVisitor visitor);

	/**
	 * Allows a visitor to visit only the elements that are grids (either this element or its children).
	 *
	 * @param visitor
	 *            the visitor, not null
	 */
	void visitGrids(final SyntacticVisitor visitor);

	/**
	 * Compact the element by (1) setting the facets to null if they are empty; (2) compacting the map behind the facets
	 * to use as less memory as possible.
	 */
	void compact();

	/**
	 * Whether this element has children or not.
	 *
	 * @return true if this element has children
	 */
	boolean hasChildren();

	/**
	 * Allows a visitor to visit all the children of this element.
	 *
	 * @param syntacticVisitor
	 *            the syntactic visitor
	 */
	void visitAllChildren(SyntacticVisitor syntacticVisitor);

}