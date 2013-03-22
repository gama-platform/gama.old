package idees.gama.features.create;


import idees.gama.features.add.AddReflexFeature;
import idees.gama.ui.image.GamaImageProvider;
import gama.EReflex;
import gama.EReflexLink;
import gama.ESpecies;

import org.eclipse.graphiti.examples.common.ExampleUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.features.context.impl.CreateContext;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class CreateReflexLinkFeature extends AbstractCreateSpeciesComponentLinkFeature {

	private static final String TITLE = "Create reflex";
	 
    private static final String USER_QUESTION = "Enter new reflex name";
 
    public CreateReflexLinkFeature(IFeatureProvider fp) {
		// provide name and description for the UI, e.g. the palette
		super(fp, "has the reflex", "Create reflex link");
	}

    private EReflex createEReflex(ICreateConnectionContext context) {
		String newReflexName = ExampleUtil.askString(TITLE, USER_QUESTION, "");
	    if (newReflexName == null || newReflexName.trim().length() == 0) {
	    	newReflexName = "my_reflex";
	    }  
	    EReflex newReflex = gama.GamaFactory.eINSTANCE.createEReflex();
		this.getDiagram().eResource().getContents().add(newReflex);
		newReflex.setName(newReflexName);
		CreateContext ac = new CreateContext();
		ac.setLocation(context.getTargetLocation().getX(), context.getTargetLocation().getY());
		ac.setSize(0, 0);
		ac.setTargetContainer(getDiagram());
		return newReflex;
	}
	
	private PictogramElement addEReflex(ICreateConnectionContext context, EReflex action) {
		CreateContext cc = new CreateContext();
		cc.setLocation(context.getTargetLocation().getX() - (int)(AddReflexFeature.INIT_WIDTH/2.0), context.getTargetLocation().getY() - (int)(AddReflexFeature.INIT_HEIGHT/2.0));
		cc.setSize(0, 0);
		cc.setTargetContainer(getDiagram());
		return getFeatureProvider().addIfPossible(new AddContext(cc, action));
	}
	
	public Connection create(ICreateConnectionContext context) {
		Connection newConnection = null;
		ESpecies source = getESpecies(context.getSourceAnchor());
		
		EReflex target = createEReflex(context);
		PictogramElement targetpe = addEReflex(context, target);
		if (source != null && target != null) {
			// create new business object
			EReflexLink eReference = createEReference(source, target);
			//eReference.setModel(source.getModel());
			getDiagram().eResource().getContents().add(eReference);
			// add connection for business object
			AddConnectionContext addContext = new AddConnectionContext(
					context.getSourceAnchor(), getAnchor(targetpe));
			addContext.setNewObject(eReference);
			newConnection = (Connection) getFeatureProvider().addIfPossible(
					addContext);
			source.getReflexLinks().add(eReference);
			target.getReflexLinks().add(eReference);
		}

		return newConnection;
	}

	private EReflexLink createEReference(ESpecies source, EReflex target) {
		EReflexLink eReference = gama.GamaFactory.eINSTANCE.createEReflexLink();
		eReference.setSource(source);
		eReference.setTarget(target);
		return eReference;
	}
	
	@Override
	public String getCreateImageId() {
		return GamaImageProvider.IMG_REFLEXLINK;
	}

}
