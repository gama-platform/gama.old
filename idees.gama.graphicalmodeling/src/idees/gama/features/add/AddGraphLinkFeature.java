package idees.gama.features.add;



import gama.EGraphLink;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddConnectionContext;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddFeature;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.util.IColorConstant;

public class AddGraphLinkFeature extends AbstractAddFeature {
 
    public AddGraphLinkFeature (IFeatureProvider fp) {
        super(fp);
    }
 
    public PictogramElement add(IAddContext context) {
    	IAddConnectionContext addConContext = (IAddConnectionContext) context;
        EGraphLink addedEReference = (EGraphLink) context.getNewObject();
        IPeCreateService peCreateService = Graphiti.getPeCreateService();
       
       
        // CONNECTION WITH POLYLINE
        Connection connection = peCreateService
            .createFreeFormConnection(getDiagram());
        connection.setStart(addConContext.getSourceAnchor());
        connection.setEnd(addConContext.getTargetAnchor());
 
        IGaService gaService = Graphiti.getGaService();
        Polyline polyline = gaService.createPolyline(connection);
        polyline.setLineWidth(4);
        polyline.setForeground(manageColor(IColorConstant.GREEN));
 
        // create link and wire it
        link(connection, addedEReference);
 
        // add dynamic text decorator for the association name
        ConnectionDecorator textDecorator =
            peCreateService.createConnectionDecorator(connection, true,
            0.5, true);
        Text text = gaService.createText(textDecorator);
        		//createDefaultText(textDecorator);
        text.setForeground(manageColor(IColorConstant.BLACK));
        gaService.setLocation(text, 10, 0);
        text.setValue("edge/node");
 
        return connection;
    }
 
    public boolean canAdd(IAddContext context) {
        // return true if given business object is an EReference
        // note, that the context must be an instance of IAddConnectionContext
       if (context instanceof IAddConnectionContext
            && context.getNewObject() instanceof EGraphLink) {
    		 return true;
        }
    	return false;
    }
}