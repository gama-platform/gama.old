/*********************************************************************************************
 *
 * 'StatementWithChildrenDescription.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.descriptions;

import java.util.Arrays;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

import com.google.common.collect.Iterables;

import gnu.trove.map.hash.THashMap;
import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.util.Collector;
import msi.gama.util.ICollector;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.IVarExpression;
import msi.gaml.statements.Arguments;
import msi.gaml.statements.Facets;
import msi.gaml.types.IType;

public class StatementWithChildrenDescription extends StatementDescription {

	protected THashMap<String, IVarExpression> temps;
	protected final ICollector<IDescription> children = new Collector.Ordered<>();

	public StatementWithChildrenDescription(final String keyword, final IDescription superDesc,
			final Iterable<IDescription> cp, final boolean hasArgs, final EObject source, final Facets facets,
			final Arguments alreadyComputedArgs) {
		super(keyword, superDesc, hasArgs, /* cp, */source, facets, alreadyComputedArgs);
		addChildren(cp);
	}

	@Override
	public boolean visitChildren(final DescriptionVisitor visitor) {
		for (final IDescription d : children) {
			if (!visitor.visit(d)) { return false; }
		}
		return true;
	}

	@Override
	public boolean visitOwnChildrenRecursively(final DescriptionVisitor visitor) {
		for (final IDescription d : children) {
			if (!visitor.visit(d)) { return false; }
			if (!d.visitOwnChildrenRecursively(visitor)) { return false; }
		}
		return true;

	}

	@Override
	public boolean visitOwnChildren(final DescriptionVisitor visitor) {
		for (final IDescription d : children) {
			if (!visitor.visit(d)) { return false; }
		}
		return true;
	}

	@Override
	public Iterable<IDescription> getOwnChildren() {
		return children;
	}

	@Override
	public void dispose() {
		super.dispose();
		children.clear();
		if (temps != null) {
			temps.forEachValue(object -> {
				object.dispose();
				return true;
			});
		}
		temps = null;
	}

	@Override
	public boolean hasAttribute(final String name) {
		return temps != null && temps.containsKey(name);
	}

	@Override
	public IExpression getVarExpr(final String name, final boolean asField) {
		if (temps != null) { return temps.get(name); }
		return null;
	}

	public boolean hasTemps() {
		return getMeta().hasScope() /* canHaveTemps */ && temps != null;
	}

	public IExpression addTemp(final IDescription declaration, final String name, final IType<?> type) {
		// TODO Should separate validation from execution, here.

		if (!getMeta().hasScope() /* canHaveTemps */) {
			if (getEnclosingDescription() == null) { return null; }
			if (!(getEnclosingDescription() instanceof StatementWithChildrenDescription)) { return null; }

			return ((StatementWithChildrenDescription) getEnclosingDescription()).addTemp(declaration, name, type);
		}
		final String kw = getKeyword();
		final String facet = kw == IKeyword.LET || kw == IKeyword.LOOP ? IKeyword.NAME : IKeyword.RETURNS;
		if (temps == null) {
			temps = new THashMap<>();
		}
		if (temps.containsKey(name) && !name.equals(MYSELF)) {
			declaration.warning("This declaration of " + name + " shadows a previous declaration",
					IGamlIssue.SHADOWS_NAME, facet);
		}
		final SpeciesDescription sd = declaration.getSpeciesContext();
		final ModelDescription md = declaration.getModelDescription();
		if (sd != null && sd != md && sd.hasAttribute(name)) {
			declaration.warning(
					"This declaration of " + name + " shadows the declaration of an attribute of " + sd.getName(),
					IGamlIssue.SHADOWS_NAME, facet);
		}
		if (md != null && md.hasAttribute(name)) {
			declaration.warning("This declaration of " + name + " shadows the declaration of a global attribute",
					IGamlIssue.SHADOWS_NAME, facet);
		}
		final IExpression result =
				msi.gama.util.GAML.getExpressionFactory().createVar(name, type, false, IVarExpression.TEMP, this);
		temps.put(name, (IVarExpression) result);
		return result;
	}

	@Override
	public IDescription addChild(final IDescription child) {
		final IDescription d = super.addChild(child);
		if (d != null) {
			children.add(child);
		}
		return d;
	}

	@Override
	public StatementWithChildrenDescription copy(final IDescription into) {
		final Iterable<IDescription> children = Iterables.transform(this.children, each -> each.copy(into));
		final StatementWithChildrenDescription desc = new StatementWithChildrenDescription(getKeyword(), into, children,
				false, element, getFacetsCopy(), passedArgs == null ? null : passedArgs.cleanCopy());
		desc.originName = getOriginName();
		return desc;
	}

	@Override
	public void replaceChildrenWith(final Iterable<IDescription> array) {
		final List<IDescription> descs = Arrays.asList(Iterables.toArray(array, IDescription.class));
		children.clear();
		children.addAll(descs);
	}

	/**
	 * @return
	 */
	public boolean isBreakable() {
		return getMeta().isBreakable();
	}

	@Override
	public IVarExpression addNewTempIfNecessary(final String facetName, final IType type) {
		if (getKeyword().equals(LOOP) && facetName.equals(NAME)) {
			// Case of loops: the variable is inside the loop (not outside)
			return (IVarExpression) addTemp(this, getLitteral(facetName), type);
		}
		return super.addNewTempIfNecessary(facetName, type);
	}

}