package idees.gama.features.edit;

import gama.EAction;
import gama.ESpecies;
import idees.gama.diagram.MyGamaToolBehaviorProvider;
import idees.gama.ui.editFrame.EditActionFrame;
import idees.gama.ui.editFrame.EditFrame;

import java.util.List;

import msi.gama.util.GamaList;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;

public class EditActionFeature  extends EditFeature {
   
	 public EditActionFeature(IFeatureProvider fp, EditFrame frame, MyGamaToolBehaviorProvider tbp ) {
        super(fp, frame, tbp);
       
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
            	if (frame == null || frame.getShell() == null || frame.getShell().isDisposed() ) {
            		frame = new EditActionFrame(getDiagram(), getFeatureProvider(), this,eAction, null, speciesList());
            		frame.open();
            		tbp.getFrames().put(eAction, frame);
            	
            	} else {
          
            		frame.getShell().setFocus();
            	}
            	
            }
        }
        this.hasDoneChanges = true;
    }
    
    private List<ESpecies> speciesList (){
    	List<ESpecies> species = new GamaList<ESpecies>();
    	List<Shape> contents = getDiagram().getChildren();
        if (contents != null) {
        	 for (Shape obj : contents) {
            	Object bo = getBusinessObjectForPictogramElement(obj);
            	if (bo instanceof ESpecies) {
            		species.add((ESpecies) bo);
	            }
            }
        }
    	return species;
    }
 
}