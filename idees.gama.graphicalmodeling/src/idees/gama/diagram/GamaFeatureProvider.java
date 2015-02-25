package idees.gama.diagram;

import gama.*;
import idees.gama.features.add.*;
import idees.gama.features.create.*;
import idees.gama.features.layout.*;
import idees.gama.features.modelgeneration.*;
import idees.gama.features.others.*;
import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.model.IModel;
import msi.gama.outputs.*;
import msi.gaml.architecture.reflex.ReflexStatement;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.*;
import msi.gaml.variables.*;
import org.eclipse.emf.transaction.*;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.*;
import org.eclipse.graphiti.features.context.*;
import org.eclipse.graphiti.features.context.impl.*;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.internal.datatypes.impl.LocationImpl;
import org.eclipse.graphiti.mm.pictograms.*;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.features.DefaultFeatureProvider;

public class GamaFeatureProvider extends DefaultFeatureProvider {

	private String typeOfModel;
	private IModel gamaModel;
	private final GamaFeatureProvider fp;
	private final List<String> built_in_species = Arrays.asList("osm_node", "osm_building", "osm_road", "graph_edge",
		"graph_node", "AgentDB", "Physical3DWorld", "cluster_builder", "experimentator", "agent",
		"multicriteria_analyzer", "base_node", "base_edge", "world", "node", "edge");
	private final List<String> built_in_variables = Arrays.asList("fatal", "duration", "machine_time", "step",
		"model_path", "total_duration", "seed", "average_duration", "warnings", "cycle", "time", "rng", "project_path",
		"workspace_path", "graph_edge", "graph_node", "AgentDB", "Physical3DWorld", "cluster_builder",
		"experimentator", "agent", "multicriteria_analyzer", "base_node", "base_edge", "shape", "location", "agents",
		"peers", "members", "name", "population", "host");
	private final List<String> built_in_actions = Arrays.asList("goto", "move", "wander", "follow", "wander_3D",
		"_init_", "_step_", "error", "pause", "die", "write", "tell", "debug", "percieved_area", "halt",
		"neighbourhood_exclusive");

	public GamaFeatureProvider(final IDiagramTypeProvider dtp) {
		super(dtp);
		fp = this;
	}

	@Override
	public IAddFeature getAddFeature(final IAddContext context) {
		if ( context.getNewObject() instanceof EWorldAgent ) {
			return new AddWorldFeature(this);
		} else if ( context.getNewObject() instanceof ESpecies ) {
			return new AddSpeciesFeature(this);
		} else if ( context.getNewObject() instanceof EAction ) {
			return new AddActionFeature(this);
		} else if ( context.getNewObject() instanceof EReflex ) {
			return new AddReflexFeature(this);
		} else if ( context.getNewObject() instanceof EAspect ) {
			return new AddAspectFeature(this);
		} else if ( context.getNewObject() instanceof EGUIExperiment ) {
			return new AddGuiExperimentFeature(this);
		} else if ( context.getNewObject() instanceof EBatchExperiment ) {
			return new AddBatchExperimentFeature(this);
		} else if ( context.getNewObject() instanceof EDisplay ) {
			return new AddDisplayFeature(this);
		} else if ( context.getNewObject() instanceof ESubSpeciesLink ) {
			return new AddSubSpecieLinkFeature(this);
		} else if ( context.getNewObject() instanceof EActionLink ) {
			return new AddActionLinkFeature(this);
		} else if ( context.getNewObject() instanceof EReflexLink ) {
			return new AddReflexLinkFeature(this);
		} else if ( context.getNewObject() instanceof EAspectLink ) {
			return new AddAspectLinkFeature(this);
		} else if ( context.getNewObject() instanceof EDisplayLink ) {
			return new AddDisplayLinkFeature(this);
		} else if ( context.getNewObject() instanceof EExperimentLink ) {
			return new AddEExperimentLinkFeature(this);
		} else if ( context.getNewObject() instanceof EInheritLink ) {
			return new AddInheritingLinkFeature(this);
		} else if ( context.getNewObject() instanceof EGraphLink ) { return new AddGraphLinkFeature(this); }
		return super.getAddFeature(context);
	}

	@Override
	public ICreateFeature[] getCreateFeatures() {
		// init();
		return new ICreateFeature[] {};

	}

