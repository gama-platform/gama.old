/*********************************************************************************************
 * 
 *
 * 'AbstractSyntacticElement.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.compilation;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;

import gnu.trove.procedure.TObjectObjectProcedure;
import msi.gama.common.interfaces.IKeyword;
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
	public Iterable<ISyntacticElement> getChildren() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public Iterable<ISyntacticElement> getSpecies() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public Iterable<ISyntacticElement> getExperiments() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public Iterable<ISyntacticElement> getGrids() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public void addChild(final ISyntacticElement e) {
		throw new RuntimeException("No children allowed");
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
	public void setDependencies(final Set<String> strings) {
	}

	@Override
	public Set<String> getDependencies() {
		return Collections.EMPTY_SET;
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
			facets.forEachEntry(new TObjectObjectProcedure<String, IExpressionDescription>() {

				@Override
				public boolean execute(final String a, final IExpressionDescription b) {
					if (b != null)
						ff.put(a, sp != null && sp.isLabel(a) ? b.cleanCopy().compileAsLabel() : b.cleanCopy());
					return true;
				}
			});
			return ff;
		}
		return null;
	}

	@Override
	public void setFacet(final String string, final IExpressionDescription expr) {
		if (expr == null)
			return;
		if (facets == null)
			facets = new Facets();
		facets.put(string, expr);
	}

	@Override
	public String getName() {
		// Default behavior. Redefined in subclasses
		final IExpressionDescription expr = getExpressionAt(IKeyword.NAME);
		return expr == null ? null : expr.toString();
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
		for (final ISyntacticElement e : this.getChildren())
			e.computeStats(stats);
		for (final ISyntacticElement e : this.getSpecies())
			e.computeStats(stats);
		for (final ISyntacticElement e : this.getExperiments())
			e.computeStats(stats);
		for (final ISyntacticElement e : this.getGrids())
			e.computeStats(stats);
	}

}