/*********************************************************************************************
 *
 * 'AbstractSyntacticElement.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.compilation.ast;

import java.util.Map;

import org.eclipse.emf.ecore.EObject;

import msi.gama.common.interfaces.IKeyword;
import msi.gaml.descriptions.IDescription.IFacetVisitor;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.descriptions.SymbolProto;
import msi.gaml.statements.Facets;

/**
 * Class AbstractSyntacticElement.
 * 
 * @author drogoul
 * @since 15 sept. 2013
 * 
 */
public abstract class AbstractSyntacticElement implements ISyntacticElement {

	private Facets facets;
	private String keyword;
	final EObject element;

	AbstractSyntacticElement(final String keyword, final Facets facets, final EObject element) {
		this.keyword = keyword;
		this.facets = facets;
		this.element = element;
	}

	@Override
	public EObject getElement() {
		return element;
	}

	@Override
	public String toString() {
		return getKeyword() + " " + getName() + " " + (facets == null ? "" : facets.keySet().toString());
	}

	@Override
	public void addChild(final ISyntacticElement e) {
		throw new RuntimeException("No children allowed for " + getKeyword());
	}

	@Override
	public void setKeyword(final String name) {
		keyword = name;
	}

	@Override
	public String getKeyword() {
		return keyword;
	}

	@Override
	public final boolean hasFacets() {
		return facets != null;
	}

	@Override
	public final boolean hasFacet(final String name) {
		return facets != null && facets.contains(name);
	}

	@Override
	public final IExpressionDescription getExpressionAt(final String name) {
		return facets == null ? null : facets.get(name);
	}

	@Override
	public final Facets copyFacets(final SymbolProto sp) {
		if (facets != null) {
			final Facets ff = new Facets();
			visitFacets((a, b) -> {
				if (b != null) {
					ff.put(a, sp != null && sp.isLabel(a) ? b.cleanCopy().compileAsLabel() : b.cleanCopy());
				}
				return true;
			});
			return ff;
		}
		return null;
	}

	@Override
	public void setFacet(final String string, final IExpressionDescription expr) {
		if (expr == null) { return; }
		if (facets == null) {
			facets = new Facets();
		}
		facets.put(string, expr);
	}

	@Override
	public String getName() {
		// Default behavior. Redefined in subclasses
		final IExpressionDescription expr = getExpressionAt(IKeyword.NAME);
		return expr == null ? null : expr.toString();
	}

	protected void removeFacet(final String name) {
		if (facets == null) { return; }
		facets.remove(name);
		if (facets.isEmpty()) {
			facets = null;
		}
	}

	@Override
	public boolean isSpecies() {
		return false;
	}

	@Override
	public boolean isExperiment() {
		return false;
	}

	@Override
	public void computeStats(final Map<String, Integer> stats) {
		final String s = getClass().getSimpleName();
		if (!stats.containsKey(s)) {
			stats.put(s, 1);
		} else {
			stats.put(s, stats.get(s) + 1);
		}
		visitAllChildren(element -> element.computeStats(stats));

	}

	@Override
	public void visitFacets(final IFacetVisitor visitor) {
		if (facets == null) { return; }
		facets.forEachEntry(visitor);
	}

	@Override
	public void compact() {
		if (facets == null) { return; }
		if (facets.isEmpty()) {
			facets = null;
			return;
		}
		facets.compact();
	}

	@Override
	public void visitThisAndAllChildrenRecursively(final SyntacticVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public void visitChildren(final SyntacticVisitor visitor) {}

	@Override
	public void visitSpecies(final SyntacticVisitor visitor) {}

	@Override
	public void visitExperiments(final SyntacticVisitor visitor) {}

	@Override
	public void visitGrids(final SyntacticVisitor visitor) {}

	@Override
	public void visitAllChildren(final SyntacticVisitor visitor) {
		visitGrids(visitor);
		visitSpecies(visitor);
		visitChildren(visitor);
		// visitExperiments(visitor);
	}

	@Override
	public void dispose() {
		if (facets != null) {
			facets.dispose();
		}
	}

}