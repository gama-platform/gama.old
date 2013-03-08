package idees.gama.diagram;

import gama.ESpecies;
import idees.gama.features.editSpecies.EditSpeciesFeature;
import idees.gama.features.others.RenameEGamaObjectFeature;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.context.IDoubleClickContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.tb.DefaultToolBehaviorProvider;

public class MyGamaToolBehaviorProvider extends DefaultToolBehaviorProvider{

	public MyGamaToolBehaviorProvider(IDiagramTypeProvider diagramTypeProvider) {
		super(diagramTypeProvider);
	}
	
	 @Override
	    public ICustomFeature getDoubleClickFeature(IDoubleClickContext context) {
	        ICustomFeature customFeature = null;
	        PictogramElement[] pes = context.getPictogramElements();
	        if (pes != null && pes.length == 1) {
	            Object bo = getDiagramTypeProvider().getFeatureProvider().getBusinessObjectForPictogramElement(pes[0]);
	       
		        if (bo instanceof ESpecies ) {
		        	customFeature = new EditSpeciesFeature(getFeatureProvider());
		        } else {
		        	customFeature = new RenameEGamaObjectFeature(getFeatureProvider());
		        }
		        // canExecute() tests especially if the context contains a EClass
		        if (customFeature != null && customFeature.canExecute(context)) {
		            return customFeature;
		        }
	        }
	 
	        return super.getDoubleClickFeature(context);
	    }

}
