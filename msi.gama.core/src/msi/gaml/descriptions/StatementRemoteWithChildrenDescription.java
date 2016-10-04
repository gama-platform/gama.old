package msi.gaml.descriptions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

import msi.gaml.compilation.ISymbol;
import msi.gaml.factories.ChildrenProvider;
import msi.gaml.statements.Arguments;
import msi.gaml.statements.Facets;
import msi.gaml.types.IType;

public class StatementRemoteWithChildrenDescription extends StatementWithChildrenDescription {

	protected IDescription previousDescription;

	public StatementRemoteWithChildrenDescription(final String keyword, final IDescription superDesc,
			final ChildrenProvider cp, final boolean hasArgs, final EObject source, final Facets facets,
			final Arguments alreadyComputedArgs) {
		super(keyword, superDesc, cp, hasArgs, source, facets, alreadyComputedArgs);
	}

	@Override
	public void dispose() {
		super.dispose();
		previousDescription = null;
	}

	@Override
	public boolean validateChildren() {
		IDescription previousEnclosingDescription = null;
		try {

			final SpeciesDescription denotedSpecies = getType().getDenotedSpecies();
			if (denotedSpecies != null) {
				final SpeciesDescription s = getSpeciesContext();
				if (s != null) {
					final IType t = s.getType();
					addTemp(this, MYSELF, t);
					previousEnclosingDescription = getEnclosingDescription();
					setEnclosingDescription(denotedSpecies);

					// FIXME ===> Model Description is lost if we are
					// dealing
					// with a built-in species !
				}
			}

			return super.validateChildren();
		} finally {
			if (previousEnclosingDescription != null) {
				setEnclosingDescription(previousEnclosingDescription);
			}
		}
	}

	@Override
	public List<? extends ISymbol> compileChildren() {

		final SpeciesDescription sd = getType().getDenotedSpecies();
		if (sd != null) {
			final IType t = getSpeciesContext().getType();
			addTemp(this, MYSELF, t);
			setEnclosingDescription(sd);
		}

		return super.compileChildren();
	}

	@Override
	public StatementRemoteWithChildrenDescription copy(final IDescription into) {
		final List<IDescription> children = new ArrayList();
		visitChildren(new DescriptionVisitor<IDescription>() {

			@Override
			public boolean visit(final IDescription desc) {
				children.add(desc.copy(into));
				return true;
			}
		});

		final StatementRemoteWithChildrenDescription desc = new StatementRemoteWithChildrenDescription(getKeyword(),
				into, new ChildrenProvider(children), false, element, getFacetsCopy(),
				passedArgs == null ? null : passedArgs.cleanCopy());
		desc.originName = getOriginName();
		return desc;
	}

	@Override
	public void setEnclosingDescription(final IDescription desc) {
		previousDescription = getEnclosingDescription();
		super.setEnclosingDescription(desc);
	}

	@Override
	public ModelDescription getModelDescription() {
		ModelDescription result = super.getModelDescription();
		if (result == null && previousDescription != null) {
			result = previousDescription.getModelDescription();
		}
		return result;
	}

	@Override
	public IDescription getDescriptionDeclaringVar(final String name) {
		IDescription result = super.getDescriptionDeclaringVar(name);
		if (result == null && previousDescription != null) {
			result = previousDescription.getDescriptionDeclaringVar(name);
		}
		return result;
	}

	@Override
	public IDescription getDescriptionDeclaringAction(final String name) {
		IDescription result = super.getDescriptionDeclaringAction(name);
		if (result == null && previousDescription != null) {
			result = previousDescription.getDescriptionDeclaringAction(name);
		}
		return result;
	}

}
