package msi.gama.gui.metadata.images;

import java.io.*;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.*;
import org.eclipse.swt.*;
import org.eclipse.swt.graphics.ImageData;
import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * Content type matcher for image files.
 */
public class ImageContentTypeDescriber implements IContentDescriber {

	public final static TIntObjectHashMap<String> formatsLongNames = new TIntObjectHashMap() {

		{
			put(SWT.IMAGE_BMP, "Windows Bitmap (BMP)");
			put(SWT.IMAGE_BMP_RLE, "Windows Bitmap (BMP)");
			put(SWT.IMAGE_OS2_BMP, "OS/2 Bitmap (BMP)");
			put(SWT.IMAGE_GIF, "Graphics Interchange Format (GIF)");
			put(SWT.IMAGE_JPEG, "Joint Photographic Experts Group (JPEG)");
			put(SWT.IMAGE_PNG, "Portable Network Graphics (PNG)");
			put(SWT.IMAGE_ICO, "Icon File (ICO)");
			put(SWT.IMAGE_TIFF, "Tagged Image File Format (TIFF)");
			put(SWT.IMAGE_UNDEFINED, "Unknown Format");
			put(ImagePropertyPage.IMAGE_ASC, "ASCII Format (ASC)");
			put(ImagePropertyPage.IMAGE_PGM, "Portable Graymap (PGM)");
		}
	};

	/**
	 * The namespace for the the content description properties this will set.
	 */
	public static final String IMAGES_NS = "msi.gama.image";

	/**
	 * Name of the image height property this sets.
	 */
	public static final QualifiedName HEIGHT = new QualifiedName(IMAGES_NS, "height"); //$NON-NLS-1$

	/**
	 * Name of the image width property this sets.
	 */
	public static final QualifiedName WIDTH = new QualifiedName(IMAGES_NS, "width"); //$NON-NLS-1$

	/**
	 * Name of the image depth property this sets.
	 */
	public static final QualifiedName DEPTH = new QualifiedName(IMAGES_NS, "depth"); //$NON-NLS-1$

	/**
	 * Name fo the image type (from SWT.IMAGE_*) property this sets.
	 */
	public static final QualifiedName TYPE = new QualifiedName(IMAGES_NS, "type"); //$NON-NLS-1$

	private static final QualifiedName[] SUPPORTED_OPTIONS = { WIDTH, HEIGHT, DEPTH, TYPE };

	@Override
	public int describe(final InputStream contents, final IContentDescription description) throws IOException {

		try {
			ImageData im = ImageDataLoader.getImageData(contents);
			if ( im != null ) {
				if ( description.isRequested(HEIGHT) ) {
					description.setProperty(HEIGHT, im.height);
				}
				if ( description.isRequested(WIDTH) ) {
					description.setProperty(WIDTH, im.width);
				}
				if ( description.isRequested(DEPTH) ) {
					description.setProperty(DEPTH, im.depth);
				}
				if ( description.isRequested(TYPE) ) {
					description.setProperty(TYPE, im.type);
				}
			} else {
				return INVALID;
			}

			return VALID;
		} catch (SWTException ex) {
			return INVALID;
		}
	}

	@Override
	public QualifiedName[] getSupportedOptions() {
		return SUPPORTED_OPTIONS;
	}

	public static String getLongLabel(final int type) {
		return formatsLongNames.contains(type) ? formatsLongNames.get(type) : formatsLongNames.get(SWT.IMAGE_UNDEFINED);
	}
}