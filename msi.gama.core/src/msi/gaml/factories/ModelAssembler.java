/**
 * Created by drogoul, 15 avr. 2014
 *
 */
package msi.gaml.factories;

import static msi.gama.common.interfaces.IKeyword.ENVIRONMENT;
import static msi.gama.common.interfaces.IKeyword.FREQUENCY;
import static msi.gama.common.interfaces.IKeyword.GLOBAL;
import static msi.gama.common.interfaces.IKeyword.INIT;
import static msi.gama.common.interfaces.IKeyword.NAME;
import static msi.gama.common.interfaces.IKeyword.SCHEDULES;
import static msi.gama.common.interfaces.IKeyword.SHAPE;
import static msi.gama.common.interfaces.IKeyword.SPECIES;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import gnu.trove.map.hash.THashMap;
import gnu.trove.procedure.TObjectObjectProcedure;
import gnu.trove.procedure.TObjectProcedure;
import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.util.GAML;
import msi.gama.util.TOrderedHashMap;
import msi.gaml.compilation.GamlCompilationError;
import msi.gaml.compilation.ISyntacticElement;
import msi.gaml.compilation.SyntacticFactory;
import msi.gaml.compilation.SyntacticModelElement;
import msi.gaml.descriptions.ErrorCollector;
import msi.gaml.descriptions.ExperimentDescription;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.descriptions.SymbolDescription;
import msi.gaml.descriptions.TypeDescription;
import msi.gaml.descriptions.VariableDescription;
import msi.gaml.expressions.ConstantExpression;
import msi.gaml.statements.Facets;
import msi.gaml.types.TypeNode;
import msi.gaml.types.TypeTree;
import msi.gaml.types.Types;

/**
 * Class ModelAssembler.
 *
 * @author drogoul
 * @since 15 avr. 2014
 *
 */
public class ModelAssembler {

	/**
	 *
	 */
	public ModelAssembler() {
	}

