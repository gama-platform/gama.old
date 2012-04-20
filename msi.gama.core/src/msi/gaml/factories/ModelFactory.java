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

import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.*;
import msi.gama.kernel.model.IModel;
import msi.gama.precompiler.GamlAnnotations.handles;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.uses;
import msi.gama.precompiler.*;
import msi.gaml.compilation.GamlCompiler;
import msi.gaml.descriptions.*;
import msi.gaml.skills.Skill;

/**
 * Written by drogoul Modified on 27 oct. 2009
 * 
 * @todo Description
 */
@handles({ ISymbolKind.MODEL })
@uses({ ISymbolKind.EXPERIMENT, ISymbolKind.SPECIES, ISymbolKind.ENVIRONMENT, ISymbolKind.OUTPUT })
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ModelFactory extends SymbolFactory {

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

	/**
	 * Recursively adds micro-species (built from SpeciesStructure) to a species.
	 * 
	 * @param macroSpecies
	 * @param microSpecies
	 * @throws GamlException
	 */
	private void addMicroSpecies(final SpeciesDescription macroSpecies,
		final SpeciesStructure microSpecies) {

		SpeciesDescription microSpeciesDesc =
			(SpeciesDescription) createDescription(microSpecies.getNode(), macroSpecies, null);
		macroSpecies.addChild(microSpeciesDesc);

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

		Set<String> userRedefinedOrNewVars = new HashSet<String>();
		List<IDescription> subDescs = new ArrayList<IDescription>();
		for ( ISyntacticElement child : children ) {
			// if micro-species were already added, no need to re-add them
			if ( !ModelFactory.SPECIES_NODES.contains(child.getKeyword()) ) {
				IDescription desc;
				desc = f.createDescriptionRecursively(child, speciesDesc);
				subDescs.add(desc);

				if ( desc instanceof VariableDescription ) {
					userRedefinedOrNewVars.add(desc.getName());
				}

			}
		}
		speciesDesc.addChildren(subDescs);
		speciesDesc.setUserRedefinedAndNewVars(userRedefinedOrNewVars);

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

		Set<String> userRedefinedOrNewVars = new HashSet<String>();
		// List<IDescription> subDescs = new ArrayList<IDescription>();
		for ( ISyntacticElement child : e.getChildren() ) {

			IDescription desc;
			desc = f.createDescriptionRecursively(child, sd);
			// subDescs.add(desc);

			if ( desc instanceof VariableDescription ) {
				userRedefinedOrNewVars.add(desc.getName());
			}

		}
		// sd.addChildren(subDescs);
		sd.setUserRedefinedAndNewVars(userRedefinedOrNewVars);
		sd.finalizeDescription();

	}

	public synchronized ModelDescription parse(final ModelStructure structure,
		final IErrorCollector collect) {
		ModelDescription model = new ModelDescription(structure.getPath(), structure.getSource());
		model.getFacets().putAsLabel(IKeyword.NAME, structure.getName());

		// Collecting built-in species & species
		Set<SpeciesDescription> builtIn = computeBuiltInSpecies(model);
		SpeciesDescription worldSpeciesDesc = null;

		// Add "world_species" to ModelDescription
		for ( final SpeciesDescription spd : builtIn ) {
			if ( IKeyword.WORLD_SPECIES_NAME.equals(spd.getName()) ) {
				worldSpeciesDesc = spd;
				model.addChild(spd);
				break;
			}
		}

		if ( worldSpeciesDesc == null ) {
			model.flagError("Unable to load the built-in 'world' species. Halting compilation");
			return model;
		}

		// Add built-in species to "world_species"
		for ( final SpeciesDescription spd : builtIn ) {
			if ( !IKeyword.WORLD_SPECIES_NAME.equals(spd.getName()) ) {
				worldSpeciesDesc.addChild(spd);
			}
		}

		// recursively add user-defined species to world species and down on to the species
		// hierarchy
		for ( SpeciesStructure speciesStructure : structure.getSpecies() ) {
			addMicroSpecies(worldSpeciesDesc, speciesStructure);
		}

		// Complementing the world
		ISymbolFactory f = chooseFactoryFor(IKeyword.GLOBAL, null);
		List<IDescription> subDescs = new ArrayList();
		Set<String> userRedefinedOrNewVars = new HashSet<String>();
		for ( final ISyntacticElement e : structure.getGlobalNodes() ) {
			for ( ISyntacticElement child : e.getChildren() ) {
				IDescription desc;
				desc = f.createDescriptionRecursively(child, worldSpeciesDesc);
				subDescs.add(desc);

				if ( desc instanceof VariableDescription ) {
					userRedefinedOrNewVars.add(desc.getName());
				}

			}
		}
		worldSpeciesDesc.addChildren(subDescs);
		worldSpeciesDesc.setUserRedefinedAndNewVars(userRedefinedOrNewVars);

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

		return model;
	}

	public static Set<SpeciesDescription> computeBuiltInSpecies(final ModelDescription model) {
		Set<SpeciesDescription> builtInSpecies = new HashSet();

		// Firstly, create "world_species" (defined in WorldSkill) SpeciesDescription with
		// ModelDescription as SuperDescription
		String facets[] = new String[0];
		Class worldSkill = GamlCompiler.getBuiltInSpeciesClasses().get(IKeyword.WORLD_SPECIES_NAME);
		skill s = (skill) worldSkill.getAnnotation(skill.class);
		if ( s != null ) {
			String[] names = s.value();
			String skillName = names[0];
			facets =
				new String[] { /* ISymbol.SPECIES, */
					IKeyword.NAME,
					IKeyword.WORLD_SPECIES_NAME,
					IKeyword.BASE,
					GamlCompiler.getBuiltInSpeciesClasses().get(IKeyword.DEFAULT)
						.getCanonicalName(), IKeyword.SKILLS, "[" + skillName + "]" }; // TODO
			// Parser.createLiteral
			// ?
		}
		SpeciesDescription worldSpeciesDescription;
		worldSpeciesDescription =
			(SpeciesDescription) DescriptionFactory.createDescription(IKeyword.SPECIES, model,
				facets);
		if ( worldSpeciesDescription == null ) { return Collections.EMPTY_SET; // FIXME Exception ?
		}
		builtInSpecies.add(worldSpeciesDescription);

		// Secondly, create other built-in SpeciesDescriptions with worldSpeciesDescription as
		// SuperDescription
		for ( String speciesName : GamlCompiler.getBuiltInSpeciesClasses().keySet() ) {
			if ( !IKeyword.WORLD_SPECIES_NAME.equals(speciesName) ) {
				Class c = GamlCompiler.getBuiltInSpeciesClasses().get(speciesName);
				facets =
					new String[] { /* ISymbol.SPECIES, */IKeyword.NAME, speciesName,
						IKeyword.BASE, c.getCanonicalName() };
				if ( Skill.class.isAssignableFrom(c) ) {
					s = (skill) c.getAnnotation(skill.class);
					if ( s != null ) {
						String[] names = s.value();
						String skillName = names[0];
						facets =
							new String[] { /* ISymbol.SPECIES, */
								IKeyword.NAME,
								speciesName,
								IKeyword.BASE,
								GamlCompiler.getBuiltInSpeciesClasses().get(IKeyword.DEFAULT)
									.getCanonicalName(), IKeyword.SKILLS, skillName };
					}
				}
				SpeciesDescription sd;
				sd =
					(SpeciesDescription) DescriptionFactory.createDescription(IKeyword.SPECIES,
						worldSpeciesDescription, facets);
				if ( sd != null ) {
					builtInSpecies.add(sd);
				}

				// OutputManager.debug("Built-in species " + speciesName +
				// " created with Java support in " + c.getSimpleName());
			}
		}
		return builtInSpecies;
	}

	private IDescription createDefaultExperiment() {

		IDescription def;
		if ( GuiUtils.isInHeadLessMode() ) {
			System.out.println("head Less");
			def =
				DescriptionFactory.createDescription(IKeyword.EXPERIMENT, IKeyword.NAME,
					IKeyword.DEFAULT_EXPERIMENT, IKeyword.TYPE, IKeyword.HEADLESS_UI);
		} else {
			def =
				DescriptionFactory.createDescription(IKeyword.EXPERIMENT, IKeyword.NAME,
					IKeyword.DEFAULT_EXPERIMENT, IKeyword.TYPE, IKeyword.GUI_);
		}
		complementExperimentSpecies((ExperimentDescription) def);
		return def;

	}

	public synchronized IModel compile(final ModelStructure structure, final IErrorCollector collect) {
		IModel m = null;
		ModelDescription md = parse(structure, collect);
		if ( !md.hasExperiment(IKeyword.DEFAULT_EXPERIMENT) ) {
			md.addChild(createDefaultExperiment());
		}
		if ( collect.hasErrors() ) { return null; }
		m = (IModel) compileDescription(md);
		return m;
	}

	/**
	 * @param ms
	 * @param collect
	 */
	public synchronized void validate(final ModelStructure structure, final IErrorCollector collect) {
		long time = System.currentTimeMillis();
		ModelDescription md = parse(structure, collect);
		long now = System.currentTimeMillis();
		GuiUtils.debug(structure.getPath() + ": parsing to descriptions took: " + (now - time) +
			"ms");
		time = now;

		if ( !md.hasExperiment(IKeyword.DEFAULT_EXPERIMENT) ) {
			md.addChild(createDefaultExperiment());
		}
		validateDescription(md);
		GuiUtils.debug(structure.getPath() + ": validation took: " +
			(System.currentTimeMillis() - time) + "ms");
		md.dispose();
	}

}
