package idees.gama.features.create;


import gama.EBatchExperiment;
import gama.EExperiment;
import gama.EExperimentLink;
import gama.EWorldAgent;
import idees.gama.diagram.GamaDiagramEditor;
import idees.gama.features.add.AddBatchExperimentFeature;
import idees.gama.features.modelgeneration.ModelGenerator;
import idees.gama.ui.image.GamaImageProvider;

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

public class CreateBatchExperimentLinkFeature  extends AbstractCreateConnectionFeature {

	 private static final String TITLE = "Create a batch experiment";
	  
	 private static final String USER_QUESTION = "Enter new batch experiment name";
	 
	public CreateBatchExperimentLinkFeature(IFeatureProvider fp) {
		// provide name and description for the UI, e.g. the palette
		super(fp, "is composed of a batch experiment", "Create a new batch experiment");
	}

	private EBatchExperiment createEBatchExperiment(ICreateConnectionContext context) {
		String newBatchName = ExampleUtil.askString(TITLE, USER_QUESTION, "my_batch_xp");
	    if (newBatchName == null || newBatchName.length() == 0) {
	    	return null;
	    }  
	    EBatchExperiment newBatchExp= gama.GamaFactory.eINSTANCE.createEBatchExperiment();
	    newBatchExp.setError("");
	    newBatchExp.setHasError(false);
	    newBatchExp.setName(newBatchName);
		this.getDiagram().eResource().getContents().add(newBatchExp);
		CreateContext ac = new CreateContext();
		ac.setLocation(context.getTargetLocation().getX(), context.getTargetLocation().getY());
		ac.setSize(0, 0);
		ac.setTargetContainer(getDiagram());
		return newBatchExp;
	}
	
	private PictogramElement addEBatchExperiment(ICreateConnectionContext context, EBatchExperiment batchExp) {
		CreateContext cc = new CreateContext();
		cc.setLocation(context.getTargetLocation().getX() - (int)(AddBatchExperimentFeature.INIT_WIDTH/2.0), context.getTargetLocation().getY() - (int)(AddBatchExperimentFeature.INIT_HEIGHT/2.0));
		cc.setSize(0, 0);
		cc.setTargetContainer(getDiagram());
		return getFeatureProvider().addIfPossible(new AddContext(cc, batchExp));
	}
	
	public Connection create(ICreateConnectionContext context) {
		Connection newConnection = null;
		EWorldAgent source = getEWorldAgent(context.getSourceAnchor());
		EBatchExperiment target = createEBatchExperiment(context);
		if (source != null && target != null) {
			PictogramElement targetpe = addEBatchExperiment(context, target);
			// create new business object
			EExperimentLink eReference = createEReference(source, target);
			//eReference.setModel(source.getModel());
			getDiagram().eResource().getContents().add(eReference);
			// add connection for business object
			AddConnectionContext addContext = new AddConnectionContext(
					context.getSourceAnchor(), getAnchor(targetpe));
			addContext.setNewObject(eReference);
			newConnection = (Connection) getFeatureProvider().addIfPossible(
					addContext);
			eReference.setExperiment(target);
			eReference.setSpecies(source);
			source.getExperimentLinks().add(eReference);
			target.setExperimentLink(eReference);
		}
		GamaDiagramEditor diagramEditor = ((GamaDiagramEditor)getFeatureProvider().getDiagramTypeProvider().getDiagramEditor());
		diagramEditor.addEOject(target);
		
		return newConnection;
	}

	public boolean canCreate(ICreateConnectionContext context) {
		EWorldAgent source = getEWorldAgent(context.getSourceAnchor());
		if (source != null) {
			return true;
		}
		return false;
	}

	public boolean canStartConnection(ICreateConnectionContext context) {
		if (getEWorldAgent(context.getSourceAnchor()) != null) {
			return true;
		}
		return false;
	}
	
	protected EWorldAgent getEWorldAgent(Anchor anchor) {
		if (anchor != null) {
			Object object = getBusinessObjectForPictogramElement(anchor
					.getParent());
			if (object instanceof EWorldAgent) {
				return (EWorldAgent) object;
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
	


	/**
	 * Creates a EReference between two EClasses.
	 */
	private EExperimentLink createEReference(EWorldAgent source, EExperiment target) {
		EExperimentLink eReference = gama.GamaFactory.eINSTANCE.createEExperimentLink();
		return eReference;
	}
	
	@Override
	public String getCreateImageId() {
		return GamaImageProvider.IMG_BATCHXPLINK;
	}
	
	public void execute(IContext context) {
		super.execute(context);
		ModelGenerator.modelValidation(getFeatureProvider(), getDiagram());
	}
}