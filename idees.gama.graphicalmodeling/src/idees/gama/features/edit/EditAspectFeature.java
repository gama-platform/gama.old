package idees.gama.features.edit;

import gama.EAspect;
import idees.gama.diagram.MyGamaToolBehaviorProvider;
import idees.gama.ui.editFrame.EditAspectFrame;
import idees.gama.ui.editFrame.EditFrame;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class EditAspectFeature  extends EditFeature {
   
    public EditAspectFeature(IFeatureProvider fp, EditFrame frame, MyGamaToolBehaviorProvider tbp ) {
        super(fp, frame, tbp);
    }
    
    @Override
    public String getDescription() {
        return "Edition of an aspect";
    }
 
    @Override
    public boolean canExecute(ICustomContext context) {
        // allow rename if exactly one pictogram element
        // representing a EClass is selected
        boolean ret = false;
        PictogramElement[] pes = context.getPictogramElements();
        if (pes != null && pes.length == 1) {
            Object bo = getBusinessObjectForPictogramElement(pes[0]);
            if (bo instanceof EAspect) {
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
            if (bo instanceof EAspect) {
            	EAspect eAspect = (EAspect) bo; 
            	if (frame == null || frame.getShell() == null || frame.getShell().isDisposed() ) {
                	frame =  new EditAspectFrame(getDiagram(), getFeatureProvider(), this,eAspect, null);
            		frame.open();
            		tbp.getFrames().put(eAspect, frame);
            	
            	} else {
            		frame.getShell().setFocus();
            	}
            }
        }
        this.hasDoneChanges = true;
    }
 
}