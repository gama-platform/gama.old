/*******************************************************************************************************
 *
 * msi.gaml.compilation.ast.SyntacticComposedElement.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.compilation.ast;

import java.util.Arrays;
import java.util.function.Predicate;

import org.eclipse.emf.ecore.EObject;

import msi.gaml.operators.Strings;
import msi.gaml.statements.Facets;

/**
 * The class SyntacticElement.
 *
 * @author drogoul
 * @since 5 f�vr. 2012
 *
 */
public class SyntacticComposedElement extends AbstractSyntacticElement {

	/**
	 * The children.
	 */
	ISyntacticElement[] children;

	/**
	 * Instantiates a new syntactic composed element.
	 *
	 * @param keyword
	 *            the keyword
	 * @param facets
	 *            the facets
	 * @param statement
	 *            the statement
	 */
	SyntacticComposedElement(final String keyword, final Facets facets, final EObject statement) {
		super(keyword, facets, statement);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gaml.compilation.ast.AbstractSyntacticElement#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		visitAllChildren(c -> sb.append(Strings.LN).append(Strings.TAB).append(c.toString()));
		return super.toString() + sb;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gaml.compilation.ast.AbstractSyntacticElement#addChild(msi.gaml.compilation.ast.ISyntacticElement)
	 */
	@Override
	public void addChild(final ISyntacticElement e) {

		if (e == null) { return; }
		if (children == null) {
			children = new ISyntacticElement[] { e };
		} else {
			children = Arrays.copyOf(children, children.length + 1);
			children[children.length - 1] = e;
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * msi.gaml.compilation.ast.AbstractSyntacticElement#visitThisAndAllChildrenRecursively(msi.gaml.compilation.ast.
	 * ISyntacticElement.SyntacticVisitor)
	 */
	@Override
	public void visitThisAndAllChildrenRecursively(final SyntacticVisitor visitor) {
		visitor.visit(this);
		if (children != null) {
			for (final ISyntacticElement child : children) {
				child.visitThisAndAllChildrenRecursively(visitor);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gaml.compilation.ast.AbstractSyntacticElement#visitChildren(msi.gaml.compilation.ast.ISyntacticElement.
	 * SyntacticVisitor)
	 */
	@Override
	public void visitChildren(final SyntacticVisitor visitor) {
		visitAllChildren(visitor, OTHER_FILTER);
	}

	/**
	 * Visit all children.
	 *
	 * @param visitor
	 *            the visitor
	 * @param filter
	 *            the filter
	 */
	protected void visitAllChildren(final SyntacticVisitor visitor, final Predicate<ISyntacticElement> filter) {
		if (children != null) {
			for (final ISyntacticElement e : children) {
				if (filter.test(e)) {
					visitor.visit(e);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gaml.compilation.ast.ISyntacticElement#hasChildren()
	 */
	@Override
	public boolean hasChildren() {
		return children != null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gaml.compilation.ast.AbstractSyntacticElement#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		if (children != null) {
			for (final ISyntacticElement e : children) {
				e.dispose();
			}
		}
		children = null;
	}

	// Added to fix Issue #2619
	@Override
	public void visitSpecies(final SyntacticVisitor visitor) {
		visitAllChildren(visitor, SPECIES_FILTER);
	}

	// Added to fix Issue #2619
	@Override
	public void visitGrids(final SyntacticVisitor visitor) {
		visitAllChildren(visitor, GRID_FILTER);
	}

}
