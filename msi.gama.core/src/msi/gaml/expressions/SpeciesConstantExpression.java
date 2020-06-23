/*******************************************************************************************************
 *
 * msi.gaml.expressions.SpeciesConstantExpression.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.expressions;

import msi.gama.kernel.model.IModel;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.runtime.IScope;
import msi.gama.util.ICollector;
import msi.gaml.descriptions.IVarDescriptionUser;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.descriptions.VariableDescription;
import msi.gaml.types.IType;

@SuppressWarnings ({ "rawtypes" })
public class SpeciesConstantExpression extends ConstantExpression {

	public SpeciesConstantExpression(final String string, final IType t) {
		super(string, t);
	}

	@Override
	public Object _value(final IScope scope) {
		final IAgent a = scope.getAgent();
		if (a != null) {
			// hqnghi if main description contains micro-description then
			// species comes from micro-model
			final IModel m = scope.getModel();
			final ModelDescription micro = this.getGamlType().getContentType().getSpecies().getModelDescription();
			final ModelDescription main = m == null ? null : (ModelDescription) scope.getModel().getDescription();
			final Boolean fromMicroModel = main == null || main.getMicroModel(micro.getAlias()) != null;
			if (!fromMicroModel) {
				final IPopulation pop = a.getPopulationFor((String) value);
				if (pop != null) { return pop.getSpecies(); }
				return scope.getModel().getSpecies((String) value);
			} else {
				final IPopulation pop = scope.getRoot().getExternMicroPopulationFor(micro.getAlias() + "." + value);
				if (pop != null) { return pop.getSpecies(); }
				return scope.getModel().getSpecies((String) value, this.getGamlType().getContentType().getSpecies());
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
		return getGamlType().getContentType().getSpecies().getDocumentationWithoutMeta();
	}

	/**
	 * Method collectPlugins()
	 *
	 * @see msi.gama.common.interfaces.IGamlDescription#collectPlugins(java.util.Set)
	 */
	// @Override
	// public void collectMetaInformation(final GamlProperties meta) {
	// final SpeciesDescription sd = getGamlType().getContentType().getSpecies();
	// if (sd != null) {
	// meta.put(GamlProperties.PLUGINS, sd.getDefiningPlugin());
	// if (sd.isBuiltIn()) {
	// meta.put(GamlProperties.SPECIES, (String) value);
	// }
	// }
	// }

	@Override
	public boolean isContextIndependant() {
		return false;
	}

	@Override
	public void collectUsedVarsOf(final SpeciesDescription species,
			final ICollector<IVarDescriptionUser> alreadyProcessed, final ICollector<VariableDescription> result) {
		if (alreadyProcessed.contains(this)) { return; }
		alreadyProcessed.add(this);
		if (species.hasAttribute(value.toString())) {
			result.add(species.getAttribute(value.toString()));
		}
	}

}