	public ModelDescription assemble(final String projectPath, final String modelPath,
			final List<ISyntacticElement> models, final ErrorCollector collector, final boolean document,
			final Map<String, ModelDescription> mm, final Collection<URI> absoluteAlternatePaths) {
		final TOrderedHashMap<String, ISyntacticElement> speciesNodes = new TOrderedHashMap();
		TOrderedHashMap<String, TOrderedHashMap<String, ISyntacticElement>> experimentNodes = null;
		final ISyntacticElement globalNodes = SyntacticFactory.create(GLOBAL, (EObject) null, true);
		final ISyntacticElement source = models.get(0);
		Facets globalFacets = null;
		// System.out.println("Assembling " + modelPath);
		if (source.hasFacet(IKeyword.PRAGMA)) {
			final Facets facets = source.copyFacets(null);
			final List<String> pragmas = (List<String>) facets.get(IKeyword.PRAGMA).getExpression().value(null);
			collector.resetInfoAndWarning();
			if (pragmas != null) {
				if (pragmas.contains(IKeyword.NO_INFO))
					collector.setNoInfo();
				if (pragmas.contains(IKeyword.NO_WARNING))
					collector.setNoWarning();
			}

		}
		final Map<String, SpeciesDescription> tempSpeciesCache = new THashMap();

		for (int n = models.size(), i = n - 1; i >= 0; i--) {
			final SyntacticModelElement currentModel = (SyntacticModelElement) models.get(i);
			if (currentModel != null) {
				if (currentModel.hasFacets()) {
					if (globalFacets == null)
						globalFacets = new Facets(currentModel.copyFacets(null));
					else
						globalFacets.putAll(currentModel.copyFacets(null));
				}
				for (final ISyntacticElement se : currentModel.getChildren()) {
					globalNodes.addChild(se);
				}
				for (final ISyntacticElement se : currentModel.getSpecies()) {
					addSpeciesNode(se, speciesNodes, collector);
				}
				// We input the species so that grids are always the last ones
				// (see DiffusionStatement)

				for (final ISyntacticElement se : currentModel.getGrids()) {
					addSpeciesNode(se, speciesNodes, collector);
				}
				for (final ISyntacticElement ee : currentModel.getExperiments()) {
					if (experimentNodes == null)
						experimentNodes = new TOrderedHashMap();
					addExperimentNode(ee, currentModel.getName(), experimentNodes, collector);
				}
				for (final ISyntacticElement se : currentModel.getChildren()) {
					if (ENVIRONMENT.equals(se.getKeyword())) {
						collector.add(new GamlCompilationError(
								"'environment' cannot be declared anymore.Its bounds should be declared in the global section as the value of the 'shape' attribute",
								null, se.getElement(), false, false));
					}
				}
			}
		}

		final String modelName = buildModelName(source.getName());

		// We build a list of working paths from which the composite model will
		// be able to look for resources. These working paths come from the
		// imported models
		List<String> absoluteAlternatePathAsStrings = new ArrayList();

		if (!absoluteAlternatePaths.isEmpty()) {
			absoluteAlternatePathAsStrings = new ArrayList();
			for (final URI uri : absoluteAlternatePaths) {
				String path = uri.path();
				if (!path.endsWith("/"))
					path = path + "/";
				absoluteAlternatePathAsStrings.add(path);
			}
		}
		Collections.reverse(absoluteAlternatePathAsStrings);
		final ModelDescription model = new ModelDescription(modelName, null, projectPath, modelPath,
				source.getElement(), null, ModelDescription.ROOT, globalFacets, collector,
				absoluteAlternatePathAsStrings);

		// model.addSpeciesType(model);

		Collection<String> allModelNames = null;
		for (final ISyntacticElement element : models) {
			final String s = buildModelName(element.getName());
			if (!s.equals(modelName)) {
				if (allModelNames == null)
					allModelNames = new ArrayList();
				allModelNames.add(s);
			}
		}
		model.setImportedModelNames(allModelNames);
		model.isDocumenting(document);

		// hqnghi add micro-models
		if (mm != null) {
			model.setMicroModels(mm);
			model.addChildren(new ArrayList(mm.values()));
		}
		// end-hqnghi
		// recursively add user-defined species to world and down on to the
		// hierarchy
		speciesNodes.forEachValue(new TObjectProcedure<ISyntacticElement>() {

			@Override
			public boolean execute(final ISyntacticElement speciesNode) {
				addMicroSpecies(model, speciesNode, tempSpeciesCache);
				return true;
			}

		});
		if (experimentNodes != null)
			experimentNodes
					.forEachEntry(new TObjectObjectProcedure<String, TOrderedHashMap<String, ISyntacticElement>>() {

						@Override
						public boolean execute(final String s, final TOrderedHashMap<String, ISyntacticElement> b) {
							b.forEachValue(new TObjectProcedure<ISyntacticElement>() {

								@Override
								public boolean execute(final ISyntacticElement experimentNode) {
									addExperiment(s, model, experimentNode, tempSpeciesCache);
									return true;
								}
							});
							return true;
						}
					});

		// Parent the species and the experiments of the model (all are now
		// known).
		speciesNodes.forEachValue(new TObjectProcedure<ISyntacticElement>() {

			@Override
			public boolean execute(final ISyntacticElement speciesNode) {
				parentSpecies(model, speciesNode, model, tempSpeciesCache);
				return true;
			}
		});

		if (experimentNodes != null)
			experimentNodes
					.forEachEntry(new TObjectObjectProcedure<String, TOrderedHashMap<String, ISyntacticElement>>() {

						@Override
						public boolean execute(final String s, final TOrderedHashMap<String, ISyntacticElement> b) {
							b.forEachValue(new TObjectProcedure<ISyntacticElement>() {

								@Override
								public boolean execute(final ISyntacticElement experimentNode) {
									parentExperiment(model, experimentNode);
									return true;
								}
							});
							return true;
						}
					});

		// Initialize the hierarchy of types
		model.buildTypes();
		// hqnghi build micro-models as types
		if (mm != null)
			for (final Entry<String, ModelDescription> entry : mm.entrySet()) {
				model.getTypesManager().alias(entry.getValue().getName(), entry.getKey());
			}
		// end-hqnghi

		// Make species and experiments recursively create their attributes,
		// actions....
		complementSpecies(model, globalNodes);

		speciesNodes.forEachValue(new TObjectProcedure<ISyntacticElement>() {

			@Override
			public boolean execute(final ISyntacticElement speciesNode) {
				complementSpecies(model.getMicroSpecies(speciesNode.getName()), speciesNode);
				return true;
			}
		});

		if (experimentNodes != null)
			experimentNodes
					.forEachEntry(new TObjectObjectProcedure<String, TOrderedHashMap<String, ISyntacticElement>>() {

						@Override
						public boolean execute(final String s, final TOrderedHashMap<String, ISyntacticElement> b) {
							b.forEachValue(new TObjectProcedure<ISyntacticElement>() {

								@Override
								public boolean execute(final ISyntacticElement experimentNode) {
									complementSpecies(model.getExperiment(experimentNode.getName()), experimentNode);
									return true;
								}
							});
							return true;
						}
					});

		// Complement recursively the different species (incl. the world). The
		// recursion is hierarchical

		model.inheritFromParent();
		// scope.getGui().debug("ModelFactory.assemble building inheritance for
		// " + list);
		for (final SpeciesDescription sd : getSpeciesInHierarchicalOrder(model)) {
			sd.inheritFromParent();
			if (sd.isExperiment()) {
				sd.finalizeDescription();
			}
		}

		// Issue #1708 (put before the finalization)
		if (model.hasFacet(SCHEDULES) || model.hasFacet(FREQUENCY)) {
			createSchedulerSpecies(model);
		}

		model.finalizeDescription();

		// We now can safely put the model inside "experiment"
		// FIXME : Is it necessary ??
		// model.setEnclosingDescription(ModelDescription.ROOT.getSpeciesDescription(EXPERIMENT));

		// Parse the other definitions (output, environment, ...)

		VariableDescription vd = model.getAttribute(SHAPE);
		if (!vd.hasFacet(INIT)) {
			final Facets f = new Facets(NAME, SHAPE);
			// TODO Catch the right EObject (instead of null)
			f.put(INIT,
					GAML.getExpressionFactory().createOperator("envelope", model, null, new ConstantExpression(100)));
			final ISyntacticElement shape = SyntacticFactory.create(IKeyword.GEOMETRY, f, false);
			vd = (VariableDescription) DescriptionFactory.create(shape, model, null);
			model.addChild(vd);
			model.resortVarName(vd);
		}

		if (document) {
			DescriptionFactory.document(model);
		}
		return model;

	}