	public void init() {
		final Diagram diagram = getDiagramTypeProvider().getDiagram();

		if ( diagram.getChildren().isEmpty() ) {
			TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(diagram);
			domain.getCommandStack().execute(new RecordingCommand(domain) {

				@Override
				public void doExecute() {
					EWorldAgent newClass = gama.GamaFactory.eINSTANCE.createEWorldAgent();
					diagram.eResource().getContents().add(newClass);
					newClass.setName("world");
					EContinuousTopology newTopo = gama.GamaFactory.eINSTANCE.createEContinuousTopology();
					diagram.eResource().getContents().add(newTopo);
					newClass.setTopology(newTopo);

					CreateContext ac = new CreateContext();
					ac.setLocation(100, 50);
					ac.setSize(0, 0);
					ac.setTargetContainer(diagram);
					GamaDiagramEditor diagramEditor = (GamaDiagramEditor) getDiagramTypeProvider().getDiagramEditor();
					diagramEditor.addEOject(newClass);

					PictogramElement worldPE = addIfPossible(new AddContext(ac, newClass));
					if ( "skeleton".equals(typeOfModel) ) {
						initSkeleton(newClass, diagram);
					} else if ( "example".equals(typeOfModel) ) {
						initSkeleton(newClass, diagram);
					} else if ( "custom".equals(typeOfModel) ) {
						initCustom(newClass, worldPE, diagram);
						LayoutDiagramFeature.execute(diagram);
						IUpdateContext context = new UpdateContext(diagram);
						IUpdateFeature dfp = fp.getUpdateFeature(context);

						dfp.execute(context);

					} else {
						initSimple(newClass, diagram);
					}
					ModelGenerator.modelValidation(fp, diagram);
					diagramEditor.updateEObjectErrors();
				}
			});
		}
	}

	public void initSimple(final EWorldAgent world, final Diagram diagram) {
		final CreateGuiExperimentLinkFeature cXp = new CreateGuiExperimentLinkFeature(this);
		final CreateDisplayLinkFeature cDisp = new CreateDisplayLinkFeature(this);
		PictogramElement worldPe = getPictogramElementForBusinessObject(world);

		CreateConnectionContext context = new CreateConnectionContext();
		context.setSourcePictogramElement(worldPe);
		context.setSourceAnchor(Graphiti.getPeService().getChopboxAnchor((AnchorContainer) worldPe));
		// context.setSourceLocation(new LocationImpl(100,50));
		context.setTargetLocation(new LocationImpl(600, 50));
		Connection link = cXp.create(context, false);
		AddConnectionContext cont = new AddConnectionContext(null, null);
		cont.setNewObject(link);
		addIfPossible(cont);
	}

	@SuppressWarnings("restriction")
	public void initSkeleton(final EWorldAgent world, final Diagram diagram) {
		final CreateGuiExperimentLinkFeature cXp = new CreateGuiExperimentLinkFeature(this);
		final CreateDisplayLinkFeature cDisp = new CreateDisplayLinkFeature(this);
		PictogramElement worldPe = getPictogramElementForBusinessObject(world);

		CreateConnectionContext context = new CreateConnectionContext();
		context.setSourcePictogramElement(worldPe);
		context.setSourceAnchor(Graphiti.getPeService().getChopboxAnchor((AnchorContainer) worldPe));
		// context.setSourceLocation(new LocationImpl(100,50));
		context.setTargetLocation(new LocationImpl(600, 50));
		Connection link = cXp.create(context, false);
		AddConnectionContext cont = new AddConnectionContext(null, null);
		cont.setNewObject(link);
		addIfPossible(cont);

		CreateConnectionContext contextDisp = new CreateConnectionContext();
		PictogramElement xp = null;
		List<Shape> contents = diagram.getChildren();
		for ( Shape obj : contents ) {
			Object bo = getBusinessObjectForPictogramElement(obj);
			if ( bo instanceof EExperiment ) {
				xp = obj;
				break;
			}

		}
		contextDisp.setSourcePictogramElement(xp);
		contextDisp.setSourceAnchor(Graphiti.getPeService().getChopboxAnchor((AnchorContainer) xp));
		// contextDisp.setSourceLocation(new LocationImpl(500,50));
		contextDisp.setTargetLocation(new LocationImpl(800, 50));
		Connection linkDisp = cDisp.create(contextDisp, false);
		AddConnectionContext contDisp = new AddConnectionContext(null, null);
		contDisp.setNewObject(linkDisp);
		addIfPossible(contDisp);
	}

