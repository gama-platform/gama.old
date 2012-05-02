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
package msi.gaml.factories;

import static msi.gama.common.interfaces.IKeyword.*;
import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.kernel.model.IModel;
import msi.gama.precompiler.GamlAnnotations.handles;
import msi.gama.precompiler.GamlAnnotations.uses;
import msi.gama.precompiler.*;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.*;

/**
 * Written by drogoul Modified on 27 oct. 2009
 * 
 * @todo Description
 */
@handles({ ISymbolKind.MODEL })
@uses({ ISymbolKind.EXPERIMENT, ISymbolKind.SPECIES, ISymbolKind.ENVIRONMENT, ISymbolKind.OUTPUT })
public class ModelFactory extends SymbolFactory {

	public static Set<SpeciesDescription> BUILT_IN_SPECIES;

	/**
	 * @param superFactory
	 */
	public ModelFactory(final ISymbolFactory superFactory) {
		super(superFactory);
	}

	public ModelFactory() {
		super(null);
	}

	public static boolean isBuiltIn(final String name) {
		return GamlCompiler.getBuiltInSpeciesClasses().containsKey(name);
	}

	public final static List<String> SPECIES_NODES = Arrays.asList(IKeyword.SPECIES, IKeyword.GRID);

	private void addMicroSpecies(final SpeciesDescription macroSpecies,
		final SpeciesStructure microSpecies) {

		SpeciesDescription microSpeciesDesc =
			(SpeciesDescription) createDescription(microSpecies.getNode(), macroSpecies, null);

		String compile = microSpeciesDesc.getFacets().getLabel("compile");
		if ( compile != null && compile.equals("true") )

		{
			Class c = null; // do something to generate the class
			String className = null; // get the name of the class (i.e. c.getCanonicalName()).
			GamaClassLoader.getInstance().addNewClass(c);
			Set<Class> set = new HashSet();
			set.add(c);
			GamaBundleLoader.scanBuiltIn(set);
			String[] facets = new String[] { NAME, "dummy", BASE, className };
			SpeciesDescription sd =
				(SpeciesDescription) DescriptionFactory.createDescription(SPECIES, macroSpecies,
					facets);
			macroSpecies.addChild(sd);
		} else {
			macroSpecies.addChild(microSpeciesDesc);
		}

		for ( SpeciesStructure microSpecStructure : microSpecies.getMicroSpecies() ) {
			addMicroSpecies(microSpeciesDesc, microSpecStructure);
		}
	}

	/**
	 * Recursively complements a species with its micro-species.
	 * Add variables, behaviors (actions, reflex, task, states, ...), aspects to species.
	 * 
	 * @param macroSpecies the macro-species
	 * @param microSpeciesStructure the structure of micro-species
	 */
	private void complementSpecies(final SpeciesDescription macroSpecies,
		final SpeciesStructure microSpeciesStructure) {
		ISyntacticElement microSpeciesNode = microSpeciesStructure.getNode();
		SpeciesDescription speciesDesc =
			macroSpecies.getMicroSpecies(microSpeciesNode.getLabel(IKeyword.NAME));
		String keyword = getKeyword(microSpeciesNode);
		String context = speciesDesc.getKeyword();
		ISymbolFactory f = chooseFactoryFor(keyword, context);
		List<ISyntacticElement> children = microSpeciesNode.getChildren();

		for ( ISyntacticElement child : children ) {
			// if micro-species were already added, no need to re-add them
			if ( !ModelFactory.SPECIES_NODES.contains(child.getKeyword()) ) {
				speciesDesc.addChild(f.createDescriptionRecursively(child, speciesDesc));
			}
		}

		// recursively complement micro-species
		for ( SpeciesStructure microSpec : microSpeciesStructure.getMicroSpecies() ) {
			complementSpecies(speciesDesc, microSpec);
		}
	}

	private void complementExperimentSpecies(final SpeciesDescription sd) {
		ISyntacticElement e = sd.getSourceInformation();
		String keyword = getKeyword(e);
		String context = sd.getKeyword();
		ISymbolFactory f = chooseFactoryFor(keyword, context);
		for ( ISyntacticElement child : e.getChildren() ) {
			f.createDescriptionRecursively(child, sd); // ???
		}
		sd.finalizeDescription();
	}

