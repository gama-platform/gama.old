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
import static msi.gaml.compilation.AbstractGamlAdditions.*;
import java.util.List;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.kernel.model.IModel;
import msi.gama.precompiler.GamlAnnotations.factory;
import msi.gama.precompiler.*;
import msi.gaml.descriptions.*;
import msi.gaml.statements.Facets;

/**
 * Written by drogoul Modified on 27 oct. 2009
 * 
 * @todo Description
 */
@factory(handles = { ISymbolKind.MODEL })
public class ModelFactory extends SymbolFactory {

	public ModelFactory(final List<Integer> handles) {
		super(handles);
	}

	private void addMicroSpecies(final ModelDescription model, final IDescription macro,
		final SpeciesStructure micro) {
		// Create the species description without any children
		SpeciesDescription mDesc =
			(SpeciesDescription) create(micro.getNode(), macro, IChildrenProvider.NONE);
		// Add it to its macro-species
		macro.addChild(mDesc);
		// Add it to the model
		model.addSpeciesDescription(mDesc);
		// Recursively create each micro-species of the newly added micro-species
		for ( SpeciesStructure microSpecStructure : micro.getMicroSpecies() ) {
			addMicroSpecies(model, mDesc, microSpecStructure);
		}
	}

	/**
	 * Recursively complements a species and its micro-species.
	 * Add variables, behaviors (actions, reflex, task, states, ...), aspects to species.
	 * 
	 * @param macro the macro-species
	 * @param micro the structure of micro-species
	 */
	private void complementSpecies(final SpeciesDescription macro, final SpeciesStructure micro) {
		ISyntacticElement msNode = micro.getNode();
		// Gather the previously created species
		SpeciesDescription mDesc = macro.getMicroSpecies(msNode.getLabel(IKeyword.NAME));
		if ( mDesc == null ) { return; }
		// GuiUtils.debug("Complementing " + mDesc.getName());
		for ( ISyntacticElement child : msNode.getChildren() ) {
			String kw = child.getKeyword();
			if ( !ModelStructure.SPECIES_NODES.contains(kw) ) {
				IDescription childDesc = create(child, mDesc);
				if ( childDesc != null ) {
					mDesc.addChild(childDesc);
				}
			}
		}
		// recursively complement micro-species
		for ( SpeciesStructure microSpec : micro.getMicroSpecies() ) {
			complementSpecies(mDesc, microSpec);
		}
	}

	public ModelDescription parse(final ModelStructure structure) {
		ModelDescription model = new ModelDescription(structure);
		model.getSourceInformation().setDescription(model);
		model.getFacets().putAsLabel(IKeyword.NAME, structure.getName());
		// Collect and build built-in species
		SpeciesDescription world = computeBuiltInSpecies(model);
		// recursively add user-defined species to world and down on to the hierarchy
		for ( SpeciesStructure speciesStructure : structure.getSpecies() ) {
			addMicroSpecies(model, world, speciesStructure);
		}
		// Add all the new species descriptions as types
		model.buildTypes();

		// Complement the world with its elements
		for ( final ISyntacticElement e : structure.getGlobalNodes() ) {
			for ( ISyntacticElement child : e.getChildren() ) {
				world.addChild(create(child, world));
			}
		}
		// Complement recursively the different species
		for ( SpeciesStructure specStructure : structure.getSpecies() ) {
			complementSpecies(world, specStructure);
		}
		// Make species recursively inherit (of attributes, actions, control, ... ) from their
		// parent, create their control, skills
		world.finalizeDescription();

		// Parse the other definitions (output, environment, ...)
		for ( final ISyntacticElement e : structure.getModelNodes() ) {
			IDescription dd = create(e, model);
			if ( dd != null ) {
				model.addChild(dd);
			}
		}
		// Gather the species created to see if some describe experiments, in which case they are
		// added to the experiments of the model
		for ( IDescription desc : world.getAllMicroSpecies() ) {
			if ( desc instanceof ExperimentDescription ) {
				// TODO addExperiment() without breaking the link
				model.addChild(desc);
			}
		}

		// Adding the default experiment if it is not already defined
		if ( !model.hasExperiment(DEFAULT_EXP) ) {
			model.addChild(createDefaultExperiment());
		}
		return model;
	}

	public SpeciesDescription computeBuiltInSpecies(final ModelDescription model) {
		// We create a new world
		ISyntacticElement ww = model.getSourceInformation().getChild(GLOBAL);
		SpeciesDescription world =
			DescriptionFactory.createSpeciesDescription(WORLD_SPECIES, WORLD_AGENT_CLASS, model,
				WORLD_AGENT_CONSTRUCTOR, getSpeciesSkills(WORLD_SPECIES), ww == null ? new Facets()
					: ww.getFacets());
		model.addChild(world);
		// We then reattach the previous built-in species to the new world
		for ( SpeciesDescription sd : BUILT_IN_SPECIES.values() ) {
			sd.setSuperDescription(world);
			world.addChild(sd);
		}
		return world;
	}

	private IDescription createDefaultExperiment() {
		String type = GuiUtils.isInHeadLessMode() ? HEADLESS_UI : GUI_;
		return DescriptionFactory.create(type, NAME, DEFAULT_EXP, TYPE, type);
	}

	public IModel compile(final ModelStructure structure) {
		return (IModel) compile(parse(structure));
	}

	synchronized public ModelDescription validate(final ModelStructure structure) {
		ModelDescription md = parse(structure);
		md.getTypesManager().printTypeHierarchy();
		validate(md);
		return md;
	}

}