	private Iterable<SpeciesDescription> getSpeciesInHierarchicalOrder(final ModelDescription model) {
		final TypeTree<SpeciesDescription> hierarchy = Types.getBuiltInSpeciesTree().copy();
		final List<SpeciesDescription> speciesLeft = new ArrayList();
		model.getAllSpecies(speciesLeft);
		final List<SpeciesDescription> speciesToRemove = new ArrayList();

		while (!speciesLeft.isEmpty()) {
			for (final SpeciesDescription sd : speciesLeft) {
				if (sd instanceof ModelDescription) {
					speciesToRemove.add(sd);
				} else {
					final SpeciesDescription parent = sd.getParent();
					if (sd == parent || parent == null) {
						// Takes care of invalid species
						return Collections.EMPTY_LIST;
					} else {
						final TypeNode<SpeciesDescription> superNode = hierarchy.find(parent);
						if (superNode != null) {
							superNode.addChild(sd);
							speciesToRemove.add(sd);
						}
					}
				}
			}
			speciesLeft.removeAll(speciesToRemove);
			speciesToRemove.clear();
		}
		return Iterables.filter(hierarchy.getAllElements(TypeTree.Order.PRE_ORDER),
				new Predicate<SpeciesDescription>() {

					@Override
					public boolean apply(final SpeciesDescription input) {
						if (input.isBuiltIn() || input instanceof ModelDescription)
							return false;
						return true;
					}
				});
	}

