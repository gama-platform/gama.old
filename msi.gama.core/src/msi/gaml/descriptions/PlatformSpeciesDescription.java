/*******************************************************************************************************
 *
 * msi.gaml.descriptions.PlatformSpeciesDescription.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.descriptions;

import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.common.preferences.Pref;
import msi.gaml.compilation.AbstractGamlAdditions;
import msi.gaml.compilation.IAgentConstructor;
import msi.gaml.compilation.IGamaHelper;
import msi.gaml.expressions.IExpression;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.statements.Facets;

public class PlatformSpeciesDescription extends SpeciesDescription {

	IVarDescriptionProvider alternateVarProvider;

	public PlatformSpeciesDescription(final String keyword, final Class<?> clazz, final SpeciesDescription macroDesc,
			final SpeciesDescription parent, final Iterable<? extends IDescription> cp, final EObject source,
			final Facets facets) {
		super(keyword, clazz, macroDesc, parent, cp, source, facets);
	}

	public PlatformSpeciesDescription(final String name, final Class<?> clazz, final SpeciesDescription superDesc,
			final SpeciesDescription parent, final IAgentConstructor<?> helper, final Set<String> skills2,
			final Facets ff, final String plugin) {
		super(name, clazz, superDesc, parent, helper, skills2, ff, plugin);
	}

	@Override
	public void attachAlternateVarDescriptionProvider(final IVarDescriptionProvider vp) {
		alternateVarProvider = vp;
	}

	@Override
	public void copyJavaAdditions() {
		super.copyJavaAdditions();
		for (final Map.Entry<String, Pref<?>> pref : GamaPreferences.getAll().entrySet()) {
			addPref(pref.getKey(), pref.getValue());
		}
	}

	public void addPref(final String key, final Pref<?> entry) {

		final VariableDescription var = (VariableDescription) DescriptionFactory.create(entry.getType().toString(),
				PlatformSpeciesDescription.this, NAME, key);
		AbstractGamlAdditions.TEMPORARY_BUILT_IN_VARS_DOCUMENTATION.put(key, entry.getTitle());
		final IGamaHelper<?> get = (scope, agent, skill, values) -> GamaPreferences.get(key).getValue();
		final IGamaHelper<?> set = (scope, agent, skill, val) -> {
			GamaPreferences.get(key).setValue(scope, val);
			return this;
		};
		final IGamaHelper<?> init = (scope, agent, skill, values) -> GamaPreferences.get(key).getValue();
		var.addHelpers(get, init, set);
		addChild(var);

	}

	@Override
	public ValidationContext getValidationContext() {
		return ValidationContext.NULL;
	}

	@Override
	public IVarDescriptionProvider getDescriptionDeclaringVar(final String name) {
		IVarDescriptionProvider provider = super.getDescriptionDeclaringVar(name);
		if (provider == null && alternateVarProvider != null && alternateVarProvider.hasAttribute(name)) {
			provider = alternateVarProvider;
		}
		return provider;
	}

	public IExpression getFakePrefExpression(final String key) {
		final VariableDescription var = (VariableDescription) DescriptionFactory.create(IKeyword.UNKNOWN,
				PlatformSpeciesDescription.this, NAME, key);
		AbstractGamlAdditions.TEMPORARY_BUILT_IN_VARS_DOCUMENTATION.put(key,
				"This preference is not available in the current configuration of GAMA");
		return var.getVarExpr(true);
	}

}
