package idees.gama.features.create;

import gama.EAction;
import gama.EActionLink;
import gama.ESpecies;
import idees.gama.features.add.AddActionFeature;
import idees.gama.ui.image.GamaImageProvider;

import org.eclipse.graphiti.examples.common.ExampleUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.features.context.impl.CreateContext;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class CreateActionLinkFeature extends AbstractCreateSpeciesComponentLinkFeature {

	  private static final String TITLE = "Create action";
	  
	    private static final String USER_QUESTION = "Enter new action name";
	 
	    
	public CreateActionLinkFeature(IFeatureProvider fp) {
		// provide name and description for the UI, e.g. the palette
		super(fp, "has the action", "Create action link");
	}

	
	private EAction createEAction(ICreateConnectionContext context) {
		String newActionName = ExampleUtil.askString(TITLE, USER_QUESTION, "");
	    if (newActionName == null || newActionName.trim().length() == 0) {
	    	newActionName = "my_action";
	    }  
		EAction newAction = gama.GamaFactory.eINSTANCE.createEAction();
		this.getDiagram().eResource().getContents().add(newAction);
		newAction.setName(newActionName);
		CreateContext ac = new CreateContext();
		ac.setLocation(context.getTargetLocation().getX(), context.getTargetLocation().getY());
		ac.setSize(0, 0);
		ac.setTargetContainer(getDiagram());
		return newAction;
	}
	
	private PictogramElement addEAction(ICreateConnectionContext context, EAction action) {
		CreateContext cc = new CreateContext();
		cc.setLocation(context.getTargetLocation().getX() - (int)(AddActionFeature.INIT_WIDTH/2.0), context.getTargetLocation().getY() - (int)(AddActionFeature.INIT_HEIGHT/2.0));
		cc.setSize(0, 0);
		cc.setTargetContainer(getDiagram());
		return getFeatureProvider().addIfPossible(new AddContext(cc, action));
	}
	
	public Connection create(ICreateConnectionContext context) {
		Connection newConnection = null;
		ESpecies source = getESpecies(context.getSourceAnchor());
		
		EAction target = createEAction(context);
		PictogramElement targetpe = addEAction(context, target);
		if (source != null && target != null) {
			// create new business object
			EActionLink eReference = createEReference(source, target);
			//eReference.setModel(source.getModel());
			getDiagram().eResource().getContents().add(eReference);
			// add connection for business object
			AddConnectionContext addContext = new AddConnectionContext(
					context.getSourceAnchor(), getAnchor(targetpe));
			addContext.setNewObject(eReference);
			
			newConnection = (Connection) getFeatureProvider().addIfPossible(
					addContext);
			source.getOutcomingLinks().add(eReference);
			target.getIncomingLinks().add(eReference);
		}

		return newConnection;
	}

	private EActionLink createEReference(ESpecies source, EAction target) {
		EActionLink eReference = gama.GamaFactory.eINSTANCE.createEActionLink();
		eReference.setSource(source);
		eReference.setTarget(target);
		return eReference;
	}
	
	@Override
	public String getCreateImageId() {
		return GamaImageProvider.IMG_ACTIONLINK;
	}

}
