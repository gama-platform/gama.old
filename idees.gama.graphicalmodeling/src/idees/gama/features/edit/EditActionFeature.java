package idees.gama.features.edit;

import idees.gama.ui.editFrame.EditActionFrame;
import gama.EAction;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class EditActionFeature  extends EditFeature {
   
    public EditActionFeature(IFeatureProvider fp) {
        super(fp);
    }
 
    @Override
    public String getDescription() {
        return "Edition of an action";
    }
 
    @Override
    public boolean canExecute(ICustomContext context) {
        // allow rename if exactly one pictogram element
        // representing a EClass is selected
        boolean ret = false;
        PictogramElement[] pes = context.getPictogramElements();
        if (pes != null && pes.length == 1) {
            Object bo = getBusinessObjectForPictogramElement(pes[0]);
            if (bo instanceof EAction) {
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
            if (bo instanceof EAction) {
            	EAction eAction = (EAction) bo; 
            	EditActionFrame eaf = new EditActionFrame(getDiagram(), getFeatureProvider(), this,eAction, null);
            	eaf.open();
            }
        }
        this.hasDoneChanges = true;
    }
 
}