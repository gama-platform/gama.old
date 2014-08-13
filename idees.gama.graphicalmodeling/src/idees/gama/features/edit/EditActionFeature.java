package idees.gama.features.edit;

import java.util.List;

import idees.gama.ui.editFrame.EditActionFrame;
import gama.EAction;
import gama.ESpecies;

import msi.gama.util.GamaList;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;

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
            	EditActionFrame eaf = new EditActionFrame(getDiagram(), getFeatureProvider(), this,eAction, null, speciesList());
            	eaf.open();
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