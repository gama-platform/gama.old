package idees.gama.diagram;


import gama.EAction;
import gama.EActionLink;
import gama.EAspect;
import gama.EAspectLink;
import gama.EBatchExperiment;
import gama.EContinuousTopology;
import gama.EDisplay;
import gama.EDisplayLink;
import gama.EExperiment;
import gama.EExperimentLink;
import gama.EGUIExperiment;
import gama.EGamaObject;
import gama.EGraphLink;
import gama.EInheritLink;
import gama.EReflex;
import gama.EReflexLink;
import gama.ESpecies;
import gama.ESubSpeciesLink;
import gama.ETopology;
import gama.EVariable;
import gama.EWorldAgent;
import idees.gama.features.add.AddActionFeature;
import idees.gama.features.add.AddActionLinkFeature;
import idees.gama.features.add.AddAspectFeature;
import idees.gama.features.add.AddAspectLinkFeature;
import idees.gama.features.add.AddBatchExperimentFeature;
import idees.gama.features.add.AddDisplayFeature;
import idees.gama.features.add.AddDisplayLinkFeature;
import idees.gama.features.add.AddEExperimentLinkFeature;
import idees.gama.features.add.AddGraphLinkFeature;
import idees.gama.features.add.AddGuiExperimentFeature;
import idees.gama.features.add.AddInheritingLinkFeature;
import idees.gama.features.add.AddReflexFeature;
import idees.gama.features.add.AddReflexLinkFeature;
import idees.gama.features.add.AddSpeciesFeature;
import idees.gama.features.add.AddSubSpecieLinkFeature;
import idees.gama.features.add.AddWorldFeature;
import idees.gama.features.create.CreateActionLinkFeature;
import idees.gama.features.create.CreateAspectLinkFeature;
import idees.gama.features.create.CreateBatchExperimentLinkFeature;
import idees.gama.features.create.CreateDisplayLinkFeature;
import idees.gama.features.create.CreateGuiExperimentLinkFeature;
import idees.gama.features.create.CreateInheritingLinkFeature;
import idees.gama.features.create.CreateReflexLinkFeature;
import idees.gama.features.create.CreateSubGraphSpeciesFeature;
import idees.gama.features.create.CreateSubGridLinkFeature;
import idees.gama.features.create.CreateSubSpeciesLinkFeature;
import idees.gama.features.layout.LayoutCommonFeature;
import idees.gama.features.layout.LayoutDiagramFeature;
import idees.gama.features.layout.LayoutEExperimentFeature;
import idees.gama.features.layout.LayoutESpeciesEExperimentFeature;
import idees.gama.features.modelgeneration.ModelGenerationFeature;
import idees.gama.features.others.ChangeColorEGamaObjectFeature;
import idees.gama.features.others.RenameEGamaObjectFeature;
import idees.gama.features.others.UpdateEGamaObjectFeature;

import java.util.Collection;
import java.util.List;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.IExperimentSpecies;
import msi.gama.kernel.model.IModel;
import msi.gama.outputs.AbstractOutputManager;
import msi.gama.outputs.IOutput;
import msi.gama.outputs.LayeredDisplayOutput;
import msi.gama.util.GamaList;
import msi.gaml.architecture.reflex.ReflexStatement;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.skills.ISkill;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.ActionStatement;
import msi.gaml.statements.AspectStatement;
import msi.gaml.statements.IExecutable;
import msi.gaml.statements.IStatement;
import msi.gaml.variables.IVariable;
import msi.gaml.variables.Variable;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeature;
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.features.context.impl.CreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.CreateContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.internal.datatypes.impl.LocationImpl;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement; 
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.features.DefaultFeatureProvider;

public class GamaFeatureProvider extends DefaultFeatureProvider {
 
	private String typeOfModel;
	private IModel gamaModel;
	private List<String> built_in_species =  GamaList.with("graph_edge", "graph_node", "AgentDB", "Physical3DWorld", "cluster_builder","experimentator", "agent", "multicriteria_analyzer", "base_node", "base_edge");
	private List<String> built_in_variables =  GamaList.with("fatal", "duration", "machine_time", "step", "model_path", "total_duration", "seed", "average_duration", "warnings", "cycle", "time", "rng", "project_path", "workspace_path", "graph_edge", "graph_node", "AgentDB", "Physical3DWorld", "cluster_builder","experimentator", "agent", "multicriteria_analyzer", "base_node", "base_edge", "shape", "location", "agents", "peers", "members","name", "population", "host");
	private List<String> built_in_actions =  GamaList.with("error", "pause", "die", "write", "tell", "debug", "percieved_area", "halt", "neighbourhood_exclusive");
	
	
	public GamaFeatureProvider(IDiagramTypeProvider dtp) {
        super(dtp);
    }
    
