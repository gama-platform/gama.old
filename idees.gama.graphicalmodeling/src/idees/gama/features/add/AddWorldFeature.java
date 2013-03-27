package idees.gama.features.add;

import idees.gama.ui.image.GamaImageProvider;
import gama.EVariable;
import gama.EWorldAgent;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddShapeFeature;
import org.eclipse.graphiti.mm.algorithms.Image;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
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

public class AddWorldFeature extends AbstractAddShapeFeature {
 
	public static final int INIT_WIDTH = 150;
	public static final int INIT_HEIGHT = 75;
	
	private static final IColorConstant SPECIES_TEXT_FOREGROUND =
        new ColorConstant(0, 0, 0);
 
    private static final IColorConstant SPECIES_FOREGROUND =
        new ColorConstant(205,173,0);

    private static final IColorConstant SPECIES_BACKGROUND =
    	new ColorConstant(255,255,210);
 
    public AddWorldFeature(IFeatureProvider fp) {
        super(fp);
    }
 
    public boolean canAdd(IAddContext context) {
        // check if user wants to add a EClass
        if (context.getNewObject() instanceof EWorldAgent) {
            // check if user wants to add to a diagram
            if (context.getTargetContainer() instanceof Diagram) {
                return true;
            }
        }
        return false;
    }
 
    public PictogramElement add(IAddContext context) {
    	EWorldAgent addedClass = (EWorldAgent) context.getNewObject();
         Diagram targetDiagram = (Diagram) context.getTargetContainer();
  
         // CONTAINER SHAPE WITH ROUNDED RECTANGLE
         IPeCreateService peCreateService = Graphiti.getPeCreateService();
         ContainerShape containerShape =
             peCreateService.createContainerShape(targetDiagram, true);
  
         // check whether the context has a size (e.g. from a create feature)
         // otherwise define a default size for the shape
         int width = context.getWidth() <= 0 ? INIT_WIDTH : context.getWidth();
 		 int height = context.getHeight() <= 0 ? INIT_HEIGHT : context.getHeight();

        IGaService gaService = Graphiti.getGaService();
 
        {
            // create and set graphics algorithm
            RoundedRectangle roundedRectangle =
                gaService.createRoundedRectangle(containerShape, 5, 5);
            roundedRectangle.setForeground(manageColor(SPECIES_FOREGROUND));
            roundedRectangle.setBackground(manageColor(SPECIES_BACKGROUND));
            roundedRectangle.setLineWidth(2);
            gaService.setLocationAndSize(roundedRectangle,
                context.getX(), context.getY(), width, height);
 
            // if added Class has no resource we add it to the resource 
            // of the diagram
            // in a real scenario the business model would have its own resource
            if (addedClass.eResource() == null) {
                     getDiagram().eResource().getContents().add(addedClass);
            }
            // create link and wire it
            link(containerShape, addedClass);
        }
 
        // SHAPE WITH LINE
        {
            // create shape for line
            Shape shape = peCreateService.createShape(containerShape, false);
 
            // create and set graphics algorithm
            Polyline polyline =
                gaService.createPolyline(shape, new int[] { 0, 20, width, 20 });
            polyline.setForeground(manageColor(SPECIES_FOREGROUND));
            polyline.setLineWidth(2);
        }
 
        // SHAPE WITH TEXT
        {
            // create shape for text
            Shape shape = peCreateService.createShape(containerShape, false);
 
            // create and set text graphics algorithm
            Text text = gaService.createDefaultText(getDiagram(), shape,
                        addedClass.getName());
            text.setForeground(manageColor(SPECIES_TEXT_FOREGROUND));
            text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
            text.setVerticalAlignment(Orientation.ALIGNMENT_CENTER);
            text.getFont().setBold(true);
            text.getFont().setSize(14);
           
            gaService.setLocationAndSize(text, 0, 0, width, 20);
 
            // create link and wire it
            link(shape, addedClass);
        }
        {
            // create shape for text
            Shape shape2 = peCreateService.createShape(containerShape, false);
 
            // create and set text graphics algorithm
            String variables = "";
            for (EVariable var:addedClass.getVariables() ) {
            	variables += (var.getType().isEmpty() ? "var" : var.getType()) + " " + var.getName()+ "\n";
            }
            Text text2 = gaService.createDefaultText(getDiagram(), shape2,
            		variables);
            text2.setForeground(manageColor(SPECIES_TEXT_FOREGROUND));
            text2.setHorizontalAlignment(Orientation.ALIGNMENT_LEFT);
           // text2.setVerticalAlignment(Orientation.);
            text2.getFont().setSize(14);
            gaService.setLocationAndSize(text2, 5, 25, width, 1 + addedClass.getVariables().size() * 20);
 
            // create link and wire it
            link(shape2, addedClass);
        }
        {
        	
            Shape shape3 = peCreateService.createShape(containerShape, false);
            
            Image icon1= gaService.createImage(shape3, GamaImageProvider.IMG_SPECIESCONTINUOUSTOPO);
            gaService.setLocationAndSize(icon1,
                    20 -width/2, 3, 30, 15);
           
            
            link(shape3, addedClass);
            
        }
        // add a chopbox anchor to the shape
        peCreateService.createChopboxAnchor(containerShape);
  
   
        // call the layout feature
        layoutPictogramElement(containerShape);
        return containerShape;
    }
}