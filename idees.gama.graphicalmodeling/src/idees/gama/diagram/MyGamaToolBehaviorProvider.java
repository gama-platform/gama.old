package idees.gama.diagram;

import idees.gama.features.others.RenameEGamaObjectFeature;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.context.IDoubleClickContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.tb.DefaultToolBehaviorProvider;

public class MyGamaToolBehaviorProvider extends DefaultToolBehaviorProvider{

	public MyGamaToolBehaviorProvider(IDiagramTypeProvider diagramTypeProvider) {
		super(diagramTypeProvider);
	}
	
	 @Override
	    public ICustomFeature getDoubleClickFeature(IDoubleClickContext context) {
	        ICustomFeature customFeature =
	            new RenameEGamaObjectFeature(getFeatureProvider());
	        // canExecute() tests especially if the context contains a EClass
	        if (customFeature.canExecute(context)) {
	            return customFeature;
	        }
	 
	        return super.getDoubleClickFeature(context);
	    }

}
