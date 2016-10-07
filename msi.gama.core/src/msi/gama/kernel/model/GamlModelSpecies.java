/*********************************************************************************************
 *
 *
 * 'GamlModelSpecies.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.kernel.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.outputs.AbstractOutputManager;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.util.TOrderedHashMap;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.species.GamlSpecies;
import msi.gaml.species.ISpecies;
import msi.gaml.types.IType;

@symbol(name = { IKeyword.MODEL }, kind = ISymbolKind.MODEL, with_sequence = true, internal = true, concept = {})
@facets(value = { @facet(name = IKeyword.VERSION, type = IType.ID, optional = true),
		@facet(name = IKeyword.AUTHOR, type = IType.ID, optional = true),
		@facet(name = IKeyword.PRAGMA, type = IType.LIST, of = IType.STRING, optional = true, internal = true),
		@facet(name = IKeyword.TORUS, type = IType.BOOL, optional = true),
		@facet(name = IKeyword.NAME, type = IType.ID, optional = false),
		@facet(name = IKeyword.PARENT, type = IType.ID, optional = true),
		@facet(name = IKeyword.SKILLS, type = IType.LIST, optional = true),
		@facet(name = IKeyword.CONTROL, type = IType.SKILL, optional = true),
		@facet(name = IKeyword.FREQUENCY, type = IType.INT, optional = true),
		@facet(name = IKeyword.SCHEDULES, type = IType.CONTAINER, optional = true),
		@facet(name = IKeyword.TOPOLOGY, type = IType.TOPOLOGY, optional = true) }, omissible = IKeyword.NAME)
@doc("The root declaration of all models")
@SuppressWarnings({ "unchecked", "rawtypes" })
public class GamlModelSpecies extends GamlSpecies implements IModel {

	protected final Map<String, IExperimentPlan> experiments = new TOrderedHashMap();
	protected final Map<String, IExperimentPlan> titledExperiments = new TOrderedHashMap();
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
		if (exp == null) {
			return;
		}
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
		if (speciesName == null) {
			return null;
		}
		if (speciesName.equals(getName())) {
			return this;
		}
		/*
		 * the original is: return getAllSpecies().get(speciesName);
		 */

		// hqnghi 11/Oct/13
		// get experiementSpecies in any model
		ISpecies sp = getAllSpecies().get(speciesName);
		if (sp == null) {
			sp = getExperiment(speciesName);
			if (sp == null) {
				for (final ISpecies mm : getAllSpecies().values()) {
					if (mm instanceof GamlModelSpecies) {
						sp = ((GamlModelSpecies) mm).getExperiment(speciesName);
					}
				}
			}
		}
		return sp;
	}

	@Override
	public ISpecies getSpecies(final String speciesName, final SpeciesDescription specDes) {
		if (speciesName == null) {
			return null;
		}
		if (speciesName.equals(getName())) {
			return this;
		}
		// hqnghi 11/Oct/13
		// get experiementSpecies in any model
		ISpecies sp = getExperiment(speciesName);
		if (sp == null) {
			for (final ISpecies mm : getAllSpecies().values()) {
				if (mm instanceof GamlModelSpecies && specDes.getOriginName().equals(mm.getName())) {
					sp = ((GamlModelSpecies) mm).getExperiment(speciesName);
					if (sp != null) {
						return sp;
					}
				}
			}
		}
		sp = getAllSpecies().get(speciesName);
		return sp;
	}

	@Override
	public Map<String, ISpecies> getAllSpecies() {
		if (allSpecies == null) {
			allSpecies = new TOrderedHashMap();
			final Deque<ISpecies> speciesStack = new ArrayDeque<ISpecies>();
			speciesStack.push(this);
			ISpecies currentSpecies;
			while (!speciesStack.isEmpty()) {
				currentSpecies = speciesStack.pop();
				// scope.getGui().debug("GamlModelSpecies: effectively adding "
				// + currentSpecies.getName());
				allSpecies.put(currentSpecies.getName(), currentSpecies);
				final List<ISpecies> microSpecies = currentSpecies.getMicroSpecies();
				for (final ISpecies microSpec : microSpecies) {
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

		final List<IExperimentPlan> experiments = new ArrayList<>();
		for (final Iterator<? extends ISymbol> it = children.iterator(); it.hasNext();) {
			final ISymbol s = it.next();
			if (s instanceof IExperimentPlan) {
				experiments.add((IExperimentPlan) s);
				it.remove();
			} else if (s instanceof AbstractOutputManager) {
				forExperiment.add(s);
				it.remove();
			}
		}
		// Add the variables, etc. to the model
		super.setChildren(children);
		// Add the experiments and the default outputs to all experiments
		for (final IExperimentPlan exp : experiments) {
			addExperiment(exp);
			exp.setChildren(forExperiment);
		}
	}

}
