package idees.gama.diagram;

import gama.*;
import idees.gama.features.create.*;
import idees.gama.features.edit.*;
import idees.gama.ui.editFrame.EditFrame;
import java.util.*;
import msi.gama.util.TOrderedHashMap;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.*;
import org.eclipse.graphiti.features.context.IDoubleClickContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.palette.IPaletteCompartmentEntry;
import org.eclipse.graphiti.palette.impl.*;
import org.eclipse.graphiti.tb.DefaultToolBehaviorProvider;

public class MyGamaToolBehaviorProvider extends DefaultToolBehaviorProvider {

	final Map<EObject, EditFrame> frames;

	public MyGamaToolBehaviorProvider(final IDiagramTypeProvider diagramTypeProvider) {
		super(diagramTypeProvider);
		frames = new TOrderedHashMap<EObject, EditFrame>();
	}

	@Override
	public ICustomFeature getDoubleClickFeature(final IDoubleClickContext context) {
		ICustomFeature customFeature = null;
		PictogramElement[] pes = context.getPictogramElements();
		if ( pes != null && pes.length == 1 ) {
			Object bo = getDiagramTypeProvider().getFeatureProvider().getBusinessObjectForPictogramElement(pes[0]);
			if ( bo instanceof EExperiment ) {
				customFeature = new EditExperimentFeature(getFeatureProvider(), frames.get(bo), this);
			} else if ( bo instanceof ESpecies ) {
				customFeature = new EditSpeciesFeature(getFeatureProvider(), frames.get(bo), this);
			} else if ( bo instanceof EAction ) {
				customFeature = new EditActionFeature(getFeatureProvider(), frames.get(bo), this);
			} else if ( bo instanceof EReflex ) {
				customFeature = new EditReflexFeature(getFeatureProvider(), frames.get(bo), this);
			} else if ( bo instanceof EAspect ) {
				customFeature = new EditAspectFeature(getFeatureProvider(), frames.get(bo), this);
			} else if ( bo instanceof EDisplay ) {
				customFeature = new EditDisplayFeature(getFeatureProvider(), frames.get(bo), this);
			} /*
			 * else {
			 * customFeature = new RenameEGamaObjectFeature(getFeatureProvider());
			 * }
			 */
			// canExecute() tests especially if the context contains a EClass
			if ( customFeature != null && customFeature.canExecute(context) ) { return customFeature; }
		}

		return super.getDoubleClickFeature(context);
	}

	@Override
	public IPaletteCompartmentEntry[] getPalette() {
		List<IPaletteCompartmentEntry> ret = new ArrayList<IPaletteCompartmentEntry>();

		// add new compartment at the end of the existing compartments
		PaletteCompartmentEntry compartmentAgentEntry = new PaletteCompartmentEntry("Agents", null);
		PaletteCompartmentEntry compartmentAgentFeatureEntry = new PaletteCompartmentEntry("Agent features", null);
		PaletteCompartmentEntry compartmentExperimentEntry = new PaletteCompartmentEntry("Experiments", null);
		ret.add(compartmentAgentEntry);
		ret.add(compartmentAgentFeatureEntry);
		ret.add(compartmentExperimentEntry);

		IFeatureProvider featureProvider = getFeatureProvider();
		ICreateConnectionFeature[] createConnectionFeatures = featureProvider.getCreateConnectionFeatures();
		for ( ICreateConnectionFeature cf : createConnectionFeatures ) {
			ConnectionCreationToolEntry connectionCreationToolEntry =
				new ConnectionCreationToolEntry(cf.getCreateName(), cf.getCreateDescription(), cf.getCreateImageId(),
					cf.getCreateLargeImageId());
			connectionCreationToolEntry.addCreateConnectionFeature(cf);
			if ( cf instanceof CreateSubSpeciesLinkFeature || cf instanceof CreateSubGridLinkFeature ||
				cf instanceof CreateSubGraphSpeciesFeature || cf instanceof CreateInheritingLinkFeature ) {
				compartmentAgentEntry.addToolEntry(connectionCreationToolEntry);
			} else if ( cf instanceof CreateActionLinkFeature || cf instanceof CreateAspectLinkFeature ||
				cf instanceof CreateReflexLinkFeature ) {
				compartmentAgentFeatureEntry.addToolEntry(connectionCreationToolEntry);
			} else {
				compartmentExperimentEntry.addToolEntry(connectionCreationToolEntry);
			}
		}
		ICreateFeature[] createFeatures = featureProvider.getCreateFeatures();
		for ( ICreateFeature cf : createFeatures ) {
			ObjectCreationToolEntry objectCreationToolEntry =
				new ObjectCreationToolEntry(cf.getCreateName(), cf.getCreateDescription(), cf.getCreateImageId(),
					cf.getCreateLargeImageId(), cf);
			compartmentExperimentEntry.addToolEntry(objectCreationToolEntry);
		}

		return ret.toArray(new IPaletteCompartmentEntry[ret.size()]);
	}

	public Map<EObject, EditFrame> getFrames() {
		return frames;
	}

}
