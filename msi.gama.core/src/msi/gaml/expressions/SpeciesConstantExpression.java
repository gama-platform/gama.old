/*********************************************************************************************
 *
 * 'SpeciesConstantExpression.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.expressions;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.precompiler.GamlProperties;
import msi.gama.runtime.IScope;
import msi.gama.util.ICollector;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.descriptions.TypeDescription;
import msi.gaml.descriptions.VariableDescription;
import msi.gaml.types.IType;

@SuppressWarnings({ "rawtypes" })
public class SpeciesConstantExpression extends ConstantExpression {

	public SpeciesConstantExpression(final String string, final IType t) {
		super(string, t);
	}

	@Override
	public Object value(final IScope scope) {
		final IAgent a = scope.getAgent();
		if (a != null) {
			// hqnghi if main description contains micro-description then
			// species comes from micro-model
			final ModelDescription micro = this.getType().getContentType().getSpecies().getModelDescription();
			final ModelDescription main = (ModelDescription) scope.getModel().getDescription();
			final Boolean fromMicroModel = main.getMicroModel(micro.getAlias()) != null;
			if (!fromMicroModel) {
				final IPopulation pop = scope.getAgent().getPopulationFor((String) value);
				if (pop != null) {
					return pop.getSpecies();
				}
				return scope.getSimulation().getModel().getSpecies((String) value);
			} else {
				final IPopulation pop = scope.getRoot().getExternMicroPopulationFor(micro.getAlias() + "." + value);
				if (pop != null) {
					return pop.getSpecies();
				}
				return scope.getSimulation().getModel().getSpecies((String) value,
						this.getType().getContentType().getSpecies());
			}
			// end-hqnghi
		}
		return null;
	}

	@Override
	public boolean isConst() {
		return false;
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		// if ( computed ) { return super.serialize(includingBuiltIn); }
		return (String) value;
	}

	@Override
	public String getDocumentation() {
		return getType().getContentType().getSpecies().getDocumentationWithoutMeta();
	}

	/**
	 * Method collectPlugins()
	 * 
	 * @see msi.gama.common.interfaces.IGamlDescription#collectPlugins(java.util.Set)
	 */
	@Override
	public void collectMetaInformation(final GamlProperties meta) {
		final SpeciesDescription sd = getType().getContentType().getSpecies();
		if (sd != null) {
			meta.put(GamlProperties.PLUGINS, sd.getDefiningPlugin());
			if (sd.isBuiltIn()) {
				meta.put(GamlProperties.SPECIES, (String) value);
			}
		}
	}

	@Override
	public void collectUsedVarsOf(final IDescription species, final ICollector<VariableDescription> result) {
		if (species.hasAttribute(value.toString()))
			result.add(((TypeDescription) species).getAttribute(value.toString()));
	}

}
