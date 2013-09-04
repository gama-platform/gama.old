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
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
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
import msi.gaml.compilation.*;
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

	private void addMicroSpecies(final SpeciesDescription macro, final SyntacticElement micro) {
		// Create the species description without any children
		final SpeciesDescription mDesc = (SpeciesDescription) create(micro, macro, IChildrenProvider.NONE);
		// Add it to its macro-species
		macro.addChild(mDesc);
		// Recursively create each micro-species of the newly added micro-species
		for ( final SyntacticElement speciesNode : micro.getSpeciesChildren() ) {
			addMicroSpecies(mDesc, speciesNode);
		}
	}

	private void addExperiment(final ModelDescription model, final SyntacticElement experiment) {
		// Create the experiment description
		IDescription desc = create(experiment, model, IChildrenProvider.NONE);
		if ( !(desc instanceof ExperimentDescription) ) {
			desc = create(experiment, model, IChildrenProvider.NONE);
		}
		final ExperimentDescription eDesc = (ExperimentDescription) desc;
		model.addChild(eDesc);
	}

	/**
	 * Recursively complements a species and its micro-species.
	 * Add variables, behaviors (actions, reflex, task, states, ...), aspects to species.
	 * 
	 * @param macro the macro-species
	 * @param micro the structure of micro-species
	 */
	private void complementSpecies(final SpeciesDescription species, final SyntacticElement node) {
		if ( species == null ) { return; }
		species.copyJavaAdditions();
		// GuiUtils.debug("++++++ Building variables & behaviors of " + species.getName());
		final List<SyntacticElement> subspecies = new ArrayList();
		for ( final SyntacticElement child : node.getChildren() ) {
			if ( !child.isExperiment() && !child.isSpecies() ) {
				final IDescription childDesc = create(child, species);
				if ( childDesc != null ) {
					species.addChild(childDesc);
				}
			} else {
				subspecies.add(child);
			}
		}
		// recursively complement micro-species
		for ( final SyntacticElement e : subspecies ) {
			final SpeciesDescription sd = species.getMicroSpecies(e.getName());
			if ( sd != null ) {
				complementSpecies(sd, e);
			}
		}

	}

	private void parentSpecies(final SpeciesDescription macro, final SyntacticElement micro,
		final ModelDescription model) {
		// Gather the previously created species
		final SpeciesDescription mDesc = macro.getMicroSpecies(micro.getName());
		if ( mDesc == null || mDesc.isExperiment() ) { return; }
		String p = mDesc.getFacets().getLabel(IKeyword.PARENT);
		// If no parent is defined, we assume it is "agent"
		if ( p == null ) {
			p = IKeyword.AGENT;
		}
		final SpeciesDescription parent = model.getSpeciesDescription(p);
		mDesc.setParent(parent);
		for ( final SyntacticElement speciesNode : micro.getSpeciesChildren() ) {
			parentSpecies(mDesc, speciesNode, model);
		}
	}

	private void parentExperiment(final ModelDescription macro, final SyntacticElement micro,
		final ModelDescription model) {
		// Gather the previously created species
		final SpeciesDescription mDesc = macro.getExperiment(micro.getName());
		if ( mDesc == null ) { return; }
		final String p = mDesc.getFacets().getLabel(IKeyword.PARENT);
		// If no parent is defined, we assume it is "experiment"
		SpeciesDescription parent = model.getExperiment(p);
		if ( parent == null ) {
			parent = (SpeciesDescription) ModelDescription.ROOT.getTypesManager().getSpecies(IKeyword.EXPERIMENT);
		}
		mDesc.setParent(parent);
		// for ( SyntacticElement speciesNode : micro.getSpeciesChildren() ) {
		// parentSpecies(mDesc, speciesNode, model);
		// }
	}

	public ModelDescription assemble(final String projectPath, final String modelPath,
		final List<SyntacticElement> models) {
		// GuiUtils.debug("ModelFactory.assemble BEGIN " + modelPath);
		final Map<String, SyntacticElement> speciesNodes = new LinkedHashMap();
		final List<SyntacticElement> experimentNodes = new ArrayList();
		final SyntacticElement globalNodes = new SyntacticElement(GLOBAL, (EObject) null);
		ErrorCollector collector = new ErrorCollector();
		final Facets globalFacets = new Facets();
		final List<SyntacticElement> otherNodes = new ArrayList();
		final SyntacticElement source = models.get(0);
		SyntacticElement lastGlobalNode = source;
		for ( int n = models.size(), i = n - 1; i >= 0; i-- ) {
			final SyntacticElement e = models.get(i);
			if ( e != null ) {
				for ( final SyntacticElement se : e.getChildren() ) {
					if ( se.isGlobal() ) {
						// We build the facets resulting from the different arguments
						globalFacets.putAll(se.copyFacets());
						for ( final SyntacticElement ge : se.getChildren() ) {
							if ( ge.isSpecies() ) {
								speciesNodes.put(ge.getName(), ge);
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
						speciesNodes.put(se.getName(), se);
					} else if ( se.isExperiment() ) {
						experimentNodes.add(se);
					} else {
						if ( !ENVIRONMENT.equals(se.getKeyword()) ) {
							collector.add(new GamlCompilationError("This " + se.getKeyword() +
								" should be declared either in a species or in the global section", null, se
								.getElement(), true, false));
						}
						otherNodes.add(se);
					}
				}
			}
		}

		final String modelName = source.getLabel(NAME).replace(' ', '_') + "_model";
		globalFacets.putAsLabel(NAME, modelName);

		final ModelDescription model =
			new ModelDescription(modelName, null, projectPath, modelPath, lastGlobalNode.getElement(), null,
				ModelDescription.ROOT, globalFacets, collector);
		DescriptionFactory.setGamlDescription(source.getElement(), model);
		// model.setGlobal(true);
		model.addSpeciesType(model);

		// recursively add user-defined species to world and down on to the hierarchy
		for ( final SyntacticElement speciesNode : speciesNodes.values() ) {
			addMicroSpecies(model, speciesNode);
		}
		for ( final SyntacticElement experimentNode : experimentNodes ) {
			addExperiment(model, experimentNode);
		}

		// Parent the species and the experiments of the model (all are now known).
		for ( final SyntacticElement speciesNode : speciesNodes.values() ) {
			parentSpecies(model, speciesNode, model);
		}

		for ( final SyntacticElement experimentNode : experimentNodes ) {
			parentExperiment(model, experimentNode, model);
		}
		// Initialize the hierarchy of types
		model.buildTypes();

		// Make species and experiments recursively create their attributes, actions....
		complementSpecies(model, globalNodes);
		for ( final SyntacticElement speciesNode : speciesNodes.values() ) {
			complementSpecies(model.getMicroSpecies(speciesNode.getName()), speciesNode);
		}
		for ( final SyntacticElement experimentNode : experimentNodes ) {
			complementSpecies(model.getExperiment(experimentNode.getName()), experimentNode);
		}

		// Complement recursively the different species (incl. the world). The recursion is hierarchical
		final TypeTree<SpeciesDescription> hierarchy = model.getTypesManager().getSpeciesHierarchy();
		// GuiUtils.debug("Hierarchy: " + hierarchy.toStringWithDepth());
		final List<TypeNode<SpeciesDescription>> list = hierarchy.build(TypeTree.Order.PRE_ORDER);

		model.inheritFromParent();
		// GuiUtils.debug("ModelFactory.assemble building inheritance for " + list);
		for ( final TypeNode<SpeciesDescription> node : list ) {

			final SpeciesDescription sd = node.getData();
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
		for ( final SyntacticElement e : otherNodes ) {
			// COMPATIBILITY to remove the environment and put its definition in the world
			if ( ENVIRONMENT.equals(e.getKeyword()) ) {
				environmentDefined = translateEnvironment(model, e);
			} else {
				//
				final IDescription dd = create(e, model);
				if ( dd != null ) {
					model.addChild(dd);
				}
			}
		}
		if ( !environmentDefined ) {
			VariableDescription vd = model.getVariable(SHAPE);
			if ( !vd.getFacets().containsKey(INIT) ) {
				final Facets f = new Facets(NAME, SHAPE);
				f.put(INIT, GAML.getExpressionFactory().createOperator("envelope", model, new ConstantExpression(100)));
				final SyntacticElement shape = new SyntacticElement(IKeyword.GEOMETRY, f);
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

	private boolean translateEnvironment(final SpeciesDescription world, final SyntacticElement e) {
		final boolean environmentDefined = true;
		final SyntacticElement shape = new SyntacticElement(GEOMETRY, new Facets(NAME, SHAPE));
		IExpressionDescription bounds = e.getFacet(BOUNDS);
		if ( bounds == null ) {
			final IExpressionDescription width = e.getFacet(WIDTH);
			final IExpressionDescription height = e.getFacet(HEIGHT);
			if ( width != null && height != null ) {
				bounds = new OperatorExpressionDescription(IExpressionCompiler.INTERNAL_POINT, width, height);
			} else {
				bounds = ConstantExpressionDescription.create(100);
			}
		}
		bounds = new OperatorExpressionDescription("envelope", bounds);
		shape.setFacet(INIT, bounds);
		final IExpressionDescription depends = e.getFacet(DEPENDS_ON);
		if ( depends != null ) {
			shape.setFacet(DEPENDS_ON, depends);
		}
		final VariableDescription vd = (VariableDescription) create(shape, world);
		world.addChild(vd);
		world.resortVarName(vd);
		final IExpressionDescription ed = e.getFacet(TORUS);
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

	public ModelDescription createRootModel(final String name, final Class clazz, final SpeciesDescription macro,
		final SpeciesDescription parent) {
		final Facets f = new Facets(NAME, name, KEYWORD, MODEL);
		ModelDescription.ROOT = new ModelDescription(name, clazz, "", "", null, macro, parent, f);
		return ModelDescription.ROOT;
	}

}
