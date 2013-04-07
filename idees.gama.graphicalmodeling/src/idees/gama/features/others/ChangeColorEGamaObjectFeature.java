package idees.gama.features.others;

import gama.EGamaObject;

import java.util.List;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.algorithms.styles.Color;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;

public class ChangeColorEGamaObjectFeature extends AbstractCustomFeature {
 
	private boolean hasDoneChanges = false;
	   
    public ChangeColorEGamaObjectFeature(IFeatureProvider fp) {
        super(fp);
    }
 
  
    @Override
    public String getName() {
        return "Change the color";
    }

    @Override
    public String getDescription() {
        return "Change the color";
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
            	  
            	IGaService gaService = Graphiti.getGaService();
            	List<Integer> currentColor = eClass.getColorPicto();
                Color oldColor = gaService.manageColor(getDiagram(), currentColor.get(0), currentColor.get(1), currentColor.get(2));
                // ask user for a new class name
                Color newColor =idees.gama.features.ExampleUtil.editColor(oldColor);
                if (newColor != null && !newColor.equals(oldColor)) {
                    eClass.getColorPicto().clear();
                    eClass.getColorPicto().add(newColor.getRed());
                    eClass.getColorPicto().add(newColor.getGreen());
                    eClass.getColorPicto().add(newColor.getBlue());
                    this.hasDoneChanges = true;
                    
                }
                 layoutPictogramElement(pes[0]);
                
            }
        }
    }
 
    @Override
    public boolean hasDoneChanges() {
           return this.hasDoneChanges;
    }
}