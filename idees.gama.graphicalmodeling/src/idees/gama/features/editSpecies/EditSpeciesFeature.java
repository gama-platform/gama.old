package idees.gama.features.editSpecies;

import java.util.List;

import gama.ESpecies;

import msi.gama.util.GamaList;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;

public class EditSpeciesFeature  extends AbstractCustomFeature {
 
    public boolean hasDoneChanges = false;
     
    public EditSpeciesFeature(IFeatureProvider fp) {
        super(fp);
    }
 
    @Override
    public String getName() {
        return "Edit";
    }
 
    @Override
    public String getDescription() {
        return "Edition of a species";
    }
 
    @Override
    public boolean canExecute(ICustomContext context) {
        // allow rename if exactly one pictogram element
        // representing a EClass is selected
        boolean ret = false;
        PictogramElement[] pes = context.getPictogramElements();
        if (pes != null && pes.length == 1) {
            Object bo = getBusinessObjectForPictogramElement(pes[0]);
            if (bo instanceof ESpecies) {
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
            if (bo instanceof ESpecies) {
            	ESpecies eSpecies = (ESpecies) bo;
            	EditSpeciesFrame esf = new EditSpeciesFrame(getDiagram(), getFeatureProvider(), this,eSpecies, speciesList());
            	esf.open();
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
 
    @Override
    public boolean hasDoneChanges() {
           return this.hasDoneChanges;
    }
}