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
import msi.gama.kernel.model.IModel;
import msi.gama.precompiler.GamlAnnotations.factory;
import msi.gama.precompiler.*;
import msi.gama.util.GAML;
import msi.gaml.compilation.SyntacticElement;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.*;
import msi.gaml.statements.Facets;
import msi.gaml.types.*;
import org.eclipse.emf.ecore.EObject;

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

	private void addMicroSpecies(final SpeciesDescription macro, final ISyntacticElement micro) {
		// Create the species description without any children
		SpeciesDescription mDesc = (SpeciesDescription) create(micro, macro, IChildrenProvider.NONE);
		// Add it to its macro-species
		macro.addChild(mDesc);
		// Recursively create each micro-species of the newly added micro-species
		for ( ISyntacticElement speciesNode : micro.getSpeciesChildren() ) {
			addMicroSpecies(mDesc, speciesNode);
		}
	}

	private void addExperiment(ModelDescription model, final ISyntacticElement experiment) {
		// Create the experiment description
		ExperimentDescription eDesc = (ExperimentDescription) create(experiment, model, IChildrenProvider.NONE);
		model.addChild(eDesc);
	}

	/**
	 * Recursively complements a species and its micro-species.
	 * Add variables, behaviors (actions, reflex, task, states, ...), aspects to species.
	 * 
	 * @param macro the macro-species
	 * @param micro the structure of micro-species
	 */
	private void complementSpecies(final SpeciesDescription species, final ISyntacticElement node) {
		if ( species == null ) { return; }
		species.copyJavaAdditions();
		// GuiUtils.debug("++++++ Building variables & behaviors of " + species.getName());
		List<ISyntacticElement> subspecies = new ArrayList();
		for ( ISyntacticElement child : node.getChildren() ) {
			if ( !child.isExperiment() && !child.isSpecies() ) {
				IDescription childDesc = create(child, species);
				if ( childDesc != null ) {
					species.addChild(childDesc);
				}
			} else {
				subspecies.add(child);
			}
		}
		// recursively complement micro-species
		for ( ISyntacticElement e : subspecies ) {
			SpeciesDescription sd = species.getMicroSpecies(e.getName());
			if ( sd != null ) {
				complementSpecies(sd, e);
			}
		}

	}

	private void parentSpecies(final SpeciesDescription macro, final ISyntacticElement micro, ModelDescription model) {
		// Gather the previously created species
		SpeciesDescription mDesc = macro.getMicroSpecies(micro.getName());
		if ( mDesc == null || mDesc.isExperiment() ) { return; }
		String p = mDesc.getFacets().getLabel(IKeyword.PARENT);
		// If no parent is defined, we assume it is "agent"
		if ( p == null ) {
			p = IKeyword.AGENT;
		}
		SpeciesDescription parent = model.getSpeciesDescription(p);
		mDesc.setParent(parent);
		for ( ISyntacticElement speciesNode : micro.getSpeciesChildren() ) {
			parentSpecies(mDesc, speciesNode, model);
		}
	}

	private void parentExperiment(final ModelDescription macro, final ISyntacticElement micro, ModelDescription model) {
		// Gather the previously created species
		SpeciesDescription mDesc = macro.getExperiment(micro.getName());
		if ( mDesc == null ) { return; }
		String p = mDesc.getFacets().getLabel(IKeyword.PARENT);
		// If no parent is defined, we assume it is "experiment"
		SpeciesDescription parent = model.getExperiment(p);
		if ( parent == null ) {
			parent = (SpeciesDescription) ModelDescription.ROOT.getTypesManager().getSpecies(IKeyword.EXPERIMENT);
		}
		mDesc.setParent(parent);
		// for ( ISyntacticElement speciesNode : micro.getSpeciesChildren() ) {
		// parentSpecies(mDesc, speciesNode, model);
		// }
	}

	public ModelDescription assemble(final String projectPath, final String modelPath,
		final List<ISyntacticElement> models) {
		// GuiUtils.debug("ModelFactory.assemble BEGIN " + modelPath);
		final List<ISyntacticElement> speciesNodes = new ArrayList();
		final List<ISyntacticElement> experimentNodes = new ArrayList();
		final ISyntacticElement globalNodes = new SyntacticElement(GLOBAL, (EObject) null);
		final Facets globalFacets = new Facets();
		final List<ISyntacticElement> otherNodes = new ArrayList();
		// TODO Verify that it is the right model
		ISyntacticElement source = models.get(0);
		ISyntacticElement lastGlobalNode = source;
		for ( int n = models.size(), i = n - 1; i >= 0; i-- ) {
			ISyntacticElement e = models.get(i);
			for ( ISyntacticElement se : e.getChildren() ) {
				if ( se.isGlobal() ) {
					// We build the facets resulting from the different arguments
					globalFacets.putAll(se.getFacets());
					for ( ISyntacticElement ge : se.getChildren() ) {
						if ( ge.isSpecies() ) {
							speciesNodes.add(ge);
						} else if ( ge.isExperiment() ) {
							experimentNodes.add(ge);
						} else {
							if ( i == 0 ) {
								lastGlobalNode = ge;
							}
							globalNodes.addChild(ge);
						}
					}

				} else if ( se.isSpecies() ) {
					speciesNodes.add(se);
				} else if ( se.isExperiment() ) {
					experimentNodes.add(se);
				} else {
					otherNodes.add(se);
				}
			}
		}

		String modelName = source.getLabel(NAME).replace(' ', '_') + "_model";
		globalFacets.putAsLabel(NAME, modelName);

		ModelDescription model =
			new ModelDescription(modelName, null, projectPath, modelPath, lastGlobalNode.getElement(), null,
				ModelDescription.ROOT, globalFacets);
		DescriptionFactory.setGamlDescription(source.getElement(), model);
		// model.setGlobal(true);
		model.addSpeciesType(model);

		// recursively add user-defined species to world and down on to the hierarchy
		for ( ISyntacticElement speciesNode : speciesNodes ) {
			addMicroSpecies(model, speciesNode);
		}
		for ( ISyntacticElement experimentNode : experimentNodes ) {
			addExperiment(model, experimentNode);
		}

		// Parent the species and the experiments of the model (all are now known).
		for ( ISyntacticElement speciesNode : speciesNodes ) {
			parentSpecies(model, speciesNode, model);
		}

		for ( ISyntacticElement experimentNode : experimentNodes ) {
			parentExperiment(model, experimentNode, model);
		}
		// Initialize the hierarchy of types
		model.buildTypes();

		// Make species and experiments recursively create their attributes, actions....
		complementSpecies(model, globalNodes);
		for ( ISyntacticElement speciesNode : speciesNodes ) {
			complementSpecies(model.getMicroSpecies(speciesNode.getName()), speciesNode);
		}
		for ( ISyntacticElement experimentNode : experimentNodes ) {
			complementSpecies(model.getExperiment(experimentNode.getName()), experimentNode);
		}

		// Complement recursively the different species (incl. the world). The recursion is hierarchical
		TypeTree<SpeciesDescription> hierarchy = model.getTypesManager().getSpeciesHierarchy();
		// GuiUtils.debug("Hierarchy: " + hierarchy.toStringWithDepth());
		List<TypeNode<SpeciesDescription>> list = hierarchy.build(TypeTree.Order.PRE_ORDER);

		model.inheritFromParent();
		// GuiUtils.debug("ModelFactory.assemble building inheritance for " + list);
		for ( TypeNode<SpeciesDescription> node : list ) {

			SpeciesDescription sd = node.getData();
			if ( !sd.isBuiltIn() ) {
				// GuiUtils.debug("Copying Java additions and parent additions to " + sd.getName());
				sd.inheritFromParent();
				if ( sd.isExperiment() ) {
					sd.finalizeDescription();
				}
			}
		}

		// The same for experiments
		// FIXME Needs to be done hierarchically
		// for ( String s : model.getExperimentNames() ) {
		// ExperimentDescription ed = model.getExperiment(s);
		// ed.inheritFromParent();
		// ed.finalizeDescription();
		//
		// }
		model.finalizeDescription();

		// We now can safely put the model inside "experiment"
		model.setEnclosingDescription(ModelDescription.ROOT.getSpeciesDescription(EXPERIMENT));

		// Parse the other definitions (output, environment, ...)
		boolean environmentDefined = false;
		for ( final ISyntacticElement e : otherNodes ) {
			// COMPATIBILITY to remove the environment and put its definition in the world
			if ( ENVIRONMENT.equals(e.getKeyword()) ) {
				environmentDefined = translateEnvironment(model, e);
			} else {
				//
				IDescription dd = create(e, model);
				if ( dd != null ) {
					model.addChild(dd);
				}
			}
		}
		if ( !environmentDefined ) {
			VariableDescription vd = model.getVariable(SHAPE);
			if ( !vd.getFacets().containsKey(INIT) ) {
				Facets f = new Facets(NAME, SHAPE);
				f.put(INIT, GAML.getExpressionFactory().createOperator("envelope", model, new ConstantExpression(100)));
				ISyntacticElement shape = new SyntacticElement(IKeyword.GEOMETRY, f);
				vd = (VariableDescription) create(shape, model);
				model.addChild(vd);
				model.resortVarName(vd);
			}
		}
		// Gather the species created to see if some describe experiments, in which case they are
		// added to the experiments of the model
		// TODO Verify this selfAndParentMicroSpecies() !
		// for ( IDescription desc : world.getSelfAndParentMicroSpecies() ) {
		// if ( desc instanceof ExperimentDescription ) {
		// model.addChild(desc);
		// }
		// }

		return model;

	}

	private boolean translateEnvironment(SpeciesDescription world, final ISyntacticElement e) {
		boolean environmentDefined = true;
		ISyntacticElement shape = new SyntacticElement(GEOMETRY, new Facets(NAME, SHAPE));
		IExpressionDescription bounds = e.getFacet(BOUNDS);
		if ( bounds == null ) {
			IExpressionDescription width = e.getFacet(WIDTH);
			IExpressionDescription height = e.getFacet(HEIGHT);
			if ( width != null && height != null ) {
				bounds = new OperatorExpressionDescription(IExpressionCompiler.INTERNAL_POINT, width, height);
			} else {
				bounds = ConstantExpressionDescription.create(100);
			}
		}
		bounds = new OperatorExpressionDescription("envelope", bounds);
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

	public IModel compile(final ModelDescription description) {
		return (IModel) super.compile(description);
	}

	public ModelDescription validate(final ModelDescription description) {
		super.validate(description);
		return description;
	}

	public ModelDescription createRootModel(String name, Class clazz, SpeciesDescription macro,
		SpeciesDescription parent) {
		Facets f = new Facets(NAME, name, KEYWORD, MODEL);
		ModelDescription.ROOT = new ModelDescription(name, clazz, "", "", null, macro, parent, f);
		return ModelDescription.ROOT;
	}

}
