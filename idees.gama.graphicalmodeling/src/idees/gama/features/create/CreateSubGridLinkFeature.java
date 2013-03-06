package idees.gama.features.create;


import gama.EGrid;
import gama.ESpecies;
import gama.ESubSpeciesLink;
import idees.gama.features.add.AddGridFeature;

import org.eclipse.graphiti.examples.common.ExampleUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.features.context.impl.CreateContext;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class CreateSubGridLinkFeature extends AbstractCreateSpeciesComponentLinkFeature {

	 private static final String TITLE = "Create a grid";
	  
	 private static final String USER_QUESTION = "Enter new cell name";
	 
	public CreateSubGridLinkFeature(IFeatureProvider fp) {
		// provide name and description for the UI, e.g. the palette
		super(fp, "is composed of a grid", "Create micro-macro agents link");
	}

	private EGrid createEGrid(ICreateConnectionContext context) {
		String newSpeciesName = ExampleUtil.askString(TITLE, USER_QUESTION, "");
	    if (newSpeciesName == null || newSpeciesName.trim().length() == 0) {
	    	newSpeciesName = "my_cell";
	    }  
	    EGrid newSpecies = gama.GamaFactory.eINSTANCE.createEGrid();
		this.getDiagram().eResource().getContents().add(newSpecies);
		newSpecies.setName(newSpeciesName);
		CreateContext ac = new CreateContext();
		ac.setLocation(context.getTargetLocation().getX(), context.getTargetLocation().getY());
		ac.setSize(0, 0);
		ac.setTargetContainer(getDiagram());
		return newSpecies;
	}
	
	private PictogramElement addEGrid(ICreateConnectionContext context, EGrid species) {
		CreateContext cc = new CreateContext();
		cc.setLocation(context.getTargetLocation().getX() - (int)(AddGridFeature.INIT_WIDTH/2.0), context.getTargetLocation().getY() - (int)(AddGridFeature.INIT_HEIGHT/2.0));
		cc.setSize(0, 0);
		cc.setTargetContainer(getDiagram());
		return getFeatureProvider().addIfPossible(new AddContext(cc, species));
	}
	
	public Connection create(ICreateConnectionContext context) {
		Connection newConnection = null;
		ESpecies source = getESpecies(context.getSourceAnchor());
		EGrid target = createEGrid(context);
		PictogramElement targetpe = addEGrid(context, target);
		if (source != null && target != null) {
			// create new business object
			ESubSpeciesLink eReference = createEReference(source, target);
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


	/**
	 * Creates a EReference between two EClasses.
	 */
	private ESubSpeciesLink createEReference(ESpecies source, ESpecies target) {
		ESubSpeciesLink eReference = gama.GamaFactory.eINSTANCE.createESubSpeciesLink();
		return eReference;
	}

}
