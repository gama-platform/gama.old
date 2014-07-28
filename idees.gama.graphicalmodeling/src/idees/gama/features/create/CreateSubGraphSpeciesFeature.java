package idees.gama.features.create;

import gama.EGraphLink;
import gama.EGraphTopologyEdge;
import gama.EGraphTopologyNode;
import gama.ESpecies;
import gama.ESubSpeciesLink;
import idees.gama.diagram.GamaDiagramEditor;
import idees.gama.features.ExampleUtil;
import idees.gama.features.add.AddSpeciesFeature;
import idees.gama.features.modelgeneration.ModelGenerator;
import idees.gama.ui.image.GamaImageProvider;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.features.context.impl.CreateContext;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class CreateSubGraphSpeciesFeature extends AbstractCreateSpeciesComponentLinkFeature {

	 private static final String TITLE = "Create graph";
	  
	 private static final String USER_QUESTION_NODE = "Enter new node species name";
	 private static final String USER_QUESTION_EDGE = "Enter new edge species name";
		
	 
	public CreateSubGraphSpeciesFeature(IFeatureProvider fp) {
		// provide name and description for the UI, e.g. the palette
		super(fp, "is composed of a graph", "Create a new graph");
	}

	private ESpecies createNode(ICreateConnectionContext context) {
		String newNodeName = ExampleUtil.askString(TITLE, USER_QUESTION_NODE, "my_node");
	    if (newNodeName == null || newNodeName.trim().length() == 0) {
	    	return null;
	    }  
	    ESpecies newNode = gama.GamaFactory.eINSTANCE.createESpecies();
	    newNode.setError("");
	    newNode.setHasError(false);
		
		this.getDiagram().eResource().getContents().add(newNode);
		newNode.setName(newNodeName);
		EGraphTopologyNode newTopoNode = gama.GamaFactory.eINSTANCE.createEGraphTopologyNode();
		
		this.getDiagram().eResource().getContents().add(newTopoNode);
		newNode.setTopology(newTopoNode);
		CreateContext acNode = new CreateContext();
		acNode.setLocation(context.getTargetLocation().getX() - 100, context.getTargetLocation().getY());
		acNode.setSize(0, 0);
		acNode.setTargetContainer(getDiagram());
		ModelGenerator.modelValidation(getFeatureProvider(), getDiagram());
		return newNode;
	}
	
	private ESpecies createEdge(ICreateConnectionContext context) {
	
		String newEdgeName = ExampleUtil.askString(TITLE, USER_QUESTION_EDGE, "my_edge");
	    if (newEdgeName == null || newEdgeName.trim().length() == 0) {
	    	return null;
	    }  
	    ESpecies newEdge = gama.GamaFactory.eINSTANCE.createESpecies();
	    newEdge.setError("");
	    newEdge.setHasError(false);
		
		this.getDiagram().eResource().getContents().add(newEdge);
		newEdge.setName(newEdgeName);
		EGraphTopologyEdge newTopoEdge = gama.GamaFactory.eINSTANCE.createEGraphTopologyEdge();
		this.getDiagram().eResource().getContents().add(newTopoEdge);
		newEdge.setTopology(newTopoEdge);
		/*CreateContext acEdge = new CreateContext();
		acEdge.setLocation(context.getTargetLocation().getX() + 100, context.getTargetLocation().getY());
		acEdge.setSize(0, 0);
		acEdge.setTargetContainer(getDiagram());*/
		return newEdge;
	}
	
	
	private PictogramElement addESpecies(ICreateConnectionContext context, ESpecies species) {
		CreateContext cc = new CreateContext();
		if (species.getTopology() instanceof EGraphTopologyNode) 
			cc.setLocation(context.getTargetLocation().getX() + 100 - (int)(AddSpeciesFeature.INIT_WIDTH/2.0), context.getTargetLocation().getY() - (int)(AddSpeciesFeature.INIT_HEIGHT/2.0));
		else 
			cc.setLocation(context.getTargetLocation().getX() - 100 - (int)(AddSpeciesFeature.INIT_WIDTH/2.0), context.getTargetLocation().getY() - (int)(AddSpeciesFeature.INIT_HEIGHT/2.0));
		
		cc.setSize(0, 0);
		cc.setTargetContainer(getDiagram());
		return getFeatureProvider().addIfPossible(new AddContext(cc, species));
	}
	
	public Connection create(ICreateConnectionContext context) {
		Connection newConnectionNode = null;
		ESpecies source = getESpecies(context.getSourceAnchor());
		ESpecies targetNode = createNode(context);
		ESpecies targetEdge = createEdge(context);
		
		if (source != null && targetNode != null  && targetEdge != null) {
			PictogramElement targetNpe = addESpecies(context, targetNode);
			PictogramElement targetEpe = addESpecies(context, targetEdge);
			
			// create new business object
			ESubSpeciesLink eReference = createEReference(source, targetNode);
			//eReference.setModel(source.getModel());
			getDiagram().eResource().getContents().add(eReference);
			// add connection for business object
			AddConnectionContext addContext = new AddConnectionContext(
					context.getSourceAnchor(), getAnchor(targetNpe));
			addContext.setNewObject(eReference);
			newConnectionNode = (Connection) getFeatureProvider().addIfPossible(
					addContext);
			eReference.setMacro(source);
			eReference.setMicro(targetNode);
			source.getMicroSpeciesLinks().add(eReference);
			targetNode.getMacroSpeciesLinks().add(eReference);
			GamaDiagramEditor diagramEditor = ((GamaDiagramEditor)getFeatureProvider().getDiagramTypeProvider().getDiagramEditor());
			
			if (source != null && targetEdge != null) {
				// create new business object
				ESubSpeciesLink eReference2 = createEReference(source, targetEdge);
				//eReference.setModel(source.getModel());
				getDiagram().eResource().getContents().add(eReference2);
				// add connection for business object
				AddConnectionContext addContext2 = new AddConnectionContext(
						context.getSourceAnchor(), getAnchor(targetEpe));
				addContext.setNewObject(eReference2);
				getFeatureProvider().addIfPossible(
						addContext2);
				eReference2.setMacro(source);
				eReference2.setMicro(targetEdge);
				source.getMicroSpeciesLinks().add(eReference2);
				targetEdge.getMacroSpeciesLinks().add(eReference2);
				diagramEditor.addEOject(targetEdge);
			}
			if (targetNode != null && targetEdge != null) {
				// create new business object
				EGraphLink eReference3 = gama.GamaFactory.eINSTANCE.createEGraphLink();
				//eReference.setModel(source.getModel());
				getDiagram().eResource().getContents().add(eReference3);
				// add connection for business object
				AddConnectionContext addContext3 = new AddConnectionContext(
						 getAnchor(targetNpe), getAnchor(targetEpe));
				addContext.setNewObject(eReference3);
				getFeatureProvider().addIfPossible(
						addContext3);
				eReference3.setNode(targetNode);
				eReference3.setEdge(targetEdge);
				diagramEditor.addEOject(targetNode);
			}
		}
		return newConnectionNode;
	}


	/**
	 * Creates a EReference between two EClasses.
	 */
	private ESubSpeciesLink createEReference(ESpecies source, ESpecies target) {
		ESubSpeciesLink eReference = gama.GamaFactory.eINSTANCE.createESubSpeciesLink();
		return eReference;
	}

	@Override
	public String getCreateImageId() {
		return GamaImageProvider.IMG_SPECIESGRAPHTOPO;
	}
	
	public void execute(IContext context) {
		super.execute(context);
		ModelGenerator.modelValidation(getFeatureProvider(), getDiagram());
	}

}
