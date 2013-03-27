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
import idees.gama.features.layout.LayoutEExperimentFeature;
import idees.gama.features.layout.LayoutESpeciesEExperimentFeature;
import idees.gama.features.modelgeneration.ModelGenerationFeature;
import idees.gama.features.others.RenameEGamaObjectFeature;
import idees.gama.features.others.UpdateEGamaObjectFeature;

import java.util.List;

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
         			 
         			 addIfPossible(new AddContext(ac, newClass));
         			 if ("skeleton".equals(typeOfModel)) {
         				initSkeleton(newClass, diagram);
         			 } else if ("example".equals(typeOfModel)) {
         				initSkeleton(newClass, diagram);
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
    	return new ICustomFeature[] { new RenameEGamaObjectFeature(this),new ModelGenerationFeature(this)};
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


   
}