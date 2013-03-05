package idees.gama.features.modelgeneration;

import gama.ESpecies;

import java.util.List;

import msi.gama.util.GamaList;
import msi.gaml.factories.ModelStructure;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.Shape;

public class ModelGenerationFeature extends AbstractCustomFeature {
 
    private boolean hasDoneChanges = false;
     
    public ModelGenerationFeature(IFeatureProvider fp) {
        super(fp);
       
 
    }
 
    @Override
    public String getName() {
        return "Generate Gaml model";
    }
 
    @Override
    public String getDescription() {
        return "Generate Gaml model from diagram";
    }
 
    @Override
    public boolean canExecute(ICustomContext context) {
      return true;
    }
 
    @Override
    public void execute(ICustomContext context) {
       
    	List<Shape> contents = getDiagram().getChildren();
        System.out.println("getDiagram().getChildren()" + getDiagram().getChildren());
        if (contents != null) {
        	ESpecies worldAgent = null;
            for (Shape obj : contents) {
            	Object bo = getBusinessObjectForPictogramElement(obj);
            	 System.out.println("obj : " + obj + " bo : " + bo);
            	if (bo instanceof ESpecies) {
            		ESpecies eSpecies = (ESpecies) bo;
	                System.out.println("eClass" + eSpecies);
	                if (eSpecies.getIncomingLinks() == null || eSpecies.getIncomingLinks().isEmpty()) {
	                	worldAgent = eSpecies;
	                	//break;
	                }
	            }
            }
            
        }
    }
 
    @Override
    public boolean hasDoneChanges() {
           return this.hasDoneChanges;
    }
}
 