/**
 * Created by drogoul, 15 avr. 2014
 *
 */
package msi.gaml.factories;

import static msi.gama.common.interfaces.IKeyword.BOUNDS;
import static msi.gama.common.interfaces.IKeyword.DEPENDS_ON;
import static msi.gama.common.interfaces.IKeyword.ENVIRONMENT;
import static msi.gama.common.interfaces.IKeyword.EXPERIMENT;
import static msi.gama.common.interfaces.IKeyword.FREQUENCY;
import static msi.gama.common.interfaces.IKeyword.GEOMETRY;
import static msi.gama.common.interfaces.IKeyword.GLOBAL;
import static msi.gama.common.interfaces.IKeyword.GRID;
import static msi.gama.common.interfaces.IKeyword.HEIGHT;
import static msi.gama.common.interfaces.IKeyword.INIT;
import static msi.gama.common.interfaces.IKeyword.NAME;
import static msi.gama.common.interfaces.IKeyword.POINT;
import static msi.gama.common.interfaces.IKeyword.SCHEDULES;
import static msi.gama.common.interfaces.IKeyword.SHAPE;
import static msi.gama.common.interfaces.IKeyword.SPECIES;
import static msi.gama.common.interfaces.IKeyword.TORUS;
import static msi.gama.common.interfaces.IKeyword.WIDTH;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

