package idees.gama.features.edit;

import idees.gama.diagram.MyGamaToolBehaviorProvider;
import idees.gama.ui.editFrame.EditFrame;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;

public abstract class EditFeature extends AbstractCustomFeature {
 
	protected EditFrame frame;
	MyGamaToolBehaviorProvider tbp;
   
    public boolean hasDoneChanges = false;
     
    public EditFeature(IFeatureProvider fp,EditFrame frame, MyGamaToolBehaviorProvider tbp) {
        super(fp);
        this.frame = frame;
        this.tbp = tbp;
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