	public void addVariable(final IVariable var, final ESpecies target, final List<String> listSpecies) {
		if ( built_in_variables.contains(var.getName()) || built_in_species.contains(var.getName()) ||
			listSpecies.contains(var.getName()) ) { return; }
		EVariable eVar = gama.GamaFactory.eINSTANCE.createEVariable();
		eVar.setType(var.getType().toString());
		eVar.setName(var.getName());
		if ( var.hasFacet(IKeyword.INIT) ) {
			eVar.setInit(var.getFacet(IKeyword.INIT).serialize(false));
		}
		if ( var.hasFacet(IKeyword.UPDATE) ) {
			eVar.setUpdate(var.getFacet(IKeyword.UPDATE).serialize(false));
		}
		if ( var.hasFacet(IKeyword.MIN) ) {
			eVar.setMin(var.getFacet(IKeyword.MIN).serialize(false));
		}
		if ( var.hasFacet(IKeyword.MAX) ) {
			eVar.setMax(var.getFacet(IKeyword.MAX).serialize(false));
		}
		if ( var.hasFacet(IKeyword.FUNCTION) ) {
			eVar.setFunction(var.getFacet(IKeyword.FUNCTION).serialize(false));
		}
		if ( var.hasFacet(IKeyword.VALUE) ) {
			eVar.setFunction(var.getFacet(IKeyword.VALUE).serialize(false));
		}
		target.getVariables().add(eVar);
	}

	public ESpecies createMicroSpecies(final ESpecies source, final PictogramElement sourceE, final ISpecies species,
		final Diagram diagram, final List<String> listSpecies) {
		ESpecies target = gama.GamaFactory.eINSTANCE.createESpecies();
		diagram.eResource().getContents().add(target);
		/*
		 * Collection<ISkill> skills = ((SpeciesDescription) species.getDescription()).getSkills().values();
		 * for (ISkill sk : skills) {
		 * target.getSkills().add(sk.toString());
		 * 
		 * }
		 */
		target.setName(species.getName());

		ETopology newTopo = null;
		if ( species.isGrid() ) {
			newTopo = gama.GamaFactory.eINSTANCE.createEGridTopology();
		} else if ( species.isGraph() ) {
			newTopo = gama.GamaFactory.eINSTANCE.createEGraphTopologyNode();
		} else {
			newTopo = gama.GamaFactory.eINSTANCE.createEContinuousTopology();
		}

		diagram.eResource().getContents().add(newTopo);
		target.setTopology(newTopo);

		for ( IVariable var : species.getVars() ) {
			if ( ((Variable) var).getgSkill() == null ) {
				addVariable(var, target, listSpecies);
			}
		}

		CreateContext ac = new CreateContext();

		ac.setLocation(0, 0);
		ac.setSize(0, 0);
		ac.setTargetContainer(diagram);

		PictogramElement targetE = addIfPossible(new AddContext(ac, target));

		ESubSpeciesLink eReference = gama.GamaFactory.eINSTANCE.createESubSpeciesLink();
		diagram.eResource().getContents().add(eReference);

		// add connection for business object
		AddConnectionContext addContext = new AddConnectionContext(getAnchor(sourceE), getAnchor(targetE));
		addContext.setNewObject(eReference);
		addIfPossible(addContext);
		eReference.setMacro(source);
		eReference.setMicro(target);
		source.getMicroSpeciesLinks().add(eReference);
		target.getMacroSpeciesLinks().add(eReference);
		GamaDiagramEditor diagramEditor = (GamaDiagramEditor) getDiagramTypeProvider().getDiagramEditor();
		diagramEditor.addEOject(target);

		for ( ActionStatement action : species.getActions() ) {
			if ( !built_in_actions.contains(action.getName()) ) {
				createAction(target, targetE, action, diagram);
			}
		}
		for ( IStatement stat : species.getBehaviors() ) {
			if ( stat instanceof ReflexStatement ) {
				if ( stat.getName() != null && !stat.getName().isEmpty() && !stat.getName().startsWith("internal_init") ) {
					createReflex(target, targetE, (ReflexStatement) stat, diagram);
				} else {
					String gmlCode = "";
					ReflexStatement rs = (ReflexStatement) stat;
					if ( rs.getCommands() != null ) {
						for ( IStatement st : rs.getCommands() ) {
							if ( st == null ) {
								continue;
							}
							gmlCode += st.serialize(false);
						}
					}
					target.setInit(gmlCode);
				}
			}
			// System.out.println("stat : " + stat);
			// if (stat instanceof ReflexStatement)
			// createReflex( target, targetE, (ReflexStatement) stat, diagram);
		}
		for ( IExecutable asp : species.getAspects() ) {
			if ( asp instanceof AspectStatement ) {
				createAspect(target, targetE, (AspectStatement) asp, diagram);
			}
		}

		return target;
	}

