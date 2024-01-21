/*******************************************************************************************************
 *
 * StatementRemoteWithChildrenDescription.java, in msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.descriptions;

import org.eclipse.emf.ecore.EObject;

import com.google.common.collect.Iterables;

import msi.gaml.compilation.ISymbol;
import msi.gaml.statements.Arguments;
import msi.gaml.statements.Facets;
import msi.gaml.types.IType;

/**
 * The Class StatementRemoteWithChildrenDescription.
 */
@SuppressWarnings ({ "rawtypes" })
public class StatementRemoteWithChildrenDescription extends StatementWithChildrenDescription {

	/** The previous description. */
	protected IDescription alternateEnclosingDescription;

	/**
	 * Instantiates a new statement remote with children description.
	 *
	 * @param keyword
	 *            the keyword
	 * @param enclosing
	 *            the enclosing description
	 * @param children
	 *            the children to be added to the description
	 * @param hasArgs
	 *            whether the description has arguments
	 * @param source
	 *            the source of the description (from XText)
	 * @param facets
	 *            the facets of the description
	 * @param alreadyComputedArgs
	 *            the args that has already been computed
	 */
	public StatementRemoteWithChildrenDescription(final String keyword, final IDescription enclosing,
			final Iterable<IDescription> children, final boolean hasArgs, final EObject source, final Facets facets,
			final Arguments alreadyComputedArgs) {
		super(keyword, enclosing, children, hasArgs, source, facets, alreadyComputedArgs);
	}

	@Override
	public void dispose() {
		super.dispose();
		alternateEnclosingDescription = null;
	}

	@Override
	public boolean validateChildren() {
		IDescription previousEnclosingDescription = null;
		try {
			previousEnclosingDescription = pushRemoteContext();
			return super.validateChildren();
		} finally {
			popRemoteContext(previousEnclosingDescription);
		}
	}

	@Override
	public Iterable<? extends ISymbol> compileChildren() {

		final SpeciesDescription sd = getGamlType().getDenotedSpecies(this);
		if (sd != null) {
			final IType t = getSpeciesContext().getGamlType();
			addTemp(this, MYSELF, t);
			setEnclosingDescription(sd);
		}

		return super.compileChildren();
	}

	@Override
	public StatementRemoteWithChildrenDescription copy(final IDescription into) {
		final Iterable<IDescription> children =
				this.children != null ? Iterables.transform(this.children, each -> each.copy(into)) : null;
		final StatementRemoteWithChildrenDescription desc = new StatementRemoteWithChildrenDescription(getKeyword(),
				into, children, false, element, getFacetsCopy(), passedArgs == null ? null : passedArgs.cleanCopy());
		desc.originName = getOriginName();
		return desc;
	}

	@Override
	public void setEnclosingDescription(final IDescription desc) {
		alternateEnclosingDescription = getEnclosingDescription();
		super.setEnclosingDescription(desc);
	}

	@Override
	public ModelDescription getModelDescription() {
		ModelDescription result = super.getModelDescription();
		if (result == null && alternateEnclosingDescription != null) {
			result = alternateEnclosingDescription.getModelDescription();
		}
		return result;
	}

	@Override
	public IVarDescriptionProvider getDescriptionDeclaringVar(final String name) {
		IVarDescriptionProvider result = super.getDescriptionDeclaringVar(name);
		if (result == null && alternateEnclosingDescription != null) {
			result = alternateEnclosingDescription.getDescriptionDeclaringVar(name);
		}
		return result;
	}

	@Override
	public IDescription getDescriptionDeclaringAction(final String name, final boolean superInvocation) {
		IDescription result = super.getDescriptionDeclaringAction(name, superInvocation);
		if (result == null && alternateEnclosingDescription != null) {
			result = alternateEnclosingDescription.getDescriptionDeclaringAction(name, superInvocation);
		}
		return result;
	}

	/**
	 * Push remote context.
	 *
	 * @return the i description
	 */
	public IDescription pushRemoteContext() {
		final SpeciesDescription denotedSpecies = getGamlType().getDenotedSpecies(this);
		IDescription previousEnclosingDescription = null;
		if (denotedSpecies != null) {
			final SpeciesDescription s = getSpeciesContext();
			if (s != null) {
				final IType t = s.getGamlType();
				addTemp(this, MYSELF, t);
				previousEnclosingDescription = getEnclosingDescription();
				setEnclosingDescription(denotedSpecies);
				// FIXME ===> Model Description is lost if we are dealing with a built-in species !
			}
		}
		// else {
		// DEBUG.LOG("Impossible to push remote context as the denoted species is null in " + this);
		// }
		return previousEnclosingDescription;
	}

	/**
	 * Pop remote context.
	 *
	 * @param previousEnclosingDescription
	 *            the previous enclosing description
	 */
	public void popRemoteContext(final IDescription previousEnclosingDescription) {

		if (previousEnclosingDescription != null) { setEnclosingDescription(previousEnclosingDescription); }

	}

}
