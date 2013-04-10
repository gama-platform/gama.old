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
import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.kernel.model.IModel;
import msi.gama.precompiler.GamlAnnotations.factory;
import msi.gama.precompiler.*;
import msi.gama.runtime.GAMA;
import msi.gaml.compilation.SyntacticElement;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.*;
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
		final ISyntacticElement micro) {
		// Create the species description without any children
		TypeDescription mDesc = (TypeDescription) create(micro, macro, IChildrenProvider.NONE);
		// Add it to its macro-species
		macro.addChild(mDesc);
		// Add it to the model
		// model.addSpeciesDescription(mDesc);
		// Recursively create each micro-species of the newly added micro-species
		for ( ISyntacticElement speciesNode : micro.getSpeciesChildren() ) {
			addMicroSpecies(model, mDesc, speciesNode);
		}
	}

	/**
	 * Recursively complements a species and its micro-species.
	 * Add variables, behaviors (actions, reflex, task, states, ...), aspects to species.
	 * 
	 * @param macro the macro-species
	 * @param micro the structure of micro-species
	 */
	private void complementSpecies(final SpeciesDescription macro, final ISyntacticElement micro) {
		// Gather the previously created species
		SpeciesDescription mDesc = macro.getMicroSpecies(micro.getName());
		if ( mDesc == null ) { return; }
		// GuiUtils.debug("Complementing " + mDesc.getName());
		for ( ISyntacticElement child : micro.getChildren() ) {
			if ( !child.isExperiment() && !child.isSpecies() ) {
				IDescription childDesc = create(child, mDesc);
				if ( childDesc != null ) {
					mDesc.addChild(childDesc);
				}
			}
		}
		// recursively complement micro-species
		for ( ISyntacticElement speciesNode : micro.getSpeciesChildren() ) {
			complementSpecies(mDesc, speciesNode);
		}
	}

	public ModelDescription assemble(final String projectPath, final String modelPath,
		final List<ISyntacticElement> models) {
		final List<ISyntacticElement> speciesNodes = new ArrayList();
		final List<ISyntacticElement> globalNodes = new ArrayList();
		final List<ISyntacticElement> otherNodes = new ArrayList();
		ISyntacticElement source = models.get(models.size() - 1);
		for ( int n = models.size(), i = n - 1; i >= 0; i-- ) {
			ISyntacticElement e = models.get(i);
			for ( ISyntacticElement se : e.getChildren() ) {
				if ( se.isGlobal() ) {
					globalNodes.addAll(se.getChildren());
				} else if ( se.isSpecies() || se.isExperiment() ) {
					speciesNodes.add(se);
				} else {
					otherNodes.add(se);
				}
			}
		}
		ModelDescription model =
			new ModelDescription(projectPath, modelPath, source.getElement(), source.getFacets());
		DescriptionFactory.setGamlDescription(source.getElement(), model);
		model.getFacets().putAsLabel(IKeyword.NAME, source.getLabel(NAME));

		// Collect and build built-in species
		SpeciesDescription world = computeBuiltInSpecies(model, source);

		// recursively add user-defined species to world and down on to the hierarchy
		for ( ISyntacticElement speciesNode : speciesNodes ) {
			addMicroSpecies(model, world, speciesNode);
		}
		// Add all the new species descriptions as types
		model.buildTypes();

		// Complement the world with its elements
		for ( final ISyntacticElement child : globalNodes ) {
			world.addChild(create(child, world));
		}
		// Complement recursively the different species
		for ( ISyntacticElement speciesNode : speciesNodes ) {
			complementSpecies(world, speciesNode);
		}
		// Make species recursively inherit (of attributes, actions, control, ... ) from their
		// parent, create their control, skills
		world.finalizeDescription();

		// Parse the other definitions (output, environment, ...)
		boolean environmentDefined = false;
		for ( final ISyntacticElement e : otherNodes ) {
			// COMPATIBILITY to remove the environment and put its definition in the world
			if ( ENVIRONMENT.equals(e.getKeyword()) ) {
				environmentDefined = translateEnvironment(world, e);
			} else {
				//
				IDescription dd = create(e, model);
				if ( dd != null ) {
					model.addChild(dd);
				}
			}
		}
		if ( !environmentDefined ) {
			Facets f = new Facets(NAME, SHAPE);
			f.put(
				INIT,
				GAMA.getExpressionFactory().createOperator("envelope", world,
					new ConstantExpression(100)));
			ISyntacticElement shape = new SyntacticElement(IKeyword.GEOMETRY, f);
			VariableDescription vd = (VariableDescription) create(shape, world);
			world.addChild(vd);
			world.resortVarName(vd);
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

	private boolean translateEnvironment(SpeciesDescription world, final ISyntacticElement e) {
		boolean environmentDefined;
		environmentDefined = true;
		ISyntacticElement shape = new SyntacticElement(IKeyword.GEOMETRY, new Facets(NAME, SHAPE));
		IExpressionDescription bounds = e.getFacet(BOUNDS);
		if ( bounds == null ) {
			IExpressionDescription width = e.getFacet(WIDTH);
			IExpressionDescription height = e.getFacet(HEIGHT);
			if ( width != null && height != null ) {
				bounds =
					new OperatorExpressionDescription("envelope",
						new OperatorExpressionDescription(IExpressionCompiler.INTERNAL_POINT,
							width, height));
			}
		}
		if ( bounds == null ) {
			bounds =
				new OperatorExpressionDescription("envelope",
					new ConstantExpressionDescription(100));
		} // else {
			// bounds = new OperatorExpressionDescription("envelope", bounds);
			// }
		shape.setFacet(INIT, bounds);
		IExpressionDescription depends = e.getFacet(DEPENDS_ON);
		if ( depends != null ) {
			shape.setFacet(DEPENDS_ON, depends);
		}
		VariableDescription vd = (VariableDescription) create(shape, world);
		world.addChild(vd);
		world.resortVarName(vd);
		IExpressionDescription ed = e.getFacet(TORUS);
		// TODO Is the call to compilation correct at that point ?
		if ( ed != null ) {
			world.getFacets().put(TORUS, ed.compile(world));
		}
		return environmentDefined;
	}

	public SpeciesDescription computeBuiltInSpecies(final ModelDescription model,
		ISyntacticElement source) {
		// We create a new world
		ISyntacticElement ww = source.getChild(GLOBAL);
		SpeciesDescription world =
			DescriptionFactory.createSpeciesDescription(WORLD_SPECIES, WORLD_AGENT_CLASS, model,
				WORLD_AGENT_CONSTRUCTOR, getSpeciesSkills(WORLD_SPECIES), ww == null ? new Facets()
					: ww.getFacets());
		model.addChild(world);
		// We then reattach the previous built-in species to the new world
		for ( TypeDescription sd : BUILT_IN_SPECIES.values() ) {
			sd.setSuperDescription(world);
			// model.addSpeciesDescription(sd);
			world.addChild(sd);
		}
		return world;
	}

	private IDescription createDefaultExperiment() {
		String type = GuiUtils.isInHeadLessMode() ? HEADLESS_UI : GUI_;
		ExperimentDescription desc =
			(ExperimentDescription) DescriptionFactory.create(type, NAME, DEFAULT_EXP, TYPE, type);
		desc.finalizeDescription();
		return desc;
	}

	public IModel compile(final ModelDescription description) {
		return (IModel) super.compile(description);
	}

	public ModelDescription validate(final ModelDescription description) {
		super.validate(description);
		return description;
	}

}
