package idees.gama.diagram;

import gama.EAction;
import gama.EAspect;
import gama.EGrid;
import gama.EReflex;
import gama.ESpecies;
import idees.gama.features.create.CreateActionLinkFeature;
import idees.gama.features.create.CreateAspectLinkFeature;
import idees.gama.features.create.CreateReflexLinkFeature;
import idees.gama.features.create.CreateSubGridLinkFeature;
import idees.gama.features.create.CreateSubSpeciesLinkFeature;
import idees.gama.features.edit.EditActionFeature;
import idees.gama.features.edit.EditAspectFeature;
import idees.gama.features.edit.EditReflexFeature;
import idees.gama.features.edit.EditSpeciesFeature;
import idees.gama.features.others.RenameEGamaObjectFeature;
import idees.gama.ui.editFrame.EditAspectFrame;
import idees.gama.ui.image.GamaImageProvider;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDoubleClickContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.palette.IPaletteCompartmentEntry;
import org.eclipse.graphiti.palette.impl.ConnectionCreationToolEntry;
import org.eclipse.graphiti.palette.impl.ObjectCreationToolEntry;
import org.eclipse.graphiti.palette.impl.PaletteCompartmentEntry;
import org.eclipse.graphiti.tb.DefaultToolBehaviorProvider;
import org.eclipse.graphiti.tb.IDecorator;
import org.eclipse.graphiti.tb.ImageDecorator;

public class MyGamaToolBehaviorProvider extends DefaultToolBehaviorProvider{

	public MyGamaToolBehaviorProvider(IDiagramTypeProvider diagramTypeProvider) {
		super(diagramTypeProvider);
	}
	
	 @Override
	    public ICustomFeature getDoubleClickFeature(IDoubleClickContext context) {
	        ICustomFeature customFeature = null;
	        PictogramElement[] pes = context.getPictogramElements();
	        if (pes != null && pes.length == 1) {
	            Object bo = getDiagramTypeProvider().getFeatureProvider().getBusinessObjectForPictogramElement(pes[0]);
	       
		        if (bo instanceof ESpecies ) {
		        	customFeature = new EditSpeciesFeature(getFeatureProvider());
		        } 
		        else if (bo instanceof EAction ) {
		        	customFeature = new EditActionFeature(getFeatureProvider());
		        } else if (bo instanceof EReflex ) {
				    customFeature = new EditReflexFeature(getFeatureProvider());
		        } else if (bo instanceof EAspect ) {
				    customFeature = new EditAspectFeature(getFeatureProvider());
				} else {
		        	customFeature = new RenameEGamaObjectFeature(getFeatureProvider());
		        }
		        // canExecute() tests especially if the context contains a EClass
		        if (customFeature != null && customFeature.canExecute(context)) {
		            return customFeature;
		        }
	        }
	 
	        return super.getDoubleClickFeature(context);
	    }
	 
	 
	 @Override
	    public IPaletteCompartmentEntry[] getPalette() {
	        List<IPaletteCompartmentEntry> ret =
	            new ArrayList<IPaletteCompartmentEntry>();
	 
	        // add new compartment at the end of the existing compartments
	        PaletteCompartmentEntry compartmentAgentEntry =
	            new PaletteCompartmentEntry("Agents", null);
	        PaletteCompartmentEntry compartmentAgentFeatureEntry =
		            new PaletteCompartmentEntry("Agent features", null);
	        PaletteCompartmentEntry compartmentExperimentEntry =
		            new PaletteCompartmentEntry("Experiments", null);
	        ret.add(compartmentAgentEntry);
	        ret.add(compartmentAgentFeatureEntry);
	        ret.add(compartmentExperimentEntry);
	  
	        IFeatureProvider featureProvider = getFeatureProvider();
	        ICreateConnectionFeature[] createConnectionFeatures =
		             featureProvider.getCreateConnectionFeatures();
		        for (ICreateConnectionFeature cf : createConnectionFeatures) {
		        	ConnectionCreationToolEntry connectionCreationToolEntry =
			                new ConnectionCreationToolEntry(cf.getCreateName(), cf
			                  .getCreateDescription(), cf.getCreateImageId(),
			                    cf.getCreateLargeImageId());
			                        connectionCreationToolEntry.addCreateConnectionFeature(cf);
		            if (cf instanceof CreateSubSpeciesLinkFeature || cf instanceof CreateSubGridLinkFeature) {
			            compartmentAgentEntry.addToolEntry(connectionCreationToolEntry);
		            } else if (cf instanceof CreateActionLinkFeature || cf instanceof CreateAspectLinkFeature || cf instanceof CreateReflexLinkFeature) {
		            	compartmentAgentFeatureEntry.addToolEntry(connectionCreationToolEntry);
		            } else {
		            	compartmentExperimentEntry.addToolEntry(connectionCreationToolEntry);
		            }
		        }
		      ICreateFeature[] createFeatures = featureProvider.getCreateFeatures();
		        for (ICreateFeature cf : createFeatures) {
		        	ObjectCreationToolEntry objectCreationToolEntry =
			                   new ObjectCreationToolEntry(cf.getCreateName(),
			                     cf.getCreateDescription(), cf.getCreateImageId(),
			                        cf.getCreateLargeImageId(), cf);
		        	compartmentExperimentEntry.addToolEntry(objectCreationToolEntry);
		        }
		       
		        
	 
	        return ret.toArray(new IPaletteCompartmentEntry[ret.size()]);
	    }

}
