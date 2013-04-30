/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.kernel.model;

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.IExperimentSpecies;
import msi.gama.outputs.OutputManager;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.util.GamaList;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.*;
import msi.gaml.species.*;
import msi.gaml.types.IType;

@symbol(name = { IKeyword.MODEL }, kind = ISymbolKind.MODEL, with_sequence = true)
@inside(kinds = ISymbolKind.SPECIES)
@facets(value = { @facet(name = IKeyword.NAME, type = IType.ID, optional = true),
	@facet(name = IKeyword.VERSION, type = IType.ID, optional = true),
	@facet(name = IKeyword.AUTHOR, type = IType.ID, optional = true) }, omissible = IKeyword.NAME)
public class GamlModel extends GamlSpecies implements IModel {

	protected final Map<String, IExperimentSpecies> experiments = new HashMap<String, IExperimentSpecies>();

	// protected ISpecies worldSpecies;

	public GamlModel(final IDescription description) {
		super(description);
		setName(description.getName());
	}

	@Override
	public ModelDescription getDescription() {
		return (ModelDescription) description;
	}

	@Override
	public String getRelativeFilePath(final String filePath, final boolean shouldExist) {
		return getDescription().constructModelRelativePath(filePath, shouldExist);
	}

	@Override
	public boolean isTorus() {
		return ((ModelDescription) description).isTorus();
	}

	@Override
	public String getFolderPath() {
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

	protected void addExperiment(final IExperimentSpecies exp) {
		if ( exp == null ) { return; }
		experiments.put(exp.getName(), exp);
		exp.setModel(this);
	}

	@Override
	public IExperimentSpecies getExperiment(final String s) {
		// if ( s == null ) { return getExperiment(IKeyword.DEFAULT_EXP); }
		return experiments.get(s);
	}

	@Override
	public Collection<IExperimentSpecies> getExperiments() {
		return experiments.values();
	}

	@Override
	public void dispose() {
		super.dispose();
		// worldSpecies.dispose();
		for ( IExperimentSpecies exp : experiments.values() ) {
			exp.dispose();
		}
		experiments.clear();
	}

	// @Override
	// public ISpecies getWorldSpecies() {
	// return worldSpecies;
	// }

	@Override
	public ISpecies getSpecies(final String speciesName) {
		if ( speciesName == null ) { return null; }

		Deque<ISpecies> speciesStack = new ArrayDeque<ISpecies>();
		speciesStack.push(this);
		ISpecies currentSpecies;
		while (!speciesStack.isEmpty()) {
			currentSpecies = speciesStack.pop();
			if ( currentSpecies.getName().equals(speciesName) ) { return currentSpecies; }

			List<ISpecies> microSpecies = currentSpecies.getMicroSpecies();
			for ( ISpecies microSpec : microSpecies ) {
				if ( microSpec.getMacroSpecies().equals(currentSpecies) ) {
					speciesStack.push(microSpec);
				}
			}
		}

		return null;
	}

	@Override
	public void setChildren(final List<? extends ISymbol> children) {
		GamaList forExperiment = new GamaList();

		List<IExperimentSpecies> experiments = new ArrayList();
		for ( Iterator<? extends ISymbol> it = children.iterator(); it.hasNext(); ) {
			ISymbol s = it.next();
			if ( s instanceof IExperimentSpecies ) {
				experiments.add((IExperimentSpecies) s);
				it.remove();
			} else if ( s instanceof OutputManager ) {
				forExperiment.add(s);
				it.remove();
			}
		}
		// Add the variables, etc. to the model
		super.setChildren(children);
		// Add the experiments and the default outputs to all experiments
		for ( IExperimentSpecies exp : experiments ) {
			addExperiment(exp);
			exp.setChildren(forExperiment);
		}
	}
}
