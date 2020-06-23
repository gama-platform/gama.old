/*******************************************************************************************************
 *
 * msi.gama.kernel.model.GamlModelSpecies.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling
 * and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.kernel.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.apache.commons.lang.StringUtils;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.outputs.AbstractOutputManager;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.util.GamaMapFactory;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.species.GamlSpecies;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.IStatement;
import msi.gaml.statements.test.TestStatement;
import msi.gaml.types.IType;

@symbol (
		name = { IKeyword.MODEL },
		kind = ISymbolKind.MODEL,
		with_sequence = true,
		internal = true,
		concept = { IConcept.MODEL })
@facets (
		value = { @facet (
				name = IKeyword.VERSION,
				type = IType.ID,
				optional = true,
				doc = @doc ("The version of this model")),
				@facet (
						name = IKeyword.AUTHOR,
						type = IType.ID,
						optional = true,
						doc = @doc ("The author of the model")),
				@facet (
						name = IKeyword.PRAGMA,
						type = IType.LIST,
						of = IType.STRING,
						optional = true,
						internal = true,
						doc = @doc ("For internal use only")),
				@facet (
						name = IKeyword.TORUS,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("Whether the model will be based on a toroidal environment or not")),
				@facet (
						name = IKeyword.NAME,
						type = IType.ID,
						optional = false,
						doc = @doc ("The name of the model")),
				@facet (
						name = IKeyword.PARENT,
						type = IType.ID,
						optional = true,
						doc = @doc ("Whether this model inherits from another one or not (must be in the same project and folder)")),
				@facet (
						name = IKeyword.SKILLS,
						type = IType.LIST,
						optional = true,
						doc = @doc ("The list of skills attached to this model")),
				@facet (
						name = IKeyword.CONTROL,
						type = IType.SKILL,
						optional = true,
						doc = @doc ("The control architecture attached to this model")),
				@facet (
						name = IKeyword.FREQUENCY,
						type = IType.INT,
						optional = true,
						doc = @doc ("Specifies how often the model (e.g. every x cycles) will be asked to execute")),
				@facet (
						name = IKeyword.SCHEDULES,
						type = IType.CONTAINER,
						of = IType.AGENT,
						doc = @doc ("A container of agents (a species, a dynamic list, or a combination of species and containers) , which represents which agents will be actually scheduled when the population is scheduled for execution. For instance, 'species a schedules: (10 among a)' will result in a population that schedules only 10 of its own agents every cycle. 'species b schedules: []' will prevent the agents of 'b' to be scheduled. Note that the scope of agents covered here can be larger than the population, which allows to build complex scheduling controls; for instance, defining 'global schedules: [] {...} species b schedules: []; species c schedules: b + world; ' allows to simulate a model where the agents of b are scheduled first, followed by the world, without even having to create an instance of c."),
						optional = true),
				@facet (
						name = IKeyword.TOPOLOGY,
						type = IType.TOPOLOGY,
						optional = true,
						doc = @doc ("The topology of this model. Can be used to specify boundaries (although it is preferred to set the shape attribute).")) },
		omissible = IKeyword.NAME)
@doc ("A model is a species that is used to specify the 'world' of all the agents in the model. The corresponding population is hosted by experiments and accessible by the keyword 'simulations' (or 'simulation' to get the most recently created one)")
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamlModelSpecies extends GamlSpecies implements IModel {

	protected final Map<String, IExperimentPlan> experiments = GamaMapFactory.create();
	protected final Map<String, IExperimentPlan> titledExperiments = GamaMapFactory.create();
	protected Map<String, ISpecies> allSpecies;

	public GamlModelSpecies(final IDescription description) {
		super(description);
		setName(description.getName());
	}

	@Override
	public ModelDescription getDescription() {
		return (ModelDescription) description;
	}

	@Override
	public Collection<String> getImportedPaths() {
		return getDescription().getAlternatePaths();
	}

	@Override
	public String getWorkingPath() {
		return getDescription().getModelFolderPath();
	}

	@Override
	public String getFilePath() {
		return getDescription().getModelFilePath();
	}

	@Override
	public String getProjectPath() {
		return getDescription().getModelProjectPath();
	}

	protected void addExperiment(final IExperimentPlan exp) {
		if (exp == null) { return; }
		experiments.put(exp.getName(), exp);
		titledExperiments.put(exp.getFacet(IKeyword.TITLE).literalValue(), exp);
		exp.setModel(this);
	}

	@Override
	public IExperimentPlan getExperiment(final String s) {
		// First we try to get it using its "internal" name
		IExperimentPlan e = experiments.get(s);
		if (e == null) {
			// Otherwise with its title
			e = titledExperiments.get(s);
			if (e == null) {
				// Finally, if the string is an int, we try to get the n-th
				// experiment
				if (StringUtils.isNumeric(s)) {
					final int i = Integer.parseInt(s);
					final List<String> names = new ArrayList(experiments.keySet());
					if (names.size() > 0) {
						e = getExperiment(names.get(i));
					}
				}
			}
		}
		return e;
	}

	@Override
	public void dispose() {
		super.dispose();
		for (final IExperimentPlan exp : experiments.values()) {
			exp.dispose();
		}
		experiments.clear();
		titledExperiments.clear();
		if (allSpecies != null) {
			allSpecies.clear();
		}
	}

	@Override
	public ISpecies getSpecies(final String speciesName) {
		if (speciesName == null) { return null; }
		if (speciesName.equals(getName())) { return this; }
		/*
		 * the original is: return getAllSpecies().get(speciesName);
		 */

		// hqnghi 11/Oct/13
		// get experiementSpecies in any model
		ISpecies sp = getAllSpecies().get(speciesName);
		if (sp == null) {
			sp = getExperiment(speciesName);
			if (sp == null) {
				for (final Map.Entry<String, ISpecies> entry : getAllSpecies().entrySet()) {
					final ISpecies mm = entry.getValue();
					if (mm instanceof GamlModelSpecies) {
						sp = ((GamlModelSpecies) mm).getExperiment(speciesName);
						if (sp != null) { return sp; }
					}
				}
			}
		}
		return sp;
	}

	@Override
	public ISpecies getSpecies(final String speciesName, final SpeciesDescription specDes) {
		if (speciesName == null) { return null; }
		if (speciesName.equals(getName())) { return this; }
		// hqnghi 11/Oct/13
		// get experiementSpecies in any model
		ISpecies sp = getExperiment(speciesName);
		if (sp == null) {
			for (final Map.Entry<String, ISpecies> entry : getAllSpecies().entrySet()) {
				final ISpecies mm = entry.getValue();
				if (mm instanceof GamlModelSpecies && specDes.getOriginName().equals(mm.getName())) {
					sp = ((GamlModelSpecies) mm).getExperiment(speciesName);
					if (sp != null) { return sp; }
				}
			}
		}
		sp = getAllSpecies().get(speciesName);
		return sp;
	}

	@Override
	public Map<String, ISpecies> getAllSpecies() {
		if (allSpecies == null) {
			allSpecies = GamaMapFactory.create();
			final Deque<ISpecies> speciesStack = new ArrayDeque<>();
			speciesStack.push(this);
			ISpecies currentSpecies;
			while (!speciesStack.isEmpty()) {
				currentSpecies = speciesStack.pop();
				// scope.getGui().debug("GamlModelSpecies: effectively adding "
				// + currentSpecies.getName());
				allSpecies.put(currentSpecies.getName(), currentSpecies);
				final List<ISpecies> theMicroSpecies = currentSpecies.getMicroSpecies();
				for (final ISpecies microSpec : theMicroSpecies) {
					if (microSpec.getMacroSpecies().equals(currentSpecies)) {
						speciesStack.push(microSpec);
					}
				}
			}
		}
		return allSpecies;
	}

	@Override
	public void setChildren(final Iterable<? extends ISymbol> children) {
		final List forExperiment = new ArrayList<>();

		final List<IExperimentPlan> theExperiments = new ArrayList<>();

		for (final Iterator<? extends ISymbol> it = children.iterator(); it.hasNext();) {
			final ISymbol s = it.next();

			if (s instanceof IExperimentPlan) {
				theExperiments.add((IExperimentPlan) s);
				it.remove();
			} else if (s instanceof AbstractOutputManager) {
				forExperiment.add(s);
				it.remove();
			}
		}
		// Add the variables, etc. to the model
		super.setChildren(children);
		// Add the experiments and the default outputs to all experiments
		for (final IExperimentPlan exp : theExperiments) {
			addExperiment(exp);
			exp.setChildren(forExperiment);
		}
	}

	static Predicate<IStatement> isTest = s -> (s instanceof TestStatement);

	@Override
	public List<TestStatement> getAllTests() {

		final List<TestStatement> tests = new ArrayList<>();
		final Consumer<IStatement> filter = t -> {
			if (t instanceof TestStatement) {
				tests.add((TestStatement) t);
			}
		};
		// Fix Issue #2659
		// getBehaviors().forEach(filter);
		getAllSpecies().values().forEach(s -> s.getBehaviors().forEach(filter));
		return tests;
	}

}