    @Override
    public IAddFeature getAddFeature(IAddContext context) {
    	if (context.getNewObject() instanceof EWorldAgent) {
             return new AddWorldFeature(this);
        } else if (context.getNewObject() instanceof ESpecies) {
            return new AddSpeciesFeature(this);
        } else if (context.getNewObject() instanceof EAction) {
            return new AddActionFeature(this);
        } else if (context.getNewObject() instanceof EReflex) {
            return new AddReflexFeature(this);
        } else if (context.getNewObject() instanceof EAspect) {
            return new AddAspectFeature(this);
        } else if (context.getNewObject() instanceof EGUIExperiment) {
            return new AddGuiExperimentFeature(this);
        } else if (context.getNewObject() instanceof EBatchExperiment) {
            return new AddBatchExperimentFeature(this);
        } else if (context.getNewObject() instanceof EDisplay) {
            return new AddDisplayFeature(this);
        } else if (context.getNewObject() instanceof ESubSpeciesLink) {
            return new AddSubSpecieLinkFeature(this);
        } else if (context.getNewObject() instanceof EActionLink) {
            return new AddActionLinkFeature(this);
        } else if (context.getNewObject() instanceof EReflexLink) {
            return new AddReflexLinkFeature(this);
        } else if (context.getNewObject() instanceof EAspectLink) {
            return new AddAspectLinkFeature(this);
        } else if (context.getNewObject() instanceof EDisplayLink) {
            return new AddDisplayLinkFeature(this);
        } else if (context.getNewObject() instanceof EExperimentLink) {
            return new AddEExperimentLinkFeature(this);
        } else if (context.getNewObject() instanceof EInheritLink) {
        	return new AddInheritingLinkFeature(this);
        } else if (context.getNewObject() instanceof EGraphLink) {
        	return new AddGraphLinkFeature(this);
        }
    	return super.getAddFeature(context);
    }
    
    @Override
    public ICreateFeature[] getCreateFeatures() {
    //	init();
    	return new ICreateFeature[] { };
    	 
    }
    
