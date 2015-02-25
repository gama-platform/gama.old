package idees.gama.features.edit;

import gama.ESpecies;
import idees.gama.diagram.MyGamaToolBehaviorProvider;
import idees.gama.ui.editFrame.*;
import java.util.*;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.mm.pictograms.*;

public class EditSpeciesFeature extends EditFeature {

	public EditSpeciesFeature(final IFeatureProvider fp, final EditFrame frame, final MyGamaToolBehaviorProvider tbp) {
		super(fp, frame, tbp);

	}

	@Override
	public String getDescription() {
		return "Edition of a species";
	}

	@Override
	public boolean canExecute(final ICustomContext context) {
		// allow rename if exactly one pictogram element
		// representing a EClass is selected
		boolean ret = false;
		PictogramElement[] pes = context.getPictogramElements();
		if ( pes != null && pes.length == 1 ) {
			Object bo = getBusinessObjectForPictogramElement(pes[0]);
			if ( bo instanceof ESpecies ) {
				ret = true;
			}
		}
		return ret;
	}

	@Override
	public void execute(final ICustomContext context) {
		PictogramElement[] pes = context.getPictogramElements();
		if ( pes != null && pes.length == 1 ) {
			Object bo = getBusinessObjectForPictogramElement(pes[0]);
			if ( bo instanceof ESpecies ) {
				ESpecies eSpecies = (ESpecies) bo;
				if ( frame == null || frame.getShell() == null || frame.getShell().isDisposed() ) {
					frame = new EditSpeciesFrame(getDiagram(), getFeatureProvider(), this, eSpecies, speciesList());
					frame.open();
					tbp.getFrames().put(eSpecies, frame);

				} else {
					frame.getShell().setFocus();
				}
			}
		}
		this.hasDoneChanges = true;
	}

	private List<ESpecies> speciesList() {
		List<ESpecies> species = new ArrayList<ESpecies>();
		List<Shape> contents = getDiagram().getChildren();
		if ( contents != null ) {
			for ( Shape obj : contents ) {
				Object bo = getBusinessObjectForPictogramElement(obj);
				if ( bo instanceof ESpecies ) {
					species.add((ESpecies) bo);
				}
			}
		}
		return species;
	}

}