	public EExperiment createXP(final ESpecies source, final PictogramElement sourceE, final IExperimentPlan xp,
		final Diagram diagram) {
		EExperiment target = null;
		if ( xp.isGui() ) {
			target = gama.GamaFactory.eINSTANCE.createEGUIExperiment();
		} else {
			target = gama.GamaFactory.eINSTANCE.createEBatchExperiment();
		}
		diagram.eResource().getContents().add(target);
		target.setName(xp.getName());// xp.getName().substring("Experiment ".length(), xp.getName().length()));
		CreateContext ac = new CreateContext();

		ac.setLocation(0, 0);

		ac.setSize(0, 0);
		ac.setTargetContainer(diagram);
		PictogramElement targetE = addIfPossible(new AddContext(ac, target));

		EExperimentLink eReference = gama.GamaFactory.eINSTANCE.createEExperimentLink();
		diagram.eResource().getContents().add(eReference);

		// add connection for business object
		AddConnectionContext addContext = new AddConnectionContext(getAnchor(sourceE), getAnchor(targetE));
		addContext.setNewObject(eReference);
		addIfPossible(addContext);
		eReference.setSpecies(source);
		eReference.setExperiment(target);
		source.getExperimentLinks().add(eReference);
		target.setExperimentLink(eReference);
		GamaDiagramEditor diagramEditor = (GamaDiagramEditor) getDiagramTypeProvider().getDiagramEditor();
		diagramEditor.addEOject(target);
		if ( xp != null && xp.isGui() && xp.getSimulationOutputs() != null ) {
			for ( IOutput output : ((AbstractOutputManager) xp.getSimulationOutputs()).getOutputs().values() ) {
				if ( output instanceof LayeredDisplayOutput ) {
					createDisplay((EGUIExperiment) target, targetE, output, diagram);
				}
			}
		}

		return target;
	}

	public EAction createAction(final ESpecies source, final PictogramElement sourceE, final ActionStatement action,
		final Diagram diagram) {
		if ( action == null ) { return null; }
		EAction target = gama.GamaFactory.eINSTANCE.createEAction();
		diagram.eResource().getContents().add(target);
		target.setName(action.getName());
		String gmlCode = "";
		if ( action.getCommands() != null ) {
			for ( IStatement st : action.getCommands() ) {
				gmlCode += st.serialize(false) + System.getProperty("line.separator");
			}
		}
		target.setGamlCode(gmlCode);
		CreateContext ac = new CreateContext();

		ac.setLocation(0, 0);

		ac.setSize(0, 0);
		ac.setTargetContainer(diagram);

		PictogramElement targetE = addIfPossible(new AddContext(ac, target));

		EActionLink eReference = gama.GamaFactory.eINSTANCE.createEActionLink();
		diagram.eResource().getContents().add(eReference);

		// add connection for business object
		AddConnectionContext addContext = new AddConnectionContext(getAnchor(sourceE), getAnchor(targetE));
		addContext.setNewObject(eReference);
		addIfPossible(addContext);
		eReference.setSpecies(source);
		eReference.setAction(target);
		source.getActionLinks().add(eReference);
		target.getActionLinks().add(eReference);
		GamaDiagramEditor diagramEditor = (GamaDiagramEditor) getDiagramTypeProvider().getDiagramEditor();
		diagramEditor.addEOject(target);

		return target;
	}

