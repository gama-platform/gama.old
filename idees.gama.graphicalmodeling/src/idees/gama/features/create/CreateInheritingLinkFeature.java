package idees.gama.features.create;


import gama.EInheritLink;
import gama.ESpecies;
import gama.EWorldAgent;
import idees.gama.ui.image.GamaImageProvider;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.mm.pictograms.Connection;

public class CreateInheritingLinkFeature extends AbstractCreateSpeciesComponentLinkFeature {

	  public CreateInheritingLinkFeature(IFeatureProvider fp) {
          // provide name and description for the UI, e.g. the palette
          super(fp, "is parent of", "The source species is parent of the target species");
  }

  public Connection create(ICreateConnectionContext context) {
          Connection newConnection = null;
          ESpecies source = getESpecies(context.getSourceAnchor());
          ESpecies target = getESpecies(context.getTargetAnchor());
          if (source != null && target != null) {
                  // create new business object
                  EInheritLink eReference =  gama.GamaFactory.eINSTANCE.createEInheritLink();
                  //eReference.setModel(source.getModel());
                  getDiagram().eResource().getContents().add(eReference);
                  // add connection for business object
                   AddConnectionContext addContext =
                          new AddConnectionContext(context.getSourceAnchor(), context
                              .getTargetAnchor());
                      addContext.setNewObject(eReference);
              newConnection =
                          (Connection) getFeatureProvider().addIfPossible(addContext);
                  eReference.setParent(source);
                  eReference.setChild(target);
                  target.setInheritsFrom(source);
          }

          return newConnection;
  }

  public boolean canCreate(ICreateConnectionContext context) {
          ESpecies source = getESpecies(context.getSourceAnchor());
          ESpecies target = getESpecies(context.getTargetAnchor());
          if (source != null && target != null && !(source instanceof EWorldAgent) && !(target instanceof EWorldAgent) && target.getInheritsFrom() == null) {
                  return true;
          }
          return false;
  }

  public boolean canStartConnection(ICreateConnectionContext context) {
          ESpecies source = getESpecies(context.getSourceAnchor());
          if (source != null && !(source instanceof EWorldAgent)) {
                  return true;
          }
          return false;
  }
  

  
  @Override
  public String getCreateImageId() {
          return GamaImageProvider.IMG_INHERITINGLINK;
  }

}