	private void createSchedulerSpecies(final ModelDescription model) {
		final SpeciesDescription sd = (SpeciesDescription) DescriptionFactory.create(SPECIES, model, NAME,
				"_internal_global_scheduler");
		sd.finalizeDescription();
		if (model.hasFacet(SCHEDULES)) {
			model.warning(
					"'schedules' is deprecated in global. Define a dedicated species instead and add the facet to it",
					IGamlIssue.DEPRECATED, NAME);
			sd.setFacet(SCHEDULES, model.getFacet(SCHEDULES));
			model.removeFacets(SCHEDULES);
		}
		if (model.hasFacet(FREQUENCY)) {
			model.warning(
					"'frequency' is deprecated in global. Define a dedicated species instead and add the facet to it",
					IGamlIssue.DEPRECATED, NAME);
			sd.setFacet(FREQUENCY, model.getFacet(FREQUENCY));
			model.removeFacets(FREQUENCY);
		}
		model.addChild(sd);
	}

	void addExperiment(final String origin, final ModelDescription model, final ISyntacticElement experiment,
			final Map<String, SpeciesDescription> cache) {
		// Create the experiment description
		final IDescription desc = DescriptionFactory.create(experiment, model, ChildrenProvider.NONE);
		final ExperimentDescription eDesc = (ExperimentDescription) desc;
		cache.put(eDesc.getName(), eDesc);
		((SymbolDescription) desc).resetOriginName();
		desc.setOriginName(buildModelName(origin));
		model.addChild(desc);
	}

	void addExperimentNode(final ISyntacticElement element, final String modelName,
			final Map<String, TOrderedHashMap<String, ISyntacticElement>> experimentNodes,
			final ErrorCollector collector) {
		// First we verify that this experiment has not been declared previously
		final String experimentName = element.getName();
		for (final String otherModel : experimentNodes.keySet()) {
			if (!otherModel.equals(modelName)) {
				final Map<String, ISyntacticElement> otherExperiments = experimentNodes.get(otherModel);
				if (otherExperiments.containsKey(experimentName)) {
					collector.add(new GamlCompilationError(
							"Experiment " + experimentName + " supersedes the one declared in " + otherModel,
							IGamlIssue.DUPLICATE_DEFINITION, element.getElement(), false, true));
					// We remove the old one
					otherExperiments.remove(experimentName);
				}
			}
		}

		if (!experimentNodes.containsKey(modelName)) {
			experimentNodes.put(modelName, new TOrderedHashMap());
		}
		final Map<String, ISyntacticElement> nodes = experimentNodes.get(modelName);
		if (nodes.containsKey(experimentName)) {
			collector.add(new GamlCompilationError("Experiment " + element.getName() + " is declared twice",
					IGamlIssue.DUPLICATE_DEFINITION, element.getElement(), false, false));
		}
		nodes.put(experimentName, element);
	}

	void addMicroSpecies(final SpeciesDescription macro, final ISyntacticElement micro,
			final Map<String, SpeciesDescription> cache) {
		// Create the species description without any children
		final SpeciesDescription mDesc = (SpeciesDescription) DescriptionFactory.create(micro, macro,
				ChildrenProvider.NONE);
		cache.put(mDesc.getName(), mDesc);
		// Add it to its macro-species
		macro.addChild(mDesc);
		// Recursively create each micro-species of the newly added
		// micro-species
		for (final ISyntacticElement speciesNode : micro.getSpecies()) {
			if (speciesNode.isSpecies() || speciesNode.isExperiment()) {
				addMicroSpecies(mDesc, speciesNode, cache);
			}
		}
	}