	public EDisplay createDisplay(final EGUIExperiment source, final PictogramElement sourceE, final IOutput display,
		final Diagram diagram) {
		if ( display == null ) { return null; }
		EDisplay target = gama.GamaFactory.eINSTANCE.createEDisplay();
		diagram.eResource().getContents().add(target);
		target.setName(display.getName());

		CreateContext ac = new CreateContext();

		ac.setLocation(0, 0);

		ac.setSize(0, 0);
		ac.setTargetContainer(diagram);

		PictogramElement targetE = addIfPossible(new AddContext(ac, target));

		EDisplayLink eReference = gama.GamaFactory.eINSTANCE.createEDisplayLink();
		diagram.eResource().getContents().add(eReference);

		// add connection for business object
		AddConnectionContext addContext = new AddConnectionContext(getAnchor(sourceE), getAnchor(targetE));
		addContext.setNewObject(eReference);
		addIfPossible(addContext);
		eReference.setExperiment(source);
		eReference.setDisplay(target);
		source.getDisplayLinks().add(eReference);
		target.setDisplayLink(eReference);
		GamaDiagramEditor diagramEditor = (GamaDiagramEditor) getDiagramTypeProvider().getDiagramEditor();
		diagramEditor.addEOject(target);

		return target;
	}

	public EAspect createAspect(final ESpecies source, final PictogramElement sourceE, final AspectStatement aspect,
		final Diagram diagram) {
		if ( aspect == null ) { return null; }
		EAspect target = gama.GamaFactory.eINSTANCE.createEAspect();
		diagram.eResource().getContents().add(target);
		target.setName(aspect.getName());

		CreateContext ac = new CreateContext();

		ac.setLocation(0, 0);

		ac.setSize(0, 0);
		ac.setTargetContainer(diagram);

		PictogramElement targetE = addIfPossible(new AddContext(ac, target));

		EAspectLink eReference = gama.GamaFactory.eINSTANCE.createEAspectLink();
		diagram.eResource().getContents().add(eReference);

		// add connection for business object
		AddConnectionContext addContext = new AddConnectionContext(getAnchor(sourceE), getAnchor(targetE));
		addContext.setNewObject(eReference);
		addIfPossible(addContext);
		eReference.setSpecies(source);
		eReference.setAspect(target);
		source.getAspectLinks().add(eReference);
		target.getAspectLinks().add(eReference);
		GamaDiagramEditor diagramEditor = (GamaDiagramEditor) getDiagramTypeProvider().getDiagramEditor();
		diagramEditor.addEOject(target);

		return target;
	}

	public EReflex createReflex(final ESpecies source, final PictogramElement sourceE, final ReflexStatement reflex,
		final Diagram diagram) {
		if ( reflex == null ) { return null; }
		EReflex target = gama.GamaFactory.eINSTANCE.createEReflex();
		diagram.eResource().getContents().add(target);
		target.setName(reflex.getName());
		String gmlCode = "";

		if ( reflex.getCommands() != null ) {
			for ( IStatement st : reflex.getCommands() ) {
				if ( st == null ) {
					continue;
				}
				gmlCode += st.serialize(false);
			}
		}
		if ( reflex.hasFacet(IKeyword.WHEN) ) {
			target.setCondition(reflex.getFacet(IKeyword.WHEN).serialize(false));
		}

		target.setGamlCode(gmlCode);
		CreateContext ac = new CreateContext();

		ac.setLocation(0, 0);

		ac.setSize(0, 0);
		ac.setTargetContainer(diagram);

		PictogramElement targetE = addIfPossible(new AddContext(ac, target));

		EReflexLink eReference = gama.GamaFactory.eINSTANCE.createEReflexLink();
		diagram.eResource().getContents().add(eReference);

		// add connection for business object
		AddConnectionContext addContext = new AddConnectionContext(getAnchor(sourceE), getAnchor(targetE));
		addContext.setNewObject(eReference);
		addIfPossible(addContext);
		eReference.setSpecies(source);
		eReference.setReflex(target);
		source.getReflexLinks().add(eReference);
		target.getReflexLinks().add(eReference);
		GamaDiagramEditor diagramEditor = (GamaDiagramEditor) getDiagramTypeProvider().getDiagramEditor();
		diagramEditor.addEOject(target);

		return target;
	}

	protected Anchor getAnchor(final PictogramElement targetpe) {
		Anchor ret = null;
		if ( targetpe instanceof Anchor ) {
			ret = (Anchor) targetpe;
		} else if ( targetpe instanceof AnchorContainer ) {
			ret = Graphiti.getPeService().getChopboxAnchor((AnchorContainer) targetpe);
		}
		return ret;
	}