	public ModelDescription parse(final ModelStructure structure) {
		ModelDescription model = new ModelDescription(structure.getPath(), structure.getSource());
		model.getSourceInformation().setDescription(model);
		model.getFacets().putAsLabel(IKeyword.NAME, structure.getName());

		// Collecting built-in species & species
		SpeciesDescription worldSpeciesDesc =
			(SpeciesDescription) model.addChild(computeWorldDescription(model));
		computeBuiltInSpecies(worldSpeciesDesc);

		// recursively add user-defined species to world species and down on to the species
		// hierarchy
		for ( SpeciesStructure speciesStructure : structure.getSpecies() ) {
			addMicroSpecies(worldSpeciesDesc, speciesStructure);
		}

		// Complementing the world
		ISymbolFactory f = chooseFactoryFor(IKeyword.GLOBAL, null);
		for ( final ISyntacticElement e : structure.getGlobalNodes() ) {
			for ( ISyntacticElement child : e.getChildren() ) {
				worldSpeciesDesc.addChild(f.createDescriptionRecursively(child, worldSpeciesDesc));
			}
		}

		// Complementing species
		for ( SpeciesStructure specStructure : structure.getSpecies() ) {
			complementSpecies(worldSpeciesDesc, specStructure);
		}

		// Inheritance (of attributes, actions, primitives, control, ... ) between parent-species &
		// sub-species
		worldSpeciesDesc.finalizeDescription();

		// Parse the other definitions (output, environment, batch...)
		for ( final ISyntacticElement e : structure.getModelNodes() ) {
			IDescription dd = createDescriptionRecursively(e, model);
			if ( dd instanceof ExperimentDescription ) {
				complementExperimentSpecies((ExperimentDescription) dd);
			}
			if ( dd != null ) {
				model.addChild(dd);
			}
		}

		// Adding the default experiment if it is not already defined
		if ( !model.hasExperiment(DEFAULT_EXP) ) {
			model.addChild(createDefaultExperiment());
		}

		return model;
	}

	public SpeciesDescription computeWorldDescription(final ModelDescription model) {
		String cName = GamlCompiler.getBuiltInSpeciesClasses().get(DEFAULT).getCanonicalName();
		IDescription wd =
			DescriptionFactory.createDescription(SPECIES, model, new String[] { NAME,
				WORLD_SPECIES_NAME, BASE, cName });
		if ( wd == null ) {
			model.flagError("Impossible to create world species. Check your GAML setup. ");
			return null;
		}
		return (SpeciesDescription) wd;

	}

	public static void computeBuiltInSpecies(final SpeciesDescription world) {
		if ( BUILT_IN_SPECIES == null ) {
			BUILT_IN_SPECIES = new HashSet();
			for ( Map.Entry<String, Class> e : GamlCompiler.getBuiltInSpeciesClasses().entrySet() ) {
				String name = e.getKey();
				if ( !WORLD_SPECIES_NAME.equals(name) ) {
					BUILT_IN_SPECIES.add((SpeciesDescription) DescriptionFactory.createDescription(
						SPECIES, world, new String[] { NAME, name, BASE,
							e.getValue().getCanonicalName() }));
				}
			}
		}
		for ( SpeciesDescription sd : BUILT_IN_SPECIES ) {
			sd.setSuperDescription(world);
			world.addChild(sd);
		}
	}

	private IDescription createDefaultExperiment() {
		IDescription def;
		if ( GuiUtils.isInHeadLessMode() ) {
			def =
				DescriptionFactory.createDescription(EXPERIMENT, NAME, DEFAULT_EXP, TYPE,
					HEADLESS_UI);
		} else {
			def = DescriptionFactory.createDescription(EXPERIMENT, NAME, DEFAULT_EXP, TYPE, GUI_);
		}
		complementExperimentSpecies((ExperimentDescription) def);
		return def;

	}

	public IModel compile(final ModelStructure structure) {
		ModelDescription md = parse(structure);
		IModel model = (IModel) compileDescription(md);
		return model;
	}

	synchronized public ModelDescription validate(final ModelStructure structure) {
		ModelDescription md = parse(structure);
		validateDescription(md);
		return md;
	}

}
