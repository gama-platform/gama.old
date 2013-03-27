package idees.gama.ui.image;

import org.eclipse.graphiti.ui.platform.AbstractImageProvider;

public class GamaImageProvider extends AbstractImageProvider {
 
    // The prefix for all identifiers of this image provider
    protected static final String PREFIX = 
              "idees.gama.";
 
    public static final String IMG_SUBSPECIESLINK= PREFIX + "subspecieslink";
    public static final String IMG_SUBGRIDLINK= PREFIX + "subgridlink";
    public static final String IMG_SPECIESGRIDTOPO= PREFIX + "speciesgridtopo";
    public static final String IMG_SPECIESGRAPHTOPO= PREFIX + "speciesgraphtopo";
    public static final String IMG_SPECIESCONTINUOUSTOPO= PREFIX + "speciescontinuoustopo";
    public static final String IMG_SPECIESGRAPHNODETOPO= PREFIX + "speciesgraphnodetopo";
    public static final String IMG_SPECIESGRAPHEDGETOPO= PREFIX + "speciesgraphedgetopo";
    public static final String IMG_ACTIONLINK= PREFIX + "actionlink";
    public static final String IMG_ASPECTLINK= PREFIX + "aspectlink";
    public static final String IMG_REFLEXLINK= PREFIX + "reflexlink";
    public static final String IMG_GUIXPLINK= PREFIX + "guixplink";
    public static final String IMG_BATCHXPLINK= PREFIX + "batchxplink";
    public static final String IMG_DISPLAYLINK= PREFIX + "displaylink";
    public static final String IMG_INHERITINGLINK= PREFIX + "inheritinglink";
 
    @Override
    protected void addAvailableImages() {
        // register the path for each image identifier
        addImageFilePath(IMG_SUBSPECIESLINK, "icons/subspecieslink.png");
        addImageFilePath(IMG_SUBGRIDLINK, "icons/subgridlink.png");
        addImageFilePath(IMG_SPECIESGRIDTOPO, "icons/speciesgridtopo.png");
        addImageFilePath(IMG_SPECIESGRAPHTOPO, "icons/speciesgraphtopo.png");
        addImageFilePath(IMG_SPECIESCONTINUOUSTOPO, "icons/speciescontinuoustopo.png");
        addImageFilePath(IMG_SPECIESGRAPHNODETOPO, "icons/speciesgraphnodetopo.png");
        addImageFilePath(IMG_SPECIESGRAPHEDGETOPO, "icons/speciesgraphedgetopo.png");
        addImageFilePath(IMG_ACTIONLINK, "icons/actionlink.png");
        addImageFilePath(IMG_ASPECTLINK, "icons/aspectlink.png");
        addImageFilePath(IMG_REFLEXLINK, "icons/reflexlink.png");
        addImageFilePath(IMG_GUIXPLINK, "icons/guixplink.png");
        addImageFilePath(IMG_BATCHXPLINK, "icons/batchxplink.png");
        addImageFilePath(IMG_DISPLAYLINK, "icons/displaylink.png");
        addImageFilePath(IMG_INHERITINGLINK, "icons/inheritinglink.png");
    }
}