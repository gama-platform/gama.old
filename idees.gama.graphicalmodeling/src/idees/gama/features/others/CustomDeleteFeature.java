package idees.gama.features.others;

import gama.EWorldAgent;
import idees.gama.diagram.GamaDiagramEditor;
import idees.gama.features.modelgeneration.ModelGenerator;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.context.impl.RemoveContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.features.DefaultDeleteFeature;

public class CustomDeleteFeature extends DefaultDeleteFeature{

	IFeatureProvider fp;
	
	public CustomDeleteFeature(IFeatureProvider fp) {
		super(fp);
		this.fp = fp;
	}

	public boolean canDelete(IDeleteContext context) {
		PictogramElement pictogramElement = context.getPictogramElement();
		 Object bo = getBusinessObjectForPictogramElement(pictogramElement);
		if (bo instanceof EWorldAgent) {
			return false;
		}
		IRemoveContext rc = new RemoveContext(pictogramElement);
		IRemoveFeature removeFeature = getFeatureProvider().getRemoveFeature(rc);
		boolean ret = (removeFeature != null);
		return ret;
	}

	@Override
	public void postDelete(IDeleteContext context) {
		super.postDelete(context);
		 Object bo = getBusinessObjectForPictogramElement(context.getPictogramElement());
			
		GamaDiagramEditor diagramEditor = ((GamaDiagramEditor)fp.getDiagramTypeProvider().getDiagramEditor());
		if (bo instanceof EObject)
			diagramEditor.removeEOject((EObject) bo);
		
		ModelGenerator.modelValidation(getFeatureProvider(), getDiagram());
	}

	
}
