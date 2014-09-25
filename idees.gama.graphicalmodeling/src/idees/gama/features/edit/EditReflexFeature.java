package idees.gama.features.edit;

import gama.EReflex;
import idees.gama.diagram.MyGamaToolBehaviorProvider;
import idees.gama.ui.editFrame.EditFrame;
import idees.gama.ui.editFrame.EditReflexFrame;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class EditReflexFeature  extends EditFeature {
   
    public EditReflexFeature(IFeatureProvider fp, EditFrame frame, MyGamaToolBehaviorProvider tbp ) {
        super(fp, frame, tbp);
       
    }
 
    @Override
    public String getDescription() {
        return "Edition of a reflex";
    }
 
    @Override
    public boolean canExecute(ICustomContext context) {
        // allow rename if exactly one pictogram element
        // representing a EClass is selected
        boolean ret = false;
        PictogramElement[] pes = context.getPictogramElements();
        if (pes != null && pes.length == 1) {
            Object bo = getBusinessObjectForPictogramElement(pes[0]);
            if (bo instanceof EReflex) {
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
            if (bo instanceof EReflex) {
            	EReflex eReflex = (EReflex) bo;
            	if (frame == null || frame.getShell() == null || frame.getShell().isDisposed() ) {
            		frame =  new EditReflexFrame(getDiagram(), getFeatureProvider(), this,eReflex);
            		frame.open();
            		tbp.getFrames().put(eReflex, frame);
            	
            	} else {
            		frame.getShell().setFocus();
            	}
            }
        }
        this.hasDoneChanges = true;
    }
 
}