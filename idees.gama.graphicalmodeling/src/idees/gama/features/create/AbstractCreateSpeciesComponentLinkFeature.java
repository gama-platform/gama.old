package idees.gama.features.create;

import gama.ESpecies;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.features.impl.AbstractCreateConnectionFeature;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;

public abstract class AbstractCreateSpeciesComponentLinkFeature extends AbstractCreateConnectionFeature {

	
	public AbstractCreateSpeciesComponentLinkFeature(IFeatureProvider fp, String name,
			String description) {
		super(fp, name, description);
	}

	public boolean canCreate(ICreateConnectionContext context) {
		ESpecies source = getESpecies(context.getSourceAnchor());
		if (source != null) {
			return true;
		}
		return false;
	}

	public boolean canStartConnection(ICreateConnectionContext context) {
		if (getESpecies(context.getSourceAnchor()) != null) {
			return true;
		}
		return false;
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
	
	protected ESpecies getESpecies(Anchor anchor) {
		if (anchor != null) {
			Object object = getBusinessObjectForPictogramElement(anchor
					.getParent());
			if (object instanceof ESpecies) {
				return (ESpecies) object;
			}
		}
		return null;
	}
	
	
	
}
