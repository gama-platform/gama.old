package idees.gama.features.add;


import idees.gama.ui.image.GamaImageProvider;
import gama.EAction;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddShapeFeature;
import org.eclipse.graphiti.mm.algorithms.Ellipse;
import org.eclipse.graphiti.mm.algorithms.Image;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.util.ColorConstant;
import org.eclipse.graphiti.util.IColorConstant;

public class AddActionFeature extends AbstractAddShapeFeature {
 
	public static final int INIT_WIDTH = 120;
	public static final int INIT_HEIGHT = 50;
	
    private static final IColorConstant CLASS_TEXT_FOREGROUND =
        new ColorConstant(0,0,0);
 
    private static final IColorConstant CLASS_FOREGROUND =
        new ColorConstant(255,99,71);
 
    private static final IColorConstant CLASS_BACKGROUND =
        new ColorConstant(255, 204, 153);
 
    public AddActionFeature(IFeatureProvider fp) {
        super(fp);
    }
 
    public boolean canAdd(IAddContext context) {
        // check if user wants to add a EClass
        if (context.getNewObject() instanceof EAction) {
            // check if user wants to add to a diagram
            if (context.getTargetContainer() instanceof Diagram) {
                return true;
            }
        }
        return false;
    }
 
    public PictogramElement add(IAddContext context) {
    	EAction addedClass = (EAction) context.getNewObject();
         Diagram targetDiagram = (Diagram) context.getTargetContainer();
         IPeCreateService peCreateService = Graphiti.getPeCreateService();
         ContainerShape containerShape =
             peCreateService.createContainerShape(targetDiagram, true);
  
         int width = context.getWidth() <= 0 ? INIT_WIDTH : context.getWidth();
         int height = context.getHeight() <= 0 ? INIT_HEIGHT : context.getHeight();
  
        IGaService gaService = Graphiti.getGaService();
 
        {
            // create and set graphics algorithm
           Ellipse ellipse =
                gaService.createEllipse(containerShape);
	           ellipse.setForeground(manageColor(CLASS_FOREGROUND));
	           ellipse.setBackground(manageColor(CLASS_BACKGROUND));
	           ellipse.setLineWidth(2);
	           gaService.setLocationAndSize(ellipse,
                context.getX(), context.getY(), width, height);
            
            if (addedClass.eResource() == null) {
                     getDiagram().eResource().getContents().add(addedClass);
            }
            // create link and wire it
            link(containerShape, addedClass);
        }
 
        // SHAPE WITH TEXT
        {
            // create shape for text
            Shape shape = peCreateService.createShape(containerShape, false);
 
            // create and set text graphics algorithm
            Text text = gaService.createDefaultText(getDiagram(), shape,
                        addedClass.getName());
            text.setForeground(manageColor(CLASS_TEXT_FOREGROUND));
            text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
            text.getFont().setSize(14);
           // text.getFont().setBold(true);
            gaService.setLocationAndSize(text, 0, 0, width, height);
 
            // create link and wire it
            link(shape, addedClass);
        }
        {
        	
            Shape shape3 = peCreateService.createShape(containerShape, false);
            
            Image icon1= gaService.createImage(shape3, GamaImageProvider.IMG_ACTIONLINK);
            gaService.setLocationAndSize(icon1,
                    100, 100, 15, 15);
            
            link(shape3, addedClass);
            
        }
        
        // add a chopbox anchor to the shape
        peCreateService.createChopboxAnchor(containerShape);
  
   
        // call the layout feature
        layoutPictogramElement(containerShape);
        return containerShape;
    }
}