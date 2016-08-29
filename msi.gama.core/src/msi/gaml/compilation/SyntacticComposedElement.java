/*********************************************************************************************
 * 
 *
 * 'SyntacticComposedElement.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.compilation;

import java.util.Arrays;

import org.eclipse.emf.ecore.EObject;

import com.google.common.base.Predicate;

import msi.gaml.statements.Facets;

/**
 * The class SyntacticElement.
 * 
 * @author drogoul
 * @since 5 f�vr. 2012
 * 
 */
public class SyntacticComposedElement extends AbstractSyntacticElement {

	ISyntacticElement[] children;

	SyntacticComposedElement(final String keyword, final Facets facets, final EObject statement) {
		super(keyword, facets, statement);
	}

	@Override
	public void addChild(final ISyntacticElement e) {
		if (e == null) {
			return;
		}
		if (children == null)
			children = new ISyntacticElement[] { e };
		else {
			children = Arrays.copyOf(children, children.length + 1);
			children[children.length - 1] = e;
		}
	}

	@Override
	public void visitThisAndAllChildrenRecursively(final SyntacticVisitor visitor) {
		visitor.visit(this);
		if (children != null) {
			for (final ISyntacticElement child : children) {
				child.visitThisAndAllChildrenRecursively(visitor);
			}
		}
	}

	@Override
	protected void visitAllChildren(final SyntacticVisitor visitor) {
		if (children != null) {
			for (final ISyntacticElement e : children) {
				visitor.visit(e);
			}
		}
	}

	@Override
	public void visitChildren(final SyntacticVisitor visitor) {
		visitAllChildren(visitor, OTHER_FILTER);
	}

	protected void visitAllChildren(final SyntacticVisitor visitor, final Predicate<ISyntacticElement> filter) {
		if (children != null) {
			for (final ISyntacticElement e : children) {
				if (filter.apply(e))
					visitor.visit(e);
			}
		}
	}

}
