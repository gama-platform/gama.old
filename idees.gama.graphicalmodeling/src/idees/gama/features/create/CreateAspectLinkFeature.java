package idees.gama.features.create;


import idees.gama.features.add.AddAspectFeature;
import gama.EAspect;
import gama.EAspectLink;
import gama.ESpecies;

import org.eclipse.graphiti.examples.common.ExampleUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.features.context.impl.CreateContext;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class CreateAspectLinkFeature extends AbstractCreateSpeciesComponentLinkFeature {

	 private static final String TITLE = "Create aspect";
	 
	 private static final String USER_QUESTION = "Enter new aspect name";
	    
	public CreateAspectLinkFeature(IFeatureProvider fp) {
		// provide name and description for the UI, e.g. the palette
		super(fp, "has the aspect", "Create aspect link");
	}
	
	private EAspect createEAspect(ICreateConnectionContext context) {
		String newAspectName = ExampleUtil.askString(TITLE, USER_QUESTION, "");
	    if (newAspectName == null || newAspectName.trim().length() == 0) {
	    	newAspectName = "my_aspect";
	    }  
	    EAspect newAspect = gama.GamaFactory.eINSTANCE.createEAspect();
		this.getDiagram().eResource().getContents().add(newAspect);
		newAspect.setName(newAspectName);
		CreateContext ac = new CreateContext();
		ac.setLocation(context.getTargetLocation().getX(), context.getTargetLocation().getY());
		ac.setSize(0, 0);
		ac.setTargetContainer(getDiagram());
		return newAspect;
	}
	
	private PictogramElement addEAspect(ICreateConnectionContext context, EAspect aspect) {
		CreateContext cc = new CreateContext();
		cc.setLocation(context.getTargetLocation().getX() - (int)(AddAspectFeature.INIT_WIDTH/2.0), context.getTargetLocation().getY() - (int)(AddAspectFeature.INIT_HEIGHT/2.0));
		cc.setSize(0, 0);
		cc.setTargetContainer(getDiagram());
		return getFeatureProvider().addIfPossible(new AddContext(cc, aspect));
	}
	
	public Connection create(ICreateConnectionContext context) {
		Connection newConnection = null;
		ESpecies source = getESpecies(context.getSourceAnchor());
		
		EAspect target = createEAspect(context);
		PictogramElement targetpe = addEAspect(context, target);
		if (source != null && target != null) {
			// create new business object
			EAspectLink eReference = createEReference(source, target);
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

	private EAspectLink createEReference(ESpecies source, EAspect target) {
		EAspectLink eReference = gama.GamaFactory.eINSTANCE.createEAspectLink();
		eReference.setSource(source);
		eReference.setTarget(target);
		return eReference;
	}

}
