package idees.gama.diagram;


import gama.EAction;
import gama.EActionLink;
import gama.EAspect;
import gama.EAspectLink;
import gama.EBatchExperiment;
import gama.EDisplay;
import gama.EDisplayLink;
import gama.EExperiment;
import gama.EGUIExperiment;
import gama.EGamaObject;
import gama.EGrid;
import gama.EReflex;
import gama.EReflexLink;
import gama.ESpecies;
import gama.ESubSpeciesLink;
import idees.gama.features.add.AddActionFeature;
import idees.gama.features.add.AddActionLinkFeature;
import idees.gama.features.add.AddAspectFeature;
import idees.gama.features.add.AddAspectLinkFeature;
import idees.gama.features.add.AddBatchExperimentFeature;
import idees.gama.features.add.AddDisplayFeature;
import idees.gama.features.add.AddDisplayLinkFeature;
import idees.gama.features.add.AddGridFeature;
import idees.gama.features.add.AddGuiExperimentFeature;
import idees.gama.features.add.AddReflexFeature;
import idees.gama.features.add.AddReflexLinkFeature;
import idees.gama.features.add.AddSpeciesFeature;
import idees.gama.features.add.AddSubSpecieLinkFeature;
import idees.gama.features.create.CreateActionLinkFeature;
import idees.gama.features.create.CreateAspectLinkFeature;
import idees.gama.features.create.CreateBatchExperimentFeature;
import idees.gama.features.create.CreateDisplayLinkFeature;
import idees.gama.features.create.CreateGuiExperimentFeature;
import idees.gama.features.create.CreateReflexLinkFeature;
import idees.gama.features.create.CreateSubGridLinkFeature;
import idees.gama.features.create.CreateSubSpeciesLinkFeature;
import idees.gama.features.layout.LayoutCommonFeature;
import idees.gama.features.layout.LayoutESpeciesEExperimentFeature;
import idees.gama.features.modelgeneration.InitModelFeature;
import idees.gama.features.modelgeneration.ModelGenerationFeature;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeature;
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.features.DefaultFeatureProvider;

public class GamaFeatureProvider extends DefaultFeatureProvider {
 
	 public GamaFeatureProvider(IDiagramTypeProvider dtp) {
        super(dtp);
    }
    
    @Override
    public IAddFeature getAddFeature(IAddContext context) {
    	if (context.getNewObject() instanceof EGrid) {
            return new AddGridFeature(this);
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
        }
        return super.getAddFeature(context);
    }
    
    @Override
    public ICreateFeature[] getCreateFeatures() {
    	return new ICreateFeature[] { 
        		new CreateGuiExperimentFeature(this),
        		new CreateBatchExperimentFeature(this)
        		};
    }
    
    
    
 
    @Override
    public ILayoutFeature getLayoutFeature(ILayoutContext context) {
        PictogramElement pictogramElement = context.getPictogramElement();
        Object bo = getBusinessObjectForPictogramElement(pictogramElement);
        if (bo instanceof ESpecies || bo instanceof EExperiment) {
            return new LayoutESpeciesEExperimentFeature(this);
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
            new CreateActionLinkFeature (this),
            new CreateReflexLinkFeature (this),
            new CreateAspectLinkFeature (this),
            new CreateDisplayLinkFeature (this)};
    }
    
    @Override
    public ICustomFeature[] getCustomFeatures(ICustomContext context) {
    	return new ICustomFeature[] { new ModelGenerationFeature(this),new InitModelFeature(this) };
    }
   
}