import gnu.trove.map.hash.THashMap;
import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.util.GAML;
import msi.gama.util.TOrderedHashMap;
import msi.gaml.compilation.GamlCompilationError;
import msi.gaml.compilation.ISyntacticElement;
import msi.gaml.compilation.SyntacticFactory;
import msi.gaml.descriptions.ConstantExpressionDescription;
import msi.gaml.descriptions.ErrorCollector;
import msi.gaml.descriptions.ExperimentDescription;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.descriptions.OperatorExpressionDescription;
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
		final Map<String, ISyntacticElement> speciesNodes = new TOrderedHashMap();
		final Map<String, Map<String, ISyntacticElement>> experimentNodes = new TOrderedHashMap();
		final ISyntacticElement globalNodes = SyntacticFactory.create(GLOBAL, (EObject) null, true);
		final ISyntacticElement source = models.get(0);
		final Facets globalFacets = new Facets();
		final List<ISyntacticElement> otherNodes = new ArrayList();
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
			final ISyntacticElement currentModel = models.get(i);
			if (currentModel != null) {
				for (final ISyntacticElement se : currentModel.getChildren()) {
					if (se.isGlobal()) {
						// We build the facets resulting from the different
						// arguments
						globalFacets.putAll(se.copyFacets(null));
						for (final ISyntacticElement ge : se.getChildren()) {
							if (ge.isSpecies()) {
								addSpeciesNode(ge, speciesNodes, collector);
							} else if (ge.isExperiment()) {
								addExperimentNode(ge, currentModel.getName(), experimentNodes, collector);
							} else {
								globalNodes.addChild(ge);
							}
						}

					} else if (se.isSpecies()) {
						addSpeciesNode(se, speciesNodes, collector);
					} else if (se.isExperiment()) {
						addExperimentNode(se, currentModel.getName(), experimentNodes, collector);
					} else {
						if (!ENVIRONMENT.equals(se.getKeyword())) {
							collector.add(new GamlCompilationError(
									"This " + se.getKeyword()
											+ " should be declared either in a species or in the global section",
									null, se.getElement(), true, false));
						}
						otherNodes.add(se);
					}
				}
			}
		}

		final String modelName = buildModelName(source.getName());
		final Set<String> allModelNames = new LinkedHashSet();
		allModelNames.add(modelName);
		for (final ISyntacticElement element : models) {
			allModelNames.add(buildModelName(element.getName()));
		}
		globalFacets.putAsLabel(NAME, modelName);

		// We first sort the species so that grids are always the last ones (see
		// SignalVariable)
		for (final ISyntacticElement speciesNode : new ArrayList<ISyntacticElement>(speciesNodes.values())) {
			if (speciesNode.getKeyword().equals(GRID)) {
				speciesNodes.remove(speciesNode.getName());
				speciesNodes.put(speciesNode.getName(), speciesNode);
			}
		}

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
		final ModelDescription model = new ModelDescription(modelName, null, projectPath,
				modelPath, /* lastGlobalNode.getElement() */
				source.getElement(), null, ModelDescription.ROOT, globalFacets, collector,
				absoluteAlternatePathAsStrings);

		// model.setGlobal(true);
		model.addSpeciesType(model);
		model.setImportedModelNames(allModelNames);
		model.isDocumenting(document);

		// hqnghi add micro-models
		// if ( mm != null ) {
		model.setMicroModels(mm);
		model.addChildren(new ArrayList(mm.values()));
		// }
		// end-hqnghi
		// recursively add user-defined species to world and down on to the
		// hierarchy
		for (final ISyntacticElement speciesNode : speciesNodes.values()) {
			addMicroSpecies(model, speciesNode, tempSpeciesCache);
		}
		for (final String s : experimentNodes.keySet()) {
			for (final ISyntacticElement experimentNode : experimentNodes.get(s).values()) {
				addExperiment(s, model, experimentNode, tempSpeciesCache);
			}
		}

		// Parent the species and the experiments of the model (all are now
		// known).
		for (final ISyntacticElement speciesNode : speciesNodes.values()) {
			parentSpecies(model, speciesNode, model, tempSpeciesCache);
		}
		for (final String s : experimentNodes.keySet()) {
			for (final ISyntacticElement experimentNode : experimentNodes.get(s).values()) {
				parentExperiment(model, experimentNode, model, tempSpeciesCache);
			}
		}
		// Initialize the hierarchy of types
		model.buildTypes();
		// hqnghi build micro-models as types
		for (final Entry<String, ModelDescription> entry : mm.entrySet()) {
			model.getTypesManager().alias(entry.getValue().getName(), entry.getKey());
		}
		// end-hqnghi

		// Make species and experiments recursively create their attributes,
		// actions....
		complementSpecies(model, globalNodes);
		for (final ISyntacticElement speciesNode : speciesNodes.values()) {
			complementSpecies(model.getMicroSpecies(speciesNode.getName()), speciesNode);
		}
		for (final String s : experimentNodes.keySet()) {
			for (final ISyntacticElement experimentNode : experimentNodes.get(s).values()) {
				complementSpecies(model.getExperiment(experimentNode.getName()), experimentNode);
			}
		}

		// Complement recursively the different species (incl. the world). The
		// recursion is hierarchical
		final TypeTree<SpeciesDescription> hierarchy = model.getTypesManager().getSpeciesHierarchy();
		// scope.getGui().debug("Hierarchy: " + hierarchy.toStringWithDepth());
		final List<TypeNode<SpeciesDescription>> list = hierarchy.build(TypeTree.Order.PRE_ORDER);

		model.inheritFromParent();
		// scope.getGui().debug("ModelFactory.assemble building inheritance for
		// " + list);
		for (final TypeNode<SpeciesDescription> node : list) {

			final SpeciesDescription sd = node.getData();
			if (!sd.isBuiltIn()) {
				// scope.getGui().debug("Copying Java additions and parent
				// additions to " + sd.getName());
				sd.inheritFromParent();
				if (sd.isExperiment()) {
					sd.finalizeDescription();
				}
			}
		}

		// Issue #1708 (put before the finalization)
		if (model.getFacets().contains(SCHEDULES) || model.getFacets().contains(FREQUENCY)) {
			createSchedulerSpecies(model);
		}

		model.finalizeDescription();

		// We now can safely put the model inside "experiment"
		model.setEnclosingDescription(ModelDescription.ROOT.getSpeciesDescription(EXPERIMENT));

		// Parse the other definitions (output, environment, ...)
		boolean environmentDefined = false;
		for (final ISyntacticElement e : otherNodes) {
			// COMPATIBILITY to remove the environment and put its definition in
			// the world
			if (ENVIRONMENT.equals(e.getKeyword())) {
				environmentDefined = translateEnvironment(model, e);
			} else {
				//
				final IDescription dd = DescriptionFactory.create(e, model, null);
				if (dd != null) {
					model.addChild(dd);
				}
			}
		}
		if (!environmentDefined) {
			VariableDescription vd = model.getVariable(SHAPE);
			if (!vd.getFacets().containsKey(INIT)) {
				final Facets f = new Facets(NAME, SHAPE);
				// TODO Catch the right EObject (instead of null)
				f.put(INIT, GAML.getExpressionFactory().createOperator("envelope", model, null,
						new ConstantExpression(100)));
				final ISyntacticElement shape = SyntacticFactory.create(IKeyword.GEOMETRY, f, false);
				vd = (VariableDescription) DescriptionFactory.create(shape, model, null);
				model.addChild(vd);
				model.resortVarName(vd);
			}
		}

		if (document) {
			DescriptionFactory.document(model);
		}
		return model;

	}

	private void createSchedulerSpecies(final ModelDescription model) {
		final SpeciesDescription sd = (SpeciesDescription) DescriptionFactory.create(SPECIES, model, NAME,
				"_internal_global_scheduler");
		sd.finalizeDescription();
		if (model.getFacets().contains(SCHEDULES)) {
			model.warning(
					"'schedules' is deprecated in global. Define a dedicated species instead and add the facet to it",
					IGamlIssue.DEPRECATED, NAME);
			sd.getFacets().put(SCHEDULES, model.getFacets().get(SCHEDULES));
			model.getFacets().remove(SCHEDULES);
		}
		if (model.getFacets().contains(FREQUENCY)) {
			model.warning(
					"'frequency' is deprecated in global. Define a dedicated species instead and add the facet to it",
					IGamlIssue.DEPRECATED, NAME);
			sd.getFacets().put(FREQUENCY, model.getFacets().get(FREQUENCY));
			model.getFacets().remove(FREQUENCY);
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
			final Map<String, Map<String, ISyntacticElement>> experimentNodes, final ErrorCollector collector) {
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
		for (final ISyntacticElement speciesNode : micro.getChildren()) {
			if (speciesNode.isSpecies() || speciesNode.isExperiment()) {
				// forces the micro-species to be created
				macro.getMicroSpecies();
				break;
			}
		}
		// Add it to its macro-species
		macro.addChild(mDesc);
		// Recursively create each micro-species of the newly added
		// micro-species
		for (final ISyntacticElement speciesNode : micro.getChildren()) {
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
		speciesNodes.put(element.getName(), element);
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
		// scope.getGui().debug("++++++ Building variables & behaviors of " +
		// species.getName());
		final List<ISyntacticElement> subspecies = new ArrayList();
		for (final ISyntacticElement child : node.getChildren()) {
			if (!child.isExperiment() && !child.isSpecies()) {
				final IDescription childDesc = DescriptionFactory.create(child, species, null);
				if (childDesc != null) {
					species.addChild(childDesc);
				}
			} else {
				subspecies.add(child);
			}
		}
		// recursively complement micro-species
		for (final ISyntacticElement e : subspecies) {
			final SpeciesDescription sd = species.getMicroSpecies(e.getName());
			if (sd != null) {
				complementSpecies(sd, e);
			}
		}

	}

	void parentExperiment(final ModelDescription macro, final ISyntacticElement micro, final ModelDescription model,
			final Map<String, SpeciesDescription> cache) {
		// Gather the previously created species
		final SpeciesDescription mDesc = macro.getExperiment(micro.getName());
		if (mDesc == null) {
			return;
		}
		final String p = mDesc.getFacets().getLabel(IKeyword.PARENT);
		// If no parent is defined, we assume it is "experiment"
		// No cache needed for experiments ??
		SpeciesDescription parent = model.getExperiment(p);
		if (parent == null) {
			parent = (SpeciesDescription) ModelDescription.ROOT.getTypesManager().getSpecies(IKeyword.EXPERIMENT);
		}
		mDesc.setParent(parent);
		// for ( SyntacticElement speciesNode : micro.getSpeciesChildren() ) {
		// parentSpecies(mDesc, speciesNode, model);
		// }
	}

	void parentSpecies(final SpeciesDescription macro, final ISyntacticElement micro, final ModelDescription model,
			final Map<String, SpeciesDescription> cache) {
		// Gather the previously created species
		final SpeciesDescription mDesc = cache.get(micro.getName());
		// final SpeciesDescription mDesc =
		// macro.getMicroSpecies(micro.getName());
		if (mDesc == null || mDesc.isExperiment()) {
			return;
		}
		String p = mDesc.getFacets().getLabel(IKeyword.PARENT);
		// If no parent is defined, we assume it is "agent"
		if (p == null) {
			p = IKeyword.AGENT;
		}
		SpeciesDescription parent = lookupSpecies(p, cache);
		// DEBUG
		if (parent == null) {
			System.out.println("Null parent for species " + micro.getName());
			parent = model.getSpeciesDescription(p);
		}
		mDesc.setParent(parent);
		for (final ISyntacticElement speciesNode : micro.getChildren()) {
			if (speciesNode.isSpecies() || speciesNode.isExperiment()) {
				parentSpecies(mDesc, speciesNode, model, cache);
			}
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
			final Collection<TypeDescription> builtIn = Types.getBuiltInSpecies();
			for (final TypeDescription td : builtIn) {
				if (td.getName().equals(name)) {
					result = (SpeciesDescription) td;
					break;
				}
			}
		}
		return result;
	}

	boolean translateEnvironment(final SpeciesDescription world, final ISyntacticElement e) {
		final boolean environmentDefined = true;
		final ISyntacticElement shape = SyntacticFactory.create(GEOMETRY, new Facets(NAME, SHAPE), false);
		IExpressionDescription bounds = e.getExpressionAt(BOUNDS);
		if (bounds == null) {
			final IExpressionDescription width = e.getExpressionAt(WIDTH);
			final IExpressionDescription height = e.getExpressionAt(HEIGHT);
			if (width != null && height != null) {
				bounds = new OperatorExpressionDescription(POINT, width, height);
			} else {
				bounds = ConstantExpressionDescription.create(100);
			}
		}
		bounds = new OperatorExpressionDescription("envelope", bounds);
		shape.setFacet(INIT, bounds);
		final IExpressionDescription depends = e.getExpressionAt(DEPENDS_ON);
		if (depends != null) {
			shape.setFacet(DEPENDS_ON, depends);
		}
		final VariableDescription vd = (VariableDescription) DescriptionFactory.create(shape, world, null);
		world.addChild(vd);
		world.resortVarName(vd);
		final IExpressionDescription ed = e.getExpressionAt(TORUS);
		// TODO Is the call to compilation correct at that point ?
		if (ed != null) {
			world.getFacets().put(TORUS, ed.compile(world));
		}
		return environmentDefined;
	}

	protected String buildModelName(final String source) {
		final String modelName = source.replace(' ', '_') + ModelDescription.MODEL_SUFFIX;
		return modelName;
	}

}
