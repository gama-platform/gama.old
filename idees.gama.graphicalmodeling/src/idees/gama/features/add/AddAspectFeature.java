package idees.gama.features.add;

import gama.EAspect;
import idees.gama.ui.image.GamaImageProvider;
import java.util.*;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddShapeFeature;
import org.eclipse.graphiti.mm.algorithms.*;
import org.eclipse.graphiti.mm.algorithms.styles.*;
import org.eclipse.graphiti.mm.pictograms.*;
import org.eclipse.graphiti.services.*;

public class AddAspectFeature extends AbstractAddShapeFeature {

	public static final int INIT_WIDTH = 150;
	public static final int INIT_HEIGHT = 50;

	private static final List<Integer> CLASS_BACKGROUND = Arrays.asList(152, 251, 152);

	public AddAspectFeature(final IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canAdd(final IAddContext context) {
		// check if user wants to add a EClass
		if ( context.getNewObject() instanceof EAspect ) {
			// check if user wants to add to a diagram
			if ( context.getTargetContainer() instanceof Diagram ) { return true; }
		}
		return false;
	}

	@Override
	public PictogramElement add(final IAddContext context) {
		EAspect addedClass = (EAspect) context.getNewObject();
		boolean error = addedClass.getHasError() != null && addedClass.getHasError();

		Diagram targetDiagram = (Diagram) context.getTargetContainer();
		IPeCreateService peCreateService = Graphiti.getPeCreateService();
		ContainerShape containerShape = peCreateService.createContainerShape(targetDiagram, true);

		int width = context.getWidth() <= 0 ? INIT_WIDTH : context.getWidth();
		int height = context.getHeight() <= 0 ? INIT_HEIGHT : context.getHeight();

		IGaService gaService = Graphiti.getGaService();

		{
			// create and set graphics algorithm
			Ellipse ellipse = gaService.createEllipse(containerShape);
			ellipse.setForeground(manageColor(error ? ColorDisplay.CLASS_FOREGROUND_ERROR
				: ColorDisplay.CLASS_FOREGROUND_OK));
			if ( addedClass.getColorPicto().isEmpty() ) {
				addedClass.getColorPicto().addAll(CLASS_BACKGROUND);
			}
			List<Integer> currentColor = addedClass.getColorPicto();
			Color color =
				gaService.manageColor(getDiagram(), currentColor.get(0), currentColor.get(1), currentColor.get(2));
			ellipse.setBackground(color);
			ellipse.setLineWidth(error ? 4 : 2);
			gaService.setLocationAndSize(ellipse, context.getX(), context.getY(), width, height);

			if ( addedClass.eResource() == null ) {
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
			Text text = gaService.createDefaultText(getDiagram(), shape, addedClass.getName());
			text.setForeground(manageColor(ColorDisplay.CLASS_TEXT_FOREGROUND));
			text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
			text.setFont(gaService.manageFont(getDiagram(), "Arial", 12, false, false));
			// text.getFont().setBold(true);
			gaService.setLocationAndSize(text, 0, 0, width, height);

			// create link and wire it
			link(shape, addedClass);
		}
		{

			Shape shape3 = peCreateService.createShape(containerShape, false);

			Image icon1 = gaService.createImage(shape3, GamaImageProvider.IMG_ASPECTLINK);
			gaService.setLocationAndSize(icon1, 100, 100, 15, 15);

			link(shape3, addedClass);

		}

		// add a chopbox anchor to the shape
		peCreateService.createChopboxAnchor(containerShape);

		// call the layout feature
		layoutPictogramElement(containerShape);
		return containerShape;
	}
}