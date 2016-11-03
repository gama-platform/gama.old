/*********************************************************************************************
 *
 * 'PlatformSpeciesDescription.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.descriptions;

import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;

import msi.gama.common.GamaPreferences;
import msi.gama.common.interfaces.IVarAndActionSupport;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.GamaHelper;
import msi.gaml.compilation.IAgentConstructor;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.statements.Facets;

public class PlatformSpeciesDescription extends SpeciesDescription {

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
	public void copyJavaAdditions() {
		super.copyJavaAdditions();
		for (final Map.Entry<String, GamaPreferences.Entry<?>> pref : GamaPreferences.getAll().entrySet()) {
			final VariableDescription var = (VariableDescription) DescriptionFactory
					.create(pref.getValue().getType().toString(), PlatformSpeciesDescription.this, NAME, pref.getKey());
			final GamaHelper get = new GamaHelper() {

				@Override
				public Object run(final IScope scope, final IAgent agent, final IVarAndActionSupport skill,
						final Object... values) throws GamaRuntimeException {
					return GamaPreferences.get(pref.getKey()).getValue();
				}
			};
			final GamaHelper set = new GamaHelper() {

				@Override
				public Object run(final IScope scope, final IAgent agent, final IVarAndActionSupport target,
						final Object... value) throws GamaRuntimeException {
					GamaPreferences.get(pref.getKey()).setValue(scope, value[0]);
					return this;
				}

			};
			final GamaHelper init = new GamaHelper(null) {

				@Override
				public Object run(final IScope scope, final IAgent agent, final IVarAndActionSupport skill,
						final Object... values) throws GamaRuntimeException {
					return GamaPreferences.get(pref.getKey()).getValue();
				}

			};
			var.addHelpers(get, init, set);
			addChild(var);
		}
	}

}
