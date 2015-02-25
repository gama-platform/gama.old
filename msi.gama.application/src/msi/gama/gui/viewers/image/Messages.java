package msi.gama.gui.viewers.image;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "msi.gama.gui.viewers.image.messages"; //$NON-NLS-1$
	public static String ImageViewer_loadImageTask;
	public static String SaveImageAsDialog_saveAsBMPLabel;
	public static String SaveImageAsDialog_saveAsGIFLabel;
	public static String SaveImageAsDialog_saveAsICOLabel;
	public static String SaveImageAsDialog_saveAsImageInfoGroupLabel;
	public static String SaveImageAsDialog_saveAsJPEGLabel;
	public static String ImageViewer_saveAsLoadImageDataError;
	public static String ImageViewer_saveAsPipeJobName;
	public static String SaveImageAsDialog_saveAsPNGLabel;
	public static String SaveImageAsDialog_saveAsTIFFLabel;
	public static String SaveImageAsDialog_saveAsTypeComboLabel;
	public static String ImageViewer_saveErrorMessage;
	public static String ImageViewer_saveErrorTitle;
	public static String ImageViewerActionBarContributor_imageInfo;
	public static String ImageViewerActionBarContributor_mouseLocation;
	public static String ImageViewerActionBarContributor_rgb;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {}
}
