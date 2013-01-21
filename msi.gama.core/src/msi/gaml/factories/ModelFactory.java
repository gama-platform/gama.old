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
import msi.gama.precompiler.GamlAnnotations.factory;
import msi.gama.precompiler.*;
import msi.gaml.compilation.AbstractGamlAdditions;
import msi.gaml.descriptions.*;
import msi.gaml.statements.Facets;

/**
 * Written by drogoul Modified on 27 oct. 2009
 * 
 * @todo Description
 */
@factory(handles = { ISymbolKind.MODEL }, uses = { ISymbolKind.EXPERIMENT, ISymbolKind.SPECIES,
	ISymbolKind.ENVIRONMENT, ISymbolKind.OUTPUT })
public class ModelFactory extends SymbolFactory implements ISymbolFactory.Model {

	public ModelFactory(final List<Integer> handles, final List<Integer> uses) {
		super(handles, uses);
	}

	public final static List<String> SPECIES_NODES = Arrays.asList(IKeyword.SPECIES, IKeyword.GRID);

	private void addMicroSpecies(final SpeciesDescription macro, final SpeciesStructure micro) {
		SpeciesDescription mDesc =
			(SpeciesDescription) createDescription(micro.getNode(), macro, IChildrenProvider.NONE);
		macro.addChild(mDesc);
		for ( SpeciesStructure microSpecStructure : micro.getMicroSpecies() ) {
			addMicroSpecies(mDesc, microSpecStructure);
		}
	}

	/**
	 * Recursively complements a species with its micro-species.
	 * Add variables, behaviors (actions, reflex, task, states, ...), aspects to species.
	 * 
	 * @param macroSpecies the macro-species
	 * @param microSpeciesStructure the structure of micro-species
	 */
	private void complementSpecies(final SpeciesDescription macro, final SpeciesStructure micro) {
		ISyntacticElement msNode = micro.getNode();
		SpeciesDescription mDesc = macro.getMicroSpecies(msNode.getLabel(IKeyword.NAME));
		ISymbolFactory f = chooseFactoryFor(getKeyword(msNode), mDesc.getKeyword());
		for ( ISyntacticElement child : msNode.getChildren() ) {
			// if micro-species were already added, no need to re-add them
			if ( !ModelFactory.SPECIES_NODES.contains(child.getKeyword()) ) {
				mDesc.addChild(f.createDescriptionRecursively(child, mDesc));
			}
		}
		// recursively complement micro-species
		for ( SpeciesStructure microSpec : micro.getMicroSpecies() ) {
			complementSpecies(mDesc, microSpec);
		}
	}

	private void complementExperimentSpecies(final SpeciesDescription sd) {
		ISyntacticElement e = sd.getSourceInformation();
		ISymbolFactory f = chooseFactoryFor(getKeyword(e), sd.getKeyword());
		for ( ISyntacticElement child : e.getChildren() ) {
			f.createDescriptionRecursively(child, sd); // ???
		}
		sd.finalizeDescription();
	}

	public ModelDescription parse(final ModelStructure structure) {
		ModelDescription model =
			new ModelDescription(structure.getProjectPath(), structure.getPath(),
				structure.getSource());
		model.getSourceInformation().setDescription(model);
		model.getFacets().putAsLabel(IKeyword.NAME, structure.getName());

		// Collecting built-in species
		SpeciesDescription world = computeBuiltInSpecies(model);

		// recursively add user-defined species to world and down on to the hierarchy
		for ( SpeciesStructure speciesStructure : structure.getSpecies() ) {
			addMicroSpecies(world, speciesStructure);
		}

		// Complementing the world
		ISymbolFactory f = chooseFactoryFor(IKeyword.GLOBAL, null);
		for ( final ISyntacticElement e : structure.getGlobalNodes() ) {
			for ( ISyntacticElement child : e.getChildren() ) {
				world.addChild(f.createDescriptionRecursively(child, world));
			}
		}

		// Complementing species
		for ( SpeciesStructure specStructure : structure.getSpecies() ) {
			complementSpecies(world, specStructure);
		}

		// Inheritance (of attributes, actions, control, ... ) between parent & sub-species
		world.finalizeDescription();

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

	@Override
	public SpeciesFactory getSpeciesFactory() {
		return (SpeciesFactory) chooseFactoryFor(IKeyword.SPECIES);
	}

	public SpeciesDescription computeBuiltInSpecies(final ModelDescription model) {
		// We create a new world
		ISyntacticElement ww = model.getSourceInformation().getChild(GLOBAL);
		IDescription world =
			getSpeciesFactory().createSpeciesDescription(WORLD_SPECIES,
				AbstractGamlAdditions.WORLD_AGENT_CLASS, model,
				AbstractGamlAdditions.WORLD_AGENT_CONSTRUCTOR,
				AbstractGamlAdditions.getSpeciesSkills(WORLD_SPECIES),
				ww == null ? new Facets() : ww.getFacets());
		model.addChild(world);
		// We then reattach the previous built-in species to the new world
		for ( SpeciesDescription sd : AbstractGamlAdditions.BUILT_IN_SPECIES.values() ) {
			sd.setSuperDescription(world);
			world.addChild(sd);
		}
		return (SpeciesDescription) world;
	}

	private IDescription createDefaultExperiment() {
		IDescription def;
		if ( GuiUtils.isInHeadLessMode() ) {
			def = DescriptionFactory.create(HEADLESS_UI, NAME, DEFAULT_EXP, TYPE, HEADLESS_UI);
		} else {
			def = DescriptionFactory.create(GUI_, NAME, DEFAULT_EXP, TYPE, GUI_);
		}
		complementExperimentSpecies((ExperimentDescription) def);
		return def;

	}

	@Override
	public IModel compile(final ModelStructure structure) {
		ModelDescription md = parse(structure);
		IModel model = (IModel) compileDescription(md);
		return model;
	}

	@Override
	synchronized public ModelDescription validate(final ModelStructure structure) {
		ModelDescription md = parse(structure);
		validateDescription(md);
		return md;
	}

}
