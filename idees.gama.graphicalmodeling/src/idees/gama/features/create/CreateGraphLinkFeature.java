package idees.gama.features.create;

import gama.EGraphLink;
import gama.EGraphTopologyEdge;
import gama.EGraphTopologyNode;
import gama.ESpecies;
import idees.gama.diagram.GamaDiagramEditor;
import idees.gama.features.modelgeneration.ModelGenerator;
import idees.gama.ui.image.GamaImageProvider;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.mm.pictograms.Connection;

public class CreateGraphLinkFeature extends AbstractCreateSpeciesComponentLinkFeature {
	
	 
	public CreateGraphLinkFeature(IFeatureProvider fp) {
		// provide name and description for the UI, e.g. the palette
		super(fp, "node/edge link", "Create a new node/edge link ");
	}
		
	public Connection create(ICreateConnectionContext context) {
		Connection newConnectionNode = null;
		ESpecies source = getESpecies(context.getSourceAnchor());
		ESpecies target = getESpecies(context.getTargetAnchor());
			
	
		if (source != null && target != null) {
			// create new business object
			EGraphLink eReference = gama.GamaFactory.eINSTANCE.createEGraphLink();
			//eReference.setModel(source.getModel());
			getDiagram().eResource().getContents().add(eReference);
			// add connection for business object
			AddConnectionContext addContext = new AddConnectionContext(
					context.getSourceAnchor(),context.getTargetAnchor());
			addContext.setNewObject(eReference);
			getFeatureProvider().addIfPossible(
					addContext);
			if (target.getTopology() instanceof EGraphTopologyNode) {
				eReference.setNode(target);
				eReference.setEdge(source);
			} else {
				eReference.setNode(source);
				eReference.setEdge(target);
			}
		}
		GamaDiagramEditor diagramEditor = ((GamaDiagramEditor)getFeatureProvider().getDiagramTypeProvider().getDiagramEditor());
		diagramEditor.addEOject(target);
		
		return newConnectionNode;
	}


	@Override
	public String getCreateImageId() {
		return GamaImageProvider.IMG_SPECIESGRAPHTOPO;
	}
	
	public boolean canCreate(ICreateConnectionContext context) {
		ESpecies target = getESpecies(context.getTargetAnchor());
		ESpecies source = getESpecies(context.getSourceAnchor());
		if (target != null && source != null &&
				(((source.getTopology() instanceof EGraphTopologyNode) && (target.getTopology() instanceof EGraphTopologyEdge)) 
						|| ((source.getTopology() instanceof EGraphTopologyEdge) && (target.getTopology() instanceof EGraphTopologyNode)))) {
			return true;
		}
		return false;
	}

	public boolean canStartConnection(ICreateConnectionContext context) {
		ESpecies source = getESpecies(context.getSourceAnchor());
		if (source != null && ((source.getTopology() instanceof EGraphTopologyNode) || (source.getTopology() instanceof EGraphTopologyEdge))) {
			return true;
		}
		return false;
	}
	
	public void execute(IContext context) {
		super.execute(context);
		ModelGenerator.modelValidation(getFeatureProvider(), getDiagram());
	}
	

}
