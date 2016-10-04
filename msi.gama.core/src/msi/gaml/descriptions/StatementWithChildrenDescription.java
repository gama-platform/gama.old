package msi.gaml.descriptions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

import gnu.trove.map.hash.THashMap;
import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.IVarExpression;
import msi.gaml.factories.ChildrenProvider;
import msi.gaml.statements.Arguments;
import msi.gaml.statements.Facets;
import msi.gaml.types.IType;

public class StatementWithChildrenDescription extends StatementDescription {

	protected THashMap<String, IVarExpression> temps;
	protected List<IDescription> children;

	public StatementWithChildrenDescription(final String keyword, final IDescription superDesc,
			final ChildrenProvider cp, final boolean hasArgs, final EObject source, final Facets facets,
			final Arguments alreadyComputedArgs) {
		super(keyword, superDesc, cp, hasArgs, source, facets, alreadyComputedArgs);
	}

	@Override
	public boolean visitChildren(final DescriptionVisitor visitor) {
		if (children == null)
			return true;
		for (final IDescription d : children) {
			if (!visitor.visit(d))
				return false;
		}
		return true;
	}

	@Override
	public boolean visitOwnChildren(final DescriptionVisitor visitor) {
		if (children == null)
			return true;
		for (final IDescription d : children) {
			if (!visitor.visit(d))
				return false;
		}
		return true;
	}

	@Override
	public void dispose() {
		super.dispose();

		children = null;
		if (temps != null)
			temps.forEachValue(object -> {
				object.dispose();
				return true;
			});
		temps = null;
	}

	@Override
	public boolean hasAttribute(final String name) {
		return temps != null && temps.containsKey(name);
	}

	@Override
	public IExpression getVarExpr(final String name, final boolean asField) {
		if (temps != null) {
			return temps.get(name);
		}
		return null;
	}

	public boolean hasTemps() {
		return getMeta().hasScope() /* canHaveTemps */ && temps != null;
	}

	public IExpression addTemp(final IDescription declaration, final String name, final IType type) {
		// TODO Should separate validation from execution, here.

		if (!getMeta().hasScope() /* canHaveTemps */) {
			if (getEnclosingDescription() == null) {
				return null;
			}
			if (!(getEnclosingDescription() instanceof StatementWithChildrenDescription)) {
				return null;
			}

			return ((StatementWithChildrenDescription) getEnclosingDescription()).addTemp(declaration, name, type);
		}
		final String kw = getKeyword();
		final String facet = kw == IKeyword.LET || kw == IKeyword.LOOP ? IKeyword.NAME : IKeyword.RETURNS;
		if (temps == null)
			temps = new THashMap();
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
		final IExpression result = msi.gama.util.GAML.getExpressionFactory().createVar(name, type, false,
				IVarExpression.TEMP, this);
		temps.put(name, (IVarExpression) result);
		return result;
	}
	//
	// @Override
	// public void copyTempsAbove() {
	// if (!getMeta().hasScope() /* canHaveTemps */)
	// return;
	// IDescription d = getEnclosingDescription();
	// while (d != null && d instanceof StatementWithChildrenDescription) {
	// if (((StatementWithChildrenDescription) d).hasTemps()) {
	// if (temps == null)
	// temps = new THashMap(((StatementWithChildrenDescription) d).temps);
	// else
	// temps.putAll(((StatementWithChildrenDescription) d).temps);
	// }
	// d = d.getEnclosingDescription();
	// }
	// }

	@Override
	public IDescription addChild(final IDescription child) {
		final IDescription d = super.addChild(child);
		if (d != null) {
			if (children == null)
				children = new ArrayList();
			children.add(child);
		}
		return d;
	}

	@Override
	public StatementWithChildrenDescription copy(final IDescription into) {
		final List<IDescription> children = new ArrayList();
		visitChildren(new DescriptionVisitor<IDescription>() {

			@Override
			public boolean visit(final IDescription desc) {
				children.add(desc.copy(into));
				return true;
			}
		});

		final StatementWithChildrenDescription desc = new StatementWithChildrenDescription(getKeyword(), into,
				new ChildrenProvider(children), false, element, getFacetsCopy(),
				passedArgs == null ? null : passedArgs.cleanCopy());
		desc.originName = getOriginName();
		return desc;
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