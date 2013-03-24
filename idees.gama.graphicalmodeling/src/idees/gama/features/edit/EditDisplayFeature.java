package idees.gama.features.edit;

import gama.EDisplay;
import idees.gama.ui.editFrame.EditDisplayFrame;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class EditDisplayFeature  extends EditFeature {
   
    public EditDisplayFeature(IFeatureProvider fp) {
        super(fp);
    }
 
    @Override
    public String getDescription() {
        return "Edition of a display";
    }
 
    @Override
    public boolean canExecute(ICustomContext context) {
        // allow rename if exactly one pictogram element
        // representing a EClass is selected
        boolean ret = false;
        PictogramElement[] pes = context.getPictogramElements();
        if (pes != null && pes.length == 1) {
            Object bo = getBusinessObjectForPictogramElement(pes[0]);
            if (bo instanceof EDisplay) {
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
            if (bo instanceof EDisplay) {
            	EDisplay eDisplay = (EDisplay) bo; 
            	EditDisplayFrame eaf = new EditDisplayFrame(getDiagram(), getFeatureProvider(), this,eDisplay, null);
            	eaf.open();
            }
        }
        this.hasDoneChanges = true;
    }
 
}