	public void initCustom(final EWorldAgent eWorld, final PictogramElement worldPE, final Diagram diagram) {
		List<String> listSpecies = new ArrayList<String>();
		listSpecies.addAll(gamaModel.getMicroSpeciesNames());
		for ( IVariable var : gamaModel.getVars() ) {
			addVariable(var, eWorld, listSpecies);
		}
		for ( ActionStatement action : gamaModel.getActions() ) {
			if ( !built_in_actions.contains(action.getName()) ) {
				createAction(eWorld, worldPE, action, diagram);
			}
		}
		for ( IStatement stat : gamaModel.getBehaviors() ) {
			if ( stat instanceof ReflexStatement ) {
				if ( stat.getName() != null && !stat.getName().isEmpty() && !stat.getName().startsWith("internal_init") ) {
					createReflex(eWorld, worldPE, (ReflexStatement) stat, diagram);
				} else {
					String gmlCode = "";
					ReflexStatement rs = (ReflexStatement) stat;
					if ( rs.getCommands() != null ) {
						for ( IStatement st : rs.getCommands() ) {
							if ( st == null ) {
								continue;
							}
							gmlCode += st.serialize(false);
						}
					}
					eWorld.setInit(gmlCode);
				}
			}
		}
		buildAgent(gamaModel, eWorld, worldPE, diagram, listSpecies);

	}

	public void buildAgent(final IModel gamaSpecies, final ESpecies species, final PictogramElement speciesE,
		final Diagram diagram, final List<String> listSpecies) {
		Set<String> xpNames = gamaSpecies.getDescription().getModelDescription().getExperimentNames();
		for ( String xpN : xpNames ) {
			createXP(species, speciesE, gamaSpecies.getExperiment(xpN), diagram);
		}
		for ( ISpecies sp : gamaSpecies.getMicroSpecies() ) {

			if ( built_in_species.contains(sp.getName()) ) {
				continue;
			}

			createMicroSpecies(species, speciesE, sp, diagram, listSpecies);

		}
	}

	@Override
	public ILayoutFeature getLayoutFeature(final ILayoutContext context) {
		PictogramElement pictogramElement = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pictogramElement);
		if ( bo instanceof ESpecies ) {
			return new LayoutESpeciesFeature(this);
		} else if ( bo instanceof EExperiment ) {
			return new LayoutEExperimentFeature(this);
		} else if ( bo instanceof EGamaObject ) { return new LayoutCommonFeature(this); }
		return super.getLayoutFeature(context);
	}

	@Override
	public IFeature[] getDragAndDropFeatures(final IPictogramElementContext context) {
		// simply return all create connection features
		return getCreateConnectionFeatures();
	}

	@Override
	public ICreateConnectionFeature[] getCreateConnectionFeatures() {
		return new ICreateConnectionFeature[] {
			new CreateSubSpeciesLinkFeature(this),
			new CreateSubGridLinkFeature(this),
			// new CreateSubGraphSpeciesFeature(this),
			new CreateInheritingLinkFeature(this), new CreateActionLinkFeature(this),
			new CreateReflexLinkFeature(this), new CreateAspectLinkFeature(this), new CreateDisplayLinkFeature(this),
			new CreateBatchExperimentLinkFeature(this), new CreateGuiExperimentLinkFeature(this) };
	}

	@Override
	public ICustomFeature[] getCustomFeatures(final ICustomContext context) {
		return new ICustomFeature[] {/* new RenameEGamaObjectFeature(this), */new ModelGenerationFeature(this),
			new LayoutDiagramFeature(this), new ChangeColorEGamaObjectFeature(this) };
	}

	@Override
	public IRemoveFeature getRemoveFeature(final IRemoveContext context) {
		return new EmptyRemoveFeature(this);
	}

	@Override
	public IDeleteFeature getDeleteFeature(final IDeleteContext context) {
		return new CustomDeleteFeature(this);
	}

	@Override
	public IUpdateFeature getUpdateFeature(final IUpdateContext context) {
		PictogramElement pictogramElement = context.getPictogramElement();
		if ( pictogramElement instanceof ContainerShape ) {
			Object bo = getBusinessObjectForPictogramElement(pictogramElement);
			if ( bo instanceof EGamaObject ) { return new UpdateEGamaObjectFeature(this); }
		}
		return super.getUpdateFeature(context);
	}

	public String getTypeOfModel() {
		return typeOfModel;
	}

	public void setTypeOfModel(final String typeOfModel) {
		this.typeOfModel = typeOfModel;
	}

	public IModel getGamaModel() {
		return gamaModel;
	}

	public void setGamaModel(final IModel gamaModel) {
		this.gamaModel = gamaModel;
	}

}