	void addSpeciesNode(final ISyntacticElement element, final Map<String, ISyntacticElement> speciesNodes,
			final ErrorCollector collector) {
		final String name = element.getName();
		if (speciesNodes.containsKey(name)) {
			collector.add(new GamlCompilationError("Species " + name + " is declared twice",
					IGamlIssue.DUPLICATE_DEFINITION, element.getElement(), false, false));
			collector.add(new GamlCompilationError("Species " + name + " is declared twice",
					IGamlIssue.DUPLICATE_DEFINITION, speciesNodes.get(name).getElement(), false, false));
		}
		speciesNodes.put(name, element);
	}

	/**
	 * Recursively complements a species and its micro-species. Add variables,
	 * behaviors (actions, reflex, task, states, ...), aspects to species.
	 *
	 * @param macro
	 *            the macro-species
	 * @param micro
	 *            the structure of micro-species
	 */
	void complementSpecies(final SpeciesDescription species, final ISyntacticElement node) {
		if (species == null) {
			return;
		}
		species.copyJavaAdditions();
		for (final ISyntacticElement child : node.getChildren()) {
			final IDescription childDesc = DescriptionFactory.create(child, species, null);
			if (childDesc != null) {
				species.addChild(childDesc);
			}
		}
		// recursively complement micro-species
		for (final ISyntacticElement e : node.getSpecies()) {
			final SpeciesDescription sd = species.getMicroSpecies(e.getName());
			if (sd != null) {
				complementSpecies(sd, e);
			}
		}

	}

	void parentExperiment(final ModelDescription model, final ISyntacticElement micro) {
		// Gather the previously created species
		final SpeciesDescription mDesc = model.getExperiment(micro.getName());
		if (mDesc == null) {
			return;
		}
		final String p = mDesc.getLitteral(IKeyword.PARENT);
		// If no parent is defined, we assume it is "experiment"
		// No cache needed for experiments ??
		SpeciesDescription parent = model.getExperiment(p);
		if (parent == null) {
			parent = Types.get(IKeyword.EXPERIMENT).getSpecies();
		}
		mDesc.setParent(parent);
	}

	void parentSpecies(final SpeciesDescription macro, final ISyntacticElement micro, final ModelDescription model,
			final Map<String, SpeciesDescription> cache) {
		// Gather the previously created species
		final SpeciesDescription mDesc = cache.get(micro.getName());
		if (mDesc == null || mDesc.isExperiment()) {
			return;
		}
		String p = mDesc.getLitteral(IKeyword.PARENT);
		// If no parent is defined, we assume it is "agent"
		if (p == null) {
			p = IKeyword.AGENT;
		}
		SpeciesDescription parent = lookupSpecies(p, cache);
		// DEBUG
		if (parent == null) {
			parent = model.getSpeciesDescription(p);
		}
		mDesc.setParent(parent);
		for (final ISyntacticElement speciesNode : micro.getSpecies()) {
			parentSpecies(mDesc, speciesNode, model, cache);
		}
	}

	/**
	 * Lookup first in the cache passed in argument, then in the built-in
	 * species
	 * 
	 * @param cache
	 * @return
	 */
	SpeciesDescription lookupSpecies(final String name, final Map<String, SpeciesDescription> cache) {
		SpeciesDescription result = cache.get(name);
		if (result == null) {
			for (final TypeDescription td : Types.getBuiltInSpecies()) {
				if (td.getName().equals(name)) {
					result = (SpeciesDescription) td;
					break;
				}
			}
		}
		return result;
	}

	protected String buildModelName(final String source) {
		final String modelName = source.replace(' ', '_') + ModelDescription.MODEL_SUFFIX;
		return modelName;
	}

}
