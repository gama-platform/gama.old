package idees.gama.features.add;

import gama.ESubSpeciesLink;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddConnectionContext;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddFeature;
import org.eclipse.graphiti.mm.GraphicsAlgorithmContainer;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.util.IColorConstant;

public class AddSubSpecieLinkFeature extends AbstractAddFeature {
 
    public AddSubSpecieLinkFeature (IFeatureProvider fp) {
        super(fp);
    }
 
    public PictogramElement add(IAddContext context) {
    	IAddConnectionContext addConContext = (IAddConnectionContext) context;
        ESubSpeciesLink addedEReference = (ESubSpeciesLink) context.getNewObject();
        IPeCreateService peCreateService = Graphiti.getPeCreateService();
       
       
        // CONNECTION WITH POLYLINE
        Connection connection = peCreateService
            .createFreeFormConnection(getDiagram());
        connection.setStart(addConContext.getSourceAnchor());
        connection.setEnd(addConContext.getTargetAnchor());
 
        IGaService gaService = Graphiti.getGaService();
        Polyline polyline = gaService.createPolyline(connection);
        polyline.setLineWidth(2);
        polyline.setForeground(manageColor(IColorConstant.BLACK));
 
        // create link and wire it
        link(connection, addedEReference);
 
        return connection;
    }
 
    public boolean canAdd(IAddContext context) {
        // return true if given business object is an EReference
        // note, that the context must be an instance of IAddConnectionContext
       if (context instanceof IAddConnectionContext
            && context.getNewObject() instanceof ESubSpeciesLink) {
    		 return true;
        }
    	return false;
    }
    
    private Polyline createArrow(GraphicsAlgorithmContainer gaContainer) {
        
        IGaService gaService = Graphiti.getGaService();
        Polyline polyline =
            gaService.createPolyline(gaContainer, new int[] { -15, 10, 0, 0, -15,
                -10 });
        polyline.setForeground(manageColor(IColorConstant.BLACK));
        polyline.setLineWidth(2);
        return polyline;
    }
}