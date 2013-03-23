package idees.gama.features.others;

import gama.EGamaObject;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class RenameEGamaObjectFeature extends AbstractCustomFeature {
 
    private boolean hasDoneChanges = false;
     
    public RenameEGamaObjectFeature(IFeatureProvider fp) {
        super(fp);
    }
 
    @Override
    public String getName() {
        return "Rename";
    }
 
    @Override
    public String getDescription() {
        return "Change the name of the object";
    }
 
    @Override
    public boolean canExecute(ICustomContext context) {
        // allow rename if exactly one pictogram element
        // representing a EClass is selected
        boolean ret = false;
        PictogramElement[] pes = context.getPictogramElements();
        if (pes != null && pes.length == 1) {
            Object bo = getBusinessObjectForPictogramElement(pes[0]);
            if (bo instanceof EGamaObject) {
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
            if (bo instanceof EGamaObject) {
            	EGamaObject eClass = (EGamaObject) bo;
                String currentName = eClass.getName();
                // ask user for a new class name
                String newName =idees.gama.features.ExampleUtil.askString(getName(), getDescription(),
                        currentName);
                if (newName != null && !newName.equals(currentName)) {
                    this.hasDoneChanges = true;
                    eClass.setName(newName);
                }
            }
        }
    }
 
    @Override
    public boolean hasDoneChanges() {
           return this.hasDoneChanges;
    }
}