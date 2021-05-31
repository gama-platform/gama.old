/*******************************************************************************************************
 *
 * msi.gaml.compilation.kernel.GamaMetaModel.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.compilation.kernel;

import static msi.gama.common.interfaces.IKeyword.AGENT;
import static msi.gama.common.interfaces.IKeyword.EXPERIMENT;
import static msi.gama.common.interfaces.IKeyword.MODEL;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import msi.gama.common.interfaces.IExperimentAgentCreator;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.model.GamlModelSpecies;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gaml.compilation.IAgentConstructor;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.descriptions.PlatformSpeciesDescription;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.types.GamaGenericAgentType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamaMetaModel {

	public final static GamaMetaModel INSTANCE = new GamaMetaModel();

	private final Map<String, IExperimentAgentCreator> experimentCreators = new HashMap<>();
	private final Map<String, SpeciesProto> tempSpecies = new HashMap();
	private final Multimap<String, String> speciesSkills = HashMultimap.create();
	private GamlModelSpecies abstractModelSpecies;
	public volatile boolean isInitialized;

	private static class SpeciesProto {

		final String name;
		final String plugin;
		final Class clazz;
		final IAgentConstructor helper;
		final String[] skills;

		public SpeciesProto(final String name, final Class clazz, final IAgentConstructor helper,
				final String[] skills) {
			plugin = GamaBundleLoader.CURRENT_PLUGIN_NAME;
			this.name = name;
			this.clazz = clazz;
			this.helper = helper;
			this.skills = skills;
		}
	}

	private GamaMetaModel() {}

	public ExperimentAgent createExperimentAgent(final String name, final IPopulation pop, final int index) {
		return (ExperimentAgent) experimentCreators.get(name).create(pop, index);
	}

	public void addExperimentAgentCreator(final String key, final IExperimentAgentCreator creator) {
		experimentCreators.put(key, creator);
	}

	public void addSpecies(final String name, final Class clazz, final IAgentConstructor helper,
			final String[] skills) {
		final SpeciesProto proto = new SpeciesProto(name, clazz, helper, skills);
		tempSpecies.put(name, proto);
	}

	public void build() {

		// We first build "agent" as the root of all other species (incl.
		// "model")
		final SpeciesProto ap = tempSpecies.remove(AGENT);
		// "agent" has no super-species yet
		final SpeciesDescription agent = buildSpecies(ap, null, null, false, false);
		((GamaGenericAgentType) Types.builtInTypes.get(IKeyword.AGENT)).setSpecies(agent);

		// We then build "model", sub-species of "agent"
		final SpeciesProto wp = tempSpecies.remove(MODEL);
		final ModelDescription model = (ModelDescription) buildSpecies(wp, null, agent, true, false);

		// We close the first loop by putting agent "inside" model
		agent.setEnclosingDescription(model);
		model.addChild(agent);

		// We create "experiment" as the root of all experiments, sub-species of
		// "agent"
		final SpeciesProto ep = tempSpecies.remove(EXPERIMENT);
		final SpeciesDescription experiment = buildSpecies(ep, null, agent, false, true);
		experiment.finalizeDescription();
		// Types.builtInTypes.addSpeciesType(experiment);

		// We now can attach "model" as a micro-species of "experiment"
		// model.setEnclosingDescription(experiment);
		model.addChild(experiment);

		// We then create all other built-in species and attach them to "model"
		for (final SpeciesProto proto : tempSpecies.values()) {
			model.addChild(
					buildSpecies(proto, model, agent, SimulationAgent.class.isAssignableFrom(proto.clazz), false));
		}
		tempSpecies.clear();
		model.buildTypes();
		model.finalizeDescription();
		isInitialized = true;
	}

	public SpeciesDescription buildSpecies(final SpeciesProto proto, final SpeciesDescription macro,
			final SpeciesDescription parent, final boolean isModel, final boolean isExperiment) {
		final Class clazz = proto.clazz;
		final String name = proto.name;
		final IAgentConstructor helper = proto.helper;
		final String[] skills = proto.skills;
		final String plugin = proto.plugin;
		final Set<String> allSkills = new HashSet(Arrays.asList(skills));
		allSkills.addAll(speciesSkills.get(name));
		SpeciesDescription desc;
		if (proto.name.equals(IKeyword.PLATFORM)) {
			desc = DescriptionFactory.createPlatformSpeciesDescription(name, clazz, macro, parent, helper, allSkills,
					plugin);
		} else {
			if (!isModel) {
				if (isExperiment) {
					desc = DescriptionFactory.createBuiltInExperimentDescription(name, clazz, macro, parent, helper,
							allSkills, plugin);
				} else {
					desc = DescriptionFactory.createBuiltInSpeciesDescription(name, clazz, macro, parent, helper,
							allSkills, plugin);
				}
			} else {
				// if it is a ModelDescription, then the macro represents the parent (except for root)
				desc = DescriptionFactory.createRootModelDescription(name, clazz, macro, parent, helper, allSkills,
						plugin);
			}
		}
		desc.copyJavaAdditions();
		desc.inheritFromParent();
		return desc;
	}

	public void addSpeciesSkill(final String spec, final String name) {
		speciesSkills.put(spec, name);
	}

	public GamlModelSpecies getAbstractModelSpecies() {
		if (abstractModelSpecies == null) {
			final IType model = Types.get(IKeyword.MODEL);
			abstractModelSpecies = (GamlModelSpecies) model.getSpecies().compile();
		}
		return abstractModelSpecies;
	}

	public PlatformSpeciesDescription getPlatformSpeciesDescription() {
		final IType platform = Types.get(IKeyword.PLATFORM);
		if (platform != null && platform != Types.NO_TYPE) return (PlatformSpeciesDescription) platform.getSpecies();
		return null;
	}

}
