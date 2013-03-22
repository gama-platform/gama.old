package idees.gama.features.edit;

import gama.EAspect;
import idees.gama.ui.editFrame.EditAspectFrame;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class EditAspectFeature  extends EditFeature {
   
    public EditAspectFeature(IFeatureProvider fp) {
        super(fp);
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
            	EditAspectFrame eaf = new EditAspectFrame(getDiagram(), getFeatureProvider(), this,eAspect, null);
            	eaf.open();
            }
        }
        this.hasDoneChanges = true;
    }
 
}