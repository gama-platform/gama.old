package idees.gama.features.edit;

import gama.EExperiment;
import idees.gama.diagram.MyGamaToolBehaviorProvider;
import idees.gama.ui.editFrame.EditExperimentFrame;
import idees.gama.ui.editFrame.EditFrame;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class EditExperimentFeature  extends EditFeature {
   
    public EditExperimentFeature(IFeatureProvider fp,EditFrame frame, MyGamaToolBehaviorProvider tbp) {
        super(fp,frame, tbp);
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
            	if (frame == null || frame.getShell() == null || frame.getShell().isDisposed() ) {
            		frame = new EditExperimentFrame(getDiagram(), getFeatureProvider(), this,eExperiment, eExperiment.getName());
            		frame.open();
            		tbp.getFrames().put(eExperiment, frame);
            	
            	} else {
            		frame.getShell().setFocus();
            	}
            }
        }
        this.hasDoneChanges = true;
    }
 
}