    public void init() {
    	final Diagram diagram = getDiagramTypeProvider().getDiagram();
    	
    	if (diagram.getChildren().isEmpty()) {
    		 TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(diagram);
         	domain.getCommandStack().execute(new RecordingCommand(domain) {
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
         			 
         			 PictogramElement worldPE = addIfPossible(new AddContext(ac, newClass));
         			 if ("skeleton".equals(typeOfModel)) {
         				initSkeleton(newClass, diagram);
         			 } else if ("example".equals(typeOfModel)) {
         				initSkeleton(newClass, diagram);
         			 } else  if ("custom".equals(typeOfModel)) {
          				initCustom(newClass, worldPE, diagram);
         			 }
         	     }
         	  });
    	}
    }
    
    @SuppressWarnings("restriction")
	public void initSkeleton(EWorldAgent world, Diagram diagram) {
    		final CreateGuiExperimentLinkFeature cXp= new CreateGuiExperimentLinkFeature(this);
    		final CreateDisplayLinkFeature cDisp= new CreateDisplayLinkFeature(this);
    		PictogramElement worldPe = getPictogramElementForBusinessObject(world);
    		
    		CreateConnectionContext context = new CreateConnectionContext();
    		context.setSourcePictogramElement(worldPe);
			context.setSourceAnchor(Graphiti.getPeService().getChopboxAnchor((AnchorContainer) worldPe));
			//context.setSourceLocation(new LocationImpl(100,50));
			context.setTargetLocation(new LocationImpl(600,50));
			Connection link = cXp.create(context, false);
			AddConnectionContext cont = new AddConnectionContext(null, null);
			cont.setNewObject(link);
			addIfPossible(cont);
			
			CreateConnectionContext contextDisp = new CreateConnectionContext();
			PictogramElement xp = null;
			List<Shape> contents = diagram.getChildren();
	        for (Shape obj : contents) {
	        	Object bo = getBusinessObjectForPictogramElement(obj);
            	if (bo instanceof EExperiment) {
            		xp = obj;
            		break;
            	}
	           
	        }
			contextDisp.setSourcePictogramElement(xp);
			contextDisp.setSourceAnchor(Graphiti.getPeService().getChopboxAnchor((AnchorContainer) xp));
			//contextDisp.setSourceLocation(new LocationImpl(500,50));
			contextDisp.setTargetLocation(new LocationImpl(800,50));
			Connection linkDisp = cDisp.create(contextDisp, false);
			AddConnectionContext contDisp = new AddConnectionContext(null, null);
			contDisp.setNewObject(linkDisp);
			addIfPossible(contDisp);
    }
    
    public void addVariable(IVariable var, ESpecies target) {
    	if (built_in_variables.contains(var.getName()))
			 return;
		 EVariable eVar =  gama.GamaFactory.eINSTANCE.createEVariable();
		 eVar.setType(var.getType().toString());
		 eVar.setName(var.getName());
		 if (var.hasFacet(IKeyword.INIT))
			 eVar.setInit(var.getFacet(IKeyword.INIT).toGaml());
		 if (var.hasFacet(IKeyword.UPDATE))
			 eVar.setUpdate(var.getFacet(IKeyword.UPDATE).toGaml());
		 if (var.hasFacet(IKeyword.MIN))
			 eVar.setMin(var.getFacet(IKeyword.MIN).toGaml());
		 if (var.hasFacet(IKeyword.MAX))
			 eVar.setMax(var.getFacet(IKeyword.MAX).toGaml());
		 if (var.hasFacet(IKeyword.FUNCTION))
			 eVar.setFunction(var.getFacet(IKeyword.FUNCTION).toGaml());
		 if (var.hasFacet(IKeyword.VALUE))
			 eVar.setFunction(var.getFacet(IKeyword.VALUE).toGaml());
		 target.getVariables().add(eVar);
    }
    
    public  ESpecies createMicroSpecies(ESpecies source, PictogramElement sourceE, ISpecies species, Diagram diagram) {
		 ESpecies target = gama.GamaFactory.eINSTANCE.createESpecies();
		 diagram.eResource().getContents().add(target);
		 Collection<ISkill> skills =  ((SpeciesDescription) species.getDescription()).getSkills().values();
		 
		
		 target.setName(species.getName());
		 
		 ETopology newTopo = null;
		 if (species.isGrid()) {
			 newTopo = gama.GamaFactory.eINSTANCE.createEGridTopology();
		 } else if (species.isGraph()) {
			 newTopo = gama.GamaFactory.eINSTANCE.createEGraphTopologyNode();
		 } else {
			 newTopo = gama.GamaFactory.eINSTANCE.createEContinuousTopology();
		 }
		 
		  diagram.eResource().getContents().add(newTopo);
		 target.setTopology(newTopo);
		
		 for (IVariable var : species.getVars()) {
			 if (((Variable) var).getgSkill() == null)
				 addVariable(var, target);
		 }
		
		 CreateContext ac = new CreateContext();
			
		 ac.setLocation(0, 0);	
		 ac.setSize(0, 0);
		 ac.setTargetContainer(diagram);
			 
		 PictogramElement targetE = addIfPossible(new AddContext(ac, target));
		 for (ActionStatement action : species.getActions()) {
			 if (! built_in_actions.contains(action.getName()))
				 createAction( target,  targetE,  action,  diagram);
		 }
		 for (IStatement stat : species.getBehaviors()) {
			 if (stat instanceof ReflexStatement)
				 createReflex( target,  targetE,  (ReflexStatement) stat,  diagram);
		 }
		 for (IExecutable asp : species.getAspects()) {
			 if (asp instanceof AspectStatement)
				 createAspect( target,  targetE,  (AspectStatement) asp,  diagram);
		 }
		 ESubSpeciesLink eReference = gama.GamaFactory.eINSTANCE.createESubSpeciesLink();
		 diagram.eResource().getContents().add(eReference);
		 
		 // add connection for business object
			AddConnectionContext addContext = new AddConnectionContext(
					getAnchor(sourceE), getAnchor(targetE));
			addContext.setNewObject(eReference);
			addIfPossible(addContext);
			eReference.setMacro(source);
			eReference.setMicro(target);
			source.getMicroSpeciesLinks().add(eReference);
			target.getMacroSpeciesLinks().add(eReference);
		 return target;
	}
    
    public  EExperiment createXP(ESpecies source, PictogramElement sourceE, IExperimentSpecies xp, Diagram diagram) {
		EExperiment target = null; 
    	if (xp.isGui()) {
    		target = gama.GamaFactory.eINSTANCE.createEGUIExperiment();
    	} else {
    		target = gama.GamaFactory.eINSTANCE.createEBatchExperiment();
    	}
		 diagram.eResource().getContents().add(target);
		 target.setName(xp.getName().substring("Experiment ".length(), xp.getName().length()));
		 CreateContext ac = new CreateContext();
		
			
		 ac.setLocation(0, 0);
			
			 ac.setSize(0, 0);
			 ac.setTargetContainer(diagram);
			 PictogramElement targetE = addIfPossible(new AddContext(ac, target));
			if (xp != null && xp.isGui() && xp.getSimulationOutputs() != null) {
				for (IOutput output : ((AbstractOutputManager)xp.getSimulationOutputs()).getOutputs().values()) {
					  if (output instanceof LayeredDisplayOutput)
						  createDisplay((EGUIExperiment) target, targetE, output, diagram);
				}
			}
	
		 EExperimentLink eReference = gama.GamaFactory.eINSTANCE.createEExperimentLink();
		 diagram.eResource().getContents().add(eReference);
		 
		 // add connection for business object
			AddConnectionContext addContext = new AddConnectionContext(
					getAnchor(sourceE), getAnchor(targetE));
			addContext.setNewObject(eReference);
			addIfPossible(addContext);
			eReference.setSpecies(source);
			eReference.setExperiment(target);
			source.getExperimentLinks().add(eReference);
			target.setExperimentLink(eReference);
		 return target;
	}
    
   public  EAction createAction(ESpecies source, PictogramElement sourceE, ActionStatement action, Diagram diagram) {
		if (action == null)
			return null;
	   EAction target = gama.GamaFactory.eINSTANCE.createEAction();
    	 diagram.eResource().getContents().add(target);
		 target.setName(action.getName());
		 String gmlCode = "";	
		 if (action.getCommands() != null) {
			 for (IStatement st : action.getCommands()) {
				 gmlCode += st.toGaml() + System.getProperty("line.separator" );
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
			AddConnectionContext addContext = new AddConnectionContext(
					getAnchor(sourceE), getAnchor(targetE));
			addContext.setNewObject(eReference);
			addIfPossible(addContext);
			eReference.setSpecies(source);
			eReference.setAction(target);
			source.getActionLinks().add(eReference);
			target.getActionLinks().add(eReference);
		 return target;
	}
   
   public  EDisplay createDisplay(EGUIExperiment source, PictogramElement sourceE, IOutput display, Diagram diagram) {
		if (display == null)
			return null;
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
			AddConnectionContext addContext = new AddConnectionContext(
					getAnchor(sourceE), getAnchor(targetE));
			addContext.setNewObject(eReference);
			addIfPossible(addContext);
			eReference.setExperiment(source);
			eReference.setDisplay(target);
			source.getDisplayLinks().add(eReference);
			target.setDisplayLink(eReference);
		 return target;
	}
   
   public  EAspect createAspect(ESpecies source, PictogramElement sourceE, AspectStatement aspect, Diagram diagram) {
		if (aspect == null)
			return null;
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
			AddConnectionContext addContext = new AddConnectionContext(
					getAnchor(sourceE), getAnchor(targetE));
			addContext.setNewObject(eReference);
			addIfPossible(addContext);
			eReference.setSpecies(source);
			eReference.setAspect(target);
			source.getAspectLinks().add(eReference);
			target.getAspectLinks().add(eReference);
		 return target;
	}
   
   public  EReflex createReflex(ESpecies source, PictogramElement sourceE, ReflexStatement reflex, Diagram diagram) {
	   if (reflex == null)
			return null;
	   EReflex target = gama.GamaFactory.eINSTANCE.createEReflex();
   	 diagram.eResource().getContents().add(target);
		 target.setName(reflex.getName());
		 String gmlCode = "";	

		 if (reflex.getCommands() != null) {
			 for (IStatement st : reflex.getCommands()) {
				 gmlCode += st.toGaml() + System.getProperty("line.separator" );
			 }
		 }
		 if (reflex.hasFacet(IKeyword.WHEN))
			 target.setCondition(reflex.getFacet(IKeyword.WHEN).toGaml());
		 
		 target.setGamlCode(gmlCode);
		 CreateContext ac = new CreateContext();
			
		 ac.setLocation(0, 0);
			
			 ac.setSize(0, 0);
			 ac.setTargetContainer(diagram);
			 
		  PictogramElement targetE = addIfPossible(new AddContext(ac, target));
	
		 EReflexLink eReference = gama.GamaFactory.eINSTANCE.createEReflexLink();
		 diagram.eResource().getContents().add(eReference);
		 
		 // add connection for business object
			AddConnectionContext addContext = new AddConnectionContext(
					getAnchor(sourceE), getAnchor(targetE));
			addContext.setNewObject(eReference);
			addIfPossible(addContext);
			eReference.setSpecies(source);
			eReference.setReflex(target);
			source.getReflexLinks().add(eReference);
			target.getReflexLinks().add(eReference);
		 return target;
	}
  
    
    protected Anchor getAnchor(PictogramElement targetpe) {
		Anchor ret = null;
		if (targetpe instanceof Anchor) {
			ret = (Anchor) targetpe;
		} else if (targetpe instanceof AnchorContainer) {
			ret = Graphiti.getPeService().getChopboxAnchor((AnchorContainer) targetpe);
		}
		return ret;
	}
	
    
   	public void initCustom(EWorldAgent eWorld,  PictogramElement worldPE, Diagram diagram) {
   		for (IVariable var : gamaModel.getVars()) {
   			addVariable(var, eWorld);
   		} 
	   	 for (ActionStatement action : gamaModel.getActions()) {
			 if (! built_in_actions.contains(action.getName()))
				 createAction( eWorld,  worldPE,  action,  diagram);
		 }
	   	for (IStatement stat : gamaModel.getBehaviors()) {
			 if (stat instanceof ReflexStatement && stat.getName() != null && !stat.getName().isEmpty())
				 createReflex( eWorld,  worldPE,  (ReflexStatement) stat,  diagram);
		 }
   		buildAgent(gamaModel, eWorld, worldPE, diagram);
       	
   		   
   		LayoutDiagramFeature.execute(diagram);
    }
   	
   	public void buildAgent(ISpecies gamaSpecies, ESpecies species, PictogramElement speciesE, Diagram diagram) {
   		for (ISpecies sp : gamaSpecies.getMicroSpecies()) {
   			if (built_in_species.contains(sp.getName()))
   				continue;
   			if (sp instanceof IExperimentSpecies) { 
   				createXP(species, speciesE, (IExperimentSpecies) sp, diagram);
   			} else {
   				createMicroSpecies(species, speciesE, sp, diagram);
   			}
   			
   		}
   	}
    
 
    @Override
    public ILayoutFeature getLayoutFeature(ILayoutContext context) {
        PictogramElement pictogramElement = context.getPictogramElement();
        Object bo = getBusinessObjectForPictogramElement(pictogramElement);
        if (bo instanceof ESpecies) {
            return new LayoutESpeciesEExperimentFeature(this);
        } else if ( bo instanceof EExperiment) {
             return new LayoutEExperimentFeature(this);  
        } else  if (bo instanceof EGamaObject) {
            return new LayoutCommonFeature(this);
        }
        return super.getLayoutFeature(context);
    }
    
    @Override
    public IFeature[] getDragAndDropFeatures(IPictogramElementContext context) {
        // simply return all create connection features
        return getCreateConnectionFeatures();
    }
    
    @Override
    public ICreateConnectionFeature[] getCreateConnectionFeatures() {
        return new ICreateConnectionFeature[] {
            new CreateSubSpeciesLinkFeature (this), 
            new CreateSubGridLinkFeature(this),
            new CreateSubGraphSpeciesFeature(this),
            new CreateInheritingLinkFeature(this),
            new CreateActionLinkFeature (this),
            new CreateReflexLinkFeature (this),
            new CreateAspectLinkFeature (this),
            new CreateDisplayLinkFeature (this),
            new CreateBatchExperimentLinkFeature(this),
        	new CreateGuiExperimentLinkFeature(this)};
    }
    
    @Override
    public ICustomFeature[] getCustomFeatures(ICustomContext context) {
    	return new ICustomFeature[] { new RenameEGamaObjectFeature(this),new ModelGenerationFeature(this), new LayoutDiagramFeature(this), new ChangeColorEGamaObjectFeature(this)};
    }
    
    @Override
    public IUpdateFeature getUpdateFeature(IUpdateContext context) {
        PictogramElement pictogramElement = context.getPictogramElement();
        if (pictogramElement instanceof ContainerShape) {
            Object bo = getBusinessObjectForPictogramElement(pictogramElement);
            if (bo instanceof EGamaObject) {
                return new UpdateEGamaObjectFeature(this);
            }
        }
        return super.getUpdateFeature(context);
    } 

	public String getTypeOfModel() {
		return typeOfModel;
	}

	public void setTypeOfModel(String typeOfModel) {
		this.typeOfModel = typeOfModel;
	}

	public IModel getGamaModel() {
		return gamaModel;
	}

	public void setGamaModel(IModel gamaModel) {
		this.gamaModel = gamaModel;
	}

   
}