package idees.gama.features.modelgeneration;

import gama.ESpecies;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.features.context.impl.CreateContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;

public class InitModelFeature extends AbstractCustomFeature {
 
    private boolean hasDoneChanges = false;
     
    public InitModelFeature(IFeatureProvider fp) {
        super(fp);
       
 
    }
 
    @Override
    public String getName() {
        return "Initiliaze model";
    }
 
    @Override
    public String getDescription() {
        return "Initialiez model";
    }
 
    @Override
    public boolean canExecute(ICustomContext context) {
      return true;
    }
 
    @Override
    public void execute(ICustomContext context) {
       
    	ESpecies newClass = gama.GamaFactory.eINSTANCE.createESpecies();
		this.getDiagram().eResource().getContents().add(newClass);
		 newClass.setName("World");
		 
		 CreateContext ac = new CreateContext();
		 ac.setLocation(context.getX(), context.getY());
		 ac.setSize(0, 0);
		 ac.setTargetContainer(getDiagram());
		 
		 getFeatureProvider().addIfPossible(new AddContext(ac, newClass));
	 
    }
 
    @Override
    public boolean hasDoneChanges() {
           return this.hasDoneChanges;
    }
}
 