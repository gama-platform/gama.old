package idees.gama.features.create;

import idees.gama.features.add.AddSpeciesFeature;
import gama.ESpecies;
import gama.ESubSpeciesLink;

import org.eclipse.graphiti.examples.common.ExampleUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.features.context.impl.CreateContext;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class CreateSubSpeciesLinkFeature extends AbstractCreateSpeciesComponentLinkFeature {

	 private static final String TITLE = "Create species";
	  
	 private static final String USER_QUESTION = "Enter new species name";
	 
	public CreateSubSpeciesLinkFeature(IFeatureProvider fp) {
		// provide name and description for the UI, e.g. the palette
		super(fp, "is composed of the species", "Create a new species");
	}

	private ESpecies createESpecies(ICreateConnectionContext context) {
		String newSpeciesName = ExampleUtil.askString(TITLE, USER_QUESTION, "");
	    if (newSpeciesName == null || newSpeciesName.trim().length() == 0) {
	    	newSpeciesName = "my_species";
	    }  
	    ESpecies newSpecies = gama.GamaFactory.eINSTANCE.createESpecies();
		this.getDiagram().eResource().getContents().add(newSpecies);
		newSpecies.setName(newSpeciesName);
		CreateContext ac = new CreateContext();
		ac.setLocation(context.getTargetLocation().getX(), context.getTargetLocation().getY());
		ac.setSize(0, 0);
		ac.setTargetContainer(getDiagram());
		return newSpecies;
	}
	
	private PictogramElement addESpecies(ICreateConnectionContext context, ESpecies species) {
		CreateContext cc = new CreateContext();
		cc.setLocation(context.getTargetLocation().getX() - (int)(AddSpeciesFeature.INIT_WIDTH/2.0), context.getTargetLocation().getY() - (int)(AddSpeciesFeature.INIT_HEIGHT/2.0));
		cc.setSize(0, 0);
		cc.setTargetContainer(getDiagram());
		return getFeatureProvider().addIfPossible(new AddContext(cc, species));
	}
	
	public Connection create(ICreateConnectionContext context) {
		Connection newConnection = null;
		ESpecies source = getESpecies(context.getSourceAnchor());
		ESpecies target = createESpecies(context);
		PictogramElement targetpe = addESpecies(context, target);
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
