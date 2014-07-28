package idees.gama.features.create;

import idees.gama.diagram.GamaDiagramEditor;
import idees.gama.features.add.AddDisplayFeature;
import idees.gama.features.modelgeneration.ModelGenerator;
import idees.gama.ui.image.GamaImageProvider;
import gama.EDisplay;
import gama.EDisplayLink;
import gama.EGUIExperiment;

import idees.gama.features.ExampleUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.features.context.impl.CreateContext;
import org.eclipse.graphiti.features.impl.AbstractCreateConnectionFeature;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;

public class CreateDisplayLinkFeature extends AbstractCreateConnectionFeature {

	  private static final String TITLE = "Create display";
	  
	  private static final String USER_QUESTION = "Enter new display name";
	 
	  public CreateDisplayLinkFeature(IFeatureProvider fp) {
		// provide name and description for the UI, e.g. the palette
		super(fp, "has the display", "Create display link");
	}

	public boolean canCreate(ICreateConnectionContext context) {
		EGUIExperiment source = getEExperiment(context.getSourceAnchor());
		if (source != null) {
			return true;
		}
		return false;
	}

	public boolean canStartConnection(ICreateConnectionContext context) {
		if (getEExperiment(context.getSourceAnchor()) != null) {
			return true;
		}
		return false;
	}

	private EDisplay createEDisplay(ICreateConnectionContext context, boolean askName) {
		String newDisplayName = "my_display";
		if (askName) {
			newDisplayName = ExampleUtil.askString(TITLE, USER_QUESTION, "my_display");
		    if (newDisplayName == null || newDisplayName.trim().length() == 0) {
		    	return null;
		    } 
		}
	    EDisplay newDisplay = gama.GamaFactory.eINSTANCE.createEDisplay();
	    newDisplay.setError("");
	    newDisplay.setHasError(false);
		this.getDiagram().eResource().getContents().add(newDisplay);
		newDisplay.setName(newDisplayName);
		CreateContext ac = new CreateContext();
		ac.setLocation(context.getTargetLocation().getX(), context.getTargetLocation().getY());
		ac.setSize(0, 0);
		ac.setTargetContainer(getDiagram());
		return newDisplay;
	}
	
	private PictogramElement addEDisplay(ICreateConnectionContext context, EDisplay Display) {
		CreateContext cc = new CreateContext();
		cc.setLocation(context.getTargetLocation().getX() - (int)(AddDisplayFeature.INIT_WIDTH/2.0), context.getTargetLocation().getY() - (int)(AddDisplayFeature.INIT_HEIGHT/2.0));
		cc.setSize(0, 0);
		cc.setTargetContainer(getDiagram());
		return getFeatureProvider().addIfPossible(new AddContext(cc, Display));
	}
	
	public Connection create(ICreateConnectionContext context) {
		return create(context, true);
	}
	
	public Connection create(ICreateConnectionContext context, boolean askName) {
		Connection newConnection = null;
		EGUIExperiment source = getEExperiment(context.getSourceAnchor());
		
		EDisplay target = createEDisplay(context, askName);
		if (source != null && target != null) {
			PictogramElement targetpe = addEDisplay(context, target);
			// create new business object
			EDisplayLink eReference = createEReference(source, target);
			//eReference.setModel(source.getModel());
			getDiagram().eResource().getContents().add(eReference);
			// add connection for business object
			AddConnectionContext addContext = new AddConnectionContext(
					context.getSourceAnchor(), getAnchor(targetpe));
			addContext.setNewObject(eReference);
			newConnection = (Connection) getFeatureProvider().addIfPossible(
					addContext);
			eReference.setExperiment(source);
			eReference.setDisplay(target);
			source.getDisplayLinks().add(eReference);
			target.setDisplayLink(eReference);
		}
		GamaDiagramEditor diagramEditor = ((GamaDiagramEditor)getFeatureProvider().getDiagramTypeProvider().getDiagramEditor());
		diagramEditor.addEOject(target);
		
		return newConnection;
	}

	
	private EGUIExperiment getEExperiment(Anchor anchor) {
		if (anchor != null) {
			Object object = getBusinessObjectForPictogramElement(anchor
					.getParent());
			if (object instanceof EGUIExperiment) {
				return (EGUIExperiment) object;
			}
		}
		return null;
	}
	
	protected Anchor getAnchor(PictogramElement targetpe) {
		Anchor ret = null;
		if (targetpe instanceof Anchor) {
			ret = (Anchor) targetpe;
		} else if (targetpe instanceof AnchorContainer) {
			ret = Graphiti.getPeService().getChopboxAnchor((AnchorContainer) targetpe);
		}
		return ret;
	}

	
	private EDisplayLink createEReference(EGUIExperiment source, EDisplay target) {
		EDisplayLink eReference = gama.GamaFactory.eINSTANCE.createEDisplayLink();
		eReference.setSource(source);
		eReference.setTarget(target);
		return eReference;
	}

	@Override
	public String getCreateImageId() {
		return GamaImageProvider.IMG_DISPLAYLINK;
	}
	
	public void execute(IContext context) {
		super.execute(context);
		ModelGenerator.modelValidation(getFeatureProvider(), getDiagram());
	}
}
