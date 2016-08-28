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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

import com.google.common.collect.Iterables;

import msi.gaml.statements.Facets;

/**
 * The class SyntacticElement.
 * 
 * @author drogoul
 * @since 5 f�vr. 2012
 * 
 */
public class SyntacticComposedElement extends AbstractSyntacticElement {
	List<ISyntacticElement> children;

	SyntacticComposedElement(final String keyword, final Facets facets, final EObject statement) {
		super(keyword, facets, statement);
	}

	@Override
	public Iterable<ISyntacticElement> getChildren() {
		if (children == null)
			return Collections.EMPTY_LIST;
		return Iterables.filter(children, OTHER_FILTER);
	}

	@Override
	public void addChild(final ISyntacticElement e) {
		if (e == null) {
			return;
		}
		if (children == null)
			children = new ArrayList();
		children.add(e);
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

}
