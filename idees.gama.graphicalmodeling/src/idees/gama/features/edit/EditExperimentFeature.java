package idees.gama.features.edit;

import gama.EExperiment;
import idees.gama.ui.editFrame.EditExperimentFrame;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class EditExperimentFeature  extends EditFeature {
   
    public EditExperimentFeature(IFeatureProvider fp) {
        super(fp);
    }
 
    @Override
    public String getDescription() {
        return "Edition of an experiment";
    }
 
    @Override
    public boolean canExecute(ICustomContext context) {
        // allow rename if exactly one pictogram element
        // representing a EClass is selected
        boolean ret = false;
        PictogramElement[] pes = context.getPictogramElements();
        if (pes != null && pes.length == 1) {
            Object bo = getBusinessObjectForPictogramElement(pes[0]);
            if (bo instanceof EExperiment) {
                ret = true;
            }
        }
        return ret;
    }
 
    @Override
    public void execute(ICustomContext context) {
        PictogramElement[] pes = context.getPictogramElements();
        if (pes != null && pes.length == 1) {
            Object bo = getBusinessObjectForPictogramElement(pes[0]);
            if (bo instanceof EExperiment) {
            	EExperiment eExperiment = (EExperiment) bo; 
            	EditExperimentFrame eaf = new EditExperimentFrame(getDiagram(), getFeatureProvider(), this,eExperiment, eExperiment.getName());
            	eaf.open();
            }
        }
        this.hasDoneChanges = true;
    }
 
}