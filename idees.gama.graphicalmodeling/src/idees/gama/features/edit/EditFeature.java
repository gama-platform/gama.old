package idees.gama.features.edit;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;

public abstract class EditFeature extends AbstractCustomFeature {
 
    public boolean hasDoneChanges = false;
     
    public EditFeature(IFeatureProvider fp) {
        super(fp);
    }
 
    @Override
    public String getName() {
        return "Edit";
    }
 
 
    @Override
    public boolean hasDoneChanges() {
           return this.hasDoneChanges;
    }
}